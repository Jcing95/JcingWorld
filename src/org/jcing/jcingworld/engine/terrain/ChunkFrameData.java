package org.jcing.jcingworld.engine.terrain;

import java.io.Serializable;

import org.jcing.jcingworld.engine.imagery.TextureAtlas;

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
	
	public ChunkFrameData(int x, int z, TextureAtlas atlas){
        this.x = x;
        this.z = z;
        initialized = true;
		topTiles = new Tile[Chunk.TILE_COUNT*Chunk.TILE_COUNT];
		generate(atlas);
	}
	
	
//    public static ChunkFrameData load(int x, int z){    
//    	return Chunk.saver.get(x,z);
//    }
    
    public void generate(TextureAtlas textureAtlas) {
        float SQUARE_SIZE = Chunk.TILE_SIZE / 2;
        int xc = (int) (x*Chunk.SIZE);
        int zc = (int) (z*Chunk.SIZE);
        for (int i = 0; i < Chunk.VERTEX_COUNT; i += 2) { // i == z
            for (int j = 0; j < Chunk.VERTEX_COUNT; j += 2) {// j == x
                float x[] = { j * SQUARE_SIZE, (j + 1) * SQUARE_SIZE, j * SQUARE_SIZE, (j + 1) * SQUARE_SIZE };
                float z[] = { i * SQUARE_SIZE, i * SQUARE_SIZE, (i + 1) * SQUARE_SIZE, (i + 1) * SQUARE_SIZE };
                float y[] = { Terrain.gen.height(x[0] + xc, z[0] + zc), Terrain.gen.height(x[1] + xc, z[1] + zc),
                        Terrain.gen.height(x[2] + xc, z[2] + zc), Terrain.gen.height(x[3] + xc, z[3] + zc) };
                setTile(j / 2, i / 2, new Tile(x, y, z, Terrain.gen.tex(x[3] + xc, z[3] + zc, textureAtlas.getNumTextures())));
            }
        }
        constructTileTextureMap();
    }

    public void constructTileTextureMap() {
        topTileTextureIndices = new float[Chunk.TILE_TEX_INDICE_COUNT * Chunk.TILE_TEX_INDICE_COUNT];

        // constructing
        for (int i = 1; i < Chunk.TILE_COUNT; i++) {
            for (int j = 1; j < Chunk.TILE_COUNT; j++) {
                topTileTextureIndices[(Chunk.TILE_TEX_INDICE_COUNT - 1 - j) * Chunk.TILE_TEX_INDICE_COUNT + (i)] = getTile(j - 1, i - 1).textureIndex;
            }
        }

//        out.println("PRINTING TERRAIN:");
//
//        String t = "";
//        for (int i = 0; i < topTileTextureIndices.length; i++) {
//            if (i % Chunk.TILE_TEX_INDICE_COUNT == 0) {
//                t += System.lineSeparator();
//            }
//            t += "[" + (int) topTileTextureIndices[i] + "]";
//        }
//        out.println(t);
    }
    
//    public static void prepare(int x, int z){
//    	Chunk.saver.prepare(x,z);
//    }
    
	public void apply(){
    	initialized = true;
//    	Chunk.saver.put(x, z, this);
    }

	public void setTile(int x, int z, Tile tile) {
		topTiles[calcTilesIndex(x, z)] = tile;
	}
    
}
