package name.martingeisse.esdk.plot.builder;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.library.procedural.ProceduralMemory;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.signal.VectorSignal;
import name.martingeisse.esdk.core.library.simulation.ClockedSimulationDesignItem;
import name.martingeisse.esdk.plot.DesignPlot;
import name.martingeisse.esdk.plot.variable.VectorFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * We cannot currently pause the plotting process because there is no concept for {@link VariablePlotSource}s to
 * handle such pauses. A source that simply stores sampled values would be fine, but a source that stores deltas
 * would have to continue collecting deltas even while paused, then group them and apply them at once. This
 * also raises the question whether delta-sampling is okay at all due to the dange of missing deltas. Also, we would
 * want to store and show to the user the fact that a pause occurred.
 *
 * For these reasons, the clockedPlotter can be started once and stopped once, to plot only a time "window", but once
 * stopped it cannot be restarted.
 */
public final class ClockedPlotter extends ClockedSimulationDesignItem {

    // construction time
    private BitSignal startSignal = new BitConstant(true);
    private BitSignal stopSignal = new BitConstant(false);
    private List<VariablePlotSource> variablePlotSources = new ArrayList<>();

    // simulation time
    private boolean started = false;
    private boolean stopped = false;
    private DesignPlotBuilder plotBuilder;

    public ClockedPlotter(ClockSignal clockSignal) {
        super(clockSignal);
    }

    public BitSignal getStartSignal() {
        return startSignal;
    }

    public void setStartSignal(BitSignal startSignal) {
        this.startSignal = startSignal;
    }

    public BitSignal getStopSignal() {
        return stopSignal;
    }

    public void setStopSignal(BitSignal stopSignal) {
        this.stopSignal = stopSignal;
    }

    // ----------------------------------------------------------------------------------------------------------------

    public void addSource(VariablePlotSource variablePlotSource) {
        variablePlotSources.add(variablePlotSource);
    }

    public void addSource(String name, BitSignal signal) {
        variablePlotSources.add(new BitSignalVariablePlotSource(name, signal));
    }

    public void addSource(String name, VectorSignal signal) {
        variablePlotSources.add(new VectorSignalVariablePlotSource(name, signal));
    }

    public void addSource(String name, VectorSignal signal, VectorFormat format) {
        variablePlotSources.add(new VectorSignalVariablePlotSource(name, signal, format));
    }

    public void addSource(String name, ProceduralMemory memory) {
        variablePlotSources.add(new MemoryVariablePlotSource(name, memory));
    }

    public void addSource(String name, ProceduralMemory memory, VectorFormat format) {
        variablePlotSources.add(new MemoryVariablePlotSource(name, memory, format));
    }

    public void addSource(String name, Plottable plottable) {
        plottable.addSources(this, name);
    }

    // ----------------------------------------------------------------------------------------------------------------

    @Override
    protected void initializeSimulation() {
        this.plotBuilder = new DesignPlotBuilder(ImmutableList.copyOf(variablePlotSources));
        this.variablePlotSources = null;
    }

    @Override
    public void computeNextState() {

        // start signal logic
        if (startSignal.getValue()) {
            started = true;
        }
        if (!started) {
            return;
        }

        // stop signal logic
        if (stopSignal.getValue()) {
            stopped = true;
        }
        if (stopped) {
            return;
        }

        plotBuilder.buildEvent();
    }

    @Override
    public void updateState() {
    }

    public DesignPlot buildPlot() {
        return plotBuilder.buildPlot();
    }

}
