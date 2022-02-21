package name.martingeisse.esdktest.designs.components.bus;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.BitConnector;
import name.martingeisse.esdk.core.library.signal.connector.VectorConnector;

public final class BusMasterInterface {

    public BitSignal enable;
    public BitSignal write;
    public VectorSignal wordAddress;
    public VectorSignal writeData;
    public VectorSignal writeMask;

    public final BitConnector acknowledge;
    public final VectorConnector readData;

    public BusMasterInterface(Component busMaster) {
        acknowledge = busMaster.inBit();
        readData = busMaster.inVector(32);
    }

}
