package org.jcing.jcingworld.engine.terrain;

import java.io.Serializable;

public class ChunkContainer implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static int CONTAINERSIZE = 8;
    
    private ChunkData[][] chunks;
    
    private int xF, zF;
    
    public ChunkContainer(int xF, int zF,Terrain terrain){
        this.xF = xF;
        this.zF = zF;
        chunks = new ChunkData[CONTAINERSIZE][CONTAINERSIZE];
        for (int i = 0; i < chunks.length; i++) {
            for (int j = 0; j < chunks.length; j++) {
                chunks[i][j] = new ChunkData((xF*(CONTAINERSIZE-1))+i,(zF*(CONTAINERSIZE-1))+j,terrain);
            }
        }
    }
    
    public ChunkData get(int x, int z){
        return chunks [x-(xF*(CONTAINERSIZE-1))][z-(zF*(CONTAINERSIZE-1))];
    }
    
    public void set(int x, int z, ChunkData chunk){
        chunks[x-(xF*(CONTAINERSIZE-1))][z-(zF*(CONTAINERSIZE-1))] = chunk;
    }
}
