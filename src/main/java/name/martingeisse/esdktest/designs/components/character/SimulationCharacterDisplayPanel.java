/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdktest.designs.components.character;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class SimulationCharacterDisplayPanel extends JPanel {

	private final SimulationCharacterDisplay display;
	private final BufferedImage image;
	private final LinkedList<Byte> inputBuffer;

	public SimulationCharacterDisplayPanel(SimulationCharacterDisplay display) {
		super(false);
		this.display = display;
		this.image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
		this.inputBuffer = new LinkedList<>();

		setFocusable(true);
		setSize(640, 480);
		setPreferredSize(new Dimension(640, 480));
		addKeyListener(new KeyAdapter() {

			private final KeyCodeTranslator translator = new KeyCodeTranslator();

			@Override
			public void keyPressed(KeyEvent e) {
				handle(translator.translate(e.getKeyCode(), false));
			}

			@Override
			public void keyReleased(KeyEvent e) {
				handle(translator.translate(e.getKeyCode(), true));
			}

			private void handle(byte[] bytes) {
				if (bytes != null) {
					for (byte b : bytes) {
						inputBuffer.offer(b);
					}
				}
			}

		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		for (int matrixY = 0; matrixY < 30; matrixY++) {
			int matrixRowBase = matrixY << 7;
			for (int matrixX = 0; matrixX < 80; matrixX++) {
				int asciiCode = display.characterMatrix[matrixRowBase + matrixX] & 0xff;
				byte[] characterPixels = CharacterGenerator.CHARACTER_DATA[asciiCode];
				for (int pixelY = 0; pixelY < 16; pixelY++) {
					byte pixelRow = characterPixels[pixelY];
					for (int pixelX = 0; pixelX < 8; pixelX++) {
						int rgb = ((pixelRow & 1) == 0 ? 0 : 0xc0c0c0);
						image.setRGB((matrixX << 3) + pixelX, (matrixY << 4) + pixelY, rgb);
						pixelRow >>= 1;
					}
				}
			}
		}
		g.drawImage(image, 0, 0, null);
	}

	public byte readInput() {
		return inputBuffer.isEmpty() ? 0 : inputBuffer.poll();
	}

	// TODO refactor with MonitorPanel
	public static JFrame openWindow(SimulationCharacterDisplayPanel displayPanel, String title) {
		JFrame frame = new JFrame(title);
		frame.add(displayPanel);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
		new Timer(500, event -> displayPanel.repaint()).start();
		return frame;
	}

}
