/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.pin;

import name.martingeisse.esdk.core.library.blackbox.BlackboxInstance;
import name.martingeisse.esdk.core.library.blackbox.BlackboxInstancePort;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.ToplevelPortConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

/**
 * A single bidirectional pin, bit-typed, that is directly connected to a bidirectional port
 * of a blackbox module.
 *
 * This class is commonly used when a bidirectional port must be connected to a more complex FPGA
 * primitive than just a tristate buffer, for example, DDR I/Os.
 */
public final class BidirectionalModulePortPin extends Pin {

	private final BlackboxInstancePort port;

	public BidirectionalModulePortPin(BlackboxInstance moduleInstance, String portName) {
		port = new BlackboxInstancePort(moduleInstance, portName) {
			@Override
			protected void printPortAssignment(VerilogWriter out) {
				out.print("." + getPortName() + "(");
				out.print(BidirectionalModulePortPin.this.getNetName());
				out.print(')');
			}
		};
	}

	public BlackboxInstancePort getPort() {
		return port;
	}

	public static BidirectionalModulePortPin getByNetName(String netName) {
		return getByNetName(BidirectionalModulePortPin.class, netName);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.assignFixedName(getNetName(), BidirectionalModulePortPin.this);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("inout", getNetName(), null);
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

}
