/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural;

import name.martingeisse.esdk.core.library.procedural.statement.target.VectorAssignmentTarget;
import name.martingeisse.esdk.core.library.procedural.statement.target.VectorTargetConstantIndexSelection;
import name.martingeisse.esdk.core.library.procedural.statement.target.VectorTargetIndexSelection;
import name.martingeisse.esdk.core.library.procedural.statement.target.VectorTargetRangeSelection;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public final class ProceduralVectorRegister extends ProceduralRegister implements VectorSignal, VectorAssignmentTarget {

	private final int width;
	private Vector value;
	private Vector nextValue;

	public ProceduralVectorRegister(int width) {
		this.width = width;
		this.value = Vector.of(width, 0);
		this.nextValue = value;
	}

	public ProceduralVectorRegister(int width, Vector initialValue) {
		if (initialValue.getWidth() != width) {
			throw new IllegalArgumentException("initial value must have width of register (" + width + "): " + initialValue);
		}
		this.width = width;
		this.value = initialValue;
		this.nextValue = value;
		setInitialized(true);
	}

	@Override
	public int getWidth() {
		return width;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	public VectorTargetIndexSelection selectTarget(VectorSignal index) {
		return new VectorTargetIndexSelection(this, index);
	}

	public VectorTargetConstantIndexSelection selectTarget(int index) {
		return new VectorTargetConstantIndexSelection(this, index);
	}

	public VectorTargetRangeSelection selectTarget(int from, int to) {
		return new VectorTargetRangeSelection(this, from, to);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		return value;
	}

	public Vector getNextValue() {
		return nextValue;
	}

	@Override
	public void setNextValue(Vector nextValue) {
		if (nextValue == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		if (nextValue.getWidth() != width) {
			throw new IllegalArgumentException("trying to set next value of wrong width " + nextValue.getWidth() + ", should be " + width);
		}
		this.nextValue = nextValue;
	}

	@Override
	void updateValue() {
		value = nextValue;
	}

	/**
	 * This method directly sets the current value. This is useful, for example, to override the initial value of
	 * a register for simulation.
	 *
	 * DO NOT CALL THIS from within any clock handler! Doing so makes the behavior dependent on the order in which
	 * clock handlers are executed, which is undefined by design.
	 */
	public void overrideCurrentValue(Vector value) {
		if (value == null) {
			throw new IllegalArgumentException("value cannot be null");
		}
		if (value.getWidth() != width) {
			throw new IllegalArgumentException("trying to set next value of wrong width " + value.getWidth() + ", should be " + width);
		}
		this.value = value;
		// We also have to override the next value because for a procedural register that gets assigned inside an
		// if-statement (i.e. has a clock enable), that statement conditionally sets the next value, but the next
		// value always gets written to the current value in updateValue(), restoring the old value from before this
		// method got called.
		this.nextValue = value;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// synthesis
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void printInitializerValue(VerilogWriter out) {
		value.printVerilogExpression(out);
	}

}
