/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.blackbox;

import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;

/**
 *
 */
public abstract class BlackboxInstanceInputPort extends BlackboxInstancePort {

	public BlackboxInstanceInputPort(BlackboxInstance moduleInstance, String portName) {
		super(moduleInstance, portName);
	}

	public abstract Signal getAssignedSignal();

	@Override
	protected void printPortAssignment(VerilogWriter out) {
		out.print("." + getPortName() + "(");
		if (getAssignedSignal() == null) {
			throw new IllegalStateException("input port " + getPortName() +
					" of instance of module " + getModuleInstance().getBlackboxTemplateName() + " has no assigned signal");
		}
		out.printSignal(getAssignedSignal());
		out.print(')');
	}

}
