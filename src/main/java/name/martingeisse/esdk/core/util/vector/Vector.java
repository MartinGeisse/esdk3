/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.util.vector;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.RealVerilogExpressionWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;

/**
 * Represents a bit vector with a specific width.
 * <p>
 * Equality rules: Two vectors are equal if and only if their width and bits are equal. That is, leading zeroes are
 * significant when checking equality. Normally, vectors of different width are not compared at all, so this
 * behavior is defined only for cases such as using vectors as map keys.
 * <p>
 */
public abstract class Vector {

	private final int width;

	/**
	 * Creates a vector value with the specified width and whose bits are the two's complement representation of the
	 * specified value.
	 */
	public static Vector of(int width, long value) {
		if (width > 64) {
			throw new UnsupportedOperationException("vectors larger than 64 bits not yet implemented");
		}
		return new Int64Vector(width, value);
	}

	public static Vector repeat(int width, boolean bit) {
		if (width == 64) {
			return Vector.of(width, bit ? -1 : 0);
		} else {
			return Vector.of(width, bit ? ((1L << width) - 1) : 0);
		}
	}

	public static Vector parseHex(int width, String digits) {
		if (width > 64) {
			throw new UnsupportedOperationException("vectors larger than 64 bits not yet implemented");
		}
		return new Int64Vector(width, new BigInteger(digits, 16).longValue());
	}

	Vector(int width) {
		if (width < 0) {
			throw new IllegalArgumentException("width cannot be negative");
		}
		this.width = width;
	}

	public final int getWidth() {
		return width;
	}

	/**
	 * Returns the value of this vector as an int, using unsigned representation. This vector must be at most 31 bits
	 * wide (otherwise its value cannot be represented as an int).
	 */
	public abstract int getAsUnsignedInt() throws ArithmeticException;

	/**
	 * Returns the value of this vector as a long, using unsigned representation. This vector must be at most 63 bits
	 * wide (otherwise its value cannot be represented as a long).
	 */
	public abstract long getAsUnsignedLong() throws ArithmeticException;

	/**
	 * Returns the value of this vector as an int, using signed representation. This vector must be at most 32 bits
	 * wide (otherwise its value cannot be represented as an int).
	 */
	public abstract int getAsSignedInt() throws IllegalStateException;

	/**
	 * Returns the value of this vector as a long, using signed representation. This vector must be at most 64 bits
	 * wide (otherwise its value cannot be represented as a long).
	 */
	public abstract long getAsSignedLong() throws IllegalStateException;

	/**
	 * Expects this vector and the argument vector to be of the same size. Interprets the vectors as unsigned numbers,
	 * adds them, truncates the result to the same width and returns it as a vector. (Due to the truncation,
	 * signed / unsigned does not actually make a difference).
	 */
	public abstract Vector add(Vector other);

	/**
	 * Expects this vector and the argument vector to be of the same size. Interprets the vectors as unsigned numbers,
	 * subtracts them, truncates the result to the same width and returns it as a vector. (Due to the truncation,
	 * signed / unsigned does not actually make a difference).
	 */
	public abstract Vector subtract(Vector other);

	/**
	 * Expects this vector and the argument vector to be of the same size. Interprets the vectors as unsigned numbers,
	 * multiplies them, truncates the result to the same width and returns it as a vector. (Due to the truncation,
	 * signed / unsigned does not actually make a difference).
	 * <p>
	 * Note that often, the full result of multiplication -- which has up to twice that width -- is needed. In that
	 * case, both vectors must be extended to the full width before multiplication. Signed/unsigned then *does* make
	 * a difference, and must be taken into account when extending the inputs (either sign-extending or zero-extending
	 * them).
	 */
	public abstract Vector multiply(Vector other);

	/**
	 * Selects a single bit.
	 */
	public abstract boolean select(int index);

	/**
	 * Selects a single bit.
	 */
	public abstract boolean select(Vector index);

	/**
	 * Selects a range of bits as a vector.
	 */
	public abstract Vector select(int from, int to);

	/**
	 * Concatenates the specified bit (left operand) and this vector (right operand).
	 */
	public abstract Vector prepend(boolean bit);

	/**
	 * Concatenates this vector (left operand) and the specified bit (right operand).
	 */
	public abstract Vector concat(boolean bit);

	/**
	 * Concatenates this vector (left operand) and the specified vector (right operand).
	 */
	public abstract Vector concat(Vector vector);

	/**
	 * Returns the bitwise NOT of this vector.
	 */
	public abstract Vector not();

	/**
	 * Returns the 2's complement negation of this vector.
	 */
	public abstract Vector negate();

	/**
	 * Expects this vector and the argument vector to be of the same size and bitwise-ANDs them.
	 */
	public abstract Vector and(Vector other);

	/**
	 * Expects this vector and the argument vector to be of the same size and bitwise-ORs them.
	 */
	public abstract Vector or(Vector other);

	/**
	 * Expects this vector and the argument vector to be of the same size and bitwise-XORs them.
	 */
	public abstract Vector xor(Vector other);

	/**
	 * Expects this vector and the argument vector to be of the same size and bitwise-XNORs them.
	 */
	public abstract Vector xnor(Vector other);

	/**
	 * Returns a vector with the same width as this vector, but with the value shifted left by the specified amount.
	 * Shifted-in bits are zero. Shifted-out bits are discarded.
	 * <p>
	 * The amount must not be negative, and must be less than the width of this vector.
	 */
	public abstract Vector shiftLeft(int amount);

	/**
	 * Returns a vector with the same width as this vector, but with the value shifted right by the specified amount.
	 * Shifted-in bits are zero. Shifted-out bits are discarded.
	 * <p>
	 * The amount must not be negative, and must be less than the width of this vector.
	 */
	public abstract Vector shiftRight(int amount);

	/**
	 * Numerically compares the unsigned meaning of this vector and the specified other vector. Returns -1, 0 or 1
	 * if this vector is less than, equal to, or greater than the argument vector, respectively. The argument must
	 * have the same width as this vector.
	 */
	public abstract int compareUnsigned(Vector other);

	public String getVerilogExpression() {
		StringWriter stringWriter = new StringWriter();
		printVerilogExpression(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	public void printVerilogExpression(PrintWriter out) {
		printVerilogExpression(new MyVerilogExpressionWriter(out));
	}

	public void printVerilogExpression(VerilogExpressionWriter out) {
		out.print(width);
		out.print("'h");
		printDigits(out);
	}

	public String getDigits() {
		StringWriter stringWriter = new StringWriter();
		printDigits(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	public void printDigits(PrintWriter out) {
		printDigits(new MyVerilogExpressionWriter(out));
	}

	public abstract void printDigits(VerilogExpressionWriter out);

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		printVerilogExpression(printWriter);
		printWriter.flush();
		return stringWriter.toString();
	}

	private static final class MyVerilogExpressionWriter extends RealVerilogExpressionWriter {

		MyVerilogExpressionWriter(PrintWriter out) {
			super(out);
		}

		@Override
		public void internalPrintSignal(Signal signal, VerilogExpressionNesting nesting) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void internalPrintMemory(ProceduralMemory memory) {
			throw new UnsupportedOperationException();
		}

	}
}
