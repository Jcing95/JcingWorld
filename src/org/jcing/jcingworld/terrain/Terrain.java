package org.jcing.jcingworld.terrain;

import java.util.Random;

import org.jcing.jcingworld.main.Loader;
import org.jcing.jcingworld.models.RawModel;
import org.jcing.jcingworld.textures.TerrainTexture;
import org.jcing.jcingworld.textures.TerrainTexturePack;
import org.jcing.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Terrain {

	private static final float SQUARE_SIZE = 16;
	private static final int VERTEX_COUNT = 100;
	public static final float SIZE = SQUARE_SIZE * VERTEX_COUNT;

	private static float maxDelta = 32;
	private float[][] heightMap;

	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;

	public Terrain(float gridX, float gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * VERTEX_COUNT * SQUARE_SIZE;
		this.z = gridZ * VERTEX_COUNT * SQUARE_SIZE;
		// loadheightMap();
		this.model = generateTerrain(loader);
	}

	private RawModel generateTerrain(Loader loader) {
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];

		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
		int vertexPointer = 0;

		loadheightMap();
		// VERTICES NORMALS TEXTURECOORDS
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {

				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer * 3 + 1] = heightMap[j][i];
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;

				// TODO: vertex management
				Vector3f normal = calculateNormal(j, i);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;

				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}

		// INDICES
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	private Vector3f calculateNormal(int x, int z) {
		float heightL=0,heightR=0,heightD=0,heightU=0;
		if(x-1 >= 0)
		heightL = heightMap[x-1][z];
		if(x+1 < heightMap.length)
		heightR = heightMap[x + 1][z];
		if(z-1 >= 0)
		heightD = heightMap[x][ z - 1];
		if(z+1 < heightMap.length)
		heightU = heightMap[x][ z + 1];
		
		Vector3f normal = new Vector3f(heightL - heightR,SQUARE_SIZE, heightD - heightU);
		normal.normalise();
		return normal;
	}
	
	private void loadheightMap() {
		Random random = new Random();
		heightMap = new float[VERTEX_COUNT][VERTEX_COUNT];
		for (int i = 0; i < heightMap.length; i++) {
			for (int j = 0; j < heightMap[i].length; j++) {
				if (i == 0 && j == 0) {
					heightMap[j][i] = 1 + random.nextFloat();
				} else {
					float delta = random.nextFloat() * maxDelta;
					if (j == 0) {
						heightMap[j][i] = heightMap[j][i - 1] - maxDelta / 2 + delta;
					} else if (i == 0) {
						heightMap[j][i] = heightMap[j - 1][i] - maxDelta / 2 + delta;
					} else {
						heightMap[j][i] = (heightMap[j][i - 1] + heightMap[j - 1][i]) / 2.0f - maxDelta / 2 + delta;
					}
				}
			}
		}
	}

	public boolean inTerrain(float globalX, float globalZ) {
		// float x = getRelativeCoordinate(globalX);
		// float y = getRelativeCoordinate(globalZ);
		float x = globalX - this.x;
		float z = globalZ - this.z;
		if (x < 0 || z < 0 || z >= SIZE || x >= SIZE) {
			return false;
		}
		return true;
	}

	public float getHeight(float globalX, float globalZ) {
		if (!inTerrain(globalX, globalZ)) {
			return 0;
		}
		float squareNumber = SIZE / (float) (VERTEX_COUNT - 1);
		float terrainX = (globalX - this.x);
		float terrainZ = (globalZ - this.z);
		int gridX = (int) Math.floor(terrainX / squareNumber);
		int gridZ = (int) Math.floor(terrainZ / squareNumber);
		float xCoord = (terrainX % squareNumber) / squareNumber;
		float zCoord = (terrainZ % squareNumber) / squareNumber;
		float answer;
		
		if(gridX +1 >= heightMap.length || gridZ+1 >= heightMap.length){
			return 0;
		}
		if (xCoord <= (1 - zCoord)) {
			answer = Maths.barryCentric(new Vector3f(0, heightMap[gridX][gridZ], 0), new Vector3f(1, heightMap[gridX + 1][gridZ], 0),
					new Vector3f(0, heightMap[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1, heightMap[gridX + 1][gridZ], 0), new Vector3f(1, heightMap[gridX + 1][gridZ + 1], 1),
					new Vector3f(0, heightMap[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
	}

	public float getRelativeCoordinate(float globalCoordinate) {
		boolean xn = false;
		if (globalCoordinate < 0) {
			xn = true;
			// x *= -1;
		}

		globalCoordinate /= SIZE;
		if (xn) {
			globalCoordinate--;
			globalCoordinate += Math.abs(this.x);
		}
		return globalCoordinate;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

}
