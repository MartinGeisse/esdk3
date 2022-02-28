/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdktest.designs.components.vga;

/**
 * Decodes VGA signals and sends the resulting images to an implementation of the Output interface.
 *
 * The RGB values are expected to be in the range 0..255.
 *
 * This class does not have any porch detection, so the porches will be part of the output.
 */
public final class VgaSignalDecoder {

	private final int width;
	private final int height;
	private final int clocksPerPixel;
	private final Output output;
	private int w;
	private int x;
	private int y;
	private boolean rowStarted;

	public VgaSignalDecoder(int width, int height, int clocksPerPixel, Output output) {
		this.width = width;
		this.height = height;
		this.clocksPerPixel = clocksPerPixel;
		this.output = output;
		this.w = 0;
		this.x = 0;
		this.y = 0;
		this.rowStarted = false;
	}

	public void consumeDataUnit(int r, int g, int b, boolean hsync, boolean vsync) {
		if (!vsync) {
			if (w != 0 || x != 0 || y != 0) {
				output.onFrameFinished();
			}
			w = 0;
			x = 0;
			y = 0;
			rowStarted = false;
			return;
		}
		if (!hsync) {
			if (rowStarted) {
				w = 0;
				x = 0;
				y++;
			}
			rowStarted = false;
			return;
		}
		rowStarted = true;
		w++;
		if (w == clocksPerPixel) {
			w = 0;
			if (x < width && y < height) {
				output.outputPixel(x, y, r, g, b);
			}
			x++;
		}
	}

	public interface Output {

		void outputPixel(int x, int y, int r, int g, int b);

		void onFrameFinished();

	}

}
