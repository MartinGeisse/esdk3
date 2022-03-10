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
        out.flush();
    }

    public void disassemble(Vector instruction) {
        if (instruction.getWidth() != 32) {
            throw new IllegalArgumentException("argument has wrong width: " + instruction.getWidth() + " (expected 32)");
        }
        disassemble(instruction.getAsSignedInt());
        out.flush();
    }

    public void disassemble(int instruction) {
        int opcode = getOpcodeField(instruction);
        switch (opcode) {

            case 0b0000011:
                disassembleLoad(instruction);
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
                disassembleOp(instruction, true);
                break;

            case 0b0010111:
                printUiInstruction("auipc", instruction);
                break;

            case 0b0011011:
                unimplementedOpcode("OP-IMM-32");
                break;

            // case 0b0011111: -- handle as invalid opcode in default branch

            case 0b0100011:
                disassembleStore(instruction);
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
                disassembleOp(instruction, false);
                break;

            case 0b0110111:
                printUiInstruction("lui", instruction);
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
        out.flush();
    }

    // ----------------------------------------------------------------------------------------------------------------
    // opcode-specific disassemblers
    // ----------------------------------------------------------------------------------------------------------------

    protected void unimplementedOpcode(String opcodeText) {
        out.print(opcodeText + " (not implemented in disassembler)");
    }

    protected void disassembleOp(int instruction, boolean isOpImm) {
        int funct3 = getFunct3Field(instruction);
        boolean bit30 = getBit(instruction, 30);
        switch (funct3) {

            case 0:
                if (isOpImm) {
                    printOpImm("addi", instruction);
                } else if (bit30) {
                    printOp("sub", instruction);
                } else {
                    printOp("add", instruction);
                }
                break;

            case 1:
                if (isOpImm) {
                    printShiftOpImm("slli", instruction);
                } else {
                    printOp("sll", instruction);
                }
                break;

            case 2:
                if (isOpImm) {
                    printOpImm("slti", instruction);
                } else {
                    printOp("slt", instruction);
                }
                break;

            case 3:
                if (isOpImm) {
                    printOpImm("sltiu", instruction);
                } else {
                    printOp("sltu", instruction);
                }
                break;

            case 4:
                if (isOpImm) {
                    printOpImm("xori", instruction);
                } else {
                    printOp("xor", instruction);
                }
                break;

            case 5:
                if (isOpImm) {
                    if (bit30) {
                        printShiftOpImm("srai", instruction);
                    } else {
                        printShiftOpImm("srli", instruction);
                    }
                } else {
                    if (bit30) {
                        printOp("sra", instruction);
                    } else {
                        printOp("srl", instruction);
                    }
                }
                break;

            case 6:
                if (isOpImm) {
                    printOpImm("ori", instruction);
                } else {
                    printOp("or", instruction);
                }
                break;

            case 7:
                if (isOpImm) {
                    printOpImm("andi", instruction);
                } else {
                    printOp("and", instruction);
                }
                break;

            default:
                throw new RuntimeException();

        }
    }

    protected void printOp(String mnemonic, int instruction) {
        out.print(mnemonic + " x" + getRdField(instruction) + ", x" + getRs1Field(instruction) + ", x" + getRs2Field(instruction));
    }

    protected void printOpImm(String mnemonic, int instruction) {
        out.print(mnemonic + " x" + getRdField(instruction) + ", x" + getRs1Field(instruction) + ", " + getOpImmediateField(instruction));
    }

    protected void printShiftOpImm(String mnemonic, int instruction) {
        out.print(mnemonic + " x" + getRdField(instruction) + ", x" + getRs1Field(instruction) + ", " + getShiftAmountField(instruction));
    }

    protected void printUiInstruction(String mnemonic, int instruction) {
        out.print(mnemonic + " x" + getRdField(instruction) + ", " + getUiField(instruction));
    }

    protected void disassembleLoad(int instruction) {
        int dataRegister = getRdField(instruction);
        int offset = getOpImmediateField(instruction);
        int addressRegister = getRs1Field(instruction);
        int width = getFunct3Field(instruction);
        switch (width) {

            case 0:
                printLoadStore("lb", dataRegister, offset, addressRegister);
                break;

            case 1:
                printLoadStore("lh", dataRegister, offset, addressRegister);
                break;

            case 2:
                printLoadStore("lw", dataRegister, offset, addressRegister);
                break;

            case 4:
                printLoadStore("lbu", dataRegister, offset, addressRegister);
                break;

            case 5:
                printLoadStore("lhu", dataRegister, offset, addressRegister);
                break;

            default:
                printLoadStore("load-unknown-size", dataRegister, offset, addressRegister);
                break;

        }
    }

    protected void disassembleStore(int instruction) {
        int dataRegister = getRs2Field(instruction);
        int offset = ((instruction & 0xfe000000) >> 20) | ((instruction >> 7) & 31);
        int addressRegister = getRs1Field(instruction);
        int width = getFunct3Field(instruction);
        switch (width) {

            case 0:
                printLoadStore("sb", dataRegister, offset, addressRegister);
                break;

            case 1:
                printLoadStore("sh", dataRegister, offset, addressRegister);
                break;

            case 2:
                printLoadStore("sw", dataRegister, offset, addressRegister);
                break;

        }
    }

    protected void printLoadStore(String mnemonic, int dataRegister, int offset, int addressRegister) {
        out.print(mnemonic + " x" + dataRegister + ", " + offset + "(x" + addressRegister + ")");
    }

    // ----------------------------------------------------------------------------------------------------------------
    // helpers
    // ----------------------------------------------------------------------------------------------------------------

    protected final int getOpcodeField(int instruction) {
        return instruction & 127;
    }

    protected final int getFunct3Field(int instruction) {
        return (instruction >> 12) & 7;
    }

    protected final int getOpImmediateField(int instruction) {
        return instruction >> 20; // sign-extended
    }

    protected final int getShiftAmountField(int instruction) {
        return getOpImmediateField(instruction) & 31;
    }

    protected final int getRdField(int instruction) {
        return (instruction >> 7) & 31;
    }

    protected final int getRs1Field(int instruction) {
        return (instruction >> 15) & 31;
    }

    protected final int getRs2Field(int instruction) {
        return (instruction >> 20) & 31;
    }

    protected final int getUiField(int instruction) {
        return (instruction >>> 12);
    }

    protected final int getUiFieldPrescaled(int instruction) {
        return instruction & 0xfffff000;
    }

    protected final boolean getBit(int instruction, int index) {
        return ((instruction >> index) & 1) != 0;
    }

    /**
     * Truncates or zero-extends the value if necessary.
     */
    protected final String toBinaryString(int value, int bits) {
        String s = "00000000000000000000000000000000" + Integer.toBinaryString(value);
        return s.substring(s.length() - bits);
    }

}
