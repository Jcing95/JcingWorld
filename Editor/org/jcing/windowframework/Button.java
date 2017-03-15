package org.jcing.windowframework;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import org.jcing.windowframework.io.Mouse;

public class Button extends TextField{

    public LinkedList<ActionListener> listeners;
    public Mouse mouse;
    
    public Button(int x, int y, String label) {
        super(x, y, label);
        listeners = new LinkedList<ActionListener>();
    }
    
    public boolean evaluateClick(Mouse mouse, boolean pressed){
        if(pressed)
            return super.evaluateClick(mouse,pressed);
        for (ActionListener actionListener : listeners) {
            actionListener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,null));
        }
        return true;
    }
    
    @Override
    public void setContainer(Container c){
        super.setContainer(c);
        mouse = c.getWin().getMouse();
    }

}
