package org.jcing.windowframework;

public class Focus{
	
	private boolean focus;
	private FocusManager fm;
	private Component c;
	//TODO: may deletion of focus
	
	public Focus(FocusManager fm, Component c){
		fm.addFocus(this);
		this.fm = fm;
	}
	
	public void setFocus(boolean focus){
		this.focus = focus;
	}
	
	public boolean getFocus(){
		return focus;
	}
	
	public boolean evaluate(int x, int y){
		return c.contains(x,y);
	}
	
}
