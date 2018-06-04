package org.jcing.jcingworld.engine.io;

import org.lwjgl.glfw.GLFW;

public class KeyBoard {

	private static boolean[] key = new boolean[GLFW.GLFW_KEY_LAST];
	private static boolean[] toggled = new boolean[GLFW.GLFW_KEY_LAST];

	public static boolean key(int glfwkey) {
		return key[glfwkey];
	}

	public static boolean toggled(int glfwkey) {
		return toggled[glfwkey];
	}

	public static void press(int key2) {
		if (key2 >= 0) {
			key[key2] = true;
			toggled[key2] = !toggled[key2];
		}
	}

	public static void release(int key2) {
		key[key2] = false;
	}

}
