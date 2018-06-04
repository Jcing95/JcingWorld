package org.jcing.jcingworld.engine.rendering;

import java.util.List;

import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.shading.terrain.TerrainShader;
import org.jcing.jcingworld.terrain.Chunk;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class TerrainRenderer {

	private TerrainShader shader;

	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.stop();
	}

	public void render(List<Chunk> terrains) {
		for (Chunk terrain : terrains) {
			prepareTerrain(terrain);
			LoadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			unbindTexturedModel();
		}
	}

	public void prepareTerrain(Chunk chunk) {
		RawModel rawModel = chunk.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindValues(chunk);
		shader.loadShineVariables(1, 0);

	}

	private void bindValues(Chunk chunk) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, chunk.getBlendMap().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, chunk.getTexturePack().getTextureID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, chunk.getSelectedTex().getTextureID());
		shader.loadTerrainData(chunk.getTextureIndices(), chunk.getTexturePack().getRows(), chunk.getSelected());

		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());
		//
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());
		// GL13.glActiveTexture(GL13.GL_TEXTURE2);
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());
		// GL13.glActiveTexture(GL13.GL_TEXTURE3);
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());
		// GL13.glActiveTexture(GL13.GL_TEXTURE4);
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getblackTexture().getTextureID());
		// GL13.glActiveTexture(GL13.GL_TEXTURE5);

	}

	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	private void LoadModelMatrix(Chunk terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}

}
