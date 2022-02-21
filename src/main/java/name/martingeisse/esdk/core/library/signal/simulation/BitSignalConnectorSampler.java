/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.clocked.ClockedItem;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.connector.BitConnector;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

/**
 * Combines the functionality of {@link BitConnector} and {@link BitSampler} since having to use those
 * two classes together (which is a common case) does not really simplify the implementation of a custom item.
 */
public final class BitSignalConnectorSampler extends ClockedItem {

	private BitSignal connected;
	private boolean sample;

	public BitSignalConnectorSampler(ClockSignal clockSignal) {
		super(clockSignal);
	}

	public BitSignal getConnected() {
		return connected;
	}

	public void setConnected(BitSignal connected) {
		this.connected = connected;
	}

	public boolean getSample() {
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
