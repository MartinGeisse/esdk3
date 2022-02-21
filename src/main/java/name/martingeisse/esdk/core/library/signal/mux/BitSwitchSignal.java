package name.martingeisse.esdk.core.library.signal.mux;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;

/**
 *
 */
public final class BitSwitchSignal extends SwitchSignal<BitSignal> implements BitSignal {

	public BitSwitchSignal(VectorSignal selector) {
		super(selector);
	}

	@Override
	protected void validateOnAdd(BitSignal branch) {
	}

	@Override
	public boolean getValue() {
		return getCurrentlySelectedBranch().getValue();
	}

}
