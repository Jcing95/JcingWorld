package org.jcing.jcingworld.terrain.generation;

import org.openSimplex.OpenSimplexNoise;

public class MapGenerator {

	private float maxHeightDelta = 120;

	private OpenSimplexNoise defNoise;

	private static final float BIOMEINTERPOLATION = 1560f;

	public static final int MAXBIOMES = 5;
	private OpenSimplexNoise humidityNoise, fertilityNoise;

	public MapGenerator(long seed) {
		defNoise = new OpenSimplexNoise(seed);

		humidityNoise = new OpenSimplexNoise((long) (seed * defNoise.eval(100, 50)));
		fertilityNoise = new OpenSimplexNoise((long) (seed * defNoise.eval(50, 100)));

	}

	public float height(float x, float z) {
		return 0;
	}

	public int biome(float x, float z) {
		double fert = fertilityNoise.eval(x / BIOMEINTERPOLATION, z / BIOMEINTERPOLATION)
				+ fertilityNoise.eval(x * 3 / (BIOMEINTERPOLATION / 100), z * 3 / (BIOMEINTERPOLATION / 100)) / 5f;
		double hum = humidityNoise.eval(x / BIOMEINTERPOLATION, z / BIOMEINTERPOLATION)
				+ humidityNoise.eval(x * 3 / (BIOMEINTERPOLATION / 100), z * 3 / (BIOMEINTERPOLATION / 100)) / 5f;
		// System.out.println("F: " + fert + " H: " + hum);

		double bindbounds = 0.035;

		if (Math.abs(fert) < bindbounds || Math.abs(hum) < bindbounds)
			return 0;
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

	public int tex(float x, float z, float maxDelta) {
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
