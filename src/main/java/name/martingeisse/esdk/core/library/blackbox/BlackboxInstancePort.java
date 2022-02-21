/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.blackbox;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

/**
 *
 */
public abstract class BlackboxInstancePort extends DesignItem implements DesignItemOwned {

	private final BlackboxInstance moduleInstance;
	private final String portName;

	public BlackboxInstancePort(BlackboxInstance moduleInstance, String portName) {
		this.moduleInstance = moduleInstance;
		this.portName = portName;
		moduleInstance.addPort(portName, this);
		setName(portName);
	}

	public final BlackboxInstance getModuleInstance() {
		return moduleInstance;
	}

	public final String getPortName() {
		return portName;
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		// ports are synthesized as part of the instance they belong to
		return new EmptyVerilogContribution();
	}

	protected abstract void printPortAssignment(VerilogWriter out);

}
