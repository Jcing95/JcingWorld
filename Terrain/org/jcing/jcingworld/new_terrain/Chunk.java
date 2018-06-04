package org.jcing.jcingworld.new_terrain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.lwjgl.util.Point;

public class Chunk implements Externalizable{
	
	public static byte VERSION = 0;
	public static int CHUNKSIZE = 128;
	
	Point position;
	Tile[][] tiles;
	
	public Chunk(Point position) {
		tiles = new Tile[CHUNKSIZE][CHUNKSIZE];
		this.position = new Point();
	}
	
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		switch(in.readByte()) {
		case 0: 
			position = new Point(in.readInt(), in.readInt());
			tiles = (Tile[][]) in.readObject();
		}
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeByte(VERSION);
		out.writeInt(position.getX());
		out.writeInt(position.getY());
		out.writeObject(tiles);
	}	
	
}
