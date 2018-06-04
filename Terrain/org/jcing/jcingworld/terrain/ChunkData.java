package org.jcing.jcingworld.terrain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jcing.jcingworld.engine.imagery.TextureAtlas;
import org.jcing.jcingworld.terrain.generation.MapGenerator;

public class ChunkData implements Externalizable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 8736427281334570040L;
	private static final int VERSION = 0;

	public int x, z;

	public Tile[] topTiles;
	public float[] topTileTextureIndices; // TODO: keep topTileTextureIndice concurrent
	private int terrainID = 0;

	public boolean initialized;
	public boolean dismissed;

	private int calcTilesIndex(int x, int z) {
		return z * (Chunk.TILE_COUNT) + x;
	}

	public Tile getTile(int x, int z) {
		return topTiles[calcTilesIndex(x, z)];
	}

	public ChunkData(int x, int z, Terrain terrain) {
		this.x = x;
		this.z = z;
		initialized = true;
		topTiles = new Tile[Chunk.TILE_COUNT * Chunk.TILE_COUNT];
		generate(terrain.getTextureAtlas(), terrain.getGenerator());
	}

	public ChunkData() {

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		out.writeInt(x);
		out.writeInt(z);
		out.writeObject(topTiles);
		// for (int i = 0; i < topTiles.length; i++) {
		// out.writeObject(topTiles[i]);
		// }
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		switch (version) {
		case 0:
			x = in.readInt();
			z = in.readInt();
			topTiles = (Tile[]) in.readObject();
			constructTileTextureMap();
		}
		initialized = true;
	}

	// public void init(MapGenerator gen) {
	// for (Tile tile : topTiles) {
	// tile.generate(this, gen);
	// }
	// }

	public void generate(TextureAtlas textureAtlas, MapGenerator gen) {
		float SQUARE_SIZE = Chunk.TILE_SIZE / 2;
		int xc = (int) (x * Chunk.SIZE);
		int zc = (int) (z * Chunk.SIZE);
		for (int i = 0; i < Chunk.VERTEX_COUNT; i += 2) { // i == z
			for (int j = 0; j < Chunk.VERTEX_COUNT; j += 2) {// j == x
				// float x[] = { j * SQUARE_SIZE, (j + 1) * SQUARE_SIZE, j * SQUARE_SIZE,
				// (j + 1) * SQUARE_SIZE };
				// float z[] = { i * SQUARE_SIZE, i * SQUARE_SIZE, (i + 1) * SQUARE_SIZE,
				// (i + 1) * SQUARE_SIZE };
				// float y[] = { gen.height(x[0] + xc, z[0] + zc), gen.height(x[1] + xc, z[1] + zc),
				// gen.height(x[2] + xc, z[2] + zc), gen.height(x[3] + xc, z[3] + zc) };
				// setTile(j / 2, i / 2, new Tile(x, y, z, gen.tex(x[3] + xc, z[3] + zc)));
				Tile tile = new Tile(i, j, gen.tex(x * SQUARE_SIZE + i, z * SQUARE_SIZE + j));
				tile.generate(this, gen, (byte) (i), (byte) (j));
				setTile(i / 2, j / 2, tile);
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
	}

	private void setTile(int x, int z, Tile tile) {
		topTiles[calcTilesIndex(x, z)] = tile;
	}

	public void dismiss() {
		dismissed = true;
	}

	public int getTerrainID() {
		return terrainID;
	}

	public void setTerrainID(int terrainID) {
		this.terrainID = terrainID;
	}

}
