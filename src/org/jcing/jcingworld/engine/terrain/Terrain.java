package org.jcing.jcingworld.engine.terrain;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.jcing.filesystem.ModuloTree;
import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.Player;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.engine.imagery.TextureAtlas;
import org.jcing.jcingworld.engine.rendering.MasterRenderer;
import org.jcing.jcingworld.logging.Logs;
import org.lwjgl.util.vector.Vector2f;

public class Terrain {

	private ModuloTree<ModuloTree<Chunk>> chunks;

	//loading management
	public static final int RENDERDISTANCERADIUS = 20;
	public static final int UNLOADRANGERADIUS = 2;
	private static final boolean FLAT = false;

	private MapGenerator gen;
	private List<Vector2f> actives;
	private List<Vector2f> unloadBuffer;
	private Vector2f playerPos;

	private Loader loader;
	private MasterRenderer renderer;

	BaseImage blendMap;
	TextureAtlas atlas;

	PrintStream out = Logs.chunkLoading;

	public Terrain(Loader loader, MasterRenderer renderer) {
		chunks = new ModuloTree<ModuloTree<Chunk>>();
		actives = new LinkedList<Vector2f>();
		this.loader = loader;
		this.renderer = renderer;
		blendMap = loader.loadTexture("terrain/blend/64.png", false);
		atlas = new TextureAtlas("terrain/erde", loader);//loader.loadTexture("terrain/100Square.png", false),16);
		gen = new MapGenerator(1337);
		initActiveMap();
	}

	private void initActiveMap() {
		out.println("initializing Rendermap with a distance of: " + RENDERDISTANCERADIUS + " there will be "
				+ Math.pow(RENDERDISTANCERADIUS * 2, 2) + " Chunks rendered");
		for (int i = -RENDERDISTANCERADIUS - UNLOADRANGERADIUS; i < RENDERDISTANCERADIUS + UNLOADRANGERADIUS; i++) {
			for (int j = -RENDERDISTANCERADIUS - UNLOADRANGERADIUS; j < RENDERDISTANCERADIUS + UNLOADRANGERADIUS; j++) {
				if (new Vector2f(i, j).length() <= RENDERDISTANCERADIUS)
					actives.add(new Vector2f(i, j));
				else
					unloadBuffer.add(new Vector2f(i, j));
			}
		}
	}

	public Vector2f chunkAtWorldPos(float x, float z) {
		return new Vector2f((int) Math.floor(x / Chunk.SIZE), (int) Math.floor(z / Chunk.SIZE));
	}

	public void updatePlayerPos(Player player) {
		if (chunkAtWorldPos(player.getPosition().getX(), player.getPosition().getZ()) != playerPos) {
			playerPos = chunkAtWorldPos(player.getPosition().getX(), player.getPosition().getZ());
			for (Vector2f curr : actives) {
				if (getChunk((int) (curr.x + playerPos.x), (int) (curr.y + playerPos.y)) == null) {
					addChunk((int) (curr.x + playerPos.x), (int) (curr.y + playerPos.y));
				}
			}

		}
	}

	public Chunk getChunk(int x, int y) {

		if (chunks.get(x) != null)
			return chunks.get(x).get(y);
		return null;
	}

	public void addChunk(int x, int y) {
		Chunk chunk = new Chunk(x, y, loader, renderer.getTerrainShader(), atlas, blendMap, this);
		if (chunks.get(x) == null) {
			ModuloTree<Chunk> newMap = new ModuloTree<Chunk>();
			newMap.set(chunk, y);
			if (chunks.get(x) == null) {
				chunks.set(newMap, x);
			}
		}
		chunks.get(x).set(chunk, y);
		chunk.registerNeighbour(getChunk(x + 1, y), true);
		chunk.registerNeighbour(getChunk(x - 1, y), true);
		chunk.registerNeighbour(getChunk(x, y + 1), true);
		chunk.registerNeighbour(getChunk(x, y - 1), true);
		//        actives.add(new Vector2f(x, z));
	}

	public Chunk getChunk(Vector2f gridPos) {
		return getChunk((int) gridPos.x, (int) gridPos.y);
	}

	public List<Vector2f> getActives() {
		return actives;
	}

	public void processActives() {
		for (Vector2f p : actives) {
			renderer.processTerrain(getChunk((int) (p.x + playerPos.x), (int) (p.y + playerPos.y)));
			//            renderer.processTerrain(getTerrain(p));
		}
	}

	public void makeRandom() {
		//        if(playerPos != null)
		//        for (Vector2f p : actives) {
		////            if (getChunk((int) (p.x + playerPos.x), (int) (p.y + playerPos.y)) == null) {
		////                return;
		////            } else {
		//                getChunk((int) (p.x + playerPos.x), (int) (p.y + playerPos.y)).makeRandom();
		//            
		//            //            renderer.processTerrain(getTerrain(p));
		//        }
	}

	public float getHeightAt(float x, float z) {
		return gen.height(x, z);

	}

	public int tex(float x, float z, int max) {
		// TODO Auto-generated method stub
		return (int) Math.floor(gen.tex(x, z) * max);
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

}
