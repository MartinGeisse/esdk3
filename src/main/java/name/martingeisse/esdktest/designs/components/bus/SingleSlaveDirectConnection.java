package name.martingeisse.esdktest.designs.components.bus;

/**
 * This class builds a direct connection between a bus master and a single bus slave, without any address decoding
 * happending in the bus itself. This causes the slave to be mirrored across the whole address space of the master.
 */
public final class SingleSlaveDirectConnection {

    private SingleSlaveDirectConnection() {
    }

    /**
     * Builds a direct connection.
     */
    public static void build(BusMasterInterface masterInterface, BusSlaveInterface slaveInterface) {
        masterInterface.validateConstructedCorrectly();
        slaveInterface.validateConstructedCorrectly();
        slaveInterface.enable.connect(masterInterface.enable);
        slaveInterface.write.connect(masterInterface.write);
        InternalUtil.connectLowerBits(slaveInterface.wordAddress, masterInterface.wordAddress);
        InternalUtil.connectLowerBits(slaveInterface.writeData, masterInterface.writeData);
        slaveInterface.writeMask.connect(masterInterface.writeMask);
        masterInterface.acknowledge.connect(slaveInterface.acknowledge);
        masterInterface.readData.connect(InternalUtil.zeroExtend32(slaveInterface.readData));
    }

}
