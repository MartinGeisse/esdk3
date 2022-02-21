/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.verilog.expression;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.signal.Signal;

/**
 * Helper class to turn signals into expressions. This class is passed through the signals that make up an expression
 * recursively. In contrast to VerilogWriter, it is not concerned with writing the line-based contents of the file,
 * but the in-line parts of a single expression.
 *
 * Signal classes should expect to be called twice:
 * - the first call is a "dry-run" to collect all relevant signals, detect duplicates and determine allowed expression
 *   nesting (generating helper wires to resolve forbidden nesting, [1])
 * - the second call actually writes the expression.
 *
 * Signal classes should use {@link #printSignal(Signal, VerilogExpressionNesting)} to print
 * sub-expressions, or equivalently,
 * {@link Signal#printVerilogExpression(VerilogExpressionWriter, VerilogExpressionNesting)}.
 * Directly calling {@link Signal#printVerilogImplementationExpression(VerilogExpressionWriter)} may produce wrong
 * results and is not allowed.
 */
public interface VerilogExpressionWriter {
	VerilogExpressionWriter print(String s);
	VerilogExpressionWriter print(int i);
	VerilogExpressionWriter print(char c);
	VerilogExpressionWriter printSignal(Signal signal, VerilogExpressionNesting nesting);
	VerilogExpressionWriter printMemory(ProceduralMemory memory);
}
