package org.jcing.jcingworld.engine.terrain;

import org.lwjgl.util.vector.Vector3f;

public class Tile {

	private float[] x;
	private float[] y;
	private float[] z;

	private Vector3f normal;
	private boolean swapTriangles; // false wenn oben/rechts nicht tiefste
	int textureID;
	int indexX, indexY;

	public Tile(float[] x, float[] y, float[] z, int indexX, int indexY) {
		this.x = new float[4];
		this.y = new float[4];
		this.z = new float[4];
		for (int i = 0; i < 4; i++) {
			this.x[i] = x[i];
			this.y[i] = y[i];
			this.z[i] = z[i];
		}
		this.indexX = indexX;
		this.indexY = indexY;
		calcSwap();
		calcNormal();
	}

	private void calcNormal() {
		// TODO: implement
	}

	private void calcSwap() {
		// TODO: implement
	}

	public float[] getX() {
		return x;
	}

	public float[] getY() {
		return y;
	}

	public float[] getZ() {
		return z;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public boolean isSwapTriangles() {
		return swapTriangles;
	}

	public int getIndexX() {
		return indexX;
	}

	public int getIndexY() {
		return indexY;
	}
}
