package name.martingeisse.esdk.plot.builder;

import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.plot.VariablePlotDescriptor;

public final class BitSignalVariablePlotSource implements VariablePlotSource {

    private final String name;
    private final BitSignal signal;

    public BitSignalVariablePlotSource(String name, BitSignal signal) {
        this.name = name;
        this.signal = signal;
    }

    @Override
    public VariablePlotDescriptor buildDescriptor() {
        return new VariablePlotDescriptor.Bit(name);
    }

    @Override
    public Object buildSample() {
        return signal.getValue();
    }

}
