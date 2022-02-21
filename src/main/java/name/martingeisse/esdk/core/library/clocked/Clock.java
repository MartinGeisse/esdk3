/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.clocked;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.procedural.ClockedBlock;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A real clock network (after synthesis) reacts to real clock edges, i.e. 0-to-1 transitions of the clock signal. In
 * simulation, on the other hand, clock edges must be simulated by calling {@link #simulateClockEdge()} since the
 * simulation core doesn't recognize or even simulate asynchronous signal edges. Therefore, simulation of a clock
 * network ignores the clock signal. This shouldn't be a problem in synchronous systems but means you can't use dirty
 * asynchronous tricks such as manually generated clock signals.
 *
 * TODO validate that calling code does not create two Clock instances for the same input signal. This will cause
 * simulation bugs. Maybe rename Clock to ClockDomain to emphasize that there shouldn't be two of them for the
 * same clock.
 */
public final class Clock extends DesignItem implements DesignItemOwned, ClockSignal {

	private final BitSignal inputSignal;
	private List<ClockedItem> targetItemsForSimulation;

	public Clock(BitSignal inputSignal) {
		this.inputSignal = checkSameDesign(inputSignal);
	}

	public BitSignal getInputSignal() {
		return inputSignal;
	}

	@Override
	public Clock getClock() {
		return this;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	public ClockedBlock createBlock() {
		return new ClockedBlock(this);
	}

	public ClockedBlock createBlock(Consumer<ClockedBlock> dslFragment) {
		ClockedBlock block = createBlock();
		dslFragment.accept(block);
		return block;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	protected void initializeSimulation() {
		this.targetItemsForSimulation = new ArrayList<>();
		for (DesignItem item : getDesign().getItems()) {
			if (item instanceof ClockedItem) {
				ClockedItem clockedItem = (ClockedItem)item;
				if (clockedItem.getClock() == this) {
					targetItemsForSimulation.add(clockedItem);
				}
			}
		}
	}

	public void simulateClockEdge() {
		for (ClockedItem item : targetItemsForSimulation) {
			item.computeNextState();
		}
		for (ClockedItem item : targetItemsForSimulation) {
			item.updateState();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot write the implementation expression for a clock signal");
	}

}
