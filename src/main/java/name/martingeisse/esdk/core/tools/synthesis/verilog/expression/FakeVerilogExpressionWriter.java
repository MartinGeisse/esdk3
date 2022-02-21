package name.martingeisse.esdk.core.tools.synthesis.verilog.expression;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.signal.Signal;

/**
 * Base class for "fake" writers that re-use the verilog writing routines to analyze signal usage. This avoids
 * duplicate code and helps to avoid subtle bugs due to the analysis being wrong.
 */
public abstract class FakeVerilogExpressionWriter implements VerilogExpressionWriter {

    @Override
    public final VerilogExpressionWriter print(String s) {
        return this;
    }

    @Override
    public final VerilogExpressionWriter print(int i) {
        return this;
    }

    @Override
    public final VerilogExpressionWriter print(char c) {
        return this;
    }

    @Override
    public final VerilogExpressionWriter printSignal(Signal signal, VerilogExpressionNesting nesting) {
        visitSignal(signal, nesting);
        return this;
    }

    @Override
    public final VerilogExpressionWriter printMemory(ProceduralMemory memory) {
        visitMemory(memory);
        return this;
    }

    protected abstract void visitSignal(Signal signal, VerilogExpressionNesting nesting);
    protected abstract void visitMemory(ProceduralMemory memory);

}
