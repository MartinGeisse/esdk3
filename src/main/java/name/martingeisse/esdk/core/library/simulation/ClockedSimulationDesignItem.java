/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.simulation;

import name.martingeisse.esdk.core.library.clocked.ClockedItem;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

/**
 * Like {@link ClockedItem}, but prevents synthesis.
 */
public abstract class ClockedSimulationDesignItem extends ClockedItem {

	public ClockedSimulationDesignItem(ClockSignal clockSignal) {
		super(clockSignal);
	}

	@Override
	public final VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
	}

}
