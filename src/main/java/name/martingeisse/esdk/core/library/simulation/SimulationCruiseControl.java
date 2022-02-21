package name.martingeisse.esdk.core.library.simulation;

/**
 *
 */
public class SimulationCruiseControl extends SimulationDesignItem {

	private final long simulationPeriodTicks;
	private final long realTimePeriodMilliseconds;
	private long lastTime;

	public SimulationCruiseControl(long simulationPeriodTicks, long realTimePeriodMilliseconds) {
		this.simulationPeriodTicks = simulationPeriodTicks;
		this.realTimePeriodMilliseconds = realTimePeriodMilliseconds;
	}

	@Override
	protected void initializeSimulation() {
		lastTime = System.currentTimeMillis();
		fire(this::callback, 0);
	}

	private void callback() {
		long now = System.currentTimeMillis();
		long elapsedDelta = now - lastTime;
		long pendingDelta = realTimePeriodMilliseconds - elapsedDelta;
		if (pendingDelta > 0) {
			try {
				Thread.sleep(pendingDelta);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		lastTime = now;
		fire(this::callback, simulationPeriodTicks);
	}

}
