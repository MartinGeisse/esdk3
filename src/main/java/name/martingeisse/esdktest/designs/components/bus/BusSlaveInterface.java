package name.martingeisse.esdktest.designs.components.bus;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.signal.*;
import name.martingeisse.esdk.core.library.signal.connector.BitConnector;
import name.martingeisse.esdk.core.library.signal.connector.VectorConnector;
import name.martingeisse.esdk.plot.builder.ClockedPlotter;
import name.martingeisse.esdk.plot.builder.Plottable;

public final class BusSlaveInterface implements Plottable {

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
     * This method ensures that the component which provides a bus master interface has correctly constructed it.
     * It does NOT validate whether all signals are connected to a bus.
     */
    public void validateConstructedCorrectly() {
        checkMissing(acknowledge, "acknowledge");
        checkMissing(readData, "readData");
    }

    private void checkMissing(Signal signal, String what) {
        if (signal == null) {
            throw new RuntimeException(getClass().getSimpleName() + " was not constructed correcly -- missing " + what);
        }
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

    public void addSources(ClockedPlotter plotter, String name) {
        plotter.addSource(name + ".enable", enable);
        plotter.addSource(name + ".write", write);
        plotter.addSource(name + ".acknowledge", acknowledge);
        plotter.addSource(name + ".wordAddress", wordAddress);
        plotter.addSource(name + ".writeData", writeData);
        plotter.addSource(name + ".writeMask", writeMask);
        plotter.addSource(name + ".readData", readData);
    }

}
