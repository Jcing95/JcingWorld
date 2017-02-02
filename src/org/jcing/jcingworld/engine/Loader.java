package org.jcing.jcingworld.engine;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.jcing.jcingworld.engine.entities.models.RawModel;
import org.jcing.jcingworld.engine.imagery.BaseImage;
import org.jcing.jcingworld.logging.Logs;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

/**
 * Loads stuff to the Graphics card
 * 
 * @author ThinMatrix / Jasin
 *
 */
public class Loader {

    //TODO: fix VAO management - VAOData + Textures list instead of vao/vbo etc.
    //		essential for consitency later on!

    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    //	private List<VAOData> VAODatas = new ArrayList<VAOData>();

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals,
            int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        int verticesPointer = storeDataInAttributeList(0, 3, positions);
        int textureCoordsPointer = storeDataInAttributeList(1, 2, textureCoords);
        int normalsPointer = storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        VAOData data = new VAOData(vaoID, verticesPointer, textureCoordsPointer, normalsPointer);
        return new RawModel(data, indices.length);
    }

    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals,
            int[] indices, float[] textureOffsets) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        int verticesPointer = storeDataInAttributeList(0, 3, positions);
        int textureCoordsPointer = storeDataInAttributeList(1, 2, textureCoords);
        int normalsPointer = storeDataInAttributeList(2, 3, normals);
        int texOffsetPointer = storeDataInAttributeList(3, 2, textureOffsets);
        unbindVAO();
        VAOData data = new VAOData(vaoID, verticesPointer, textureCoordsPointer, normalsPointer,
                texOffsetPointer);
        return new RawModel(data, indices.length);
    }

    public int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        unbindVAO();
        return vaoID;
    }

    public RawModel loadToVAO(float[] positions) {
        int vaoID = createVAO();
        int posPointer = storeDataInAttributeList(0, 2, positions);
        unbindVAO();
        VAOData data = new VAOData(vaoID, posPointer, -1, -1);
        return new RawModel(data, positions.length / 2);
    }

    public BaseImage loadTexture(String fileName, boolean linear) {
        Logs.textureLoader.println("--loading Texture [" + fileName + "]");

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        //         STBImage.stbi_set_flip_vertically_on_load(1);
        //         STBImage.stbi_
        ByteBuffer image = STBImage.stbi_load("res/" + fileName, w, h, comp, 4);
        if (image == null) {
            throw new RuntimeException("Failed to load [" + fileName + "]!" + System.lineSeparator()
                    + STBImage.stbi_failure_reason());
        }
        int width = w.get();
        int height = h.get();
        int textureID = GL11.glGenTextures();
        Logs.textureLoader
                .println("width: " + width + " height: " + height + " + ID: " + textureID);
        // image.flip();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, image);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        if (linear) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                    GL11.GL_LINEAR_MIPMAP_LINEAR);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        textures.add(textureID);
        Logs.textureLoader.println("succesfully loaded Texture [" + fileName + "]");
        return new BaseImage(width, height, textureID);
    }

    private ByteBuffer bufferedImageToByteBuffer(BufferedImage img) {
        int[] pixels = new int[img.getWidth() * img.getHeight()];
        img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(img.getWidth() * img.getHeight() * 4);

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int pixel = pixels[y * img.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF)); // Green component
                buffer.put((byte) (pixel & 0xFF)); // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component. Only for RGBA
            }
        }

        buffer.flip();
        return buffer;
    }

    public BaseImage loadTexture(BufferedImage img, boolean linear) {
        Logs.textureLoader.println("-+-loading Texture from BufferedImage");

        ByteBuffer image = bufferedImageToByteBuffer(img);
        if (image == null) {
            throw new RuntimeException("Failed to load bufferedImage!" + System.lineSeparator());
        }
        int width = img.getWidth();
        int height = img.getHeight();
        int textureID = GL11.glGenTextures();
        Logs.textureLoader
                .println("width: " + width + " height: " + height + " + ID: " + textureID);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, image);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        if (linear) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                    GL11.GL_LINEAR_MIPMAP_LINEAR);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        textures.add(textureID);
        Logs.textureLoader.println("succesfully loaded Texture from BufferedImage");
        return new BaseImage(width, height, textureID);
    }

    public void updateVBOfloat(int vaoID, int vboID, int offset, float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        GL30.glBindVertexArray(vaoID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, (long) (offset * Float.BYTES), buffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL11.glDeleteTextures(texture);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private int storeDataInAttributeList(int attributeNumber, int attributeSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, attributeSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vboID;
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    @Deprecated
    public int loadTexture(String fileName) {
        Logs.textureLoader.println("loading Texture" + fileName);

        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        // STBImage.stbi_set_flip_vertically_on_load(1);
        ByteBuffer image = STBImage.stbi_load("res/" + fileName + ".png", w, h, comp, 4);
        if (image == null) {
            throw new RuntimeException("Failed to load a texture file!" + System.lineSeparator()
                    + STBImage.stbi_failure_reason());
        }
        int width = w.get();
        int height = h.get();

        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE, image);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        textures.add(textureID);
        Logs.textureLoader.println("succesfully loaded Texture" + fileName);
        return textureID;
    }
}
