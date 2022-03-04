package name.martingeisse.esdk.plot.builder;

import name.martingeisse.esdk.plot.VariablePlotDescriptor;

public interface VariablePlotSource {
    VariablePlotDescriptor buildDescriptor();
    Object buildSample();
}
