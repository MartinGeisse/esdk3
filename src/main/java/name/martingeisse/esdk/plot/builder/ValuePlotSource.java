package name.martingeisse.esdk.plot.builder;

import name.martingeisse.esdk.plot.ValuePlotDescriptor;

public interface ValuePlotSource {
    ValuePlotDescriptor buildDescriptor();
    Object buildUpdate();
}
