/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.pin;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.*;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 * A single bidirectional pin, bit typed, with a tristate buffer. A bit signal enables or
 * disables the buffer, another bit signal is used for the output value, and the input
 * value is available as a third bit signal.
 *
 * This class is commonly used for bidirectional pins that do not require any special
 * handling from FPGA primitives, but can be implemented with simple Verilog constructs.
 * An example would be low-speed bidirectional buses.
 */
public final class BidirectionalPin extends Pin implements BitSignal {

	private final PinSimulationSignal settableInputBitSignal;
	private BitSignal outputSignal;
	private BitSignal outputEnableSignal;

	public BidirectionalPin() {
		this.settableInputBitSignal = new PinSimulationSignal();
	}

	public PinSimulationSignal getSettableInputBitSignal() {
		return settableInputBitSignal;
	}

	public BitSignal getOutputSignal() {
		return outputSignal;
	}

	public void setOutputSignal(BitSignal outputSignal) {
		this.outputSignal = checkSameDesign(outputSignal);
	}

	public BitSignal getOutputEnableSignal() {
		return outputEnableSignal;
	}

	public void setOutputEnableSignal(BitSignal outputEnableSignal) {
		this.outputEnableSignal = checkSameDesign(outputEnableSignal);
	}

	public static BidirectionalPin getByNetName(String netName) {
		return getByNetName(BidirectionalPin.class, netName);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return outputEnableSignal.getValue() ? outputSignal.getValue() : settableInputBitSignal.getValue();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// We analyze the output and output-enable signals in a separate VerilogContribution.
		// This is correct; we cannot analyze them here because those usages should be analyzed
		// regardless of whether the pin's input signal is used anywhere.
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException();
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.declareFixedNameSignal(BidirectionalPin.this, getNetName(), VerilogSignalDeclarationKeyword.NONE, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				consumer.consumeSignalUsage(outputSignal, VerilogExpressionNesting.ALL);
				consumer.consumeSignalUsage(outputEnableSignal, VerilogExpressionNesting.ALL);
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("inout", getNetName(), null);
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				out.print("assign " + getNetName() + " = ");
				out.printSignal(outputEnableSignal);
				out.println(" ? ");
				out.printSignal(outputSignal);
				out.println(" : 1'bz;");
			}

		};
	}

}
