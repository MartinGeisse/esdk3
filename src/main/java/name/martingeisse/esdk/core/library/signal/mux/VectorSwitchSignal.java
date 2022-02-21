package name.martingeisse.esdk.core.library.signal.mux;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public final class VectorSwitchSignal extends SwitchSignal<VectorSignal> implements VectorSignal {

	private final int width;

	public VectorSwitchSignal(VectorSignal selector, int width) {
		super(selector);
		this.width = width;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	protected void validateOnAdd(VectorSignal branch) {
		if (branch.getWidth() != width) {
			throw new IllegalArgumentException("switch statement width is " + width + ", but branch width is " + branch.getWidth());
		}
	}

	@Override
	public Vector getValue() {
		return getCurrentlySelectedBranch().getValue();
	}

}
