package name.martingeisse.esdktest.designs;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdktest.board.lattice.Ecp5IoType;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabDesign;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabIoPins;
import name.martingeisse.esdktest.designs.components.Blinker;
import name.martingeisse.esdktest.designs.components.character.CharacterDisplay;
import name.martingeisse.esdktest.designs.components.SimplePll;

import java.io.File;

public class CharacterDisplayHelloWorldMain extends Component {

    public static void main(String[] args) throws Exception {
        OrangeCrabDesign design = new OrangeCrabDesign();
        ClockSignal clock = SimplePll.create(design.createClockPin(), 2, 24, 25);

        Blinker blinker = new Blinker();
        blinker.clock.connect(clock);

        CharacterDisplay characterDisplay = new CharacterDisplay();
        characterDisplay.clock.connect(clock);
        characterDisplay.bus.deactivate();

        design.createLedPins(blinker.r, blinker.g, blinker.b);
        design.createReconfigurePin(design.createPushbuttonPin());

        design.createOutputPin(OrangeCrabIoPins.IO_13, characterDisplay.r.select(2), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_12, characterDisplay.r.select(1), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_11, characterDisplay.r.select(0), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_10, characterDisplay.g.select(2), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_9, characterDisplay.g.select(1), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_6, characterDisplay.g.select(0), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_5, characterDisplay.b.select(2), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.SCL, characterDisplay.b.select(1), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.SDA, characterDisplay.b.select(0), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.MOSI, characterDisplay.hsync, Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.MISO, characterDisplay.vsync, Ecp5IoType.LVCMOS33, null);

        design.implement("CharacterDisplayHelloWorld", new File("implement/CharacterDisplayHelloWorld"), true);
    }

}
