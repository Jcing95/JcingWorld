package org.jcing.jcingworld.engine.terrain;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jcing.filesystem.FileLoader;
import org.jcing.jcingworld.game.Game;
import org.jcing.jcingworld.toolbox.Maths;
import org.lwjgl.util.vector.Vector2f;

public class DataChunk {

	public HashMap<Integer, HashMap<Integer, ChunkData[][]>> loaded;
	public static final String fileExtension = ".jdc";

	public static final int SIZE = 8;

	private HashMap<Integer, Integer> assembledList;
	private boolean changed;

	public DataChunk() {
		loaded = new HashMap<Integer, HashMap<Integer, ChunkData[][]>>();
	}

	public boolean loaded(int x, int y) {
		return loaded.containsKey(Maths.fastFloor(x / SIZE))
				&& loaded.get(Maths.fastFloor(x / SIZE)).containsKey(Maths.fastFloor(y / SIZE));
	}

	private void save(int xF, int yF) {
		FileLoader.saveFile(loaded.get(xF).get(yF), new File(Game.saveGameName + "/" + xF + "_" + yF + fileExtension));
		loaded.get(xF).remove(yF);
		if (loaded.get(xF).size() == 0) {
			loaded.remove(xF);
		}
	}

	private void save(Vector2f vec) {
		save(Maths.fastFloor(vec.x), Maths.fastFloor(vec.y));
	}

	public HashMap<Integer, Integer> assembleKeys() {
		if (changed) {
			assembledList = new HashMap<Integer, Integer>(loaded.size());
			for (int x : loaded.keySet()) {
				for (int y : loaded.get(x).keySet()) {
					assembledList.put(x, y);
				}
			}
			changed = false;
		}
		return assembledList;
	}

	public void put(int x, int y, ChunkData dta) {
		if (!loaded(x, y)) {
			if (!load(Maths.fastFloor(x / SIZE), Maths.fastFloor(y / SIZE))) {
				if (!loaded.containsKey(Maths.fastFloor(x / SIZE)))
					loaded.put(Maths.fastFloor(x / SIZE), new HashMap<Integer, ChunkData[][]>());
				if (!loaded.get(Maths.fastFloor(x / SIZE)).containsKey(Maths.fastFloor(y / SIZE)))
					loaded.get(Maths.fastFloor(x / SIZE)).put(Maths.fastFloor(y / SIZE), new ChunkData[SIZE][SIZE]);
			}
		}
		loaded.get(Maths.fastFloor(x / SIZE)).get(Maths.fastFloor(y / SIZE))[Math.abs(x % SIZE)][Math
				.abs(y % SIZE)] = dta;
		changed = true;
		System.out.println("put " + x + "|" + y + " to saver ... (" + Maths.fastFloor(x / SIZE) + "|"
				+ Maths.fastFloor(y / SIZE) + ")[" + Math.abs(x % SIZE) + "][" + Math.abs(y % SIZE) + "]");
	}

	private boolean load(int xF, int yF) {
		ChunkData[][] ldt = (ChunkData[][]) FileLoader
				.loadFile(new File(Game.saveGameName + "/" + xF + "_" + yF + fileExtension));
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
			return loaded.get(Maths.fastFloor(x / SIZE)).get(Maths.fastFloor(y / SIZE))[Math.abs(x % SIZE)][Math
					.abs(y % SIZE)];
		} else {
			if (load(Maths.fastFloor(x / SIZE), Maths.fastFloor(y / SIZE))) {
				return loaded.get(Maths.fastFloor(x / SIZE)).get(Maths.fastFloor(y / SIZE))[Math.abs(x % SIZE)][Math
						.abs(y % SIZE)];
			} else {
				return null;
			}
		}
	}

	public void finish() {
		for (int x : assembleKeys().keySet()) {
			save(x, assembledList.get(x));
		}
	}

	public void unload(List<Vector2f> vecs) {
		for (Vector2f vek : vecs) {
			save(vek);
		}
	}
}
