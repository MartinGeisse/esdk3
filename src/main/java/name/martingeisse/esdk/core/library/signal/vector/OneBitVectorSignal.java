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
public final class OneBitVectorSignal extends DesignItem implements VectorSignal, DesignItemOwned {

	private final BitSignal bitSignal;

	public OneBitVectorSignal(BitSignal bitSignal) {
		this.bitSignal = bitSignal;
	}

	public BitSignal getBitSignal() {
		return bitSignal;
	}

	@Override
	public int getWidth() {
		return 1;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public Vector getValue() {
		return Vector.of(1, bitSignal.getValue() ? 1 : 0);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return bitSignal.compliesWith(nesting);
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		out.printSignal(bitSignal, VerilogExpressionNesting.SIGNALS_AND_CONSTANTS);
	}

}
