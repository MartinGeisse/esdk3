/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.pin.simulation;

import name.martingeisse.esdk.core.library.pin.Pin;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.*;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * This "pin" is actually an array of pins. It makes an input vector signal available to
 * other items. It cannot be used for synthesis in the usual way since no assignment of
 * real pins is done, but is used for generating a Verilog module with a vector input
 * port that is not meant for immediate synthesis. For example, this is useful when
 * generating a Verilog module that is to be used in a larger, Verilog-based design.
 */
public final class VectorInputPin extends Pin implements VectorSignal {

	private final int width;
	private final VectorPinSimulationSignal simulationSignal;

	public VectorInputPin(int width) {
		this.simulationSignal = new VectorPinSimulationSignal(width);
		this.width = width;
	}

	public VectorPinSimulationSignal getSimulationSignal() {
		return simulationSignal;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public static VectorInputPin getByNetName(String netName) {
		return getByNetName(VectorInputPin.class, netName);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		return simulationSignal.getValue();
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
				context.declareFixedNameSignal(VectorInputPin.this, getNetName(), VerilogSignalDeclarationKeyword.NONE, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
				consumer.consumePort("input", getNetName(), width);
			}

			@Override
			public void printImplementation(VerilogWriter out) {
			}

		};
	}

}
