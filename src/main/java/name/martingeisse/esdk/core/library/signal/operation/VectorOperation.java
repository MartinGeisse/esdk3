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
 * See {@link ShiftOperation} for shifting. That class is separate because it has different width constraints on
 * the right operand.
 */
public final class VectorOperation extends DesignItem implements VectorSignal, DesignItemOwned {

	private final Operator operator;
	private final VectorSignal leftOperand;
	private final VectorSignal rightOperand;

	public VectorOperation(Operator operator, VectorSignal leftOperand, VectorSignal rightOperand) {
		if (leftOperand.getWidth() != rightOperand.getWidth()) {
			throw new IllegalArgumentException("operand width mismatch: " + leftOperand.getWidth() + " vs. " + rightOperand.getWidth());
		}
		this.operator = operator;
		this.leftOperand = checkSameDesign(leftOperand);
		this.rightOperand = checkSameDesign(rightOperand);
	}

	public Operator getOperator() {
		return operator;
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

	public enum Operator {
		ADD("+"),
		SUBTRACT("-"),
		MULTIPLY("*"),
		AND("&"),
		OR("|"),
		XOR("^");

		private final String symbol;

		Operator(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}

		// ------------------------------------------------------------------------------------------------------------
		// simulation
		// ------------------------------------------------------------------------------------------------------------

		public Vector evaluate(Vector leftOperand, Vector rightOperand) {
			switch (this) {

				case ADD:
					return leftOperand.add(rightOperand);

				case SUBTRACT:
					return leftOperand.subtract(rightOperand);

				case MULTIPLY:
					return leftOperand.multiply(rightOperand);

				case AND:
					return leftOperand.and(rightOperand);

				case OR:
					return leftOperand.or(rightOperand);

				case XOR:
					return leftOperand.xor(rightOperand);

				default:
					throw new UnsupportedOperationException();

			}
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		return operator.evaluate(leftOperand.getValue(), rightOperand.getValue());
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
		out.print(operator.getSymbol());
		out.print(' ');
		out.printSignal(rightOperand, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
