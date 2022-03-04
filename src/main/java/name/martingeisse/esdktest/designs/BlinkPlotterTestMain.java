package name.martingeisse.esdktest.designs;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdk.core.library.simulation.ClockGenerator;
import name.martingeisse.esdk.core.library.simulation.SimulationTimeLimit;
import name.martingeisse.esdk.plot.builder.BitSignalVariablePlotSource;
import name.martingeisse.esdk.plot.builder.ClockedPlotter;
import name.martingeisse.esdk.plot.builder.VectorSignalVariablePlotSource;
import name.martingeisse.esdk.plot.render.HtmlRenderer;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabDesign;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class BlinkPlotterTestMain extends Component {

    public static void main(String[] args) throws Exception {
        OrangeCrabDesign design = new OrangeCrabDesign();

        BlinkPlotterTestMain blinkMain = new BlinkPlotterTestMain();
        Clock clock = new Clock(design.createClockPin());
        new ClockGenerator(clock, 10);
        new SimulationTimeLimit(1000);

        blinkMain.clock.connect(clock);

        ClockedPlotter plotter = new ClockedPlotter(clock,
            new VectorSignalVariablePlotSource("counter", blinkMain.counter),
            new BitSignalVariablePlotSource("led", blinkMain.led)
        );

        design.simulate();

        File outputFolder = new File("output");
        if (!outputFolder.exists() && !outputFolder.mkdir()) {
            throw new IOException("could not create output folder");
        }
        File outputFile = new File(outputFolder, "blink.html");
        try (FileOutputStream outStream = new FileOutputStream(outputFile)) {
            try (OutputStreamWriter writer = new OutputStreamWriter(outStream, StandardCharsets.UTF_8)) {
                try (PrintWriter printWriter = new PrintWriter(writer)) {
                    new HtmlRenderer(printWriter).renderStandalone(plotter.buildPlot());
                }
            }
        }
    }

    public final ClockConnector clock = inClock();
    public final BitSignal led;

    public final VectorSignal counter;

    public BlinkPlotterTestMain() {
        var counter = vectorRegister(4, 0);
        on(clock, () -> inc(counter));
        led = select(counter, 3);

        this.counter = counter;
    }

}
