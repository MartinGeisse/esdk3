/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.pin;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.blackbox.BlackboxInstance;
import name.martingeisse.esdk.core.library.blackbox.BlackboxInstancePort;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.ToplevelPortConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

import java.util.ArrayList;
import java.util.List;

/**
 * An array of bidirectional pins, the array being vector-typed, that is directly connected to a
 * bidirectional vector-typed port of a blackbox module.
 *
 * This class is commonly used when an array of bidirectional ports must be connected to a more
 * complex FPGA primitive than just tristate buffers, for example, DDR I/Os.
 */
public final class BidirectionalModulePortPinArray extends DesignItem implements DesignItemOwned {

	private final BlackboxInstancePort port;
	private final String netName;
	private final ImmutableList<Pin> pins;

	public BidirectionalModulePortPinArray(BlackboxInstance moduleInstance, String portName, String netName, String... pinIds) {
		port = new BlackboxInstancePort(moduleInstance, portName) {
			@Override
			protected void printPortAssignment(VerilogWriter out) {
				out.print("." + getPortName() + "(" + netName + ")");
			}
		};
		this.netName = netName;
		List<Pin> pins = new ArrayList<>();
		for (int i = 0; i < pinIds.length; i++) {
			int index = i;
			Pin pin = new Pin() {

				@Override
				public VerilogContribution getVerilogContribution() {
					return new EmptyVerilogContribution();
				}

				@Override
				public String getNetName() {
					return netName + "<" + index + ">";
				}

			};
			pin.setId(pinIds[i]);
			pins.add(pin);
		}
		this.pins = ImmutableList.copyOf(pins);
	}

	public BlackboxInstancePort getPort() {
		return port;
	}

	public String getNetName() {
		return netName;
	}

	public ImmutableList<Pin> getPins() {
		return pins;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.assignFixedName(netName, BidirectionalModulePortPinArray.this);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("inout", netName, pins.size());
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

}
