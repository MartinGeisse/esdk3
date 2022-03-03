package name.martingeisse.esdk.plot;

import com.google.common.collect.ImmutableList;

/**
 * Plot objects are immutable, but not value objects in general. That is, their identity does matter, and is
 * usually used for referencing.
 *
 * There is no such thing as initial values. Each value plot only consists of updates. For those value plots for which
 * an initial value makes sense at all, it must come with the first update. Before / without that update, default
 * values apply.
 *
 * The trigger that causes events and their values to be plotted defines how the timing of these events must be
 * interpreted. The common case is clock-triggered, which means that each event contains the values that are stable
 * at the end of a specific clock cycle, immediately before the next clock edge. This ensures that values stored in
 * registers correspond to the values when treating a register as a signal, and the values of derived signals are
 * consistent with register values, in the sense of the operators that define the derived signals. Value plots
 * that describe changes to the current state, as opposed to the current state itself, should be defined in a way
 * that is consistent with this definition.
 *
 * TODO define the timing of the initialization events / values
 */
public final class DesignPlot {

    public final ImmutableList<ValuePlotDescriptor> valuePlotDescriptors;
    public final ImmutableList<Event> events;

    public DesignPlot(ImmutableList<ValuePlotDescriptor> valuePlotDescriptors, ImmutableList<Event> events) {
        if (valuePlotDescriptors == null) {
            throw new IllegalArgumentException("valuePlotDescriptors is null");
        }
        for (ValuePlotDescriptor valuePlotDescriptor : valuePlotDescriptors) {
            if (valuePlotDescriptor == null) {
                throw new IllegalArgumentException("valuePlotDescriptors contains null element");
            }
        }
        if (events == null) {
            throw new IllegalArgumentException("events is null");
        }
        for (Event event : events) {
            if (event == null) {
                throw new IllegalArgumentException("elements contains null element");
            }
        }
        this.valuePlotDescriptors = valuePlotDescriptors;
        this.events = events;
    }

}
