package org.jcing.jcingworld.engine.terrain;

import java.awt.Point;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;

import org.jcing.filesystem.FileLoader;
import org.jcing.jcingworld.game.Game;
import org.jcing.jcingworld.logging.Logs;

public class DataChunk {

	public HashMap<Integer, HashMap<Integer, ChunkFrameData[][]>> loaded;
	public static final String fileExtension = ".jcf";

	public static final int SIZE = 8;

	private LinkedList<Point> assembledKeys;

	private Terrain terrain;
	
	private PrintStream out = Logs.subLog(Logs.chunkLoading, "ChunkData_Management", false);

	public DataChunk(Terrain terrain) {
	    this.terrain = terrain;
		loaded = new HashMap<Integer, HashMap<Integer, ChunkFrameData[][]>>();
		assembledKeys = new LinkedList<Point>();
	}

	private void put(int x, int z, ChunkFrameData dta) {
		int xF = makeF(x);
		int zF = makeF(z);
		if (!loaded(xF, zF)) {
			if (!load(xF, zF)) {
				if (!loaded.containsKey(xF))
					loaded.put(xF, new HashMap<Integer, ChunkFrameData[][]>());
				loaded.get(xF).put(zF, new ChunkFrameData[SIZE][SIZE]);
			}
		}
		loaded.get(xF).get(zF)[arr(x)][arr(z)] = dta;
		if(!assembledKeys.contains(new Point(xF,zF))){
			assembledKeys.add(new Point(xF, zF));
			out.println("registered!" + assembledKeys.size() + "("+xF +"|"+zF+")");
		}
		out.println("put " + x + "|" + z + " to saver ... (" + xF + "|" + zF + ")[" + arr(x) + "][" + arr(z) + "]");
	}

	public ChunkFrameData get(int x, int z) {
		int xF = makeF(x);
		int zF = makeF(z);
		if (loaded(xF, zF)) {
			out.println("got loaded CD[" + arr(x) + "|" + arr(z) + "] at (" +xF + "|" + zF + ")");
			if(loaded.get(xF).get(zF)[arr(x)][arr(z)] == null){
				ChunkFrameData dta = new ChunkFrameData(x,z,terrain.getTextureAtlas());
	    		put(x,z,dta);
				return dta;
			}else
			return loaded.get(xF).get(zF)[arr(x)][arr(z)];
		} else {
			if (load(xF, zF)) {
				out.println("loading existing CD (" + xF + "|" + zF + ")[" + arr(x) + "|" + arr(z) + "]");
				if(loaded.get(xF).get(zF)[arr(x)][arr(z)] == null){
					ChunkFrameData dta = new ChunkFrameData(x,z,terrain.getTextureAtlas());
		    		put(x,z,dta);
					return dta;
				}else
				return loaded.get(xF).get(zF)[arr(x)][arr(z)];
			} else {
				out.println("tried to load CD from (" + xF + "|" + zF + ")[" + arr(x) + "|" + arr(z) + "] ... creating it");
				ChunkFrameData dta = new ChunkFrameData(x,z,terrain.getTextureAtlas());
	    		put(x,z,dta);
				return dta;
			}
		}
	}

	private boolean load(int xF, int zF) {
		ChunkFrameData[][] fromFile = (ChunkFrameData[][]) FileLoader.loadFile(genFileName(xF, zF));
		if (fromFile != null) {
			if (!loaded.containsKey(xF)) {
				loaded.put(xF, new HashMap<Integer, ChunkFrameData[][]>());
			}
			loaded.get(xF).put(zF, fromFile);
			
//			changed = true;
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

//	public HashMap<Integer, Integer> assembleKeys() {
//		if (changed) {
//			assembledKeys = new HashMap<Integer, Integer>(loaded.size());
//			for (int x : loaded.keySet()) {
//				for (int y : loaded.get(x).keySet()) {
//					assembledKeys.put(x, y);
//				}
//			}
//			changed = false;
//		}
//		return assembledKeys;
//	}

	public void printStatus() {
////		assembleKeys();
//		out.println("Printing DataChunk status");
//
//		for (int x : assembledKeys.keySet()) {
//			out.println("- [" + x + "][" + assembledKeys.get(x) + "]");
//			ChunkFrameData[][] dta = loaded.get(x).get(assembledKeys.get(x));
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
//		assembleKeys();
		for (Point pt : assembledKeys) {
//          frame.percent = (double)i/assembledKeys.size();
//          frame.repaint();
//            save(pt.x, pt.y);
            out.println("saved " + pt.x + "|" + pt.y);
        }
		
//		for (int x : loaded.keySet()) {
//            for (int z : loaded.get(x).keySet()) {
//                save(x,z);
//            }
//        }
//		Thread th = new Thread(){
//		    public void run(){
////		        WorldSaverFrame frame = new WorldSaverFrame();
//		        int i=0;
//		        for (int x : assembledKeys.keySet()) {
////		            frame.percent = (double)i/assembledKeys.size();
////		            frame.repaint();
//		            save(x, assembledKeys.get(x));
//		            out.println("saved " + x + "|" + assembledKeys.get(x));
//		            i++;
//		        }
////		        frame.dispose();
//		        out.println("finished saving!");
//		        try {
//					join();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//		    }
//		};
//		th.run();
//		
//		
	}

	private boolean loaded(int xF, int zF) {
		return loaded.containsKey(xF) && loaded.get(xF).containsKey(zF);
	}

	private File genFileName(int xF, int zF) {
		return new File("saves/" + Game.saveGameName + "/" + xF + "_" + zF + "/frame" +fileExtension);
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
		return -(x % SIZE);
	}

	public void prepare(int x, int z) {
		int xF = makeF(x);
		int zF = makeF(z);
		if(!loaded(xF,zF)){
			if(!load(xF,zF)){
				if (!loaded.containsKey(xF))
					loaded.put(xF, new HashMap<Integer, ChunkFrameData[][]>());
				loaded.get(xF).put(zF, new ChunkFrameData[SIZE][SIZE]);
				init(xF, zF);
			}
		}
		if(!assembledKeys.contains(new Point(xF,zF))){
            assembledKeys.add(new Point(xF, zF));
            out.println("registered!" + assembledKeys.size() + "("+xF +"|"+zF+")");
        }
	}

    private void init(int xF, int zF) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
//                ChunkFrameData dta =  
                loaded.get(xF).get(zF)[i][j]=new ChunkFrameData(xF+i,zF+j,terrain.getTextureAtlas());
                
            }
        }
    }
}
