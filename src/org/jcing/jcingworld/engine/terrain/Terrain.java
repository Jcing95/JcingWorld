package org.jcing.jcingworld.engine.terrain;

import java.io.PrintStream;
import java.util.Random;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.engine.imagery.TextureAtlas;
import org.jcing.jcingworld.engine.shading.terrain.TerrainShader;
import org.jcing.jcingworld.logging.Logs;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

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
    // TODO: Tile MUST be a square (X,Z) at the Moment due to Texture Coordinate
    // calculation in Vertex shader: Try to fix this later!
    // IDEA: 4 per Vertex coordinates + calculation in Geometry shader.

    private static final int VERTEX_COUNT = (TILE_COUNT) * 2;
    public static final float SIZE = TILE_SIZE * (TILE_COUNT - 1);

    private static float maxDelta = 1f;
    private float[][] heightMap;
    private Tile[][] tiles;
    private float[] textureIndices;

    private float x;
    private float z;
    private float gridX, gridZ;
    private RawModel model;
    private TextureAtlas textureAtlas;
    private BaseImage blendMap;

    private static final boolean FLAT = true;

    public static final float TEXTURES_PER_SQUARE = 2f;
    private PrintStream out = Logs.terrain;

    public Terrain(float gridX, float gridZ, Loader loader, TerrainShader shader,
            TextureAtlas textureAtlas, BaseImage blendMap) {
        this.textureAtlas = textureAtlas;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.gridX = gridX;
        this.gridZ = gridZ;
        out.println("generating Terrain[" + gridX + "][" + gridZ + "] " + SIZE + "² ...");
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
        int[] indices = new int[4 * 6 * TILE_COUNT * TILE_COUNT];
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
                textureCoords[vertexPointer * 2 + 1] = 1
                        - (float) (i + 0.5f) / ((float) TILE_COUNT);
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
                textureCoords[vertexPointer * 2 + 1] = 1
                        - (float) (i + 0.5f) / ((float) TILE_COUNT);
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
        return loader.loadToVAO(vertices, textureCoords, normals, indices, textureIndices);
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
                            heightMap[j][i] = (heightMap[j][i - 1] + heightMap[j - 1][i]) / 2.0f
                                    - maxDelta / 2 + delta;
                        }
                    }
                }
            }
        }
    }

    private static int texdex = 0;

    private void generateTiles() {
        tiles = new Tile[TILE_COUNT][TILE_COUNT];
        textureIndices = new float[TILE_COUNT * TILE_COUNT];
        float SQUARE_SIZE = Terrain.TILE_SIZE / 2;
        out.println("PRINTING TERRAIN:");
        for (int i = 0; i < VERTEX_COUNT; i += 2) { // i == z

            for (int j = 0; j < VERTEX_COUNT; j += 2) {// j == x
                float x[] = { j * SQUARE_SIZE, (j + 1) * SQUARE_SIZE, j * SQUARE_SIZE,
                        (j + 1) * SQUARE_SIZE };
                float y[] = { heightMap[j][i], heightMap[j + 1][i], heightMap[j][i + 1],
                        heightMap[j + 1][i + 1] };
                float z[] = { i * SQUARE_SIZE, i * SQUARE_SIZE, (i + 1) * SQUARE_SIZE,
                        (i + 1) * SQUARE_SIZE };
                //                tiles[j / 2][i / 2] = new Tile(x, y, z, j / 2, i / 2,
                //                        (int) (Math.random() * textureAtlas.getNumTextures()));
                texdex = (texdex + 1) % textureAtlas.getNumTextures();
                tiles[j / 2][i / 2] = new Tile(x, y, z, j / 2, i / 2, texdex);
            }

        }
        for (int i = 0; i < tiles.length; i++) {
            String t = "";
            for (int j = 0; j < tiles[0].length; j++) {
                textureIndices[i * tiles.length + j] = tiles[i][j].textureIndex;
                t += "[" + tiles[i][j].textureIndex + "]";
            }
            out.println(t);
        }
        // System.out.println(squares.length + " last: "+
        // squares[squares.length-1][squares.length-1].getX()[3]);
    }

    public void connectRight() {

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
                    new Vector3f(1, heightMap[gridX + 1][gridZ], 0),
                    new Vector3f(0, heightMap[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            answer = Maths.barryCentric(new Vector3f(1, heightMap[gridX + 1][gridZ], 0),
                    new Vector3f(1, heightMap[gridX + 1][gridZ + 1], 1),
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

    public void connectSouth(Terrain terrain) {

    }

    public float[] getTextureIndices() {
        return textureIndices;
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

    public TextureAtlas getTexturePack() {
        return textureAtlas;
    }

    public BaseImage getBlendMap() {
        return blendMap;
    }

    public float getGridX() {
        return gridX;
    }

    public float getGridZ() {
        return gridZ;
    }

    private int txtindex = 0;

    public void makeRandom() {
        for (int i = 0; i < TILE_COUNT; i++) {
            textureIndices[txtindex] = (textureIndices[txtindex] + 1)
                    % textureAtlas.getNumTextures();
            txtindex = (txtindex + 1) % (textureIndices.length);
        }
    }

}
