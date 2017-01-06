package org.jcing.jcingworld.game;

import java.util.LinkedList;
import java.util.List;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.OBJLoader;
import org.jcing.jcingworld.engine.entities.Camera;
import org.jcing.jcingworld.engine.entities.Entity;
import org.jcing.jcingworld.engine.entities.Player;
import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.entities.models.TexturedModel;
import org.jcing.jcingworld.engine.imagery.ModelTexture;
import org.jcing.jcingworld.engine.io.KeyBoard;
import org.jcing.jcingworld.engine.io.Mouse;
import org.jcing.jcingworld.engine.lighting.Ambient;
import org.jcing.jcingworld.engine.lighting.Light;
import org.jcing.jcingworld.engine.rendering.MasterRenderer;
import org.jcing.jcingworld.engine.terrain.Chunk;
import org.jcing.jcingworld.engine.terrain.Terrain;
import org.jcing.jcingworld.toolbox.MousePicker;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Vector3f;

public class Game {
    private Light sun;
    private Ambient ambient;
    private Camera cam;

    //	private Terrain terrain[];

    private List<Entity> flora;

    private Player player;
    private MasterRenderer renderer;

    private MousePicker picker;
    private Entity pickTest;
    private Terrain terrain;

    public Game(Loader loader, MasterRenderer renderer) {
        this.renderer = renderer;

        sun = new Light(new Vector3f(0, 20000, 20000), new Vector3f(1, 1, 1));
        ambient = new Ambient(0.25f);
        cam = new Camera();

        terrain = new Terrain(loader, renderer);
        int terrSize = 20;
        for (int i = -terrSize / 2; i < terrSize / 2; i++) {
            for (int j = -terrSize / 2; j < terrSize / 2; j++) {
                terrain.addTerain(i, j);
                ;
            }
        }

        RawModel stemobj = OBJLoader.loadObjModel("stem.obj", loader);
        ModelTexture stemtex = new ModelTexture(loader.loadTexture("stem.png", true));
//        stemtex.useFakeLighting(true);
        TexturedModel stem = new TexturedModel(stemobj, stemtex);
        flora = new LinkedList<Entity>();
        
        

        player = new Player(null, new Vector3f(0, 0, 0), 0, 90, 0, 1, terrain);

        picker = new MousePicker(cam, renderer.getProjectionMatrix(), terrain);

        pickTest = new Entity(
                new TexturedModel(OBJLoader.loadObjModel("circle.obj", loader),
                        new ModelTexture(loader.loadTexture("red.png", true))),
                new Vector3f(0, 0, 0), 0, 0, 0, 2);
//        for (int i = 0; i < 500; i++) {
//            float x = (float) (Math.random()*10f)*Chunk.SIZE;
//            float y = (float) (Math.random()*10f)*Chunk.SIZE;
//            flora.add(new Entity(stem, new Vector3f(x,terrain.getHeightAt(x, y),y), 0, 0, 0, 1));
//        }   
    }

    public void tick() {
        if (KeyBoard.key(GLFW.GLFW_KEY_R)) {
            player.reset();
        }
        terrain.makeRandom();
        picker.update();
        pickTest.setPosition(picker.getCurrentTerrainPoint());
        pickTest.increasePosition(0, 0.1f, 0);
        player.move();
        player.moveCamera(cam);

        
        terrain.updatePlayerPos(player);
        terrain.processActives();
        if (Mouse.button[GLFW.GLFW_MOUSE_BUTTON_LEFT])
            renderer.processEntity(pickTest);
        for (Entity entity : flora) {
            renderer.processEntity(entity);
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

    public Chunk getTerrain() {
        return terrain.getChunk(0, 0);
    }

    public Player getPlayer() {
        return player;
    }

    public Entity getPickTest() {
        return pickTest;
    }
}
