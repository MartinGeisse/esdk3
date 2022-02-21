package name.martingeisse.esdk.core.library.signal.vector;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public final class VectorRepetition extends DesignItem implements VectorSignal, DesignItemOwned {

	private final VectorSignal vectorSignal;
	private final int repetitions;

	public VectorRepetition(VectorSignal vectorSignal, int repetitions) {
		this.vectorSignal = vectorSignal;
		this.repetitions = repetitions;
	}

	public VectorSignal getVectorSignal() {
		return vectorSignal;
	}

	public int getRepetitions() {
		return repetitions;
	}

	@Override
	public int getWidth() {
		return vectorSignal.getWidth() * repetitions;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		Vector single = vectorSignal.getValue();
		Vector result = Vector.of(0, 0);
		for (int i = 0; i < repetitions; i++) {
			result = result.concat(single);
		}
		return result;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.print('{');
		out.print(repetitions);
		out.print('{');
		out.printSignal(vectorSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print("}}");
	}

}
