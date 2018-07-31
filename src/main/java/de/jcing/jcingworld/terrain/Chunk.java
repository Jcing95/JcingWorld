package de.jcing.jcingworld.terrain;

import org.lwjgl.util.vector.Vector2f;

import de.jcing.jcingworld.engine.GraphicsLoader;
import de.jcing.jcingworld.engine.entities.models.RawModel;
import de.jcing.jcingworld.engine.imagery.BaseImage;
import de.jcing.jcingworld.engine.imagery.TextureAtlas;
import de.jcing.jcingworld.engine.shading.terrain.TerrainShader;
import de.jcing.jcingworld.toolbox.Maths;
import de.jcing.log.Log;

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

	
	
	// private float[][] heightMap;

	// private Point gridPos;
	private RawModel model;
	private TextureAtlas textureAtlas;
	private BaseImage blendMap;
	private Terrain terrain;

	private Log log = Log.getLog(Chunk.class);

	private static int[] indices;
	
	float xPhysical, zPhysical;
	int xLogical, zLogical;

	private BaseImage selectedTex;
	
	private Tile[] tiles;
	private float[] textureIndices;

	public Chunk(int logicalX, int logicalZ, GraphicsLoader loader, TerrainShader shader, TextureAtlas textureAtlas, BaseImage blendMap, BaseImage selectedTex,
			Terrain terrain) {
		// TODO: everything from manager
		
		xLogical = logicalX;
		zLogical = logicalZ;
		
		xPhysical = logicalX * SIZE;
		zPhysical = logicalZ * SIZE;
		
		this.textureAtlas = textureAtlas;
		this.blendMap = blendMap;
		this.selectedTex = selectedTex;
		this.terrain = terrain;

		initData();
		
		this.model = generateTerrain(loader, shader);

	}
	
	
	private void initData() {
		tiles = new Tile[TILE_COUNT * TILE_COUNT];
		
		for (int i = 0; i < Chunk.VERTEX_COUNT; i += 2) { // i == z
			for (int j = 0; j < Chunk.VERTEX_COUNT; j += 2) {// j == x
				Tile tile = new Tile(i, j);
				tile.generate(this, terrain.getGenerator(), i,j);
				setTile(i / 2, j / 2, tile);
			}
		}
		textureIndices = new float[Chunk.TILE_TEX_INDICE_COUNT * Chunk.TILE_TEX_INDICE_COUNT];

		// constructing
		for (int i = 1; i < Chunk.TILE_COUNT; i++) {
			for (int j = 1; j < Chunk.TILE_COUNT; j++) {
				textureIndices[(Chunk.TILE_TEX_INDICE_COUNT - 1 - j) * Chunk.TILE_TEX_INDICE_COUNT + (i)] = getTile(j - 1, i - 1).textureIndex;
			}
		}
	}
	
	public Tile getTile(int x, int z) {
		return tiles[z * TILE_COUNT + x];
	}
	
	private void setTile(int x, int z, Tile tile) {
		 tiles[z * TILE_COUNT + x] = tile;
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

	private RawModel generateTerrain(GraphicsLoader loader, TerrainShader shader) {
		// info.println("generated random Heightmap");
		log.info("generated Tiles");
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int vertexPointer = 0;
		// VERTICES NORMALS TEXTURECOORDS
		for (int j = 0; j < TILE_COUNT; j++) { // i == z
			for (int i = 0; i < TILE_COUNT; i++) { // j == x;
				// SQUARE BOTTOMLEFT
				vertices[vertexPointer * 3] = getTile(i, j).getX()[0];
				vertices[vertexPointer * 3 + 1] = getTile(i, j).getY()[0];
				vertices[vertexPointer * 3 + 2] = getTile(i, j).getZ()[0];
				normals[vertexPointer * 3] = getTile(i, j).getNormal()[0].getX();
				normals[vertexPointer * 3 + 1] = getTile(i, j).getNormal()[0].getY();
				normals[vertexPointer * 3 + 2] = getTile(i, j).getNormal()[0].getZ();
				textureCoords[vertexPointer * 2] = (float) j / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) i / ((float) TILE_COUNT);
				vertexPointer++;

				// SQUARE BOTTOMRIGHT
				vertices[vertexPointer * 3] = getTile(i, j).getX()[1];
				vertices[vertexPointer * 3 + 1] = getTile(i, j).getY()[1];
				vertices[vertexPointer * 3 + 2] = getTile(i, j).getZ()[1];
				normals[vertexPointer * 3] = getTile(i, j).getNormal()[1].getX();
				normals[vertexPointer * 3 + 1] = getTile(i, j).getNormal()[1].getY();
				normals[vertexPointer * 3 + 2] = getTile(i, j).getNormal()[1].getZ();
				textureCoords[vertexPointer * 2] = (float) (j + 0f) / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0.5f) / ((float) TILE_COUNT);
				vertexPointer++;

				// SQUARE TOPLEFT
				vertices[vertexPointer * 3] = getTile(i, j).getX()[2];
				vertices[vertexPointer * 3 + 1] = getTile(i, j).getY()[2];
				vertices[vertexPointer * 3 + 2] = getTile(i, j).getZ()[2];
				normals[vertexPointer * 3] = getTile(i, j).getNormal()[2].getX();
				normals[vertexPointer * 3 + 1] = getTile(i, j).getNormal()[2].getY();
				normals[vertexPointer * 3 + 2] = getTile(i, j).getNormal()[2].getZ();
				textureCoords[vertexPointer * 2] = (float) (j + 0.5f) / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0f) / ((float) TILE_COUNT);
				vertexPointer++;

				// SQUARE TOPRIGHT
				vertices[vertexPointer * 3] = getTile(i, j).getX()[3];
				vertices[vertexPointer * 3 + 1] = getTile(i, j).getY()[3];
				vertices[vertexPointer * 3 + 2] = getTile(i, j).getZ()[3];
				normals[vertexPointer * 3] = getTile(i, j).getNormal()[3].getX();
				normals[vertexPointer * 3 + 1] = getTile(i, j).getNormal()[3].getY();
				normals[vertexPointer * 3 + 2] = getTile(i, j).getNormal()[3].getZ();
				textureCoords[vertexPointer * 2] = (float) (j + 0.5f) / ((float) TILE_COUNT);
				textureCoords[vertexPointer * 2 + 1] = 1 - (float) (i + 0.5f) / ((float) TILE_COUNT);
				vertexPointer++;
			}
		}

		log.info("loaded Mesh!");
		return loader.loadToVAO(vertices, textureCoords, normals, indices, textureIndices);
	}

	public void registerNeighbour(Chunk terrain, boolean first) {
		// L T R B
		if (terrain != null) {
			log.debug("");

			int side = checkSide(terrain);
			log.debug("### registering " + terrain.getCoordinateString() + " at " + getCoordinateString() + "(side " + side + ")");
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

		if (terrain.getLogicalX() < this.getLogicalX())
			return 0;
		if (terrain.getLogicalZ() > this.getLogicalZ())
			return 1;
		if (terrain.getLogicalX() > this.getLogicalX())
			return 2;
		if (terrain.getLogicalZ() < this.getLogicalZ())
			return 3;
		log.error("someting went wrong! " + getCoordinateString() + " == " + terrain.getCoordinateString() + "??");
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
		Tile[] border = new Tile[TILE_COUNT - 1];
		switch (index) {
		case 0:
			// +X
			for (int i = 0; i < border.length; i++) {
				border[i] = getTile(TILE_COUNT - 1, i);
			}
			return border;
		case 1:

			for (int i = 0; i < border.length; i++) {
				border[border.length - 1 - i] = getTile(i, 0);
			}
			return border;
		case 2:
			// left border

			for (int i = 0; i < border.length; i++) {
				border[i] = getTile(0, i);
			}
			return border;
		case 3:

			for (int i = 0; i < border.length; i++) {
				border[border.length - 1 - i] = getTile(i, TILE_COUNT - 1);
			}
			return border;

		default:
			log.error("WHY THE FUXK ARE NO INDEX?");
			return null;
		}
	}

	public void setTileBorder(Tile[] tiles, int index) {
		// L T R B
		switch (index) {
		case 0:
			log.info("SETTING " + getCoordinateString() + "(left side)");
			for (int i = 0; i < tiles.length; i++) {
				// works
				textureIndices[(TILE_TEX_INDICE_COUNT - 1) * (TILE_TEX_INDICE_COUNT) + (i + 1)] = tiles[i].textureIndex;
			}

			break;
		case 1:
			log.info("SETTING " + getCoordinateString() + "(top side)");
			for (int i = 0; i < tiles.length; i++) {
				// this.tiles[i][0] = tiles[i];
				textureIndices[(i + 1) * TILE_TEX_INDICE_COUNT + TILE_TEX_INDICE_COUNT - 1] = tiles[i].textureIndex;
			}
			break;
		case 2:
			log.info("SETTING " + getCoordinateString() + "(right side)");
			for (int i = 0; i < tiles.length; i++) {
				// this.tiles[i][this.tiles[i].length - 1] = tiles[i];
				textureIndices[i + 1] = tiles[i].textureIndex;
			}

			break;
		case 3:

			log.info("SETTING " + getCoordinateString() + "(bottom side)");
			for (int i = 0; i < tiles.length; i++) {
				// works
				textureIndices[(i + 1) * (TILE_TEX_INDICE_COUNT)] = tiles[i].textureIndex;
			}
			break;
		}
	}

	public boolean isInTerrain(float globalX, float globalZ) {
		// float x = getRelativeCoordinate(globalX);
		// float y = getRelativeCoordinate(globalZ);
		float x = globalX - this.xPhysical;
		float z = globalZ - this.zPhysical;
		if (x < 0 || z < 0 || z >= SIZE || x >= SIZE) {
			return false;
		}
		return true;
	}

	public float[] getTextureIndices() {
		return textureIndices;
	}

	
	public String getCoordinateString() {
		return "[" + (int) getLogicalX() + "][" + (int) getLogicalZ() + "]";
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

	public int getLogicalX() {
		return xLogical;
	}

	public int getLogicalZ() {
		return zLogical;
	}

	public float getPhysicalX() {
		return this.xPhysical;
	}

	public float getPhysicalZ() {
		return this.zPhysical;
	}

	public void dismiss() {
		model.delete();
	}

	int i, j;

	private int convertWorldPos(float x) {
		return Maths.fastFloor(x / Chunk.SIZE);
	}

	public Vector2f getSelected() {
		Vector2f mousePos = new Vector2f(terrain.getMousePos().x, terrain.getMousePos().z);
		if (convertWorldPos(mousePos.x) == this.getLogicalX() && convertWorldPos(mousePos.y) == this.getLogicalZ()) {
			// System.out.println("SELECTED!" + (mousePos.y-z)/TILE_SIZE +"|"+(mousePos.x-x)/TILE_SIZE);
			return new Vector2f(Maths.fastFloor((mousePos.y - zPhysical) / TILE_SIZE), Maths.fastFloor(TILE_COUNT - (mousePos.x - xPhysical) / TILE_SIZE - 1));
		}
		i %= TILE_COUNT;
		j %= TILE_COUNT;
		return new Vector2f(-1, -1);
		// return selectedVec;
	}

	public BaseImage getSelectedTex() {
		return selectedTex;
	}

	// public Tile[][] getTiles() {
	// return chunkdata.tiles;
	// }

}
