package org.jcing.jcingworld.engine.terrain;

import java.io.Serializable;

public class ChunkData implements Serializable{
    /**
     * 
     */
	
	public static final DataChunk saver = new DataChunk();
	
    private static final long serialVersionUID = 8736427281334570040L;
    
	public int x, z;
    public Tile[][] tiles;
    public float[] tileTextureIndices;
    
	public boolean initialized;
    
	
    public static ChunkData load(int x, int z){
    	ChunkData dta = saver.get(x,z);
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
    	saver.put(x, z, this);
    }
    
}
