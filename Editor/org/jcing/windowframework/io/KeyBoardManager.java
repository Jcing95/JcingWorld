package org.jcing.windowframework.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class KeyBoardManager implements KeyListener {

	private LinkedList<String> lastStrings;
	private String input;

	public KeyBoardManager() {
		input = "";
		lastStrings = new LinkedList<String>();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		input += e.getKeyChar();
		System.out.println(input);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		KeyBoard.press(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		KeyBoard.release(e.getKeyCode());
		if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			lastStrings.addFirst(input);
			input = "";
		}
	}

}
