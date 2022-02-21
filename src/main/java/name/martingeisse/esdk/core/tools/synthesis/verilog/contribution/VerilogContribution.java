/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.verilog.contribution;

import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.ToplevelPortConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;

/**
 *
 */
public interface VerilogContribution {

	/**
	 * This method is called first and allows items to assign names, mark signals as requiring a declaration, and
	 * generate auxiliary files.
	 */
	void prepareSynthesis(SynthesisPreparationContext context);

	/**
	 * This method is second and is used to build information on how signals are used. This serves two purposes:
	 * First, signals that are used in multiple places require a signal definition. Second, even a signal that is used
	 * in a single place may require a signal definition if it is too complex for the place it is used. The latter
	 * is because nesting signal expressions may cause weird effects in Verilog, and a signal definition gets rid
	 * of them.
	 *
	 * The general way this method works is: Any item that uses signals must report so to the usage consumer. This
	 * allows to recursively collect all signals required for synthesis.
	 *
	 * Signals themselves inherit a default implementation of this method from the Signal interface. That implementation
	 * re-uses the code for Verilog generation to find all used signals. This way, the two mechanisms cannot
	 * accidentally differ.
	 */
	void analyzeSignalUsage(SignalUsageConsumer consumer);

	/**
	 * The third method to be called, this allows items to contribute top-level ports, that is, ports of the enclosing
	 * Verilog module.
	 */
	default void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
	}

	/**
	 * This method allows an item to print the declarations needed for itself. Declarations go on top of the module
	 * contents.
	 *
	 * Note that the most common kind of declaration, that is signal declarations, are NOT written in this method but
	 * are generated implicitly for signals that have been found to require a declaration in analyzeSignalUsage().
	 */
	default void printDeclarations(VerilogWriter out) {
	}

	/**
	 * This method allows an item to print implementation code for itself. All implementation code is printed after
	 * the declarations.
	 */
	void printImplementation(VerilogWriter out);

}
