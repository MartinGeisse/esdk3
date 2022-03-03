package name.martingeisse.esdk.plot.plotter;

import name.martingeisse.esdk.plot.ValuePlotDescriptor;

public interface ValuePlotter {
    ValuePlotDescriptor buildDescriptor();
    Object buildUpdate();
}
