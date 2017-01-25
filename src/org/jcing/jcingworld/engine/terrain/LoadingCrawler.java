package org.jcing.jcingworld.engine.terrain;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import org.jcing.jcingworld.engine.DisplayManager;
import org.jcing.jcingworld.engine.entities.Player;
import org.lwjgl.opengl.GL;
import org.lwjgl.util.vector.Vector2f;

public class LoadingCrawler extends Thread {

    private List<Point> preparePackagesTemplate;
    private List<Point> loadedChunks;
    private List<Point> markedAsLoaded;
    private List<Point> unloaded;
    private boolean running, check;
    private Terrain terrain;
    Player player;

    public LoadingCrawler(Terrain terrain) {
        this.terrain = terrain;
        loadedChunks = new LinkedList<Point>();
        unloaded = new LinkedList<Point>();
        markedAsLoaded = new LinkedList<Point>();
        preparePackagesTemplate = new LinkedList<Point>();
        initTemplate();
        check = false;
        running = true;
    }

    private void initTemplate() {
        for (int i = 0; i < Math.ceil((float) Terrain.RENDERDISTANCERADIUS / DataChunk.SIZE)
                + 1; i++) {
            for (int j = 0; j < Math.ceil((float) Terrain.RENDERDISTANCERADIUS / DataChunk.SIZE)
                    + 1; j++) {
                if (new Vector2f(i, j)
                        .length() < Math.ceil((float) Terrain.RENDERDISTANCERADIUS / DataChunk.SIZE)
                                + 1)
                    preparePackagesTemplate.add(new Point(i, j));
            }
        }
    }

    public void run() {
        //		setPriority(MIN_PRIORITY);
        GL.setCapabilities(DisplayManager.glCapabilities);

        while (running) {
            while (!check && running) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            check = false;
            for (Point pack : preparePackagesTemplate) {
                terrain.getSaver().preparePackage(
                        pack.x + DataChunk.makePack(terrain.getPlayerChunkPos().x),
                        pack.y + DataChunk.makePack(terrain.getPlayerChunkPos().y));
            }
            for (Point chunk : markedAsLoaded) {
                loadedChunks.add(chunk);
            }
            markedAsLoaded.clear();
            
            for (Point chunk : loadedChunks) {
                if (terrain.isSupposedToUnload(chunk)) {
                    terrain.unload(chunk);
                    unloaded.add(chunk);
                }
            }
            for (Point point : unloaded) {
                loadedChunks.remove(point);
            }
            unloaded.clear();
        }
    }

    public void markLoaded(Point chunk) {
        markedAsLoaded.add(chunk);
    }

    public List<Point> getLoadedChunks() {
        return preparePackagesTemplate;
    }

    public void setLoadedChunks(List<Point> loadedChunks) {
        this.preparePackagesTemplate = loadedChunks;
    }

    public void check() {
        check = true;
    }

    public void setRunning(boolean b) {
        running = b;
    }
}
