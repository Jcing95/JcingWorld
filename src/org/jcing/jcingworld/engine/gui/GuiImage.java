package org.jcing.jcingworld.gui;

import org.lwjgl.util.vector.Vector2f;

public class GUIpart {

	private int texture;
	private Vector2f pos;
	private Vector2f scale;

	public GUIpart(int texture, Vector2f pos, Vector2f scale) {
		this.texture = texture;
		this.pos = pos;
		this.scale = scale;
	}

	public int getTexture() {
		return texture;
	}

	public Vector2f getPos() {
		return pos;
	}

	public Vector2f getScale() {
		return scale;
	}

}
