/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.blackbox;

import name.martingeisse.esdk.core.library.signal.VectorSignal;

/**
 *
 */
public final class BlackboxInstanceVectorInputPort extends BlackboxInstanceInputPort {

	private final int width;
	private VectorSignal assignedSignal;

	public BlackboxInstanceVectorInputPort(BlackboxInstance moduleInstance, String portName, int width) {
		super(moduleInstance, portName);
		this.width = width;
	}

	public BlackboxInstanceVectorInputPort(BlackboxInstance moduleInstance, String portName, int width, VectorSignal assignedSignal) {
		this(moduleInstance, portName, width);
		setAssignedSignal(assignedSignal);
	}

	@Override
	public VectorSignal getAssignedSignal() {
		return assignedSignal;
	}

	public void setAssignedSignal(VectorSignal assignedSignal) {
		if (assignedSignal != null && assignedSignal.getWidth() != width) {
			throw new IllegalArgumentException("expected signal width " + width + ", got " + assignedSignal.getWidth());
		}
		this.assignedSignal = assignedSignal;
	}

}
