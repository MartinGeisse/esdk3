package name.martingeisse.esdktest.designs;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdktest.board.lattice.Ecp5IoType;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabDesign;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabIoPins;
import name.martingeisse.esdktest.designs.components.Blinker;
import name.martingeisse.esdktest.designs.components.CharacterDisplay;
import name.martingeisse.esdktest.designs.components.SimplePll;
import name.martingeisse.esdktest.designs.components.bus.BusMasterInterface;

import java.io.File;

public class BusHelloWorldMain extends Component {

    public static void main(String[] args) throws Exception {
        OrangeCrabDesign design = new OrangeCrabDesign();
        ClockSignal clock = SimplePll.create(design.createClockPin(), 2, 24, 25);

        //
        // toplevel components
        //

        Blinker blinker = new Blinker();
        blinker.clock.connect(clock);

        CharacterDisplay characterDisplay = new CharacterDisplay();
        characterDisplay.clock.connect(clock);

        Writer writer = new Writer();



        //
        // pins
        //

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

        //
        // implement this design!
        //

        design.implement("CharacterDisplayHelloWorld", new File("implement/CharacterDisplayHelloWorld"), true);
    }

    public static class Writer extends Component {

        public final ClockConnector clock = inClock();
        public final BusMasterInterface bus = new BusMasterInterface(this);

        public Writer() {
            var counter = vectorRegister(24, 0);
            var wordAddress = vectorRegister(30, 0);
            var write = bitRegister(false);
            on(clock, () -> {

                // this is actually a race condition because it will screw up when the write cycle
                // takes longer than a full turnover of the delay counter, 2^24 clock cycles, but
                // we just assume that doesn't happen.
                when(and(write, bus.acknowledge), () -> set(write, false));

                when(eq(counter, 0), () -> {
                    set(write, true);
                    inc(wordAddress);
                });
                inc(counter);
            });

            bus.enable = write;
            bus.write = constant(true);
            bus.wordAddress = wordAddress;
            bus.writeData = constant(32, '.');
            bus.writeMask = constant(4, 15);
        }
    }

}
