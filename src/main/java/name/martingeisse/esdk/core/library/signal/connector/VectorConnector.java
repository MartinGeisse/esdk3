/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.connector;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * Vector-typed signal connector. See {@link SignalConnector} for details.
 */
public final class VectorConnector extends SignalConnector implements VectorSignal {

	private final int width;
	private VectorSignal connected;

	public VectorConnector(int width) {
		this.width = width;
	}

	@Override
	public VectorSignal getConnected() {
		return connected;
	}

	/**
	 * Same as connect(), but using Java beans naming.
	 */
	public void setConnected(VectorSignal connected) {
		if (connected.getWidth() != width) {
			throw new IllegalArgumentException("wrong signal width for connected signal: " +
					connected.getWidth() + " (should be " + width + ")");
		}
		this.connected = connected;
	}

	/**
	 * Same as setConnected(), but using DSL syntax.
	 */
	public void connect(VectorSignal connected) {
		setConnected(connected);
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public Vector getValue() {
		return connected.getValue();
	}

}
