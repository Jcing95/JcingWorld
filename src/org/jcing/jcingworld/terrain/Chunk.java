package org.jcing.jcingworld.terrain;

import java.io.PrintStream;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.engine.imagery.TextureAtlas;
import org.jcing.jcingworld.engine.shading.terrain.TerrainShader;
import org.jcing.jcingworld.logging.Logs;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;

/**
 * 
 * 
 * 
 * @author Jcing
 *
 */
public class Chunk {

	public static final float TILE_SIZE = 6;

	public static final int TILE_COUNT = 16;
	public static final int TILE_TEX_INDICE_COUNT = TILE_COUNT + 1;
	public static final float SIZE = TILE_SIZE * (TILE_COUNT - 1);

	// public static final float TEXTURES_PER_SQUARE = 2f;

	// TODO: Tile MUST be a square (X,Z) at the Moment due to Texture Coordinate
	// calculation in Vertex shader: Try to fix this later!
	// IDEA: 4 per Vertex coordinates + calculation in Geometry shader.

	static final int VERTEX_COUNT = (TILE_COUNT) * 2;

	// TODO: terrain generation y Interpolation

	// private float[][] heightMap;
	ChunkData chunkdata;

	// private Point gridPos;
	private RawModel model;
	private TextureAtlas textureAtlas;
	private BaseImage blendMap;
	private Terrain terrain;

	private static PrintStream info = Logs.subLog(Logs.chunkLoading, "info", false);
	private static PrintStream out = Logs.subLog(Logs.chunkLoading, "Chunk", false);

	private static int[] indices;
	float x, z;

	private BaseImage selectedTex;

	public Chunk(int gridX, int gridZ, Loader loader, TerrainShader shader, TextureAtlas textureAtlas,
			BaseImage blendMap, BaseImage selectedTex, Terrain terrain) {
		// TODO: everything from manager
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
//		bounds = new Rectangle((int)x,(int)z,(int)SIZE,(int)SIZE);
		this.textureAtlas = textureAtlas;
		this.blendMap = blendMap;
		this.selectedTex = selectedTex;
		this.terrain = terrain;
		// gridPos = new Vector2f(gridX, gridZ);
		chunkdata = terrain.getSaver().get(gridX, gridZ);
		if (!chunkdata.initialized) {
			System.out.println("CHUNK " + chunkdata.x + "|" + chunkdata.z + " WAS NOT INITIALIZED ("
					+ Maths.fastFloor(chunkdata.x / DataChunk.SIZE) + "|"
					+ Maths.fastFloor(chunkdata.z / DataChunk.SIZE) + ") ...");
			info.println("generating Chunk[" + gridX + "][" + gridZ + "] - " + SIZE + "m² at " + TILE_COUNT + " Tiles");
			//			chunkdata.generate(textureAtlas);
			//			chunkdata.apply();
			out.println("G" + gridZ + " d" + chunkdata.z);
		}
		this.model = generateTerrain(loader, shader);

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

	private RawModel generateTerrain(Loader loader, TerrainShader shader) {
//		info.println("generated random Heightmap");
		info.println("generated Tiles");
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

		info.println("loaded Mesh!");
		return loader.loadToVAO(vertices, textureCoords, normals, indices, chunkdata.topTileTextureIndices);
	}

	public void registerNeighbour(Chunk terrain, boolean first) {
		// L T R B
		if (terrain != null) {
			Logs.terrainRegistering.println();

			int side = checkSide(terrain);
			Logs.terrainRegistering.println("### registering " + terrain.getCoordinateString() + " at "
					+ getCoordinateString() + "(side " + side + ")");
			// if (side == 3)
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
	private int checkSide(Chunk terrain) {
		// L T R B
		// return 0; //TODO: right bottom

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

	// private float getNoiseHeight(int i, int j) {
	// float xi = x + j / 2.0f * TILE_SIZE;
	// float zi = z + i / 2.0f * TILE_SIZE;
	// // out.println("xi: " + xi + " zi: "+ zi);
	// return heightDelta * (float) (noise.eval(xi / interpolation, zi /
	// interpolation))
	// + (heightDelta / 6) * (float) (noise.eval(xi / (interpolation / 6), zi /
	// (interpolation / 4)))
	// + (continentalHeightDelta) * (float) (noise.eval(xi /
	// (continentalInterpolation), zi / (continentalInterpolation)));
	// }

	/**
	 * 
	 * @param index
	 *            which side to be returned. here: (0=R|1=B|2=L|3=T)
	 * @return the (<b>opposite</b>) Side of the index of this Terrain!
	 */
	public Tile[] getTileBorder(int index) {
		// R B L T
		Tile[] border = new Tile[TILE_COUNT - 1];
		switch (index) {
		case 0:
			// +X
			for (int i = 0; i < border.length; i++) {
				border[i] = chunkdata.getTile(TILE_COUNT - 1, i);
			}
			return border;
		case 1:

			for (int i = 0; i < border.length; i++) {
				border[border.length - 1 - i] = chunkdata.getTile(i, 0);
			}
			return border;
		case 2:
			// left border

			for (int i = 0; i < border.length; i++) {
				border[i] = chunkdata.getTile(0, i);
			}
			return border;
		case 3:

			for (int i = 0; i < border.length; i++) {
				border[border.length - 1 - i] = chunkdata.getTile(i, TILE_COUNT - 1);
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
				// works
				chunkdata.topTileTextureIndices[(TILE_TEX_INDICE_COUNT - 1) * (TILE_TEX_INDICE_COUNT)
						+ (i + 1)] = tiles[i].textureIndex;
			}

			break;
		case 1:
			out.println("SETTING " + getCoordinateString() + "(top side)");
			for (int i = 0; i < tiles.length; i++) {
				// this.tiles[i][0] = tiles[i];
				chunkdata.topTileTextureIndices[(i + 1) * TILE_TEX_INDICE_COUNT + TILE_TEX_INDICE_COUNT
						- 1] = tiles[i].textureIndex;
			}
			break;
		case 2:
			out.println("SETTING " + getCoordinateString() + "(right side)");
			for (int i = 0; i < tiles.length; i++) {
				// this.tiles[i][this.tiles[i].length - 1] = tiles[i];
				chunkdata.topTileTextureIndices[i + 1] = tiles[i].textureIndex;
			}

			break;
		case 3:

			out.println("SETTING " + getCoordinateString() + "(bottom side)");
			for (int i = 0; i < tiles.length; i++) {
				// works
				chunkdata.topTileTextureIndices[(i + 1) * (TILE_TEX_INDICE_COUNT)] = tiles[i].textureIndex;
			}
			break;
		}
	}

	public void makeRandom() {
		// for (int i = 0; i < TILE_COUNT + 1; i++) {
		// tileTextureIndices[txtindex] = (tileTextureIndices[txtindex] + 1)
		// % textureAtlas.getNumTextures();
		// txtindex = (txtindex + 1) % (tileTextureIndices.length);
		// }
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
		return chunkdata.topTileTextureIndices;
	}

	public ChunkData getChunkdata() {
		return chunkdata;
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

	public int getGridX() {
		return chunkdata.x;
	}

	public int getGridZ() {
		return chunkdata.z;
	}

	public float getX() {
		return this.x;
	}

	public float getZ() {
		return this.z;
	}

	public void dismiss() {
		model.delete();
		chunkdata.dismiss();
	}

	int i,j;
	
	private int convertWorldPos(float x){
		return Maths.fastFloor(x / Chunk.SIZE);
	}
	public Vector2f getSelected() {
		Vector2f mousePos = new Vector2f(terrain.getMousePos().x,terrain.getMousePos().z);
		if(convertWorldPos(mousePos.x) == this.getGridX() && convertWorldPos(mousePos.y) == this.getGridZ()){
//			System.out.println("SELECTED!" +  (mousePos.y-z)/TILE_SIZE +"|"+(mousePos.x-x)/TILE_SIZE);
			return new Vector2f(Maths.fastFloor((mousePos.y-z)/TILE_SIZE),Maths.fastFloor(TILE_COUNT-(mousePos.x-x)/TILE_SIZE-1));
		}
		i%=TILE_COUNT;
		j%= TILE_COUNT;
		return new Vector2f(-1,-1);
//		return selectedVec;
	}

	public BaseImage getSelectedTex() {
		// TODO Auto-generated method stub
		return selectedTex;
	}


	// public Tile[][] getTiles() {
	// return chunkdata.tiles;
	// }

}
