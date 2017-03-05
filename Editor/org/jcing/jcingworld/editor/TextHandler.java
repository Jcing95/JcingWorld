package org.jcing.jcingworld.editor;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

public class TextHandler implements KeyListener {

	private LinkedList<String> lastStrings;
	private String input;
	
	
	public TextHandler() {
		input = "";
		lastStrings = new LinkedList<String>();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		input+= e.getKeyChar();
		
		System.out.println(input);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE){
			lastStrings.addFirst(input);
			input = "";
		}
	}

}
