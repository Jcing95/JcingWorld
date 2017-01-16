package org.jcing.jcingworld.engine.terrain;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;

import org.jcing.filesystem.FileLoader;
import org.jcing.jcingworld.game.Game;
import org.jcing.jcingworld.logging.Logs;
import org.jcing.jcingworld.toolbox.Maths;

public class DataChunk {

	public HashMap<Integer, HashMap<Integer, ChunkData[][]>> loaded;
	public static final String fileExtension = ".jdc";

	public static final int SIZE = 4;

	private HashMap<Integer, Integer> assembledKeys;
	private boolean changed = true;

	private PrintStream out = Logs.subLog(Logs.chunkLoading, "ChunkData_Management", true);

	public DataChunk() {
		loaded = new HashMap<Integer, HashMap<Integer, ChunkData[][]>>();
	}

	public void put(int x, int z, ChunkData dta) {
		int xF = makeF(x);
		int zF = makeF(z);
		if (!loaded(xF, zF)) {
			if (!load(xF, zF)) {
				if (!loaded.containsKey(xF))
					loaded.put(xF, new HashMap<Integer, ChunkData[][]>());
				loaded.get(xF).put(zF, new ChunkData[SIZE][SIZE]);
			}
		}
		loaded.get(xF).get(zF)[arr(x)][arr(z)] = dta;
		changed = true;
		out.println("put " + x + "|" + z + " to saver ... (" + xF + "|" + zF + ")[" + arr(x) + "][" + arr(z) + "]");
	}

	public ChunkData get(int x, int z) {
		int xF = makeF(x);
		int zF = makeF(z);
		if (loaded(xF, zF)) {
			out.println("got loaded CD[" + x + "|" + z + "] at (" +xF + "|" + zF + ")");
			return loaded.get(xF).get(zF)[arr(x)][arr(z)];
		} else {
			if (load(xF, zF)) {
				out.println("loading existing CD[" + x + "|" + z + "]");
				return loaded.get(xF).get(zF)[arr(x)][arr(z)];
			} else {
				out.println("tried to load CD[" + x + "|" + z + "]");
				return null;
			}
		}
	}

	private boolean load(int xF, int zF) {
		ChunkData[][] fromFile = (ChunkData[][]) FileLoader.loadFile(genFileName(xF, zF));
		if (fromFile != null) {
			if (!loaded.containsKey(xF)) {
				loaded.put(xF, new HashMap<Integer, ChunkData[][]>());
			}
			loaded.get(xF).put(zF, fromFile);
			changed = true;
			return true;
		} else {
			return false;
		}
	}

	private void save(int xF, int zF) {
		FileLoader.saveFile(loaded.get(xF).get(zF), genFileName(xF, zF));
		loaded.get(xF).remove(zF);
		if (loaded.get(xF).size() == 0) {
			loaded.remove(xF);
		}
	}

	// private void save(Vector2f vec) {
	// save(Maths.fastFloor(vec.x), Maths.fastFloor(vec.y));
	// }

	public HashMap<Integer, Integer> assembleKeys() {
		if (changed) {
			assembledKeys = new HashMap<Integer, Integer>(loaded.size());
			for (int x : loaded.keySet()) {
				for (int y : loaded.get(x).keySet()) {
					assembledKeys.put(x, y);
				}
			}
			changed = false;
		}
		return assembledKeys;
	}

	public void printStatus() {
//		assembleKeys();
//		out.println("Printing DataChunk status");
//
//		for (int x : assembledKeys.keySet()) {
//			out.println("- [" + x + "][" + assembledKeys.get(x) + "]");
//			ChunkData[][] dta = loaded.get(x).get(assembledKeys.get(x));
//			out.println("subChunk:");
//
//			int length = (x > assembledKeys.get(x)) ? ("" + x).length() : ("" + assembledKeys.get(x)).length();
//			String notLoaded = "[";
//			for (int j = 0; j < length; j++) {
//				notLoaded += "X";
//			}
//			notLoaded += "|";
//			for (int j = 0; j < length; j++) {
//				notLoaded += "X";
//			}
//			notLoaded += "]";
//
//			for (int i = 0; i < dta.length; i++) {
//				String line = "----";
//				for (int j = 0; j < dta.length; j++) {
//					if (dta[i][j] != null) {
//						line += "[" + x + i + "|" + assembledKeys.get(x) + j + "]";
//					} else {
//						line += notLoaded;
//					}
//				}
//				out.println(line);
//			}
//		}
	}

	public void finish() {
		for (int x : assembleKeys().keySet()) {
			save(x, assembledKeys.get(x));
		}
	}

	private boolean loaded(int xF, int zF) {
		return loaded.containsKey(xF) && loaded.get(xF).containsKey(zF);
	}

	private File genFileName(int xF, int zF) {
		return new File(Game.saveGameName + "/" + xF + "_" + zF + fileExtension);
	}

	private int makeF(int x){
		if (x >= 0) {
			return x / SIZE;
		}
		return (x / SIZE)-1;
	}
	
	private int arr(int x) {
		if (x >= 0) {
			return x % SIZE;
		}
		return SIZE + (x % SIZE);
	}
}
