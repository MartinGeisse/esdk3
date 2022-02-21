/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.operation;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 *
 */
public final class BitOperation extends DesignItem implements BitSignal, DesignItemOwned {

	private final Operator operator;
	private final BitSignal leftOperand;
	private final BitSignal rightOperand;

	public BitOperation(Operator operator, BitSignal leftOperand, BitSignal rightOperand) {
		this.operator = operator;
		this.leftOperand = checkSameDesign(leftOperand);
		this.rightOperand = checkSameDesign(rightOperand);
	}

	public Operator getOperator() {
		return operator;
	}

	public BitSignal getLeftOperand() {
		return leftOperand;
	}

	public BitSignal getRightOperand() {
		return rightOperand;
	}

	public enum Operator {
		AND("&"),
		OR("|"),
		XOR("^"),
		XNOR("==");

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

		public boolean evaluate(boolean leftOperand, boolean rightOperand) {
			switch (this) {

				case AND:
					return leftOperand & rightOperand;

				case OR:
					return leftOperand | rightOperand;

				case XOR:
					return leftOperand ^ rightOperand;

				case XNOR:
					return leftOperand == rightOperand;

				default:
					throw new UnsupportedOperationException();

			}
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
