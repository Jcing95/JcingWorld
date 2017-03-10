package org.jcing.jcingworld.editor.test;
import java.awt.Color;

import org.jcing.jcingworld.engine.io.Mouse;
import org.jcing.windowframework.Pane;
import org.jcing.windowframework.TextField;
import org.jcing.windowframework.Window;
import org.jcing.windowframework.decorations.Border;

public class FrameWorkTest extends Window{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8408125424589550656L;

	public static void main(String args[]) {
		new FrameWorkTest();		
	}
	
	
	TextField testField;
	Pane testpane;
	
	public FrameWorkTest(){
		testField = new TextField(50, 50, "Hey du, ich bin ein Textfeld ;*");
		testField.addDecoration(new Border(new Color(0, 0, 0, 100), 2));
		testField.setBackground(new Color(155, 15, 35, 150));
		testField.enableShadow(true);
//		testField.addShadow(4, 4);
		testpane = new Pane(50, 50, 540, 380);
		testpane.setBackground(Color.WHITE.darker().darker().darker());
		testpane.enableShadow(true);
//		testpane.addShadow(15, 15);
		testpane.add(testField);
		comps.add(testpane);
		activate();	
	}
	
	int panepos = 20;
	int summand = 1;
	
	@Override
	public void tick(){
		if(Mouse.button[0])
			testpane.push(Mouse.deltaX, Mouse.deltaY);
	}
	
}
