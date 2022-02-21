package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.clocked.ClockedItem;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 * A register whose input can be set via a setter method during simulation. This is useful to implement the RTL
 * interface of a simulated component.
 *
 * Unlike {@link SimulatedSettableSignal}, this class is used to update a signal synchronously to a clock edge.
 *
 * Usage: call setNextValue() during computeNextState().
 *
 * Using this signal in a way that is not relevant to synthesis, such as a simulation replacement signal of instance
 * ports, is allowed.
 */
public abstract class SimulatedRegister extends ClockedItem implements Signal {

	SimulatedRegister(ClockSignal clockSignal) {
		super(clockSignal);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public final VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

	@Override
	public final void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot print an implementation expression for " + this);
	}

}
