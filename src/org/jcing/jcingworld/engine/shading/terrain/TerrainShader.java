package org.jcing.jcingworld.engine.shading.terrain;

import org.jcing.jcingworld.engine.entities.Camera;
import org.jcing.jcingworld.engine.lighting.Ambient;
import org.jcing.jcingworld.engine.lighting.Light;
import org.jcing.jcingworld.engine.shading.ShaderProgram;
import org.jcing.jcingworld.engine.terrain.Terrain;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class TerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/org/jcing/jcingworld/engine/shading/terrain/TerrainVertexShader";
	private static final String FRAGMENT_FILE = "src/org/jcing/jcingworld/engine/shading/terrain/TerrainFragmentShader";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_ambient;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColour;
	private int location_mainTexture;
	private int location_topTexture;
	private int location_leftTexture;
	private int location_bottomTexture;
	private int location_rightTexture;
	private int location_blendMap;
	private int location_terrainSize;
	private int location_textureIndices;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColour = super.getUniformLocation("lightColor");
		location_ambient = super.getUniformLocation("ambient");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColour = super.getUniformLocation("skyColor");
		location_mainTexture = super.getUniformLocation("backgroundTexture");
		location_topTexture = super.getUniformLocation("rTexture");
		location_leftTexture = super.getUniformLocation("gTexture");
		location_bottomTexture = super.getUniformLocation("bTexture");
		location_rightTexture = super.getUniformLocation("blackTexture");
		location_blendMap = super.getUniformLocation("blendMap");
		location_terrainSize = super.getUniformLocation("terrainSize");
		location_textureIndices = super.getUniformBufferIndex("textureIndicesBuffer");
	}

	public void connectTextureUnits() {
		super.loadInt(location_mainTexture, 0);
		super.loadInt(location_topTexture, 1);
		super.loadInt(location_leftTexture, 2);
		super.loadInt(location_bottomTexture, 3);
		super.loadInt(location_rightTexture, 4);
		super.loadInt(location_blendMap, 5);
		super.loadFloat(location_terrainSize, Terrain.TILE_COUNT);
	}

	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_reflectivity, reflectivity);
		super.loadFloat(location_shineDamper, damper);
	}

	public void loadViewMatrix(Camera camera) {
		super.loadMatrix(location_viewMatrix, Maths.createViewMatrix(camera));
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}

	public void loadLight(Light light) {
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColour, light.getColour());
	}

	public void loadAmbient(Ambient ambient) {
		super.loadFloat(location_ambient, ambient.brightness);
	}

	public void loadSkyColour(float red, float green, float blue) {
		super.loadVector(location_skyColour, new Vector3f(red, green, blue));
	}

}
