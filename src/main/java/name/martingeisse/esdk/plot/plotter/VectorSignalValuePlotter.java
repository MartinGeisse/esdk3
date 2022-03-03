package name.martingeisse.esdk.plot.plotter;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.plot.ValuePlotDescriptor;
import name.martingeisse.esdk.plot.VectorFormat;

public final class VectorSignalValuePlotter implements ValuePlotter {

    private final String name;
    private final VectorSignal signal;
    private final VectorFormat format;

    public VectorSignalValuePlotter(String name, VectorSignal signal) {
        this(name, signal, null);
    }

    public VectorSignalValuePlotter(String name, VectorSignal signal, VectorFormat format) {
        this.name = name;
        this.signal = signal;
        this.format = format;
    }

    @Override
    public ValuePlotDescriptor buildDescriptor() {
        return new ValuePlotDescriptor.Vector(name, signal.getWidth(), format);
    }

    @Override
    public Object buildUpdate() {
        return signal.getValue();
    }

}
