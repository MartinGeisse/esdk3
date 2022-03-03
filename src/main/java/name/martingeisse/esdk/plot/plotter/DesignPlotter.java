package name.martingeisse.esdk.plot.plotter;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.plot.Event;
import name.martingeisse.esdk.plot.ValuePlotDescriptor;

import java.util.ArrayList;
import java.util.List;

public final class DesignPlotter {

    private final ImmutableList<ValuePlotter> valuePlotters;
    private final List<Event> events;

    public DesignPlotter(ImmutableList<ValuePlotter> valuePlotters) {
        this.valuePlotters = valuePlotters;
        this.events = new ArrayList<>();
    }

    public ImmutableList<ValuePlotDescriptor> buildValuePlotDescriptors() {
        List<ValuePlotDescriptor> valuePlotDescriptors = new ArrayList<>();
        for (ValuePlotter valuePlotter : valuePlotters) {
            valuePlotDescriptors.add(valuePlotter.buildDescriptor());
        }
        return ImmutableList.copyOf(valuePlotDescriptors);
    }

    public void buildEvent() {
        List<Object> updates = new ArrayList<>();
        for (ValuePlotter valuePlotter : valuePlotters) {
            updates.add(valuePlotter.buildUpdate());
        }
        events.add(new Event(ImmutableList.copyOf(updates)));
    }

    public ImmutableList<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }

}
