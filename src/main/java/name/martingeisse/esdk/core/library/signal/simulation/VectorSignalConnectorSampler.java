/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.clocked.ClockedItem;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.VectorConnector;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * Combines the functionality of {@link VectorConnector} and {@link VectorSampler} since having to use those
 * two classes together (which is a common case) does not really simplify the implementation of a custom item.
 */
public final class VectorSignalConnectorSampler extends ClockedItem {

	private final int width;
	private VectorSignal connected;
	private Vector sample;

	public VectorSignalConnectorSampler(ClockSignal clockSignal, int width) {
		super(clockSignal);
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public VectorSignal getConnected() {
		return connected;
	}

	public void setConnected(VectorSignal connected) {
		if (connected.getWidth() != width) {
			throw new IllegalArgumentException("wrong signal width for connected signal: " +
				connected.getWidth() + " (should be " + width + ")");
		}
		this.connected = connected;
	}

	public Vector getSample() {
		return sample;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
		if (connected == null) {
			throw new IllegalStateException("no connected signal");
		}
		sample = Vector.of(getWidth(), 0);
	}

	@Override
	public void computeNextState() {
		sample = connected.getValue();
	}

	@Override
	public void updateState() {
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
