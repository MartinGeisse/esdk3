package name.martingeisse.esdk.plot.builder;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.plot.Event;
import name.martingeisse.esdk.plot.ValuePlotDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 TODO should make it clear that this is not by itself part of a design!
 TODO should make it clear that this does not by itself render the plot!
 *
 */
public final class DesignPlotBuilder {

    private final ImmutableList<ValuePlotSource> valuePlotSources;
    private final List<Event> events;

    public DesignPlotBuilder(ImmutableList<ValuePlotSource> valuePlotSources) {
        this.valuePlotSources = valuePlotSources;
        this.events = new ArrayList<>();
    }

    public ImmutableList<ValuePlotDescriptor> buildValuePlotDescriptors() {
        List<ValuePlotDescriptor> valuePlotDescriptors = new ArrayList<>();
        for (ValuePlotSource valuePlotSource : valuePlotSources) {
            valuePlotDescriptors.add(valuePlotSource.buildDescriptor());
        }
        return ImmutableList.copyOf(valuePlotDescriptors);
    }

    public void buildEvent() {
        List<Object> updates = new ArrayList<>();
        for (ValuePlotSource valuePlotSource : valuePlotSources) {
            updates.add(valuePlotSource.buildUpdate());
        }
        events.add(new Event(ImmutableList.copyOf(updates)));
    }

    public ImmutableList<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }

}
