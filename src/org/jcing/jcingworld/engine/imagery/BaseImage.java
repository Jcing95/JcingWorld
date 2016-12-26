package org.jcing.jcingworld.engine.imagery;

public class BaseImage {
    protected int width, height;
    protected int ID;

    public BaseImage(int width, int height, int iD) {
        this.width = width;
        this.height = height;
        ID = iD;
    }

    public BaseImage(BaseImage base) {
        this.width = base.width;
        this.height = base.height;
        this.ID = base.ID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTextureID() {
        return ID;
    }

}
