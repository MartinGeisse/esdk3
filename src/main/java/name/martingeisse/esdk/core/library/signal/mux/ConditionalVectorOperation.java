/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.mux;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public final class ConditionalVectorOperation extends ConditionalOperation implements VectorSignal {

	private final VectorSignal onTrue;
	private final VectorSignal onFalse;

	public ConditionalVectorOperation(BitSignal condition, VectorSignal onTrue, VectorSignal onFalse) {
		super(condition);
		if (onTrue.getWidth() != onFalse.getWidth()) {
			throw new IllegalArgumentException("onTrue has width " + onTrue.getWidth() + " but onFalse has width " + onFalse.getWidth());
		}
		this.onTrue = checkSameDesign(onTrue);
		this.onFalse = checkSameDesign(onFalse);
	}

	public VectorSignal getOnTrue() {
		return onTrue;
	}

	public VectorSignal getOnFalse() {
		return onFalse;
	}

	@Override
	public int getWidth() {
		return onTrue.getWidth();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		return getCondition().getValue() ? onTrue.getValue() : onFalse.getValue();
	}

}
