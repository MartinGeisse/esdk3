/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.connector;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.tools.validation.ValidationContext;

/**
 * This signal simply produces the same values as another signal which is settable after construction. This helps
 * constructing signal networks since most other signal classes are immutable after construction.
 * Note that the connected signal must not be changed once simulation or synthesis has started.
 */
public abstract class SignalConnector extends DesignItem implements Signal, DesignItemOwned {

	public abstract Signal getConnected();

	@Override
	public void validate(ValidationContext context) {
		if (getConnected() == null) {
			context.reportError(getName() + ": no signal connected");
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
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return getConnected().compliesWith(nesting);
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		// we allow "all" here because we detect invalid nesting in compliesWith()
		out.printSignal(getConnected(), VerilogExpressionNesting.ALL);
	}

}
