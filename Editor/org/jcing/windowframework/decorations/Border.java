package org.jcing.windowframework.decorations;

import java.awt.Color;
import java.awt.Graphics;

public class Border extends Decoration{

	
	protected Color color;
	protected int bold;
	
	public Border(Color c, int width){
		this.color = c;
		this.bold = width;
	}
	
	@Override
	public void paint(Graphics g) {
		g.setColor(color);
		g.fillRect(0, 0, comp.getWidth(), bold);
		g.fillRect(comp.getWidth()-bold, 0, bold, comp.getHeight());
		g.fillRect(0, comp.getHeight()-bold, comp.getWidth(),bold);
		g.fillRect(0, 0, bold, comp.getHeight());
	}
	
	
	
}
