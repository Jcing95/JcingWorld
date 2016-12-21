package org.jcing.jcingworld.engine.terrain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.engine.imagery.TextureAtlas;
import org.jcing.jcingworld.engine.rendering.MasterRenderer;
import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector2f;

public class TerrainManager {

	private Map<Integer, Map<Integer, Terrain>> terrains;
	private List<Point> actives;

	private Loader loader;
	private MasterRenderer renderer;
	
	BaseImage blendMap;
	TextureAtlas atlas;
	
	public TerrainManager(Loader loader, MasterRenderer renderer) {
		terrains = new HashMap<Integer, Map<Integer, Terrain>>();
		actives = new LinkedList<Point>();
		this.loader =loader;
		this.renderer = renderer;
		blendMap = loader.loadTexture("terrain/blend/256.png", false);
        atlas = new TextureAtlas("terrain/grass",loader);
	}
	
	public Terrain getTerrain(int x, int z) {
	    if(terrains.containsKey(x) && terrains.get(x).containsKey(z))
		return terrains.get(x).get(z);
	    return null;
	}

	public void addTerain(int x, int z) {
	    Terrain terrain = new Terrain(x, z, loader, renderer.getTerrainShader(), atlas, blendMap);
		if (terrains.containsKey(x)) {
			terrains.get(x).put(z, terrain);
		} else {
			Map<Integer, Terrain> newMap = new HashMap<Integer, Terrain>();
			newMap.put(z, terrain);
			terrains.put(x, newMap);
		}
		actives.add(new Point(x,z));
	}
	
	public Terrain getTerrain(Vector2f gridPos){
	    return getTerrain((int)gridPos.x, (int)gridPos.y);
	}

	public List<Point> getActives() {
		return actives;
	}

    public void processActives() {
        for(Point p:actives){
            renderer.processTerrain(getTerrain(p.getX(), p.getY()));
        }
    }

    public void makeRandom() {
//        for(Point p:actives){
//            getTerrain(p.getX(), p.getY()).makeRandom();;
//        }
    }

    public float getHeightAt(float x, float z) {
        int tix = (int)Math.floor(x/Terrain.SIZE);
        int tiz = (int)Math.floor(z/Terrain.SIZE);
        float offx = x%Terrain.SIZE;
        float offz = z%Terrain.SIZE;
        if(getTerrain(tix, tiz) != null){
            return getTerrain(tix, tiz).getHeight(x, z);
        }
        return 0;
    }

}
