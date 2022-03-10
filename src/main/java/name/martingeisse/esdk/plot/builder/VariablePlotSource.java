package name.martingeisse.esdk.plot.builder;

import name.martingeisse.esdk.plot.variable.VariablePlotDescriptor;

public interface VariablePlotSource {
    VariablePlotDescriptor buildDescriptor();
    Object buildSample();
}
