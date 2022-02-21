/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.verilog;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.RealVerilogExpressionWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * Helper class to write the (line-based) contents of the main Verilog file. This class does not cover writing the
 * parts of an expression, but delegates to VerilogExpressionWriter.
 */
public abstract class VerilogWriter extends PrintWriter {

	private int indentation = 0;

	public VerilogWriter(Writer out) {
		super(out);
	}

	public void startIndentation() {
		indentation++;
	}

	public void endIndentation() {
		indentation--;
	}

	public void indent() {
		for (int i = 0; i < indentation; i++) {
			print('\t');
		}
	}

	/**
	 * Returns the Verilog name for the specified item. Returns null if the item does not have a
	 * Verilog declaration and therefore no name -- ignoring, however, whether that declaration
	 * has already been written to the output.
	 *
	 * For an item that has a name, this name may differ from the user-given name, in particular
	 * if there is no user-given name or if that name is not unique.
	 */
	public abstract String getNameOrNull(DesignItem item);

	/**
	 * Like getNameOrNull(), but fails with an exception if no name was assigned to the item.
	 */
	public String getName(DesignItem item) {
		String name = getNameOrNull(item);
		if (name == null) {
			throw new IllegalArgumentException("no verilog name has been assigned to item: " + item);
		}
		return name;
	}

	/**
	 * Prints the Verilog name of the specified item, throwing an exception if it has none.
	 */
	public void printName(DesignItem item) {
		print(getName(item));
	}

	/**
	 * Prints the expression to use for a signal at a point where the signal gets used. This either prints
	 * the signal name or the implementing expression, based on whether a declaration for the signal is
	 * available.
	 *
	 * Unlike VerilogExpressionWriter, this method does not take the nesting of the signal as a parameter.
	 * In fact, while printing the output, the nesting cannot be taken into account at all -- wrong nesting
	 * would have to enforce a declaration for the signal, but at the time the signal expression gets printed,
	 * it is too late for that. The VerilogExpressionWriter can only get away with that because the code that
	 * calls it gets run twice, once with a fake implementation that observes the nesting and decides whether
	 * a signal declaration is needed, and once while printing -- the latter ignoring the nesting just like this
	 * method does.
	 *
	 * Since on the top level, no fake printing is done, an item that is going to call this method must also
	 * implement analyzeSignalUsage() and report the nesting to the SignalUsageConsumer.
	 */
	public void printSignal(Signal signal) {
		if (signal == null) {
			throw new IllegalArgumentException("signal argument is null");
		}
		String name = getNameOrNull(signal.getDesignItem());
		if (name == null) {
			printImplementationExpression(signal);
		} else {
			print(name);
		}
	}

	/**
	 * Prints the expression to use for a procedural memory at a point where the memory gets used.
	 */
	public void printMemory(ProceduralMemory memory) {
		printName(memory);
	}

	/**
	 * This method should normally not be called directly since {@link #printSignal(Signal)}
	 * is usually the right method to use. This method works similarly, but for named signals,
	 * it prints the defining expression, not the name.
	 */
	void printImplementationExpression(Signal signal) {
		signal.printVerilogImplementationExpression(new RealVerilogExpressionWriter(this) {

			@Override
			public void internalPrintSignal(Signal signal, VerilogExpressionNesting nesting) {
				VerilogWriter.this.printSignal(signal);
			}

			@Override
			public void internalPrintMemory(ProceduralMemory memory) {
				VerilogWriter.this.printMemory(memory);
			}

		});
	}

}
