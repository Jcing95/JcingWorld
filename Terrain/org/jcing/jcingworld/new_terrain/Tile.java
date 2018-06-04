package org.jcing.jcingworld.new_terrain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

public class Tile implements Externalizable {

	public static final byte VERSION = 0;

	HashMap<HeightProperty, Integer> mats;

	public Tile() {
		mats = new HashMap<HeightProperty, Integer>();
		
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		switch (in.readByte()) {
		case 0:
			mats = new HashMap<HeightProperty, Integer>(in.available()/(Float.BYTES * 2 + Integer.BYTES));
			while(in.available() >= Float.BYTES * 2 + Integer.BYTES) {
				mats.put(new HeightProperty(in.readFloat(), in.readFloat()), in.readInt());
			}
			break;
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.write(VERSION);
		for(HeightProperty h : mats.keySet()) {
			out.writeFloat(h.from);
			out.writeFloat(h.to);
			out.writeInt(mats.get(h));
		}
	}

	private class HeightProperty {

		float from, to;
		
		public HeightProperty(float from, float to) {
			this.from = this.to;
		}

		public boolean contains(float val) {
			return val >= from && val <= to;
		}
		
		@Override
		public boolean equals(Object o) {
			return o instanceof HeightProperty && ((HeightProperty) o).from == from && ((HeightProperty) o).to == to;
		}
	}

}
