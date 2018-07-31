package de.jcing.jcingworld.game;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

import de.jcing.jcingworld.engine.GraphicsLoader;
import de.jcing.jcingworld.engine.entities.Camera;
import de.jcing.jcingworld.engine.entities.Player;
import de.jcing.jcingworld.engine.io.KeyBoard;
import de.jcing.jcingworld.engine.lighting.Light;
import de.jcing.jcingworld.engine.rendering.MasterRenderer;
import de.jcing.jcingworld.terrain.Terrain;
import de.jcing.jcingworld.toolbox.MousePicker;

public class Game {

	private Light sun;
	private Camera cam;

	private Player player;

	private MousePicker picker;
	private Terrain terrain;

	//////////////////////////////////////////////////////////////////////////////////////////////
	public static String saveGameName = "InDevTest";
	//////////////////////////////////////////////////////////////////////////////////////////////

	public Game(GraphicsLoader loader, MasterRenderer renderer) {
		//TODO: access loader and Masterrenderer in a static way!
		
		sun = new Light(new Vector3f(0, 20000, 20000), new Vector3f(1, 1, 1), 0.25f);
		cam = new Camera();

		terrain = new Terrain(loader, renderer);
		terrain.initPosition(0, 0);

		player = new Player(null, new Vector3f(0, 0, 0), 0, 90, 0, 1, terrain);

		picker = new MousePicker(cam, renderer.getProjectionMatrix(), terrain);
	}

	public Terrain getTerrain(int i) {
		return terrain;
	}

	public void tick() {
		// TODO: Wrap Keyboard
		if (KeyBoard.key(GLFW.GLFW_KEY_R)) {
			player.reset();
		}

		picker.update();
		player.move();
		player.moveCamera(cam);

		terrain.updatePlayerPos(player);
		terrain.select(picker.getCurrentTerrainPoint());
		terrain.processActives();
	}

	public Light getSun() {
		return sun;
	}

	public Camera getCam() {
		return cam;
	}

	// public Chunk getTerrain() {
	// return terrain.getChunk(0, 0);
	// }

	public Player getPlayer() {
		return player;
	}

	public void cleanUp() {
		
	}
}
