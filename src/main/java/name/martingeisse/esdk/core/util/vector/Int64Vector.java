/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.util.vector;

import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
final class Int64Vector extends Vector {

	private final long value;

	Int64Vector(int width, long value) {
		super(width);
		if (width > 64) {
			throw new IllegalArgumentException("this class does not support widths greater than 64, was: " + width);
		}
		if (width < 64) {
			long mask = ((1L << width) - 1);
			if ((value & mask) != value) {
				throw new IllegalArgumentException("value " + value + " has more than " + width + " bits");
			}
		}
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Int64Vector) {
			Int64Vector other = (Int64Vector) obj;
			return getWidth() == other.getWidth() && value == other.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getWidth()).append(value).toHashCode();
	}

	@Override
	public int getAsUnsignedInt() throws ArithmeticException {
		if (getWidth() > 31) {
			throw new ArithmeticException("cannot convert a vector of width " + getWidth() + " to int");
		}
		return (int) value;
	}

	@Override
	public long getAsUnsignedLong() throws ArithmeticException {
		if (getWidth() > 63) {
			throw new ArithmeticException("cannot convert a vector of width " + getWidth() + " to long");
		}
		return value;
	}

	@Override
	public int getAsSignedInt() throws IllegalStateException {
		if (getWidth() > 32) {
			throw new IllegalStateException("cannot return the bits of a vector of width " + getWidth() + " as int");
		}
		return (int) value;
	}

	@Override
	public long getAsSignedLong() {
		return value;
	}

	private long expectSameWidth(Vector other) {
		if (getWidth() != other.getWidth()) {
			throw new IllegalArgumentException("expected a vector of same width as this (" + getWidth() + "), got " + other.getWidth());
		}
		return ((Int64Vector) other).value;
	}

	private Int64Vector truncate(long result) {
		return truncate(result, getWidth());
	}

	private static Int64Vector truncate(long result, int width) {
		if (width < 64) {
			long truncated = result & ((1L << width) - 1);
			return new Int64Vector(width, truncated);
		} else {
			return new Int64Vector(width, result);
		}
	}

	@Override
	public Vector add(Vector other) {
		return truncate(value + expectSameWidth(other));
	}

	@Override
	public Vector subtract(Vector other) {
		return truncate(value - expectSameWidth(other));
	}

	@Override
	public Vector multiply(Vector other) {
		return truncate(value * expectSameWidth(other));
	}

	@Override
	public boolean select(int index) {
		int width = getWidth();
		if (index < 0 || index >= width) {
			throw new IllegalArgumentException("invalid index " + index + " for width " + width);
		}
		return ((value >> index) & 1) != 0;
	}

	@Override
	public boolean select(Vector index) {
		int indexWidth = index.getWidth();
		if (indexWidth > 31 || (1 << indexWidth > getWidth())) {
			throw new IllegalArgumentException("index width " + indexWidth + " is too wide for vector width " + getWidth());
		}
		return select(index.getAsUnsignedInt());
	}

	@Override
	public Vector select(int from, int to) {
		if (to < 0 || from < to || from >= getWidth()) {
			throw new IllegalArgumentException("invalid range [" + from + " .. " + to + "] for width " + getWidth());
		}
		int selectedWidth = from - to + 1;
		return truncate(value >> to, selectedWidth);
	}

	@Override
	public Vector prepend(boolean bit) {
		return new Int64Vector(getWidth() + 1, (bit ? (1L << getWidth()) : 0) | value);
	}

	@Override
	public Vector concat(boolean bit) {
		return new Int64Vector(getWidth() + 1, (value << 1) | (bit ? 1 : 0));
	}

	@Override
	public Vector concat(Vector vector) {
		int otherWidth = vector.getWidth();
		return new Int64Vector(getWidth() + otherWidth, value << otherWidth | vector.getAsSignedLong());
	}

	@Override
	public Vector not() {
		return truncate(~value);
	}

	@Override
	public Vector negate() {
		return truncate(-value);
	}

	@Override
	public Vector and(Vector other) {
		return new Int64Vector(getWidth(), value & expectSameWidth(other));
	}

	@Override
	public Vector or(Vector other) {
		return new Int64Vector(getWidth(), value | expectSameWidth(other));
	}

	@Override
	public Vector xor(Vector other) {
		return new Int64Vector(getWidth(), value ^ expectSameWidth(other));
	}

	@Override
	public Vector xnor(Vector other) {
		return new Int64Vector(getWidth(), ~(value ^ expectSameWidth(other)));
	}

	@Override
	public Vector shiftLeft(int amount) {
		if (amount < 0 || amount >= getWidth()) {
			throw new IllegalArgumentException("invalid shift amount " + amount + " for width " + getWidth());
		}
		return truncate(value << amount);
	}

	@Override
	public Vector shiftRight(int amount) {
		if (amount < 0 || amount >= getWidth()) {
			throw new IllegalArgumentException("invalid shift amount " + amount + " for width " + getWidth());
		}
		return new Int64Vector(getWidth(), value >>> amount);
	}

	@Override
	public int compareUnsigned(Vector other) {
		return Long.compareUnsigned(value, expectSameWidth(other));
	}

	@Override
    public void printDigits(VerilogExpressionWriter out) {
		int width = getWidth();
		int printWidth = (width + 3) / 4;
		String zeros = StringUtils.repeat('0', printWidth);
		String digits = Long.toString(value, 16);
		String combined = zeros + digits;
		out.print(combined.substring(combined.length() - printWidth));
	}

}
