/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal.mux;

import name.martingeisse.esdk.core.library.signal.BitSignal;

/**
 *
 */
public final class ConditionalBitOperation extends ConditionalOperation implements BitSignal {

	private final BitSignal onTrue;
	private final BitSignal onFalse;

	public ConditionalBitOperation(BitSignal condition, BitSignal onTrue, BitSignal onFalse) {
		super(condition);
		this.onTrue = checkSameDesign(onTrue);
		this.onFalse = checkSameDesign(onFalse);
	}

	public BitSignal getOnTrue() {
		return onTrue;
	}

	public BitSignal getOnFalse() {
		return onFalse;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return getCondition().getValue() ? onTrue.getValue() : onFalse.getValue();
	}

}
