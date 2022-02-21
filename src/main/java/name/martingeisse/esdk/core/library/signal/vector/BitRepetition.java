package name.martingeisse.esdk.core.library.signal.vector;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 *
 */
public final class BitRepetition extends DesignItem implements VectorSignal, DesignItemOwned {

	private final BitSignal bitSignal;
	private final int repetitions;

	public BitRepetition(BitSignal bitSignal, int repetitions) {
		this.bitSignal = bitSignal;
		this.repetitions = repetitions;
	}

	public BitSignal getBitSignal() {
		return bitSignal;
	}

	public int getRepetitions() {
		return repetitions;
	}

	@Override
	public int getWidth() {
		return repetitions;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		return Vector.repeat(repetitions, bitSignal.getValue());
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
		out.printSignal(bitSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
		out.print("}}");
	}

}
