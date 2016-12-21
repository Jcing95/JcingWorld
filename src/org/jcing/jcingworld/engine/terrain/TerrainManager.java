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
		blendMap = loader.loadTexture("terrain/blendmapGood.png", false);
        atlas = new TextureAtlas(loader.loadTexture("terrain/100Square.png",false),16);//"terrain/dirt",loader);
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

}
