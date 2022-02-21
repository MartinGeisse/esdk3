/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core.tools.synthesis.verilog;

/**
 *
 */
public interface ToplevelPortConsumer {

	/**
	 * Contributes a port to the enclosing verilog module.
	 *
	 * The direction must be the keyword used in verilog: input, output, or inout.
	 *
	 * The vectorSize should be null for bit-typed ports.
	 */
	void consumePort(String direction, String name, Integer vectorSize);

}
