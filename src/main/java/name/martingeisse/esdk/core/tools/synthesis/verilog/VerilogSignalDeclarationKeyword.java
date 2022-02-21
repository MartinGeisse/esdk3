package name.martingeisse.esdk.core.tools.synthesis.verilog;

/**
 * Distinguishes how a signal gets declared: as a wire, reg, or not at all.
 */
public enum VerilogSignalDeclarationKeyword {

	NONE, WIRE, REG;

	public String getKeyword() {
		if (this == NONE) {
			throw new UnsupportedOperationException();
		}
		return name().toLowerCase();
	}

}
