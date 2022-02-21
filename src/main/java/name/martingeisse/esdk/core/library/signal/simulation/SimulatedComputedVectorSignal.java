/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.util.IntProvider;
import name.martingeisse.esdk.core.util.VectorProvider;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * Vector version of {@link SimulatedComputedSignal}.
 */
public abstract class SimulatedComputedVectorSignal extends SimulatedComputedSignal implements VectorSignal {

	public static SimulatedComputedVectorSignal of(int width, VectorProvider vectorProvider) {
		return new SimulatedComputedVectorSignal() {

			@Override
			public int getWidth() {
				return width;
			}

			@Override
			public Vector getValue() {
				return vectorProvider.getValue();
			}

		};
	}

	public static SimulatedComputedVectorSignal of(int width, IntProvider intProvider) {
		return new SimulatedComputedVectorSignal() {

			@Override
			public int getWidth() {
				return width;
			}

			@Override
			public Vector getValue() {
				return Vector.of(width, intProvider.getValue());
			}

		};
	}

}
