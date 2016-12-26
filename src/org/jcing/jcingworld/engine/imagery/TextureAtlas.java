package org.jcing.jcingworld.engine.imagery;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.LinkedList;

import org.jcing.filesystem.FolderLoader;
import org.jcing.jcingworld.engine.Loader;
import org.jcing.jcingworld.logging.Logs;

public class TextureAtlas extends BaseImage {

    protected int textureSize;
    protected int rows;
    protected int numTextures;
    private PrintStream out = Logs.atlas;

    public TextureAtlas(BaseImage base, int subTextureSize) {
        super(base);
        rows = width / subTextureSize;
        this.textureSize = subTextureSize;
        this.numTextures = rows * rows;
        Logs.atlas.println("creating TextureAtlas: " + rows + " rows x " + subTextureSize + "px ("
                + numTextures + ") Textures combined");

    }

    public TextureAtlas(TextureAtlas atlas) {
        super(atlas);
        rows = atlas.rows;
        numTextures = atlas.numTextures;
        //        Logs.atlas.println("creating TextureAtlas: " + rows + " rows x " + textureSize +"px");
        textureSize = atlas.textureSize;
    }

    public TextureAtlas(String folderPath, Loader loader) {
        super(0, 0, 0);
        constructAtlas(folderPath, loader);
    }

    protected void constructAtlas(String folderPath, Loader loader) {
        LinkedList<BufferedImage> imgs = new FolderLoader().indexedLoad(folderPath);
        out.println("constructing atlas...");
        int size = (int) Math.ceil(Math.sqrt(imgs.size()));
        int width = imgs.getFirst().getWidth();
        int height = imgs.getFirst().getHeight();
        BufferedImage base = new BufferedImage(width * size, height * size,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = base.createGraphics(); //TODO: better way than drawing?
        int i = 0;
        int j = 0;
        for (BufferedImage bufferedImage : imgs) {
            g.drawImage(bufferedImage, i * width, j * height, null);
            i++;
            if (i >= size) {
                i = 0;
                j++;
            }
        }
        BaseImage img = loader.loadTexture(base, false);
        this.height = img.height;
        this.width = img.width;
        this.ID = img.ID;
        this.numTextures = imgs.size();
        this.textureSize = width;
        this.rows = size;
        imgs.clear();
        FolderLoader.saveImage("map.png", base);
        out.println("succesfully constructed atlas: " + numTextures + " textures in " + rows
                + " rows (" + this.width + "px²)");
    }

    public int getNumTextures() {
        return numTextures;
    }

    protected TextureAtlas(BaseImage base) {
        super(base);
    }

    public int getTextureSize() {
        return textureSize;
    }

    public int getRows() {
        return rows;
    }

}
