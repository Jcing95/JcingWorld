package org.jcing.jcingworld.engine.rendering;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jcing.jcingworld.engine.DisplayManager;
import org.jcing.jcingworld.engine.entities.Camera;
import org.jcing.jcingworld.engine.entities.Entity;
import org.jcing.jcingworld.engine.entities.models.TexturedModel;
import org.jcing.jcingworld.engine.lighting.Light;
import org.jcing.jcingworld.engine.shading.entities.StaticShader;
import org.jcing.jcingworld.engine.shading.terrain.TerrainShader;
import org.jcing.jcingworld.terrain.Chunk;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class MasterRenderer {

	private static final float FOV = 100;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 1000;

	private static final float RED = Maths.getFloatColor(70);
	private static final float GREEN = Maths.getFloatColor(171);
	private static final float BLUE = Maths.getFloatColor(200);

	private Matrix4f projectionMatrix;

	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;

	private TerrainRenderer terrainRenderer;
	private TerrainShader terrainShader = new TerrainShader();

	private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
	private List<Chunk> terrains = new LinkedList<Chunk>();

	public MasterRenderer() {
		createProjectionMatrix();
		renderer = new EntityRenderer(shader, projectionMatrix);
		terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);

	}

	public void render(Light sun, Camera camera) {
		prepare();
		shader.start();
		shader.loadSkyColour(RED, GREEN, BLUE);
		shader.loadLight(sun);
//		shader.loadAmbient(sun.getAmbient());
		shader.loadViewMatrix(camera);
		renderer.render(entities);

		shader.stop();
		terrainShader.start();
		terrainShader.loadSkyColour(RED, GREEN, BLUE);
		terrainShader.loadLight(sun);
//		terrainShader.loadAmbient(sun);
		terrainShader.loadViewMatrix(camera);
		terrainRenderer.render(terrains);
		terrainShader.stop();
		terrains.clear();
		entities.clear();
	}

	public void processTerrain(Chunk terrain) {
		if (terrain != null)
			terrains.add(terrain);
		else
			System.err.println("TerrainRenderer received NULL-Terrain!");
	}

	public void prepare() {
		GL11.glClearColor(RED, GREEN, BLUE, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void processEntity(Entity entity) {
		TexturedModel entityModel = entity.getModel();
		List<Entity> batch = entities.get(entityModel);
		if (batch != null) {
			batch.add(entity);
		} else {
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(entityModel, newBatch);
		}
	}

	public void cleanUp() {
		shader.cleanUp();
		terrainShader.cleanUp();
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public TerrainShader getTerrainShader() {
		return terrainShader;
	}

	private void createProjectionMatrix() {
		// IntBuffer w = BufferUtils.createIntBuffer(1);
		// IntBuffer h = BufferUtils.createIntBuffer(1);
		// GLFW.glfwGetWindowFrameSize(DisplayManager.window, w, h);
		int width = DisplayManager.width;
		int height = DisplayManager.height;

		float aspectRatio = (float) width / (float) height;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = (float) y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}

}
