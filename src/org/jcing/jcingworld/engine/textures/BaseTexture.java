package org.jcing.jcingworld.engine.textures;

public class BaseTexture {
	protected int width, height;
	protected final int ID;	
	
	public BaseTexture(int width, int height, int iD) {
		this.width = width;
		this.height = height;
		ID = iD;
	}
	
	public BaseTexture(BaseTexture base) {
		this.width = base.width;
		this.height = base.height;
		this.ID = base.ID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTextureID() {
		return ID;
	}
	
}
