package de.jcing.windowframework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import de.jcing.windowframework.decorations.Border;
import de.jcing.windowframework.decorations.Decoration;
import de.jcing.windowframework.io.Mouse;

public abstract class Component implements Comparable<Component> {

	protected int width, height;
	protected int x, y;
	protected BufferedImage img;
	protected LinkedList<Decoration> decorations;
	protected Color background, foreground;

	protected boolean focus;
	protected boolean transparent;

	protected Color shadow;
	protected static final float shadowColorFactor = 0.7f;
	protected static final float shadowAlphaFactor = 0.3f;
	protected int shadowX, shadowY;
	protected boolean hasShadow;
	protected int shadowBlurIterations;
	protected BufferedImage shadowPic;
	protected float shadowSmoothnes;

	protected boolean movable;

	protected Container container;

	public static Decoration focused = new Border(Color.cyan, 2);

	protected int renderPriority;

	protected Anchor anchor;

	protected boolean hovered;

	public Component(int x, int y, int width, int height) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		background = new Color(20, 200, 50);
		foreground = new Color(10, 15, 50);
		decorations = new LinkedList<Decoration>();
		shadowX = (int) Math.sqrt(width * height) / 20;
		shadowY = (int) Math.sqrt(width * height) / 20;
		shadowSmoothnes = 0.9f;
		ComputeShadow(background);
		hasShadow = false;
		renderPriority = 0;
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		movable = false;
	}

	public Anchor getAnchor() {
		return anchor;
	}

	public void setAnchor(Anchor anchor) {
		this.anchor = anchor;
		anchor.add(this);
		anchor.setContainer(container);
	}

	protected void setContainer(Container c) {
		this.container = c;
		if (anchor != null)
			anchor.setContainer(container);
	}

	public void push(int deltaX, int deltaY) {
		move(x + deltaX, y + deltaY);
	}

	public void move(int x, int y) {
		this.x = container.setWithXBounds(x, width);
		this.y = container.setWithYBounds(y, height);
	}

	// private int lastX, lastY;

	public void moveWithMouse(Mouse mouse) {
		push(mouse.getDeltaX(), mouse.getDeltaY());
	}

	private void ComputeShadow(Color c) {
		shadowPic = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		if (c.getAlpha() < 200)
			shadow = new Color((int) (background.getRed() * shadowColorFactor), (int) (background.getGreen() * shadowColorFactor),
					(int) (background.getBlue() * shadowColorFactor), (int) (background.getAlpha() * shadowAlphaFactor));
		shadow = new Color(0, 0, 0, 100);

		if (shadowX > shadowY)
			shadowBlurIterations = Math.round(shadowX * shadowSmoothnes);
		else
			shadowBlurIterations = Math.round(shadowY * shadowSmoothnes);
		shadow = new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(), (int) (shadow.getAlpha() / shadowBlurIterations));

		Graphics gr = shadowPic.getGraphics();
		gr.setColor(shadow);
		for (int i = 0; i < shadowBlurIterations; i++) {
			gr.fillRect((int) (i / (float) shadowBlurIterations * shadowX), (int) (i / (float) shadowBlurIterations * shadowY),
					width - 2 * (int) (i / (float) shadowBlurIterations * shadowX), height - 2 * (int) (i / (float) shadowBlurIterations * shadowY));
		}

	}

	public boolean getFocus() {
		return focus;
	}

	@Override
	public int compareTo(Component o) {
		return Integer.compare(o.renderPriority, renderPriority);
	}

	public void print(Graphics gr) {
		print(gr, 0, 0);
	}

	public void print(Graphics gr, int xOffset, int yOffset) {
		Graphics2D g = (Graphics2D) img.getGraphics();

		if (!transparent) {
			if (hasShadow) {
				gr.drawImage(shadowPic, x + shadowX + xOffset, y + shadowY + yOffset, null);
			}
			g.setBackground(background);
			g.clearRect(0, 0, width, height);
			g.setColor(foreground);
			if (focus)
				focused.print(g, this); // TODO: remove Focushighlight
		} else {
			g.setBackground(new Color(0, 0, 0, 0));
			g.clearRect(0, 0, width, height);
		}
		for (Decoration decoration : decorations) {
			decoration.print(img.getGraphics(), this);
		}
		if (anchor != null)
			anchor.print(g);

		g.setColor(foreground);
		paint(g);
		gr.drawImage(img, x + xOffset, y + yOffset, null);
		g.dispose();
	}

	public abstract void paint(Graphics g);

	public void addDecoration(Decoration d) {
		decorations.add(d);
	}

	public void enableShadow(boolean enabled) {
		hasShadow = true;
		ComputeShadow(background);
	}

	public void addShadow(int offsetX, int offsetY) {
		this.shadowX = offsetX;
		this.shadowY = offsetY;
		enableShadow(true);
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
		ComputeShadow(background);
	}

	public Color getForeground() {
		return foreground;
	}

	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	protected void setFocus(boolean focus) {
		if (focus) {
			renderPriority = -10;
			container.getWin().setFocused(this);
			container.sort();
			this.focus = true;
		} else {
			renderPriority = 0;
			this.focus = false;
		}
	}

	public boolean evaluateClick(Mouse mouse, boolean pressed) {
		if (pressed)
			return evaluateClick(mouse.getPosX(), mouse.getPosY());
		return false;
	}

	public boolean evaluateClick(int x, int y) {
		if (anchor != null) {
			if (anchor.evaluateClick(x - this.x, y - this.y))
				return true;
		}
		if (contains(x, y)) {
			setFocus(true);
		} else
			setFocus(false);
		return focus;
	}

	public boolean contains(int x2, int y2) {
		if (x <= x2 && y <= y2 && x + width >= x2 && y + height >= y2)
			return true;
		return false;
	}

	public void evaluateMouse(Mouse mouse) {
		if (contains(mouse.getPosX(), mouse.getPosY()))
			hovered = true;
		else
			hovered = false;
		if (anchor != null)
			anchor.evaluateMouse(mouse);
		if (focus) {
			if (movable && mouse.getButton()[0]) {
				moveWithMouse(mouse);
			}
		}
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public int getRenderPriority() {
		return renderPriority;
	}

	public void setRenderPriority(int renderPriority) {
		this.renderPriority = renderPriority;
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
	}

}
