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
 * interpreted. The common case is clock-triggered, which means that each each event contains the values that are
 * asserted at a specific clock edge TODO not correct either. The confusion comes from register values vs. signal
 * values -- register values change at edges and are stable during cycles, while signal values change during cycles
 * and are stable at edges!
 *      - treating a register as a signal returns its current value
 *      - the only difference is when the values change
 *      - registers change precisely at clock edges. Signals change at an undefined point somewhere during a cycle
 *          (the exact point is not modeled in RTL!)
 *      - the signal associated with a register changes directly after an edge
 *      - directly before an edge, each register signal is equal to the stored value, and derived signals have
 *          values that are consistent with the register values and the operators that define the derived signals
 *      - solution: define the sampling point for the plot either directly before or after a clock edge (which?)
 *          -> before the edge!
 *      - "directly before the edge" is precise but confusing, because it refers to an edge when the values get
 *          destroyed.
 *      - better: "at the end of a clock cycle" -- refers to the cycle, not the edge.
 *
 * during a specific clock cycle. TODO correct? values are definitely asserted at a clock *edge* and may
 * change during a cycle, so that's exactly wrong!
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
