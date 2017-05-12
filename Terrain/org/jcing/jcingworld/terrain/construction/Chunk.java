package org.jcing.jcingworld.terrain.construction;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.engine.shading.terrain.TerrainShader;
import org.jcing.jcingworld.logging.Logs;
import org.jcing.jcingworld.terrain.Terrain;
import org.jcing.jcingworld.terrain.generation.MapGenerator;

public class Chunk implements Externalizable{

	private byte VERSION = 0;
	
	public static final float TILE_SIZE = 6;

	public static final int TILE_COUNT = 16;
	public static final int TILE_TEX_INDICE_COUNT = TILE_COUNT + 1;
	public static final float SIZE = TILE_SIZE * (TILE_COUNT - 1);
	
	public static final int VERTEX_COUNT = (TILE_COUNT) * 2;
		
	private TileBase[][] tiles;
	private RawModel model;
	private BaseImage blendMap;
	private Terrain terrain;
	
	private float x,z;
	
	private static int[] indices;
	
	private static PrintStream out = Logs.subLog(Logs.chunkLoading, "newChunk", true);
	
	public Chunk(){
		if(indices == null){
			initIndices();
		}
	}
	
	
	public void initialize(Loader loader, Terrain terrain, float GRIDx, float GRIDy){
		tiles = new TileBase[TILE_COUNT][TILE_COUNT];
		this.terrain = terrain;
		this.x = GRIDx * SIZE;
		this.z = GRIDy * SIZE;
		if(tiles == null){
			generateTiles();
		}
		model = generateTerrain(terrain.getLoader(), terrain.getShader());
	}
	
	private void generateTiles(){
		
	}
	
	private RawModel generateTerrain(Loader loader, TerrainShader shader) {
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int vertexPointer = 0;
		// VERTICES NORMALS TEXTURECOORDS
		for (int j = 0; j < TILE_COUNT; j++) { // i == z
			for (int i = 0; i < TILE_COUNT; i++) { // j == x;
				// SQUARE BOTTOMLEFT
				vertices[vertexPointer * 3] = chunkdata.getTile(i, j).getX()[0];
				vertices[vertexPointer * 3 + 1] = chunkdata.getTile(i, j).getY()[0];
				vertices[vertexPointer * 3 + 2] = chunkdata.getTile(i, j).getZ()[0];
				normals[vertexPointer * 3] = chunkdata.getTile(i, j).getNormal()[0].getX();
				normals[vertexPointer * 3 + 1] = chunkdata.getTile(i, j).getNormal()[0].getY();
				normals[vertexPointer * 3 + 2] = chunkdata.getTile(i, j).getNormal()[0].getZ();
				textureCoords[vertexPointer * 2] = (float) j / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) i / ((float) TILE_COUNT);
				vertexPointer++;

				// SQUARE BOTTOMRIGHT
				vertices[vertexPointer * 3] = chunkdata.getTile(i, j).getX()[1];
				vertices[vertexPointer * 3 + 1] = chunkdata.getTile(i, j).getY()[1];
				vertices[vertexPointer * 3 + 2] = chunkdata.getTile(i, j).getZ()[1];
				normals[vertexPointer * 3] = chunkdata.getTile(i, j).getNormal()[1].getX();
				normals[vertexPointer * 3 + 1] = chunkdata.getTile(i, j).getNormal()[1].getY();
				normals[vertexPointer * 3 + 2] = chunkdata.getTile(i, j).getNormal()[1].getZ();
				textureCoords[vertexPointer * 2] = (float) (j + 0f) / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0.5f) / ((float) TILE_COUNT);
				vertexPointer++;

				// SQUARE TOPLEFT
				vertices[vertexPointer * 3] = chunkdata.getTile(i, j).getX()[2];
				vertices[vertexPointer * 3 + 1] = chunkdata.getTile(i, j).getY()[2];
				vertices[vertexPointer * 3 + 2] = chunkdata.getTile(i, j).getZ()[2];
				normals[vertexPointer * 3] = chunkdata.getTile(i, j).getNormal()[2].getX();
				normals[vertexPointer * 3 + 1] = chunkdata.getTile(i, j).getNormal()[2].getY();
				normals[vertexPointer * 3 + 2] = chunkdata.getTile(i, j).getNormal()[2].getZ();
				textureCoords[vertexPointer * 2] = (float) (j + 0.5f) / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0f) / ((float) TILE_COUNT);
				vertexPointer++;

				// SQUARE TOPRIGHT
				vertices[vertexPointer * 3] = chunkdata.getTile(i, j).getX()[3];
				vertices[vertexPointer * 3 + 1] = chunkdata.getTile(i, j).getY()[3];
				vertices[vertexPointer * 3 + 2] = chunkdata.getTile(i, j).getZ()[3];
				normals[vertexPointer * 3] = chunkdata.getTile(i, j).getNormal()[3].getX();
				normals[vertexPointer * 3 + 1] = chunkdata.getTile(i, j).getNormal()[3].getY();
				normals[vertexPointer * 3 + 2] = chunkdata.getTile(i, j).getNormal()[3].getZ();
				textureCoords[vertexPointer * 2] = (float) (j + 0.5f) / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0.5f) / ((float) TILE_COUNT);
				vertexPointer++;
			}
		}

		return loader.loadToVAO(vertices, textureCoords, normals, indices, chunkdata.topTileTextureIndices);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		VERSION = in.readByte();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	public static void initIndices() {
		// INDICES
		indices = new int[4 * 6 * (TILE_COUNT - 1) * (TILE_COUNT - 1)];

		int topStep = 2 * VERTEX_COUNT;
		int pointer = 0;
		for (int gz = 0; gz < TILE_COUNT - 1; gz++) {
			int square = 0;
			for (int gx = 0; gx < TILE_COUNT - 1; gx++) {
				square = ((gz) * VERTEX_COUNT * 2) + gx * 4;

				/**/// 2t##3t##6t##7t
				/**/// #SSS#SSS#SSS#
				/**/// #SSS#SSS#SSS#
				/**/// 0t##1t##4t##5t
				/**/// #SSS#SSS#SSS#
				/**/// #SSS#SSS#SSS#
				/**/// 2###3###6###7
				/**/// #SSS#SSS#SSS#
				/**/// #SSS#SSS#SSS#
				/**/// 0###1###4###5

				// SQUARE
				/**/// TRI 1
				/**/indices[pointer++] = square + 2;
				/**/indices[pointer++] = square + 1;
				/**/indices[pointer++] = square + 0;
				/**/// TRI 2
				/**/indices[pointer++] = square + 1;
				/**/indices[pointer++] = square + 2;
				/**/indices[pointer++] = square + 3;

				// PSEUDO RIGHT
				/**/// TRI 1
				/**/indices[pointer++] = square + 3;
				/**/indices[pointer++] = square + 4;
				/**/indices[pointer++] = square + 1;
				/**/// TRI 2
				/**/indices[pointer++] = square + 4;
				/**/indices[pointer++] = square + 3;
				/**/indices[pointer++] = square + 6;

				// PSEUDO PSEUDO
				/**/// TRI 1
				/**/indices[pointer++] = square + 1 + topStep;
				/**/indices[pointer++] = square + 6;
				/**/indices[pointer++] = square + 3;
				/**/// TRI 2
				/**/indices[pointer++] = square + 6;
				/**/indices[pointer++] = square + 1 + topStep;
				/**/indices[pointer++] = square + 4 + topStep;

				// PSEUDO TOP
				/**/// TRI 1
				/**/indices[pointer++] = square + topStep;
				/**/indices[pointer++] = square + 3;
				/**/indices[pointer++] = square + 2;
				/**/// TRI 2
				/**/indices[pointer++] = square + 3;
				/**/indices[pointer++] = square + topStep;
				/**/indices[pointer++] = square + 1 + topStep;

			}
		}
	}
}
