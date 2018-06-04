package org.jcing.jcingworld.terrain;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jcing.filesystem.FileLoader;
import org.jcing.jcingworld.game.Game;

public class ChunkContainer implements Externalizable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String fileExtension = ".jcf";

	private static final int VERSION = 0;
	public static int CONTAINERSIZE = 8;

	private ChunkData[][] chunks;

	private int xF, zF;

	public ChunkContainer(int xF, int zF, Terrain terrain) {
		this.xF = xF;
		this.zF = zF;
		chunks = new ChunkData[CONTAINERSIZE + 1][CONTAINERSIZE + 1];
		for (int i = 0; i < chunks.length; i++) {
			for (int j = 0; j < chunks.length; j++) {
				chunks[i][j] = new ChunkData((xF * (CONTAINERSIZE)) + i, (zF * (CONTAINERSIZE)) + j, terrain);
				// chunks[i][j].init(terrain.getGenerator());
			}
		}
	}

	public ChunkContainer() {

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(VERSION);
		// out.writeInt(CONTAINERSIZE);
		out.writeInt(xF);
		out.writeInt(zF);
		for (int i = 0; i < chunks.length; i++) {
			for (int j = 0; j < chunks.length; j++) {
				out.writeObject(chunks[i][j]);
			}
		}
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		switch (version) {
		case 0:
			// CONTAINERSIZE = in.readInt();
			xF = in.readInt();
			zF = in.readInt();
			chunks = new ChunkData[CONTAINERSIZE + 1][CONTAINERSIZE + 1];
			for (int i = 0; i < chunks.length; i++) {
				for (int j = 0; j < chunks.length; j++) {
					chunks[i][j] = (ChunkData) in.readObject();
				}
			}
		}
	}

	public ChunkData get(int x, int z) {
		return chunks[x - (xF * (CONTAINERSIZE))][z - (zF * (CONTAINERSIZE))];
	}

	public void set(int x, int z, ChunkData chunk) {
		chunks[x - (xF * (CONTAINERSIZE - 1))][z - (zF * (CONTAINERSIZE - 1))] = chunk;
	}

	private File genFileName() {
		return new File("saves/" + Game.saveGameName + "/" + xF + "_" + zF + "/frame" + fileExtension);
	}

	public void dismiss() {
		FileLoader.saveFile(this, genFileName());
		chunks = null;
		// for (int i = 0; i < chunks.length; i++) {
		// for (int j = 0; j < chunks.length; j++) {
		// if(!chunks[i][j].dismissed){
		// return false;
		// }
		// }
		// }
		// return true;
	}
}
