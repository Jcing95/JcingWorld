package org.jcing.jcingworld.editor.test;
import java.awt.Color;

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
	TextField[][] texts;
	Pane testpane;
	
	int xPanes = 8, yPanes = 5;
	String testFieldString = "12!";
	
	
	public FrameWorkTest(){
	    texts = new TextField[xPanes][yPanes];
	    int width = new TextField(50,50,testFieldString).getWidth()+10;
	    for (int i = 0; i < xPanes; i++) {
	    	 for (int j = 0; j < yPanes; j++) {
	             texts[i][j] = new TextField(width*(i+1),50+50*j,""+i+j+"!");
	             texts[i][j].addShadow(5,5);
	         }
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
            for (int j = 0; j < texts[i].length; j++) {
            	testpane.add(texts[i][j]);
			}
        }
//		testpane.setTransparent(true);
		add(testpane);

		activate();	
		System.out.println("fin");
	}

	@Override
	public void tick(){
		
	}
	
}
