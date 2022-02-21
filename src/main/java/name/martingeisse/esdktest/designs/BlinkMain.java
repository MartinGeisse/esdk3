package name.martingeisse.esdktest.designs;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.connector.BitConnector;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdktest.board.orange_crab.OrangeCrabDesign;

import java.io.File;

public class BlinkMain extends Component {

    public static void main(String[] args) throws Exception {
        OrangeCrabDesign design = new OrangeCrabDesign();

        BlinkMain blinkMain = new BlinkMain();
        Clock clock = new Clock(design.createClockPin());
        blinkMain.clock.connect(clock);
        blinkMain.button.connect(design.createPushbuttonPin());
        design.createLedRedPin(blinkMain.r);
        design.createLedGreenPin(blinkMain.g);
        design.createLedBluePin(blinkMain.b);

        design.implement("ColorBlink", new File("implement/ColorBlink"), true);
    }

    public final ClockConnector clock = inClock();
    public final BitConnector button = inBit();
    public final BitSignal r;
    public final BitSignal g;
    public final BitSignal b;

    public BlinkMain() {
        var counter = vectorRegister(24, 0);
        on(clock, () -> inc(counter));
        var light = select(counter, 23);
        r = and(light, button);
        g = constant(false);
        b = light;
    }

}
