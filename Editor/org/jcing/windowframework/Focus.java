package org.jcing.windowframework;

public class Focus{
	
	private boolean focus;
	private FocusManager fm;
	private Component c;
	//TODO: may deletion of focus
	
	private boolean initialized;
	
	public Focus(){
	}
	
	public Focus(FocusManager fm, Component c){
		fm.addFocus(this);
		this.fm = fm;
		this.c = c;
		initialized = true;
	}
	
	public void setFocus(boolean focus){
		this.focus = focus;
	}
	
	public boolean getFocus(){
		return focus;
	}
	
	public boolean evaluate(int x, int y){
	    if(!initialized)
	        return false;
	    System.out.println("FOCUSED");
		return c.contains(x,y);
	}
	
}
