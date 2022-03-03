package name.martingeisse.esdk.plot;

import name.martingeisse.esdk.core.util.vector.Vector;

public enum NumberFormat implements VectorFormat {

    DECIMAL {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector value) {
            return Long.toString(value.getAsUnsignedLong());
        }
    },

    HEXADECIMAL {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector value) {
            return "0x" + Long.toHexString(value.getAsUnsignedLong());
        }
    },

    HEXADECIMAL_PADDED {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector value) {
            int digits = (value.getWidth() + 3) / 4;
            String s = "0000000000000000" + Long.toHexString(value.getAsUnsignedLong());
            return "0x" + s.substring(s.length() - digits);
        }
    },

    BINARY {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector value) {
            return "0b" + Long.toBinaryString(value.getAsUnsignedLong());
        }
    },

    BINARY_PADDED {
        @Override
        public String render(ValuePlotDescriptor descriptor, Vector value) {
            String s = "0000000000000000000000000000000000000000000000000000000000000000" + Long.toBinaryString(value.getAsUnsignedLong());
            return "0b" + s.substring(s.length() - value.getWidth());
        }
    }

}
