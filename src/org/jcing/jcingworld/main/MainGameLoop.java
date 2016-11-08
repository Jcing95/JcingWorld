package org.jcing.jcingworld.main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.io.File;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jcing.jcingworld.entities.Ambient;
import org.jcing.jcingworld.entities.Camera;
import org.jcing.jcingworld.entities.Entity;
import org.jcing.jcingworld.entities.Light;
import org.jcing.jcingworld.entities.Player;
import org.jcing.jcingworld.font.FontType;
import org.jcing.jcingworld.font.GUIText;
import org.jcing.jcingworld.font.rendering.TextMaster;
import org.jcing.jcingworld.gui.GUIRenderer;
import org.jcing.jcingworld.gui.GUIpart;
import org.jcing.jcingworld.io.KeyBoard;
import org.jcing.jcingworld.io.Mouse;
import org.jcing.jcingworld.models.RawModel;
import org.jcing.jcingworld.models.TexturedModel;
import org.jcing.jcingworld.renderengine.DisplayManager;
import org.jcing.jcingworld.renderengine.MasterRenderer;
import org.jcing.jcingworld.renderengine.OBJLoader;
import org.jcing.jcingworld.terrain.Terrain;
import org.jcing.jcingworld.textures.ModelTexture;
import org.jcing.jcingworld.textures.TerrainTexture;
import org.jcing.jcingworld.textures.TerrainTexturePack;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class MainGameLoop {

	private long window;

	private double newMouseX;

	private double newMouseY;

	private double prevMouseX;

	private double prevMouseY;

	public static void main(String[] args) {
		new MainGameLoop();
	}

	public MainGameLoop() {
		try {
			window = DisplayManager.init();
			loop();

			// Free the window callbacks and destroy the window
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
		} finally {
			// Terminate GLFW and free the error callback
			glfwTerminate();
			glfwSetErrorCallback(null).free();
		}
	}

	private void loop() {

		Loader loader = new Loader();
		TextMaster.init(loader);
		FontType font = new FontType(loader.loadTexture("fonts/halfelven",true), new File("res/fonts/halfelven.fnt"));
		GUIText text = new GUIText("WELCOME TO THE FANTASTIC WORLD OF JCING!",1,font, new Vector2f(0.5f,0.5f),0.5f,false);
		text.setColour(0.3f, 0.75f, 0.4f);
		
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		Light sun = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));
		Ambient ambient = new Ambient(0.125f);
		Camera cam = new Camera();

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("tut/grassy", true));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("tut/mud", true));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("tut/grassFlowers", true));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("tut/path", true));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("tut/blendMap", true));

		Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
		Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);

		ModelTexture pyramid = new ModelTexture(loader.loadTexture("pyramid", true));

		pyramid.setShineDamper(10);
		pyramid.setReflectivity(1);
		// pyramid.setUseFakeLighting(true);

		MasterRenderer renderer = new MasterRenderer();

		Random random = new Random();
		ArrayList<Entity> flora = new ArrayList<Entity>();

		RawModel treeobj = OBJLoader.loadObjModel("tut/tree", loader);
		ModelTexture treetex = new ModelTexture(loader.loadTexture("tut/tree", true));
		TexturedModel tree = new TexturedModel(treeobj, treetex);

		RawModel fernobj = OBJLoader.loadObjModel("tut/fern", loader);
		ModelTexture ferntex = new ModelTexture(loader.loadTexture("tut/fern", true));
		ferntex.setHasTransparency(true);
		ferntex.setUseFakeLighting(true);
		TexturedModel fern = new TexturedModel(fernobj, ferntex);

		RawModel grassobj = OBJLoader.loadObjModel("tut/grass", loader);
		ModelTexture grasstex = new ModelTexture(loader.loadTexture("tut/grass", true));
		grasstex.setHasTransparency(true);
		grasstex.setUseFakeLighting(true);
		TexturedModel grass = new TexturedModel(grassobj, grasstex);

		RawModel lowtreeobj = OBJLoader.loadObjModel("tut/lowPolyTree", loader);
		ModelTexture lowtreetex = new ModelTexture(loader.loadTexture("tut/lowPolyTree", true));
		TexturedModel lowtree = new TexturedModel(lowtreeobj, lowtreetex);

		for (int i = 0; i < 4000; i++) {
			Entity entity;
			switch (random.nextInt(10)) {
			case 0:
				entity = new Entity(tree, new Vector3f(1600 * random.nextFloat() - 800, 0, -800 * random.nextFloat()), 0, random.nextFloat() * 360, 0,
						3.5f + 3.5f * random.nextFloat());
				break;
			case 1:
				entity = new Entity(lowtree, new Vector3f(1600 * random.nextFloat() - 800, 0, -800 * random.nextFloat()), 0, random.nextFloat() * 360,
						0, 0.5f + 0.5f * random.nextFloat());
				break;
			case 2:
			case 3:
			case 4:
				entity = new Entity(fern, new Vector3f(1600 * random.nextFloat() - 800, 0, -800 * random.nextFloat()), 0, random.nextFloat() * 360, 0,
						0.5f + 0.5f * random.nextFloat());
				break;
			default:
				entity = new Entity(grass, new Vector3f(1600 * random.nextFloat() - 800, 0, -800 * random.nextFloat()), 0, random.nextFloat() * 360,
						0, 2.0f + 1f * random.nextFloat());

			}
			flora.add(entity);
		}
		// Entity entity = new Entity(texturedModel, new Vector3f(0, 0.0f, -3f),
		// 0f, 0, 0, 1);

		Player player = new Player(null, new Vector3f(0, 0, 5), 0, 0, 0, 1);

		List<GUIpart> guis = new ArrayList<GUIpart>();
		GUIpart gui = new GUIpart(loader.loadTexture("matrix",true),new Vector2f(0.5f,0.5f), new Vector2f(0.25f,0.25f));
		guis.add(gui);
		
		GUIRenderer guiRenderer = new GUIRenderer(loader);
		
		// Test method for testing different kinds of Stuff
		test();
		
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {
			manageMouse();
			player.move();
			player.moveCamera(cam);
			if (KeyBoard.key(GLFW.GLFW_KEY_R)) {
				cam.reset();
			}

			// renderer.processEntity(entity);

			for (Entity e : flora) {
				// bar.increaseRotation(0, spin[i], 0);
				renderer.processEntity(e);
			}

			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);

			renderer.render(sun, ambient, cam);
			guiRenderer.render(guis);
			TextMaster.render();
			DisplayManager.updateDisplay();
		}
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		
	}

	private void test() {
		Matrix4f rot = new Matrix4f();
		Matrix4f vec = new Matrix4f();
		vec.m00 = 1;
		vec.m01 = 1;
		vec.m02 = 1;
		vec.m03 = 1;
		rot.setIdentity();

		rot.rotate((float) Math.toRadians(30), new Vector3f(0, 1, 0));
		vec.mul(vec, rot, vec);
		System.out.println();
	}

	private void manageMouse() {
		DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer y = BufferUtils.createDoubleBuffer(1);

		GLFW.glfwGetCursorPos(DisplayManager.window, x, y);
		x.rewind();
		y.rewind();

		newMouseX = x.get();
		newMouseY = y.get();

		Mouse.posX = newMouseX;
		Mouse.posY = newMouseY;

		double deltaX = newMouseX - prevMouseX;
		double deltaY = newMouseY - prevMouseY;

		Mouse.deltaY = deltaY;
		Mouse.deltaX = deltaX;

		prevMouseX = newMouseX;
		prevMouseY = newMouseY;

	}

}
