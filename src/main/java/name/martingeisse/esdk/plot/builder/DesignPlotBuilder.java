package name.martingeisse.esdk.plot.builder;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.plot.DesignPlot;
import name.martingeisse.esdk.plot.Event;
import name.martingeisse.esdk.plot.ValuePlotDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is able to build a {@link DesignPlot} from {@link ValuePlotSource}s. It is completely passive and
 * usually used indirectly through an active plot-building object such as a {@link ClockedPlotter}.
 *
 * This class also does not deal with rendering the generated plot in any way.
 */
public final class DesignPlotBuilder {

    private final ImmutableList<ValuePlotSource> valuePlotSources;
    private Event initializer;
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

    public void buildInitializer() {
        if (initializer != null) {
            throw new IllegalStateException("initializer has been built already");
        }
        if (!events.isEmpty()) {
            throw new IllegalStateException("normal events have already been added");
        }
        List<Object> updates = new ArrayList<>();
        for (ValuePlotSource valuePlotSource : valuePlotSources) {
            updates.add(valuePlotSource.buildInitializerUpdate());
        }
        initializer = new Event(ImmutableList.copyOf(updates));
    }

    public void buildEvent() {
        if (initializer == null) {
            throw new IllegalStateException("no initializer has been built yet");
        }
        List<Object> updates = new ArrayList<>();
        for (ValuePlotSource valuePlotSource : valuePlotSources) {
            updates.add(valuePlotSource.buildUpdate());
        }
        events.add(new Event(ImmutableList.copyOf(updates)));
    }

    public Event getInitializer() {
        return initializer;
    }

    public ImmutableList<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }

    public DesignPlot buildPlot() {
        if (initializer == null) {
            throw new IllegalStateException("no initializer has been built yet");
        }
        return new DesignPlot(buildValuePlotDescriptors(), initializer, ImmutableList.copyOf(events));
    }

}
