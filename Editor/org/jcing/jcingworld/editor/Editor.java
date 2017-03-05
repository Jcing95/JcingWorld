package org.jcing.jcingworld.editor;

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
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.jcing.jcingworld.editor.test.BiomeTest;
import org.jcing.options.JTable;
import org.lwjgl.util.vector.Vector2f;

public class Editor extends Canvas implements ActionListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8255319694373975038L;
	private static final double VERSION = 0.1;
	JFrame win;

	TextHandler textHandler;
	JButton test;
	
	BiomeTest biomeTest;

	public Editor() {
		win = new JFrame("Jcing Filetable Editor " + VERSION);

		this.setSize(new Dimension(640, 480));

		win.add(this);
		// win.add(test);
		win.pack();
		win.setVisible(true);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.createBufferStrategy(3);
		setBackground(new Color(3, 24, 133));

		textHandler = new TextHandler();

		addKeyListener(textHandler);
		addMouseMotionListener(this);

		JTable<Integer> test = new JTable<Integer>();
		test.add(5);
		test.add(7);
		test.add(3);
		test.enableIdentifying();
		test.identify(1, "test");
		biomeTest = new BiomeTest(8000);

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
		biomeTest.init(new Vector2f(0, 0), 100f);
	}

	public static void main(String args[]) {
		new Editor();		
	}

	private void tick() {

	}

	public void render() {
		// System.out.println("render");
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		paint(g);

		g.dispose();
		bs.show();
	}

	public void paint(Graphics g) {
		if(biomeTest == null){
			System.out.println("lulz");
			return;
		}
		if(biomeTest.getImg() == null){
			System.out.println("lelel");
			return;
		}
		g.drawImage(biomeTest.getImg().getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_SMOOTH), 0, 0,null);
//		System.out.println("frame" + biomeTest.getImg());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

}
