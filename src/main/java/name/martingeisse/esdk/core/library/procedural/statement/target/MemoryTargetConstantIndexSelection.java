/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement.target;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.function.Consumer;

/**
 *
 */
public final class MemoryTargetConstantIndexSelection implements VectorAssignmentTarget {

	private final ProceduralMemory memory;
	private final int index;

	public MemoryTargetConstantIndexSelection(ProceduralMemory memory, int index) {
		if (index < 0 || index >= memory.getMatrix().getRowCount()) {
			throw new IllegalArgumentException("index " + index + " is out of range for matrix row count " + memory.getMatrix().getRowCount());
		}
		this.memory = memory;
		this.index = index;
	}

	public ProceduralMemory getMemory() {
		return memory;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public int getWidth() {
		return memory.getMatrix().getColumnCount();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// construction
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void collectAssignedRegistersAndMemories(Consumer<ProceduralRegister> registerConsumer, Consumer<ProceduralMemory> memoryConsumer) {
		memoryConsumer.accept(memory);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void setNextValue(Vector nextValue) {
		memory.requestUpdate(index, nextValue);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void printVerilogAssignmentTarget(VerilogWriter out) {
		out.printMemory(memory);
		out.print('[');
		out.print(index);
		out.print(']');
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
	}

}
