package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * Vector version of {@link SimulatedSettableSignal}.
 */
public final class SimulatedSettableVectorSignal extends SimulatedSettableSignal implements VectorSignal {

	private final int width;
	private Vector value;

	public SimulatedSettableVectorSignal(int width) {
		this.width = width;
		this.value = Vector.of(width, 0);
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

	public void setValue(Vector value) {
		if (value.getWidth() != width) {
			throw new IllegalArgumentException("got vector value of wrong width " + value.getWidth() + ", expected " + width);
		}
		this.value = value;
	}

}
