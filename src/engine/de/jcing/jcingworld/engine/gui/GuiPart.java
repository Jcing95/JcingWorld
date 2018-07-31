package de.jcing.jcingworld.engine.gui;

import org.lwjgl.util.vector.Vector2f;

import de.jcing.jcingworld.engine.imagery.BaseImage;

public class GuiPart extends BaseImage {

	private Vector2f pos;
	private Vector2f scale;

	public GuiPart(BaseImage base, Vector2f pos, Vector2f scale) {
		super(base);
		this.pos = pos;
		this.scale = scale;
	}

	public Vector2f getPos() {
		return pos;
	}

	public Vector2f getScale() {
		return scale;
	}

}
