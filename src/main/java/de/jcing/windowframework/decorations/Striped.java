package de.jcing.windowframework.decorations;

import java.awt.Color;
import java.awt.Graphics;

import de.jcing.windowframework.Component;

public class Striped extends Decoration {

	public static final int DIAGONAL = 0;

	protected int diagXDelta = 3;

	protected Color color;
	protected int style;

	public Striped(Color color, int style) {
		this.color = color;
		this.style = style;
	}

	@Override
	public void paint(Graphics g, Component c) {
		g.setColor(color);
		switch (style) {
		case 0:
			int rX = c.getHeight();
			int x = -rX;
			int h = rX;
			while (x < c.getWidth()) {
				g.drawLine(x, h, x + rX, 0);
				x += diagXDelta;
			}
		}
	}

	public int getDiagXDelta() {
		return diagXDelta;
	}

	public void setDiagXDelta(int diagXDelta) {
		this.diagXDelta = diagXDelta;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

}
