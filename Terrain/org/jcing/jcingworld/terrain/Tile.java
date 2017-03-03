package org.jcing.jcingworld.terrain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.lwjgl.util.vector.Vector3f;

public class Tile implements Externalizable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6529445147414158037L;
	
	private static final int TILEVERSION = 0;
	
	public static final int SIZE = 6;
	
	private float[] x;
    private float[] y;
    private float[] z;
    private Vector3f[] normal;

//    private Vector3f normal;
//    private boolean swapTriangles; // false wenn oben/rechts nicht tiefste
    public int textureIndex;

    public Tile(float[] x, float[] y, float[] z, int textureIndex) {
        this.x = new float[4];
        this.y = new float[4];
        this.z = new float[4];
        normal = new Vector3f[4];
        for (int i = 0; i < 4; i++) {
            this.x[i] = x[i];
            this.y[i] = y[i];
            this.z[i] = z[i];
        }
        this.textureIndex = textureIndex;
        calcSwap();
        calcNormal();
    }

    public Tile(){
    	
    }
    
    @Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(TILEVERSION);
		for (int i = 0; i < 4; i++) {
			out.writeFloat(x[i]);
		}
		for (int i = 0; i < 4; i++) {
			out.writeFloat(y[i]);
		}
		for (int i = 0; i < 4; i++) {
			out.writeFloat(z[i]);
		}
//		for (int i = 0; i < 4; i++) {
//			out.writeFloat(normal[i].x);
//			out.writeFloat(normal[i].y);
//			out.writeFloat(normal[i].z);
//		}
		out.writeInt(textureIndex);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		switch(version){
		case 0:
			this.x = new float[4];
	        this.y = new float[4];
	        this.z = new float[4];
	        normal = new Vector3f[4];
			for (int i = 0; i < 4; i++) {
				x[i] = in.readFloat();
			}
			for (int i = 0; i < 4; i++) {
				y[i] = in.readFloat();
			}
			for (int i = 0; i < 4; i++) {
				z[i] = in.readFloat();
			}
//			for (int i = 0; i < 4; i++) {
//				normal[i] = new Vector3f(in.readFloat(),in.readFloat(),in.readFloat());
//			}
			textureIndex = in.readInt();
			calcNormal();
		}
	}

	private Vector3f calcNormal() {
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

//    public boolean isSwapTriangles() {
//        return swapTriangles;
//    }
}
