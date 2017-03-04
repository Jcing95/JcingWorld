package org.jcing.jcingworld.game;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.Camera;
import org.jcing.jcingworld.engine.entities.Player;
import org.jcing.jcingworld.engine.io.KeyBoard;
import org.jcing.jcingworld.engine.lighting.Light;
import org.jcing.jcingworld.engine.rendering.MasterRenderer;
import org.jcing.jcingworld.terrain.Chunk;
import org.jcing.jcingworld.terrain.Terrain;
import org.jcing.jcingworld.toolbox.MousePicker;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

public class Game {

    private Light sun;
    private Camera cam;

    private Player player;

    private MousePicker picker;
    private Terrain terrain;

    //////////////////////////////////////////////////////////////////////////////////////////////
    public static String saveGameName = "InDevTest";
    //////////////////////////////////////////////////////////////////////////////////////////////

    public Game(Loader loader, MasterRenderer renderer) {
        sun = new Light(new Vector3f(0, 20000, 20000), new Vector3f(1, 1, 1), 0.25f);
        cam = new Camera();

        terrain = new Terrain(loader, renderer);
        terrain.initPosition(0, 0);

        player = new Player(null, new Vector3f(0, 0, 0), 0, 90, 0, 1, terrain);

        picker = new MousePicker(cam, renderer.getProjectionMatrix(), terrain);
    }

    
    
    public void tick() {
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

    public Chunk getTerrain() {
        return terrain.getChunk(0, 0);
    }

    public Player getPlayer() {
        return player;
    }

    public void cleanUp() {
        terrain.finish();
    }
}
