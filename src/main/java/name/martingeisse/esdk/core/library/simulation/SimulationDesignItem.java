/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.simulation;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

/**
 * Like {@link DesignItem}, but prevents synthesis.
 */
public abstract class SimulationDesignItem extends DesignItem implements DesignItemOwned {

	@Override
	public final VerilogContribution getVerilogContribution() {
		throw newSynthesisNotSupportedException();
	}

}
