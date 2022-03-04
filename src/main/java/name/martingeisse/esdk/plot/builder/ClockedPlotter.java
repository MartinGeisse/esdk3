package name.martingeisse.esdk.plot.builder;

import com.google.common.collect.ImmutableList;
import name.martingeisse.esdk.core.library.signal.BitConstant;
import name.martingeisse.esdk.core.library.signal.BitSignal;
import name.martingeisse.esdk.core.library.signal.ClockSignal;
import name.martingeisse.esdk.core.library.simulation.ClockedSimulationDesignItem;
import name.martingeisse.esdk.plot.DesignPlot;

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

    private final DesignPlotBuilder plotBuilder;
    private BitSignal startSignal = new BitConstant(true);
    private boolean started = false;
    private BitSignal stopSignal = new BitConstant(false);
    private boolean stopped = false;

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
