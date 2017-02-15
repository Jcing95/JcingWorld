package org.jcing.filetable;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.jcing.options.JTable;

public class FileTableEditor extends Canvas implements ActionListener, KeyListener, MouseMotionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8255319694373975038L;
	private static final double VERSION = 0.1;
	JFrame win;

	JButton test;
	
	public FileTableEditor() {
		win = new JFrame("Jcing Filetable Editor " + VERSION);

		this.setSize(new Dimension(1280, 720));

		win.add(this);
//		win.add(test);
		win.pack();
		win.setVisible(true);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.createBufferStrategy(3);
		setBackground(new Color(3,24,133));
		addKeyListener(this);
		addMouseMotionListener(this);
		
		JTable<Integer> test = new JTable<Integer>();
		test.add(5);
		test.add(7);
		test.add(3);
		test.enableIdentifying();
		test.identify(1, "test");
		
		Thread repainter = new Thread() {
			public void run() {
				while (true) {
					tick();
					render();
					try {
						sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		repainter.start();
		
		
	}

	public static void main(String args[]) {
		new FileTableEditor();
	}

	private void tick() {

	}


	public void render() {
		//		System.out.println("render");
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		//		m.getGame().getActiveLevel().paint(g);
		//
		//		m.getGame().getActiveLevel().paintMovables(g);

		paint(g);
		//		if (showCreator)
		//			m.getCreator().paint(g);

		g.dispose();
		bs.show();
	}

	public void paint(Graphics g) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}
	
	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

}
