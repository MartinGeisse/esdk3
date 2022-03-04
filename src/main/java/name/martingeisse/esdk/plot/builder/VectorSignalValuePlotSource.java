package name.martingeisse.esdk.plot.builder;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.plot.ValuePlotDescriptor;
import name.martingeisse.esdk.plot.VectorFormat;

public final class VectorSignalValuePlotSource implements ValuePlotSource {

    private final String name;
    private final VectorSignal signal;
    private final VectorFormat format;

    public VectorSignalValuePlotSource(String name, VectorSignal signal) {
        this(name, signal, null);
    }

    public VectorSignalValuePlotSource(String name, VectorSignal signal, VectorFormat format) {
        this.name = name;
        this.signal = signal;
        this.format = format;
    }

    @Override
    public ValuePlotDescriptor buildDescriptor() {
        return new ValuePlotDescriptor.Vector(name, signal.getWidth(), format);
    }

    @Override
    public Object buildSample() {
        return signal.getValue();
    }

}
