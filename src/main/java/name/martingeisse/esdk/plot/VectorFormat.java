package name.martingeisse.esdk.plot;

import name.martingeisse.esdk.core.util.vector.Vector;

public interface VectorFormat {

    String render(VariablePlotDescriptor descriptor, Vector sample);

}
