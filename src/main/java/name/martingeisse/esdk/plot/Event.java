package name.martingeisse.esdk.plot;

import com.google.common.collect.ImmutableList;

public final class Event {

    public final ImmutableList<Object> updates;

    public Event(ImmutableList<Object> updates) {
        if (updates == null) {
            throw new IllegalArgumentException("updates is null");
        }
        this.updates = updates;
    }

    public void validate(ImmutableList<ValuePlotDescriptor> valuePlotDescriptors) {
        if (updates.size() != valuePlotDescriptors.size()) {
            throw new IllegalArgumentException("invalid number of updates: " + updates.size() + " (expected " + valuePlotDescriptors.size() + ")");
        }
        int numberOfUpdates = updates.size();
        for (int i = 0; i < numberOfUpdates; i++) {
            valuePlotDescriptors.get(i).validateUpdate(updates.get(i));
        }
    }

}
