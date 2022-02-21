/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement.target;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.library.procedural.ProceduralVectorRegister;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.function.Consumer;

/**
 *
 */
public final class VectorTargetIndexSelection implements BitAssignmentTarget {

	private final ProceduralVectorRegister containerTarget;
	private final VectorSignal indexSignal;

	public VectorTargetIndexSelection(ProceduralVectorRegister containerTarget, VectorSignal indexSignal) {
		if (containerTarget.getWidth() < (1 << indexSignal.getWidth())) {
			throw new IllegalArgumentException("container of width " + containerTarget.getWidth() + " is too small for index of width " + indexSignal.getWidth());
		}
		this.containerTarget = containerTarget;
		this.indexSignal = indexSignal;
	}

	public ProceduralVectorRegister getContainerTarget() {
		return containerTarget;
	}

	public VectorSignal getIndexSignal() {
		return indexSignal;
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
		int index = indexSignal.getValue().getAsUnsignedInt();
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
		out.printSignal(indexSignal);
		out.print(']');
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		containerTarget.analyzeSignalUsage(consumer);
		consumer.consumeSignalUsage(indexSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

}
