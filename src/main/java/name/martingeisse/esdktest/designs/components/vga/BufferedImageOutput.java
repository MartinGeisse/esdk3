/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdktest.designs.components.vga;

import java.awt.image.BufferedImage;

/**
 * An implementation of the {@link VgaSignalDecoder.Output} interface that writes the resulting
 * images to a {@link BufferedImage} object. A callback can be provided that gets run when
 * an image has been completed.
 */
public final class BufferedImageOutput implements VgaSignalDecoder.Output {

	private final BufferedImage image;
	private final Runnable frameFinishedCallback;

	public BufferedImageOutput(int width, int height, Runnable frameFinishedCallback) {
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		this.frameFinishedCallback = frameFinishedCallback;
	}

	@Override
	public void outputPixel(int x, int y, int r, int g, int b) {
		image.setRGB(x, y, ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff));
	}

	@Override
	public void onFrameFinished() {
		frameFinishedCallback.run();
	}

	public BufferedImage getImage() {
		return image;
	}

}
