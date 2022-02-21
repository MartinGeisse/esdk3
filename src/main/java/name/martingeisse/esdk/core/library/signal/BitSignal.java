/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal;

import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.mux.ConditionalBitOperation;
import name.martingeisse.esdk.core.library.signal.mux.ConditionalVectorOperation;
import name.martingeisse.esdk.core.library.signal.operation.BitNotOperation;
import name.martingeisse.esdk.core.library.signal.operation.BitOperation;
import name.martingeisse.esdk.core.library.signal.simulation.BitSampler;
import name.martingeisse.esdk.core.library.signal.vector.BitRepetition;
import name.martingeisse.esdk.core.library.signal.vector.OneBitVectorSignal;

/**
 *
 */
public interface BitSignal extends Signal {

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	boolean getValue();

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	default BitNotOperation not() {
		return new BitNotOperation(this);
	}

	default BitOperation and(BitSignal other) {
		return new BitOperation(BitOperation.Operator.AND, this, other);
	}

	default BitOperation or(BitSignal other) {
		return new BitOperation(BitOperation.Operator.OR, this, other);
	}

	default BitOperation xor(BitSignal other) {
		return new BitOperation(BitOperation.Operator.XOR, this, other);
	}

	default BitOperation xnor(BitSignal other) {
		return new BitOperation(BitOperation.Operator.XNOR, this, other);
	}

	default ConditionalBitOperation conditional(BitSignal onTrue, BitSignal onFalse) {
		return new ConditionalBitOperation(this, onTrue, onFalse);
	}

	default ConditionalVectorOperation conditional(VectorSignal onTrue, VectorSignal onFalse) {
		return new ConditionalVectorOperation(this, onTrue, onFalse);
	}

	default BitSignal compareEqual(BitSignal other) {
		return xnor(other);
	}

	default BitSignal compareNotEqual(BitSignal other) {
		return xor(other);
	}

	default OneBitVectorSignal asOneBitVector() {
		return new OneBitVectorSignal(this);
	}

	default BitRepetition repeat(int repetitions) {
		return new BitRepetition(this, repetitions);
	}

	default BitSampler sampler(Clock clock) {
		return new BitSampler(clock, this);
	}

}
