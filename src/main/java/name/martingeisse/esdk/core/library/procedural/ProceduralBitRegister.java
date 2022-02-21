/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural;

import name.martingeisse.esdk.core.library.procedural.statement.target.BitAssignmentTarget;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;

/**
 *
 */
public final class ProceduralBitRegister extends ProceduralRegister implements BitSignal, BitAssignmentTarget {

	private boolean value;
	private boolean nextValue;

	public ProceduralBitRegister() {
	}

	public ProceduralBitRegister(boolean initialValue) {
		this.value = initialValue;
		this.nextValue = initialValue;
		setInitialized(true);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return value;
	}

	public boolean getNextValue() {
		return nextValue;
	}

	@Override
	public void setNextValue(boolean nextValue) {
		this.nextValue = nextValue;
	}

	@Override
	void updateValue() {
		value = nextValue;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// synthesis
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void printInitializerValue(VerilogWriter out) {
		out.print(BitConstant.getVerilogConstant(value));
	}

}
