package name.martingeisse.esdk.plot.builder;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.plot.variable.VariablePlotDescriptor;
import name.martingeisse.esdk.plot.variable.VectorFormat;

public final class VectorSignalVariablePlotSource implements VariablePlotSource {

    private final String name;
    private final VectorSignal signal;
    private final VectorFormat format;

    public VectorSignalVariablePlotSource(String name, VectorSignal signal) {
        this(name, signal, null);
    }

    public VectorSignalVariablePlotSource(String name, VectorSignal signal, VectorFormat format) {
        this.name = name;
        this.signal = signal;
        this.format = format;
    }

    @Override
    public VariablePlotDescriptor buildDescriptor() {
        return new VariablePlotDescriptor.Vector(name, signal.getWidth(), format);
    }

    @Override
    public Object buildSample() {
        return signal.getValue();
    }

}
