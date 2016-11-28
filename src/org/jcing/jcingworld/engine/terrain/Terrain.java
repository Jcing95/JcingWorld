package org.jcing.jcingworld.engine.terrain;

import java.nio.FloatBuffer;
import java.util.Random;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.textures.TerrainTexture;
import org.jcing.jcingworld.engine.textures.TerrainTexturePack;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Terrain {

	public static final float SQUARE_SIZE = 4;

	public static final int SQUARE_COUNT = 128;
	private static final int VERTEX_COUNT = (SQUARE_COUNT) * 2;
	public static final float SIZE = SQUARE_SIZE * SQUARE_COUNT;

	private static float maxDelta = 1f;
	private float[][] heightMap;
	private Tile[][] tiles;

	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;

	private static final boolean FLAT = false;

	public static final float TEXTURES_PER_SQUARE = 1f;

	public Terrain(float gridX, float gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader);
	}

	private RawModel generateTerrain(Loader loader) {
		loadheightMap();
		generateSquares();
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[4 * 6 * (SQUARE_COUNT) * (SQUARE_COUNT)];
		int vertexPointer = 0;

		// VERTICES NORMALS TEXTURECOORDS
		for (int j = 0; j < SQUARE_COUNT; j++) { // i == z
			for (int i = 0; i < SQUARE_COUNT; i++) { // j == x;
				// TODO: NORMALS
				// SQUARE BOTTOMLEFT
				vertices[vertexPointer * 3] = tiles[i][j].getX()[0];
				vertices[vertexPointer * 3 + 1] = tiles[i][j].getY()[0];
				vertices[vertexPointer * 3 + 2] = tiles[i][j].getZ()[0];
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) j / ((float) SQUARE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) i / ((float) SQUARE_COUNT);
				vertexPointer++;

				// SQUARE BOTTOMRIGHT
				vertices[vertexPointer * 3] = tiles[i][j].getX()[1];
				vertices[vertexPointer * 3 + 1] = tiles[i][j].getY()[1];
				vertices[vertexPointer * 3 + 2] = tiles[i][j].getZ()[1];
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) (j + 0f) / ((float) SQUARE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0.5f) / ((float) SQUARE_COUNT);
				vertexPointer++;

				// SQUARE TOPLEFT
				vertices[vertexPointer * 3] = tiles[i][j].getX()[2];
				vertices[vertexPointer * 3 + 1] = tiles[i][j].getY()[2];
				vertices[vertexPointer * 3 + 2] = tiles[i][j].getZ()[2];
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) (j + 0.5f) / ((float) SQUARE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0f) / ((float) SQUARE_COUNT);
				vertexPointer++;

				// SQUARE TOPRIGHT
				vertices[vertexPointer * 3] = tiles[i][j].getX()[3];
				vertices[vertexPointer * 3 + 1] = tiles[i][j].getY()[3];
				vertices[vertexPointer * 3 + 2] = tiles[i][j].getZ()[3];
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) (j + 0.5f) / ((float) SQUARE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0.5f) / ((float) SQUARE_COUNT);
				vertexPointer++;
			}
		}

		// INDICES
		int topStep = 2 * VERTEX_COUNT;
		int pointer = 0;
		for (int gz = 0; gz < SQUARE_COUNT; gz++) {
			int square = 0;
			for (int gx = 0; gx < SQUARE_COUNT; gx++) {
				square = ((gz) * VERTEX_COUNT * 2) + gx * 4;

				/**/ // 2t##3t##6t##7t
				/**/ // #SSS#SSS#SSS#
				/**/ // #SSS#SSS#SSS#
				/**/ // 0t##1t##4t##5t
				/**/ // #SSS#SSS#SSS#
				/**/ // #SSS#SSS#SSS#
				/**/ // 2###3###6###7
				/**/ // #SSS#SSS#SSS#
				/**/ // #SSS#SSS#SSS#
				/**/ // 0###1###4###5

				// SQUARE
				/**/// TRI 1
				/**/indices[pointer++] = square + 2;
				/**/indices[pointer++] = square + 1;
				/**/indices[pointer++] = square + 0;
				/**/// TRI 2
				/**/indices[pointer++] = square + 1;
				/**/indices[pointer++] = square + 2;
				/**/indices[pointer++] = square + 3;

				if (gx < SQUARE_COUNT - 1) {
					// PSEUDO RIGHT
					/**/// TRI 1
					/**/indices[pointer++] = square + 3;
					/**/indices[pointer++] = square + 4;
					/**/indices[pointer++] = square + 1;
					/**/// TRI 2
					/**/indices[pointer++] = square + 4;
					/**/indices[pointer++] = square + 3;
					/**/indices[pointer++] = square + 6;

					if (gz < SQUARE_COUNT - 1) {
						// PSEUDO PSEUDO
						/**/// TRI 1
						/**/indices[pointer++] = square + 1 + topStep;
						/**/indices[pointer++] = square + 6;
						/**/indices[pointer++] = square + 3;
						/**/// TRI 2
						/**/indices[pointer++] = square + 6;
						/**/indices[pointer++] = square + 1 + topStep;
						/**/indices[pointer++] = square + 4 + topStep;
					}
				}

				if (gz < SQUARE_COUNT - 1) {
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
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	private void loadheightMap() {
		Random random = new Random();
		heightMap = new float[VERTEX_COUNT][VERTEX_COUNT];
		if (!FLAT) {
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
			// heightMap[0][0] = 20;
			// heightMap[VERTEX_COUNT-1][VERTEX_COUNT-1] = 100;
		}
	}

	private void generateSquares() {
		tiles = new Tile[SQUARE_COUNT][SQUARE_COUNT];
		float SQUARE_SIZE = Terrain.SQUARE_SIZE / 2;
		for (int i = 0; i < VERTEX_COUNT; i += 2) { // i == z
			for (int j = 0; j < VERTEX_COUNT; j += 2) {// j == x

				float x[] = { j * SQUARE_SIZE, (j + 1) * SQUARE_SIZE, j * SQUARE_SIZE, (j + 1) * SQUARE_SIZE };
				float y[] = { heightMap[j][i], heightMap[j + 1][i], heightMap[j][i + 1], heightMap[j + 1][i + 1] };
				float z[] = { i * SQUARE_SIZE, i * SQUARE_SIZE, (i + 1) * SQUARE_SIZE, (i + 1) * SQUARE_SIZE };
				tiles[j / 2][i / 2] = new Tile(x, y, z, j / 2, i / 2);
			}
		}
		// System.out.println(squares.length + " last: "+
		// squares[squares.length-1][squares.length-1].getX()[3]);
	}

	private Vector3f calculateNormal(int x, int z) {
		// TODO: Implement
		float heightL = 0, heightR = 0, heightD = 0, heightU = 0;
		if (x - 1 >= 0)
			heightL = heightMap[x - 1][z];
		if (x + 1 < heightMap.length)
			heightR = heightMap[x + 1][z];
		if (z - 1 >= 0)
			heightD = heightMap[x][z - 1];
		if (z + 1 < heightMap.length)
			heightU = heightMap[x][z + 1];

		Vector3f normal = new Vector3f(heightL - heightR, SQUARE_SIZE, heightD - heightU);
		normal.normalise();
		return normal;
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

	public float getHeight(float x, float z) {
		// float z = globalZ; // switch weil faul und fehler
		// float x = globalX;
		if (!inTerrain(x, z)) {
			return 0;
		}
		float squareNumber = SQUARE_SIZE / 2;// SIZE / (float) (SQUARE_COUNT);
		float terrainX = (x - this.x);
		float terrainZ = (z - this.z);
		int gridX = (int) Math.floor(terrainX / squareNumber);
		int gridZ = (int) Math.floor(terrainZ / squareNumber);
		float xCoord = (terrainX % squareNumber) / squareNumber;
		float zCoord = (terrainZ % squareNumber) / squareNumber;
		float answer;

		if (gridX + 1 >= heightMap.length || gridZ + 1 >= heightMap.length) {
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
