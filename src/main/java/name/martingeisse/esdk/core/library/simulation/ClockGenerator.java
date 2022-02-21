package name.martingeisse.esdk.core.library.simulation;

import name.martingeisse.esdk.core.library.clocked.Clock;

/**
 *
 */
public final class ClockGenerator extends IntervalItem {

    public ClockGenerator(Clock clock, long period) {
        super(period, clock::simulateClockEdge);
    }

    public ClockGenerator(Clock clock, long period, long initialOffset) {
        super(period, initialOffset, clock::simulateClockEdge);
    }

}
