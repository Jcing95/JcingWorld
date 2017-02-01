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
        chunks = new ChunkData[CONTAINERSIZE+1][CONTAINERSIZE+1];
        for (int i = 0; i < chunks.length; i++) {
            for (int j = 0; j < chunks.length; j++) {
                chunks[i][j] = new ChunkData((xF*(CONTAINERSIZE))+i,(zF*(CONTAINERSIZE))+j,terrain);
            }
        }
    }
    
    public ChunkData get(int x, int z){
        return chunks [x-(xF*(CONTAINERSIZE))][z-(zF*(CONTAINERSIZE))];
    }
    
    public void set(int x, int z, ChunkData chunk){
        chunks[x-(xF*(CONTAINERSIZE-1))][z-(zF*(CONTAINERSIZE-1))] = chunk;
    }

	public void dismiss() {
		chunks = null;
//		for (int i = 0; i < chunks.length; i++) {
//			for (int j = 0; j < chunks.length; j++) {
//				if(!chunks[i][j].dismissed){
//					return false;
//				}
//			}
//		}
//		return true;
	}
}
