package de.jcing.filetable;

import java.io.Externalizable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import de.jcing.filesystem.FileLoader;

public class FileTable<O> implements Externalizable, FilenameFilter {

	public static final byte VERSION = 0;

	private static final String FileTableExtension = ".jft";

	private ArrayList<String> paths;
	private String extension;
	private String path;

	public FileTable() {

	}

	public FileTable(String fileExtension) {
		extension = fileExtension;
		paths = new ArrayList<String>();
	}

	public void add(String path) {
		if (path.endsWith(extension)) {
			paths.add(path);
		}
	}

	public void remove(int index) {
		paths.remove(index);
	}

	public void remove(String path) {
		paths.remove(path);
	}

	public void swap(int index1, int index2) {
		String placeholder = paths.get(index1);
		paths.set(index1, paths.get(index2));
		paths.set(index2, placeholder);
	}

	public int indexOf(String path) {
		return paths.indexOf(path);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<O> loadAll() {
		ArrayList<O> fls = new ArrayList<O>();
		for (int i = 0; i < paths.size(); i++) {
			fls.add((O) FileLoader.loadFile(new File(path + "/" + paths.get(i))));
		}
		return fls;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		byte v = in.readByte();
		switch (v) {
		case 0:
			int len = in.readInt();
			for (int i = 0; i < len; i++) {
				paths.add(in.readLine());
			}
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeByte(VERSION);
		out.writeInt(paths.size());
		for (int i = 0; i < paths.size(); i++) {
			out.writeChars(paths.get(i));
			out.writeChars(System.lineSeparator());
		}

	}

	@Override
	public boolean accept(File loc, String name) {
		return name.endsWith(extension);
	}

}
