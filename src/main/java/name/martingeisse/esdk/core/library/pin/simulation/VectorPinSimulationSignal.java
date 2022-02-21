/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.pin.simulation;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.simulation.SimulatedSettableVectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * This similar to an {@link SimulatedSettableVectorSignal} except that won't complain about synthesis as long as it is not
 * asked for an implementation expression.
 */
public class VectorPinSimulationSignal extends DesignItem implements VectorSignal, DesignItemOwned {

	private final int width;
	private Vector value;

	public VectorPinSimulationSignal(int width) {
		this.width = width;
		this.value = Vector.of(width, 0);
	}

	@Override
	public int getWidth() {
		return width;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		return value;
	}

	public void setValue(Vector value) {
		if (value.getWidth() != width) {
			throw new IllegalArgumentException("get vector value of wrong width " + value.getWidth() + ", expected " + width);
		}
		this.value = value;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot print an implementation expression for " + this);
	}

}
