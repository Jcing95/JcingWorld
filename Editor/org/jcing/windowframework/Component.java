package org.jcing.windowframework;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import org.jcing.windowframework.decorations.Border;
import org.jcing.windowframework.decorations.Decoration;
import org.jcing.windowframework.io.Mouse;

public abstract class Component {

    protected int width, height;
    protected int x, y;
    protected BufferedImage img;
    protected LinkedList<Decoration> decorations;
    protected Color background, foreground;

    protected Window win;

    protected boolean focus;
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
    
    protected Decoration focusDecoration;

    public static Decoration focused = new Border(Color.cyan, 2);

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
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        movable = true;
        focusDecoration = focused.getInstance(this);
    }

    protected void setWin(Window win) {
        this.win = win;
    }

    public void push(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //	private int lastX, lastY;

    public void moveWithMouse(Mouse mouse) {
        push(mouse.getDeltaX(), mouse.getDeltaY());
    }

    private void ComputeShadow(Color c) {
        shadowPic = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        if (c.getAlpha() < 200)
            shadow = new Color((int) (background.getRed() * shadowColorFactor),
                    (int) (background.getGreen() * shadowColorFactor),
                    (int) (background.getBlue() * shadowColorFactor),
                    (int) (background.getAlpha() * shadowAlphaFactor));
        shadow = new Color(0, 0, 0, 100);

        if (shadowX > shadowY)
            shadowBlurIterations = Math.round(shadowX * shadowSmoothnes);
        else
            shadowBlurIterations = Math.round(shadowY * shadowSmoothnes);
        shadow = new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(),
                (int) (shadow.getAlpha() / shadowBlurIterations));

        Graphics gr = shadowPic.getGraphics();
        gr.setColor(shadow);
        for (int i = 0; i < shadowBlurIterations; i++) {
            gr.fillRect((int) (i / (float) shadowBlurIterations * shadowX),
                    (int) (i / (float) shadowBlurIterations * shadowY),
                    width - 2 * (int) (i / (float) shadowBlurIterations * shadowX),
                    height - 2 * (int) (i / (float) shadowBlurIterations * shadowY));
        }

    }

    public boolean getFocus() {
        return focus;
    }

    public void print(Graphics gr) {
        if (hasShadow) {
            gr.drawImage(shadowPic, x + shadowX, y + shadowY, null);
        }
        Graphics2D g = (Graphics2D) img.getGraphics();
        // g.setBackground(new Color(0,0,0,0));
        // g.clearRect(0, 0, width, height);

        //		if(focus.getFocus())
        //		    this.setBackground(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255),150));
        g.setBackground(background);
        g.clearRect(0, 0, width, height);
        g.setColor(foreground);
        if (focus)
            focusDecoration.print(g);
        for (Decoration decoration : decorations) {
            decoration.print(img.getGraphics());
        }
        g.setColor(foreground);
        paint(img.getGraphics());
        gr.drawImage(img, x, y, null);
        g.dispose();
    }

    public abstract void paint(Graphics g);

    public void addDecoration(Decoration d) {
        d.setComp(this);
        decorations.add(d);
    }

    public void enableShadow(boolean enabled) {
        hasShadow = true;
        ComputeShadow(background);
    }

    public void addShadow(int offsetX, int offsetY) {
        hasShadow = true;
        this.shadowX = offsetX;
        this.shadowY = offsetY;
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
            win.setFocused(this);
            this.focus = true;
        } else {
            this.focus = false;
        }
    }

    public boolean evaluateClick(int x, int y) {
        if (contains(x, y))
            setFocus(true);
        else
            setFocus(false);
        return focus;
    }

    public boolean contains(int x2, int y2) {
        if (x <= x2 && y <= y2 && x + width >= x2 && y + height >= y2)
            return true;
        return false;
    }

    public void evaluateMouse(Mouse mouse) {
        if (movable && focus) {
            if (mouse.getButton()[0]) {
                moveWithMouse(mouse);
            }
        }
    }

    public void setContainer(Container c) {
     this.container = c;   
    }

}
