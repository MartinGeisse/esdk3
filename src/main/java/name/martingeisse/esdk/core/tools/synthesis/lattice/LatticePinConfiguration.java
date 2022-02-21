/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.lattice;

import name.martingeisse.esdk.core.library.pin.Pin;
import name.martingeisse.esdk.core.library.pin.PinConfiguration;
import org.apache.commons.lang3.tuple.Pair;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class LatticePinConfiguration extends PinConfiguration {

    private final List<Pair<String, String>> properties = new ArrayList<>();
    private String frequency;

    public List<Pair<String, String>> getProperties() {
        return properties;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void set(String key, String value) {
        properties.add(Pair.of(key, value));
    }

    public void writePcf(Pin pin, PrintWriter out) {
        out.println("LOCATE COMP \"" + pin.getNetName() + "\" SITE \"" + pin.getId() + "\";");
        for (Pair<String, String> property : properties) {
            out.println("IOBUF PORT \"" + pin.getNetName() + "\" " + property.getKey() + '=' + property.getValue() + ';');
        }
        if (frequency != null) {
            out.println("FREQUENCY PORT \"" + pin.getNetName() + "\" " + frequency + ";");
        }
    }

}
