package org.jcing.options;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Flagbyte implements Externalizable {

	private int flags;
	public static final int length = Integer.BYTES * 8;

	public void set(int index, boolean status) {
		if (status && !is(index)) {
			flags += Math.pow(2, index);
		}
		if (!status && is(index)) {
			flags -= Math.pow(2, index);
		}
	}

	public boolean is(int index) {
		return (flags >> index) % 2 > 0;
	}

	public boolean[] asBoolean() {
		boolean[] res = new boolean[length];
		for (int i = 0; i < res.length; i++) {
			res[i] = is(i);
		}
		return res;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		flags = in.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(flags);
	}

}
