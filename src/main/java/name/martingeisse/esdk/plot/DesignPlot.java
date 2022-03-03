package name.martingeisse.esdk.plot;

import com.google.common.collect.ImmutableList;

/**
 * Plot objects are immutable, but not value objects in general. That is, their identity does matter, and is
 * usually used for referencing.
 *
 * There is no such thing as initial values. Each value plot only consists of updates. For those value plots for which
 * an initial value makes sense at all, it must come with the first update. Before / without that update, default
 * values apply.
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
