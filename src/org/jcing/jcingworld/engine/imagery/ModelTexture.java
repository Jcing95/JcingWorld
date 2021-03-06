package org.jcing.jcingworld.engine.imagery;

public class ModelTexture extends BaseImage {

    private float shineDamper = 1;
    private float reflectivity = 0;

    private boolean hasTransparency = false;
    private boolean useFakeLighting = false;

    public ModelTexture(int width, int height, int iD) {
        super(width, height, iD);
    }

    public ModelTexture(BaseImage baseTexture) {
        super(baseTexture);
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public boolean isHasTransparency() {
        return hasTransparency;
    }

    public void setHasTransparency(boolean hasTransparency) {
        this.hasTransparency = hasTransparency;
    }

    public boolean isUseFakeLighting() {
        return useFakeLighting;
    }

    public void useFakeLighting(boolean useFakeLighting) {
        this.useFakeLighting = useFakeLighting;
    }

}
