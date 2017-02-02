package org.jcing.jcingworld.engine.lighting;

import org.lwjgl.util.vector.Vector3f;

public class Ambient {

    public float brightness;
    public Vector3f color;

    public Ambient(float brightness) {
        this.brightness = brightness;
    }

}
