/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.vector;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Note: Unlike other object fields, the list of signals must be determined in advance. This is to ensure that the
 * result width doesn't change.
 */
public final class Concatenation extends DesignItem implements VectorSignal, DesignItemOwned {

	private final ImmutableList<Signal> signals;
	private final int width;

	public Concatenation(Signal... signals) {
		this(ImmutableList.copyOf(signals));
	}

	public Concatenation(ImmutableList<Signal> signals) {

		// store signals
		for (Signal signal : signals) {
			checkSameDesign(signal);
		}
		this.signals = signals;

		// precompute total width for faster access
		int width = 0;
		for (Signal signal : signals) {
			if (signal instanceof BitSignal) {
				width++;
			} else if (signal instanceof VectorSignal) {
				width += ((VectorSignal) signal).getWidth();
			} else {
				throw new IllegalArgumentException("list of signals contains unknown signal type: " + signal);
			}
		}
		this.width = width;

	}

	public ImmutableList<Signal> getSignals() {
		return signals;
	}

	@Override
	public int getWidth() {
		return width;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		Vector result = Vector.of(0, 0);
		for (Signal elementSignal : signals) {
			if (elementSignal instanceof BitSignal) {
				result = result.concat(((BitSignal) elementSignal).getValue());
			} else if (elementSignal instanceof VectorSignal) {
				result = result.concat(((VectorSignal) elementSignal).getValue());
			} else {
				throw new RuntimeException("invalid signal: " + elementSignal);
			}
		}
		return result;
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
		if (width == 0) {
			throw new RuntimeException("cannot print zero-width concatenation");
		}

		// XST cannot handle zero-width vectors
		List<Signal> nonzeroWidthSignals = new ArrayList<>(signals);
		nonzeroWidthSignals.removeIf(signal -> (signal instanceof VectorSignal) && (((VectorSignal) signal).getWidth() == 0));

		out.print('{');
		boolean first = true;
		for (Signal signal : nonzeroWidthSignals) {
			if (first) {
				first = false;
			} else {
				out.print(", ");
			}
			out.printSignal(signal, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		}
		out.print('}');
	}

}
