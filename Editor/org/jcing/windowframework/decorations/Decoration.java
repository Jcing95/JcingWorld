package org.jcing.windowframework.decorations;

import java.awt.Color;
import java.awt.Graphics;

import org.jcing.windowframework.Component;

public abstract class Decoration {
	
	protected Component comp;
	
	public void print(Graphics g){
		Color old = g.getColor();
		paint(g);
		g.setColor(old);
	}
	
	public abstract Decoration getInstance(Component c);
	
	public abstract void paint(Graphics g);

	public void setComp(Component component) {
		comp = component;
	}

}