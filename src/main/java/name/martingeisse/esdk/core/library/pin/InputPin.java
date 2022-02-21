/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.pin;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.*;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 * A single input pin, bit-typed, that is available as a bit signal to other items.
 */
public final class InputPin extends Pin implements BitSignal {

	private final PinSimulationSignal settableBitSignal;

	public InputPin() {
		this.settableBitSignal = new PinSimulationSignal();
	}

	public PinSimulationSignal getSettableBitSignal() {
		return settableBitSignal;
	}

	public static InputPin getByNetName(String netName) {
		return getByNetName(InputPin.class, netName);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return settableBitSignal.getValue();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// input pins don't use any other signals
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
				context.declareFixedNameSignal(InputPin.this, getNetName(), VerilogSignalDeclarationKeyword.NONE, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("input", getNetName(), null);
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

}
