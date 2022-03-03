package name.martingeisse.esdk.plot;

public abstract class ValuePlotDescriptor {

    public final String name;

    private ValuePlotDescriptor(String name) {
        this.name = name;
    }

    public abstract void validateUpdate(Object update);

    protected final void checkValid(Object update, boolean valid) {
        if (!valid) {
            invalidUpdate(update);
        }
    }

    protected final void invalidUpdate(Object o) {
        throw new IllegalArgumentException("invalid update for " + this + ": " + o);
    }

    public static class Bit extends ValuePlotDescriptor {

        public Bit(String name) {
            super(name);
        }

        @Override
        public void validateUpdate(Object update) {
            checkValid(update, update instanceof Boolean);
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
        public void validateUpdate(Object update) {
            checkValid(update, update instanceof name.martingeisse.esdk.core.util.vector.Vector);
            name.martingeisse.esdk.core.util.vector.Vector vectorUpdate = (name.martingeisse.esdk.core.util.vector.Vector)update;
            checkValid(update, vectorUpdate.getWidth() == this.width);
        }

    }

}
