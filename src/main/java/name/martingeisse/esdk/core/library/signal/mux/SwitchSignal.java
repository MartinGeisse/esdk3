package name.martingeisse.esdk.core.library.signal.mux;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SignalUsageConsumer;
import name.martingeisse.esdk.core.tools.synthesis.verilog.SynthesisPreparationContext;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogSignalDeclarationKeyword;
import name.martingeisse.esdk.core.tools.synthesis.verilog.VerilogWriter;
import name.martingeisse.esdk.core.tools.synthesis.verilog.contribution.VerilogContribution;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionNesting;
import name.martingeisse.esdk.core.tools.synthesis.verilog.expression.VerilogExpressionWriter;
import name.martingeisse.esdk.core.util.vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class SwitchSignal<BRANCH extends Signal> extends DesignItem implements Signal, DesignItemOwned {

	private final VectorSignal selector;
	private final List<Case<BRANCH>> cases;
	private BRANCH defaultSignal;

	public SwitchSignal(VectorSignal selector) {
		this.selector = selector;
		this.cases = new ArrayList<>();
		this.defaultSignal = null;
	}

	public final VectorSignal getSelector() {
		return selector;
	}

	public final ImmutableList<Case<BRANCH>> getCases() {
		return ImmutableList.copyOf(cases);
	}

	public final BRANCH getDefaultSignal() {
		return defaultSignal;
	}

	public final void setDefaultSignal(BRANCH defaultSignal) {
		validateOnAdd(defaultSignal);
		this.defaultSignal = defaultSignal;
	}

	public final void addCase(Vector selectorValue, BRANCH branch) {
		addCase(ImmutableList.of(selectorValue), branch);
	}

	public final void addCase(Vector selectorValue1, Vector selectorValue2, BRANCH branch) {
		addCase(ImmutableList.of(selectorValue1, selectorValue2), branch);
	}

	public final void addCase(Vector selectorValue1, Vector selectorValue2,
							  Vector selectorValue3, BRANCH branch) {
		addCase(ImmutableList.of(selectorValue1, selectorValue2, selectorValue3), branch);
	}

	public final void addCase(ImmutableList<Vector> selectorValues, BRANCH branch) {
		for (Vector selectorValue : selectorValues) {
			if (selectorValue.getWidth() != selector.getWidth()) {
				throw new IllegalArgumentException("selector value has width " + selectorValue.getWidth() +
					", expected " + selector.getWidth());
			}
		}
		validateOnAdd(branch);
		cases.add(new Case<>(selectorValues, branch));
	}

	protected abstract void validateOnAdd(BRANCH branch);

	protected final BRANCH getCurrentlySelectedBranch() {
		Vector actualSelectorValue = selector.getValue();
		for (Case<BRANCH> aCase : cases) {
			for (Vector caseSelectorValue : aCase.getSelectorValues()) {
				if (actualSelectorValue.equals(caseSelectorValue)) {
					return aCase.getBranch();
				}
			}
		}
		if (defaultSignal == null) {
			throw new IllegalStateException("selector value " + actualSelectorValue +
				" did not match any case but there is no default branch");
		}
		return defaultSignal;
	}

	public static final class Case<B extends Signal> {

		private final ImmutableList<Vector> selectorValues;
		private final B branch;

		public Case(ImmutableList<Vector> selectorValues, B branch) {
			this.selectorValues = selectorValues;
			this.branch = branch;
		}

		public ImmutableList<Vector> getSelectorValues() {
			return selectorValues;
		}

		public B getBranch() {
			return branch;
		}
	}

	@Override
	public VerilogContribution getVerilogContribution() {
		return new VerilogContribution() {

			@Override
			public void prepareSynthesis(SynthesisPreparationContext context) {
				context.declareSignal(SwitchSignal.this, VerilogSignalDeclarationKeyword.REG, false);
			}

			@Override
			public void analyzeSignalUsage(SignalUsageConsumer consumer) {
			}

			@Override
			public void printDeclarations(VerilogWriter out) {
			}

			@Override
			public void printImplementation(VerilogWriter out) {
				out.indent();
				out.println("always @(*) begin");
				out.startIndentation();
				out.indent();
				out.print("case (");
				out.printSignal(selector);
				out.println(")");
				out.println();
				out.startIndentation();
				for (Case<BRANCH> aCase : cases) {
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
					out.indent();
					out.printSignal(SwitchSignal.this);
					out.print(" <= ");
					out.printSignal(aCase.branch);
					out.println(";");
					out.endIndentation();
					out.indent();
					out.println("end");
					out.println();
				}
				if (defaultSignal != null) {
					out.indent();
					out.println("default: begin");
					out.startIndentation();
					out.indent();
					out.printSignal(SwitchSignal.this);
					out.print(" <= ");
					out.printSignal(defaultSignal);
					out.println(";");
					out.endIndentation();
					out.indent();
					out.println("end");
					out.println();
				}
				out.endIndentation();
				out.indent();
				out.println("endcase");
				out.endIndentation();
				out.indent();
				out.println("end");
			}

		};
	}

	@Override
	public boolean compliesWith(VerilogExpressionNesting nesting) {
		return false;
	}

	@Override
	public void analyzeSignalUsage(SignalUsageConsumer consumer) {
		consumer.consumeSignalUsage(selector, VerilogExpressionNesting.SELECTIONS_SIGNALS_AND_CONSTANTS);
		for (Case<?> aCase : cases) {
			consumer.consumeSignalUsage(aCase.branch, VerilogExpressionNesting.ALL);
		}
		if (defaultSignal != null) {
			consumer.consumeSignalUsage(defaultSignal, VerilogExpressionNesting.ALL);
		}
	}

	@Override
	public void printVerilogImplementationExpression(VerilogExpressionWriter out) {
		throw new UnsupportedOperationException("cannot generate implementation expression for SwitchSignal");
	}

}
