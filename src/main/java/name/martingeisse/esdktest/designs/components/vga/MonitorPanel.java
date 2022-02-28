/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdktest.designs.components.vga;

import javax.swing.*;
import java.awt.*;

/**
 * For simplicity, this class has no front/back porch detection. The width and height as passed to this class must
 * include all porches, and they will be displayed. It should not include the sync pulses.
 */
public class MonitorPanel extends JPanel {

	private final Monitor monitor;
	private final BufferedImageOutput imageOutput;

	public MonitorPanel(Monitor monitor, int width, int height, int clocksPerPixel) {
		super(false);
		this.monitor = monitor;
		this.imageOutput = new BufferedImageOutput(width, height, this::repaint);
		monitor.vgaSignalDecoder(new VgaSignalDecoder(width, height, clocksPerPixel, imageOutput));
		setSize(width, height);
		setPreferredSize(new Dimension(width, height));
	}

	public Monitor getMonitor() {
		return monitor;
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(imageOutput.getImage(), 0, 0, null);
	}

	public static void openWindow(MonitorPanel monitorPanel, String title) {
		JFrame frame = new JFrame(title);
		frame.add(monitorPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		new Timer(500, event -> monitorPanel.repaint()).start();
	}

}
