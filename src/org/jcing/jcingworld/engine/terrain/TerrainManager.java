package org.jcing.jcingworld.engine.terrain;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.Player;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.engine.imagery.TextureAtlas;
import org.jcing.jcingworld.engine.rendering.MasterRenderer;
import org.jcing.jcingworld.logging.Logs;
import org.lwjgl.util.vector.Vector2f;

public class TerrainManager {

    private Map<Integer, Map<Integer, Terrain>> terrains;

    //loading management
    public static final int RENDERDISTANCE = 5;
    //    private Vector2f[] activeChunks;
    private List<Vector2f> actives;
    private Vector2f playerPos;

    private Loader loader;
    private MasterRenderer renderer;

    BaseImage blendMap;
    TextureAtlas atlas;

    PrintStream out = Logs.chunkLoading;

    public TerrainManager(Loader loader, MasterRenderer renderer) {
        terrains = new HashMap<Integer, Map<Integer, Terrain>>();
        actives = new LinkedList<Vector2f>();
        this.loader = loader;
        this.renderer = renderer;
        blendMap = loader.loadTexture("terrain/blend/32.png", false);
        atlas = new TextureAtlas("/terrain/stone", loader);
        initActiveMap();
    }

    private void initActiveMap(){
        out.println("initializing Rendermap with a distance of: " + RENDERDISTANCE + " there will be " + Math.pow(RENDERDISTANCE*2,2) + " Chunks rendered");
        for (int i = -RENDERDISTANCE; i < RENDERDISTANCE; i++) {
            for (int j = -RENDERDISTANCE; j < RENDERDISTANCE; j++) {
                actives.add(new Vector2f(i,j));
            } 
        }
    }

    public Vector2f chunkAtWorldPos(float x, float z) {
        return new Vector2f(x / Terrain.SIZE, z / Terrain.SIZE);
    }

    public void updatePlayerPos(Player player) {
        if (chunkAtWorldPos(player.getPosition().getX(),
                player.getPosition().getZ()) != playerPos) {
            playerPos = chunkAtWorldPos(player.getPosition().getX(), player.getPosition().getZ());
        }
        for (Vector2f curr : actives) {
            if (getTerrain((int) (curr.x + playerPos.x), (int) (curr.y + playerPos.y)) == null) {
                addTerain((int) (curr.x + playerPos.x), (int) (curr.y + playerPos.y));
            }
        }
    }

    public Terrain getTerrain(int x, int z) {
        if (terrains.containsKey(x) && terrains.get(x).containsKey(z))
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
        terrain.registerNeighbour(getTerrain(x + 1, z), true);
        terrain.registerNeighbour(getTerrain(x - 1, z), true);
        terrain.registerNeighbour(getTerrain(x, z + 1), true);
        terrain.registerNeighbour(getTerrain(x, z - 1), true);
        //        actives.add(new Vector2f(x, z));
    }

    public Terrain getTerrain(Vector2f gridPos) {
        return getTerrain((int) gridPos.x, (int) gridPos.y);
    }

    public List<Vector2f> getActives() {
        return actives;
    }

    public void processActives() {
        for (Vector2f p : actives) {
            renderer.processTerrain(
                    getTerrain((int) (p.x + playerPos.x), (int) (p.y + playerPos.y)));
            //            renderer.processTerrain(getTerrain(p));
        }
    }

    public void makeRandom() {
        //        for(Vector2f p:actives){
        //            getTerrain(p.getX(), p.getY()).makeRandom();;
        //        }
    }

    public float getHeightAt(float x, float z) {
        int tix = (int) Math.floor(x / Terrain.SIZE);
        int tiz = (int) Math.floor(z / Terrain.SIZE);
        if (getTerrain(tix, tiz) != null) {
            return getTerrain(tix, tiz).getHeight(x, z);
        }
        return 0;
    }

}
