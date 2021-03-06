package org.jcing.jcingworld.engine.rendering;

import java.util.List;

import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.gui.GuiPart;
import org.jcing.jcingworld.engine.shading.gui.GUIShader;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

public class GUIRenderer {

    private final RawModel quad;
    private GUIShader shader;

    public GUIRenderer(Loader loader) {
        float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
        quad = loader.loadToVAO(positions);
        shader = new GUIShader();
    }

    public void render(List<GuiPart> guis) {
        shader.start();
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (GuiPart gui : guis) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTextureID());
            Matrix4f matrix = Maths.createTransformationMatrix(gui.getPos(), gui.getScale());
            shader.loadTransformation(matrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    // public void render(GUIpart guipart, GUIShader shader) {
    // TexturedModel model = createRect();
    // RawModel rawModel = model.getRawModel();
    // GL30.glBindVertexArray(rawModel.getVaoID());
    // GL20.glEnableVertexAttribArray(0);
    // GL20.glEnableVertexAttribArray(1);
    // Matrix4f transformationMatrix =
    // Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(),
    // entity.getRotY(), entity.getRotZ(),
    // entity.getScale());
    // shader.loadTransformationMatrix(transformationMatrix);
    // GL13.glActiveTexture(GL13.GL_TEXTURE0);
    // GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
    // GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
    // GL11.GL_UNSIGNED_INT, 0);
    // // GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());
    // GL20.glDisableVertexAttribArray(0);
    // GL20.glDisableVertexAttribArray(1);
    // GL30.glBindVertexArray(0);
    // }
    //
    // private TexturedModel createRect() {
    //
    // }
}
