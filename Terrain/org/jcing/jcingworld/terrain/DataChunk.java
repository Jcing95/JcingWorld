package org.jcing.jcingworld.terrain;

import java.awt.Point;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;

import org.jcing.filesystem.FileLoader;
import org.jcing.jcingworld.game.Game;
import org.jcing.jcingworld.logging.Logs;

public class DataChunk {

	public HashMap<Point, ChunkContainer> loaded;

	public static final int SIZE = 8;

	//    private LinkedList<Point> assembledKeys;

	private Terrain terrain;

	private PrintStream out = Logs.subLog(Logs.chunkLoading, "ChunkData_Management", false);

	public DataChunk(Terrain terrain) {
		this.terrain = terrain;
		loaded = new HashMap<Point, ChunkContainer>(40);
		//        assembledKeys = new LinkedList<Point>();
	}

	//    private void put(int x, int z, ChunkData dta) {
	//        int xF = makePack(x);
	//        int zF = makePack(z);
	//        if (!loaded(xF, zF)) {
	//            if (!load(xF, zF)) {
	//                if (!loaded.containsKey(xF))
	//                    loaded.put(xF, new HashMap<Integer, ChunkData[][]>(
	//                            Terrain.RENDERDISTANCERADIUS / DataChunk.SIZE + 2, 1));
	//                loaded.get(xF).put(zF, new ChunkData[SIZE][SIZE]);
	//            }
	//        }
	//        loaded.get(xF).get(zF)[arr(x)][arr(z)] = dta;
	//        if (!assembledKeys.contains(new Point(xF, zF))) {
	//            assembledKeys.add(new Point(xF, zF));
	//            out.println("registered!" + assembledKeys.size() + "(" + xF + "|" + zF + ")");
	//        }
	//        out.println("put " + x + "|" + z + " to saver ... (" + xF + "|" + zF + ")[" + arr(x) + "]["
	//                + arr(z) + "]");
	//    }

	public ChunkData get(int x, int z) {
		int xF = makePack(x);
		int zF = makePack(z);
		if (loaded(xF, zF)) {
			out.println("loaded CD (" + x + "|" + z + ") at (" + xF + "|" + zF + ")");
			return loaded.get(new Point(xF, zF)).get(x, z);
			//            if (loaded.get(xF).get(zF)[arr(x)][arr(z)] == null) {
			//                ChunkData dta = new ChunkData(x, z, terrain.getTextureAtlas(), terrain);
			//                put(x, z, dta);
			//                return dta;
			//            } else
			//                return loaded.get(xF).get(zF)[arr(x)][arr(z)];
		} else {
			out.println("UNLOADED CD (" + x + "|" + z + ") at (" + xF + "|" + zF + ")");
			preparePackage(x, z);
			return loaded.get(new Point(xF, zF)).get(x, z);
			//            if (load(xF, zF)) {
			//                out.println("loading existing CD (" + xF + "|" + zF + ")[" + arr(x) + "|" + arr(z)
			//                        + "]");
			//                if (loaded.get(xF).get(zF)[arr(x)][arr(z)] == null) {
			//                    ChunkData dta = new ChunkData(x, z, terrain.getTextureAtlas(), terrain);
			//                    put(x, z, dta);
			//                    return dta;
			//                } else
			//                    return loaded.get(xF).get(zF)[arr(x)][arr(z)];
			//            } else {
			//                out.println("tried to load CD from (" + xF + "|" + zF + ")[" + arr(x) + "|" + arr(z)
			//                        + "] ... creating it");
			//                ChunkData dta = new ChunkData(x, z, terrain.getTextureAtlas(), terrain);
			//                put(x, z, dta);
			//                return dta;
			//            }
		}
	}

	private boolean load(int xF, int zF) {
		ChunkContainer fromFile = (ChunkContainer) FileLoader.loadFile(genFileName(xF, zF));
		//        if (!loaded.containsKey(new Point(xF,zF))) {
		//            loaded.put(xF, new HashMap<Integer, ChunkContainer>());
		//        }
		if (fromFile != null) {
			loaded.put(new Point(xF, zF), fromFile);
			return true;
		} else {
			return false;
		}
	}

	public void dismiss(Point point) {
		if (loaded.containsKey(point)) {
			loaded.get(point).dismiss();
			//TODO: save ChunkContainers
			System.out.println("removed " + point);
			loaded.remove(point);
		} else {
			System.out.println("could not dismiss " + point + " loaded:" + loaded.size());
			//		for (Point p : loaded.keySet()) {
			//			System.out.println("--| " + p);
			//		}
		}

		//        loaded.get(xF).get(zF)[arrX][arrZ] = null;
		//        if(empty(loaded.get(xF).get(zF))){
		//            loaded.get(xF).remove(zF);
		//            if(loaded.get(xF).size() == 0){
		//                loaded.remove(xF);
		//            }
		//        }

	}

	//    private boolean empty(ChunkData[][] chunkDatas) {
	//        for (ChunkData[] chunkDatas2 : chunkDatas) {
	//            for (ChunkData chunkData : chunkDatas2) {
	//                if(chunkData != null){
	//                    return false;
	//                }
	//            }
	//        }
	//        return true;
	//    }

	private void save(int xF, int zF) {
//		FileLoader.saveFile(loaded.get(new Point(xF, zF)), genFileName(xF, zF));
		loaded.remove(new Point(xF, zF));
	}

	public void finish() {
		//        for (Point pt : assembledKeys) {
		//            out.println("saved " + pt.x + "|" + pt.y);
		//        }

		for (Point pt : loaded.keySet()) {
			loaded.get(pt).dismiss();
		}

		//UNUSED:

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
		return loaded.containsKey(new Point(xF, zF));// && loaded.get(xF).containsKey(zF);
	}

	private File genFileName(int xF, int zF) {
		return new File("saves/" + Game.saveGameName + "/" + xF + "_" + zF + "/frame" + ChunkContainer.fileExtension);
	}

	public static int makePack(int x) {
		if (x >= 0) {
			return x / ChunkContainer.CONTAINERSIZE;
		}
		return (x / ChunkContainer.CONTAINERSIZE) - 1;
	}

	private int arr(int x) {
		if (x >= 0) {
			return x % SIZE;
		}
		return -(x % SIZE);
	}

	public void preparePackage(int x, int z) {
		int xF = makePack(x);
		int zF = makePack(z);
		if (!loaded(xF, zF)) {
			if (!load(xF, zF)) {
				loaded.put(new Point(xF, zF), new ChunkContainer(xF, zF, terrain));
				System.out.println("loaded new Package " + new Point(xF, zF) + " loaded:" + loaded.size());

			}
		}
		//        if (!assembledKeys.contains(new Point(xF, zF))) {
		//            assembledKeys.add(new Point(xF, zF));
		//            out.println("registered!" + assembledKeys.size() + "(" + xF + "|" + zF + ")");
		//        }

		//        int xF = x;//makeF(x);
		//        int zF = z;//makeF(z);
		//        if (!loaded(xF, zF)) {
		//            if (!load(xF, zF)) {
		//                if (!loaded.containsKey(xF))
		//                    loaded.put(xF, new HashMap<Integer, ChunkData[][]>(
		//                            Terrain.RENDERDISTANCERADIUS / DataChunk.SIZE + 2, 1));
		//                loaded.get(xF).put(zF, new ChunkData[SIZE][SIZE]);
		//                init(x, z);
		//            }
		//        }

	}

	//    private void init(int x, int z) {
	//        for (int i = 0; i < SIZE; i++) {
	//            for (int j = 0; j < SIZE; j++) {
	//                //                ChunkFrameData dta =  
	//                loaded.get(makePack(x)).get(makePack(z))[arr(i + x)][arr(j + z)] = new ChunkData(
	//                        x + i, z + j, terrain.getTextureAtlas(), terrain);
	//
	//            }
	//        }
	//    }
}
