package name.martingeisse.esdk.core.library.memory;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.Matrix;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * This is a simplified wrapper around a memory with a single asynchronous read port.
 */
public final class LookupTable extends DesignItem implements VectorSignal, DesignItemOwned {

	private final Memory memory;
	private final AsynchronousMemoryReadPort port;

	public LookupTable(Matrix matrix, VectorSignal indexSignal) {
		this(new Memory(matrix), indexSignal);
	}

	public LookupTable(int width, VectorSignal indexSignal) {
		this(new Memory(1 << indexSignal.getWidth(), width), indexSignal);
	}

	private LookupTable(Memory memory, VectorSignal indexSignal) {
		this.memory = memory;
		this.port = memory.createAsynchronousReadPort();
		this.port.setAddressSignal(indexSignal);
	}

	public Memory getMemory() {
		return memory;
	}

	public Matrix getMatrix() {
		return memory.getMatrix();
	}

	@Override
	public int getWidth() {
		return port.getReadDataSignal().getWidth();
	}

	@Override
	public Vector getValue() {
		return port.getReadDataSignal().getValue();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		port.getReadDataSignal().printVerilogImplementationExpression(out);
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
