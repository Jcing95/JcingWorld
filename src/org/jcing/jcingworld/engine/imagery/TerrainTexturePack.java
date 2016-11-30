package org.jcing.jcingworld.engine.imagery;

public class TerrainTexturePack {

	private BaseImage backgroundTexture;
	private BaseImage rTexture;
	private BaseImage gTexture;
	private BaseImage bTexture;
	private BaseImage blackTexture;

	public TerrainTexturePack(BaseImage backgroundTexture, BaseImage rTexture, BaseImage gTexture, BaseImage bTexture,
			BaseImage blackTexture) {
		this.backgroundTexture = backgroundTexture;
		this.rTexture = rTexture;
		this.gTexture = gTexture;
		this.bTexture = bTexture;
		this.blackTexture = blackTexture;
	}

	public BaseImage getBackgroundTexture() {
		return backgroundTexture;
	}

	public BaseImage getrTexture() {
		return rTexture;
	}

	public BaseImage getgTexture() {
		return gTexture;
	}

	public BaseImage getbTexture() {
		return bTexture;
	}

	public BaseImage getblackTexture() {
		return blackTexture;
	}

}
