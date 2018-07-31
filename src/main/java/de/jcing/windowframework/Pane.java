package de.jcing.windowframework;

import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

import de.jcing.windowframework.io.Mouse;

public class Pane extends Component implements Container {

	public LinkedList<Component> comps;

	public Pane(int x, int y, int width, int height) {
		super(x, y, width, height);
		comps = new LinkedList<Component>();
	}

	public void add(Component c) {
		c.setContainer(this);
		comps.add(c);
	}

	@Override
	public void evaluateMouse(Mouse mouse) {
		super.evaluateMouse(mouse);
		for (Component component : comps) {
			component.evaluateMouse(mouse);
		}
	}

	@Override
	public boolean evaluateClick(int x, int y) {
		Iterator<Component> i = comps.descendingIterator();
		while (i.hasNext()) {
			if (i.next().evaluateClick(x - this.x, y - this.y))
				return true;
		}

		return super.evaluateClick(x, y);
	}

	@Override
	public boolean getFocus() {
		if (focus)
			return true;
		for (Component component : comps) {
			if (component.getFocus())
				return true;
		}
		return false;
	}

	@Override
	public void paint(Graphics g) {
		for (Component component : comps) {
			component.print(g);
		}
	}

	@Override
	public void sort() {
		comps.sort(null);
	}

	@Override
	public Window getWin() {
		return container.getWin();
	}

	@Override
	public int setWithXBounds(int x, int width) {
		if (x < 0)
			return 0;
		if (x > this.width - width)
			return this.width - width;
		return x;
	}

	@Override
	public int setWithYBounds(int y, int height) {
		if (y < 0)
			return 0;
		if (y > this.height - height)
			return this.height - height;
		return y;
	}

}
