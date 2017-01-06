package org.jcing.jcingworld.engine.terrain;

import org.lwjgl.util.vector.Vector3f;

public class Tile {

    private float[] x;
    private float[] y;
    private float[] z;
    private Vector3f[] normal;

//    private Vector3f normal;
    private boolean swapTriangles; // false wenn oben/rechts nicht tiefste
    int textureIndex;
    int indexX, indexY;

    public Tile(float[] x, float[] y, float[] z, int indexX, int indexY, int textureIndex) {
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
        this.textureIndex = textureIndex;
        calcSwap();
        calcNormal();
    }

    private Vector3f calcNormal() {
        normal = new Vector3f[4];
        Vector3f left = Vector3f.sub(new Vector3f(x[1],y[1],z[1]), new Vector3f(x[0],y[0],z[0]), null);
        Vector3f right = Vector3f.sub(new Vector3f(x[0],y[0],z[0]), new Vector3f(x[2],y[2],z[2]), null);
        normal[0] = Vector3f.cross(left, right, null);
        
        left = Vector3f.sub(new Vector3f(x[1],y[1],z[1]), new Vector3f(x[2],y[2],z[2]), null);
        right = Vector3f.sub(new Vector3f(x[0],y[0],z[0]), new Vector3f(x[1],y[1],z[1]), null);
        normal[1] = Vector3f.cross(left, right, null);
        left = Vector3f.sub(new Vector3f(x[1],y[1],z[1]), new Vector3f(x[2],y[2],z[2]), null);
        right = Vector3f.sub(new Vector3f(x[2],y[2],z[2]), new Vector3f(x[3],y[3],z[3]), null);
        normal[2] = Vector3f.cross(left, right, null);
        left = Vector3f.sub(new Vector3f(x[1],y[1],z[1]), new Vector3f(x[3],y[3],z[3]), null);
        right = Vector3f.sub(new Vector3f(x[2],y[2],z[2]), new Vector3f(x[3],y[3],z[3]), null);
        normal[3] = Vector3f.cross(left, right, null);
//        System.out.println(normal[0]);
        return null;
    }

    private void calcSwap() {
        // TODO: TileCalcSwap
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

    public Vector3f[] getNormal() {
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
