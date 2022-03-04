package name.martingeisse.esdk.plot.variable;

import name.martingeisse.esdk.core.util.vector.Vector;

public interface VectorFormat {

    String render(VariablePlotDescriptor descriptor, Vector sample);

}
