package org.jcing.geometry;

import org.lwjgl.util.vector.Vector3f;

public class Triangle {

	private Vector3f p1,p2,p3;

	public Triangle(Vector3f p1, Vector3f p2, Vector3f p3) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	public Vector3f getP1() {
		return p1;
	}

	public void setP1(Vector3f p1) {
		this.p1 = p1;
	}

	public Vector3f getP2() {
		return p2;
	}

	public void setP2(Vector3f p2) {
		this.p2 = p2;
	}

	public Vector3f getP3() {
		return p3;
	}

	public void setP3(Vector3f p3) {
		this.p3 = p3;
	}
	
	
	
}
