/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.util.BitProvider;

/**
 * Bit version of {@link SimulatedComputedSignal}.
 */
public abstract class SimulatedComputedBitSignal extends SimulatedComputedSignal implements BitSignal {

	public static SimulatedComputedBitSignal of(BitProvider bitProvider) {
		return new SimulatedComputedBitSignal() {
			@Override
			public boolean getValue() {
				return bitProvider.getValue();
			}
		};
	}

}
