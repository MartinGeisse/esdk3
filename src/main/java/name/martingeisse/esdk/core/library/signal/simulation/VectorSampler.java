package name.martingeisse.esdk.core.library.signal.simulation;

import name.martingeisse.esdk.core.library.clocked.ClockedItem;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.util.vector.Vector;

/**
 * Same as {@link BitSampler} but for vector signals. See that class for an explanation.
 */
public final class VectorSampler extends ClockedItem {

	private final VectorSignal signal;
	private Vector sample;

	public VectorSampler(ClockSignal clockSignal, VectorSignal signal) {
		super(clockSignal);
		this.signal = signal;
	}

	public int getWidth() {
		return signal.getWidth();
	}

	public Vector getSample() {
		return sample;
	}

	// ----------------------------------------------------------------------------------------------------------------
	// simulation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public void initializeSimulation() {
		this.sample = Vector.of(getWidth(), 0);
	}

	@Override
	public void computeNextState() {
		sample = signal.getValue();
	}

	@Override
	public void updateState() {
	}

	// ----------------------------------------------------------------------------------------------------------------
	// Verilog generation
	// ----------------------------------------------------------------------------------------------------------------

	@Override
	public VerilogContribution getVerilogContribution() {
		return new EmptyVerilogContribution();
	}

}
