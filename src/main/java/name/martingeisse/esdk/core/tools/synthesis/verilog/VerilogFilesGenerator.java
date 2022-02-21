/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.verilog;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Generates a folder with a verilog file as the final output to use in other projects.
 */
public class VerilogFilesGenerator {

	private final String name;
	private final File outputFolder;

	public VerilogFilesGenerator(String name, File outputFolder) {
		this.name = name;
		this.outputFolder = outputFolder;
	}

	public void clean() throws IOException {
		FileUtils.deleteDirectory(outputFolder);
	}

	public void generate() throws IOException {
		if (!outputFolder.mkdirs()) {
			throw new IOException("could not create output folders");
		}

		AuxiliaryFileFactory auxiliaryFileFactory =
			filename -> new FileOutputStream(new File(outputFolder, filename));

		generateFile(name + ".v", out -> {
			VerilogCodeGenerator verilogCodeGenerator = new VerilogCodeGenerator(out, name, auxiliaryFileFactory);
			verilogCodeGenerator.generate();
		});

	}

	private void generateFile(String filename, Consumer<PrintWriter> contentGenerator) throws IOException {
		File file = new File(outputFolder, filename);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.US_ASCII)) {
				try (PrintWriter printWriter = new PrintWriter(outputStreamWriter)) {
					contentGenerator.accept(printWriter);
				}
			}
		}
	}

}
