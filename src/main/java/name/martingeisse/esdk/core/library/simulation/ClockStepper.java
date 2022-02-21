package name.martingeisse.esdk.core.library.simulation;

import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.BitConstant;

/**
 * Allows to single-step (clock cycle-wise) through an RTL design with a single clock.
 * <p>
 * To make this class work correctly, no other code should drive the same clock network or start or stop simulation.
 */
public class ClockStepper extends SimulationDesignItem {

	private final Clock clock;
	private final int clockPeriod;

	public ClockStepper(int clockPeriod) {
		this(new Clock(new BitConstant(false)), clockPeriod);
	}

	public ClockStepper(Clock clock, int clockPeriod) {
		this.clock = clock;
		this.clockPeriod = clockPeriod;
	}

	public Clock getClock() {
		return clock;
	}

	public int getClockPeriod() {
		return clockPeriod;
	}

	public void step() {
		step(1);
	}

	public void step(int cycles) {
		stepInternal(cycles);
		getDesign().continueSimulation();
	}

	private void stepInternal(int cycles) {
		if (cycles < 1) {
			getDesign().stopSimulation();
		} else {
			fire(() -> {
				clock.simulateClockEdge();
				stepInternal(cycles - 1);
			}, clockPeriod);
		}
	}

}
