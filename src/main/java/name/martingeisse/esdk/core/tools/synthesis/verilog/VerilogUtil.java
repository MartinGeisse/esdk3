package name.martingeisse.esdk.core.tools.synthesis.verilog;

import name.martingeisse.esdk.core.util.Matrix;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class VerilogUtil {

	/**
	 * Helper method to generate a .mif file for the contents of a memory.
	 */
	public static void generateMif(AuxiliaryFileFactory auxiliaryFileFactory, String filename, Matrix matrix) {
		if (filename == null) {
			throw new IllegalArgumentException("filename argument is null");
		}
		if (matrix == null) {
			throw new IllegalArgumentException("matrix argument is null");
		}
		try (OutputStream outputStream = auxiliaryFileFactory.create(filename)) {
			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.US_ASCII)) {
				matrix.writeToMif(new PrintWriter(outputStreamWriter));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
