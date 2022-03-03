package name.martingeisse.esdk.plot.builder;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.simulation.ClockedSimulationDesignItem;
import name.martingeisse.esdk.plot.DesignPlot;

import java.util.List;

public final class ClockedPlotter extends ClockedSimulationDesignItem {

    private final ImmutableList<ValuePlotSource> valuePlotSources;
    private final DesignPlotBuilder plotBuilder;

    public ClockedPlotter(ClockSignal clockSignal, ValuePlotSource... valuePlotSources) {
        this(clockSignal, ImmutableList.copyOf(valuePlotSources));
    }

    public ClockedPlotter(ClockSignal clockSignal, List<ValuePlotSource> valuePlotSources) {
        this(clockSignal, ImmutableList.copyOf(valuePlotSources));
    }

    public ClockedPlotter(ClockSignal clockSignal, ImmutableList<ValuePlotSource> valuePlotSources) {
        super(clockSignal);
        this.valuePlotSources = valuePlotSources;
        this.plotBuilder = new DesignPlotBuilder(valuePlotSources);
    }

    public DesignPlotBuilder getPlotBuilder() {
        return plotBuilder;
    }

    public DesignPlot buildPlot() {
        return plotBuilder.buildPlot();
    }

    @Override
    public void computeNextState() {
    }

    @Override
    public void updateState() {
    }

}
