package org.jcing.windowframework;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.util.LinkedList;

import javax.swing.JFrame;

import org.jcing.jcingworld.editor.test.BiomeTest;
import org.jcing.jcingworld.engine.io.Mouse;
import org.jcing.options.JTable;
import org.jcing.windowframework.decorations.Border;
import org.jcing.windowframework.io.KeyBoardManager;
import org.lwjgl.util.vector.Vector2f;

public class Window extends Canvas implements ActionListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8255319694373975038L;
	private static final double VERSION = 0.2;
	JFrame win;

	KeyBoardManager textHandler;

	BiomeTest biomeTest;

	Dimension size;
	
	Thread repainter;
	
	protected LinkedList<Component> comps;

	public Window() {
		
		comps = new LinkedList<Component>();
		textHandler = new KeyBoardManager();
		size = new Dimension(640,480);
		setBackground(new Color(15,20,40));
		addKeyListener(textHandler);
		addMouseMotionListener(this);
			
	}

	public void activate(){
		win = new JFrame("Jcing WindowFramework" + VERSION);
		this.setSize(size);
		win.add(this);
		win.pack();
		win.setVisible(true);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.createBufferStrategy(3);
		repainter = new Thread() {
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
	
	public void tick() {
		
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
		for (Component component : comps) {
			component.print(g);
		}
		g.dispose();
		bs.show();
	}

	public void paint(Graphics g) {
		
		for (Component component : comps) {
			component.print(g);
		}
		
		// if(biomeTest == null){
		// System.out.println("lulz");
		// return;
		// }
		// if(biomeTest.getImg() == null){
		// System.out.println("lelel");
		// return;
		// }
		// g.drawImage(biomeTest.getImg().getScaledInstance(getWidth(),
		// getHeight(), BufferedImage.SCALE_SMOOTH), 0, 0,null);
//		testField.print(g);
		// System.out.println("frame" + biomeTest.getImg());
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
