package de.jcing.windowframework;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;

import de.jcing.windowframework.io.Mouse;

public class Anchor extends Component {

	protected LinkedList<Component> anchored;

	public Anchor(int x, int y, int width, int height) {
		super(x, y, width, height);
		anchored = new LinkedList<Component>();
		movable = true;
		setBackground(new Color(90, 40, 10, 75));
	}

	@Override
	public void paint(Graphics g) {

	}

	public void add(Component c) {
		anchored.add(c);
	}

	@Override
	public void evaluateMouse(Mouse mouse) {
		if (focus && mouse.getButton()[0]) {
			moveWithMouse(mouse);
		}
	}

	@Override
	public void push(int x, int y) {
		for (Component component : anchored) {
			component.push(x, y);
		}
	}
}
