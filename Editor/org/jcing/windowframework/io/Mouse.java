package org.jcing.windowframework.io;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import org.jcing.windowframework.Window;

public class Mouse implements MouseListener, MouseMotionListener {

	Window win;

	boolean button[];

	int posX, lastPosX;
	int posY, lastPosY;

	int lastClickX;
	int lastClickY;

	int deltaX;
	int deltaY;

	boolean drag;

	public Mouse(Window win) {
		this.win = win;
		button = new boolean[3];
	}

	public void addMouseWheelListener(MouseWheelListener l) {
		win.addMouseWheelListener(l);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		posX = e.getX();
		posY = e.getY();
		drag = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		posX = e.getX();
		posY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() - 1 < 4)
			button[e.getButton() - 1] = true;
		lastClickX = e.getX();
		lastClickY = e.getY();
		win.processClick(this, true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() - 1 < 4)
			button[e.getButton() - 1] = false;
		drag = false;
		win.processClick(this, false);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	

	}

	@Override
	public void mouseExited(MouseEvent e) {


	}

	public boolean[] getButton() {
		return button;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getLastClickX() {
		return lastClickX;
	}

	public int getLastClickY() {
		return lastClickY;
	}

	public boolean isDrag() {
		return drag;
	}

	public int getDeltaX() {
		return deltaX;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public void move() {
		deltaX = posX - lastPosX;
		deltaY = posY - lastPosY;
		lastPosX = posX;
		lastPosY = posY;
	}

}
