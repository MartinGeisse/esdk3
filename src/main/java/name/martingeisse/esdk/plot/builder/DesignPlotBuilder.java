package name.martingeisse.esdk.plot.builder;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.plot.DesignPlot;
import name.martingeisse.esdk.plot.Event;
import name.martingeisse.esdk.plot.variable.VariablePlotDescriptor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is able to build a {@link DesignPlot} from {@link VariablePlotSource}s. It is completely passive and
 * usually used indirectly through an active plot-building object such as a {@link ClockedPlotter}.
 *
 * This class also does not deal with rendering the generated plot in any way.
 */
public final class DesignPlotBuilder {

    private final ImmutableList<VariablePlotSource> variablePlotSources;
    private final List<Event> events;

    public DesignPlotBuilder(ImmutableList<VariablePlotSource> variablePlotSources) {
        this.variablePlotSources = variablePlotSources;
        this.events = new ArrayList<>();
    }

    public ImmutableList<VariablePlotDescriptor> buildVariablePlotDescriptors() {
        List<VariablePlotDescriptor> variablePlotDescriptors = new ArrayList<>();
        for (VariablePlotSource variablePlotSource : variablePlotSources) {
            variablePlotDescriptors.add(variablePlotSource.buildDescriptor());
        }
        return ImmutableList.copyOf(variablePlotDescriptors);
    }

    public void buildEvent() {
        List<Object> samples = new ArrayList<>();
        for (VariablePlotSource variablePlotSource : variablePlotSources) {
            samples.add(variablePlotSource.buildSample());
        }
        events.add(new Event(ImmutableList.copyOf(samples)));
    }

    public ImmutableList<Event> getEvents() {
        return ImmutableList.copyOf(events);
    }

    public DesignPlot buildPlot() {
        return new DesignPlot(buildVariablePlotDescriptors(), ImmutableList.copyOf(events));
    }

}
