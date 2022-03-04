package name.martingeisse.esdk.plot.builder;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.simulation.ClockedSimulationDesignItem;
import name.martingeisse.esdk.plot.DesignPlot;

import java.util.List;

public final class ClockedPlotter extends ClockedSimulationDesignItem {

    private final DesignPlotBuilder plotBuilder;

    public ClockedPlotter(ClockSignal clockSignal, VariablePlotSource... variablePlotSources) {
        this(clockSignal, ImmutableList.copyOf(variablePlotSources));
    }

    public ClockedPlotter(ClockSignal clockSignal, List<VariablePlotSource> variablePlotSources) {
        this(clockSignal, ImmutableList.copyOf(variablePlotSources));
    }

    public ClockedPlotter(ClockSignal clockSignal, ImmutableList<VariablePlotSource> variablePlotSources) {
        super(clockSignal);
        this.plotBuilder = new DesignPlotBuilder(variablePlotSources);
    }

    public DesignPlotBuilder getPlotBuilder() {
        return plotBuilder;
    }

    public DesignPlot buildPlot() {
        return plotBuilder.buildPlot();
    }

    @Override
    public void computeNextState() {
        plotBuilder.buildEvent();
    }

    @Override
    public void updateState() {
    }

}
