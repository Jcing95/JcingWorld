package org.jcing.jcingworld.terrain.generation;

import org.openSimplex.OpenSimplexNoise;

public class TerrainNoise {
	
	private OpenSimplexNoise noise;
	long seed;
	float interpolation, heightDelta;
	
    public TerrainNoise(OpenSimplexNoise noise, long seed, float heightDelta) {
        super();
        this.noise = noise;
        this.seed = seed;
        this.heightDelta = heightDelta;
    }
	
	
	
}
