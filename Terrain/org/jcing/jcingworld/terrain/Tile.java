package org.jcing.jcingworld.terrain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jcing.jcingworld.terrain.generation.MapGenerator;
import org.lwjgl.util.vector.Vector3f;

public class Tile implements Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6529445147414158037L;

	private static final byte TILEVERSION = 1;

	public static final int SIZE = 6;

	private int x, y;
	private float yDelta;
	private Chunk chunk;

	private float[] xV;
	private float[] yV;
	private float[] zV;
	private Vector3f[] normal;

	// private Vector3f normal;
	// private boolean swapTriangles; // false wenn oben/rechts nicht tiefste
	public int textureIndex;

	public Tile(float[] x, float[] y, float[] z, int textureIndex) {
		this.xV = new float[4];
		this.yV = new float[4];
		this.zV = new float[4];
		normal = new Vector3f[4];
		for (int i = 0; i < 4; i++) {
			this.xV[i] = x[i];
			this.yV[i] = y[i];
			this.zV[i] = z[i];
		}
		this.textureIndex = textureIndex;
		calcSwap();
		calcNormal();
	}

	public Tile(int x, int y, int textureIndex) {
		this.xV = new float[4];
		this.yV = new float[4];
		this.zV = new float[4];
		normal = new Vector3f[4];
		this.x = x;
		this.y = y;
		this.yDelta = 0;
		this.textureIndex = 2;
	}

	public Tile() {

	}

	public void generate(ChunkData data, MapGenerator gen, int x, int y) {
		this.x = x;
		this.y = y;
		float SQUARE_SIZE = Chunk.TILE_SIZE / 2;
		int xc = (int) (data.x * Chunk.SIZE);
		int zc = (int) (data.z * Chunk.SIZE);

		float xa[] = { x * SQUARE_SIZE, (x + 1) * SQUARE_SIZE, x * SQUARE_SIZE, (x + 1) * SQUARE_SIZE };
		float za[] = { y * SQUARE_SIZE, y * SQUARE_SIZE, (y + 1) * SQUARE_SIZE, (y + 1) * SQUARE_SIZE };
		float ya[] = { gen.height(xa[0] + xc, za[0] + zc), gen.height(xa[1] + xc, za[1] + zc), gen.height(xa[2] + xc, za[2] + zc),
				gen.height(xa[3] + xc, za[3] + zc) };
		if (textureIndex < 0)
			textureIndex = gen.tex(data.x * SQUARE_SIZE + x, data.z * SQUARE_SIZE + y);
		for (int i = 0; i < 4; i++) {
			this.xV[i] = xa[i];
			this.yV[i] = ya[i];
			this.zV[i] = za[i];
		}
		calcSwap();
		calcNormal();
		// return new Tile(xa, ya, za, gen.tex(xa[3] + xc, za[3] + zc));
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeByte(TILEVERSION);
		out.writeFloat(yDelta);
		out.writeInt(textureIndex);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		byte version = in.readByte();
		switch (version) {
		case 0:
			this.xV = new float[4];
			this.yV = new float[4];
			this.zV = new float[4];
			normal = new Vector3f[4];
			for (int i = 0; i < 4; i++) {
				xV[i] = in.readFloat();
			}
			for (int i = 0; i < 4; i++) {
				yV[i] = in.readFloat();
			}
			for (int i = 0; i < 4; i++) {
				zV[i] = in.readFloat();
			}
			textureIndex = in.readInt();
			calcNormal();
			break;
		case 1:
			yDelta = in.readFloat();
			textureIndex = in.readInt();
			// System.out.println("succ. read Tile");
		}
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
