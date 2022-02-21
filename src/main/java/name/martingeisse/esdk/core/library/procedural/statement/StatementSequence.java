/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorConstant;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public class StatementSequence extends Statement {

	private final List<Statement> statements = new ArrayList<>();

	public final void addStatement(Statement statement) {
		statements.add(checkSameDesign(statement));
	}

	public ImmutableList<Statement> getStatements() {
		return ImmutableList.copyOf(statements);
	}

	@Override
	public boolean isEffectivelyNop() {
		for (Statement statement : statements) {
			if (!statement.isEffectivelyNop()) {
				return false;
			}
		}
		return true;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	public final BitAssignment assign(BitSignal destination, BitSignal source) {
		BitAssignment assignment = new BitAssignment(destination, source);
		addStatement(assignment);
		return assignment;
	}

	public final BitAssignment assign(BitSignal destination, boolean value) {
		return assign(destination, new BitConstant(value));
	}

	public final VectorAssignment assign(VectorSignal destination, VectorSignal source) {
		VectorAssignment assignment = new VectorAssignment(destination, source);
		addStatement(assignment);
		return assignment;
	}

	public final VectorAssignment assign(VectorSignal destination, Vector value) {
		return assign(destination, new VectorConstant(value));
	}

	public final VectorAssignment assign(VectorSignal destination, int value) {
		if (value < 0) {
			throw new IllegalArgumentException("assign called with negative value: " + value);
		}
		VectorConstant constant = new VectorConstant(destination.getWidth(), value);
		VectorAssignment assignment = new VectorAssignment(destination, constant);
		addStatement(assignment);
		return assignment;
	}

	public final WhenStatement when(BitSignal condition) {
		WhenStatement whenStatement = new WhenStatement(condition);
		addStatement(whenStatement);
		return whenStatement;
	}

	public final SwitchStatement switchOn(VectorSignal selector) {
		SwitchStatement switchStatement = new SwitchStatement(selector);
		addStatement(switchStatement);
		return switchStatement;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// construction
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void collectAssignedRegistersAndMemories(Consumer<ProceduralRegister> registerConsumer, Consumer<ProceduralMemory> memoryConsumer) {
		for (Statement statement : statements) {
			statement.collectAssignedRegistersAndMemories(registerConsumer, memoryConsumer);
		}
	}


	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void execute() {
		for (Statement statement : statements) {
			statement.execute();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		for (Statement statement : statements) {
			statement.analyzeSignalUsage(consumer);
		}
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		for (Statement statement : statements) {
			statement.printVerilogStatements(out);
		}
	}

}
