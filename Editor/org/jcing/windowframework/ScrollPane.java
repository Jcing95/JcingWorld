package org.jcing.windowframework;

import java.awt.Graphics;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;

import org.jcing.windowframework.io.Mouse;

public class ScrollPane extends Pane implements MouseWheelListener {

	protected int yOffset;
	protected float wheelFactor = 5.75f;
	protected Mouse mouse;

	public ScrollPane(int x, int y, int width, int height) {
		super(x, y, width, height);
		yOffset = 0;
	}

	@Override
	public void setContainer(Container c){
	    super.setContainer(c);
	    mouse = c.getWin().getMouse();
	    mouse.addMouseWheelListener(this);
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
	public int setWithYBounds(int y, int height) {
		return y;
	}

}
