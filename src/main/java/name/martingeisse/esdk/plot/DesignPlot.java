package name.martingeisse.esdk.plot;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.library.clocked.Clock;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.plot.variable.VariablePlotDescriptor;

/**
 * A design plot logs a user-defined set of variables as they change during a simulation, generating a variable plot
 * per variable.
 *
 * Each variable plot is defined by a variable plot descriptor which contains the variable's data type as well as
 * meta-data such as presentation hints and compression scheme.
 *
 * The variables' values are logged as a sequence of events, each of which carries a set of samples -- one sample per
 * variable. An event corresponds to a single point in time, such as a specific point in a {@link Clock} cycle.
 *
 * Depending on the variable plot descriptor, the meaning and/or presentation of a sample may be incremental, that is,
 * only useful in combination with all preceding samples,  or non-incremental, i.e. useful on its own. This
 * distinction can be made separately for storage and presentation, yielding four combinations:
 * - stored and presented non-incremental: e.g. the value of a {@link BitSignal} or {@link VectorSignal}
 * - stored incremental but presented non-incremental: the value of a {@link ProceduralMemory}, which is well-defined
 *   at each single point in time but stored as incremental updates to reduce data size
 * - stored and presented incremental: log messages generated from the system state that together form a readable log
 * - stored non-incremental but presented incremental: this is a rare case, but may occur when a non-incremental
 *   state (such as a {@link VectorSignal}) is more useful when presented as a sequence of deltas.
 *
 * The trigger that causes events and their samples to be plotted defines how the timing of these events must be
 * interpreted. The common case is clock-triggered, which means that each event contains the values that are stable
 * at the end of a specific clock cycle, immediately before the next clock edge. This ensures that values stored in
 * registers correspond to the values when treating a register as a signal, and the values of derived signals are
 * consistent with register values, in the sense of the operators that define the derived signals. Incremental
 * variable plots should be defined defined in a way that is consistent with this definition.
 *
 * There is no special handling for the initial state of a design. The above definitions imply that the first event
 * occurs just before the first clock edge, so it will contain the initial values. To properly allow the initial values
 * for incrementally-stored variable plots to be stored in this first event, a pre-initialized state is define per
 * type of variable plot, such as (false) for bit signals, 0 for vector signals, and all-zero for memories. The
 * initial state of the system, logged in the first event, can then be represented as an incremental update with
 * respect to this pre-initialized state.
 *
 * Plot objects are immutable, but not value objects in general. That is, their identity does matter, and is
 * usually used for referencing.
 */
public final class DesignPlot {

    public final ImmutableList<VariablePlotDescriptor> variablePlotDescriptors;
    public final ImmutableList<Event> events;

    public DesignPlot(ImmutableList<VariablePlotDescriptor> variablePlotDescriptors, ImmutableList<Event> events) {
        if (variablePlotDescriptors == null) {
            throw new IllegalArgumentException("variablePlotDescriptors is null");
        }
        for (VariablePlotDescriptor variablePlotDescriptor : variablePlotDescriptors) {
            if (variablePlotDescriptor == null) {
                throw new IllegalArgumentException("variablePlotDescriptors contains null element");
            }
        }
        if (events == null) {
            throw new IllegalArgumentException("events is null");
        }
        for (Event event : events) {
            if (event == null) {
                throw new IllegalArgumentException("elements contains null element");
            }
            event.validate(variablePlotDescriptors);
        }
        this.variablePlotDescriptors = variablePlotDescriptors;
        this.events = events;
    }

}
