package org.jcing.jcingworld.gui;

import org.jcing.jcingworld.shading.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;

public class GUIShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src/org/jcing/jcingworld/gui/GUIVertexshader";
	private static final String FRAGMENT_FILE = "src/org/jcing/jcingworld/gui/GUIFragmentshader";

	private int location_transformationMatrix;

	public GUIShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public void loadTransformation(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
