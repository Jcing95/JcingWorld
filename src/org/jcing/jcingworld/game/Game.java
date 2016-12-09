package org.jcing.jcingworld.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.OBJLoader;
import org.jcing.jcingworld.engine.entities.Camera;
import org.jcing.jcingworld.engine.entities.Entity;
import org.jcing.jcingworld.engine.entities.Player;
import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.entities.models.TexturedModel;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.engine.imagery.ModelTexture;
import org.jcing.jcingworld.engine.imagery.TextureAtlas;
import org.jcing.jcingworld.engine.io.KeyBoard;
import org.jcing.jcingworld.engine.io.Mouse;
import org.jcing.jcingworld.engine.lighting.Ambient;
import org.jcing.jcingworld.engine.lighting.Light;
import org.jcing.jcingworld.engine.rendering.MasterRenderer;
import org.jcing.jcingworld.engine.terrain.Terrain;
import org.jcing.jcingworld.toolbox.MousePicker;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

public class Game {
	private Light sun;
	private Ambient ambient;
	private Camera cam;

	private Terrain terrain[];

	private List<Entity> flora;

	private Player player;
	private MasterRenderer renderer;

	private MousePicker picker;
	private Entity pickTest;

	public Game(Loader loader, MasterRenderer renderer) {
		this.renderer = renderer;

		sun = new Light(new Vector3f(0, 20000, 20000), new Vector3f(1, 1, 1));
		ambient = new Ambient(0.25f);
		cam = new Camera();

//		BaseImage backgroundTexture = (loader.loadTexture("minecraft/grass_top.png", false));
//		BaseImage rTexture = loader.loadTexture("colors/blue.png", false);
//		BaseImage gTexture = loader.loadTexture("colors/green.png", false);
//		BaseImage bTexture = loader.loadTexture("colors/blue.png", false);
//		BaseImage blackTexture = loader.loadTexture("colors/green.png", false);
//		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture, blackTexture);

		BaseImage blendMap = loader.loadTexture("terrain/blendmapGood.png", false);
		BaseImage texMap = loader.loadTexture("terrain/testMap.png", false);
		TextureAtlas atlas = new TextureAtlas(texMap,16);
		
		terrain = new Terrain[9];
		for(int i=0;i<terrain.length;i++){
		    terrain[i] = new Terrain(i, 0f, loader, renderer.getTerrainShader(), atlas, blendMap);
		}
		

		flora = new ArrayList<Entity>();

		RawModel stemobj = OBJLoader.loadObjModel("stem.obj", loader);
		ModelTexture stemtex = new ModelTexture(loader.loadTexture("stem.png", true));
		stemtex.useFakeLighting(true);
		TexturedModel stem = new TexturedModel(stemobj, stemtex);

		RawModel rockobj = OBJLoader.loadObjModel("rock.obj", loader);
		ModelTexture rocktex = new ModelTexture(loader.loadTexture("rock.png", true));
		TexturedModel rock = new TexturedModel(rockobj, rocktex);

		int entitynr = 0 / 3;
		Random random = new Random();
		for (int i = 0; i < entitynr; i++) {
			float x = terrain[0].getX() + Terrain.SIZE * random.nextFloat();
			float z = terrain[0].getZ() + Terrain.SIZE * random.nextFloat();
			float y = terrain[0].getHeight(x, z);
			flora.add(new Entity(stem, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1.5f + 1.5f * random.nextFloat()));
		}

		for (int i = 0; i < entitynr; i++) {
			float x = terrain[0].getX() + Terrain.SIZE * random.nextFloat();
			float z = terrain[0].getZ() + Terrain.SIZE * random.nextFloat();
			float y = terrain[0].getHeight(x, z);
			flora.add(new Entity(rock, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 2.0f + 5f * random.nextFloat()));
		}

		player = new Player(null, new Vector3f(0, 0, 0), 0, 90, 0, 1);

		picker = new MousePicker(cam, renderer.getProjectionMatrix(), terrain[0]);

		pickTest = new Entity(new TexturedModel(OBJLoader.loadObjModel("circle.obj", loader), new ModelTexture(loader.loadTexture("red.png", true))),
				new Vector3f(0, 0, 0), 0, 0, 0, 2);
	}

	public float getTerrainHeight(float x, float z) {
		if (terrain[0].inTerrain(x, z)) {
			return terrain[0].getHeight(x, z);
		}
		return 0;
	}

	public void tick() {
		if (KeyBoard.key(GLFW.GLFW_KEY_R)) {
			player.reset();
		}
		terrain[0].getTextureIndices()[0] ++;
		terrain[0].getTextureIndices()[0] %=9;
		picker.update();
		pickTest.setPosition(picker.getCurrentTerrainPoint());
		pickTest.increasePosition(0, 0.1f, 0);
		player.move();
		player.moveCamera(cam);
		for (Entity e : flora) {
			renderer.processEntity(e);
		}
		if (Mouse.button[GLFW.GLFW_MOUSE_BUTTON_LEFT])
			renderer.processEntity(pickTest);
		for (int i = 0; i < terrain.length; i++) {
            renderer.processTerrain(terrain[i]);
        }

	}

	public Light getSun() {
		return sun;
	}

	public Ambient getAmbient() {
		return ambient;
	}

	public Camera getCam() {
		return cam;
	}

	public Terrain getTerrain() {
		return terrain[0];
	}

	public Player getPlayer() {
		return player;
	}

	public Entity getPickTest() {
		return pickTest;
	}
}
