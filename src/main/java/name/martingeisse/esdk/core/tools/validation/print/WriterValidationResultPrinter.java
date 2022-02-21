package name.martingeisse.esdk.core.tools.validation.print;

import java.io.Closeable;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 *
 */
public final class WriterValidationResultPrinter implements ValidationResultPrinter, Closeable {

	private final PrintWriter out;
	private int indentation;

	public WriterValidationResultPrinter(Writer out) {
		this(new PrintWriter(out));
	}

	public WriterValidationResultPrinter(PrintStream out) {
		this(new PrintWriter(out));
	}

	public WriterValidationResultPrinter(PrintWriter out) {
		this.out = out;
	}

	public void flush() {
		out.flush();
	}

	public void close() {
		out.close();
	}

	@Override
	public void printFoldedSubItem(String propertyName, String className) {
		indent();
		if (propertyName == null) {
			out.println("* " + className + " (--)");
		} else {
			out.println(propertyName + ": " + className + " (--)");
		}
	}

	@Override
	public void beginItem(String propertyName, String className) {
		indent();
		if (propertyName == null) {
			out.println("* " + className);
		} else {
			out.println(propertyName + ": " + className);
		}
		indentation++;
	}

	@Override
	public void printError(String message) {
		indent();
		out.println("ERROR: " + message);
	}

	@Override
	public void printWarning(String message) {
		indent();
		out.println("WARNING: " + message);
	}

	@Override
	public void printReference(String propertyName, String reference) {
		indent();
		if (propertyName == null) {
			out.println("-> " + reference);
		} else {
			out.println(propertyName + " -> " + reference);
		}
	}

	@Override
	public void endItem() {
		indentation--;
	}

	private void indent() {
		for (int i = 0; i < indentation; i++) {
			out.print("  ");
		}
	}

}
