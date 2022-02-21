package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.library.procedural.statement.Statement;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;

/**
 * A signal whose value can be set by high-level models. This is meant as a bridge to simulate high-level models and
 * RTL models together.
 *
 * NOTE: The signal must NOT be changed during the computeNextState() phase of a clock cycle. Use
 * {@link SimulatedRegister} to change simulated signals synchronously to a clock edge.
 *
 * Unlike {@link ProceduralRegister}, this class does not work together with {@link Statement}. This makes it
 * easier to use, but it cannot be synthesized. Also, changes to the value are reflected directly, so it is not
 * possible to update multiple settable signals synchronously to a clock edge.
 *
 * Using this signal in a way that is not relevant to synthesis, such as a simulation replacement signal of instance
 * ports, is allowed.
 */
public abstract class SimulatedSettableSignal extends DesignItem implements Signal, DesignItemOwned {

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
