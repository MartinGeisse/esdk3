/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.connector;

import name.martingeisse.esdk.core.library.signal.BitSignal;

/**
 * Bit-typed signal connector. See {@link SignalConnector} for details.
 */
public final class BitConnector extends SignalConnector implements BitSignal {

	private BitSignal connected;

	@Override
	public BitSignal getConnected() {
		return connected;
	}

	/**
	 * Same as connect(), but using Java beans naming.
	 */
	public void setConnected(BitSignal connected) {
		this.connected = connected;
	}

	/**
	 * Same as setConnected(), but using DSL syntax.
	 */
	public void connect(BitSignal connected) {
		this.connected = connected;
	}

	@Override
	public boolean getValue() {
		return connected.getValue();
	}

}
