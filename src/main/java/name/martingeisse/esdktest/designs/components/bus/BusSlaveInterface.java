package name.martingeisse.esdktest.designs.components.bus;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorConstant;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.BitConnector;
import name.martingeisse.esdk.core.library.signal.connector.VectorConnector;

public final class BusSlaveInterface {

    public final BitConnector enable;
    public final BitConnector write;
    public final VectorConnector wordAddress;
    public final VectorConnector writeData;
    public final VectorConnector writeMask;

    public BitSignal acknowledge;
    public VectorSignal readData;

    public BusSlaveInterface(Component busSlave, int localAddressBits, int writeDataBits) {
        enable = busSlave.inBit();
        write = busSlave.inBit();
        wordAddress = busSlave.inVector(localAddressBits);
        writeData = busSlave.inVector(writeDataBits);
        writeMask = busSlave.inVector(4);
    }

    /**
     * Connects all input ports to a no-op pseudo-bus so that this slave doesn't act up.
     */
    public void deactivate() {
        enable.connect(new BitConstant(false));
        write.connect(new BitConstant(false));
        wordAddress.connect(new VectorConstant(wordAddress.getWidth(), 0));
        writeData.connect(new VectorConstant(writeData.getWidth(), 0));
        writeMask.connect(new VectorConstant(4, 0));
    }

}
