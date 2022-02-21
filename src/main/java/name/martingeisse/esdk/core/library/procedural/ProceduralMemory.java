package name.martingeisse.esdk.core.library.procedural;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.procedural.statement.target.MemoryTargetConstantIndexSelection;
import name.martingeisse.esdk.core.library.procedural.statement.target.MemoryTargetIndexSelection;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogUtil;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class ProceduralMemory extends DesignItem implements DesignItemOwned {

    private final Matrix matrix;
    private final List<Update> updates = new ArrayList<>();
    private ClockedBlock clockedBlock;

    public ProceduralMemory(int rowCount, int columnCount) {
        this(new Matrix(rowCount, columnCount));
    }

    public ProceduralMemory(Matrix matrix) {
        this.matrix = matrix;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    /**
     * This getter returns null before finishing construction.
     */
    public ClockedBlock getClockedBlock() {
        return clockedBlock;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // construction
    // ----------------------------------------------------------------------------------------------------------------

    @Override
    protected void finalizeConstructionAfterValidation() {
        for (ClockedBlock block : getDesign().getItems(ClockedBlock.class)) {
            if (block.getOrDetermineAssignedProceduralMemories().contains(this)) {
                if (clockedBlock != null) {
                    // TODO this indicates that finalizing and validation aren't as independent as I'd like
                    throw new RuntimeException("multiple clocked blocks assign to memory " + this);
                }
                clockedBlock = block;
            }
        }
        // if no clockedBlock has been found, then this is a ROM
    }

    // ----------------------------------------------------------------------------------------------------------------
    // factory methods
    // ----------------------------------------------------------------------------------------------------------------

    public VectorSignal select(VectorSignal index) {
        return new ProceduralMemoryIndexSelection(this, index);
    }

    public VectorSignal select(int index) {
        return new ProceduralMemoryConstantIndexSelection(this, index);
    }

    public MemoryTargetIndexSelection selectTarget(VectorSignal index) {
        return new MemoryTargetIndexSelection(this, index);
    }

    public MemoryTargetConstantIndexSelection selectTarget(int index) {
        return new MemoryTargetConstantIndexSelection(this, index);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // simulation
    // ----------------------------------------------------------------------------------------------------------------

    public void requestUpdate(int index, Vector value) {
        if (index < 0 || index >= matrix.getRowCount()) {
            throw new IllegalArgumentException("invalid index: " + index);
        }
        if (value.getWidth() != matrix.getColumnCount()) {
            throw new IllegalArgumentException("new value has width " + value.getWidth() + ", expected " + matrix.getColumnCount());
        }
        Update update = new Update();
        update.index = index;
        update.value = value;
        updates.add(update);
    }

    /**
     * Updates the value from the stored next value.
     */
    void updateMatrix() {
        for (Update update : updates) {
            matrix.setRow(update.index, update.value);
        }
        updates.clear();
    }

    private static class Update {
        private int index;
        private Vector value;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Verilog generation
    // ----------------------------------------------------------------------------------------------------------------

    @Override
    public VerilogContribution getVerilogContribution() {
        return new VerilogContribution() {

            @Override
            public void prepareSynthesis(SynthesisPreparationContext context) {
                String verilogName = context.assignGeneratedName(ProceduralMemory.this);
                VerilogUtil.generateMif(context.getAuxiliaryFileFactory(), verilogName + ".mif", matrix);
            }

            @Override
            public void analyzeSignalUsage(SignalUsageConsumer consumer) {
            }

            @Override
            public void printDeclarations(VerilogWriter out) {
                out.println("reg [" + (matrix.getColumnCount() - 1) + ":0] " +
                        out.getName(ProceduralMemory.this) + " [" + (matrix.getRowCount() - 1) + ":0];");
            }

            @Override
            public void printImplementation(VerilogWriter out) {
                String verilogName = out.getName(ProceduralMemory.this);
                out.indent();
                out.println("initial begin");
                out.startIndentation();
                out.println("\t$readmemh(\"" + verilogName + ".mif\", " + verilogName + ", 0, " +
                        (matrix.getRowCount() - 1) + ");");
                out.endIndentation();
                out.indent();
                out.println("end");
            }

        };
    }

}
