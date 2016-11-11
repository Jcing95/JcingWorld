package org.jcing.jcingworld.main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.io.File;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;
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
import org.jcing.jcingworld.models.OBJLoader;
import org.jcing.jcingworld.models.RawModel;
import org.jcing.jcingworld.models.TexturedModel;
import org.jcing.jcingworld.renderengine.DisplayManager;
import org.jcing.jcingworld.renderengine.MasterRenderer;
import org.jcing.jcingworld.terrain.Terrain;
import org.jcing.jcingworld.textures.ModelTexture;
import org.jcing.jcingworld.textures.TerrainTexture;
import org.jcing.jcingworld.textures.TerrainTexturePack;
import org.jcing.toolbox.MousePicker;
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

	private static Terrain terrain, terrain2;
	GUIText text;

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
		FontType font = new FontType(loader.loadTexture("fonts/halfelven", true), new File("res/fonts/halfelven.fnt"));
		text = new GUIText("WELCOME TO THE FANTASTIC WORLD OF JCING!", 1, font, new Vector2f(0.5f, 0.5f), 0.5f, false);
		text.setColour(1f, 0.25f, 0.4f);

		GL11.glEnable(GL11.GL_DEPTH_TEST);

		Light sun = new Light(new Vector3f(0, 20000, 2000), new Vector3f(1, 1, 1));
		Ambient ambient = new Ambient(0.25f);
		
		Camera cam = new Camera();
		
		

		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("tut/grassy", true));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("tut/mud", true));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("tut/grassFlowers", true));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("tut/path", true));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap", true));

		terrain = new Terrain(-0.5f, -0.5f, loader, texturePack, blendMap);
//		terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);

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

		RawModel rockobj = OBJLoader.loadObjModel("cube", loader);
		ModelTexture rocktex = new ModelTexture(loader.loadTexture("cube", true));
		// grasstex.setHasTransparency(true);
		// grasstex.setUseFakeLighting(true);
		TexturedModel rock = new TexturedModel(rockobj, rocktex);

		for (int i = 0; i < 300; i++) {
			float x = Terrain.SIZE * random.nextFloat();
			float z = -Terrain.SIZE * random.nextFloat();
			float y = terrain.getHeight(x, z);
			flora.add(new Entity(tree, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 3.5f + 3.5f * random.nextFloat()));
		}

		for (int i = 0; i < 300; i++) {
			float x = Terrain.SIZE * random.nextFloat();
			float z = -Terrain.SIZE * random.nextFloat();
			float y = terrain.getHeight(x, z);
			flora.add(new Entity(fern, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f + 0.5f * random.nextFloat()));
		}

		for (int i = 0; i < 300; i++) {
			float x = Terrain.SIZE * random.nextFloat();
			float z = -Terrain.SIZE * random.nextFloat();
			float y = terrain.getHeight(x, z);
			flora.add(new Entity(rock, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 2.0f + 1f * random.nextFloat()));
		}

		Player player = new Player(null, new Vector3f(0, 0, 5), 0, 0, 0, 1);

		List<GUIpart> guis = new ArrayList<GUIpart>();
		GUIpart gui = new GUIpart(loader.loadTexture("matrix", true), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
		guis.add(gui);

		GUIRenderer guiRenderer = new GUIRenderer(loader);

		MousePicker picker = new MousePicker(cam, renderer.getProjectionMatrix(),terrain);
		
		Entity pickTest = new Entity(new TexturedModel(OBJLoader.loadObjModel("tut/lowPolyTree", loader),new ModelTexture(loader.loadTexture("tut/lowPolyTree", true))),new Vector3f(0,1,0),0,0,0,1);
		// Test method for testing different kinds of Stuff
		test();
		DecimalFormat dec = new DecimalFormat("#.##");
		dec.setMinimumFractionDigits(2);
		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {
			manageMouse();
			picker.update();
			pickTest.setPosition(picker.getCurrentTerrainPoint());
			pickTest.increasePosition(0, -0.5f, 0);
			player.move();
			player.moveCamera(cam);
			if (KeyBoard.key(GLFW.GLFW_KEY_R)) {
				cam.reset();
			}
			text.setText("FPS: " + DisplayManager.fps + " X: " + dec.format(player.getPosition().x) + " Y: " + dec.format(player.getPosition().y)
					+ " Z: " + dec.format(player.getPosition().z));
			// renderer.processEntity(entity);

			for (Entity e : flora) {
				// bar.increaseRotation(0, spin[i], 0);
				renderer.processEntity(e);
			}
			renderer.processEntity(pickTest);
			renderer.processTerrain(terrain);
//			renderer.processTerrain(terrain2);

			renderer.render(sun, ambient, cam);
			guiRenderer.render(guis);
			TextMaster.render();
			DisplayManager.updateDisplay();
		}
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();

	}

	public static float getTerrainHeight(float x, float z) {
		if (terrain.inTerrain(x, z)) {
			return terrain.getHeight(x, z);
		}
		if (terrain2.inTerrain(x, z)) {
			return terrain2.getHeight(x, z);
		}
		return 0;
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
		Matrix4f.mul(vec, rot, vec);
		System.out.println();
		// GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
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
