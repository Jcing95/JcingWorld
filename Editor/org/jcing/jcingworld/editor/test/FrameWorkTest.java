package org.jcing.jcingworld.editor.test;
import java.awt.Color;
import java.awt.Point;

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
	TextField[] texts;
	Pane testpane;
	
	public FrameWorkTest(){
	    texts = new TextField[15];
	    for (int i = 0; i < texts.length; i++) {
            texts[i] = new TextField(50,50*i,"TestTest");
        }
		testField = new TextField(50, 50, "Hey du, ich bin ein Textfeld ;*");
		testField.addDecoration(new Border(new Color(0, 0, 0, 100), 2));
		testField.setBackground(new Color(155, 15, 35, 150));
		testField.enableShadow(true);
		testpane = new Pane(50, 50, 540, 380);
		testpane.setBackground(Color.WHITE.darker().darker().darker());
		testpane.enableShadow(true);
		testpane.add(testField);
		for (int i = 0; i < texts.length; i++) {
            testpane.add(texts[i]);
        }
		add(testpane);
		activate();	
	}
	
	int panepos = 20;
	int summand = 1;
	
	Point lastPos;
	
	@Override
	public void tick(){
		if(mouse.getButton()[0]){
			
		}
		if(testpane.getFocus())
		    System.out.println("true");
	}
	
}
