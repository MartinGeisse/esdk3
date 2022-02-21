/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.library.procedural.statement.target.AssignmentTarget;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;

import java.util.function.Consumer;

/**
 *
 */
public abstract class Assignment extends Statement {

	public abstract Signal getDestination();
	public abstract Signal getSource();
	protected abstract AssignmentTarget getAssignmentTarget();

	// ----------------------------------------------------------------------------------------------------------------
	// construction
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void collectAssignedRegistersAndMemories(Consumer<ProceduralRegister> registerConsumer, Consumer<ProceduralMemory> memoryConsumer) {
		getAssignmentTarget().collectAssignedRegistersAndMemories(registerConsumer, memoryConsumer);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// Note: The "destination" is explicitly NOT analyzed because it is only used as a description to build
		// the assignmentTarget. For example, in the assignment "foo[bar] <= baz;", baz is the source and therefore
		// analyzed. foo[bar] is the "destination", but not analyzed as such: bar is analyzed as part of the assignment
		// target that is generated from the destination, but foo is not analyzed because the assignment target
		// understands that its value is not used here. In fact, if the value of foo is not used elsewhere, it can
		// actually be considered unused.
		getAssignmentTarget().analyzeSignalUsage(consumer);
		consumer.consumeSignalUsage(getSource(), VerilogExpressionNesting.ALL);
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		out.indent();
		getAssignmentTarget().printVerilogAssignmentTarget(out);
		out.print(" <= ");
		out.printSignal(getSource());
		out.println(";");
	}

}
