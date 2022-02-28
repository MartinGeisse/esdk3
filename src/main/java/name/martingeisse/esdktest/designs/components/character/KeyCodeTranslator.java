/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdktest.designs.components.character;

import java.awt.event.KeyEvent;

/**
 * A stateful converter from AWT key codes to simulated keyboard codes.
 */
public class KeyCodeTranslator {

	private static final byte[] simpleTable = new byte[65536];

	static {
		simpleTable[KeyEvent.VK_A] = 'a';
		simpleTable[KeyEvent.VK_B] = 'b';
		simpleTable[KeyEvent.VK_C] = 'c';
		simpleTable[KeyEvent.VK_D] = 'd';
		simpleTable[KeyEvent.VK_E] = 'e';
		simpleTable[KeyEvent.VK_F] = 'f';
		simpleTable[KeyEvent.VK_G] = 'g';
		simpleTable[KeyEvent.VK_H] = 'h';
		simpleTable[KeyEvent.VK_I] = 'i';
		simpleTable[KeyEvent.VK_J] = 'j';
		simpleTable[KeyEvent.VK_K] = 'k';
		simpleTable[KeyEvent.VK_L] = 'l';
		simpleTable[KeyEvent.VK_M] = 'm';
		simpleTable[KeyEvent.VK_N] = 'n';
		simpleTable[KeyEvent.VK_O] = 'o';
		simpleTable[KeyEvent.VK_P] = 'p';
		simpleTable[KeyEvent.VK_Q] = 'q';
		simpleTable[KeyEvent.VK_R] = 'r';
		simpleTable[KeyEvent.VK_S] = 's';
		simpleTable[KeyEvent.VK_T] = 't';
		simpleTable[KeyEvent.VK_U] = 'u';
		simpleTable[KeyEvent.VK_V] = 'v';
		simpleTable[KeyEvent.VK_W] = 'w';
		simpleTable[KeyEvent.VK_X] = 'x';
		simpleTable[KeyEvent.VK_Y] = 'y';
		simpleTable[KeyEvent.VK_Z] = 'z';
		simpleTable[KeyEvent.VK_0] = '0';
		simpleTable[KeyEvent.VK_1] = '1';
		simpleTable[KeyEvent.VK_2] = '2';
		simpleTable[KeyEvent.VK_3] = '3';
		simpleTable[KeyEvent.VK_4] = '4';
		simpleTable[KeyEvent.VK_5] = '5';
		simpleTable[KeyEvent.VK_6] = '6';
		simpleTable[KeyEvent.VK_7] = '7';
		simpleTable[KeyEvent.VK_8] = '8';
		simpleTable[KeyEvent.VK_9] = '9';
		simpleTable[KeyEvent.VK_SPACE] = ' ';
		simpleTable[KeyEvent.VK_ENTER] = '\n';
		simpleTable[KeyEvent.VK_BACK_SPACE] = '\b';
		simpleTable[KeyEvent.VK_DELETE] = 0x7f;
		simpleTable[KeyEvent.VK_UP] = 1;
		simpleTable[KeyEvent.VK_DOWN] = 2;
		simpleTable[KeyEvent.VK_LEFT] = 3;
		simpleTable[KeyEvent.VK_RIGHT] = 4;
	}

	/**
	 * Consumes an AWT key code. If one or more hardware key codes result from it, they are returned as an array,
	 * otherwise returns null.
	 */
	public byte[] translate(int awtKeyCode, boolean release) {
		if (awtKeyCode < 0 || awtKeyCode >= 65536) {
			return null;
		}
		byte simpleResult = simpleTable[awtKeyCode];
		if (simpleResult == 0) {
			return null;
		}
		byte resultByte = (byte) (release ? (simpleResult + 128) : simpleResult);
		return new byte[]{resultByte};
	}

}
