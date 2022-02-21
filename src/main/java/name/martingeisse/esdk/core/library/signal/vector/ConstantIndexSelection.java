/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.vector;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorConstant;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 * Unlike {@link IndexSelection} with an {@link VectorConstant} as the index, this class can select the upper
 * bits of a vector whose width is not a power-of-two. A non-constant selection cannot do that because it cannot
 * statically prove that the index is always within range.
 */
public final class ConstantIndexSelection extends DesignItem implements BitSignal, DesignItemOwned {

	private final VectorSignal containerSignal;
	private final int index;

	public ConstantIndexSelection(VectorSignal containerSignal, int index) {
		if (index < 0 || index >= containerSignal.getWidth()) {
			throw new IllegalArgumentException("index " + index + " out of bounds for width " + containerSignal.getWidth());
		}
		this.containerSignal = checkSameDesign(containerSignal);
		this.index = index;
	}

	public VectorSignal getContainerSignal() {
		return containerSignal;
	}

	public int getIndex() {
		return index;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return containerSignal.getValue().select(index);
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
		out.printSignal(containerSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print('[');
		out.print(index);
		out.print(']');
	}

}
