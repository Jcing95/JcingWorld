package org.jcing.jcingworld.terrain.material;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Material implements Externalizable {

	protected int textureID;

	public int getTexture() {
		return textureID;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.textureID = in.read();

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.write(textureID);

	}

}
