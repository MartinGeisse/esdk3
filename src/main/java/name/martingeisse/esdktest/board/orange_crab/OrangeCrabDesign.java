package name.martingeisse.esdktest.board.orange_crab;

import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.tools.synthesis.lattice.ProjectGenerator;
import name.martingeisse.esdktest.board.lattice.Ecp5IoType;
import name.martingeisse.esdktest.board.lattice.LatticeEcp5Design;
import name.martingeisse.esdktest.board.lattice.LatticePinModifier;

import java.io.File;
import java.io.IOException;

/**
 * Convenience class to use instead of {@link Design}. It gives you easy access to OC-specific pins, board features,
 * and so on.
 *
 * Getting an input pin for the first time generates it. If the subsequent toolchain complains about declared but
 * unused pins, chances are the getter is called somewhere but the pin isn't used. TODO: ESDK should be able
 * to remove unused pins itself -- but then, it is unclear whether a warning isn't actually useful there. Maybe
 * allow to mark a pin as "unused is okay".
 *
 * The RAM pins are not yet supported since it is not yet clear to me in which way they should be supported at
 * this point, being a convenient helper while at the same time not constraining the caller too much.
 *
 */
public class OrangeCrabDesign extends LatticeEcp5Design {

    public BitSignal createClockPin() {
        return createInputPin("A9", Ecp5IoType.LVCMOS33, LatticePinModifier.clockFrequencyMhz(48));
    }

    /**
     * The argument signal is automatically inverted to adjust for the inverting circuit on the board.
     */
    public void createLedRedPin(BitSignal ledRed) {
        createOutputPin("K4", ledRed.not(), Ecp5IoType.LVCMOS33, null);
    }

    /**
     * The argument signal is automatically inverted to adjust for the inverting circuit on the board.
     */
    public void createLedGreenPin(BitSignal ledGreen) {
        createOutputPin("M3", ledGreen.not(), Ecp5IoType.LVCMOS33, null);
    }

    /**
     * The argument signal is automatically inverted to adjust for the inverting circuit on the board.
     */
    public void createLedBluePin(BitSignal ledBlue) {
        createOutputPin("J3", ledBlue.not(), Ecp5IoType.LVCMOS33, null);
    }

    /**
     * Shortcut for the three individual LED pin methods.
     * The argument signals are automatically inverted to adjust for the inverting circuit on the board.
     */
    public void createLedPins(BitSignal red, BitSignal green, BitSignal blue) {
        createLedRedPin(red);
        createLedGreenPin(green);
        createLedBluePin(blue);
    }

    /**
     * The returned signal is automatically inverted to adjust for the inverting circuit on the board.
     */
    public BitSignal createPushbuttonPin() {
        return createInputPin("J17", Ecp5IoType.SSTL135_I).not();
    }

    /**
     * Sending TRUE through this signal starts FPGA reconfiguration.
     */
    public void createReconfigurePin(BitSignal reconfigureSignal) {
        // this pin has an internal pull-up. I'm note sure if we can just drive it TRUE when inactive.
        // The way it is done here is something I saw in another design, and it worked.
        createInOutPin("V17", new BitConstant(false), reconfigureSignal, Ecp5IoType.LVCMOS33, null);
    }

    /**
     * Like {@link #implement(String, File, int, boolean)}, but within a specific random seed.
     */
    public void implement(String toplevelName, File buildFolder, boolean program) throws IOException, InterruptedException {
        implement(toplevelName, buildFolder, 0, program);
    }

    /**
     * Implements the design.
     *
     * This method is provided for convenience to get simple projects started. It is based on the assumption that no
     * custom Verilog files, no custom lines in the constraint file and no --bootaddr for ecppack are needed.
     *
     * @param toplevelName the name of the toplevel Verilog module
     * @param buildFolder the output folder. Note that this folder will be deleted and re-created!
     * @param nextpnrSeed a random seed for the PNR stage. Adjust this is the PNR barely fails -- especially fails to
     *                    meet timing constraints -- and there is hope that randomly starting in a different way will
     *                    fix the problem.
     * @param program whether the design should also be programmed to the FPGA
     */
    public void implement(String toplevelName, File buildFolder, int nextpnrSeed, boolean program) throws IOException, InterruptedException {
        ProjectGenerator projectGenerator = new ProjectGenerator(toplevelName, buildFolder, "CSFBGA285");
        projectGenerator.setNextpnrSeed(nextpnrSeed);
        projectGenerator.clean();
        projectGenerator.generate();
        projectGenerator.build();
        if (program) {
            projectGenerator.program();
        }
    }

}
