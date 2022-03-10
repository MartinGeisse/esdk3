package name.martingeisse.esdk.plot.builder;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.plot.variable.MemorySample;
import name.martingeisse.esdk.plot.variable.VariablePlotDescriptor;
import name.martingeisse.esdk.plot.variable.VectorFormat;

public class MemoryVariablePlotSource implements VariablePlotSource {

    private final String name;
    private final ProceduralMemory memory;
    private final VectorFormat rowFormat;
    private Matrix previousContents = null;

    public MemoryVariablePlotSource(String name, ProceduralMemory memory) {
        this(name, memory, null);
    }

    public MemoryVariablePlotSource(String name, ProceduralMemory memory, VectorFormat rowFormat) {
        this.name = name;
        this.memory = memory;
        this.rowFormat = rowFormat;
    }

    @Override
    public VariablePlotDescriptor buildDescriptor() {
        Matrix matrix = memory.getMatrix();
        return new VariablePlotDescriptor.Memory(name, matrix.getRowCount(), matrix.getColumnCount(), rowFormat);
    }

    @Override
    public Object buildSample() {
        Matrix contents = memory.getMatrix();
        if (previousContents == null) {
            MemorySample.Full sample = new MemorySample.Full(contents);
            previousContents = new Matrix(contents);
            return sample;
        } else {
            MemorySample.Delta sample = new MemorySample.Delta(previousContents, contents);
            sample.applyTo(previousContents);
            return sample;
        }
    }

}
