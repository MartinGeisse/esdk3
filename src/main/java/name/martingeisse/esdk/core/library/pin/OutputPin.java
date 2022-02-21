/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.pin;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.ToplevelPortConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;

/**
 * A single output pin, bit-typed, that takes a bit signal to send off-chip.
 */
public final class OutputPin extends Pin {

	private BitSignal outputSignal;

	public BitSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(BitSignal outputSignal) {
		this.outputSignal = checkSameDesign(outputSignal);
	}

	public static OutputPin getByNetName(String netName) {
		return getByNetName(OutputPin.class, netName);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				consumer.consumeSignalUsage(outputSignal, VerilogExpressionNesting.ALL);
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("output", getNetName(), null);
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				out.print("assign " + getNetName() + " = ");
				out.printSignal(outputSignal);
				out.println(";");
			}

		};
	}

}
