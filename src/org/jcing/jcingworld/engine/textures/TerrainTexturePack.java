package org.jcing.jcingworld.engine.textures;

public class TerrainTexturePack {

	private BaseTexture backgroundTexture;
	private BaseTexture rTexture;
	private BaseTexture gTexture;
	private BaseTexture bTexture;
	private BaseTexture blackTexture;

	public TerrainTexturePack(BaseTexture backgroundTexture, BaseTexture rTexture, BaseTexture gTexture, BaseTexture bTexture,
			BaseTexture blackTexture) {
		this.backgroundTexture = backgroundTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
		this.blackTexture = blackTexture;
	}

	public BaseTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public BaseTexture getrTexture() {
		return rTexture;
	}

	public BaseTexture getgTexture() {
		return gTexture;
	}

	public BaseTexture getbTexture() {
		return bTexture;
	}

	public BaseTexture getblackTexture() {
		return blackTexture;
	}

}
