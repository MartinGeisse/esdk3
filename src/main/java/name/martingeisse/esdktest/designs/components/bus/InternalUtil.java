package name.martingeisse.esdktest.designs.components.bus;

import name.martingeisse.esdk.core.library.signal.VectorConstant;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.VectorConnector;

class InternalUtil {

    static void connectLowerBits(VectorConnector connector, VectorSignal source) {
        if (connector.getWidth() == source.getWidth()) {
            connector.connect(source);
        } else {
            connector.connect(source.select(connector.getWidth() - 1, 0));
        }
    }

    static VectorSignal zeroExtend32(VectorSignal signal) {
        if (signal.getWidth() == 32) {
            return signal;
        } else {
            return new VectorConstant(32 - signal.getWidth(), 0).concat(signal);
        }
    }

}
