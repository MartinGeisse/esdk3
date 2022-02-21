/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

import java.util.function.Consumer;

/**
 *
 */
public abstract class Statement extends DesignItem implements DesignItemOwned {

	public abstract boolean isEffectivelyNop();

	// ----------------------------------------------------------------------------------------------------------------
	// construction
	// ----------------------------------------------------------------------------------------------------------------

	public abstract void collectAssignedRegistersAndMemories(
			Consumer<ProceduralRegister> registerConsumer,
			Consumer<ProceduralMemory> memoryConsumer
	);

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	public abstract void execute();

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	// statements are synthesized as part of the block they appear in -- there are currently no statements that need
	// any extra contribution
	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}


	public abstract void analyzeSignalUsage(SignalUsageConsumer consumer);

	public abstract void printVerilogStatements(VerilogWriter out);


}
