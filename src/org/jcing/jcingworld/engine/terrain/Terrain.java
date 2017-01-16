package org.jcing.jcingworld.engine.terrain;

import java.awt.Point;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.Player;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.engine.imagery.TextureAtlas;
import org.jcing.jcingworld.engine.rendering.MasterRenderer;
import org.jcing.jcingworld.logging.Logs;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;

public class Terrain {

    private HashMap<Integer, HashMap<Integer, Chunk>> chunks;

    //loading management
    public static final int RENDERDISTANCERADIUS = 10;
    //	public static final int UNLOADRANGERADIUS = 10;
    //	public static final int PACKAGESIZE = 10;
    //	public static final int LOADEDPACKAGEDISTANCE = 5;
    //	private static final boolean FLAT = false;

    private MapGenerator gen;
    private List<Point> activesTemplate;
    private List<Point> loadedChunks;
    private Point playerPos;

    private Loader loader;
    private MasterRenderer renderer;

    BaseImage blendMap;
    TextureAtlas atlas;

    PrintStream out = Logs.chunkLoading;

    public Terrain(Loader loader, MasterRenderer renderer) {
        this.loader = loader;
        this.renderer = renderer;
        blendMap = loader.loadTexture("terrain/blend/64.png", false);
        atlas = new TextureAtlas("terrain/erde", loader);//loader.loadTexture("terrain/100Square.png", false),16);
        gen = new MapGenerator(1337);
        chunks = new HashMap<Integer, HashMap<Integer, Chunk>>(RENDERDISTANCERADIUS * 2 + 1, 1);
        activesTemplate = new LinkedList<Point>();
        loadedChunks = new LinkedList<Point>();
        initActiveMap();
    }

    private void initActiveMap() {
        out.println("initializing Rendermap with a distance of: " + RENDERDISTANCERADIUS
                + " there will be " + Math.pow(RENDERDISTANCERADIUS * 2, 2) + " Chunks rendered");
        for (int i = -RENDERDISTANCERADIUS; i < RENDERDISTANCERADIUS; i++) {
            for (int j = -RENDERDISTANCERADIUS; j < RENDERDISTANCERADIUS; j++) {
                if (Math.abs(new Vector2f(i, j).length()) <= RENDERDISTANCERADIUS)
                    activesTemplate.add(new Point(i, j));
            }
        }
    }

    public void initPosition(int x, int z) {
		for (Point p : activesTemplate) {
			addChunk(p.x+x,p.y+z);
		}
	}

	private void addChunk(int x, int z) {
	    Chunk chunk = new Chunk(x, z, loader, renderer.getTerrainShader(), atlas, blendMap, this);
	    if (chunks.get(x) == null) {
	        HashMap<Integer, Chunk> newMap = new HashMap<Integer, Chunk>(
	                RENDERDISTANCERADIUS * 2 + 1, 1);
	        newMap.put(z, chunk);
	        chunks.put(x, newMap);
	    }
	    chunks.get(x).put(z, chunk);
	    chunk.registerNeighbour(getChunk(x + 1, z), true);
	    chunk.registerNeighbour(getChunk(x - 1, z), true);
	    chunk.registerNeighbour(getChunk(x, z + 1), true);
	    chunk.registerNeighbour(getChunk(x, z - 1), true);
	    loadedChunks.add(new Point(chunk.getGridX(), chunk.getGridZ()));
	}

	public void updatePlayerPos(Player player) {
	    //		if (chunkAtWorldPos(player.getPosition().getX(), player.getPosition().getZ()) != playerPos) {
	    //			playerPos = chunkAtWorldPos(player.getPosition().getX(), player.getPosition().getZ());
	    //			for (Vector2f curr : actives) {
	    //				if (getChunk((int) (curr.x + playerPos.x), (int) (curr.y + playerPos.y)) == null) {
	    //					addChunk((int) (curr.x + playerPos.x), (int) (curr.y + playerPos.y));
	    //				}
	    //			}
	    //			checkPackageChange();
	    //		}
	}

	public void processActives() {
	    for (Point p : activesTemplate) {
	        renderer.processTerrain(getChunk(p));
	    }
	}

	public List<Point> getActives() {
	    return activesTemplate;
	}

	public Point chunkAtWorldPos(float x, float z) {
        return new Point((int) Maths.fastFloor(x / Chunk.SIZE),
                (int) Maths.fastFloor(z / Chunk.SIZE));
    }

    public Chunk getChunk(int x, int y) {
        if (chunks.get(x) != null)
            return chunks.get(x).get(y);
        return null;
    }

    public Chunk getChunk(Point gridPos) {
        return getChunk(gridPos.x, gridPos.y);
    }

    public float getHeightAt(float x, float z) {
        return gen.height(x, z);

    }

    public int tex(float x, float z, int max) {
        // TODO Auto-generated method stub
        return (int) Maths.fastFloor(gen.tex(x, z) * max);
    }

    //    public float getHeight(float x, float z) {
    //        // float z = globalZ; // switch weil faul und fehler
    //        // float x = globalX;
    //        if (!inTerrain(x, z)) {
    //            return 0;
    //        }
    //        float squareNumber = TILE_SIZE / 2;// SIZE / (float) (SQUARE_COUNT);
    //        float terrainX = (x - this.x);
    //        float terrainZ = (z - this.z);
    //        int gridX = (int) Math.floor(terrainX / squareNumber);
    //        int gridZ = (int) Math.floor(terrainZ / squareNumber);
    //        float xCoord = (terrainX % squareNumber) / squareNumber;
    //        float zCoord = (terrainZ % squareNumber) / squareNumber;
    //        float answer;
    //
    //        if (gridX + 1 >= heightMap.length || gridZ + 1 >= heightMap.length) {
    //            return 0;
    //        }
    //        if (xCoord <= (1 - zCoord)) {
    //            answer = Maths.barryCentric(new Vector3f(0, heightMap[gridX][gridZ], 0),
    //                    new Vector3f(1, heightMap[gridX + 1][gridZ], 0),
    //                    new Vector3f(0, heightMap[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
    //        } else {
    //            answer = Maths.barryCentric(new Vector3f(1, heightMap[gridX + 1][gridZ], 0),
    //                    new Vector3f(1, heightMap[gridX + 1][gridZ + 1], 1),
    //                    new Vector3f(0, heightMap[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
    //        }
    //        return answer;
    //    }

    

    //	private void checkPackageChange() {
    //		if(playerPackagePos == null){
    //			playerPackagePos = getPackage(playerPos);
    //		}else{
    //			if(playerPackagePos != getPackage(playerPos)){
    //				LinkedList<Vector2f> toRemove = new LinkedList<Vector2f>();
    //				for (Vector2f loaded : loadedChunks) {
    //					if(isSupposedToUnload(getPackage(loaded))){
    //						toRemove.add(loaded);
    //						getChunk(loaded).dismiss();
    //					}
    //				}
    //				for (Vector2f rem : toRemove) {
    //					chunks.get(rem.x).remove(rem.y);
    //					if(chunks.get(rem.x).size() < 1){
    //						chunks.remove(rem.x);
    //					}
    //				}
    //			}
    //		}
    //	}

    //	private Vector2f getPackage(Vector2f chunk){
    //		return new Vector2f((int)chunk.x/PACKAGESIZE,(int)chunk.y/PACKAGESIZE);
    //	}
    //	
    //	private boolean isSupposedToUnload(Vector2f pack){
    //		if(pack.translate(-playerPackagePos.x, -playerPackagePos.y).length() > LOADEDPACKAGEDISTANCE){
    //			return true;
    //		}
    //		return false;
    //	}
}
