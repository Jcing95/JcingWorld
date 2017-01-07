package org.jcing.filesystem;

import java.util.HashMap;

import org.jcing.jcingworld.engine.terrain.Terrain;

public class ModuloTree<O> {

	public static final int MODULATOR = Terrain.RENDERDISTANCERADIUS;

	private HashMap<Integer, ModuloTree<O>> subMap;
	private O obj;
	//    private JHash<O> superMap;
	//TODO: modulo array or no rehash

//	private int iteration;

	public static final int MAXDELTA = Terrain.RENDERDISTANCERADIUS*2+1;

	public ModuloTree() {
		subMap = new HashMap<Integer, ModuloTree<O>>(MODULATOR + 2, 1);
//		iteration = 0;
	}

	private ModuloTree(boolean child) {
//		this.iteration = iteration;
		//        subMap = neww HashMap<Integer, JHashManager<O>>();
//		this.superMap = superMap;

	}

	/*
	 * 
	 * put 123
	 * 
	 * super = it 0
	 * 
	 * 123/10 - 12 12 / 10 = 2
	 * 
	 * 
	 * 
	 * 
	 */

	private void initSubMap() {
		subMap = new HashMap<Integer, ModuloTree<O>>(MODULATOR + 1, 1);
	}

	public void set(O obj, int index) {
		if (index == 0) {
			this.obj = obj;
		} else {
			//            System.out.println(index % MODULATOR);
			if (subMap == null) {
				initSubMap();
			}
			if (subMap.get(index % MODULATOR) == null) {
				subMap.put(index % MODULATOR, new ModuloTree<O>(true));
			}
			subMap.get(index % MODULATOR).set(obj, index / MODULATOR);
		}
//		refresh(index);
	}

//	private void refresh(int index) {
//		if (index > upper) {
//			if (index > lower + MAXDELTA) {
//				put(null, lower);
//
//			}
//			upper = index;
//		}
//		if (index < lower) {
//			if (index < upper - MAXDELTA) {
//				put(null, upper);
//			}
//			lower = index;
//		}
//	}

	public O get(int index) {
		if (index == 0) {
			return obj;
		} else {
			//            if (subMap.get(index % MODULATOR) == null) {
			//                subMap.put(index % MODULATOR ,new JHash<O>(this, iteration + 1));
			//            }
			if (subMap == null) {
				initSubMap();
			}
			if (subMap.get(index % MODULATOR) != null) {
				return subMap.get(index % MODULATOR).get(index / MODULATOR);
			}
			return null;
		}
	}

	public O getObj() {
		return obj;
	}

	public void setObj(O obj) {
		this.obj = obj;
	}

}
