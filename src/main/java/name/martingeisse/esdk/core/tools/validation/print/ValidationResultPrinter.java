package name.martingeisse.esdk.core.tools.validation.print;

/**
 *
 */
public interface ValidationResultPrinter {

	void printFoldedSubItem(String propertyName, String className);
	void beginItem(String propertyName, String className);
	void endItem();
	void printError(String message);
	void printWarning(String message);
	void printReference(String propertyName, String reference);

}
