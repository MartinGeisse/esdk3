package name.martingeisse.esdk.core.library.signal.getter;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * This is a potentially faster getter to be used instead of {@link VectorSignal#getValue()}.
 *
 * See {@link BitSignalGetter} for a detailed explanation.
 */
public abstract class VectorSignalGetter {

    public abstract Vector getValue();

}
