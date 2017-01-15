package org.jcing.jcingworld.engine.terrain;

import java.io.Serializable;

public class ChunkData implements Serializable{
    /**
     * 
     */
	
	public static DataChunk saver = new DataChunk();
	
    private static final long serialVersionUID = 8736427281334570040L;
    
	int x, z;
    Tile[][] tiles;
    float[] tileTextureIndices;

	boolean initialized = false;
    
    public static ChunkData load(int x, int z){
    	return saver.get(x, z);
    }
    
    public void apply(){
    	saver.put(x, z, this);
    }
    
}
