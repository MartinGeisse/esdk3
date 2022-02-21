package name.martingeisse.esdk.core.tools.synthesis.verilog.contribution;

/**
 *
 */
public class SynthesisNotSupportedException extends RuntimeException {

	public SynthesisNotSupportedException() {
	}

	public SynthesisNotSupportedException(String message) {
		super(message);
	}

	public SynthesisNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public SynthesisNotSupportedException(Throwable cause) {
		super(cause);
	}

}
