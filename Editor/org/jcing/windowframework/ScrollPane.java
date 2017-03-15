package org.jcing.windowframework;

import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;

import org.jcing.windowframework.io.Mouse;

public class ScrollPane extends Pane implements MouseWheelListener {

	protected int yOffset;
	protected Mouse mouse;
	protected float wheelFactor = 5.75f;

	public ScrollPane(int x, int y, int width, int height, Mouse mouse) {
		super(x, y, width, height);
		this.mouse = mouse;
		mouse.addMouseWheelListener(this);
		yOffset = 0;
	}

	@Override
	public boolean evaluateClick(int x, int y) {
		if (contains(x, y)) {
			Iterator<Component> i = comps.descendingIterator();
			while (i.hasNext()) {
				if (i.next().evaluateClick(x - this.x, y - this.y - yOffset))
					return true;
			}

			// TODO: Component.evaluate Click:
			if (anchor != null) {
				if (anchor.evaluateClick(x - this.x, y - this.y))
					return true;
			}

			setFocus(true);
		} else
			setFocus(false);
		return focus;
	}

	@Override
	public int getHeight() {
		return height - yOffset;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (contains(mouse.getPosX(), mouse.getPosY()))
			yOffset -= e.getWheelRotation() * wheelFactor;
	}

	@Override
	public void paint(Graphics g) {
		for (Component component : comps) {
			component.print(g, 0, yOffset);
		}
	}
	
	@Override
	public int setWithXBounds(int x) {
		if(x<0)
			return 0;
		if(x>width)
			return width;
		return x;
	}

	@Override
	public int setWithYBounds(int y) {
		return y;
	}

}
