package name.martingeisse.esdktest.board.orange_crab;

/**
 * Maps the board IO labels to FPGA pin IDs.
 *
 * Not yet implemented:
 * - SDO_* pins (MicroSD slot)
 * - USB_* (USB-related pins)
 * - SPI_CONFIG_*, QSPI_* (flash)
 *
 * The "EN" pin is not connected to the FPGA. Seems like it can be used for a power switch.
 * The SPI0..3 (these signals appear to "vanish" in the board schematic)
 * The following pins are only connected to test points: EXT_PLL(+/-).
 */
public final class OrangeCrabIoPins {

    private OrangeCrabIoPins() {
    }

    // generic
    public static final String IO_0 = "N17";
    public static final String IO_1 = "M18";
    public static final String IO_5 = "B10";
    public static final String IO_6 = "B9";
    public static final String IO_9 = "C8";
    public static final String IO_10 = "B8";
    public static final String IO_11 = "A8";
    public static final String IO_12 = "H2";
    public static final String IO_13 = "J2";

    // I2C
    public static final String SCL = "C9";
    public static final String SDA = "C10";

    // SPI
    public static final String SCK = "R17";
    public static final String MOSI = "N16";
    public static final String MISO = "N15";

    // analog
    public static final String A_0 = "L4";
    public static final String A_1 = "N3";
    public static final String A_2 = "N4";
    public static final String A_3 = "H4";
    public static final String A_4 = "G4";
    public static final String A_5 = "T17";
    public static final String ADC_MUX0 = "F4";
    public static final String ADC_MUX1 = "F3";
    public static final String ADC_MUX2 = "F2";
    public static final String ADC_MUX3 = "H1";
    public static final String ADC_CTRL0 = "G1";
    public static final String ADC_CTRL1 = "F1";
    public static final String ADC_SENSE_LO = "G3";
    public static final String ADC_SENSE_HI = "H3";

}
