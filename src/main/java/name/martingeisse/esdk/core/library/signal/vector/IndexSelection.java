/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.vector;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorConstant;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogSignalDeclarationKeyword;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 *
 */
public final class IndexSelection extends DesignItem implements BitSignal, DesignItemOwned {

	private final VectorSignal containerSignal;
	private final VectorSignal indexSignal;

	public IndexSelection(VectorSignal containerSignal, VectorSignal indexSignal) {
		if (containerSignal.getWidth() < (1 << indexSignal.getWidth())) {
			throw new IllegalArgumentException("container of width " + containerSignal.getWidth() + " is too small for index of width " + indexSignal.getWidth());
		}
		this.containerSignal = checkSameDesign(containerSignal);
		this.indexSignal = checkSameDesign(indexSignal);
	}

	public VectorSignal getContainerSignal() {
		return containerSignal;
	}

	public VectorSignal getIndexSignal() {
		return indexSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return containerSignal.getValue().select(indexSignal.getValue().getAsUnsignedInt());
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		// The container of a selection is special in that it even cannot be a constant, only a signal. So we treat
		// constants specially. Anything that is not explicitly a constant (e.g. complex signals that only have
		// constant inputs and could be constant-folded) will not be recognized by the instanceof, but they will be
		// moved out because they don't match SIGNALS_AND_CONSTANTS.
		if (containerSignal instanceof VectorConstant) {
			return new VerilogContribution() {

				@Override
				public void prepareSynthesis(SynthesisPreparationContext context) {
					context.declareSignal(containerSignal, VerilogSignalDeclarationKeyword.WIRE, true);
				}

				@Override
				public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				}

				@Override
				public void printImplementation(VerilogWriter out) {
				}

			};
		} else {
			return new EmptyVerilogContribution();
		}
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return nesting != VerilogExpressionNesting.SIGNALS_AND_CONSTANTS;
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.printSignal(containerSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print('[');
		out.printSignal(indexSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print(']');
	}

}
