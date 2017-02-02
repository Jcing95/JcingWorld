package org.jcing.jcingworld.engine.imagery;

public class TextureAtlas extends BaseImage {
	
	protected int textureSize;
	protected int rows;
	
	
	public TextureAtlas(BaseImage base, int textureSize) {
		super(base);
		rows = width/textureSize;
		this.textureSize = textureSize;	
	}


}
