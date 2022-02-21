package name.martingeisse.esdk.core.library.procedural.statement.target;

import name.martingeisse.esdk.core.library.procedural.ProceduralMemoryConstantIndexSelection;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemoryIndexSelection;
import name.martingeisse.esdk.core.library.procedural.ProceduralVectorRegister;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.vector.ConstantIndexSelection;
import name.martingeisse.esdk.core.library.signal.vector.IndexSelection;
import name.martingeisse.esdk.core.library.signal.vector.RangeSelection;

public final class AssignmentTargetFactory {

    public static BitAssignmentTarget buildBitAssignmentTarget(BitSignal destination) {
        if (destination instanceof BitAssignmentTarget) {
            return (BitAssignmentTarget) destination;
        }
        if (destination instanceof ConstantIndexSelection) {
            ConstantIndexSelection selection = (ConstantIndexSelection)destination;
            if (selection.getContainerSignal() instanceof ProceduralVectorRegister) {
                ProceduralVectorRegister register = (ProceduralVectorRegister)selection.getContainerSignal();
                return new VectorTargetConstantIndexSelection(register, selection.getIndex());
            }
        }
        if (destination instanceof IndexSelection) {
            IndexSelection selection = (IndexSelection)destination;
            if (selection.getContainerSignal() instanceof ProceduralVectorRegister) {
                ProceduralVectorRegister register = (ProceduralVectorRegister)selection.getContainerSignal();
                return new VectorTargetIndexSelection(register, selection.getIndexSignal());
            }
        }
        throw new RuntimeException("invalid assignment destination: " + destination);
    }

    public static VectorAssignmentTarget buildVectorAssignmentTarget(VectorSignal destination) {
        if (destination instanceof VectorAssignmentTarget) {
            return (VectorAssignmentTarget) destination;
        }
        if (destination instanceof RangeSelection) {
            RangeSelection selection = (RangeSelection)destination;
            if (selection.getContainerSignal() instanceof ProceduralVectorRegister) {
                ProceduralVectorRegister register = (ProceduralVectorRegister)selection.getContainerSignal();
                return new VectorTargetRangeSelection(register, selection.getFrom(), selection.getTo());
            }
        }
        if (destination instanceof ProceduralMemoryConstantIndexSelection) {
            ProceduralMemoryConstantIndexSelection selection = (ProceduralMemoryConstantIndexSelection)destination;
            return new MemoryTargetConstantIndexSelection(selection.getMemory(), selection.getIndex());
        }
        if (destination instanceof ProceduralMemoryIndexSelection) {
            ProceduralMemoryIndexSelection selection = (ProceduralMemoryIndexSelection)destination;
            return new MemoryTargetIndexSelection(selection.getMemory(), selection.getIndexSignal());
        }
        throw new RuntimeException("invalid assignment destination: " + destination);
    }

}
