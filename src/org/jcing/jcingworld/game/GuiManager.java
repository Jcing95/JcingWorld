package org.jcing.jcingworld.game;

import java.util.LinkedList;
import java.util.List;

import org.jcing.jcingworld.engine.gui.GuiPart;
import org.jcing.jcingworld.main.MainLoop;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;

public class GuiManager {
	
	
	private List<GuiPart> parts;
	
	public GuiManager(){
		parts = new LinkedList<GuiPart>();
		parts.add(new GuiPart(MainLoop.getLoader().loadTexture("gui/crosshair", false),new Vector2f(0,0), Maths.calcOrigSizeScale(32, 32)));
	}

	public List<GuiPart> getParts() {
		return parts;
	}
	
	
}
