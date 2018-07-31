package de.jcing.jcingworld.editor.test;

import java.util.HashMap;

public class Coordinator<O> {

	O obj;

	HashMap<Integer, Coordinator<O>> subX;
	HashMap<Integer, Coordinator<O>> subY;

	final int BASE;

	public Coordinator() {
		BASE = 10;
	}

	public void init() {
		subX = new HashMap<Integer, Coordinator<O>>(BASE + 1, 1);
		subY = new HashMap<Integer, Coordinator<O>>(BASE + 1, 1);

	}

	public void insert(int x, int y, O o) {
		if (subX.containsKey(x)) {
			subX.get(x).insert(x / BASE, y, o);
		} else if (subY.containsKey(y)) {
			subY.get(y).insert(x, y / BASE, o);
		} else {
			if (subX.size() > subY.size()) {
				Coordinator<O> c = new Coordinator<O>();
				c.insert(x / BASE, y, o);
				subX.put(x, c);

				// TODO: fix n co
			}
		}

	}

}
