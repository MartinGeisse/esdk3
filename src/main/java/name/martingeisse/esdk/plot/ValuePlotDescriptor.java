package name.martingeisse.esdk.plot;

public abstract class ValuePlotDescriptor {

    public final String name;

    private ValuePlotDescriptor(String name) {
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

    public static class Bit extends ValuePlotDescriptor {

        public Bit(String name) {
            super(name);
        }

        @Override
        public void validateSample(Object sample) {
            checkValid(sample, sample instanceof Boolean);
        }
    }

    public static class Vector extends ValuePlotDescriptor {

        public final int width;
        public final VectorFormat format;

        public Vector(String name, int width) {
            this(name, width, NumberFormat.DECIMAL);
        }

        public Vector(String name, int width, VectorFormat format) {
            super(name);
            this.width = width;
            this.format = format;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public void validateSample(Object sample) {
            checkValid(sample, sample instanceof name.martingeisse.esdk.core.util.vector.Vector);
            name.martingeisse.esdk.core.util.vector.Vector vectorSample = (name.martingeisse.esdk.core.util.vector.Vector)sample;
            checkValid(sample, vectorSample.getWidth() == this.width);
        }

    }

}
