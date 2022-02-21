/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.blackbox;

import name.martingeisse.esdk.core.library.signal.VectorConstant;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public final class BlackboxInstanceVectorOutputPort extends BlackboxInstanceOutputPort implements VectorSignal {

	private VectorSignal simulationSignal;

	public BlackboxInstanceVectorOutputPort(BlackboxInstance moduleInstance, String portName, int width) {
		super(moduleInstance, portName);
		this.simulationSignal = new VectorConstant(Vector.of(width, 0));
	}

	@Override
	public VectorSignal getSimulationSignal() {
		return simulationSignal;
	}

	public void setSimulationSignal(VectorSignal simulationSignal) {
		this.simulationSignal = simulationSignal;
	}

	@Override
	public int getWidth() {
		return simulationSignal.getWidth();
	}

	@Override
	public Vector getValue() {
		return simulationSignal.getValue();
	}

}
