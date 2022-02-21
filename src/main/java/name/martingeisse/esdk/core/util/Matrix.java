/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.util;

import name.martingeisse.esdk.core.util.vector.Vector;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A mutable bit matrix. This is basically a mutable fixed-size array of {@link Vector}s which all have the same
 * pre-configured width. Access happens in rows, not individual bits.
 */
public final class Matrix {

	private static final Pattern MATRIX_FILE_ROW_PATTERN = Pattern.compile("[0-9a-fA-F]+");

	private final int rowCount;
	private final int columnCount;
	private final Vector[] rows;
	private final Vector defaultRowValue;

	public Matrix(int rowCount, int columnCount) {
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		this.rows = new Vector[rowCount];
		this.defaultRowValue = Vector.of(columnCount, 0);
	}

	public int getRowCount() {
		return rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	private void checkRowIndex(int rowIndex) {
		if (rowIndex < 0 || rowIndex >= rowCount) {
			throw new IllegalArgumentException("invalid row index " + rowIndex + " for row count " + rowCount);
		}
	}

	private void checkRowIndexRange(int fromRowIndex, int toRowIndex) {
		if (fromRowIndex < 0 || toRowIndex > rowCount || fromRowIndex > toRowIndex) {
			throw new IllegalArgumentException("invalid row index range " + fromRowIndex + ".." + toRowIndex + " for row count " + rowCount);
		}
	}

	public Vector getRow(int rowIndex) {
		checkRowIndex(rowIndex);
		Vector row = rows[rowIndex];
		return (row == null ? defaultRowValue : row);
	}

	public void setRow(int rowIndex, Vector row) {
		checkRowIndex(rowIndex);
		if (row == null) {
			throw new IllegalArgumentException("row cannot be null");
		}
		if (row.getWidth() != columnCount) {
			throw new IllegalArgumentException("row has wrong width " + row.getWidth() + ", expected " + columnCount);
		}
		rows[rowIndex] = row;
	}

	public void setRows(int fromRowIndex, int toRowIndex, Vector row) {
		checkRowIndexRange(fromRowIndex, toRowIndex);
		if (row == null) {
			throw new IllegalArgumentException("row cannot be null");
		}
		if (row.getWidth() != columnCount) {
			throw new IllegalArgumentException("row has wrong width " + row.getWidth() + ", expected " + columnCount);
		}
		Arrays.fill(rows, fromRowIndex, toRowIndex, row);
	}

	public void writeToMif(PrintWriter out) {
		int matrixDigitCount = (columnCount + 3) / 4;
		String allZeros = StringUtils.repeat('0', matrixDigitCount);
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			Vector row = rows[rowIndex];
			if (row == null) {
				row = defaultRowValue;
			}
			String digits = row.getDigits();
			String zeros = allZeros.substring(digits.length());
			out.print(zeros);
			out.println(digits);
		}
	}

	public static Matrix load(Class<?> anchorClass, String filename, int rows, int columns) {
		Matrix matrix = new Matrix(rows, columns);
		try (InputStream inputStream = anchorClass.getResourceAsStream(filename)) {
			if (inputStream == null) {
				throw new RuntimeException("matrix file not found: " + anchorClass + " / " + filename);
			}
			try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
				new HeadBodyReader() {

					private boolean rowsOk = false, columnsOk = false;
					private int firstEmptyBodyLine = -1;

					@Override
					protected void onHeadProperty(String key, String value) throws FormatException {
						switch (key) {

							case "rows":
								if (expectNonNegativeInteger(key, value) != rows) {
									throw new FormatException("mismatching number of rows");
								}
								rowsOk = true;
								break;

							case "columns":
								if (expectNonNegativeInteger(key, value) != columns) {
									throw new FormatException("mismatching number of columns");
								}
								columnsOk = true;
								break;

							default:
								throw new FormatException("unknown property: " + key);

						}
					}

					private int expectNonNegativeInteger(String key, String text) throws FormatException {
						int value;
						try {
							value = Integer.parseInt(text);
						} catch (NumberFormatException e) {
							throw new FormatException("invalid value for property '" + key + "'");
						}
						if (value < 0) {
							throw new FormatException("property '" + key + "' cannot be negative");
						}
						return value;
					}

					@Override
					protected void onStartBody() throws FormatException {
						if (!rowsOk) {
							throw new FormatException("missing 'rows' property");
						}
						if (!columnsOk) {
							throw new FormatException("missing 'columns' property");
						}
					}

					@Override
					protected void onBodyLine(int totalLineIndex, int bodyLineIndex, String line) throws FormatException {
						line = line.trim();
						if (line.isEmpty()) {
							if (firstEmptyBodyLine == -1) {
								firstEmptyBodyLine = totalLineIndex;
							}
							return;
						}
						if (firstEmptyBodyLine != -1) {
							throw new FormatException("body contains empty line(s) starting at line " + firstEmptyBodyLine);
						}
						if (!MATRIX_FILE_ROW_PATTERN.matcher(line).matches()) {
							throw new FormatException("invalid value at line " + totalLineIndex);
						}
						matrix.setRow(bodyLineIndex, Vector.parseHex(columns, line));
					}

				}.readFrom(inputStreamReader);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("could not load matrix " + anchorClass + " / " + filename, e);
		}
		return matrix;
	}

}
