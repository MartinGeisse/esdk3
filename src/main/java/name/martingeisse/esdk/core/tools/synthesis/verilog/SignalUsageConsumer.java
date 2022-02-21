/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.verilog;

import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 *
 */
public interface SignalUsageConsumer {

	/**
	 * Reports the usage of a signal in an item.
	 *
	 * The signal is used in the specified kind of nesting. This determines, based on the nature of the
	 * signal, whether it must be extracted into a declaration to avoid nesting issues in Verilog, such
	 * as unintended carry-capturing. If in doubt, pass SIGNALS_AND_CONSTANTS nesting to this method --
	 * this will use literal nesting only for signal names and constants, and extract a declaration for
	 * anything else.
	 *
	 * The signal may be null for convenience -- in that case, this method has no effect.
	 */
	void consumeSignalUsage(Signal signal, VerilogExpressionNesting nesting);

	/**
	 * Returns a class that implements VerilogExpressionWriter but does not actually write anything,
	 * but just calls consumeSignalUsage() for other signals. This allows to re-use the Verilog
	 * generation methods for signal usage analysis, so they don't accidentally differ.
	 */
	VerilogExpressionWriter getFakeExpressionWriter();

}
