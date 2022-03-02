package name.martingeisse.esdktest.designs.components.bus;

import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.simulation.SimulatedComputedVectorSignal;
import name.martingeisse.esdk.core.library.simulation.ClockedSimulationDesignItem;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * This is a simulation-only helper class to implement the bus logic behind
 * {@link BusSlaveInterface} and connect to the remaining simulation code of the
 * device behind that interface.
 * <p>
 * For now, this device responds to all bus requests immediately. This might be extended
 * in the future.
 * <p>
 * Note that due to the bus protocol, obtaining read data must be done separately from
 * read-related side effects, because they happen at different times during the simulation
 * cycle.
 * <p>
 * Using this class:
 * <p>
 * For the simple case of side effect-free reads and self-contained writes,
 * implementing this class means implementing the {@link #getReadData(int)} amd
 * {@link #executeWrite(int, int, int)} methods.
 * <p>
 * If reads should have side effects, {@link #executeRead(int)} must be implemented.
 * Note that the side effect-free signal sampling part and the side effect-causing, non-sampling
 * part must be cleanly separated due to the way simulation works.
 * <p>
 * If writes should implicitly sample other signals than the bus signals, the
 * {@link #prepareWrite(int, int, int)} method must be implemented. Again, just like for
 * reads, the side effect-free signal sampling part and the side effect-causing, non-sampling
 * part must be cleanly separated.
 */
public abstract class SimulationBusSlave extends ClockedSimulationDesignItem {

    private static final Vector ZERO32 = Vector.of(32, 0);

    public final BusSlaveInterface slaveInterface;
    private boolean enable;
    private boolean write;
    private int wordAddress;
    private int writeData;
    private int writeMask;
    private Vector readData;

    public SimulationBusSlave(ClockSignal clockSignal, BusSlaveInterface slaveInterface) {
        super(clockSignal);
        this.slaveInterface = slaveInterface;
        slaveInterface.acknowledge = new BitConstant(true);
        slaveInterface.readData = SimulatedComputedVectorSignal.of(32, () -> readData);
    }

    /**
     * Obtains read data for the specified address. The wordAddress is the same as for executeRead().
     * <p>
     * This method may sample signals but must not cause side effects.
     */
    protected abstract int getReadData(int wordAddress);

    /**
     * Executes side effects related to reading data. The word address is the same as for getReadData().
     * <p>
     * This method may cause side effects but must not sample any signals.
     */
    protected void executeRead(int wordAddress) {
    }

    /**
     * Prepares for writing data. The wordAddress is the same as for executeWrite().
     * <p>
     * This method may sample signals but must not cause side effects.
     */
    protected void prepareWrite(int wordAddress, int data, int writeMask) {
    }

    /**
     * Executes side effects related to writing data. The wordAddress is the same as for prepareWrite().
     * <p>
     * This method may cause side effects but must not sample any signals.
     */
    protected abstract void executeWrite(int wordAddress, int data, int writeMask);

    @Override
    public final void computeNextState() {
        this.enable = slaveInterface.enable.getValue();
        this.write = slaveInterface.write.getValue();

        // fetch as few values as possible, so it's faster
        if (enable) {
            this.wordAddress = slaveInterface.wordAddress.getValue().getAsSignedInt();
            if (write) {
                this.writeData = slaveInterface.writeData.getValue().getAsSignedInt();
                this.writeMask = slaveInterface.writeMask.getValue().getAsSignedInt();
                this.readData = ZERO32;
                prepareWrite(wordAddress, writeData, writeMask);
            } else {
                this.readData = Vector.of(32, getReadData(wordAddress));
            }
        } else {
            this.readData = ZERO32;
        }
    }

    @Override
    public final void updateState() {
        if (enable) {
            if (write) {
                executeWrite(wordAddress, writeData, writeMask);
            } else {
                executeRead(wordAddress);
            }
        }
    }

}
