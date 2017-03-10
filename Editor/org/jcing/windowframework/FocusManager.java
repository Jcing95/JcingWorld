package org.jcing.windowframework;

import java.util.LinkedList;

public class FocusManager {
	
	public LinkedList<Focus> comps;
	
	public FocusManager(){
		comps = new LinkedList<Focus>();
	}

	public void addFocus(Focus focus) {
		comps.add(focus); 
	}
	
	public void evaluateClick(int x, int y) {
		Focus last = null;
		for (Focus focus : comps) {
			if(focus.evaluate(x,y))
				last = focus;
		}
		if(last != null){
			last.setFocus(true);
		}
	}
	
	
	
}
