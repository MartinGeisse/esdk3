/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement.target;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.library.procedural.ProceduralVectorRegister;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.function.Consumer;

/**
 *
 */
public final class VectorTargetRangeSelection implements VectorAssignmentTarget {

	private final ProceduralVectorRegister containerTarget;
	private final int from;
	private final int to;

	public VectorTargetRangeSelection(ProceduralVectorRegister containerTarget, int from, int to) {
		if (from < 0 || to < 0 || from >= containerTarget.getWidth() || to >= containerTarget.getWidth() || from < to) {
			throw new IllegalArgumentException("invalid from/to indices for container width " +
				containerTarget.getWidth() + ": from = " + from + ", to = " + to);
		}
		this.containerTarget = containerTarget;
		this.from = from;
		this.to = to;
	}

	public ProceduralVectorRegister getContainerTarget() {
		return containerTarget;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	@Override
	public int getWidth() {
		return from - to + 1;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// construction
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void collectAssignedRegistersAndMemories(Consumer<ProceduralRegister> registerConsumer, Consumer<ProceduralMemory> memoryConsumer) {
		registerConsumer.accept(containerTarget);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void setNextValue(Vector nextValue) {
		Vector nextContainerValue = containerTarget.getNextValue();
		Vector updatedValue;
		if (to == 0) {
			if (from == nextContainerValue.getWidth() - 1) {
				updatedValue = nextValue;
			} else {
				Vector upper = nextContainerValue.select(nextContainerValue.getWidth() - 1, from + 1);
				updatedValue = upper.concat(nextValue);
			}
		} else {
			if (from == nextContainerValue.getWidth() - 1) {
				Vector lower = nextContainerValue.select(to - 1, 0);
				updatedValue = nextValue.concat(lower);
			} else {
				Vector upper = nextContainerValue.select(nextContainerValue.getWidth() - 1, from + 1);
				Vector lower = nextContainerValue.select(to - 1, 0);
				updatedValue = upper.concat(nextValue).concat(lower);
			}
		}
		containerTarget.setNextValue(updatedValue);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void printVerilogAssignmentTarget(VerilogWriter out) {
		containerTarget.printVerilogAssignmentTarget(out);
		out.print('[');
		out.print(from);
		out.print(':');
		out.print(to);
		out.print(']');
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		containerTarget.analyzeSignalUsage(consumer);
	}

}
