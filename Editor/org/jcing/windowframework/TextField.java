package org.jcing.windowframework;

import java.awt.Color;
import java.awt.Font;

public class TextField extends Component{
	
	protected int fontSize = 14;
	protected Font font = new Font("Verdana", Font.PLAIN, fontSize);
	
	protected StringBuffer text;
	
	protected Color background;
	
	public TextField(){
		
	}
	
}
