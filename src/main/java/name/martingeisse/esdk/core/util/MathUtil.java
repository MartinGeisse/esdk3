package name.martingeisse.esdk.core.util;

public final class MathUtil {

    /**
     * Expects value to be non-negative. Returns the number of bits needed to represent its value.
     */
    public static int bitsNeededFor(int value) {
        return 32 - Integer.numberOfLeadingZeros(value);
    }

    /**
     * Expects value to be non-negative. Returns the number of bits needed to represent its value.
     */
    public static int bitsNeededFor(long value) {
        return 64 - Long.numberOfLeadingZeros(value);
    }

}
