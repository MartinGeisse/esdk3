/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.getter.BitSignalGetter;
import name.martingeisse.esdk.core.library.signal.getter.DefaultSignalGetterFactory;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;

import java.util.function.Consumer;

/**
 *
 */
public final class WhenStatement extends Statement {

	private final BitSignal condition;
	private final StatementSequence thenBranch;
	private final StatementSequence otherwiseBranch;
	private BitSignalGetter conditionGetter;

	public WhenStatement(BitSignal condition) {
		this.condition = condition;
		this.thenBranch = new StatementSequence();
		this.otherwiseBranch = new StatementSequence();
	}

	public BitSignal getCondition() {
		return condition;
	}

	public StatementSequence getThenBranch() {
		return thenBranch;
	}

	public StatementSequence getOtherwiseBranch() {
		return otherwiseBranch;
	}

	@Override
	public boolean isEffectivelyNop() {
		return thenBranch.isEffectivelyNop() && otherwiseBranch.isEffectivelyNop();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// construction
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void collectAssignedRegistersAndMemories(Consumer<ProceduralRegister> registerConsumer, Consumer<ProceduralMemory> memoryConsumer) {
		thenBranch.collectAssignedRegistersAndMemories(registerConsumer, memoryConsumer);
		otherwiseBranch.collectAssignedRegistersAndMemories(registerConsumer, memoryConsumer);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void initializeSimulation() {
		super.initializeSimulation();
		conditionGetter = DefaultSignalGetterFactory.getGetter(condition);
	}

	@Override
	public void execute() {
		if (conditionGetter.getValue()) {
			thenBranch.execute();
		} else {
			otherwiseBranch.execute();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		consumer.getFakeExpressionWriter().printSignal(condition, VerilogExpressionNesting.ALL);
		thenBranch.analyzeSignalUsage(consumer);
		otherwiseBranch.analyzeSignalUsage(consumer);
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		out.indent();
		out.print("if (");
		out.printSignal(condition);
		out.println(") begin");
		out.startIndentation();
		thenBranch.printVerilogStatements(out);
		if (!otherwiseBranch.isEffectivelyNop()) {
			out.endIndentation();
			out.indent();
			out.println("end else begin");
			out.startIndentation();
			otherwiseBranch.printVerilogStatements(out);
		}
		out.endIndentation();
		out.indent();
		out.println("end");
	}

}
