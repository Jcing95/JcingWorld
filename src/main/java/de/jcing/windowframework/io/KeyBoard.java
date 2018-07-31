package de.jcing.windowframework.io;

import java.awt.event.KeyEvent;

public class KeyBoard {

	private static boolean[] key = new boolean[KeyEvent.KEY_LAST];
	private static boolean[] toggled = new boolean[KeyEvent.KEY_LAST];

	public static boolean key(int keyID) {
		return key[keyID];
	}

	public static boolean toggled(int keyID) {
		return toggled[keyID];
	}

	public static void press(int keyID) {
		if (keyID >= 0 && keyID < key.length) {
			key[keyID] = true;
			toggled[keyID] = !toggled[keyID];
		} else
			throw new IllegalArgumentException("KeyCode " + keyID + " is not a valid KeyEvent!");
	}

	public static void release(int keyID) {
		if (keyID >= 0 && keyID < key.length)
			key[keyID] = false;
		else
			throw new IllegalArgumentException("KeyCode " + keyID + " is not a valid KeyEvent!");
	}

}
