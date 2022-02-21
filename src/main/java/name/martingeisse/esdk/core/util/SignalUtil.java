package name.martingeisse.esdk.core.util;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;

public final class SignalUtil {

    private SignalUtil() {
    }

    public static BitSignal[] getAllBits(VectorSignal vectorSignal) {
        BitSignal[] result = new BitSignal[vectorSignal.getWidth()];
        for (int i = 0; i < result.length; i++) {
            result[i] = vectorSignal.select(i);
        }
        return result;
    }

}
