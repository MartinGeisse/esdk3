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
 * TODO implement fractional-size memory. If memory size is not exactly 2^N rows, then the writing the missing
 * rows should be a no-OP and reading them should be a don't care. Only implement the case of missing rows at the
 * end. Ingore how this gets mapped to FPGA primitives because this way it's much easier to express -- knowing
 * that doing it wrong will still allocate 2^N rows. Make all this with an enable switch so it doesn't happen by
 * accident.
 */
public final class ProceduralMemoryIndexSelection extends DesignItem implements VectorSignal, DesignItemOwned {

	private final ProceduralMemory memory;
	private final VectorSignal indexSignal;

	public ProceduralMemoryIndexSelection(ProceduralMemory memory, VectorSignal indexSignal) {
		if (memory.getMatrix().getRowCount() < (1 << indexSignal.getWidth())) {
			throw new IllegalArgumentException("memory with " + memory.getMatrix().getRowCount() +
				" rows is too small for index of width " + indexSignal.getWidth());
		}
		this.memory = checkSameDesign(memory);
		this.indexSignal = checkSameDesign(indexSignal);
	}

	public ProceduralMemory getMemory() {
		return memory;
	}

	public VectorSignal getIndexSignal() {
		return indexSignal;
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
		return memory.getMatrix().getRow(indexSignal.getValue().getAsUnsignedInt());
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
		out.printSignal(indexSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print(']');
	}

}
