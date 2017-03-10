package org.jcing.windowframework;

import java.awt.Graphics;
import java.util.LinkedList;

public class Pane extends Component{

	
	public LinkedList<Component> comps;
	
	public Pane(int x, int y, int width, int height) {
		super(x, y, width, height);
		comps = new LinkedList<Component>();
	}
	
	public void add(Component c){
		comps.add(c);
	}

	@Override
	public void paint(Graphics g) {
		for (Component component : comps) {
			component.print(g);
		}
	}

}
