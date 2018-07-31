package de.jcing.jcingworld.game;

import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import de.jcing.jcingworld.engine.gui.GuiPart;
import de.jcing.jcingworld.main.MainLoop;
import de.jcing.jcingworld.toolbox.Maths;

public class GUIManager {

	private List<GuiPart> parts;

	public GUIManager() {
		parts = new LinkedList<GuiPart>();
		parts.add(new GuiPart(MainLoop.getLoader().loadTexture("gui/crosshair.png", false), new Vector2f(0, 0), Maths.calcOrigSizeScale(32, 32)));
	}

	public List<GuiPart> getParts() {
		return parts;
	}

}
