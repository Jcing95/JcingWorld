package org.jcing.windowframework;

import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

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
	public void setContainer(Container c) {
		this.container = c;

	}

	@Override
	public boolean evaluateClick(int x, int y) {
		Iterator<Component> i = comps.descendingIterator();
		while (i.hasNext()) {
			if (i.next().evaluateClick(x - this.x, y - this.y))
				return true;
		}

		if (contains(x, y)) {
			setFocus(true);
		} else
			setFocus(false);
		return focus;
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

}
