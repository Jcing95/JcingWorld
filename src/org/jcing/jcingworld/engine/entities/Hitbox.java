package org.jcing.jcingworld.engine.entities;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

@SuppressWarnings(value = { "unused" })
public class Hitbox {

	// TODO: implement Hitboxes
	public Vector3f diagonal;
	public Vector3f offset;
	public Vector3f rotation;
	private Vector3f entitypos;

	/**
	 * @return the position of the Hitbox relative to Entity position.
	 */
	public Vector3f getPosition(Vector3f relativeTo) {
		Vector3f pos = new Vector3f(offset.x, offset.y, offset.z);
		pos.x += relativeTo.x;
		pos.y += relativeTo.y;
		pos.z += relativeTo.z;
		return pos;
	}

	/**
	 * @return the width at own x-Axis.
	 */
	public float getWidth() {
		return diagonal.x;
	}

	/**
	 * @return the height at own y-Axis.
	 */
	public float getHeight() {
		return diagonal.y;
	}

	/**
	 * @return the depth at own z-Axis.
	 */
	public float getDepth() {
		return diagonal.z;
	}

	// private void getRight(Vector3f relativeTo){
	// Vector3f pos = getPosition(relativeTo);
	// return pos.x + diagonal.x;
	// }
	//
	public boolean contains(Vector3f point, Vector3f relativeTo) {
		Vector3f pos = getPosition(relativeTo);
		Vector3f relativePoint = new Vector3f(point.x - pos.x, point.y - pos.y, point.z - pos.z);
		Vector4f rotated;

		return false;
	}
}
