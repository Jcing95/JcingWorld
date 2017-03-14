package org.jcing.jcingworld.editor.test;

import java.awt.image.BufferedImage;

import org.jcing.filesystem.FileLoader;
import org.jcing.jcingworld.terrain.generation.MapGenerator;
import org.lwjgl.util.vector.Vector2f;

public class BiomeTest {
	BufferedImage img;
	int size;
	MapGenerator gen = new MapGenerator(1337);

	public BiomeTest(int size) {
		img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		this.size = size;
		
	}
	
	public void init(Vector2f offset, float scale){
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				float biome = (float) gen.biome(i * scale + offset.x, j * scale + offset.y);
				int red=0, green=0, blue=0;
				switch((int)biome % 3){
				case 0:
					red = (int) (0xFF * (1f/3*(biome) / (gen.MAXBIOMES)));
					green = (int) (0xFF * (2f/3*(biome) / (gen.MAXBIOMES)));
					blue = (int) (0xFF * ((biome) / (gen.MAXBIOMES)));
					break;
				case 1:
					red = (int) (0xFF * (2f/3*(biome) / (gen.MAXBIOMES)));
					green = (int) (0xFF * ((biome) / (gen.MAXBIOMES)));
					blue = (int) (0xFF * (1f/3*(biome) / (gen.MAXBIOMES)));
					break;
				case 2:
					red = (int) (0xFF * ((biome) / (gen.MAXBIOMES)));
					green = (int) (0xFF * (1f/3*(biome) / (gen.MAXBIOMES)));
					blue = (int) (0xFF * (2f/3*(biome) / (gen.MAXBIOMES)));
					break;
				}
				
				int rgb = red << 16;
				rgb += green << 8;
				rgb += blue;
				img.setRGB(i, j, rgb);

			}
			System.out.println(i*100f/size + " %");
		}
	}

	public void save(){
		FileLoader.saveImage("biomemap.png", img);
		System.out.println("FIN");
	}
	
	public BufferedImage getImg() {
		return img;
	}
}
