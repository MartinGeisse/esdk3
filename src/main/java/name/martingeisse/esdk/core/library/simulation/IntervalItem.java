package name.martingeisse.esdk.core.library.simulation;

/**
 * Executes some code periodically in simulated time.
 */
public class IntervalItem extends SimulationDesignItem {

    private final long period;
    private final long initialOffset;
    private final Runnable action;

    public IntervalItem(long period, Runnable action) {
        this(period, 0, action);
    }

    public IntervalItem(long period, long initialOffset, Runnable action) {
        this.period = period;
        this.initialOffset = initialOffset;
        this.action = action;
    }

    public long getPeriod() {
        return period;
    }

    public long getInitialOffset() {
        return initialOffset;
    }

    public Runnable getAction() {
        return action;
    }

    @Override
    protected void initializeSimulation() {
        fire(this::callback, initialOffset);
    }

    private void callback() {
        action.run();
        fire(this::callback, period);
    }

}
