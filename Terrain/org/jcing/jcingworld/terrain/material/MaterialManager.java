package org.jcing.jcingworld.terrain.material;

public class MaterialManager {

	String materialPath;

	public MaterialManager() {

	}

	public int getMaterialTexture(Material m) {
		return m.getTexture();
	}

}
