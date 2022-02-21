/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.mux;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 *
 */
public abstract class ConditionalOperation extends DesignItem implements Signal, DesignItemOwned {

	private final BitSignal condition;

	public ConditionalOperation(BitSignal condition) {
		this.condition = checkSameDesign(condition);
	}

	public final BitSignal getCondition() {
		return condition;
	}

	public abstract Signal getOnTrue();

	public abstract Signal getOnFalse();

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.printSignal(condition, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(" ? ");
		out.printSignal(getOnTrue(), VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		out.print(" : ");
		out.printSignal(getOnFalse(), VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
	}

}
