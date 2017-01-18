package org.jcing.jcingworld.engine.terrain;

import java.awt.Point;
import java.util.List;

import org.jcing.jcingworld.engine.DisplayManager;
import org.jcing.jcingworld.engine.entities.Player;
import org.lwjgl.opengl.GL;

public class LoadCrawler extends Thread {
	
	private List<Point> loadedChunksTemplate;
	private boolean running, check;
	private Terrain terrain;
	Player player;
	
	public LoadCrawler(Terrain terrain, List<Point> loadedChunks){
		this.loadedChunksTemplate = loadedChunks;
		this.terrain = terrain;
		check = false;
		running = true;
	}
	
	public void run(){
	    setPriority(MIN_PRIORITY);
      GL.setCapabilities(DisplayManager.glCapabilities);

		while(running){
			while(!check && running){
				try {
					sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			check = false;
			for (Point chunk : loadedChunksTemplate) {
				terrain.checkLoad(chunk);
			}
			terrain.finishUnloading();
		}
	}

	public List<Point> getLoadedChunks() {
		return loadedChunksTemplate;
	}

	public void setLoadedChunks(List<Point> loadedChunks) {
		this.loadedChunksTemplate = loadedChunks;
	}

	public void setChecking() {
		check = true;
	}

	public void setRunning(boolean b) {
		running = b;
	}
}
