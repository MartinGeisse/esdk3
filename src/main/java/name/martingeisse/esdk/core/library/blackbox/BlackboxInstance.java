/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.blackbox;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogSignalDeclarationKeyword;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class BlackboxInstance extends DesignItem implements DesignItemOwned {

	private String blackboxTemplateName;
	private final Map<String, BlackboxInstancePort> ports = new HashMap<>();
	private final Map<String, Object> parameters = new HashMap<>();
	private final Map<String, Object> attributes = new HashMap<>();

	public BlackboxInstance(String blackboxTemplateName) {
		this.blackboxTemplateName = blackboxTemplateName;
	}

	public String getBlackboxTemplateName() {
		return blackboxTemplateName;
	}

	public void setBlackboxTemplateName(String blackboxTemplateName) {
		this.blackboxTemplateName = blackboxTemplateName;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public Iterable<BlackboxInstancePort> getPorts() {
		return ports.values();
	}

	public BlackboxInstanceBitInputPort createBitInputPort(String portName) {
		return createBitInputPort(portName, null);
	}

	public BlackboxInstanceBitInputPort createBitInputPort(String portName, BitSignal assignedSignal) {
		return new BlackboxInstanceBitInputPort(this, portName, assignedSignal);
	}

	public BlackboxInstanceBitInputPort createBitInputPort(String portName, boolean constantValue) {
		return createBitInputPort(portName, new BitConstant(constantValue));
	}

	public BlackboxInstanceVectorInputPort createVectorInputPort(String portName, int width) {
		return createVectorInputPort(portName, width, null);
	}

	public BlackboxInstanceVectorInputPort createVectorInputPort(String portName, int width, VectorSignal assignedSignal) {
		return new BlackboxInstanceVectorInputPort(this, portName, width, assignedSignal);
	}

	public BlackboxInstanceBitOutputPort createBitOutputPort(String portName) {
		return new BlackboxInstanceBitOutputPort(this, portName);
	}

	public BlackboxInstanceVectorOutputPort createVectorOutputPort(String portName, int width) {
		return new BlackboxInstanceVectorOutputPort(this, portName, width);
	}

	void addPort(String portName, BlackboxInstancePort port) {
		BlackboxInstancePort oldPort = ports.put(portName, port);
		if (oldPort != null) {
			ports.put(portName, oldPort);
			throw new IllegalStateException("a port with name " + portName + " was already added");
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.assignGeneratedName(BlackboxInstance.this);
				for (BlackboxInstancePort port : ports.values()) {
					if (port instanceof BlackboxInstanceOutputPort) {
						BlackboxInstanceOutputPort outputPort = (BlackboxInstanceOutputPort) port;
						context.declareSignal(outputPort, VerilogSignalDeclarationKeyword.WIRE, false);
					}
				}
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				for (BlackboxInstancePort port : ports.values()) {
					if (port instanceof BlackboxInstanceInputPort) {
						BlackboxInstanceInputPort inputPort = (BlackboxInstanceInputPort) port;
						consumer.consumeSignalUsage(inputPort.getAssignedSignal(), VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
					}
				}
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				if (!attributes.isEmpty()) {
					for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
						out.print("(* ");
						out.print(attribute.getKey());
						out.print('=');
						writeAttributeOrParameterValue(out, attribute.getValue());
						out.print(" *) ");
					}
					out.println();
				}
				if (parameters.isEmpty()) {
					out.print(blackboxTemplateName + ' ');
					out.printName(BlackboxInstance.this);
					out.print(" (");
				} else {
					out.print(blackboxTemplateName + " #(");
					boolean firstParameter = true;
					for (Map.Entry<String, Object> entry : parameters.entrySet()) {
						if (firstParameter) {
							firstParameter = false;
							out.println();
						} else {
							out.println(",");
						}
						out.print("\t." + entry.getKey() + '(');
						writeAttributeOrParameterValue(out, entry.getValue());
						out.print(')');
					}
					out.println();
					out.print(") ");
                    out.printName(BlackboxInstance.this);
                    out.print(" (");
				}
				boolean firstPort = true;
				for (BlackboxInstancePort port : ports.values()) {
					if (firstPort) {
						firstPort = false;
						out.println();
					} else {
						out.println(",");
					}
					out.print('\t');
					port.printPortAssignment(out);
				}
				out.println();
				out.println(");");
			}

			private void writeAttributeOrParameterValue(VerilogWriter out, Object value) {
				if (value instanceof String) {
					out.print("\"" + value + "\"");
				} else if (value instanceof Integer) {
					out.print(value);
				} else if (value instanceof Vector) {
					((Vector) value).printVerilogExpression(out);
				} else {
					throw new RuntimeException("invalid module attribute or parameter value: " + value);
				}
			}

		};
	}

}
