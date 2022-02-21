package name.martingeisse.esdk.core.tools.validation;

/**
 *
 */
public interface ValidationContext {

	void reportError(String message);

	void reportWarning(String message);

}
