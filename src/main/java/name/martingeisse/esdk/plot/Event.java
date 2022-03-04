package name.martingeisse.esdk.plot;

import com.google.common.collect.ImmutableList;

public final class Event {

    public final ImmutableList<Object> samples;

    public Event(ImmutableList<Object> samples) {
        if (samples == null) {
            throw new IllegalArgumentException("samples is null");
        }
        this.samples = samples;
    }

    public void validate(ImmutableList<ValuePlotDescriptor> valuePlotDescriptors) {
        if (samples.size() != valuePlotDescriptors.size()) {
            throw new IllegalArgumentException("invalid number of samples: " + samples.size() + " (expected " + valuePlotDescriptors.size() + ")");
        }
        int numberOfSamples = samples.size();
        for (int i = 0; i < numberOfSamples; i++) {
            valuePlotDescriptors.get(i).validateSample(samples.get(i));
        }
    }

}
