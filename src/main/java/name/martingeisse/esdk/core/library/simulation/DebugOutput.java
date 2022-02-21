package name.martingeisse.esdk.core.library.simulation;

import name.martingeisse.esdk.core.library.clocked.ClockedItem;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.EmptyVerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;

/**
 *
 */
public class DebugOutput extends ClockedItem {

	private final VectorSignal dataSignal;
	private final BitSignal enableSignal;
	private final Callback callback;
	private int data;
	private boolean enable;

	public DebugOutput(ClockSignal clockSignal, VectorSignal dataSignal, BitSignal enableSignal, Callback callback) {
		super(clockSignal);
		this.dataSignal = dataSignal;
		this.enableSignal = enableSignal;
		this.callback = callback;
	}

	@Override
	public void computeNextState() {
		data = dataSignal.getValue().getAsSignedInt();
		enable = enableSignal.getValue();
	}

	@Override
	public void updateState() {
		if (enable) {
			callback.handle(data);
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return EmptyVerilogContribution.INSTANCE;
	}

	public interface Callback {
		void handle(int value);
	}

}
