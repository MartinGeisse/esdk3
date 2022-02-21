package name.martingeisse.esdk.core.library.memory;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.*;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class Memory extends DesignItem implements DesignItemOwned {

	private final Matrix matrix;
	private final List<MemoryPort> ports;
	private final Signal memorySignal;

	public Memory(Matrix matrix) {
		this.matrix = matrix;
		this.ports = new ArrayList<>();
		this.memorySignal = new Signal() {

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
				throw newSynthesisNotSupportedException();
			}

			@Override
			public DesignItem getDesignItem() {
				return Memory.this;
			}

		};
	}

	public Memory(int rowCount, int columnCount) {
		this(new Matrix(rowCount, columnCount));
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public Iterable<MemoryPort> getPorts() {
		return ports;
	}

	public AsynchronousMemoryReadPort createAsynchronousReadPort() {
		AsynchronousMemoryReadPort port = new AsynchronousMemoryReadPort(this);
		ports.add(port);
		return port;
	}

	public SynchronousMemoryPort createSynchronousPort(Clock clock,
													   SynchronousMemoryPort.ReadSupport readSupport) {
		return createSynchronousPort(clock, readSupport, SynchronousMemoryPort.WriteSupport.NONE,
			SynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
	}

	public SynchronousMemoryPort createSynchronousPort(Clock clock,
													   SynchronousMemoryPort.WriteSupport writeSupport) {
		return createSynchronousPort(clock, SynchronousMemoryPort.ReadSupport.NONE, writeSupport,
			SynchronousMemoryPort.ReadWriteInteractionMode.READ_FIRST);
	}

	public SynchronousMemoryPort createSynchronousPort(Clock clock,
													   SynchronousMemoryPort.ReadSupport readSupport,
													   SynchronousMemoryPort.WriteSupport writeSupport,
													   SynchronousMemoryPort.ReadWriteInteractionMode readWriteInteractionMode) {
		SynchronousMemoryPort port = new SynchronousMemoryPort(clock, this, readSupport, writeSupport, readWriteInteractionMode);
		ports.add(port);
		return port;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	Signal getMemorySignal() {
		return memorySignal;
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			private String memoryName;

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				memoryName = context.declareSignal(memorySignal, VerilogSignalDeclarationKeyword.NONE, false);
				VerilogUtil.generateMif(context.getAuxiliaryFileFactory(), memoryName + ".mif", getMatrix());
				for (MemoryPort port : ports) {
					port.prepareSynthesis(context);
				}
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
				for (MemoryPort port : ports) {
					port.analyzeSignalUsage(consumer);
				}
			}

			@Override
			public void printDeclarations(VerilogWriter out) {
				Matrix matrix = getMatrix();
				out.println("reg [" + (matrix.getColumnCount() - 1) + ":0] " + memoryName + " [" +
					(matrix.getRowCount() - 1) + ":0];");
				for (MemoryPort port : ports) {
					port.printDeclarations(out);
				}
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				Matrix matrix = getMatrix();
				out.println("initial $readmemh(\"" + memoryName + ".mif\", " + memoryName + ", 0, " +
					(matrix.getRowCount() - 1) + ");");
				for (MemoryPort port : ports) {
					port.printImplementation(out);
				}
			}

		};
	}

}
