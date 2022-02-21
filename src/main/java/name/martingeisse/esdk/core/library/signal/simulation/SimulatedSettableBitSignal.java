package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.signal.BitSignal;

/**
 * Bit version of {@link SimulatedSettableSignal}.
 */
public final class SimulatedSettableBitSignal extends SimulatedSettableSignal implements BitSignal {

	private boolean value;

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

}
