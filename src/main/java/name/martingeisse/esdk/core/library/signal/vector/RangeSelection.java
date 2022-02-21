/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.vector;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
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
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public class RangeSelection extends DesignItem implements VectorSignal, DesignItemOwned {

	private final VectorSignal containerSignal;
	private final int from;
	private final int to;

	public RangeSelection(VectorSignal containerSignal, int from, int to) {
		if (from < 0 || to < 0 || from >= containerSignal.getWidth() || to >= containerSignal.getWidth() || from < to) {
			throw new IllegalArgumentException("invalid from/to indices for container width " +
				containerSignal.getWidth() + ": from = " + from + ", to = " + to);
		}
		this.containerSignal = checkSameDesign(containerSignal);
		this.from = from;
		this.to = to;
	}

	public VectorSignal getContainerSignal() {
		return containerSignal;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	@Override
	public int getWidth() {
		return from - to + 1;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		return containerSignal.getValue().select(from, to);
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
		out.print(from);
		out.print(':');
		out.print(to);
		out.print(']');
	}

}
