/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.procedural.statement.target;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.function.Consumer;

/**
 *
 */
public final class MemoryTargetIndexSelection implements VectorAssignmentTarget {

	private final ProceduralMemory memory;
	private final VectorSignal indexSignal;

	public MemoryTargetIndexSelection(ProceduralMemory memory, VectorSignal indexSignal) {
		int rowCount = memory.getMatrix().getRowCount();
		if (rowCount < (1 << indexSignal.getWidth())) {
			throw new IllegalArgumentException("memory with " + rowCount + " rows is too small for index of width " + indexSignal.getWidth());
		}
		this.memory = memory;
		this.indexSignal = indexSignal;
	}

	public ProceduralMemory getMemory() {
		return memory;
	}

	public VectorSignal getIndexSignal() {
		return indexSignal;
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
		int index = indexSignal.getValue().getAsUnsignedInt();
		memory.requestUpdate(index, nextValue);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void printVerilogAssignmentTarget(VerilogWriter out) {
		out.printMemory(memory);
		out.print('[');
		out.printSignal(indexSignal);
		out.print(']');
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		consumer.consumeSignalUsage(indexSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

}
