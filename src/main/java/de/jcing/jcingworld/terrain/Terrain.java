package de.jcing.jcingworld.terrain;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import de.jcing.jcingworld.engine.GraphicsLoader;
import de.jcing.jcingworld.engine.entities.Player;
import de.jcing.jcingworld.engine.imagery.BaseImage;
import de.jcing.jcingworld.engine.imagery.TextureAtlas;
import de.jcing.jcingworld.engine.rendering.MasterRenderer;
import de.jcing.jcingworld.terrain.generation.MapGenerator;
import de.jcing.jcingworld.toolbox.Maths;
import de.jcing.log.Log;

public class Terrain {

	private MapGenerator gen = new MapGenerator(1337);

	private HashMap<Point, Chunk> loadedChunks;

	// loading management
	public static final int RENDERDISTANCERADIUS = 15;
	
	private static final int KEEPCHUNKBUFFERLENGTH = 1;

	private List<Point> activesTemplate;
	private Point playerChunkPos;

	private GraphicsLoader loader;
	private MasterRenderer renderer;

	private Vector3f mousePos;

	private BaseImage blendMap;
	
	private TextureAtlas textureAtlas;

	private Log log = Log.getLog(Terrain.class);

	private BaseImage selectedTex;

	public Terrain(GraphicsLoader loader, MasterRenderer renderer) {
		this.loader = loader;
		this.renderer = renderer;

		// set textures
		blendMap = loader.loadTexture("terrain/blend/32N.png", false);
		selectedTex = loader.loadTexture("terrain/selectedOverlay.png", true);
		textureAtlas = new TextureAtlas("terrain/dev", loader);// loader.loadTexture("terrain/100Square.png", false),16);

		// intiialize lists
		loadedChunks = new HashMap<Point, Chunk>(RENDERDISTANCERADIUS * 2 + 1, 1);
		activesTemplate = new LinkedList<Point>();

		Chunk.initIndices();
		gen = new MapGenerator(1337);

		initActiveMap();
	}

	private void initActiveMap() {

		for (int i = -RENDERDISTANCERADIUS; i < RENDERDISTANCERADIUS; i++) {
			for (int j = -RENDERDISTANCERADIUS; j < RENDERDISTANCERADIUS; j++) {
				if (Math.abs(new Vector2f(i, j).length()) <= RENDERDISTANCERADIUS)
					activesTemplate.add(new Point(i, j));
			}
		}

		log.info("initialized Rendermap with a distance of: " + RENDERDISTANCERADIUS + " there will be " + activesTemplate.size()
				+ " Chunks rendered");
	}

	public TextureAtlas getTextureAtlas() {
		return textureAtlas;
	}

	public void initPosition(int x, int z) {
		for (Point p : activesTemplate) {
			addChunk(p.x + x, p.y + z);
		}
		// ulc.start();
	}

	private void addChunk(int x, int z) {
		addChunk(new Point(x, z));
	}

	private void addChunk(Point pos) {
		Chunk chunk = new Chunk(pos.x, pos.y, loader, renderer.getTerrainShader(), textureAtlas, blendMap, selectedTex, this);

		loadedChunks.put(pos, chunk);
		chunk.registerNeighbour(getChunk(pos.x + 1, pos.y), true);
		chunk.registerNeighbour(getChunk(pos.x - 1, pos.y), true);
		chunk.registerNeighbour(getChunk(pos.x, pos.y + 1), true);
		chunk.registerNeighbour(getChunk(pos.x, pos.y - 1), true);
	}

	public void updatePlayerPos(Player player) {
		if (chunkAtWorldPos(player.getPosition().getX(), player.getPosition().getZ()) != playerChunkPos) {
			playerChunkPos = chunkAtWorldPos(player.getPosition().getX(), player.getPosition().getZ());
			for (Point curr : activesTemplate) {
				if (getChunk(curr.x + playerChunkPos.x, curr.y + playerChunkPos.y) == null) {
					// System.err.println("adding chunk!");
					addChunk(curr.x + playerChunkPos.x, curr.y + playerChunkPos.y);
				}
			}
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

	public Chunk getChunk(int x, int z) {
		return getChunk(new Point(x, z));
	}

	public Chunk getChunk(Point pos) {
		return loadedChunks.get(pos);
	}

	public float getHeightAt(float x, float z) {
		return gen.height(x, z);
	}

	public int tex(float x, float z, int max) {
		// System.out.println("TEX: " + (gen.tex(x, z,max)));
		return (int) (gen.tex(x, z));
	}

	boolean isSupposedToUnload(Point chunk) {
		if (new Vector2f(chunk.x - playerChunkPos.x, chunk.y - playerChunkPos.y).length() > RENDERDISTANCERADIUS + KEEPCHUNKBUFFERLENGTH) {
			return true;
		}
		return false;
	}
	
	public Point getPlayerChunkPos() {
		return playerChunkPos;
	}

	public void select(Vector3f pos) {
		mousePos = pos;
	}

	public Vector3f getMousePos() {
		if (mousePos == null) {
			return new Vector3f(0, 0, 0);
		}
		return mousePos;
	}

	public MapGenerator getGenerator() {
		return gen;
	}
}
