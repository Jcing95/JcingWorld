package de.jcing.jcingworld.engine.lighting;

import org.lwjgl.util.vector.Vector3f;

public class Light {
	private Vector3f position;
	private Vector3f colour;
	private float ambient;

	public Light(Vector3f position, Vector3f colour, float ambient) {
		super();
		this.position = position;
		this.colour = colour;
		this.ambient = ambient;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getColour() {
		return colour;
	}

	public float getAmbient() {
		return ambient;
	}

	public void setAmbient(float ambient) {
		this.ambient = ambient;
	}

}
