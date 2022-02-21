/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public final class ProceduralMemoryConstantIndexSelection extends DesignItem implements VectorSignal, DesignItemOwned {

	private final ProceduralMemory memory;
	private final int index;

	public ProceduralMemoryConstantIndexSelection(ProceduralMemory memory, int index) {
		if (index < 0 || index >= memory.getMatrix().getRowCount()) {
			throw new IllegalArgumentException("index " + index + " is out of range for matrix row count " + memory.getMatrix().getRowCount());
		}
		this.memory = checkSameDesign(memory);
		this.index = index;
	}

	public ProceduralMemory getMemory() {
		return memory;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getWidth() {
		return memory.getMatrix().getColumnCount();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		return memory.getMatrix().getRow(index);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return nesting != VerilogExpressionNesting.SIGNALS_AND_CONSTANTS;
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.printMemory(memory);
		out.print('[');
		out.print(index);
		out.print(']');
	}

}
