package org.jcing.jcingworld.shading;

import org.jcing.jcingworld.entities.Ambient;
import org.jcing.jcingworld.entities.Camera;
import org.jcing.jcingworld.entities.Light;
import org.jcing.toolbox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class TerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/org/jcing/jcingworld/shading/TerrainVertexShader";
	private static final String FRAGMENT_FILE = "src/org/jcing/jcingworld/shading/TerrainFragmentShader";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColour;
	private int location_ambient;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_skyColour;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;

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
		location_lightColour = super.getUniformLocation("lightColour");
		location_ambient = super.getUniformLocation("ambient");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_skyColour = super.getUniformLocation("skyColour");
		location_backgroundTexture = super.getUniformLocation("backgroundTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("bTexture");
		location_blendMap = super.getUniformLocation("blendMap");
	}

	public void connectTextureUnits() {
		super.loadInt(location_backgroundTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
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
