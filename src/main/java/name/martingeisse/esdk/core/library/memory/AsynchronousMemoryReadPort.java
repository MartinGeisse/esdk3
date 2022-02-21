package name.martingeisse.esdk.core.library.memory;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.tools.validation.ValidationContext;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * Asynchronous read ports are special because they do not need a clock at all.
 */
public final class AsynchronousMemoryReadPort extends DesignItem implements MemoryPort, DesignItemOwned {

	private final Memory memory;
	private final VectorSignal readDataSignal;
	private VectorSignal addressSignal;

	AsynchronousMemoryReadPort(Memory memory) {
		this.memory = memory;
		this.readDataSignal = new ReadDataSignal();
	}

	public Memory getMemory() {
		return memory;
	}

	public VectorSignal getReadDataSignal() {
		return readDataSignal;
	}

	public VectorSignal getAddressSignal() {
		return addressSignal;
	}

	public void setAddressSignal(VectorSignal addressSignal) {
		if (addressSignal.getWidth() > 30) {
			throw new IllegalArgumentException("address width of " + addressSignal.getWidth() + " not supported");
		}
		if (1 << addressSignal.getWidth() > memory.getMatrix().getRowCount()) {
			throw new IllegalArgumentException("address width of " + addressSignal.getWidth() +
				" is too large for matrix row count " + memory.getMatrix().getRowCount());
		}
		this.addressSignal = addressSignal;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void validate(ValidationContext context) {
		if (addressSignal == null) {
			context.reportError("no address signal");
		}
	}

	@Override
	public void prepareSynthesis(SynthesisPreparationContext context) {
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		consumer.consumeSignalUsage(addressSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

	@Override
	public void printDeclarations(VerilogWriter out) {
	}

	@Override
	public void printImplementation(VerilogWriter out) {
	}

	final class ReadDataSignal extends DesignItem implements VectorSignal, DesignItemOwned {

		ReadDataSignal() {
			setName("memoryAsyncRead");
		}

		@Override
		public int getWidth() {
			return memory.getMatrix().getColumnCount();
		}

		@Override
		public Vector getValue() {
			return memory.getMatrix().getRow(addressSignal.getValue().getAsUnsignedInt());
		}

		@Override
		public VerilogContribution getVerilogContribution() {
			return new EmptyVerilogContribution();
		}

		@Override
		public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
			out.printSignal(memory.getMemorySignal(), VerilogExpressionNesting.ALL);
			out.print('[');
			out.printSignal(addressSignal, VerilogExpressionNesting.ALL);
			out.print(']');
		}

	}

}
