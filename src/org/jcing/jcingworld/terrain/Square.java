package org.jcing.jcingworld.terrain;

import org.lwjgl.util.vector.Vector3f;

public class Square {
	
	private float[] x;
	private float[] y;
	private float[] z;
	private Vector3f normal;
	private boolean swapTriangles; //false wenn oben/rechts nicht tiefste
	
	
	public Square(float[] x, float[] y, float[] z){
		this.x = new float[4];
		this.y = new float[4];
		this.z = new float[4];
		for(int i=0; i<4; i++){
			this.x[i] = x[i];
			this.y[i] = y[i];
			this.z[i] = z[i];
		}
		calcSwap();
		
	}
	
	private void calcSwap(){
		
	}
	
	
	
	
}
