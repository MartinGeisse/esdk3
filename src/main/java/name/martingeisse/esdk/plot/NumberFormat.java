package name.martingeisse.esdk.plot;

import name.martingeisse.esdk.core.util.vector.Vector;

public enum NumberFormat implements VectorFormat {

    DECIMAL {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector sample) {
            return Long.toString(sample.getAsUnsignedLong());
        }
    },

    HEXADECIMAL {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector sample) {
            return "0x" + Long.toHexString(sample.getAsUnsignedLong());
        }
    },

    HEXADECIMAL_PADDED {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector sample) {
            int digits = (sample.getWidth() + 3) / 4;
            String s = "0000000000000000" + Long.toHexString(sample.getAsUnsignedLong());
            return "0x" + s.substring(s.length() - digits);
        }
    },

    BINARY {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector sample) {
            return "0b" + Long.toBinaryString(sample.getAsUnsignedLong());
        }
    },

    BINARY_PADDED {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector sample) {
            String s = "0000000000000000000000000000000000000000000000000000000000000000" + Long.toBinaryString(sample.getAsUnsignedLong());
            return "0b" + s.substring(s.length() - sample.getWidth());
        }
    }

}
