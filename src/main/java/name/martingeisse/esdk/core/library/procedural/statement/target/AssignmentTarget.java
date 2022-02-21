/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement.target;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;

import java.util.function.Consumer;

/**
 * Represents the "signal" to which an assignment is made, but resolved as an assignment target instead
 * of a value source.
 */
public interface AssignmentTarget {

	// ----------------------------------------------------------------------------------------------------------------
	// construction
	// ----------------------------------------------------------------------------------------------------------------

	void collectAssignedRegistersAndMemories(Consumer<ProceduralRegister> registerConsumer, Consumer<ProceduralMemory> memoryConsumer);

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Passes signals used by this assignment target to the specified consumer.
	 */
	void analyzeSignalUsage(SignalUsageConsumer consumer);

	/**
	 * Writes a Verilog assignment target for this assignment target.
	 */
	void printVerilogAssignmentTarget(VerilogWriter out);

}
