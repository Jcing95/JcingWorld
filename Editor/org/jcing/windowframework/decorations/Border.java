package org.jcing.windowframework.decorations;

import java.awt.Color;
import java.awt.Graphics;

import org.jcing.windowframework.Component;

public class Border extends Decoration {

	protected Color color;
	protected int boldness;

	public Border(Color c, int boldness) {
		this.color = c;
		this.boldness = boldness;
	}

	@Override
	public void paint(Graphics g, Component comp) {
		g.setColor(color);
		g.fillRect(boldness, 0, comp.getWidth() - boldness, boldness); // LT -> RT
		g.fillRect(comp.getWidth() - boldness, boldness, boldness, comp.getHeight()); // RT -> RB
		g.fillRect(0, comp.getHeight() - boldness, comp.getWidth() - boldness, boldness); // LB -> RB
		g.fillRect(0, 0, boldness, comp.getHeight() - boldness);// LT - LB
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getBoldness() {
		return boldness;
	}

	public void setBoldness(int boldness) {
		this.boldness = boldness;
	}

}
