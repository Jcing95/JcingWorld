package org.jcing.windowframework;

import java.awt.Graphics;
import java.util.LinkedList;

public class Pane extends Component implements Container{

    public LinkedList<Component> comps;

    public Pane(int x, int y, int width, int height) {
        super(x, y, width, height);
        comps = new LinkedList<Component>();
    }

    public void add(Component c) {
        c.setWin(win);
        comps.add(c);
    }

    @Override
    public void setWin(Window win) {
        this.win = win;
        for (Component component : comps) {
            component.setWin(win);
        }
    }

    @Override
    public boolean evaluateClick(int x, int y) {
        for (Component component : comps)
            if (component.evaluateClick(x - this.x, y - this.y))
                return true;
        if (contains(x, y)) {
            setFocus(true);
        } else
            setFocus(false);
        return focus;
    }

    @Override
    public boolean getFocus() {
        if (focus)
            return true;
        for (Component component : comps) {
            if (component.getFocus())
                return true;
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        for (Component component : comps) {
            component.print(g);
        }
    }


}
