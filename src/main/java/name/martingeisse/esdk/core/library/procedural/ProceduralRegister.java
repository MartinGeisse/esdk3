/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.procedural.statement.target.AssignmentTarget;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogSignalDeclarationKeyword;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.function.Consumer;

/**
 *
 */
public abstract class ProceduralRegister extends DesignItem implements Signal, AssignmentTarget, DesignItemOwned {

	private boolean initialized;
	private ClockedBlock clockedBlock;
	private boolean errorIfNotAssigned = true;

	/**
	 * This getter returns null before finishing construction.
	 */
	public final ClockedBlock getClockedBlock() {
		return clockedBlock;
	}

	public final void disableErrorIfNotAssigned() {
		errorIfNotAssigned = false;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// construction
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void finalizeConstructionAfterValidation() {
		for (ClockedBlock block: getDesign().getItems(ClockedBlock.class)) {
			if (block.getOrDetermineAssignedProceduralRegisters().contains(this)) {
				if (clockedBlock != null) {
					// TODO this indicates that finalizing and validation aren't as independent as I'd like
					throw new RuntimeException("multiple clocked blocks assign to register " + this);
				}
				clockedBlock = block;
			}
		}

		// If no clockedBlock has been found, then this is actually a constant signal since no assignment to it is
		// ever made. We cannot be sure if this is a mistake or if it is intentional. The latter happens, for example,
		// when a complex parameterized component happens to leave out all assignments based on a specific parameter
		// combination. We therefore treat this case as an error by default, but allow to turn it off per-register
		// if the no-assignment case can happen intentionally.
		if (clockedBlock == null && errorIfNotAssigned) {
			throw new RuntimeException("no assignment found for procedural register: " + this +
					"\nIf this is intentional, you can disable this error by calling register.disableErrorIfNotAssigned().");
		}

	}

	@Override
	public void collectAssignedRegistersAndMemories(Consumer<ProceduralRegister> registerConsumer, Consumer<ProceduralMemory> memoryConsumer) {
		registerConsumer.accept(this);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Updates the value from the stored next value.
	 */
	abstract void updateValue();

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------


	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	protected abstract void printInitializerValue(VerilogWriter out);

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.declareSignal(ProceduralRegister.this, VerilogSignalDeclarationKeyword.REG, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				if (!initialized) {
					return;
				}
				out.indent();
				out.println("initial begin");
				out.startIndentation();
				out.indent();
				printVerilogAssignmentTarget(out);
				out.print(" <= ");
				printInitializerValue(out);
				out.println(";");
				out.endIndentation();
				out.indent();
				out.println("end");
			}

		};
	}

	@Override
	public final void printVerilogAssignmentTarget(VerilogWriter out) {
		out.printSignal(this);
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// procedural signals themselves don't use other signals; the assignments that
		// assign values to them do.
	}

	@Override
	public final void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot write an implementation expression for procedural signals");
	}

}
