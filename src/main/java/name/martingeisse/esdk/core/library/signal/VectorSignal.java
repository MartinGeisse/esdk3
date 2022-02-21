/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.library.signal;

import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.operation.VectorComparison;
import name.martingeisse.esdk.core.library.signal.operation.VectorNotOperation;
import name.martingeisse.esdk.core.library.signal.operation.VectorOperation;
import name.martingeisse.esdk.core.library.signal.simulation.VectorSampler;
import name.martingeisse.esdk.core.library.signal.vector.ConstantIndexSelection;
import name.martingeisse.esdk.core.library.signal.vector.IndexSelection;
import name.martingeisse.esdk.core.library.signal.vector.RangeSelection;
import name.martingeisse.esdk.core.library.signal.vector.VectorRepetition;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public interface VectorSignal extends Signal {

	int getWidth();

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	Vector getValue();

	// ----------------------------------------------------------------------------------------------------------------
	// factory methods
	// ----------------------------------------------------------------------------------------------------------------

	//
	// selection
	//

	default BitSignal select(VectorSignal index) {
		return new IndexSelection(this, index);
	}

	default BitSignal select(int index) {
		return new ConstantIndexSelection(this, index);
	}

	default VectorSignal select(int from, int to) {
		return new RangeSelection(this, from, to);
	}

	//
	// vector operations
	//

	default VectorNotOperation not() {
		return new VectorNotOperation(this);
	}

	default VectorSignal operation(VectorOperation.Operator operator, VectorSignal rightOperand) {
		return new VectorOperation(operator, this, rightOperand);
	}

	default VectorSignal operation(VectorOperation.Operator operator, Vector rightOperand) {
		return operation(operator, new VectorConstant(rightOperand));
	}

	default VectorSignal operation(VectorOperation.Operator operator, int rightOperand) {
		return operation(operator, new VectorConstant(getWidth(), rightOperand));
	}

	default VectorSignal add(VectorSignal rightOperand) {
		return operation(VectorOperation.Operator.ADD, rightOperand);
	}

	default VectorSignal add(Vector rightOperand) {
		return operation(VectorOperation.Operator.ADD, rightOperand);
	}

	default VectorSignal add(int rightOperand) {
		return operation(VectorOperation.Operator.ADD, rightOperand);
	}

	default VectorSignal subtract(VectorSignal rightOperand) {
		return operation(VectorOperation.Operator.SUBTRACT, rightOperand);
	}

	default VectorSignal subtract(Vector rightOperand) {
		return operation(VectorOperation.Operator.SUBTRACT, rightOperand);
	}

	default VectorSignal subtract(int rightOperand) {
		return operation(VectorOperation.Operator.SUBTRACT, rightOperand);
	}

	default VectorSignal multiply(VectorSignal rightOperand) {
		return operation(VectorOperation.Operator.MULTIPLY, rightOperand);
	}

	default VectorSignal multiply(Vector rightOperand) {
		return operation(VectorOperation.Operator.MULTIPLY, rightOperand);
	}

	default VectorSignal multiply(int rightOperand) {
		return operation(VectorOperation.Operator.MULTIPLY, rightOperand);
	}

	default VectorSignal and(VectorSignal rightOperand) {
		return operation(VectorOperation.Operator.AND, rightOperand);
	}

	default VectorSignal and(Vector rightOperand) {
		return operation(VectorOperation.Operator.AND, rightOperand);
	}

	default VectorSignal and(int rightOperand) {
		return operation(VectorOperation.Operator.AND, rightOperand);
	}

	default VectorSignal or(VectorSignal rightOperand) {
		return operation(VectorOperation.Operator.OR, rightOperand);
	}

	default VectorSignal or(Vector rightOperand) {
		return operation(VectorOperation.Operator.OR, rightOperand);
	}

	default VectorSignal or(int rightOperand) {
		return operation(VectorOperation.Operator.OR, rightOperand);
	}

	default VectorSignal xor(VectorSignal rightOperand) {
		return operation(VectorOperation.Operator.XOR, rightOperand);
	}

	default VectorSignal xor(Vector rightOperand) {
		return operation(VectorOperation.Operator.XOR, rightOperand);
	}

	default VectorSignal xor(int rightOperand) {
		return operation(VectorOperation.Operator.XOR, rightOperand);
	}

	//
	// comparisons
	//

	default BitSignal comparison(VectorComparison.Operator operator, VectorSignal rightOperand) {
		return new VectorComparison(operator, this, rightOperand);
	}

	default BitSignal comparison(VectorComparison.Operator operator, Vector rightOperand) {
		return comparison(operator, new VectorConstant(rightOperand));
	}

	default BitSignal comparison(VectorComparison.Operator operator, int rightOperand) {
		return comparison(operator, new VectorConstant(getWidth(), rightOperand));
	}

	default BitSignal compareEqual(VectorSignal rightOperand) {
		return comparison(VectorComparison.Operator.EQUAL, rightOperand);
	}

	default BitSignal compareEqual(Vector rightOperand) {
		return comparison(VectorComparison.Operator.EQUAL, rightOperand);
	}

	default BitSignal compareEqual(int rightOperand) {
		return comparison(VectorComparison.Operator.EQUAL, rightOperand);
	}

	default BitSignal compareNotEqual(VectorSignal rightOperand) {
		return comparison(VectorComparison.Operator.NOT_EQUAL, rightOperand);
	}

	default BitSignal compareNotEqual(Vector rightOperand) {
		return comparison(VectorComparison.Operator.NOT_EQUAL, rightOperand);
	}

	default BitSignal compareNotEqual(int rightOperand) {
		return comparison(VectorComparison.Operator.NOT_EQUAL, rightOperand);
	}

	default BitSignal compareLessThan(VectorSignal rightOperand) {
		return comparison(VectorComparison.Operator.LESS_THAN, rightOperand);
	}

	default BitSignal compareLessThan(Vector rightOperand) {
		return comparison(VectorComparison.Operator.LESS_THAN, rightOperand);
	}

	default BitSignal compareLessThan(int rightOperand) {
		return comparison(VectorComparison.Operator.LESS_THAN, rightOperand);
	}

	default BitSignal compareLessThanOrEqual(VectorSignal rightOperand) {
		return comparison(VectorComparison.Operator.LESS_THAN_OR_EQUAL, rightOperand);
	}

	default BitSignal compareLessThanOrEqual(Vector rightOperand) {
		return comparison(VectorComparison.Operator.LESS_THAN_OR_EQUAL, rightOperand);
	}

	default BitSignal compareLessThanOrEqual(int rightOperand) {
		return comparison(VectorComparison.Operator.LESS_THAN_OR_EQUAL, rightOperand);
	}

	default BitSignal compareGreaterThan(VectorSignal rightOperand) {
		return comparison(VectorComparison.Operator.GREATER_THAN, rightOperand);
	}

	default BitSignal compareGreaterThan(Vector rightOperand) {
		return comparison(VectorComparison.Operator.GREATER_THAN, rightOperand);
	}

	default BitSignal compareGreaterThan(int rightOperand) {
		return comparison(VectorComparison.Operator.GREATER_THAN, rightOperand);
	}

	default BitSignal compareGreaterThanOrEqual(VectorSignal rightOperand) {
		return comparison(VectorComparison.Operator.GREATER_THAN_OR_EQUAL, rightOperand);
	}

	default BitSignal compareGreaterThanOrEqual(Vector rightOperand) {
		return comparison(VectorComparison.Operator.GREATER_THAN_OR_EQUAL, rightOperand);
	}

	default BitSignal compareGreaterThanOrEqual(int rightOperand) {
		return comparison(VectorComparison.Operator.GREATER_THAN_OR_EQUAL, rightOperand);
	}

	//
	// other
	//

	default VectorRepetition repeat(int repetitions) {
		return new VectorRepetition(this, repetitions);
	}

	default VectorSampler sampler(Clock clock) {
		return new VectorSampler(clock, this);
	}

}
