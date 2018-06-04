package org.jcing.windowframework.decorations;

import java.awt.Color;
import java.awt.Graphics;

import org.jcing.windowframework.Component;

public abstract class Decoration {

	public void print(Graphics g, Component c) {
		Color old = g.getColor();
		paint(g, c);
		g.setColor(old);
	}

	public abstract void paint(Graphics g, Component c);

}