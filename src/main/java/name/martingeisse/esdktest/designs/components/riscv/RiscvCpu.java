package name.martingeisse.esdktest.designs.components.riscv;

import name.martingeisse.esdk.core.component.Component;
import name.martingeisse.esdk.core.library.procedural.ProceduralBitRegister;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralVectorRegister;
import name.martingeisse.esdk.core.library.procedural.statement.SwitchStatement;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.signal.connector.BitConnector;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdk.core.library.signal.mux.VectorSwitchSignal;
import name.martingeisse.esdk.core.util.vector.Vector;
import name.martingeisse.esdktest.designs.components.bus.BusMasterInterface;

public final class RiscvCpu extends Component {

    // interface
    public final ClockConnector clock = inClock();
    public final BitConnector reset = inBit();
    public final BusMasterInterface bus = new BusMasterInterface(this);
    public final BitConnector interrupt = inBit();

    // state machine
    // TODO we have 3 different representations for constants: int, Vector, VectorConstant -- ugly!
    // try to merge Vector and VectorConstant. Problem with this is the reference to the Design object
    // int cannot be a general representation because it is limited to 32 (long 64) bits
    // -> general idea: merge Vector and VectorConstant, then accept that and int everywhere
    public final ProceduralVectorRegister state = vectorRegister(5, STATE_START);
    public static final Vector STATE_START = Vector.of(5, 0);
    public static final Vector STATE_FETCH = Vector.of(5, 1);
    public static final Vector STATE_DECODE_AND_READ1 = Vector.of(5, 2);
    public static final Vector STATE_DECODE_AND_READ2 = Vector.of(5, 3);
    public static final Vector STATE_EXEC_OP_0 = Vector.of(5, 4);
    public static final Vector STATE_EXEC_OP_1 = Vector.of(5, 5);
    public static final Vector STATE_EXEC_OP_2 = Vector.of(5, 6);
    public static final Vector STATE_EXEC_OP_3 = Vector.of(5, 7);
    public static final Vector STATE_PREPARE_BRANCH = Vector.of(5, 8);
    public static final Vector STATE_EXEC_BRANCH = Vector.of(5, 9);
    public static final Vector STATE_EXEC_LUI = Vector.of(5, 10);
    public static final Vector STATE_EXEC_AUIPC = Vector.of(5, 11);
    public static final Vector STATE_EXEC_JAL = Vector.of(5, 12);
    public static final Vector STATE_EXEC_JALR = Vector.of(5, 13);
    public static final Vector STATE_MEM_COMPUTE_ADDRESS = Vector.of(5, 14);
    public static final Vector STATE_MEM_ACCESS = Vector.of(5, 15);
    public static final Vector STATE_MEM_EXTEND = Vector.of(5, 16);
    public static final Vector STATE_EXCEPTION = Vector.of(5, 17);
    public static final Vector STATE_SYSTEM_INSTRUCTION = Vector.of(5, 18);
    public static final Vector STATE_CSR_INSTRUCTION = Vector.of(5, 19);
    public static final Vector STATE_WRITE_MISC_VALUE_TO_REGISTER = Vector.of(5, 20);
    public static final Vector STATE_CUSTOM_INSTRUCTION = Vector.of(5, 21);
    public static final Vector STATE_FINISH_EARLY_FETCH = Vector.of(5, 22);

    // PC and old PC (current instruction location, even after the PC gets incremented)
    public final ProceduralVectorRegister pc = vectorRegister(32, 0);
    public final ProceduralVectorRegister oldPc = vectorRegister(32, 0);

    // instruction decoding
    public final ProceduralVectorRegister instructionRegister = vectorRegister(32);
    public final ProceduralVectorRegister immediateOperand = vectorRegister(32);
    public final VectorSignal opcode = select(instructionRegister, 6, 2);
    public final VectorSignal sourceRegisterIndex1 = select(instructionRegister, 19, 15);
    public final VectorSignal sourceRegisterIndex2 = select(instructionRegister, 24, 20);
    public final VectorSignal destinationRegisterIndex = select(instructionRegister, 11, 7);
    public final BitSignal operationIsMulDev = and(select(opcode, 3), select(instructionRegister, 25));

    // register file
    public final ProceduralMemory registers = memory(32, 32);
    public final ProceduralVectorRegister registerReadValue = vectorRegister(32); // register reads go here
    public final ProceduralVectorRegister firstRegisterValue = vectorRegister(32); // first read value is stored here on second read
    public final VectorSignal leftOperand = firstRegisterValue;
    public final VectorSignal rightOperand = when(select(opcode, 3), registerReadValue, immediateOperand);
    public final BitSignal leftOperandLessThanRightOperandSigned = lessThan(
        concat(not(select(leftOperand, 31)), select(leftOperand, 30, 0)),
        concat(not(select(rightOperand, 31)), select(rightOperand, 30, 0))
    ); // TODO move to unsigned operations helper class
    public final BitSignal leftOperandLessThanRightOperandUnsigned = lessThan(leftOperand, rightOperand);
    public final BitSignal rightShiftInBitRegister = bitRegister(); // this register is loaded even before the first execution state, so it is ready when computing temporary results

    // bus access
    public final ProceduralVectorRegister busAddressRegister = vectorRegister(32);
    public final ProceduralVectorRegister busReadDataRegister = vectorRegister(32);

    // early fetching
    public final ProceduralBitRegister earlyFetchStarted = bitRegister();
    public final ProceduralBitRegister earlyFetchFinished = bitRegister();
    public final ProceduralVectorRegister earlyFetchResult = vectorRegister(32);

    // Values from various special locations that should go into a general-purpose register can be stored here first,
    // so the actual path to the register file is a fast one.
    public final ProceduralVectorRegister miscRegisterWriteValue = vectorRegister(32);

    // exception / interrupt handling
    public final ProceduralVectorRegister exceptionHandlerReturnAddress = vectorRegister(32);

    // special registers including CSRs
    public final ProceduralBitRegister enableInterrupts = bitRegister();
    public final ProceduralVectorRegister exceptionCode = vectorRegister(5, 0);
    public static final int EXCEPTION_NONE = 0;
    public static final int EXCEPTION_INTERRUPT = 1;
    public static final int EXCEPTION_INVALID_INSTRUCTION = 2;

    public RiscvCpu() {
        buildBusInterfaceOutputs();
        buildRegisterFileLogic();
        on(clock, () -> {
            when(reset, () -> {
                set(state, STATE_START);
                set(pc, 0);
                set(enableInterrupts, false);
            }, () -> {
                when(bus.acknowledge, () -> {
                    set(earlyFetchFinished, true); // will be ignored for non-early-fetch ACKs
                    set(earlyFetchResult, bus.readData);
                });
                SwitchStatement stateSwitch = dslStatement(new SwitchStatement(state));
                addCase(stateSwitch, STATE_START, () -> {
                    set(oldPc, pc);
                    set(busAddressRegister, pc);
                    when(and(enableInterrupts, interrupt), () -> {
                        set(exceptionCode, EXCEPTION_INTERRUPT);
                        set(state, STATE_EXCEPTION);
                    }, () -> {
                        set(state, STATE_FETCH);
                    });
                });
                addCase(stateSwitch, STATE_FETCH, () -> {
                    set(instructionRegister, bus.readData);
                    when(bus.acknowledge, () -> {
                        set(state, STATE_DECODE_AND_READ1);
                        inc(pc, 4);
                    });
                });
                addCase(stateSwitch, STATE_FINISH_EARLY_FETCH, () -> {
                    // note: we could save another cycle by noticing bus.acknowledge here directly, but we'd have to
                    // add more muxes for the other values.
                    when(earlyFetchFinished, () -> {
                        set(earlyFetchStarted, false);
                        set(earlyFetchFinished, false);
                        set(oldPc, pc);
                        inc(pc, 4);
                        set(instructionRegister, earlyFetchResult);
                        when(and(enableInterrupts, interrupt), () -> {
                            set(exceptionCode, EXCEPTION_INTERRUPT);
                            set(state, STATE_EXCEPTION);
                        }, () -> {
                            set(state, STATE_DECODE_AND_READ1);
                        });
                    });
                });
                addCase(stateSwitch, STATE_DECODE_AND_READ1, () -> {
                    set(immediateOperand, concat(repeat(20, select(instructionRegister, 31)), select(instructionRegister, 31, 20)));
                    set(state, STATE_DECODE_AND_READ2);
                });
                addCase(stateSwitch, STATE_DECODE_AND_READ2, () -> {
                    set(firstRegisterValue, registerReadValue);
                    set(rightShiftInBitRegister, and(select(instructionRegister, 30), select(registerReadValue, 31)));
                    set(busAddressRegister, pc); // for early fetching
                    set(earlyFetchFinished, false);

                    SwitchStatement opcodeSwitch = dslStatement(new SwitchStatement(opcode));
                    addCase(opcodeSwitch, Vector.of(5, 0), () -> { // LOAD
                        set(state, STATE_MEM_COMPUTE_ADDRESS);
                    });
                    addCase(opcodeSwitch, Vector.of(5, 2), () -> { // custom-0
                        set(state, STATE_CUSTOM_INSTRUCTION);
                    });
                    addCase(opcodeSwitch, Vector.of(5, 3), () -> { // MISC-MEM (NOP)
                        set(state, STATE_START);
                    });
                    addCase(opcodeSwitch, Vector.of(5, 4), () -> { // OP-IMM
                        set(earlyFetchStarted, true);
                        set(state, when(
                            eq(select(instructionRegister, 14, 12), 1),
                            constant(STATE_EXEC_OP_0),
                            constant(STATE_EXEC_OP_1)
                        ));
                    });
                    addCase(opcodeSwitch, Vector.of(5, 5), () -> { // AUIPC
                        set(earlyFetchStarted, true);
                        set(state, STATE_EXEC_AUIPC);
                    });
                    addCase(opcodeSwitch, Vector.of(5, 8), () -> { // STORE
                        set(state, STATE_MEM_COMPUTE_ADDRESS);
                    });
                    addCase(opcodeSwitch, Vector.of(5, 12), () -> { // OP
                        set(earlyFetchStarted, true);
                        when(operationIsMulDev, () -> {
                            set(state, when(
                                eq(select(instructionRegister, 14, 12), 0),
                                constant(STATE_EXEC_OP_0),
                                constant(STATE_EXEC_OP_1)
                            ));
                        }, () -> {
                            set(state, when(
                                eq(select(instructionRegister, 14, 12), 1),
                                constant(STATE_EXEC_OP_0),
                                constant(STATE_EXEC_OP_1)
                            ));
                        });
                    });
                    addCase(opcodeSwitch, Vector.of(5, 13), () -> { // LUI
                        set(earlyFetchStarted, true);
                        set(state, STATE_EXEC_LUI);
                    });
                    addCase(opcodeSwitch, Vector.of(5, 24), () -> { // BRANCH
                        set(state, STATE_PREPARE_BRANCH);
                    });
                    addCase(opcodeSwitch, Vector.of(5, 25), () -> { // JALR
                        set(state, STATE_EXEC_JALR);
                    });
                    addCase(opcodeSwitch, Vector.of(5, 27), () -> { // JAL
                        set(state, STATE_EXEC_JAL);
                    });
                    addCase(opcodeSwitch, Vector.of(5, 28), () -> { // SYSTEM
                        set(state, STATE_SYSTEM_INSTRUCTION);
                    });
                    defaultCase(opcodeSwitch, () -> {
                        set(state, STATE_EXCEPTION);
                        set(exceptionCode, EXCEPTION_INVALID_INSTRUCTION);
                    });
                });
                addCase(stateSwitch, STATE_EXEC_OP_0, () -> {
                    set(state, STATE_EXEC_OP_1);
                });
                addCase(stateSwitch, STATE_EXEC_OP_1, () -> {
                    when(and(operationIsMulDev, neq(select(instructionRegister, 14, 12), 0)), () -> {
                        set(state, STATE_EXCEPTION);
                        set(exceptionCode, EXCEPTION_INVALID_INSTRUCTION);
                    }, () -> {
                        set(state, STATE_EXEC_OP_2);
                    });
                });
                addCase(stateSwitch, STATE_EXEC_OP_2, () -> {
                    set(state, STATE_EXEC_OP_3);
                });
                addCase(stateSwitch, STATE_EXEC_OP_3, () -> {
                    set(state, STATE_FINISH_EARLY_FETCH);
                });
                addCase(stateSwitch, STATE_PREPARE_BRANCH, () -> {
                    // this state loads partialBranchCondition
                    set(state, STATE_EXEC_BRANCH);
                });
                addCase(stateSwitch, STATE_EXEC_BRANCH, () -> {
                    when(buildBranchCondition(), () -> {
                        set(pc, add(
                            oldPc,
                            concat(
                                repeat(20, select(instructionRegister, 31)),
                                select(instructionRegister, 7),
                                select(instructionRegister, 30, 25),
                                select(instructionRegister, 11, 8),
                                constant(false)
                            )
                        ));
                    });
                    set(state, STATE_START);
                });
                addCase(stateSwitch, STATE_EXEC_LUI, () -> {
                    set(state, STATE_FINISH_EARLY_FETCH);
                });
                addCase(stateSwitch, STATE_EXEC_AUIPC, () -> {
                    set(state, STATE_FINISH_EARLY_FETCH);
                });
                addCase(stateSwitch, STATE_EXEC_JAL, () -> {
                    set(pc, add(oldPc, concat(
                        repeat(12, select(instructionRegister, 31)),
                        select(instructionRegister, 19, 12),
                        select(instructionRegister, 20),
                        select(instructionRegister, 30, 21),
                        constant(false)
                    )));
                    set(state, STATE_START);
                });
                addCase(stateSwitch, STATE_EXEC_JALR, () -> {
                    set(pc,
                        concat(
                            select(
                                add(
                                    firstRegisterValue,
                                    concat(
                                        repeat(20, select(instructionRegister, 31)),
                                        select(instructionRegister, 31, 20)
                                    )
                                ),
                                31, 1
                            ),
                            constant(false)
                        )
                    );
                    set(state, STATE_START);
                });
                addCase(stateSwitch, STATE_MEM_COMPUTE_ADDRESS, () -> {
                    when(select(opcode, 3), () -> {
                        set(busAddressRegister, add(firstRegisterValue,
                            concat(
                                repeat(20, select(instructionRegister, 31)),
                                select(instructionRegister, 31, 25),
                                select(instructionRegister, 11, 7)
                            )
                        ));
                    }, () ->  {
                        set(busAddressRegister, add(firstRegisterValue, immediateOperand));
                    });
                    set(state, STATE_MEM_ACCESS);
                });
                addCase(stateSwitch, STATE_MEM_ACCESS, () -> {
                    set(busReadDataRegister, bus.readData);
                    when(bus.acknowledge, () -> {
                        set(state, when(select(opcode, 3), constant(STATE_START), constant(STATE_MEM_EXTEND)));
                    });
                });
                addCase(stateSwitch, STATE_MEM_EXTEND, () -> {
                    set(state, STATE_START);
                });
                addCase(stateSwitch, STATE_EXCEPTION, () -> {
                    set(enableInterrupts, false);
                    set(pc, 4);
                    set(exceptionHandlerReturnAddress, oldPc);
                    set(state, STATE_START);
                });
                addCase(stateSwitch, STATE_SYSTEM_INSTRUCTION, () -> {
                    SwitchStatement subSwitch = dslStatement(new SwitchStatement(select(instructionRegister, 14, 12)));
                    addCase(subSwitch, new Vector[] {Vector.of(3, 0), Vector.of(3, 4)}, () -> {
                        set(state, STATE_EXCEPTION);
                        set(exceptionCode, EXCEPTION_INVALID_INSTRUCTION);
                    });
                    defaultCase(subSwitch, () -> {
                        set(state, STATE_CSR_INSTRUCTION);
                    });
                });
                addCase(stateSwitch, STATE_CSR_INSTRUCTION, () -> {
                    set(miscRegisterWriteValue, 0x11335577);
                    set(state, STATE_WRITE_MISC_VALUE_TO_REGISTER);
                });
                addCase(stateSwitch, STATE_WRITE_MISC_VALUE_TO_REGISTER, () -> {
                    set(state, STATE_START);
                });
                addCase(stateSwitch, STATE_CUSTOM_INSTRUCTION, () -> {
                    SwitchStatement subSwitch = dslStatement(new SwitchStatement(select(instructionRegister, 29, 26)));

                    // read special register
                    addCase(subSwitch, Vector.of(4, 0), () -> {
                        set(state, STATE_WRITE_MISC_VALUE_TO_REGISTER);
                        SwitchStatement specialRegisterSwitch = dslStatement(new SwitchStatement(select(instructionRegister, 24, 20)));
                        addCase(specialRegisterSwitch, Vector.of(5, 0), () -> {
                            set(miscRegisterWriteValue, exceptionHandlerReturnAddress);
                        });
                        addCase(specialRegisterSwitch, Vector.of(5, 1), () -> {
                            set(miscRegisterWriteValue, concat(constant(27, 0), exceptionCode));
                        });
                        addCase(specialRegisterSwitch, Vector.of(5, 2), () -> {
                            set(miscRegisterWriteValue, concat(constant(31, 0), enableInterrupts));
                        });
                        defaultCase(specialRegisterSwitch, () -> {
                            set(state, STATE_EXCEPTION);
                            set(exceptionCode, EXCEPTION_INVALID_INSTRUCTION);
                        });
                    });

                    // write special register
                    addCase(subSwitch, Vector.of(4, 1), () -> {
                        set(state, STATE_START);
                        SwitchStatement specialRegisterSwitch = dslStatement(new SwitchStatement(select(instructionRegister, 24, 20)));
                        addCase(specialRegisterSwitch, Vector.of(5, 0), () -> {
                            set(exceptionHandlerReturnAddress, firstRegisterValue);
                        });
                        addCase(specialRegisterSwitch, Vector.of(5, 1), () -> {
                            set(exceptionCode, select(firstRegisterValue, 4, 0));
                        });
                        addCase(specialRegisterSwitch, Vector.of(5, 2), () -> {
                            set(enableInterrupts, select(firstRegisterValue, 0));
                        });
                        defaultCase(specialRegisterSwitch, () -> {
                            set(state, STATE_EXCEPTION);
                            set(exceptionCode, EXCEPTION_INVALID_INSTRUCTION);
                        });
                    });

                    // leave exception handler
                    addCase(subSwitch, Vector.of(4, 2), () -> {
                        set(state, STATE_START);
                        set(enableInterrupts, true);
                        set(pc, exceptionHandlerReturnAddress);
                    });

                    // invalid
                    defaultCase(subSwitch, () -> {
                        set(state, STATE_EXCEPTION);
                        set(exceptionCode, EXCEPTION_INVALID_INSTRUCTION);
                    });

                });
            });
        });
    }

    // sets all output signals in the bus interface
    private void buildBusInterfaceOutputs() {
        bus.enable = or(eq(state, STATE_MEM_ACCESS), eq(state, STATE_FETCH), and(earlyFetchStarted, not(earlyFetchFinished)));
        bus.write = and(eq(state, STATE_MEM_ACCESS), select(opcode, 3));
        bus.wordAddress = select(busAddressRegister, 31, 2);
        bus.writeMask = when(
            select(instructionRegister, 13),
            constant(4, 15),
            when(
                select(instructionRegister, 12),
                when(
                    select(busAddressRegister, 1),
                    constant(4, 0b1100),
                    constant(4, 0b0011)
                ),
                when(
                    select(busAddressRegister, 1),
                    when(
                        select(busAddressRegister, 0),
                        constant(4, 0b1000),
                        constant(4, 0b0100)
                    ),
                    when(
                        select(busAddressRegister, 0),
                        constant(4, 0b0010),
                        constant(4, 0b0001)
                    )
                )
            )
        );
        bus.writeData = when(
            select(instructionRegister, 13),
            registerReadValue,
            when(
                select(instructionRegister, 12),
                repeat(2, select(registerReadValue, 15, 0)),
                repeat(4, select(registerReadValue, 7, 0))
            )
        );
    }

    // returns a signal that provides the value from the bus.readDataRegister, shifted to adjust for word-misaligned
    // position and sign-extended or zero-extended to 32 bits.
    private VectorSignal buildAdjustedBusReadData() {
        var isUpperHalfword = select(busAddressRegister, 1);
        var isUpperByte = select(busAddressRegister, 0);
        var zeroExtend = select(instructionRegister, 14);

        // halfword
        var halfwordData = when(
            isUpperHalfword,
            select(busReadDataRegister, 31, 16),
            select(busReadDataRegister, 15, 0)
        );
        var halfwordExtension = when(zeroExtend, constant(16, 0), repeat(16, select(halfwordData, 15)));

        // byte
        var byteData = when(
            isUpperHalfword,
            when(isUpperByte, select(busReadDataRegister, 31, 24), select(busReadDataRegister, 23, 16)),
            when(isUpperByte, select(busReadDataRegister, 15, 8), select(busReadDataRegister, 7, 0))
        );
        var byteExtension = when(zeroExtend, constant(24, 0), repeat(24, select(byteData, 7)));

        // result
        return when(
            select(instructionRegister, 13),
            busReadDataRegister,
            when(
                select(instructionRegister, 12),
                concat(halfwordExtension, halfwordData),
                concat(byteExtension, byteData)
            )
        );
    }

    // Returns a signal that contains the result of the operator applied to its operands, after a sufficient number
    // of clock cycles. What "sufficient" means depends on the operation TODO
    private VectorSignal buildOperatorResult() {

        // state STATE_EXEC_OP_0 is used to perform this step
        var leftShiftPreparationResult = vectorRegister(32);
        var multiplierPreparationResultLowLow = vectorRegister(32);
        var multiplierPreparationResultLowHigh = vectorRegister(16);
        var multiplierPreparationResultHighLow = vectorRegister(16);
        on(clock, () -> {
            set(leftShiftPreparationResult, shiftLeft(leftOperand, select(rightOperand, 2, 0), constant(false)));
            set(multiplierPreparationResultLowLow, multiply(
                concat(constant(16, 0), select(firstRegisterValue, 15, 0)),
                concat(constant(16, 0), select(registerReadValue, 15, 0))
            ));
            set(multiplierPreparationResultLowHigh, multiply(
                firstRegisterValue.select(15, 0),
                registerReadValue.select(31, 16)
            ));
            set(multiplierPreparationResultHighLow, multiply(
                firstRegisterValue.select(31, 16),
                registerReadValue.select(15, 0)
            ));
        });

        // I am using a temporary result register for now. From/to constraints do not work well with Xilinx tools (one of the
        // worst UIs I have ever seen...) and since the CPU should eventually be pipelined, an extra register will be needed
        // anyway and will become a pipeline register at that point.
        var execOpTemporaryResultAddSub = vectorRegister(32);
        var execOpTemporaryResultShiftLeft = vectorRegister(32);
        var execOpTemporaryResultLessThan = bitRegister();
        var execOpTemporaryResultShiftRight = vectorRegister(32);
        var execOpTemporaryResultMul = vectorRegister(32);
        on(clock, () -> {
            set(execOpTemporaryResultAddSub, when(
                and(select(opcode, 3), select(instructionRegister, 30)),
                subtract(leftOperand, rightOperand),
                add(leftOperand, rightOperand)
            ));
            set(execOpTemporaryResultShiftLeft, when(
                select(rightOperand, 3),
                concat(select(leftShiftPreparationResult, 23, 0), constant(8, 0)),
                leftShiftPreparationResult
            ));
            set(execOpTemporaryResultLessThan, when(
                select(instructionRegister, 12),
                leftOperandLessThanRightOperandUnsigned,
                leftOperandLessThanRightOperandSigned
            ));
            set(execOpTemporaryResultShiftRight,
                shiftRight(leftOperand, select(rightOperand, 3, 0), rightShiftInBitRegister)
            );
            set(execOpTemporaryResultMul,
                add(
                    add(
                        multiplierPreparationResultLowLow,
                        concat(select(multiplierPreparationResultLowHigh, 15, 0), constant(16, 0))
                    ),
                    concat(select(multiplierPreparationResultHighLow, 15, 0), constant(16, 0))
                )
            );
        });

        //
        var execOpResult = vectorRegister(32);
        on(clock, () -> {
            when(operationIsMulDev, () -> {
                set(execOpResult, execOpTemporaryResultMul);
            }, () -> {
                var stlsuResult = concat(constant(31, 0), execOpTemporaryResultLessThan);
                VectorSwitchSignal result = new VectorSwitchSignal(select(instructionRegister, 14, 12), 32);
                result.addCase(Vector.of(3, 0), execOpTemporaryResultAddSub); // ADD/SUB
                result.addCase(Vector.of(3, 1), when(
                    select(rightOperand, 4),
                    concat(select(execOpTemporaryResultShiftLeft, 15, 0), constant(16, 0)),
                    execOpTemporaryResultShiftLeft
                )); // SLL
                result.addCase(Vector.of(3, 2), stlsuResult); // SLT
                result.addCase(Vector.of(3, 3), stlsuResult); // SLTU
                result.addCase(Vector.of(3, 4), xor(leftOperand, rightOperand)); // XOR
                result.addCase(Vector.of(3, 5), when(
                    select(rightOperand, 4),
                    concat(repeat(16, rightShiftInBitRegister), select(execOpTemporaryResultShiftRight, 31, 16)),
                    execOpTemporaryResultShiftRight
                )); // SRL, SRA
                result.addCase(Vector.of(3, 6), or(leftOperand, rightOperand)); // OR
                result.addCase(Vector.of(3, 7), and(leftOperand, rightOperand)); // AND
                set(execOpResult, result);
            });
        });

        return execOpResult;
    }

    // Returns a signal that contains the branch condition after a sufficient number of clock cycles.
    //
    // "Sufficient" means: the condition can be used in the logic preceding the clock edge two cycles after loading
    // the operand-holding registers.
    private BitSignal buildBranchCondition() {
        var partialCondition = bitRegister();
        on(clock, () -> set(partialCondition, when(
            select(instructionRegister, 14),
            when(select(instructionRegister, 13),
                leftOperandLessThanRightOperandUnsigned,
                leftOperandLessThanRightOperandSigned
            ),
            when(select(instructionRegister, 13), constant(false), eq(leftOperand, rightOperand))
        )));
        return xor(partialCondition, select(instructionRegister, 12));
    }

    private void buildRegisterFileLogic() {
        var registersAddress = when(
            eq(state, STATE_DECODE_AND_READ1),
            sourceRegisterIndex1,
            when(
                isOneOf(state, STATE_DECODE_AND_READ2, STATE_EXEC_OP_0, STATE_EXEC_OP_1, STATE_EXEC_OP_2),
                sourceRegisterIndex2,
                destinationRegisterIndex
            )
        );
        var write = isOneOf(state,
            STATE_EXEC_OP_3,
            STATE_EXEC_LUI,
            STATE_EXEC_AUIPC,
            STATE_EXEC_JAL,
            STATE_EXEC_JALR,
            STATE_MEM_EXTEND,
            STATE_WRITE_MISC_VALUE_TO_REGISTER
        );
        var writeData = new VectorSwitchSignal(state, 32);
        writeData.addCase(STATE_EXEC_OP_3, buildOperatorResult());
        writeData.addCase(STATE_EXEC_LUI, concat(select(instructionRegister, 31, 12), constant(12, 0)));
        writeData.addCase(STATE_EXEC_AUIPC, add(oldPc, concat(select(instructionRegister, 31, 12), constant(12, 0))));
        writeData.addCase(STATE_EXEC_JAL, pc);
        writeData.addCase(STATE_EXEC_JALR, pc);
        writeData.addCase(STATE_MEM_EXTEND, buildAdjustedBusReadData());
        writeData.setDefaultSignal(miscRegisterWriteValue);
        on(clock, () -> {
            when(and(neq(state, STATE_MEM_COMPUTE_ADDRESS), neq(state, STATE_MEM_ACCESS)), () -> {
                set(registerReadValue, select(registers, registersAddress));
                when(and(write, neq(destinationRegisterIndex, 0)), () -> {
                    set(select(registers, registersAddress), writeData);
                });
            });
        });
    }

}
