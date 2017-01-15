package org.jcing.jcingworld.engine.terrain;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import org.jcing.filesystem.FileLoader;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;

public class dataChunk {

    public HashMap<Integer, HashMap<Integer, ChunkData[][]>> loaded;
    public static final String fileExtension = ".jdc";

    public static final int SIZE = 8;
    
    private LinkedList<Vector2f> assembledList;
    private boolean changed;
    
    public dataChunk() {
        loaded = new HashMap<Integer, HashMap<Integer, ChunkData[][]>>();
    }

    public boolean loaded(int x, int y) {
        return loaded.containsKey(Maths.fastFloor(x / SIZE))
                && loaded.get(Maths.fastFloor(x / SIZE)).containsKey(Maths.fastFloor(y / SIZE));
    }

    private void save(int x, int y) {
        ChunkData[][] dta = loaded.get(Maths.fastFloor(x)).get(Maths.fastFloor(y));
        FileLoader.saveFile(dta,
                new File(Maths.fastFloor(x) + "_" + Maths.fastFloor(y) + fileExtension));
    }
    
    public LinkedList<Vector2f> assembleKeys(){
        if(changed){
            assembledList = new LinkedList<Vector2f>();
            for (int x : loaded.keySet()) {
                for (int y : loaded.get(x).keySet()) {
                    assembledList.add(new Vector2f(x,y));
                }
            }
            changed = false;
        }
        return assembledList;
    }
    
    private boolean load(int xF, int yF) {
        ChunkData[][] ldt = (ChunkData[][]) FileLoader
                .loadFile(new File(xF + "_" + yF + fileExtension));
        if (ldt != null) {
            if (!loaded.containsKey(xF)) {
                loaded.put(xF, new HashMap<Integer, ChunkData[][]>());
            }
            loaded.get(xF).put(yF, ldt);
            changed = true;
            return true;
        } else {
            return false;
        }
    }

    public ChunkData get(int x, int y) {
        if (loaded(x, y)) {
            return loaded.get(Maths.fastFloor(x / SIZE)).get(Maths.fastFloor(y / SIZE))[x % SIZE][y
                    % SIZE];
        } else {
            if (load(Maths.fastFloor(x / SIZE), Maths.fastFloor(y / SIZE))) {
                return loaded.get(Maths.fastFloor(x / SIZE)).get(Maths.fastFloor(y / SIZE))[x
                        % SIZE][y % SIZE];
            } else {
                return null;
            }
        }
    }
}
