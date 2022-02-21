/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.clocked;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.ClockSignal;

/**
 *
 */
public abstract class ClockedItem extends DesignItem implements DesignItemOwned {

	private final ClockSignal clockSignal;

	public ClockedItem(ClockSignal clockSignal) {
		this.clockSignal = clockSignal;
	}

	public final ClockSignal getClockSignal() {
		return clockSignal;
	}

	public final Clock getClock() {
		return clockSignal.getClock();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * This method computes the next state of this item based on its current state and the signals provided by other
	 * items. It must not cause changes to any signals, or to its internal state, or to the state of any other item.
	 * <p>
	 * In RTL terms, this method runs between active clock edges and causes the register input signals to stabilize
	 * to their final values.
	 */
	public abstract void computeNextState();

	/**
	 * Updates the current state of this item based on the next state computed by {@link #computeNextState()}. This
	 * method causes signals to change values. It must not obtain the values of any signals or call getters of other
	 * items since those may or may not have changed their value already. It must not change the state of other
	 * items that trigger on the same clock edge since the order in which effects happen is undefined.
	 * <p>
	 * In RTL terms, this method runs at an active clock edge and causes all registers to load a new value.
	 */
	public abstract void updateState();

}
