package org.jcing.jcingworld.engine.terrain;

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

    private static float maxDelta = continentalHeightDelta + hillErosionDelta + hillDelta
            + roughnessDelta;

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
        height += noise(x, z, steady * hillDelta, hillInterpolation);
        height -= noise(x, z, steady * hillErosionstretchdiff, hillErosionInterpolation);

        steady += steady * noise(x, z, 1, roughnesSteadyness);
        height += noise(x, z, roughnessDelta * steady, roughnesInterpolation);
        return height;
    }

    private byte decide(float x, float z, int i) {
        if (noise(x + i * 1234, z + i * 5678, 1, 1000) >= 0) {
            return 1;
        }
        return 0;
    }

    public int tex(float x, float z, float maxDelta) {
        int iterations = 1;
        for (int i = 2; i < maxDelta; i *= 2) {
            iterations++;
        }
        byte decisions[] = new byte[iterations];
        for (int i = 0; i < decisions.length; i++) {
            decisions[i] = decide(x, z, i);
        }
        int tex = 0;
        int fac = 1;
        for (int i = 0; i < decisions.length; i++) {
            if (tex + decisions[i] * fac < maxDelta) {
                tex += decisions[i] * fac;
                fac *= 2;
            }
        }
        return tex;
        //    	return maxDelta-1;
        //    	if((noise(x,z,1,100)) < 0){
        //    		return maxDelta/2*(noise(x,z,1,100)+1);
        //    	}else{
        //    		return maxDelta-1;
        //    	}
        //        Random random = new Random((long) Math.abs((x+2356)*(z+7895)/100));
        //        return random.nextFloat()*maxDelta;
        //        float noise1 = 0;
        //        for (int i = 0; i < maxDelta; i++) {
        //            noise1 += Maths.fastFloor(Math.abs(noise(x*i,z*i,2,100*maxDelta)));
        //        }
        ////        noise1 /= 2;
        ////        float noise2 = Maths.fastFloor(Math.abs(noise(x*2,z*2,2,100)));
        ////        float noiselast = noise(noise1,noise1,maxDelta/2f,1); //pos / neg
        //        return Maths.fastFloor(noise1);

        //    	return Math.min(1,Math.max(0,(noise(noise(x,z,50,1000),(float)Math.sqrt(x*z),2,1)+1)));
    }

}
