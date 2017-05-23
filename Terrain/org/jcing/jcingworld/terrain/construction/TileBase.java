package org.jcing.jcingworld.terrain.construction;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import org.jcing.jcingworld.terrain.material.Material;

public class TileBase implements Externalizable {

	private byte x, z;

	private HashMap<Float, Material> content;

	public TileBase() {
	}

	public TileBase(byte x, byte z, Material mat, float y) {
		this.x = x;
		this.z = z;
		content = new HashMap<Float, Material>();
		content.put(y, mat);
	}

	public void addMat(Material mat, float y) {
		content.put(y, mat);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		x = in.readByte();
		z = in.readByte();
		content = (HashMap<Float, Material>) in.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeByte(x);
		out.writeByte(z);
		out.writeObject(content);

	}

	public byte getX() {
		return x;
	}
	
	public byte getZ() {
		return z;
	}

}
