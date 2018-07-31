package de.jcing.jcingworld.engine.entities;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
	Vector3f position = new Vector3f();
	float yaw, pitch, roll;

	public void reset() {
		position = new Vector3f();
		yaw = 0;
		pitch = 0;
		roll = 0;
	}

	public void setPosition(Vector3f pos) {
		position = pos;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public Vector3f getPosition() {
		return position;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public float getRoll() {
		return roll;
	}

}
