package org.jcing.jcingworld.engine.shading.terrain;

import org.jcing.jcingworld.engine.entities.Camera;
import org.jcing.jcingworld.engine.lighting.Ambient;
import org.jcing.jcingworld.engine.lighting.Light;
import org.jcing.jcingworld.engine.shading.ShaderProgram;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
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
	private int location_textureAtlas;
	private int location_blendMap;
	private int location_numTextures;
	private int location_textureIndices;
	private int location_selectedVec;
	private int location_selectedOverlay;

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
		location_textureAtlas = super.getUniformLocation("textureAtlas");
		location_blendMap = super.getUniformLocation("blendMap");
		location_numTextures = super.getUniformLocation("numTextures");
		location_textureIndices = super.getUniformLocation("textureIndices");
		location_selectedVec = super.getUniformLocation("selected");
		location_selectedOverlay = super.getUniformLocation("selectedOverlay");
	}

	public void connectTextureUnits() {
		super.loadInt(location_blendMap, 0);
		super.loadInt(location_textureAtlas, 1);
		super.loadInt(location_selectedOverlay, 2);
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

	public void loadTerrainData(float[] textureIndices, float numTextures, Vector2f selected) {
		super.loadFloatArray(location_textureIndices, textureIndices);
		super.loadFloat(location_numTextures, numTextures);
		super.load2DVector(location_selectedVec, selected);
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
