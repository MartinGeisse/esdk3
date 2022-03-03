package name.martingeisse.esdk.plot.plotter;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.plot.ValuePlotDescriptor;

public final class BitSignalValuePlotter implements ValuePlotter {

    private final String name;
    private final BitSignal signal;

    public BitSignalValuePlotter(String name, BitSignal signal) {
        this.name = name;
        this.signal = signal;
    }

    @Override
    public ValuePlotDescriptor buildDescriptor() {
        return new ValuePlotDescriptor.Bit(name);
    }

    @Override
    public Object buildUpdate() {
        return signal.getValue();
    }

}
