package org.jcing.jcingworld.engine.terrain;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import org.jcing.jcingworld.engine.DisplayManager;
import org.jcing.jcingworld.engine.entities.Player;
import org.lwjgl.opengl.GL;
import org.lwjgl.util.vector.Vector2f;

public class LoadingCrawler extends Thread {

	private List<Point> unloadPackagesTemplate;
	private List<Point> loadedChunks;
	private boolean running, check;
	private Terrain terrain;
	Player player;

	public LoadingCrawler(Terrain terrain, List<Point> loadedChunks) {
		this.terrain = terrain;
		this.loadedChunks = loadedChunks;
		unloadPackagesTemplate = new LinkedList<Point>();
		initTemplate();
		check = false;
		running = true;
	}

	private void initTemplate() {
		for (int i = 0; i < Terrain.RENDERDISTANCERADIUS / DataChunk.SIZE + 2; i++) {
			for (int j = 0; j < Terrain.RENDERDISTANCERADIUS / DataChunk.SIZE + 2; j++) {
				if (new Vector2f(i, j).length() < Terrain.RENDERDISTANCERADIUS / DataChunk.SIZE + 2)
					unloadPackagesTemplate.add(new Point(i, j));
			}
		}
	}

	public void run() {
		setPriority(MIN_PRIORITY);
		GL.setCapabilities(DisplayManager.glCapabilities);

		while (running) {
			while (!check && running) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			check = false;
			for (Point chunk : unloadPackagesTemplate) {
				if(!running)
					return;
				terrain.getSaver().preparePackage(chunk.x+DataChunk.makeF(terrain.getPlayerChunkPos().x), chunk.y+DataChunk.makeF(terrain.getPlayerChunkPos().y));
			}
			for (Point chunk : loadedChunks) {
				if(!running)
					return;
				if(terrain.isSupposedToUnload(chunk)){
					terrain.unload(chunk);
				}
			}
			terrain.finishUnloading();
		}
	}

	public List<Point> getLoadedChunks() {
		return unloadPackagesTemplate;
	}

	public void setLoadedChunks(List<Point> loadedChunks) {
		this.unloadPackagesTemplate = loadedChunks;
	}

	public void check() {
		check = true;
	}

	public void setRunning(boolean b) {
		running = b;
	}
}
