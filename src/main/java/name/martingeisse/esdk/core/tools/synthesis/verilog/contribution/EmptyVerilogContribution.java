/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.verilog.contribution;

import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.ToplevelPortConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;

/**
 *
 */
public final class EmptyVerilogContribution implements VerilogContribution {

	public static final EmptyVerilogContribution INSTANCE = new EmptyVerilogContribution();

	@Override
	public void prepareSynthesis(SynthesisPreparationContext context) {
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
	}

	@Override
	public void analyzeToplevelPorts(ToplevelPortConsumer consumer) {
	}

	@Override
	public void printDeclarations(VerilogWriter out) {
	}

	@Override
	public void printImplementation(VerilogWriter writer) {
	}

}
