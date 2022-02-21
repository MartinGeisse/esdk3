/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 * A signal whose value can be computed by high-level models. This is meant as a bridge to simulate high-level models
 * and RTL models together. Unlike other signals, this class is not meant for synthesis.
 *
 * Using this signal in a way that is not relevant to synthesis, such as a simulation replacement signal of instance
 * ports, is allowed.
 */
public abstract class SimulatedComputedSignal extends DesignItem implements Signal, DesignItemOwned {

	@Override
	public final VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public final void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot print implementation expression for " + this);
	}

}
