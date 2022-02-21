package name.martingeisse.esdk.core.util;

import name.martingeisse.esdk.core.util.vector.Vector;

public final class MemoryDataUtil {

    /**
     * Returns an 8-bit wide Matrix with as many rows and the same contents as the probided data array.
     */
    public static Matrix convertByteArrayToMatrix(byte[] data) {
        Matrix matrix = new Matrix(data.length, 8);
        writeByteArrayToMatrix(data, matrix);
        return matrix;
    }

    /**
     * Expects the matrix to be 8 bits wide, and writes the bytes from the provided data array to it.
     * The matrix must have at least as many rows as the length of the data array. If the matrix has more rows,
     * those are not changed.
     */
    public static void writeByteArrayToMatrix(byte[] data, Matrix matrix) {
        if (matrix.getRowCount() < data.length) {
            throw new IllegalArgumentException("matrix too small: has " + matrix.getRowCount() +
                    " rows but needs " + data.length);
        }
        if (matrix.getColumnCount() != 8) {
            throw new IllegalArgumentException("expected matrix to have 8 columns but has " + matrix.getColumnCount());
        }
        for (int i = 0; i < data.length; i++) {
            matrix.setRow(i, Vector.of(8, data[i] & 0xff));
        }
    }

}
