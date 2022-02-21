/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.operation;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public final class VectorComparison extends DesignItem implements BitSignal, DesignItemOwned {

	private final Operator operator;
	private final VectorSignal leftOperand;
	private final VectorSignal rightOperand;

	public VectorComparison(Operator operator, VectorSignal leftOperand, VectorSignal rightOperand) {
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

	public enum Operator {
		EQUAL("=="),
		NOT_EQUAL("!="),
		LESS_THAN("<"),
		LESS_THAN_OR_EQUAL("<="),
		GREATER_THAN(">"),
		GREATER_THAN_OR_EQUAL(">=");

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

		public boolean evaluate(Vector leftOperand, Vector rightOperand) {
			switch (this) {

				case EQUAL:
					return leftOperand.equals(rightOperand);

				case NOT_EQUAL:
					return !leftOperand.equals(rightOperand);

				case LESS_THAN:

					return compare(leftOperand, rightOperand, true, false);

				case LESS_THAN_OR_EQUAL:
					return compare(leftOperand, rightOperand, true, true);

				case GREATER_THAN:
					return compare(leftOperand, rightOperand, false, false);

				case GREATER_THAN_OR_EQUAL:
					return compare(leftOperand, rightOperand, false, true);

				default:
					throw new UnsupportedOperationException();

			}
		}

		private boolean compare(Vector leftOperand, Vector rightOperand, boolean less, boolean equal) {
			int raw = leftOperand.compareUnsigned(rightOperand);
			return raw == 0 ? equal : (raw < 0) == less;
		}

	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
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
