package name.martingeisse.esdktest.designs.components.riscv;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.io.PrintWriter;

public class InstructionToTextDisassembler {

    protected final PrintWriter out;

    public InstructionToTextDisassembler(PrintWriter out) {
        this.out = out;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // entry points
    // ----------------------------------------------------------------------------------------------------------------

    public void disassemble(VectorSignal signal) {
        disassemble(signal.getValue());
    }

    public void disassemble(Vector instruction) {
        if (instruction.getWidth() != 32) {
            throw new IllegalArgumentException("argument has wrong width: " + instruction.getWidth() + " (expected 32)");
        }
        disassemble(instruction.getAsSignedInt());
    }

    public void disassemble(int instruction) {
        int opcode = getOpcode(instruction);
        switch (opcode) {

            case 0b0000011:
                out.print("TODO LOAD");
                break;

            case 0b0000111:
                unimplementedOpcode("LOAD-FP");
                break;

            case 0b0001011:
                unimplementedOpcode("custom-0");
                break;

            case 0b0001111:
                unimplementedOpcode("MISC-MEM");
                break;

            case 0b0010011:
                out.print("TODO OP-IMM");
                break;

            case 0b0010111:
                out.print("TODO AUIPC");
                break;

            case 0b0011011:
                unimplementedOpcode("OP-IMM-32");
                break;

            // case 0b0011111: -- handle as invalid opcode in default branch

            case 0b0100011:
                out.print("TODO STORE");
                break;

            case 0b0100111:
                unimplementedOpcode("STORE-FP");
                break;

            case 0b0101011:
                unimplementedOpcode("custom-1");
                break;

            case 0b0101111:
                unimplementedOpcode("AMO");
                break;

            case 0b0110011:
                out.print("TODO OP");
                break;

            case 0b0110111:
                out.print("TODO LUI");
                break;

            case 0b0111011:
                unimplementedOpcode("OP-32");
                break;

            // case 0b0111111: -- handle as invalid opcode in default branch

            case 0b1000011:
                unimplementedOpcode("MADD");
                break;

            case 0b1000111:
                unimplementedOpcode("MSUB");
                break;

            case 0b1001011:
                unimplementedOpcode("NMSUB");
                break;

            case 0b1001111:
                unimplementedOpcode("NMADD");
                break;

            case 0b1010011:
                unimplementedOpcode("OP-FP");
                break;

            // case 0b1010111: -- handle as invalid opcode in default branch

            // case 0b1011011: -- handle as invalid opcode in default branch

            // case 0b1011111: -- handle as invalid opcode in default branch

            case 0b1100011:
                out.print("TODO BRANCH");
                break;

            case 0b1100111:
                out.print("TODO JALR");
                break;

            // case 0b1101011: -- handle as invalid opcode in default branch

            case 0b1101111:
                out.print("TODO JAL");
                break;

            case 0b1110011:
                unimplementedOpcode("SYSTEM");
                break;

            // case 0b1110111: -- handle as invalid opcode in default branch

            // case 0b1111011: -- handle as invalid opcode in default branch

            // case 0b1111111: -- handle as invalid opcode in default branch

            default:
                out.print("invalid opcode: " + toBinaryString(opcode, 7));
                break;
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // opcode-specific disassemblers
    // ----------------------------------------------------------------------------------------------------------------

    protected void unimplementedOpcode(String opcodeText) {
        out.print(opcodeText + " (not implemented in disassembler)");
    }

    // ----------------------------------------------------------------------------------------------------------------
    // helpers
    // ----------------------------------------------------------------------------------------------------------------

    protected final int getOpcode(int instruction) {
        return instruction & 127;
    }

    /**
     * Truncates or zero-extends the value if necessary.
     */
    protected final String toBinaryString(int value, int bits) {
        String s = "00000000000000000000000000000000" + Integer.toBinaryString(value);
        return s.substring(s.length() - bits);
    }


}
