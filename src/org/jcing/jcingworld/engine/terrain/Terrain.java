package org.jcing.jcingworld.engine.terrain;

import java.io.PrintStream;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.engine.imagery.TextureAtlas;
import org.jcing.jcingworld.engine.shading.terrain.TerrainShader;
import org.jcing.jcingworld.logging.Logs;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.openSimplex.OpenSimplexNoise;

/**
 * 
 * 
 * 
 * @author Jcing
 *
 */
public class Terrain {

	public static final float TILE_SIZE = 6;

	public static final int TILE_COUNT = 16;
	public static final int TILE_TEX_INDICE_COUNT = TILE_COUNT + 1;
	public static final float SIZE = TILE_SIZE * (TILE_COUNT - 1);

	public static OpenSimplexNoise noise = new OpenSimplexNoise(1337);

	// public static final float TEXTURES_PER_SQUARE = 2f;

	// TODO: Tile MUST be a square (X,Z) at the Moment due to Texture Coordinate
	// calculation in Vertex shader: Try to fix this later!
	// IDEA: 4 per Vertex coordinates + calculation in Geometry shader.

	private static final int VERTEX_COUNT = (TILE_COUNT) * 2;

	private static final boolean FLAT = false;

	private static float heightDelta = 7.25f;
	private static float interpolation = 22.75f;
	private static float continentalHeightDelta = 75;
	private static float continentalInterpolation = 275;
	// TODO: terrain generation y Interpolation

	private float[][] heightMap;
	private Tile[][] tiles;
	private float[] tileTextureIndices;

	private float x;
	private float z;
	private Vector2f gridPos;
	private RawModel model;
	private TextureAtlas textureAtlas;
	private BaseImage blendMap;

	private PrintStream out = Logs.terrain;

	public Terrain(float gridX, float gridZ, Loader loader, TerrainShader shader, TextureAtlas textureAtlas,
			BaseImage blendMap) {
		//TODO: everything from manager
		this.textureAtlas = textureAtlas;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		gridPos = new Vector2f(gridX, gridZ);
		out.println("generating Terrain[" + gridX + "][" + gridZ + "] - " + SIZE + "m² at " + TILE_COUNT + " Tiles");
		this.model = generateTerrain(loader, shader);
	}

	private RawModel generateTerrain(Loader loader, TerrainShader shader) {
		loadheightMap();
		out.println("generated random Heightmap");
		generateTiles();
		out.println("generated Tiles");
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[4 * 6 * (TILE_COUNT - 1) * (TILE_COUNT - 1)];
		int vertexPointer = 0;

		// VERTICES NORMALS TEXTURECOORDS
		for (int j = 0; j < TILE_COUNT; j++) { // i == z
			for (int i = 0; i < TILE_COUNT; i++) { // j == x;
				// SQUARE BOTTOMLEFT
				vertices[vertexPointer * 3] = tiles[i][j].getX()[0];
				vertices[vertexPointer * 3 + 1] = tiles[i][j].getY()[0];
				vertices[vertexPointer * 3 + 2] = tiles[i][j].getZ()[0];
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) j / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) i / ((float) TILE_COUNT);
				vertexPointer++;

				// SQUARE BOTTOMRIGHT
				vertices[vertexPointer * 3] = tiles[i][j].getX()[1];
				vertices[vertexPointer * 3 + 1] = tiles[i][j].getY()[1];
				vertices[vertexPointer * 3 + 2] = tiles[i][j].getZ()[1];
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) (j + 0f) / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0.5f) / ((float) TILE_COUNT);
				vertexPointer++;

				// SQUARE TOPLEFT
				vertices[vertexPointer * 3] = tiles[i][j].getX()[2];
				vertices[vertexPointer * 3 + 1] = tiles[i][j].getY()[2];
				vertices[vertexPointer * 3 + 2] = tiles[i][j].getZ()[2];
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) (j + 0.5f) / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0f) / ((float) TILE_COUNT);
				vertexPointer++;

				// SQUARE TOPRIGHT
				vertices[vertexPointer * 3] = tiles[i][j].getX()[3];
				vertices[vertexPointer * 3 + 1] = tiles[i][j].getY()[3];
				vertices[vertexPointer * 3 + 2] = tiles[i][j].getZ()[3];
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				textureCoords[vertexPointer * 2] = (float) (j + 0.5f) / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0.5f) / ((float) TILE_COUNT);
				vertexPointer++;
			}
		}

		// INDICES
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
		out.println("loaded Mesh!");
		return loader.loadToVAO(vertices, textureCoords, normals, indices, tileTextureIndices);
	}

	private void loadheightMap() {
		//        Random random = new Random();
		heightMap = new float[VERTEX_COUNT][VERTEX_COUNT];
		if (!FLAT) {
			for (int i = 0; i < heightMap.length; i++) {
				for (int j = 0; j < heightMap[i].length; j++) {
					heightMap[j][i] = getNoiseHeight(i, j);
					//                    if (i == 0 && j == 0) {
					//                        heightMap[j][i] = 1 + random.nextFloat();
					//                    } else {
					//                        float delta = random.nextFloat() * maxDelta;
					//                        if (j == 0) {
					//                            heightMap[j][i] = heightMap[j][i - 1] - maxDelta / 2 + delta;
					//                        } else if (i == 0) {
					//                            heightMap[j][i] = heightMap[j - 1][i] - maxDelta / 2 + delta;
					//                        } else {
					//                            heightMap[j][i] = (heightMap[j][i - 1] + heightMap[j - 1][i]) / 2.0f
					//                                    - maxDelta / 2 + delta;
					//                        }
					//                    }
				}
			}
		}
	}

	private float getNoiseHeight(int i, int j) {
		float xi = x + j / 2.0f * TILE_SIZE;
		float zi = z + i / 2.0f * TILE_SIZE;
		//        out.println("xi: " + xi + "  zi: "+ zi);
		return heightDelta * (float) (noise.eval(xi / interpolation, zi / interpolation))
				+ (heightDelta / 6) * (float) (noise.eval(xi / (interpolation / 6), zi / (interpolation / 4)))
				+ (continentalHeightDelta)
						* (float) (noise.eval(xi / (continentalInterpolation), zi / (continentalInterpolation)));
	}

	@SuppressWarnings("unused")
	private Vector3f calculateNormal(int x, int z) {
		// TODO: Tile? Implement normalCalculation
		float heightL = 0, heightR = 0, heightD = 0, heightU = 0;
		if (x - 1 >= 0)
			heightL = heightMap[x - 1][z];
		if (x + 1 < heightMap.length)
			heightR = heightMap[x + 1][z];
		if (z - 1 >= 0)
			heightD = heightMap[x][z - 1];
		if (z + 1 < heightMap.length)
			heightU = heightMap[x][z + 1];

		Vector3f normal = new Vector3f(heightL - heightR, TILE_SIZE, heightD - heightU);
		normal.normalise();
		return normal;
	}

	private static int tex = 0;

	private void generateTiles() {
		tex=0;
		tiles = new Tile[TILE_COUNT][TILE_COUNT];
		float SQUARE_SIZE = Terrain.TILE_SIZE / 2;
		//		System.err.println(textureAtlas.getNumTextures() + " here: " + ((int)getGridX()) % (textureAtlas.getNumTextures()-2));
		for (int i = 0; i < VERTEX_COUNT; i += 2) { // i == z
			for (int j = 0; j < VERTEX_COUNT; j += 2) {// j == x
				float x[] = { j * SQUARE_SIZE, (j + 1) * SQUARE_SIZE, j * SQUARE_SIZE, (j + 1) * SQUARE_SIZE };
				float y[] = { heightMap[j][i], heightMap[j + 1][i], heightMap[j][i + 1], heightMap[j + 1][i + 1] };
				float z[] = { i * SQUARE_SIZE, i * SQUARE_SIZE, (i + 1) * SQUARE_SIZE, (i + 1) * SQUARE_SIZE };
				// tiles[j / 2][i / 2] = new Tile(x, y, z, j / 2, i / 2,
				// (int) (Math.random() * textureAtlas.getNumTextures()));
				tex = (int)(Math.random()*textureAtlas.getNumTextures());
				if(Math.random()*100<80){
					tex %= 3;
				}
				tiles[j / 2][i / 2] = new Tile(x, y, z, j / 2, i / 2,tex);
						//(int) (Math.random() * textureAtlas.getNumTextures()));
			}
//			tex = (int) (Math.random() * textureAtlas.getNumTextures() - 1);

		}

		//		Logs.terrainRegistering.println("tileMap: ");
		//		String t = System.lineSeparator() + "0: ";
		//		for (int i = 0; i < tiles.length; i++) {
		//			for (int j = 0; j < tiles[0].length; j++) {
		//				if(tiles[j][i] != null)
		//					t += "["+j+"]";
		//				else{
		//					t += "[X]";
		//				}
		//			}
		//			t += System.lineSeparator();
		//			t+= i + ": ";
		//		}
		//		Logs.terrainRegistering.println(t);
		constructTileTextureMap();
	}

	public void constructTileTextureMap() {
		tileTextureIndices = new float[TILE_TEX_INDICE_COUNT * TILE_TEX_INDICE_COUNT];
		// TODO: Check why the fuck there are only (TILECOUNT-2)² Tiles...
		// setting 5
		for (int i = 0; i < tileTextureIndices.length; i++) {
			tileTextureIndices[i] = textureAtlas.getNumTextures() - 1;
		}

		// constructing
		for (int i = 1; i < TILE_COUNT; i++) {
			for (int j = 1; j < TILE_COUNT; j++) {
				tileTextureIndices[(i) * TILE_TEX_INDICE_COUNT + (j)] = tiles[j - 1][i - 1].textureIndex;
			}
		}

		out.println("PRINTING TERRAIN:");
		// for (int k = 0; k < texCount ;k++) {
		// String t = "";
		// for (int l = 0; l < texCount; l++) {
		// t += "[" + (int)textureIndices[k*l] +"]";
		// }
		// out.println(t);
		// }
		String t = "";
		for (int i = 0; i < tileTextureIndices.length; i++) {
			if (i % TILE_TEX_INDICE_COUNT == 0) {
				t += System.lineSeparator();
			}
			t += "[" + (int) tileTextureIndices[i] + "]";
		}
		out.println(t);

		// int mapSize = TILE_COUNT;
		// textureIndices = new float[mapSize*mapSize];
		// for (int i = 0; i < mapSize; i++) {
		// String t = "";
		// for (int j = 0; j < mapSize; j++) {
		// textureIndices[i * (mapSize) + j] = tiles[i][j].textureIndex;
		// t += "[" + tiles[i][j].textureIndex + "]";
		// }
		// out.println(t);
		// }
	}

	public void registerNeighbour(Terrain terrain, boolean first) {
		// L T R B
		if (terrain != null) {
			Logs.terrainRegistering.println();

			int side = checkSide(terrain);
			Logs.terrainRegistering.println("### registering " + terrain.getCoordinateString() + " at "
					+ getCoordinateString() + "(side " + side + ")");
//			if (side == 3)
				setTileBorder(terrain.getTileBorder(side), side);
			if (first)
				terrain.registerNeighbour(this, false);
		}
	}

	/**
	 * 
	 * @param index
	 *            which side to be returned. (0=L|1=T|2=R|3=B)
	 * @return the Side of the index of this Terrain!
	 */
	private int checkSide(Terrain terrain) {
		// L T R B
		//		return 0; //TODO: right bottom

		if (terrain.getGridX() < this.getGridX())
			return 0;
		if (terrain.getGridZ() > this.getGridZ())
			return 1;
		if (terrain.getGridX() > this.getGridX())
			return 2;
		if (terrain.getGridZ() < this.getGridZ())
			return 3;
		Logs.terrainRegistering.println(
				"someting went wrong! " + getCoordinateString() + " == " + terrain.getCoordinateString() + "??");
		return -1;

	}

	/**
	 * 
	 * @param index
	 *            which side to be returned. here: (0=R|1=B|2=L|3=T)
	 * @return the (<b>opposite</b>) Side of the index of this Terrain!
	 */
	public Tile[] getTileBorder(int index) {
		// R B L T
		Tile[] border = new Tile[tiles[0].length-1];
		switch (index) {
		case 0:
			//+X
			for (int i = 0; i < border.length; i++) {
				border[i] = tiles[i][0];
			}
			return border;
		case 1:
			for (int i = 0; i < border.length; i++) {
				border[i] = tiles[0][i];
			}
			return border;
		case 2:
			//left border
			for (int i = 0; i < border.length; i++) {
				border[i] = tiles[i][tiles.length-2];
			}
			return border;
		case 3:
			for (int i = 0; i < border.length; i++) {
				border[i] = tiles[tiles.length-2][i];
			}
			return border;

		default:
			Logs.terrainRegistering.println("WHY THE FUXK ARE NO INDEX?");
			return null;
		}
	}

	public void setTileBorder(Tile[] tiles, int index) {
		// L T R B
		PrintStream out = Logs.terrainRegistering;
		switch (index) {
		case 0:
			out.println("SETTING " + getCoordinateString() + "(left side)");
			for (int i = 0; i < tiles.length; i++) {
				//works
				tileTextureIndices[(TILE_TEX_INDICE_COUNT - 1) * (TILE_TEX_INDICE_COUNT) + (i+1)] = tiles[i].textureIndex;
			}

			break;
		case 1:
			out.println("SETTING " + getCoordinateString() + "(top side)");
			for (int i = 0; i < tiles.length; i++) {
				//				this.tiles[i][0] = tiles[i];
				tileTextureIndices[(i + 1) * TILE_TEX_INDICE_COUNT + TILE_TEX_INDICE_COUNT - 1] = tiles[i].textureIndex;
			}
			break;
		case 2:
			out.println("SETTING " + getCoordinateString() + "(right side)");
			for (int i = 0; i < tiles.length; i++) {
				//				this.tiles[i][this.tiles[i].length - 1] = tiles[i];
				tileTextureIndices[i+1] = tiles[i].textureIndex;
			}

			break;
		case 3:

			out.println("SETTING " + getCoordinateString() + "(bottom side)");
			for (int i = 0; i < tiles.length; i++) {
				//works
				tileTextureIndices[(i + 1) * (TILE_TEX_INDICE_COUNT)] = tiles[i].textureIndex;
			}
			break;
		}
	}

	private int txtindex = 0;

	public void makeRandom() {
		for (int i = 0; i < TILE_COUNT + 1; i++) {
			tileTextureIndices[txtindex] = (tileTextureIndices[txtindex] + 1) % textureAtlas.getNumTextures();
			txtindex = (txtindex + 1) % (tileTextureIndices.length);
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

	public float[] getTextureIndices() {
		return tileTextureIndices;
	}

	@Deprecated
	public float getRelativeCoordinate(float globalCoordinate) {
		boolean xn = false;
		if (globalCoordinate < 0) {
			xn = true;
		}

		globalCoordinate /= SIZE;
		if (xn) {
			globalCoordinate--;
			globalCoordinate += Math.abs(this.x);
		}
		return globalCoordinate;
	}

	public float getHeight(float x, float z) {
		// float z = globalZ; // switch weil faul und fehler
		// float x = globalX;
		if (!inTerrain(x, z)) {
			return 0;
		}
		float squareNumber = TILE_SIZE / 2;// SIZE / (float) (SQUARE_COUNT);
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
			answer = Maths.barryCentric(new Vector3f(0, heightMap[gridX][gridZ], 0),
					new Vector3f(1, heightMap[gridX + 1][gridZ], 0), new Vector3f(0, heightMap[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		} else {
			answer = Maths.barryCentric(new Vector3f(1, heightMap[gridX + 1][gridZ], 0),
					new Vector3f(1, heightMap[gridX + 1][gridZ + 1], 1),
					new Vector3f(0, heightMap[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
		}
		return answer;
	}

	public String getCoordinateString() {
		return "[" + (int) getGridX() + "][" + (int) getGridZ() + "]";
	}

	public RawModel getModel() {
		return model;
	}

	public TextureAtlas getTexturePack() {
		return textureAtlas;
	}

	public BaseImage getBlendMap() {
		return blendMap;
	}

	public Vector2f getGridPos() {
		return gridPos;
	}

	public float getGridX() {
		return gridPos.x;
	}

	public float getGridZ() {
		return gridPos.y;
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

}
