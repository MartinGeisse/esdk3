package name.martingeisse.esdktest.designs;

import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.VectorConstant;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdk.core.library.simulation.ClockGenerator;
import name.martingeisse.esdktest.board.lattice.Ecp5IoType;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabDesign;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabIoPins;
import name.martingeisse.esdktest.designs.components.Blinker;
import name.martingeisse.esdktest.designs.components.character.CharacterDisplay;
import name.martingeisse.esdktest.designs.components.SimplePll;
import name.martingeisse.esdktest.designs.components.bus.BusMasterInterface;
import name.martingeisse.esdktest.designs.components.bus.SingleSlaveDirectConnection;
import name.martingeisse.esdktest.designs.components.character.SimulationCharacterDisplay;
import name.martingeisse.esdktest.designs.components.character.SimulationCharacterDisplayPanel;
import name.martingeisse.esdktest.designs.components.vga.Monitor;
import name.martingeisse.esdktest.designs.components.vga.MonitorPanel;

import java.io.File;

@SuppressWarnings("RedundantThrows")
public class BusHelloWorldMain extends Component {

    public static void main(String[] args) throws Exception {
        // highlevelSimulationMain(args);
        // lowlevelSimulationMain(args);
        synthesisMain(args);
    }

    public static void highlevelSimulationMain(String[] args) {

        //
        // design and toplevel components
        //

        Design design = new Design();
        Clock clock = new Clock(new BitConstant(false));
        new ClockGenerator(clock, 40);

        SimulationCharacterDisplay characterDisplay = new SimulationCharacterDisplay();
        characterDisplay.clock.connect(clock);

        Writer writer = new Writer();
        writer.clock.connect(clock);

        SingleSlaveDirectConnection.build(writer.bus, characterDisplay.bus);

        //
        // simulation window
        //

        SimulationCharacterDisplayPanel displayPanel = new SimulationCharacterDisplayPanel(characterDisplay);
        SimulationCharacterDisplayPanel.openWindow(displayPanel, "BusHelloWorld");

        //
        // simulate this design!
        //

        design.simulate();
    }

    public static void lowlevelSimulationMain(String[] args) {

        //
        // design and toplevel components
        //

        Design design = new Design();
        Clock clock = new Clock(new BitConstant(false));
        new ClockGenerator(clock, 40);

        CharacterDisplay characterDisplay = new CharacterDisplay();
        characterDisplay.clock.connect(clock);

        Writer writer = new Writer();
        writer.clock.connect(clock);

        SingleSlaveDirectConnection.build(writer.bus, characterDisplay.bus);

        //
        // simulation window
        //

        Monitor monitor = new Monitor();
        monitor.clock.connect(clock);
        monitor.r.connect(characterDisplay.r.concat(new VectorConstant(5, 0)));
        monitor.g.connect(characterDisplay.g.concat(new VectorConstant(5, 0)));
        monitor.b.connect(characterDisplay.b.concat(new VectorConstant(5, 0)));
        monitor.hsync.connect(characterDisplay.hsync);
        monitor.vsync.connect(characterDisplay.vsync);

        MonitorPanel monitorPanel = new MonitorPanel(monitor, 800, 525, 1);
        MonitorPanel.openWindow(monitorPanel, "BusHelloWorld");

        //
        // simulate this design!
        //

        design.simulate();
    }

    public static void synthesisMain(String[] args) throws Exception {

        //
        // design and toplevel components
        //

        OrangeCrabDesign design = new OrangeCrabDesign();
        ClockSignal clock = SimplePll.create(design.createClockPin(), 2, 24, 25);

        Blinker blinker = new Blinker();
        blinker.clock.connect(clock);

        CharacterDisplay characterDisplay = new CharacterDisplay();
        characterDisplay.clock.connect(clock);

        Writer writer = new Writer();
        writer.clock.connect(clock);

        SingleSlaveDirectConnection.build(writer.bus, characterDisplay.bus);

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
