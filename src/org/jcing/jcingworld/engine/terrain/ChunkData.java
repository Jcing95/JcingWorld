package org.jcing.jcingworld.engine.terrain;

import java.io.Serializable;

public class ChunkData implements Serializable{
    /**
     * 
     */
	
    private static final long serialVersionUID = 8736427281334570040L;    
    
	public int x, z;
    public Tile[] tiles;
    public float[] tileTextureIndices;
    
	public boolean initialized;
    
	
	private int calcTilesIndex(int x, int z){
		return z*(Chunk.TILE_COUNT)+x;
	}
	
	public Tile getTile(int x, int z){
		return tiles[calcTilesIndex(x, z)];
	}
	
	public ChunkData(){
		tiles = new Tile[Chunk.TILE_COUNT*Chunk.TILE_COUNT];
	}
	
    public static ChunkData load(int x, int z){
    	ChunkData dta = Chunk.saver.get(x,z);
    	if(dta == null){
    		dta = new ChunkData();
    		dta.x = x;
    		dta.z = z;
    		dta.initialized = false;
    	}
    	return dta;
    }
    
	public void apply(){
    	initialized = true;
    	Chunk.saver.put(x, z, this);
    }

	public void setTile(int x, int z, Tile tile) {
		tiles[calcTilesIndex(x, z)] = tile;
	}
    
}
