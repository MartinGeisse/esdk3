package name.martingeisse.esdktest.designs.components.bus;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.signal.*;
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

    /**
     * This method ensures that the component which provides a bus master interface has correctly constructed it.
     * It does NOT validate whether all signals are connected to a bus.
     */
    public void validateConstructedCorrectly() {
        checkMissing(enable, "enable");
        checkMissing(write, "write");
        checkMissing(wordAddress, "wordAddress");
        checkMissing(writeData, "writeData");
        checkMissing(writeMask, "writeMask");
    }

    private void checkMissing(Signal signal, String what) {
        if (signal == null) {
            throw new RuntimeException(getClass().getSimpleName() + " was not constructed correcly -- missing " + what);
        }
    }

    /**
     * Connects all input ports to a no-op pseudo-bus so that this master doesn't act up.
     */
    public void deactivate() {
        acknowledge.connect(new BitConstant(true));
        readData.connect(new VectorConstant(32, 0));
    }

}
