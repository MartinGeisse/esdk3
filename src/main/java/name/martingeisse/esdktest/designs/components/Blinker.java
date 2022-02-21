package name.martingeisse.esdktest.designs.components;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;

public final class Blinker extends Component {

    public final ClockConnector clock = inClock();
    public final BitSignal r;
    public final BitSignal g;
    public final BitSignal b;

    public Blinker() {
        var counter = vectorRegister(24, 0);
        on(clock, () -> inc(counter));
        r = select(counter, 23);
        g = constant(false);
        b = constant(false);
    }

}
