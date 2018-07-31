package de.jcing.jcingworld.terrain.generation;

import opensimplexnoise.OpenSimplexNoise;

public class MapGenerator {

	private float maxHeightDelta = 120;

	private OpenSimplexNoise defNoise;

	private static final float BIOMEINTERPOLATION = 1530f;

	public static final int MAXBIOMES = 5;
	private OpenSimplexNoise humidityNoise, fertilityNoise;

	private OpenSimplexNoise noise;
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

	public MapGenerator(long seed) {
		defNoise = new OpenSimplexNoise(seed);
		noise = new OpenSimplexNoise(1337);
		humidityNoise = new OpenSimplexNoise((long) (seed * defNoise.eval(100, 50)));
		fertilityNoise = new OpenSimplexNoise((long) (seed * defNoise.eval(50, 100)));

	}

	private float noise(float x, float z, float delta, float interpolation) {
		return delta * (float) (noise.eval(x / (interpolation), z / (interpolation)));
	}

	public float height(float x, float z) {
		float height = noise(x, z, continentalHeightDelta, continentalInterpolation);

		float steady = noise(x, z, 1, hillSteadyness);
		height += noise(x, z, steady * hillDelta, hillInterpolation);
		height -= noise(x, z, steady * hillErosionstretchdiff, hillErosionInterpolation);

		steady += steady * noise(x, z, 1, roughnesSteadyness);
		height += noise(x, z, roughnessDelta * steady, roughnesInterpolation);
		return height;
	}

	public int biome(float x, float z) {
		double fert = fertilityNoise.eval(x / BIOMEINTERPOLATION, z / BIOMEINTERPOLATION)
				+ fertilityNoise.eval(x * 3 / (BIOMEINTERPOLATION / 100), z * 3 / (BIOMEINTERPOLATION / 100)) / 5f;
		double hum = humidityNoise.eval(x / BIOMEINTERPOLATION, z / BIOMEINTERPOLATION)
				+ humidityNoise.eval(x * 3 / (BIOMEINTERPOLATION / 100), z * 3 / (BIOMEINTERPOLATION / 100)) / 5f;
		// System.out.println("F: " + fert + " H: " + hum);

		double bindbounds = 0.035;
		

		if (fert < 0 && hum < 0)
			return 1;
		if (fert > 0 && hum < -0)
			return 2;
		if (fert < 0 && hum > 0)
			return 3;
		if (fert > 0 && hum > 0)
			return 4;
		return 0;
	}

	public int tex(float x, float z) {
		return biome(x, z);
	}

	public static final class BIOME {

		public static final int PLAINS = 0;
		public static final int HILLS = 1;
		public static final int DESERT = 2;
		public static final int MOUNTAINS = 3;
		public static final int OCEAN = 4;
	}
}
