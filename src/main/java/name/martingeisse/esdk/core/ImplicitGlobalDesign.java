package name.martingeisse.esdk.core;

/**
 * This class acts as a holder for a single per-thread {@link Design} instance that is implicitly used for creating
 * new design items, so it does not have to be "passed around". The reasoning behind this class is that most programs
 * will only use a single design instance. Similar to creating new items, some helper methods, such as for finding
 * items, will use the implicit global design.
 *
 * Programs that want to deal with multiple designs must do so either in different threads, or one after the other.
 * Note that since there are helper methods that use the implicit design, just *creating* the designs one after the
 * other is not enough, even though each item knows to which design it belongs.
 */
public final class ImplicitGlobalDesign {

    private ImplicitGlobalDesign() {
    }

    private static final ThreadLocal<Design> holder = new ThreadLocal<>();

    public static void set(Design design) {
        holder.set(design);
    }

    public static Design get() {
        return holder.get();
    }

    public static Design getOrFail() {
        Design design = get();
        if (design == null) {
            throw new IllegalStateException("no implicit global design found");
        }
        return design;
    }

}
