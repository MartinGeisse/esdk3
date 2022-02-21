/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.blackbox;

import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 *
 */
public abstract class BlackboxInstanceOutputPort extends BlackboxInstancePort implements Signal {

	public BlackboxInstanceOutputPort(BlackboxInstance moduleInstance, String portName) {
		super(moduleInstance, portName);
	}

	public abstract Signal getSimulationSignal();

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		// Instance output ports don't use other signals during synthesis -- the simulation signal gets ignored for that.
	}

	@Override
	public final void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot write an implementation expression for instance output ports");
	}

	@Override
	protected void printPortAssignment(VerilogWriter out) {
		out.print("." + getPortName() + "(");
		out.printSignal(this);
		out.print(')');
	}

}
