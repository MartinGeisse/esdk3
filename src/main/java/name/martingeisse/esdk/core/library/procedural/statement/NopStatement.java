/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;

import java.util.function.Consumer;

/**
 *
 */
public final class NopStatement extends Statement {

	@Override
	public boolean isEffectivelyNop() {
		return true;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// construction
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void collectAssignedRegistersAndMemories(Consumer<ProceduralRegister> registerConsumer, Consumer<ProceduralMemory> memoryConsumer) {
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void execute() {
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
	}

}
