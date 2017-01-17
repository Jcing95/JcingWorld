package org.jcing.jcingworld.engine.terrain;

import org.jcing.jcingworld.toolbox.Maths;
import org.openSimplex.OpenSimplexNoise;

public class MapGenerator {

    private OpenSimplexNoise noise;

//    private static float heightDelta = 7.25f;
//    private static float interpolation = 22.75f;

    public static float continentalHeightDelta = 100;
    private static float continentalInterpolation = 1075;

    public static float hillDelta = 245;
    public static float hillInterpolation = 375;
    public static float hillSteadyness = 1050;
    public static float hillErosionDelta = 25;
    public static float hillErosionInterpolation = 75;
    public static float hillErosionstretchdiff = 30f;

    public static float roughnessDelta = 5;
    public static float roughnesInterpolation = 15;
    public static float roughnesSteadyness = 257;
    
    private static float maxDelta = continentalHeightDelta+hillErosionDelta+hillDelta+roughnessDelta;

    public MapGenerator(long seed) {
        noise = new OpenSimplexNoise(seed);
    }

    private float noise(float x, float z, float delta, float interpolation) {
        return delta * (float) (noise.eval(x / (interpolation), z / (interpolation)));
    }

    public float height(float x, float z) {
        //        float height = continentalHeightDelta * (float) (noise.eval(x / (continentalInterpolation), z / (continentalInterpolation)));
        //        height += mountainDelta * (float) (noise.eval(x / (mountainInterpolation), z / (mountainInterpolation)));
        //        return +heightDelta * (float) (noise.eval(x / interpolation, z / interpolation))
        //                + (heightDelta / 6) * (float) (noise.eval(x / (interpolation / 6), z / (interpolation / 4)))
        //                + ());
        
        float height = noise(x, z, continentalHeightDelta, continentalInterpolation);
        
        float steady = noise(x, z, 1, hillSteadyness);
        height += noise(x, z, steady*hillDelta, hillInterpolation);
        height -= noise(x,z, steady*hillErosionstretchdiff,hillErosionInterpolation);
        
        steady += steady*noise(x, z, 1, roughnesSteadyness);
        height += noise(x, z, roughnessDelta*steady, roughnesInterpolation);
        return height;
    }

    public float tex(float x, float z) {
//    	return Math.min(Math.abs(noise(x,z,10,25)*(float) Math.sqrt(noise(x,z,10,25))),1);
    	
    	return Math.min(1,Math.max(0,(noise(noise(x,z,50,1000),(float)Math.sqrt(x*z),2,1)+1)));
    }

}
