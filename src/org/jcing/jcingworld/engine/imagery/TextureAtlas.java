package org.jcing.jcingworld.engine.imagery;

import org.jcing.jcingworld.logging.Logs;

public class TextureAtlas extends BaseImage {
	
	protected int textureSize;
	protected int rows;
	
	
	public TextureAtlas(BaseImage base, int textureSize) {
		super(base);
		rows = width/textureSize;
		Logs.atlas.println("creating TextureAtlas: " + rows + " rows x " + textureSize +"px");
		this.textureSize = textureSize;	
	}
	
	protected TextureAtlas(BaseImage base){
	    super(base);
	}

    public int getTextureSize() {
        return textureSize;
    }

    public int getRows() {
        return rows;
    }
    
}
