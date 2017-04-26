package org.jcing.jcingworld.terrain;

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
import org.jcing.jcingworld.terrain.generation.MapGenerator;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Terrain {

    
    private MapGenerator gen = new MapGenerator(1337);
    
    
    private HashMap<Point, Chunk> chunks;

    //loading management
    public static final int RENDERDISTANCERADIUS = 15;
    private static final int KEEPCHUNKBUFFERLENGTH = 1;

    

    private List<Point> activesTemplate;
    private Point playerChunkPos;

    private Loader loader;
    private MasterRenderer renderer;

    private DataChunk saver;
    
    private Vector3f mousePos;

    private BaseImage blendMap;
    private TextureAtlas textureAtlas;

    private PrintStream out = Logs.chunkLoading;

    private LoadingCrawler lc;

	private BaseImage selectedTex;

    public Terrain(Loader loader, MasterRenderer renderer) {
        this.loader = loader;
        this.renderer = renderer;
        
        //set textures
        blendMap = loader.loadTexture("terrain/blend/32N.png", false);
        selectedTex = loader.loadTexture("terrain/selectedOverlay.png", true);
        textureAtlas = new TextureAtlas("terrain/dev", loader);//loader.loadTexture("terrain/100Square.png", false),16);
        
        //intiialize lists
        chunks = new HashMap<Point, Chunk>(RENDERDISTANCERADIUS * 2 + 1, 1);
        activesTemplate = new LinkedList<Point>();
       
        
        Chunk.initIndices();
        gen = new MapGenerator(1337);
        saver= new DataChunk(this);
        
        initActiveMap();
        lc = new LoadingCrawler(this);
        lc.start();
    }

    private void initActiveMap() {
        
        for (int i = -RENDERDISTANCERADIUS; i < RENDERDISTANCERADIUS; i++) {
            for (int j = -RENDERDISTANCERADIUS; j < RENDERDISTANCERADIUS; j++) {
                if (Math.abs(new Vector2f(i, j).length()) <= RENDERDISTANCERADIUS)
                    activesTemplate.add(new Point(i, j));
            }
        }

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

    private void addChunk(int x, int z){
    	addChunk(new Point(x,z));
    }
    
    private void addChunk(Point pos) {
        Chunk chunk = new Chunk(pos.x,pos.y, loader, renderer.getTerrainShader(), textureAtlas, blendMap, selectedTex,
                this);

        chunks.put(pos, chunk);
        chunk.registerNeighbour(getChunk(pos.x + 1, pos.y), true);
        chunk.registerNeighbour(getChunk(pos.x - 1, pos.y), true);
        chunk.registerNeighbour(getChunk(pos.x, pos.y + 1), true);
        chunk.registerNeighbour(getChunk(pos.x, pos.y - 1), true);
        lc.markLoaded(new Point(chunk.getGridX(), chunk.getGridZ()));
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

    public Chunk getChunk(int x, int z) {
        return getChunk(new Point(x,z));
    }

    public Chunk getChunk(Point pos) {
        return chunks.get(pos);
    }

    public float getHeightAt(float x, float z) {
        return gen.height(x, z);
    }

    public int tex(float x, float z, int max) {
        //		System.out.println("TEX: " + (gen.tex(x, z,max)));
        return (int) (gen.tex(x, z));
    }

    boolean isSupposedToUnload(Point chunk) {
        if (new Vector2f(chunk.x - playerChunkPos.x, chunk.y - playerChunkPos.y)
                .length() > RENDERDISTANCERADIUS + KEEPCHUNKBUFFERLENGTH) {
            return true;
        }
        return false;
    }

    public void unload(Point pos) {
        chunks.get(pos).dismiss();
        chunks.remove(pos);
    }

    public void finish() {
        lc.setRunning(false);
        try {
            lc.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        saver.finish();
    }

    public DataChunk getSaver() {
        return saver;
    }

	public Point getPlayerChunkPos() {
		return playerChunkPos;
	}

	public void select(Vector3f pos) {
		mousePos = pos;
	}

	public Vector3f getMousePos() {
		if(mousePos == null){
			return new Vector3f(0,0,0);
		}
		return mousePos;
	}

    public MapGenerator getGenerator() {
        return gen;
    }
}
