package org.jcing.windowframework.io;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener{

	
	boolean button[];
	
	int posX;
	int posY;
	
	int lastClickX;
	int lastClickY;
	
	boolean drag;
	
	public Mouse(){
		button = new boolean[3];
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton()-1 < 4)
			button[e.getButton()-1] = true;
		lastClickX = e.getX();
		lastClickY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton()-1 < 4)
			button[e.getButton()-1] = false;
		drag = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
