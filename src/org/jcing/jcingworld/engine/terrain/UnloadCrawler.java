package org.jcing.jcingworld.engine.terrain;

import java.awt.Point;
import java.util.List;

import org.jcing.jcingworld.engine.DisplayManager;
import org.lwjgl.opengl.GL;

public class UnloadCrawler extends Thread {
	
	private List<Point> loadedChunks;
	private boolean running, check;
	private Terrain terrain;
	
	public UnloadCrawler(Terrain terrain, List<Point> loadedChunks){
		this.loadedChunks = loadedChunks;
		this.terrain = terrain;
		check = false;
		running = true;
	}
	
	public void run(){
		GL.setCapabilities(DisplayManager.glCapabilities);
		while(running){
			while(!check && running){
				try {
					sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			check = false;
			
		}
	}

	public List<Point> getLoadedChunks() {
		return loadedChunks;
	}

	public void setLoadedChunks(List<Point> loadedChunks) {
		this.loadedChunks = loadedChunks;
	}

	public void setChecking() {
		check = true;
	}

	public void setRunning(boolean b) {
		running = b;
	}
}
