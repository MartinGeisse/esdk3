/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.pin.simulation;

import name.martingeisse.esdk.core.library.pin.Pin;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.ToplevelPortConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;

/**
 * This "pin" is actually an array of pins. It outputs a vector signal. It cannot be used
 * for synthesis in the usual way since no assignment of real pins is done, but is
 * used for generating a Verilog module with a vector output port that is not meant for
 * immediate synthesis. For example, this is useful when generating a Verilog module that
 * is to be used in a larger, Verilog-based design.
 */
public final class VectorOutputPin extends Pin {

	private final int width;
	private VectorSignal outputSignal;

	public VectorOutputPin(int width) {
		this.width = width;
	}

	public VectorSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(VectorSignal outputSignal) {
		if (outputSignal.getWidth() != width) {
			throw new IllegalArgumentException("trying to set signal with wrong width " + outputSignal.getWidth() + ", expected " + width);
		}
		this.outputSignal = checkSameDesign(outputSignal);
	}

	public static VectorOutputPin getByNetName(String netName) {
		return getByNetName(VectorOutputPin.class, netName);
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
				consumer.consumePort("output", getNetName(), width);
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
