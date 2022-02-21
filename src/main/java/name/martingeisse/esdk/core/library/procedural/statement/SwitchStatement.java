package name.martingeisse.esdk.core.library.procedural.statement;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.procedural.ProceduralRegister;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 */
public final class SwitchStatement extends Statement {

	private final VectorSignal selector;
	private final List<Case> cases;
	private final StatementSequence defaultBranch;

	public SwitchStatement(VectorSignal selector) {
		this.selector = selector;
		this.cases = new ArrayList<>();
		this.defaultBranch = new StatementSequence();
	}

	public VectorSignal getSelector() {
		return selector;
	}

	public ImmutableList<Case> getCases() {
		return ImmutableList.copyOf(cases);
	}

	public StatementSequence getDefaultBranch() {
		return defaultBranch;
	}

	public StatementSequence addCase(Vector... selectorValues) {
		return addCase(ImmutableList.copyOf(selectorValues));
	}

	public StatementSequence addCase(ImmutableList<Vector> selectorValues) {
		for (Vector selectorValue : selectorValues) {
			if (selectorValue.getWidth() != selector.getWidth()) {
				throw new IllegalArgumentException("selector value has width " + selectorValue.getWidth() +
					", expected " + selector.getWidth());
			}
		}
		Case aCase = new Case(selectorValues);
		cases.add(aCase);
		return aCase.getBranch();
	}

	public static final class Case {

		private final ImmutableList<Vector> selectorValues;
		private final StatementSequence branch;

		public Case(ImmutableList<Vector> selectorValues) {
			this.selectorValues = selectorValues;
			this.branch = new StatementSequence();
		}

		public ImmutableList<Vector> getSelectorValues() {
			return selectorValues;
		}

		public StatementSequence getBranch() {
			return branch;
		}
	}

	@Override
	public boolean isEffectivelyNop() {
		for (Case aCase : cases) {
			if (!aCase.getBranch().isEffectivelyNop()) {
				return false;
			}
		}
		return (defaultBranch == null || defaultBranch.isEffectivelyNop());
	}

	@Override
	public void collectAssignedRegistersAndMemories(Consumer<ProceduralRegister> registerConsumer, Consumer<ProceduralMemory> memoryConsumer) {
		cases.forEach(aCase -> aCase.branch.collectAssignedRegistersAndMemories(registerConsumer, memoryConsumer));
		defaultBranch.collectAssignedRegistersAndMemories(registerConsumer, memoryConsumer);
	}

	@Override
	public void execute() {
		Vector actualSelectorValue = selector.getValue();
		for (Case aCase : cases) {
			for (Vector caseSelectorValue : aCase.getSelectorValues()) {
				if (actualSelectorValue.equals(caseSelectorValue)) {
					aCase.getBranch().execute();
					return;
				}
			}
		}
		if (defaultBranch != null) {
			defaultBranch.execute();
		}
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		for (Case aCase : cases) {
			aCase.getBranch().analyzeSignalUsage(consumer);
		}
		if (defaultBranch != null) {
			defaultBranch.analyzeSignalUsage(consumer);
		}
	}

	@Override
	public void printVerilogStatements(VerilogWriter out) {
		out.indent();
		out.print("case (");
		out.printSignal(selector);
		out.println(")");
		out.println();
		out.startIndentation();
		for (Case aCase : cases) {
			out.indent();
			boolean firstSelectorValue = true;
			for (Vector selectorValue : aCase.selectorValues) {
				if (firstSelectorValue) {
					firstSelectorValue = false;
				} else {
					out.print(", ");
				}
				out.print(selectorValue);
			}
			out.println(": begin");
			out.startIndentation();
			aCase.getBranch().printVerilogStatements(out);
			out.endIndentation();
			out.indent();
			out.println("end");
			out.println();
		}
		if (defaultBranch != null) {
			out.indent();
			out.println("default: begin");
			out.startIndentation();
			defaultBranch.printVerilogStatements(out);
			out.endIndentation();
			out.indent();
			out.println("end");
			out.println();
		}
		out.endIndentation();
		out.indent();
		out.println("endcase");
	}

}
