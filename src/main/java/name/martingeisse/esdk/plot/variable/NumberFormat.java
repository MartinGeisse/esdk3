package name.martingeisse.esdk.plot.variable;

import name.martingeisse.esdk.core.util.vector.Vector;

public enum NumberFormat implements VectorFormat {

    DECIMAL {
        @Override
        public String render(int width, long sample) {
            return Long.toString(sample);
        }
    },

    HEXADECIMAL {
        @Override
        public String render(int width, long sample) {
            return "0x" + Long.toHexString(sample);
        }
    },

    HEXADECIMAL_PADDED {
        @Override
        public String render(int width, long sample) {
            int digits = (width + 3) / 4;
            String s = "0000000000000000" + Long.toHexString(sample);
            return "0x" + s.substring(s.length() - digits);
        }
    },

    BINARY {
        @Override
        public String render(int width, long sample) {
            return "0b" + Long.toBinaryString(sample);
        }
    },

    BINARY_PADDED {
        @Override
        public String render(int width, long sample) {
            String s = "0000000000000000000000000000000000000000000000000000000000000000" + Long.toBinaryString(sample);
            return "0b" + s.substring(s.length() - width);
        }
    };

    @Override
    public String render(VariablePlotDescriptor descriptor, Vector sample) {
        return render(sample.getWidth(), sample.getAsUnsignedLong());
    }

    public abstract String render(int width, long sample);

}
