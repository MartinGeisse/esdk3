package name.martingeisse.esdktest.designs;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.blackbox.BlackboxInstance;
import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdktest.board.lattice.Ecp5IoType;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabDesign;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabIoPins;
import name.martingeisse.esdktest.designs.components.Blinker;
import name.martingeisse.esdktest.designs.components.SimplePll;

import java.io.File;

public class TestbildMain extends Component {

    public static void main(String[] args) throws Exception {
        OrangeCrabDesign design = new OrangeCrabDesign();
        ClockSignal clock = SimplePll.create(design.createClockPin(), 2, 24, 25);

        Blinker blinker = new Blinker();
        blinker.clock.connect(clock);

        TestbildMain testbild = new TestbildMain();
        testbild.clock.connect(clock);

        design.createLedPins(blinker.r, blinker.g, blinker.b);
        design.createReconfigurePin(design.createPushbuttonPin());

        design.createOutputPin(OrangeCrabIoPins.IO_13, testbild.r.select(2), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_12, testbild.r.select(1), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_11, testbild.r.select(0), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_10, testbild.g.select(2), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_9, testbild.g.select(1), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_6, testbild.g.select(0), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.IO_5, testbild.b.select(2), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.SCL, testbild.b.select(1), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.SDA, testbild.b.select(0), Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.MOSI, testbild.hsync, Ecp5IoType.LVCMOS33, null);
        design.createOutputPin(OrangeCrabIoPins.MISO, testbild.vsync, Ecp5IoType.LVCMOS33, null);

        design.implement("Testbild", new File("output/Testbild"), true);
    }

    public final ClockConnector clock = inClock();
    public final VectorSignal r, g, b;
    public final BitSignal hsync, vsync;

    @SuppressWarnings("SuspiciousNameCombination")
    public TestbildMain() {
        var x = vectorRegister(10);
        var hblank = bitRegister();
        var hsync = bitRegister();
        var y = vectorRegister(10);
        var vblank = bitRegister();
        var vsync = bitRegister();
        on(clock, () -> {
            when(eq(x, 799), () -> {
                set(hblank, false);
                set(x, 0);
                when(eq(y, 524), () -> {
                    set(vblank, false);
                    set(y, 0);
                }, () -> {
                    when(eq(y, 479), () -> {
                        set(vblank, true);
                    }, eq(y, 489), () -> {
                        set(vsync, false);
                    }, eq(y, 491), () -> {
                        set(vsync, true);
                    });
                    inc(y, 1);
                });
            }, () -> {
                when(eq(x, 639), () -> {
                    set(hblank, true);
                }, eq(x, 655), () -> {
                    set(hsync, false);
                }, eq(x, 751), () -> {
                    set(hsync, true);
                });
                inc(x, 1);
            });
        });

        var blank = or(hblank, vblank);
        this.r = when(blank, constant(3, 0), x.select(5, 3));
        this.g = when(blank, constant(3, 0), y.select(5, 3));
        this.b = when(blank, constant(3, 0), add(x, y).select(7, 5));
        this.hsync = hsync;
        this.vsync = vsync;
    }

}
