package org.jcing.jcingworld.terrain;

import java.awt.Graphics;

import javax.swing.JFrame;

public class WorldSaverFrame extends JFrame{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5613620728153360619L;

	public WorldSaverFrame(){
		setSize(300,200);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public double percent;
	public void paint(Graphics g){
		g.fillRect(0, 0, (int)(300*percent), 200);
	}
}
