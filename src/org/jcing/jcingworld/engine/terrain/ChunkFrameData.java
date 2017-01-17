package org.jcing.jcingworld.engine.terrain;

import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ChunkFrameData implements Serializable{
    /**
     * 
     */
	
    private static final long serialVersionUID = 8736427281334570040L;    
    
	public int x, z;
    public Tile[] topTiles;
    public float[] topTileTextureIndices; //TODO: keep topTileTextureIndice concurrent
    
	public boolean initialized;
    
	
	private int calcTilesIndex(int x, int z){
		return z*(Chunk.TILE_COUNT)+x;
	}
	
	public Tile getTile(int x, int z){
		return topTiles[calcTilesIndex(x, z)];
	}
	
	public ChunkFrameData(){
		topTiles = new Tile[Chunk.TILE_COUNT*Chunk.TILE_COUNT];
	}
	
    public static ChunkFrameData load(int x, int z){    
    	return Chunk.saver.get(x,z);
    }
    
    public static void prepare(int x, int z){
    	Chunk.saver.prepare(x,z);
    }
    
	public void apply(){
    	initialized = true;
//    	Chunk.saver.put(x, z, this);
    }

	public void setTile(int x, int z, Tile tile) {
		topTiles[calcTilesIndex(x, z)] = tile;
	}

	public void dismiss() {
		Chunk.saver.dismiss(x,z);
	}
    
}
