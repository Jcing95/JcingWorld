package org.jcing.jcingworld.main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.io.File;
import java.nio.DoubleBuffer;
import java.text.DecimalFormat;

import org.jcing.jcingworld.engine.DisplayManager;
import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.font.FontType;
import org.jcing.jcingworld.engine.font.GUIText;
import org.jcing.jcingworld.engine.io.Mouse;
import org.jcing.jcingworld.engine.rendering.GUIRenderer;
import org.jcing.jcingworld.engine.rendering.MasterRenderer;
import org.jcing.jcingworld.engine.rendering.TextMaster;
import org.jcing.jcingworld.game.Game;
import org.jcing.jcingworld.game.GuiManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class MainLoop {

    private static double newMouseX;
    private static double newMouseY;
    private static double prevMouseX;
    private static double prevMouseY;

    private static long window;
    private static Game game;
    private static Loader loader;
    private static MasterRenderer renderer;
    private static GUIRenderer guiRenderer;
    private static GuiManager gui;

    private static GUIText fpsText;

    private static final boolean WIRE = false;

    public static void main(String[] args) {   
        new MainLoop();
    }

    public MainLoop() {
        try {
            System.setProperty("org.lwjgl.librarypath",
                    new File("lib/lwjgl3/native").getAbsolutePath());
            
            loader = new Loader();
            window = DisplayManager.init();
            
            renderer = new MasterRenderer();
            
            guiRenderer = new GUIRenderer(loader); //TODO: overlook GUI and Font
            gui = new GuiManager();
            
            game = new Game(loader, renderer);

            TextMaster.init(loader);
            
            MasterRenderer.enableCulling();
            
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
        //Initializing FPS - text
        DecimalFormat dec = new DecimalFormat("#.##");
        dec.setMinimumFractionDigits(2);
        FontType font = new FontType(loader.loadTexture("fonts/immortal.png", true),
                new File("res/fonts/immortal.fnt"));
        fpsText = new GUIText("FPS: ", 1, font, new Vector2f(0.1f, 0.1f), 0.3f, false);
        fpsText.setColour(1f, 0.25f, 0.4f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            manageMouse(); //update mouse position
            
            game.tick(); //update game TODO: time based game ticks.
            
            //update debugging text
            fpsText.setText("FPS: " + DisplayManager.fps + " X: "
                    + dec.format(game.getPlayer().getPosition().x) + " Y: "
                    + dec.format(game.getPlayer().getPosition().y) + " Z: "
                    + dec.format(game.getPlayer().getPosition().z));

            //render game
            renderer.render(game.getSun(), game.getCam());

            //render GUIs
            guiRenderer.render(gui.getParts());
            
            //render text
            if (WIRE)
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
            TextMaster.render();
            if (WIRE)
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);

            //draw
            DisplayManager.updateDisplay();
        }
        
        game.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
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

        Mouse.deltaY = newMouseX - prevMouseX;
        Mouse.deltaX = newMouseY - prevMouseY;

        prevMouseX = newMouseX;
        prevMouseY = newMouseY;
    }

    public static Game getGame() {
        return game;
    }

    public static Loader getLoader() {
        return loader;
    }

    public static MasterRenderer getRenderer() {
        return renderer;
    }

    public static GUIRenderer getGuiRenderer() {
        return guiRenderer;
    }
}
