package de.jcing.jcingworld.terrain;

import org.lwjgl.util.vector.Vector3f;

import de.jcing.jcingworld.terrain.generation.MapGenerator;

public class Tile {

	/**
	 * 
	 */

	public static final int SIZE = 6;

	private float[] xV;
	private float[] yV;
	private float[] zV;
	private Vector3f[] normal;

	// private Vector3f normal;
	// private boolean swapTriangles; // false wenn oben/rechts nicht tiefste
	public int textureIndex;

	public Tile(int x, int z) {
		this.xV = new float[4];
		this.yV = new float[4];
		this.zV = new float[4];
		normal = new Vector3f[4];
	}

	public void generate(Chunk data, MapGenerator gen, int x, int z) {
		float SQUARE_SIZE = Chunk.TILE_SIZE / 2;
		int xc = (int) data.getPhysicalX();
		int zc = (int) data.getPhysicalZ();

		float xa[] = { x * SQUARE_SIZE, (x + 1) * SQUARE_SIZE, x * SQUARE_SIZE, (x + 1) * SQUARE_SIZE };
		float za[] = { z * SQUARE_SIZE, z * SQUARE_SIZE, (z + 1) * SQUARE_SIZE, (z + 1) * SQUARE_SIZE };
		float ya[] = { gen.height(xa[0] + xc, za[0] + zc), gen.height(xa[1] + xc, za[1] + zc), gen.height(xa[2] + xc, za[2] + zc),
				gen.height(xa[3] + xc, za[3] + zc) };
		
		textureIndex = gen.tex(xa[0] + xc, za[0] + zc);
		
		for (int i = 0; i < 4; i++) {
			this.xV[i] = xa[i];
			this.yV[i] = ya[i];
			this.zV[i] = za[i];
		}
		calcSwap();
		calcNormal();
		// return new Tile(xa, ya, za, gen.tex(xa[3] + xc, za[3] + zc));
	}

	private Vector3f calcNormal() {
		Vector3f left = Vector3f.sub(new Vector3f(xV[1], yV[1], zV[1]), new Vector3f(xV[0], yV[0], zV[0]), null);
		Vector3f right = Vector3f.sub(new Vector3f(xV[0], yV[0], zV[0]), new Vector3f(xV[2], yV[2], zV[2]), null);
		normal[0] = Vector3f.cross(left, right, null);

		left = Vector3f.sub(new Vector3f(xV[1], yV[1], zV[1]), new Vector3f(xV[2], yV[2], zV[2]), null);
		right = Vector3f.sub(new Vector3f(xV[0], yV[0], zV[0]), new Vector3f(xV[1], yV[1], zV[1]), null);
		normal[1] = Vector3f.cross(left, right, null);
		left = Vector3f.sub(new Vector3f(xV[1], yV[1], zV[1]), new Vector3f(xV[2], yV[2], zV[2]), null);
		right = Vector3f.sub(new Vector3f(xV[2], yV[2], zV[2]), new Vector3f(xV[3], yV[3], zV[3]), null);
		normal[2] = Vector3f.cross(left, right, null);
		left = Vector3f.sub(new Vector3f(xV[1], yV[1], zV[1]), new Vector3f(xV[3], yV[3], zV[3]), null);
		right = Vector3f.sub(new Vector3f(xV[2], yV[2], zV[2]), new Vector3f(xV[3], yV[3], zV[3]), null);
		normal[3] = Vector3f.cross(left, right, null);
		// System.out.println(normal[0]);
		return null;
	}

	private void calcSwap() {
		// TODO: TileCalcSwap
	}

	public float[] getX() {
		return xV;
	}

	public float[] getY() {
		return yV;
	}

	public float[] getZ() {
		return zV;
	}

	public Vector3f[] getNormal() {
		return normal;
	}

	// public boolean isSwapTriangles() {
	// return swapTriangles;
	// }
}
