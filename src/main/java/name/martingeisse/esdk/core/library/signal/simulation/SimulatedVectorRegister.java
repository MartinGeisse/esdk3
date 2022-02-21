package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * Vector version of {@link SimulatedRegister}.
 */
public final class SimulatedVectorRegister extends SimulatedRegister implements VectorSignal {

    private final int width;
    private Vector value;
    private Vector nextValue;

    public SimulatedVectorRegister(ClockSignal clockSignal, int width) {
        super(clockSignal);
        this.width = width;
        this.value = this.nextValue = Vector.of(width, 0);
    }

    @Override
    public int getWidth() {
        return width;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // simulation
    // ----------------------------------------------------------------------------------------------------------------

    @Override
    public Vector getValue() {
        return value;
    }

    public Vector getNextValue() {
        return nextValue;
    }

    public void setNextValue(Vector nextValue) {
        if (nextValue.getWidth() != width) {
            throw new IllegalArgumentException("got vector value of wrong width " + value.getWidth() + ", expected " + width);
        }
        this.nextValue = nextValue;
    }

    @Override
    public void computeNextState() {
        // must be manually set from the outside
    }

    @Override
    public void updateState() {
        this.value = nextValue;
    }

}
