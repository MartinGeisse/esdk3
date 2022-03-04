package name.martingeisse.esdk.plot.variable;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

public abstract class MemorySample {

    private MemorySample() {
    }

    public abstract void applyTo(Matrix matrix);

    public static final class Full extends MemorySample {

        public final ImmutableList<Vector> values;

        public Full(Matrix matrix) {
            Vector[] values = new Vector[matrix.getRowCount()];
            for (int i = 0; i < values.length; i++) {
                values[i] = matrix.getRow(i);
            }
            this.values = ImmutableList.copyOf(values);
        }

        @Override
        public void applyTo(Matrix matrix) {
            for (int i = 0; i < values.size(); i++) {
                matrix.setRow(i, values.get(i));
            }
        }

    }

    public static final class Delta extends MemorySample {

        public final ImmutableList<RowPatch> rowPatches;

        public Delta(Matrix oldContents, Matrix newContents) {
            if (oldContents.getRowCount() != newContents.getRowCount() || oldContents.getColumnCount() != newContents.getColumnCount()) {
                throw new IllegalArgumentException("memory size mismatch");
            }
            List<RowPatch> rowPatches = new ArrayList<>();
            for (int i = 0; i < oldContents.getRowCount(); i++) {
                if (!oldContents.getRow(i).equals(newContents.getRow(i))) {
                    rowPatches.add(new RowPatch(i, newContents.getRow(i)));
                }
            }
            this.rowPatches = ImmutableList.copyOf(rowPatches);
        }

        @Override
        public void applyTo(Matrix matrix) {
            for (RowPatch rowPatch : rowPatches) {
                rowPatch.applyTo(matrix);
            }
        }

        public static final class RowPatch {

            public final int rowIndex;
            public final Vector value;

            private RowPatch(int rowIndex, Vector value) {
                this.rowIndex = rowIndex;
                this.value = value;
            }

            private void applyTo(Matrix matrix) {
                matrix.setRow(rowIndex, value);
            }

        }

    }


}
