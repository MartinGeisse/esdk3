/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal;

import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.vector.Concatenation;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 * Note: If an implementation redefines equals() / hashCode(), then "equal" signals must be exchangeable without
 * changes to the RTL semantics. The Verilog code generator will use these methods, not object identity, to detect
 * re-use of signal objects.
 */
public interface Signal extends DesignItemOwned {

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	default Concatenation concat(Signal other) {
		return new Concatenation(this, other);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Returns true iff the expression for this signal can be generated with the specified nesting.
	 * <p>
	 * The default implementation only complies with ALL nesting. This is correct in all cases but may extract an
	 * expression to a helper signal unnecessarily.
	 */
	default boolean compliesWith(VerilogExpressionNesting nesting) {
		return nesting == VerilogExpressionNesting.ALL;
	}

	/**
	 * See {@link VerilogContribution#analyzeSignalUsage(SignalUsageConsumer)}. The default implementation will use
	 * the consumer's fake {@link VerilogExpressionWriter} to analyze signal usage, re-using the code for actual
	 * verilog generation. Signals that are synthesizable through other means than an implementation expression should
	 * override this method to analyze signal usage by other means.
	 */
	default void analyzeSignalUsage(SignalUsageConsumer consumer) {
		printVerilogImplementationExpression(consumer.getFakeExpressionWriter());
	}

	/**
	 * Writes a Verilog expression for this signal. This method only exists so nobody calls
	 * {@link #printVerilogImplementationExpression(VerilogExpressionWriter)} accidentally, being named similarly
	 * to intercept IDE autocompletion. It is equivalent to
	 * {@link VerilogExpressionWriter#printSignal(Signal, VerilogExpressionNesting)}.
	 */
	default void printVerilogExpression(VerilogExpressionWriter out, VerilogExpressionNesting nesting) {
		out.printSignal(this, nesting);
	}

	/**
	 * Writes a Verilog implementation expression for this signal. This method should only be called by the verilog
	 * generation core since it repeats the implementation expression for each usage of a shared signal. Also, not all
	 * signals support this method -- some need an extracted signal definition which is given a meaning by different
	 * means than an implementation expression (for example, switch-expressions and instance output ports).
	 */
	void printVerilogImplementationExpression(VerilogExpressionWriter out);

}
