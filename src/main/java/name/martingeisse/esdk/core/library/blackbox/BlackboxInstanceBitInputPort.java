/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.blackbox;

import name.martingeisse.esdk.core.library.signal.BitSignal;

/**
 *
 */
public final class BlackboxInstanceBitInputPort extends BlackboxInstanceInputPort {

	private BitSignal assignedSignal;

	public BlackboxInstanceBitInputPort(BlackboxInstance moduleInstance, String portName) {
		this(moduleInstance, portName, null);
	}

	public BlackboxInstanceBitInputPort(BlackboxInstance moduleInstance, String portName, BitSignal assignedSignal) {
		super(moduleInstance, portName);
		this.assignedSignal = assignedSignal;
	}

	@Override
	public BitSignal getAssignedSignal() {
		return assignedSignal;
	}

	public void setAssignedSignal(BitSignal assignedSignal) {
		this.assignedSignal = assignedSignal;
	}

}
