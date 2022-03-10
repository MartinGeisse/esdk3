package name.martingeisse.esdk.plot.variable;

public abstract class VariablePlotDescriptor {

    public final String name;

    private VariablePlotDescriptor(String name) {
        this.name = name;
    }

    public abstract void validateSample(Object sample);

    protected final void checkValid(Object sample, boolean valid) {
        if (!valid) {
            invalidSample(sample);
        }
    }

    protected final void invalidSample(Object o) {
        throw new IllegalArgumentException("invalid sample for " + this + ": " + o);
    }

    public static class Bit extends VariablePlotDescriptor {

        public Bit(String name) {
            super(name);
        }

        @Override
        public void validateSample(Object sample) {
            checkValid(sample, sample instanceof Boolean);
        }
    }

    public static class Vector extends VariablePlotDescriptor {

        public final int width;
        public final VectorFormat format;

        public Vector(String name, int width) {
            this(name, width, null);
        }

        public Vector(String name, int width, VectorFormat format) {
            super(name);
            this.width = width;
            this.format = format == null ? NumberFormat.HEXADECIMAL_PADDED : format;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public void validateSample(Object sample) {
            checkValid(sample, sample instanceof name.martingeisse.esdk.core.util.vector.Vector);
            name.martingeisse.esdk.core.util.vector.Vector vectorSample = (name.martingeisse.esdk.core.util.vector.Vector)sample;
            checkValid(sample, vectorSample.getWidth() == this.width);
        }

    }

    public static class Memory extends VariablePlotDescriptor {

        public final int rowCount, columnCount;
        public final VectorFormat rowFormat;

        public Memory(String name, int rowCount, int columnCount) {
            this(name, rowCount, columnCount, null);
        }

        public Memory(String name, int rowCount, int columnCount, VectorFormat rowFormat) {
            super(name);
            this.rowCount = rowCount;
            this.columnCount = columnCount;
            this.rowFormat = rowFormat == null ? NumberFormat.HEXADECIMAL_PADDED : rowFormat;
        }

        @Override
        public void validateSample(Object sample) {
            checkValid(sample, sample instanceof MemorySample);
        }

    }

}
