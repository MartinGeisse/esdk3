package name.martingeisse.esdk.plot.builder;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.plot.ValuePlotDescriptor;

public final class BitSignalValuePlotSource implements ValuePlotSource {

    private final String name;
    private final BitSignal signal;

    public BitSignalValuePlotSource(String name, BitSignal signal) {
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
