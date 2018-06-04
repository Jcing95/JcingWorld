package org.jcing.jcingworld.engine;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.jcing.jcingworld.engine.io.KeyBoard;
import org.jcing.jcingworld.engine.io.Mouse;
import org.jcing.log.Log;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;

public class DisplayManager {

	public static int width = 540, height = 360;

	private static long lastFrameTime;
	private static float delta;
	private static int frames;
	private static long lastSecond;

	// The window handle
	public static long window;

	public static int fps;

	public static GLCapabilities glCapabilities;
	
	public static Log log = Log.getLog(DisplayManager.class);

	public static long init() {
		log.info("Initializing Window using LWJGL " + Version.getVersion() + "!");
		
		//TODO: init DisplayManager first.
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(Log.getErrorSteam()).set();
		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are
									 // already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden
													 // after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be
													 // resizable
													 // Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		width = vidmode.width();
		height = vidmode.height();
		log.info("set width/height: " + width + "/" + height);
		
		// Create the window
		window = glfwCreateWindow(width, height, "Hello World!", GLFW.glfwGetPrimaryMonitor(), NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {

			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true);
			if (action == GLFW.GLFW_PRESS) {
				KeyBoard.press(key);
			}
			if (action == GLFW.GLFW_RELEASE) {
				KeyBoard.release(key);
			}
			// System.log.info("win: " +window + " key: " + key + " scancode:
			// " + scancode + " action: " + action + " mods: " + mods);
		});

		GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if (action == GLFW.GLFW_PRESS) {
				Mouse.button[button] = true;
			}
			if (action == GLFW.GLFW_RELEASE) {
				Mouse.button[button] = false;
			}
		});

		GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

		GLFW.glfwSetWindowSizeCallback(window, (window, width, height) -> {
			DisplayManager.width = width;
			DisplayManager.height = height;
		});
		
		log.info("Callbacks set!");
		
		
		// Center our window
		glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		// Enable v-sync
		//TODO: V-SYNC --> Options
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		lastFrameTime = getCurrentTime();

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		glCapabilities = GL.createCapabilities();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		log.info("Window succesfully created!");
		
		return window;
	}

	public static void updateDisplay() {
		glfwSwapBuffers(window); // swap the color buffers
		// Poll for window events. The key callback above will only be
		// invoked during this call.
		long currentFrameTime = getCurrentTime();
		if (currentFrameTime - lastSecond >= 1000) {
			fps = frames;
			frames = 0;
			lastSecond = currentFrameTime;
			// System.log.info("FPS: " + fps);
		}
		frames++;
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
		glfwPollEvents();
	}

	public static long getLastFrameTime() {
		return lastFrameTime;
	}

	public static float getFrameTimeSeconds() {
		return delta;
	}

	private static long getCurrentTime() {
		return GLFW.glfwGetTimerValue() * 1000 / GLFW.glfwGetTimerFrequency();
	}

}