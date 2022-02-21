package name.martingeisse.esdk.core.library.memory;

import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;

/**
 *
 */
public interface MemoryPort extends DesignItemOwned {

	void prepareSynthesis(SynthesisPreparationContext context);

	void analyzeSignalUsage(SignalUsageConsumer consumer);

	void printDeclarations(VerilogWriter out);

	void printImplementation(VerilogWriter out);

}
