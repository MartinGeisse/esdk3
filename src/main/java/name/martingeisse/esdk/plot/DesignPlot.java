package name.martingeisse.esdk.plot;

import com.google.common.collect.ImmutableList;

/**
 * Plot objects are immutable, but not value objects in general. That is, their identity does matter, and is
 * usually used for referencing.
 *
 * A plot is defined as a sequence of events, each of which carries a set of updates -- one update per value plot.
 * TODO rename updates to samples? more appropriate for signals, which is the main usage!
 * "update" -> "sample", "event" -> ???
 * No initializer is needed! Event with index 0 already samples the initial value, the first clock edge occurs
 * after that and loads the values that can be observed in the event with index 1.
 * ...
 * The initial values are defined through an initializer event that is treated the same way as other events with
 * respect to the updated values, but with special treatment in terms of timing. It is defined to happen before all
 * events, with no reference to a specific point in time, and does not take part in event numbering -- the first
 * event after the initializer, i.e. the first event in the events list, is event number 0. If affects the values as
 * follows: Before the initializer, all values are set to a pre-initialized value that depends on the value plot
 * descriptor, such as false for bit-typed plots and 0 for vector-typed plots. The initializer event is applied as
 * an update to this pre-initialized state to obtain the initialized state, just like later events are applied as
 * state updates to obtain the next state.
 *
 * The trigger that causes events and their values to be plotted defines how the timing of these events must be
 * interpreted. The common case is clock-triggered, which means that each event contains the values that are stable
 * at the end of a specific clock cycle, immediately before the next clock edge. This ensures that values stored in
 * registers correspond to the values when treating a register as a signal, and the values of derived signals are
 * consistent with register values, in the sense of the operators that define the derived signals. Value plots
 * that describe changes to the current state, as opposed to the current state itself, should be defined in a way
 * that is consistent with this definition.
 */
public final class DesignPlot {

    public final ImmutableList<ValuePlotDescriptor> valuePlotDescriptors;
    public final Event initializer;
    public final ImmutableList<Event> events;

    public DesignPlot(ImmutableList<ValuePlotDescriptor> valuePlotDescriptors, Event initializer, ImmutableList<Event> events) {
        if (valuePlotDescriptors == null) {
            throw new IllegalArgumentException("valuePlotDescriptors is null");
        }
        for (ValuePlotDescriptor valuePlotDescriptor : valuePlotDescriptors) {
            if (valuePlotDescriptor == null) {
                throw new IllegalArgumentException("valuePlotDescriptors contains null element");
            }
        }
        if (initializer == null) {
            throw new IllegalArgumentException("initializer is null");
        }
        initializer.validate(valuePlotDescriptors);
        if (events == null) {
            throw new IllegalArgumentException("events is null");
        }
        for (Event event : events) {
            if (event == null) {
                throw new IllegalArgumentException("elements contains null element");
            }
            event.validate(valuePlotDescriptors);
        }
        this.valuePlotDescriptors = valuePlotDescriptors;
        this.initializer = initializer;
        this.events = events;
    }

}
