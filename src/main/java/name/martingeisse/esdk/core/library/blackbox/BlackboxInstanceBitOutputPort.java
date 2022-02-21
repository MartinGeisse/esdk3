/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.blackbox;

import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.BitSignal;

/**
 *
 */
public final class BlackboxInstanceBitOutputPort extends BlackboxInstanceOutputPort implements BitSignal {

	private BitSignal simulationSignal;

	public BlackboxInstanceBitOutputPort(BlackboxInstance moduleInstance, String portName) {
		super(moduleInstance, portName);
		this.simulationSignal = new BitConstant(false);
	}

	@Override
	public BitSignal getSimulationSignal() {
		return simulationSignal;
	}

	public void setSimulationSignal(BitSignal simulationSignal) {
		this.simulationSignal = simulationSignal;
	}

	@Override
	public boolean getValue() {
		return simulationSignal.getValue();
	}

}
