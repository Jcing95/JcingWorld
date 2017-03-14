package org.jcing.windowframework;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JFrame;

import org.jcing.windowframework.io.KeyBoardManager;
import org.jcing.windowframework.io.Mouse;

public class Window extends Canvas implements Container {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8255319694373975038L;
	private static final double VERSION = 0.2;
	JFrame win;

	protected KeyBoardManager textHandler;
	protected FocusManager focusmanager;
	protected Mouse mouse;

	Dimension size;

	Thread repainter;

	protected Component focused;

	protected LinkedList<Component> comps;
	// protected LinkedList<Component> focused;

	public Window() {
		comps = new LinkedList<Component>();
		// focused = new LinkedList<Component>();
		focusmanager = new FocusManager();
		textHandler = new KeyBoardManager();
		mouse = new Mouse(this);
		size = new Dimension(640, 480);
		setBackground(new Color(15, 20, 40));
		addKeyListener(textHandler);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
	}

	public void activate() {
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
					manage();
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

	public void add(Component c) {
		c.setContainer(this);
		c.setContainer(this);
		comps.add(c);
	}

	public void setFocused(Component c) {
		if (focused != null)
			focused.setFocus(false);
		focused = c;
	}

	private void manage() {
		mouse.move();
		if (focused != null)
			focused.evaluateMouse(mouse);
	}

	public void processClick(int x, int y) {
		Iterator<Component> i = comps.descendingIterator();
		while(i.hasNext()){
			if(i.next().evaluateClick(x, y))
				return;
		}
//		for (Component component : comps) {
//			component.evaluateClick(x, y);
//		}
	}

	public void print(Graphics g){
		for (Component component : comps) {
			component.print(g);
		}
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
		print(g);
		paint(g);
		for (Component component : comps) {
			component.print(g);
		}
		g.dispose();
		bs.show();
	}

	public void paint(Graphics g) {
		
	}

	@Override
	public void sort() {
		System.out.println();
		comps.sort(null);
	}

	@Override
	public Window getWin() {
		return this;
	}
}
