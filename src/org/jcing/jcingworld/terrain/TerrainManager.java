package org.jcing.jcingworld.terrain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.Point;

public class TerrainManager {

	private Map<Integer, Map<Integer, Terrain>> terrains;
	private List<Point> actives;

	public TerrainManager() {
		terrains = new HashMap<Integer, Map<Integer, Terrain>>();
	}

	public Terrain getTerrain(int x, int y) {
		return terrains.get(x).get(y);
	}

	public void addTerain(int x, int y, Terrain terrain) {
		if (terrains.containsKey(x)) {
			terrains.get(x).put(y, terrain);
		} else {
			Map<Integer, Terrain> newMap = new HashMap<Integer, Terrain>();
			newMap.put(y, terrain);
			terrains.put(x, newMap);
		}
	}

	public List<Point> getActives() {
		return actives;
	}

}
