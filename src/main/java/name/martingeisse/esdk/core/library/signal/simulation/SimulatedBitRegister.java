package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.ClockSignal;

/**
 * Bit version of {@link SimulatedRegister}.
 */
public final class SimulatedBitRegister extends SimulatedRegister implements BitSignal {

	private boolean value;
	private boolean nextValue;

	public SimulatedBitRegister(ClockSignal clockSignal) {
		super(clockSignal);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public boolean getValue() {
		return value;
	}

	public boolean getNextValue() {
		return nextValue;
	}

	public void setNextValue(boolean nextValue) {
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
