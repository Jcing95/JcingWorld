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
import org.lwjgl.util.vector.Vector3f;

public class Terrain {

    private HashMap<Integer, HashMap<Integer, Chunk>> chunks;

    //loading management
    public static final int RENDERDISTANCERADIUS = 10;

    private static final int KEEPCHUNKBUFFERLENGTH = 5;

    public static MapGenerator gen = new MapGenerator(1337);
    //	public static final int UNLOADRANGERADIUS = 10;
    //	public static final int PACKAGESIZE = 10;
    //	public static final int LOADEDPACKAGEDISTANCE = 5;
    //	private static final boolean FLAT = false;

    //	private MapGenerator gen;
    private List<Point> activesTemplate;
    private List<Point> loadedChunks;
    private Point playerChunkPos;

    private Loader loader;
    private MasterRenderer renderer;

    private DataChunk saver;

    BaseImage blendMap;
    TextureAtlas textureAtlas;

    PrintStream out = Logs.chunkLoading;

    LoadingCrawler lc;

    private LinkedList<Point> unloaded;

	private BaseImage selectedTex;

    public Terrain(Loader loader, MasterRenderer renderer) {
        this.loader = loader;
        this.renderer = renderer;
        blendMap = loader.loadTexture("terrain/blend/32N.png", false);
        selectedTex = loader.loadTexture("terrain/selectedOverlay.png", true);
        textureAtlas = new TextureAtlas("terrain/naturalFloor", loader);//loader.loadTexture("terrain/100Square.png", false),16);
        
        gen = new MapGenerator(1337);
        chunks = new HashMap<Integer, HashMap<Integer, Chunk>>(RENDERDISTANCERADIUS * 2 + 1, 1);
        activesTemplate = new LinkedList<Point>();
        loadedChunks = new LinkedList<Point>();
        unloaded = new LinkedList<Point>();
        saver= new DataChunk(this);
        Chunk.initIndices();
        initActiveMap();
        lc = new LoadingCrawler(this, loadedChunks);
        lc.start();
    }

    private void initActiveMap() {
        for (int i = -RENDERDISTANCERADIUS; i < RENDERDISTANCERADIUS; i++) {
            for (int j = -RENDERDISTANCERADIUS; j < RENDERDISTANCERADIUS; j++) {
                if (Math.abs(new Vector2f(i, j).length()) <= RENDERDISTANCERADIUS)
                    activesTemplate.add(new Point(i, j));
            }
        }
//        LinkedList<Point> loadedTemplate = new LinkedList<Point>();
//        for (int i = -RENDERDISTANCERADIUS - KEEPCHUNKBUFFERLENGTH; i < RENDERDISTANCERADIUS
//                + KEEPCHUNKBUFFERLENGTH; i++) {
//            for (int j = -RENDERDISTANCERADIUS - KEEPCHUNKBUFFERLENGTH; j < RENDERDISTANCERADIUS
//                    + KEEPCHUNKBUFFERLENGTH; j++) {
//                if (Math.abs(new Vector2f(i, j).length()) <= RENDERDISTANCERADIUS
//                        + KEEPCHUNKBUFFERLENGTH)
//                    loadedTemplate.add(new Point(i, j));
//            }
//        }
        
        //		lc.run();
        out.println("initialized Rendermap with a distance of: " + RENDERDISTANCERADIUS
                + " there will be " + activesTemplate.size() + " Chunks rendered");
    }

    public TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    public void initPosition(int x, int z) {
        for (Point p : activesTemplate) {
            addChunk(p.x + x, p.y + z);
        }
        //		ulc.start();
    }

    private void addChunk(int x, int z) {
        Chunk chunk = new Chunk(x, z, loader, renderer.getTerrainShader(), textureAtlas, blendMap, selectedTex,
                this);
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
        if (chunkAtWorldPos(player.getPosition().getX(),
                player.getPosition().getZ()) != playerChunkPos) {
            playerChunkPos = chunkAtWorldPos(player.getPosition().getX(), player.getPosition().getZ());
            for (Point curr : activesTemplate) {
                if (getChunk(curr.x + playerChunkPos.x, curr.y + playerChunkPos.y) == null) {
                    //                    System.err.println("adding chunk!");
                    addChunk(curr.x + playerChunkPos.x, curr.y + playerChunkPos.y);
                }
            }
            lc.check();
        }
    }

    public void processActives() {
        for (Point p : activesTemplate) {
            renderer.processTerrain(getChunk(p.x + playerChunkPos.x, p.y + playerChunkPos.y));
        }
    }

    public List<Point> getActives() {
        return activesTemplate;
    }

    public Point chunkAtWorldPos(float x, float z) {
        return new Point(Maths.fastFloor(x / Chunk.SIZE), Maths.fastFloor(z / Chunk.SIZE));
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
        //		System.out.println("TEX: " + (gen.tex(x, z,max)));
        return (int) (gen.tex(x, z, max));
    }

    boolean isSupposedToUnload(Point chunk) {
        if (new Vector2f(chunk.x - playerChunkPos.x, chunk.y - playerChunkPos.y)
                .length() > RENDERDISTANCERADIUS + KEEPCHUNKBUFFERLENGTH) {
            return true;
        }
        return false;
    }

    //	public boolean checkLoad(Point ch) {
    //	    Point chunk = new Point(ch.x+playerPos.x,ch.y+playerPos.y);
    //		if (!loadedChunks.contains(chunk) && new Vector2f(chunk.x - playerPos.x, chunk.y - playerPos.y).length() < RENDERDISTANCERADIUS
    //				+ KEEPCHUNKBUFFERLENGTH) {
    //			ChunkFrameData.prepare(chunk.x, chunk.y);
    //			return true;
    //		}
    //		return false;
    //	}

    public void unload(Point chunk) {
        chunks.get(chunk.x).get(chunk.y).dismiss();
        chunks.get(chunk.x).remove(chunk.y);
        if (chunks.get(chunk.x).size() == 0) {
            chunks.remove(chunk.x);
        }
        unloaded.add(chunk);
    }

    public void finishUnloading() {
        for (Point point : unloaded) {
            loadedChunks.remove(point);
        }
        unloaded.clear();
    }

    public void finish() {
        lc.setRunning(false);

        try {
            lc.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        saver.finish();
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

    public DataChunk getSaver() {
        return saver;
    }

	public Point getPlayerChunkPos() {
		return playerChunkPos;
	}

	private Vector3f mousePos;
	public void select(Vector3f pos) {
		mousePos = pos;
	}

	public Vector3f getMousePos() {
		if(mousePos == null){
			return new Vector3f(0,0,0);
		}
		return mousePos;
	}
}
