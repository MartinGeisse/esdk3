package name.martingeisse.esdk.core.util;

import java.util.function.Supplier;

/**
 * Simple helper for lazy-initialized values. This class allows created values to be null.
 */
public final class Lazy<T> implements Supplier<T> {

    private final Supplier<T> factory;
    private boolean created;
    private T value;

    public Lazy(Supplier<T> factory) {
        this.factory = factory;
        this.created = false;
        this.value = null;
    }

    @Override
    public T get() {
        if (!created) {
            value = factory.get();
            created = true;
        }
        return value;
    }

}
