package org.jcing.jcingworld.engine.gui;

import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.lwjgl.util.vector.Vector2f;

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
