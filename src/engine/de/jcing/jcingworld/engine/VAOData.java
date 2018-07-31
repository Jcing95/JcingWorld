package de.jcing.jcingworld.engine;

public class VAOData {

	private int VAO;
	private int verticesVBO;
	private int textureVBO;
	private int normalsVBO;
	private int texOffsetVBO;

	public VAOData(int VAO, int verticesVBO, int textureVBO, int normalsVBO) {
		super();
		this.VAO = VAO;
		this.verticesVBO = verticesVBO;
		this.textureVBO = textureVBO;
		this.normalsVBO = normalsVBO;
	}

	public VAOData(int VAO, int verticesVBO, int textureVBO, int normalsVBO, int texOffsetVBO) {
		super();
		this.VAO = VAO;
		this.verticesVBO = verticesVBO;
		this.textureVBO = textureVBO;
		this.normalsVBO = normalsVBO;
		this.texOffsetVBO = texOffsetVBO;
	}

	public int getVAO() {
		return VAO;
	}

	public void setVAO(int vAO) {
		VAO = vAO;
	}

	public int getVerticesVBO() {
		return verticesVBO;
	}

	public void setVerticesVBO(int verticesVBO) {
		this.verticesVBO = verticesVBO;
	}

	public int getTextureVBO() {
		return textureVBO;
	}

	public void setTextureVBO(int textureVBO) {
		this.textureVBO = textureVBO;
	}

	public int getNormalsVBO() {
		return normalsVBO;
	}

	public void setNormalsVBO(int indicesVBO) {
		this.normalsVBO = indicesVBO;
	}

	public int getTexOffsetVBO() {
		return texOffsetVBO;
	}

	public void setTexOffsetVBO(int texOffsetVBO) {
		this.texOffsetVBO = texOffsetVBO;
	}

}
