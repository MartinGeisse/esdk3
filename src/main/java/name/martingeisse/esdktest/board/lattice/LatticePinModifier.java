package name.martingeisse.esdktest.board.lattice;

import name.martingeisse.esdk.core.tools.synthesis.lattice.LatticePinConfiguration;

/**
 * Allows a more convenient specification of the pin configuration.
 */
public interface LatticePinModifier {

    /**
     * Changes the specified configuration according to this modifier.
     */
    void apply(LatticePinConfiguration configuration);

    /**
     * Applies multiple modifiers.
     */
    static void apply(LatticePinConfiguration configuration, LatticePinModifier... modifiers) {
        for (LatticePinModifier modifier: modifiers) {
            modifier.apply(configuration);
        }
    }

    /**
     * The expected frequency for a clock pin.
     */
    static LatticePinModifier clockFrequencyMhz(int frequencyMhz) {
        return configuration -> configuration.setFrequency(frequencyMhz + ".0 MHz");
    }

    /**
     * The slew rate for an output pin.
     */
    static LatticePinModifier slewRate(Ecp5SlewRate slewRate) {
        return configuration -> configuration.set("SLEWRATE", slewRate.name());
    }

    /**
     * The I/O type, in particular the expected and generated voltage levels.
     */
    static LatticePinModifier ioType(Ecp5IoType ioType) {
        return configuration -> configuration.set("IO_TYPE", ioType.name());
    }

    LatticePinModifier pullUp = configuration -> configuration.set("PULLMODE", "UP");
    LatticePinModifier pullDown = configuration -> configuration.set("PULLMODE", "DOWN");

}
