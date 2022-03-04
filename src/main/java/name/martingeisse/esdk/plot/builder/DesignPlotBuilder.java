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
        List<Object> samples = new ArrayList<>();
        for (ValuePlotSource valuePlotSource : valuePlotSources) {
            samples.add(valuePlotSource.buildSample());
        }
        events.add(new Event(ImmutableList.copyOf(samples)));
    }

    public ImmutableList<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }

    public DesignPlot buildPlot() {
        return new DesignPlot(buildValuePlotDescriptors(), ImmutableList.copyOf(events));
    }

}
