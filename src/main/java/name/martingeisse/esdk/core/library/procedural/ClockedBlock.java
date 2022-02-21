/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural;

import name.martingeisse.esdk.core.library.clocked.ClockedItem;
import name.martingeisse.esdk.core.library.procedural.statement.StatementSequence;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a clocked block of statements, equivalent to a pair of "initial" and "always"
 * blocks in Verilog. The "always" part is triggered by a clock network.
 * <p>
 * For a sequence of statements grouped as a single statement, commonly referred to as a "block"
 * inside compilers, see {@link StatementSequence}.
 */
public final class ClockedBlock extends ClockedItem {

	private final StatementSequence statements;
	private List<ProceduralRegister> assignedProceduralRegisters;
	private List<ProceduralMemory> assignedProceduralMemories;

	public ClockedBlock(ClockSignal clockSignal) {
		super(clockSignal);
		this.statements = new StatementSequence();
	}

	public StatementSequence getStatements() {
		return statements;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// auto-bound registers and memories
	// ----------------------------------------------------------------------------------------------------------------

	private void initializeAssignedRegistersAndMemories() {
		if (assignedProceduralRegisters != null) {
			return;
		}
		assignedProceduralRegisters = new ArrayList<>();
		assignedProceduralMemories = new ArrayList<>();
		statements.collectAssignedRegistersAndMemories(assignedProceduralRegisters::add, assignedProceduralMemories::add);
	}

	@Override
	protected void finalizeConstructionAfterValidation() {
		// make sure the lists are non-null after finalizing the construction, even without registers / memories
		initializeAssignedRegistersAndMemories();
	}

	List<ProceduralRegister> getOrDetermineAssignedProceduralRegisters() {
		// this method gets called in undefined order WRT finishConstructionAfterValidation
		initializeAssignedRegistersAndMemories();
		return assignedProceduralRegisters;
	}

	List<ProceduralMemory> getOrDetermineAssignedProceduralMemories() {
		// this method gets called in undefined order WRT finishConstructionAfterValidation
		initializeAssignedRegistersAndMemories();
		return assignedProceduralMemories;
	}

	/**
	 * Returns null before finishing construction.
	 */
	public List<ProceduralRegister> getAssignedProceduralRegisters() {
		return assignedProceduralRegisters;
	}

	/**
	 * Returns null before finishing construction.
	 */
	public List<ProceduralMemory> getAssignedProceduralMemories() {
		return assignedProceduralMemories;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void computeNextState() {
		statements.execute();
	}

	@Override
	public void updateState() {
		for (ProceduralRegister signal : assignedProceduralRegisters) {
			signal.updateValue();
		}
		for (ProceduralMemory memory : assignedProceduralMemories) {
			memory.updateMatrix();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				statements.analyzeSignalUsage(consumer);
			}

			@Override
			public void printDeclarations(VerilogWriter out) {
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				out.indent();
				out.print("always @(posedge ");
				out.printSignal(getClock().getInputSignal());
				out.println(") begin");
				out.startIndentation();
				statements.printVerilogStatements(out);
				out.endIndentation();
				out.indent();
				out.println("end");
			}

		};
	}

}
