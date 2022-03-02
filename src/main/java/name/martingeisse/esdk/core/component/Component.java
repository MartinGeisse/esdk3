package name.martingeisse.esdk.core.component;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.library.procedural.*;
import name.martingeisse.esdk.core.library.procedural.statement.*;
import name.martingeisse.esdk.core.library.signal.*;
import name.martingeisse.esdk.core.library.signal.connector.BitConnector;
import name.martingeisse.esdk.core.library.signal.connector.ClockConnector;
import name.martingeisse.esdk.core.library.signal.connector.VectorConnector;
import name.martingeisse.esdk.core.library.signal.mux.ConditionalBitOperation;
import name.martingeisse.esdk.core.library.signal.mux.ConditionalVectorOperation;
import name.martingeisse.esdk.core.library.signal.operation.*;
import name.martingeisse.esdk.core.library.signal.vector.*;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.SignalUtil;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * A re-usable hardware component written in ESDK's domain-specific hardware description language.
 *
 * This class acts as a "DSL provider" and "DSL enforcer" for the embedded DSL used to write HDL code. No functionality
 * is provided with respect to the generated hardware, nor to the generated low-level HDL code -- that is, everything
 * that can be done with this class can be done without it. Instead, this class provides helper methods to build
 * the implementation objects, and (TODO) it uses reflection to (1) reduce boilerplate code and make
 * some of the tedious work around building a HDL model implicit, and (2) enforce programming patterns that make the
 * DSL more useful and avoid common pitfalls.
 *
 * Intended usage patterns and conventions:
 * - signal input ports should be represented by public final fields of type BitSignalConnector or
 *   VectorSignalConnector, initialized by the constructor using inClock(), inBit() or inVector().
 * - signal output ports should be represented by public final fields of any signal type, initialized by the
 *   constructor.
 * - The constructor should build the implementation logic, using the input port signal connectors as inputs. It should
 *   set all output ports.
 * - The constructor should not take signals as parameters, unless there are very few of them and their meaning is
 *   clear even when using (positional) parameters. Even then, doing so forces the caller to have the input signals
 *   available at construction time, while normal input ports can be connected at a later time, so ports are preferred.
 *
 * TODO a signal connector being a public field of a Component should trigger a specialized error output handling,
 * assuming that the code that created the instance is responsible for connecting the signal. Alternatively, the
 * port factory methods should install this with the connector (latter = better).
 *
 */
public abstract class Component extends DesignItem {

    @Override
    protected void finalizeConstructionAfterValidation() {
        // TODO build design hierarchy
        // Signal.analyzeSignalUsage
    }

    @Override
    public VerilogContribution getVerilogContribution() {
        // the contents of the component contribute to Verilog, but the component itself doesn't (for now)
        return EmptyVerilogContribution.INSTANCE;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // component DSL
    // ----------------------------------------------------------------------------------------------------------------

    // region ports

    // TODO set item names based on field names (reflection)
    // TODO set hierarchy parent

    public final ClockConnector inClock() {
        return new ClockConnector();
    }

    public final BitConnector inBit() {
        return new BitConnector();
    }

    public final VectorConnector inVector(int width) {
        return new VectorConnector(width);
    }

    // endregion ports

    // region signals

    // region constants

    public final BitSignal constant(boolean value) {
        return new BitConstant(value);
    }

    public final VectorSignal constant(Vector value) {
        return new VectorConstant(value);
    }

    public final VectorSignal constant(int width, long value) {
        return constant(Vector.of(width, value));
    }

    // endregion constants

    // region unary operations

    public final BitSignal not(BitSignal signal) {
        return new BitNotOperation(signal);
    }

    public final VectorSignal not(VectorSignal signal) {
        return new VectorNotOperation(signal);
    }

    // endregion unary operations

    // region binary operations

    // region generic

    public final BitSignal operator(BitSignal x, BitOperation.Operator operator, BitSignal y) {
        //noinspection SuspiciousNameCombination
        return new BitOperation(operator, x, y);
    }

    public final VectorSignal operator(VectorSignal x, VectorOperation.Operator operator, VectorSignal y) {
        //noinspection SuspiciousNameCombination
        return new VectorOperation(operator, x, y);
    }

    public final VectorSignal operator(VectorSignal x, VectorOperation.Operator operator, Vector y) {
        return operator(x, operator, constant(y));
    }

    public final VectorSignal operator(VectorSignal x, VectorOperation.Operator operator, int y) {
        return operator(x, operator, constant(x.getWidth(), y));
    }

    // endregion generic

    // region and

    public final BitSignal and(BitSignal... signals) {
        return reduce(true, BitOperation.Operator.AND, signals);
    }

    public final VectorSignal and(VectorSignal x, VectorSignal y) {
        return operator(x, VectorOperation.Operator.AND, y);
    }

    public final VectorSignal and(VectorSignal x, Vector y) {
        return operator(x, VectorOperation.Operator.AND, y);
    }

    public final VectorSignal and(VectorSignal x, int y) {
        return operator(x, VectorOperation.Operator.AND, y);
    }

    // endregion and

    // region or

    public final BitSignal or(BitSignal... signals) {
        return reduce(false, BitOperation.Operator.OR, signals);
    }

    public final VectorSignal or(VectorSignal x, VectorSignal y) {
        return operator(x, VectorOperation.Operator.OR, y);
    }

    public final VectorSignal or(VectorSignal x, Vector y) {
        return operator(x, VectorOperation.Operator.OR, y);
    }

    public final VectorSignal or(VectorSignal x, int y) {
        return operator(x, VectorOperation.Operator.OR, y);
    }

    // endregion or

    // region xor

    public final BitSignal xor(BitSignal... signals) {
        return reduce(false, BitOperation.Operator.XOR, signals);
    }

    public final VectorSignal xor(VectorSignal x, VectorSignal y) {
        return operator(x, VectorOperation.Operator.XOR, y);
    }

    public final VectorSignal xor(VectorSignal x, Vector y) {
        return operator(x, VectorOperation.Operator.XOR, y);
    }

    public final VectorSignal xor(VectorSignal x, int y) {
        return operator(x, VectorOperation.Operator.XOR, y);
    }

    // endregion xor

    // region add

    public final VectorSignal add(VectorSignal x, VectorSignal y) {
        return operator(x, VectorOperation.Operator.ADD, y);
    }

    public final VectorSignal add(VectorSignal x, Vector y) {
        return operator(x, VectorOperation.Operator.ADD, y);
    }

    public final VectorSignal add(VectorSignal x, int y) {
        return operator(x, VectorOperation.Operator.ADD, y);
    }

    // endregion add

    // region subtract

    public final VectorSignal subtract(VectorSignal x, VectorSignal y) {
        return operator(x, VectorOperation.Operator.SUBTRACT, y);
    }

    public final VectorSignal subtract(VectorSignal x, Vector y) {
        return operator(x, VectorOperation.Operator.SUBTRACT, y);
    }

    public final VectorSignal subtract(VectorSignal x, int y) {
        return operator(x, VectorOperation.Operator.SUBTRACT, y);
    }

    // endregion subtract

    // region multiply

    public final VectorSignal multiply(VectorSignal x, VectorSignal y) {
        return operator(x, VectorOperation.Operator.MULTIPLY, y);
    }

    public final VectorSignal multiply(VectorSignal x, Vector y) {
        return operator(x, VectorOperation.Operator.MULTIPLY, y);
    }

    public final VectorSignal multiply(VectorSignal x, int y) {
        return operator(x, VectorOperation.Operator.MULTIPLY, y);
    }

    // endregion multiply

    // region conditionals (muxes)

    public final BitSignal when(BitSignal condition, BitSignal onTrue, BitSignal onFalse) {
        return new ConditionalBitOperation(condition, onTrue, onFalse);
    }

    public final VectorSignal when(BitSignal condition, VectorSignal onTrue, VectorSignal onFalse) {
        return new ConditionalVectorOperation(condition, onTrue, onFalse);
    }

    // endregion

    // region shifting

    public final VectorSignal shiftLeft(VectorSignal shifted, VectorSignal amount, BitSignal in) {
        int shiftLimit = 1 << amount.getWidth(); // (max shift amount) + 1
        VectorSignal extended = concat(shifted, repeat(shiftLimit, in));
        VectorSignal shiftOperation = new ShiftOperation(ShiftOperation.Direction.LEFT, extended, amount);
        return select(shiftOperation, shiftOperation.getWidth() - 1, shiftLimit);
    }

    public final VectorSignal shiftRight(VectorSignal shifted, VectorSignal amount, BitSignal in) {
        int shiftLimit = 1 << amount.getWidth(); // (max shift amount) + 1
        VectorSignal extended = concat(repeat(shiftLimit, in), shifted);
        VectorSignal shiftOperation = new ShiftOperation(ShiftOperation.Direction.RIGHT, extended, amount);
        return select(shiftOperation, shifted.getWidth() - 1, 0);
    }

    // endregion

    // region generic comparison

    public final BitSignal compare(VectorSignal x, VectorComparison.Operator operator, VectorSignal y) {
        //noinspection SuspiciousNameCombination
        return new VectorComparison(operator, x, y);
    }

    public final BitSignal compare(VectorSignal x, VectorComparison.Operator operator, Vector y) {
        return compare(x, operator, constant(y));
    }

    public final BitSignal compare(VectorSignal x, VectorComparison.Operator operator, int y) {
        return compare(x, operator, constant(x.getWidth(), y));
    }

    // endregion

    // region compare/equal

    public final BitSignal eq(BitSignal... signals) {
        BitSignal partialResult = constant(true);
        if (signals.length == 0) {
            return partialResult;
        }
        for (int i = 1; i < signals.length; i++) {
            partialResult = checkSameDesign(partialResult.and(signals[i].compareEqual(signals[0])));
        }
        return partialResult;
    }

    public final BitSignal eq(VectorSignal x, VectorSignal y) {
        return compare(x, VectorComparison.Operator.EQUAL, y);
    }

    public final BitSignal eq(VectorSignal x, Vector y) {
        return compare(x, VectorComparison.Operator.EQUAL, y);
    }

    public final BitSignal eq(VectorSignal x, int y) {
        return compare(x, VectorComparison.Operator.EQUAL, y);
    }

    public final BitSignal isOneOf(VectorSignal x, VectorSignal... ys) {
        BitSignal partialResult = constant(false);
        if (ys.length == 0) {
            return partialResult;
        }
        for (VectorSignal y : ys) {
            partialResult = checkSameDesign(partialResult.or(eq(x, y)));
        }
        return partialResult;
    }

    public final BitSignal isOneOf(VectorSignal x, Vector... ys) {
        BitSignal partialResult = constant(false);
        if (ys.length == 0) {
            return partialResult;
        }
        for (Vector y : ys) {
            partialResult = checkSameDesign(partialResult.or(eq(x, y)));
        }
        return partialResult;
    }

    public final BitSignal isOneOf(VectorSignal x, int... ys) {
        BitSignal partialResult = constant(false);
        if (ys.length == 0) {
            return partialResult;
        }
        for (int y : ys) {
            partialResult = checkSameDesign(partialResult.or(eq(x, y)));
        }
        return partialResult;
    }

    // endregion compare/equal

    // region compare/not equal

    public final BitSignal neq(BitSignal x, BitSignal y) {
        return xor(x, y);
    }

    public final BitSignal neq(VectorSignal x, VectorSignal y) {
        return compare(x, VectorComparison.Operator.NOT_EQUAL, y);
    }

    public final BitSignal neq(VectorSignal x, Vector y) {
        return compare(x, VectorComparison.Operator.NOT_EQUAL, y);
    }

    public final BitSignal neq(VectorSignal x, int y) {
        return compare(x, VectorComparison.Operator.NOT_EQUAL, y);
    }

    // endregion compare/not equal

    // region compare/less/greater

    public final BitSignal lessThan(VectorSignal x, VectorSignal y) {
        return compare(x, VectorComparison.Operator.LESS_THAN, y);
    }

    public final BitSignal lessThan(VectorSignal x, Vector y) {
        return compare(x, VectorComparison.Operator.LESS_THAN, y);
    }

    public final BitSignal lessThan(VectorSignal x, int y) {
        return compare(x, VectorComparison.Operator.LESS_THAN, y);
    }

    public final BitSignal lessThanEq(VectorSignal x, VectorSignal y) {
        return compare(x, VectorComparison.Operator.LESS_THAN_OR_EQUAL, y);
    }

    public final BitSignal lessThanEq(VectorSignal x, Vector y) {
        return compare(x, VectorComparison.Operator.LESS_THAN_OR_EQUAL, y);
    }

    public final BitSignal lessThanEq(VectorSignal x, int y) {
        return compare(x, VectorComparison.Operator.LESS_THAN_OR_EQUAL, y);
    }

    public final BitSignal greaterThan(VectorSignal x, VectorSignal y) {
        return compare(x, VectorComparison.Operator.GREATER_THAN, y);
    }

    public final BitSignal greaterThan(VectorSignal x, Vector y) {
        return compare(x, VectorComparison.Operator.GREATER_THAN, y);
    }

    public final BitSignal greaterThan(VectorSignal x, int y) {
        return compare(x, VectorComparison.Operator.GREATER_THAN, y);
    }

    public final BitSignal greaterThanEq(VectorSignal x, VectorSignal y) {
        return compare(x, VectorComparison.Operator.GREATER_THAN_OR_EQUAL, y);
    }

    public final BitSignal greaterThanEq(VectorSignal x, Vector y) {
        return compare(x, VectorComparison.Operator.GREATER_THAN_OR_EQUAL, y);
    }

    public final BitSignal greaterThanEq(VectorSignal x, int y) {
        return compare(x, VectorComparison.Operator.GREATER_THAN_OR_EQUAL, y);
    }

    // endregion compare/less/greater

    // region reduction

    public final BitSignal andReduce(VectorSignal signal) {
        return eq(signal, repeat(signal.getWidth(), true));
    }

    public final BitSignal orReduce(VectorSignal signal) {
        return neq(signal, repeat(signal.getWidth(), false));
    }

    public final BitSignal xorReduce(VectorSignal signal) {
        return xor(SignalUtil.getAllBits(signal));
    }

    // endregion reduction

    // endregion binary operations

    // region (de-)vectorization

    public final VectorSignal concat(Signal... signals) {
        return new Concatenation(signals);
    }

    public final VectorSignal repeat(int repetitions, boolean value) {
        return new BitRepetition(constant(value), repetitions);
    }

    public final VectorSignal repeat(int repetitions, BitSignal signal) {
        return new BitRepetition(signal, repetitions);
    }

    public final VectorSignal repeat(int repetitions, Vector value) {
        return new VectorRepetition(constant(value), repetitions);
    }

    public final VectorSignal repeat(int repetitions, VectorSignal signal) {
        return new VectorRepetition(signal, repetitions);
    }

    public final BitSignal select(VectorSignal container, VectorSignal index) {
        return new IndexSelection(container, index);
    }

    public final BitSignal select(VectorSignal container, int index) {
        return new ConstantIndexSelection(container, index);
    }

    public final VectorSignal select(VectorSignal container, int from, int to) {
        return new RangeSelection(container, from, to);
    }

    public final VectorSignal select(ProceduralMemory container, VectorSignal index) {
        return new ProceduralMemoryIndexSelection(container, index);
    }

    public final VectorSignal select(ProceduralMemory container, int index) {
        return new ProceduralMemoryConstantIndexSelection(container, index);
    }

    // endregion

    // region helpers

    private BitSignal reduce(boolean start, BitOperation.Operator operator, BitSignal... signals) {
        if (signals.length == 0) {
            return constant(start);
        }
        BitSignal partialResult = signals[0];
        for (int i = 1; i < signals.length; i++) {
            partialResult = new BitOperation(operator, partialResult, signals[i]);
        }
        return partialResult;
    }

    // endregion helpers

    // endregion signals

    // region clocked blocks

    // region procedural registers and memories

    public final ProceduralBitRegister bitRegister() {
        return new ProceduralBitRegister();
    }

    public final ProceduralBitRegister bitRegister(boolean initialValue) {
        return new ProceduralBitRegister(initialValue);
    }

    public final ProceduralVectorRegister vectorRegister(int width) {
        return new ProceduralVectorRegister(width);
    }

    public final ProceduralVectorRegister vectorRegister(int width, Vector initialValue) {
        return new ProceduralVectorRegister(width, initialValue);
    }

    /**
     * This method derives the register width from the initial value.
     */
    public final ProceduralVectorRegister vectorRegister(Vector initialValue) {
        return new ProceduralVectorRegister(initialValue.getWidth(), initialValue);
    }

    public final ProceduralVectorRegister vectorRegister(int width, int initialValue) {
        return new ProceduralVectorRegister(width, Vector.of(width, initialValue));
    }

    public final ProceduralMemory memory(int rowCount, int columnCount) {
        return new ProceduralMemory(rowCount, columnCount);
    }

    /**
     * The size of the memory is set to the size of the matrix.
     *
     * Note: The matrix is mutable and is shared by the memory, not copied!
     */
    public final ProceduralMemory memory(Matrix matrix) {
        return new ProceduralMemory(matrix);
    }

    // endregion registers and memories

    // region clocked blocks and statement DSL framework

    private StatementSequence statementSequenceBeingBuilt = null;

    /**
     * This is the main entry point to the statement DSL. The statement DSL methods are valid inside the
     * provided body, and they always refer to the sequence of statements currently being built, including
     * nested blocks.
     */
    public final ClockedBlock on(ClockSignal clockSignal, Runnable body) {
        ClockedBlock clockedBlock = new ClockedBlock(clockSignal);
        nestedStatements(clockedBlock.getStatements(), body);
        return clockedBlock;
    }

    public final <T extends Statement> T dslStatement(T statement) {
        if (statementSequenceBeingBuilt == null) {
            throw new IllegalStateException("cannot build statements outside a statement-building body");
        }
        statementSequenceBeingBuilt.addStatement(statement);
        return statement;
    }

    private void nestedStatements(StatementSequence sequence, Runnable body) {
        StatementSequence previousSequence = statementSequenceBeingBuilt;
        try {
            statementSequenceBeingBuilt = sequence;
            body.run();
        } finally {
            statementSequenceBeingBuilt = previousSequence;
        }
    }

    // endregion clocked blocks and statement DSL framework

    // region primitive statements

    public final BitAssignment set(BitSignal destination, BitSignal source) {
        return dslStatement(new BitAssignment(destination, source));
    }

    public final BitAssignment set(BitSignal destination, boolean value) {
        return dslStatement(new BitAssignment(destination, constant(value)));
    }

    public final VectorAssignment set(VectorSignal destination, VectorSignal source) {
        return dslStatement(new VectorAssignment(destination, source));
    }

    public final VectorAssignment set(VectorSignal destination, Vector value) {
        return dslStatement(new VectorAssignment(destination, constant(value)));
    }

    public final VectorAssignment set(VectorSignal destination, long value) {
        return dslStatement(new VectorAssignment(destination, constant(destination.getWidth(), value)));
    }

    public final WhenStatement when(BitSignal condition, Runnable thenBody) {
        WhenStatement whenStatement = dslStatement(new WhenStatement(condition));
        nestedStatements(whenStatement.getThenBranch(), thenBody);
        return whenStatement;
    }

    public final WhenStatement when(BitSignal condition, Runnable thenBody, Runnable elseBody) {
        WhenStatement whenStatement = dslStatement(new WhenStatement(condition));
        nestedStatements(whenStatement.getThenBranch(), thenBody);
        nestedStatements(whenStatement.getOtherwiseBranch(), elseBody);
        return whenStatement;
    }

    // region if-elseif chains

    public final void when(
            BitSignal condition1,
            Runnable thenBody1,
            BitSignal condition2,
            Runnable thenBody2
    ) {
        when(condition1, thenBody1, () -> when(condition2, thenBody2));
    }

    public final void when(
            BitSignal condition1,
            Runnable thenBody1,
            BitSignal condition2,
            Runnable thenBody2,
            Runnable elseBody
    ) {
        when(condition1, thenBody1, () -> when(condition2, thenBody2, elseBody));
    }

    public final void when(
            BitSignal condition1,
            Runnable thenBody1,
            BitSignal condition2,
            Runnable thenBody2,
            BitSignal condition3,
            Runnable thenBody3
    ) {
        when(condition1, thenBody1, () -> when(condition2, thenBody2, condition3, thenBody3));
    }

    public final void when(
            BitSignal condition1,
            Runnable thenBody1,
            BitSignal condition2,
            Runnable thenBody2,
            BitSignal condition3,
            Runnable thenBody3,
            Runnable elseBody
    ) {
        when(condition1, thenBody1, () -> when(condition2, thenBody2, condition3, thenBody3, elseBody));
    }

    public final void when(
            BitSignal condition1,
            Runnable thenBody1,
            BitSignal condition2,
            Runnable thenBody2,
            BitSignal condition3,
            Runnable thenBody3,
            BitSignal condition4,
            Runnable thenBody4
    ) {
        when(condition1, thenBody1, () -> when(condition2, thenBody2, condition3, thenBody3, condition4, thenBody4));
    }

    public final void when(
            BitSignal condition1,
            Runnable thenBody1,
            BitSignal condition2,
            Runnable thenBody2,
            BitSignal condition3,
            Runnable thenBody3,
            BitSignal condition4,
            Runnable thenBody4,
            Runnable elseBody
    ) {
        when(condition1, thenBody1, () -> when(condition2, thenBody2, condition3, thenBody3, condition4, thenBody4, elseBody));
    }

    // endregion

    // TODO switch/case. DSL syntax not yet clear

    public final void addCase(SwitchStatement switchStatement, Vector selectorValue, Runnable body) {
        StatementSequence sequence = switchStatement.addCase(selectorValue);
        nestedStatements(sequence, body);
    }

    public final void addCase(SwitchStatement switchStatement, Vector[] selectorValues, Runnable body) {
        StatementSequence sequence = switchStatement.addCase(selectorValues);
        nestedStatements(sequence, body);
    }

    public final void defaultCase(SwitchStatement switchStatement, Runnable body) {
        nestedStatements(switchStatement.getDefaultBranch(), body);
    }

//    public final SwitchStatement switchOn(VectorSignal selector) {
//        SwitchStatement switchStatement = new SwitchStatement(selector);
//        addStatement(switchStatement);
//        return switchStatement;
//    }

    // endregion primitive statements

    // region convenience statements

    public final VectorAssignment inc(VectorSignal destination, VectorSignal deltaSignal) {
        return set(destination, add(destination, deltaSignal));
    }

    public final VectorAssignment inc(VectorSignal destination, Vector deltaValue) {
        return inc(destination, constant(deltaValue));
    }

    public final VectorAssignment inc(VectorSignal destination, long deltaValue) {
        return inc(destination, constant(destination.getWidth(), deltaValue));
    }

    public final VectorAssignment inc(VectorSignal destination) {
        return inc(destination, 1);
    }

    public final VectorAssignment dec(VectorSignal destination, VectorSignal deltaSignal) {
        return set(destination, subtract(destination, deltaSignal));
    }

    public final VectorAssignment dec(VectorSignal destination, Vector deltaValue) {
        return dec(destination, constant(deltaValue));
    }

    public final VectorAssignment dec(VectorSignal destination, long deltaValue) {
        return dec(destination, constant(destination.getWidth(), deltaValue));
    }

    public final VectorAssignment dec(VectorSignal destination) {
        return dec(destination, 1);
    }

    // endregion convenience statements

    // endregion clocked blocks

}
