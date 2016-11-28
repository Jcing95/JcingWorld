package org.jcing.jcingworld.engine.entities.models;

import org.jcing.jcingworld.engine.VAOData;

public class RawModel {

	private VAOData vaodata;
	private int vertexCount;

	public RawModel(VAOData data, int vertexCount) {
		vaodata = data;
		this.vertexCount = vertexCount;
	}

	public int getVaoID() {
		return vaodata.getVAO();
	}
	
	public VAOData getVaoData(){
		return vaodata;
	}

	public int getVertexCount() {
		return vertexCount;
	}

}
