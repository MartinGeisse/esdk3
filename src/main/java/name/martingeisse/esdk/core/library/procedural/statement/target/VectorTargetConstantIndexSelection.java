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
public final class VectorTargetConstantIndexSelection implements BitAssignmentTarget {

	private final ProceduralVectorRegister containerTarget;
	private final int index;

	public VectorTargetConstantIndexSelection(ProceduralVectorRegister containerTarget, int index) {
		if (index < 0 || index >= containerTarget.getWidth()) {
			throw new IllegalArgumentException("index " + index + " out of bounds for width " + containerTarget.getWidth());
		}
		this.containerTarget = containerTarget;
		this.index = index;
	}

	public ProceduralVectorRegister getContainerTarget() {
		return containerTarget;
	}

	public int getIndex() {
		return index;
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
	public void setNextValue(boolean nextValue) {
		Vector nextContainerValue = containerTarget.getNextValue();
		Vector updatedValue;
		if (index == 0) {
			Vector upper = nextContainerValue.select(nextContainerValue.getWidth() - 1, 1);
			updatedValue = upper.concat(nextValue);
		} else if (index == nextContainerValue.getWidth() - 1) {
			Vector lower = nextContainerValue.select(index - 1, 0);
			updatedValue = lower.prepend(nextValue);
		} else {
			Vector upper = nextContainerValue.select(nextContainerValue.getWidth() - 1, index + 1);
			Vector lower = nextContainerValue.select(index - 1, 0);
			updatedValue = upper.concat(nextValue).concat(lower);
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
		out.print(index);
		out.print(']');
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		containerTarget.analyzeSignalUsage(consumer);
	}

}
