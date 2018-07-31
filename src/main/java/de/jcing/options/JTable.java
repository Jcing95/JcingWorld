package de.jcing.options;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

public class JTable<O> implements Externalizable {

	private boolean identifying;
	private ArrayList<O> os;
	private ArrayList<String> ids;

	public JTable() {
		os = new ArrayList<O>();
		// ids = new ArrayList<String>();
	}

	public void enableIdentifying() {
		identifying = true;
		ids = new ArrayList<String>(os.size());
		for (int i = 0; i < os.size(); i++) {
			ids.add(null);
		}

	}

	public void identify(int id, String identifier) {
		ids.set(id, identifier);
	}

	public int add(O o, String identifier) {
		os.add(o);
		if (identifying)
			ids.add(identifier);
		return os.size() - 1;
	}

	public int add(O o) {
		os.add(o);
		if (identifying)
			ids.add(null);
		return os.size() - 1;
	}

	public void set(int id, O value) {
		os.set(id, value);
	}

	public O get(int id) {
		return os.get(id);
	}

	public O get(String identifier) {
		int i = ids.indexOf(identifier);
		if (i < 0)
			return null;
		return os.get(i);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int len = in.readInt();
		os = new ArrayList<O>();
		ids = new ArrayList<String>();
		for (int i = 0; i < len; i++) {
			os.add((O) in.readObject());
		}
		if (in.readBoolean()) {
			identifying = true;
			for (int i = 0; i < len; i++) {
				ids.add(in.readLine());
			}
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(os.size());
		for (O o : os) {
			out.writeObject(o);
		}
		out.writeBoolean(identifying);
		if (identifying)
			for (String string : ids) {
				out.writeChars(string);
				out.writeChars(System.lineSeparator());
			}
	}

}
