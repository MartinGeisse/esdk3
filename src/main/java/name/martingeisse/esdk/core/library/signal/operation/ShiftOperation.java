/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.operation;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * This class implements shift operators. It is separate from {@link VectorOperation} because it has different
 * width constraints on the right operand.
 * <p>
 * Shifts the left operand right or left by the number of bits indicated by the right operand. The left operand is
 * interpreted as a bit pattern and therefore has no notion of signedness. The right operand is always unsigned,
 * so a "right shift" never shifts left due to a negative shift amount.
 * <p>
 * Shifted-in bits are 0 in all cases. To shift in ones, double the width of the left operand and fill the new bits with
 * 1. To implement the typical "signed right shift" case, double the width of the left operand and fill the new bits
 * with the previous most significant bit (a.k.a. sign extension).
 * <p>
 * The result width is equal to the width of the left operand. The width of the right operand must ensure that the
 * left operand cannot be fully shifted-out. Formally, if the width of the right operand is R, then the width of the
 * left operand must be at least 2^R. This sidesteps the question of whether a large shift amount is truncated to a
 * small shift amount or if it causes the result to be zero (both cases are implemented in various systems, and
 * any confusion here can easily cause errors or inefficencies).
 */
public final class ShiftOperation extends DesignItem implements VectorSignal, DesignItemOwned {

	private final Direction direction;
	private final VectorSignal leftOperand;
	private final VectorSignal rightOperand;

	public ShiftOperation(Direction direction, VectorSignal leftOperand, VectorSignal rightOperand) {
		if (leftOperand.getWidth() < (1 << rightOperand.getWidth())) {
			throw new IllegalArgumentException("left shift operand too small: " + leftOperand.getWidth() + " for right operand width " + rightOperand.getWidth());
		}
		this.direction = direction;
		this.leftOperand = checkSameDesign(leftOperand);
		this.rightOperand = checkSameDesign(rightOperand);
	}

	public Direction getDirection() {
		return direction;
	}

	public VectorSignal getLeftOperand() {
		return leftOperand;
	}

	public VectorSignal getRightOperand() {
		return rightOperand;
	}

	@Override
	public int getWidth() {
		return leftOperand.getWidth();
	}

	public enum Direction {
		LEFT("<<"),
		RIGHT(">>");

		private final String symbol;

		Direction(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}

	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		switch (direction) {

			case LEFT:
				return leftOperand.getValue().shiftLeft(rightOperand.getValue().getAsUnsignedInt());

			case RIGHT:
				return leftOperand.getValue().shiftRight(rightOperand.getValue().getAsUnsignedInt());

			default:
				throw new UnsupportedOperationException();
		}
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
		out.printSignal(leftOperand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(' ');
		out.print(direction.getSymbol());
		out.print(' ');
		out.printSignal(rightOperand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
