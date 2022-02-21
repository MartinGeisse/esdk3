package name.martingeisse.esdk.core.library.signal.getter;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.BitConnector;
import name.martingeisse.esdk.core.library.signal.connector.VectorConnector;

/**
 * This factory must not be called until the whole simulation model has been constructed in its final form since the
 * model structure may be encoded into the returned getter.
 */
public class DefaultSignalGetterFactory {

    public static BitSignalGetter getGetter(BitSignal signal) {
        if (signal instanceof BitConnector) {
            return getGetter(((BitConnector) signal).getConnected());
        }
        return GetterGenerator.generate(signal);
    }

    public static VectorSignalGetter getGetter(VectorSignal signal) {
        if (signal instanceof VectorConnector) {
            return getGetter(((VectorConnector) signal).getConnected());
        }
        return GetterGenerator.generate(signal);
    }

}
