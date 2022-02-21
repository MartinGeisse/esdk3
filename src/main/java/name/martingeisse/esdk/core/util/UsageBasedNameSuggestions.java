package name.martingeisse.esdk.core.util;

import name.martingeisse.esdk.core.Design;
import name.martingeisse.esdk.core.DesignItem;
import name.martingeisse.esdk.core.DesignItemOwned;
import name.martingeisse.esdk.core.library.blackbox.BlackboxInstance;
import name.martingeisse.esdk.core.library.blackbox.BlackboxInstanceInputPort;
import name.martingeisse.esdk.core.library.blackbox.BlackboxInstancePort;
import name.martingeisse.esdk.core.library.memory.*;
import name.martingeisse.esdk.core.library.pin.BidirectionalPin;
import name.martingeisse.esdk.core.library.pin.OutputPin;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemoryConstantIndexSelection;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemoryIndexSelection;
import name.martingeisse.esdk.core.library.procedural.statement.Assignment;
import name.martingeisse.esdk.core.library.procedural.statement.Statement;
import name.martingeisse.esdk.core.library.procedural.statement.StatementSequence;
import name.martingeisse.esdk.core.library.procedural.statement.WhenStatement;
import name.martingeisse.esdk.core.library.signal.Signal;
import name.martingeisse.esdk.core.library.signal.connector.SignalConnector;
import name.martingeisse.esdk.core.library.signal.mux.ConditionalOperation;
import name.martingeisse.esdk.core.library.signal.mux.SwitchSignal;
import name.martingeisse.esdk.core.library.signal.operation.*;
import name.martingeisse.esdk.core.library.signal.vector.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 */
public class UsageBasedNameSuggestions {

	private final Map<DesignItem, String> independentSuggestions = new HashMap<>();
	private final Map<DesignItem, PropagatingSuggestion> propagatingSuggestions = new HashMap<>();

	public UsageBasedNameSuggestions(Design design) {
		for (DesignItem item : design.getItems()) {
			if (item instanceof SignalConnector) {
				SignalConnector connector = (SignalConnector) item;
				if (connector.getConnected() == null) {
					throw new RuntimeException("no connected signal: " + connector);
				}
				suggest(connector.getConnected().getDesignItem(), connector, name -> name);
			} else if (item instanceof BitNotOperation) {
				BitNotOperation not = (BitNotOperation) item;
				suggest(not.getOperand(), not, name -> name + "_not");
			} else if (item instanceof BitOperation) {
				BitOperation operation = (BitOperation) item;
				suggest(operation.getLeftOperand(), operation, name -> name + "_" + operation.getOperator().name().toLowerCase() + 'L');
				suggest(operation.getRightOperand(), operation, name -> name + "_" + operation.getOperator().name().toLowerCase() + 'R');
			} else if (item instanceof VectorOperation) {
				VectorOperation operation = (VectorOperation) item;
				suggest(operation.getLeftOperand(), operation, name -> name + "_" + operation.getOperator().name().toLowerCase() + 'L');
				suggest(operation.getRightOperand(), operation, name -> name + "_" + operation.getOperator().name().toLowerCase() + 'R');
			} else if (item instanceof VectorComparison) {
				VectorComparison comparison = (VectorComparison) item;
				suggest(comparison.getLeftOperand(), comparison, name -> name + "_" + comparison.getOperator().name().toLowerCase() + 'L');
				suggest(comparison.getRightOperand(), comparison, name -> name + "_" + comparison.getOperator().name().toLowerCase() + 'R');
			} else if (item instanceof ConditionalOperation) {
				ConditionalOperation conditional = (ConditionalOperation) item;
				suggest(conditional.getCondition(), conditional, name -> name + "_condition");
				suggest(conditional.getOnTrue(), conditional, name -> name + "_then");
				suggest(conditional.getOnFalse(), conditional, name -> name + "_else");
			} else if (item instanceof BlackboxInstance) {
				BlackboxInstance moduleInstance = (BlackboxInstance) item;
				for (BlackboxInstancePort port : moduleInstance.getPorts()) {
					suggest(port, moduleInstance, name -> name + '_' + port.getPortName());
				}
			} else if (item instanceof BlackboxInstanceInputPort) {
				BlackboxInstanceInputPort port = (BlackboxInstanceInputPort) item;
				suggest(port.getAssignedSignal(), port, name -> name);
			} else if (item instanceof SwitchSignal<?>) {
				SwitchSignal<?> swtch = (SwitchSignal<?>) item;
				for (SwitchSignal.Case<?> aCase : swtch.getCases()) {
					suggest(aCase.getBranch(), swtch, name -> name + "_" + aCase.getSelectorValues().get(0).getDigits());
				}
				if (swtch.getDefaultSignal() != null) {
					suggest(swtch.getDefaultSignal(), swtch, name -> name + "_default");
				}
			} else if (item instanceof OutputPin) {
				OutputPin pin = (OutputPin) item;
				suggest(pin.getOutputSignal(), pin, name -> name);
			} else if (item instanceof BidirectionalPin) {
				BidirectionalPin pin = (BidirectionalPin) item;
				suggest(pin.getOutputSignal(), pin, name -> name + "_d");
				suggest(pin.getOutputEnableSignal(), pin, name -> name + "_en");
			} else if (item instanceof IndexSelection) {
				IndexSelection indexSelection = (IndexSelection) item;
				suggest(indexSelection.getContainerSignal(), indexSelection, name -> name + "_container");
				suggest(indexSelection.getIndexSignal(), indexSelection, name -> name + "_index");
			} else if (item instanceof ConstantIndexSelection) {
				ConstantIndexSelection indexSelection = (ConstantIndexSelection) item;
				suggest(indexSelection.getContainerSignal(), indexSelection, name -> name + "_container");
			} else if (item instanceof Concatenation) {
				Concatenation concatenation = (Concatenation) item;
				int i = 0;
				for (Signal signal : concatenation.getSignals()) {
					int finalI = i;
					suggest(signal, concatenation, name -> name + "_element" + finalI);
					i++;
				}
			} else if (item instanceof BitRepetition) {
				BitRepetition repetition = (BitRepetition) item;
				suggest(repetition.getBitSignal(), repetition, name -> name + "_element");
			} else if (item instanceof VectorRepetition) {
				VectorRepetition repetition = (VectorRepetition) item;
				suggest(repetition.getVectorSignal(), repetition, name -> name + "_element");
			} else if (item instanceof LookupTable) {
				LookupTable lookupTable = (LookupTable) item;
				suggest(lookupTable.getMemory(), lookupTable, name -> name);
			} else if (item instanceof Memory) {
				Memory memory = (Memory) item;
				int i = 0;
				for (MemoryPort port : memory.getPorts()) {
					int finalI = i;
					suggest(port, memory, name -> name + "_port" + finalI);
					i++;
				}
			} else if (item instanceof AsynchronousMemoryReadPort) {
				AsynchronousMemoryReadPort port = (AsynchronousMemoryReadPort) item;
				suggest(port.getAddressSignal(), port, name -> name + "_address");
				suggest(port.getReadDataSignal(), port, name -> name + "_data");
			} else if (item instanceof SynchronousMemoryPort) {
				SynchronousMemoryPort port = (SynchronousMemoryPort) item;
				suggest(port.getClockEnableSignal(), port, name -> name + "_enable");
				suggest(port.getAddressSignal(), port, name -> name + "_address");
				suggest(port.getReadDataSignal(), port, name -> name + "_readData");
				suggest(port.getWriteDataSignal(), port, name -> name + "_writeData");
				suggest(port.getWriteEnableSignal(), port, name -> name + "_writeEnable");
			} else if (item instanceof VectorNotOperation) {
				VectorNotOperation operation = (VectorNotOperation) item;
				suggest(operation.getOperand(), operation, name -> name + "_not");
			} else if (item instanceof VectorNegateOperation) {
				VectorNegateOperation operation = (VectorNegateOperation) item;
				suggest(operation.getOperand(), operation, name -> name + "_neg");
			} else if (item instanceof ProceduralMemoryIndexSelection) {
				ProceduralMemoryIndexSelection indexSelection = (ProceduralMemoryIndexSelection) item;
				suggest(indexSelection.getMemory(), indexSelection, name -> name + "_container");
				suggest(indexSelection.getIndexSignal(), indexSelection, name -> name + "_index");
			} else if (item instanceof ProceduralMemoryConstantIndexSelection) {
				ProceduralMemoryConstantIndexSelection indexSelection = (ProceduralMemoryConstantIndexSelection) item;
				suggest(indexSelection.getMemory(), indexSelection, name -> name + "_container");
			} else if (item instanceof OneBitVectorSignal) {
				OneBitVectorSignal signal = (OneBitVectorSignal) item;
				suggest(signal.getBitSignal(), signal, name -> name);
			} else if (item instanceof ShiftOperation) {
				ShiftOperation operation = (ShiftOperation) item;
				suggest(operation.getLeftOperand(), item, name -> name + "_shiftL");
				suggest(operation.getRightOperand(), item, name -> name + "_shiftR");
			} else if (item instanceof RangeSelection) {
				RangeSelection rangeSelection = (RangeSelection) item;
				suggest(rangeSelection.getContainerSignal(), rangeSelection, name -> name + "_container");
			} else if (item instanceof Assignment) {
				Assignment assignment = (Assignment) item;
				// cannot handle partial assignment for now
				DesignItem destination = assignment.getDestination().getDesignItem();
				suggest(assignment.getSource(), destination, name -> name + "_d"); // _d for "data"
			} else if (item instanceof WhenStatement) {
				WhenStatement when = (WhenStatement) item;
				independentSuggestions.put(when.getCondition().getDesignItem(), "condition");
				improvedNaming: if (when.getOtherwiseBranch().isEffectivelyNop()) {
					Statement thenBranch = when.getThenBranch();
					while (thenBranch instanceof StatementSequence) {
						StatementSequence nestedSequence = (StatementSequence)thenBranch;
						if (nestedSequence.getStatements().size() == 1) {
							thenBranch = nestedSequence.getStatements().get(0);
						} else {
							break improvedNaming;
						}
					}
					if (thenBranch instanceof Assignment) {
						DesignItem destination = ((Assignment) thenBranch).getDestination().getDesignItem();
						suggest(when.getCondition().getDesignItem(), destination, name -> name + "_condition");
					}
				}
			}
		}
	}

	private void suggest(DesignItemOwned target, DesignItemOwned origin, Function<String, String> nameTransformation) {
		propagatingSuggestions.put(target.getDesignItem(), new PropagatingSuggestion(origin.getDesignItem(), nameTransformation));
	}

	public Map<DesignItem, String> getIndependentSuggestions() {
		return independentSuggestions;
	}

	public Map<DesignItem, PropagatingSuggestion> getPropagatingSuggestions() {
		return propagatingSuggestions;
	}

	public static final class PropagatingSuggestion {

		private final DesignItem origin;
		private final Function<String, String> nameTransformation;

		public PropagatingSuggestion(DesignItem origin, Function<String, String> nameTransformation) {
			this.origin = origin;
			this.nameTransformation = nameTransformation;
		}

		public DesignItem getOrigin() {
			return origin;
		}

		public Function<String, String> getNameTransformation() {
			return nameTransformation;
		}

	}

}
