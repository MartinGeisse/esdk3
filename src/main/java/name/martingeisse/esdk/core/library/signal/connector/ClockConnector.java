/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.connector;

import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.ClockSignal;

/**
 * Clock-typed signal connector. See {@link SignalConnector} for details.
 */
public final class ClockConnector extends SignalConnector implements ClockSignal {

	private ClockSignal connected;

	@Override
	public ClockSignal getConnected() {
		return connected;
	}

	/**
	 * Same as connect(), but using Java beans naming.
	 */
	public void setConnected(ClockSignal connected) {
		this.connected = connected;
	}

	/**
	 * Same as setConnected(), but using DSL syntax.
	 */
	public void connect(ClockSignal connected) {
		this.connected = connected;
	}

	@Override
	public Clock getClock() {
		if (connected == null) {
			throw new RuntimeException("cannot getClock(): clock signal connector is not yet connected");
		}
		return connected.getClock();
	}

}
