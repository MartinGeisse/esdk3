package name.martingeisse.esdk.core.library.simulation;

public final class SimulationTimeLimit extends SimulationDesignItem {

    private final long ticks;

    public SimulationTimeLimit(long ticks) {
        this.ticks = ticks;
    }

    @Override
    protected void initializeSimulation() {
        fire(() -> getDesign().stopSimulation(), ticks);
    }

}
