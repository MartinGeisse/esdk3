package name.martingeisse.esdktest.designs.components.riscv;

import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.simulation.ClockedSimulationDesignItem;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class TraceLogger extends ClockedSimulationDesignItem {

    private static final String[] STATE_NAMES;
    static {
        try {
            STATE_NAMES = new String[256];
            Arrays.fill(STATE_NAMES, "<unknown>");
            for (Field field : RiscvCpu.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers()) && field.getName().startsWith("STATE_")) {
                    Vector vector = (Vector)field.get(null);
                    STATE_NAMES[vector.getAsUnsignedInt()] = field.getName();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final RiscvCpu cpu;
    private final InstructionToTextDisassembler disassembler;

    public TraceLogger(RiscvCpu cpu) {
        super(cpu.clock);
        this.cpu = cpu;
        this.disassembler = new InstructionToTextDisassembler(new PrintWriter(System.out));
    }

    @Override
    public void computeNextState() {

        Vector state = cpu.state.getValue();
        if (state.equals(RiscvCpu.STATE_START) || state.equals(RiscvCpu.STATE_FINISH_EARLY_FETCH)) {
            System.out.println();
        }
        System.out.print(STATE_NAMES[state.getAsUnsignedInt()]);

        if (state.equals(RiscvCpu.STATE_START) || state.equals(RiscvCpu.STATE_FETCH) || state.equals(RiscvCpu.STATE_FINISH_EARLY_FETCH)) {
            System.out.print(", pc = " + toHex(cpu.pc));
        } else {
            System.out.print(", oldPc = " + toHex(cpu.oldPc));
        }
        if (cpu.bus.enable.getValue() && cpu.bus.acknowledge.getValue()) {
            boolean write = cpu.bus.write.getValue();
            System.out.print(write ? ", bus write 0x" : ", bus read 0x");
            System.out.print(toHex((long)cpu.bus.wordAddress.getValue().getAsSignedInt() << 2));
            System.out.print(", data 0x");
            System.out.print(toHex(write ? cpu.bus.writeData : cpu.bus.readData));
            if (write) {
                System.out.print(", mask " + Integer.toBinaryString(cpu.bus.writeMask.getValue().getAsUnsignedInt()));
            }
        }
        if (state.equals(RiscvCpu.STATE_DECODE_AND_READ1)) {
            // System.out.print(", instruction = (" + instructionToFields() + ")");
            System.out.print(", instruction = ");
            disassembler.disassemble(cpu.instructionRegister);
            System.out.println();
            int instruction = cpu.instructionRegister.getValue().getAsSignedInt();
            System.out.print("\t[rd] = " + cpu.registers.getMatrix().getRow((instruction >> 7) & 31));
            System.out.print(", [rs1] = " + cpu.registers.getMatrix().getRow((instruction >> 15) & 31));
            System.out.print(", [rs2] = " + cpu.registers.getMatrix().getRow((instruction >> 20) & 31));
        }

        System.out.println();
    }

    private String toHex(VectorSignal signal) {
        return toHex(signal.getValue());
    }

    private String toHex(Vector value) {
        return toHex(value.getAsSignedInt());
    }

    private String toHex(long value) {
        String s = "00000000" + Long.toHexString(value & 0xffffffffL);
        return s.substring(s.length() - 8);
    }

    private String instructionToFields() {
        int value = cpu.instructionRegister.getValue().getAsSignedInt();
        return "0x" + Integer.toHexString(value >>> 25) + " " +
            "rs2=" + ((value >>> 20) & 31) + " " +
            "rs1=" + ((value >>> 15) & 31) + " " +
            "0b" + (Integer.toBinaryString((value >>> 12) & 7)) + " " +
            "rd=" + ((value >>> 7) & 31) + " " +
            "0b" + Integer.toBinaryString(value & 127);
    }

    @Override
    public void updateState() {
    }

}
