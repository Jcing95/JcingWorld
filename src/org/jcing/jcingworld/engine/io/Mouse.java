package org.jcing.jcingworld.engine.io;

import org.lwjgl.glfw.GLFW;

public class Mouse {

    public static boolean button[] = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    public static double deltaY, deltaX;

    public static double posX, posY;

}
