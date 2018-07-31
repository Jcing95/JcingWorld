package de.jcing.windowframework;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import de.jcing.windowframework.io.Mouse;

public class Button extends TextField {

	protected LinkedList<ActionListener> listeners;
	protected Mouse mouse;

	protected boolean pressed;

	public Button(int x, int y, String label) {
		super(x, y, label);
		listeners = new LinkedList<ActionListener>();
	}

	public void addActionListener(ActionListener l) {
		listeners.add(l);
	}

	@Override
	public boolean evaluateClick(Mouse mouse, boolean pressed) {
		if (pressed) {
			this.pressed = super.evaluateClick(mouse, pressed);
			return this.pressed;
		}
		this.pressed = false;
		if (contains(mouse.getPosX(), mouse.getPosY())) {
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
			for (ActionListener actionListener : listeners) {
				actionListener.actionPerformed(event);
			}
		}
		return true;
	}

	@Override
	public void setContainer(Container c) {
		super.setContainer(c);
		mouse = c.getWin().getMouse();
	}

	@Override
	public void paint(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;
		if (hovered)
			g.setBackground(background.brighter());
		if (pressed)
			g.setBackground(background.darker());
		g.clearRect(0, 0, width, height);
		super.paint(g);

	}
}
