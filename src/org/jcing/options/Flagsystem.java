package org.jcing.options;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

public class Flagsystem implements Externalizable {

    private ArrayList<Flagbyte> flags;
    private int size;

    public Flagsystem() {
        flags = new ArrayList<Flagbyte>();
    }

    public int addFlag(boolean status) {
        if (flags.size() <= size / Flagbyte.length)
            flags.add(new Flagbyte());
        setFlag(size, status);
        size++;
        return size-1;
    }

    public void setFlag(int id, boolean status) {
        if (flags.size() > id / Flagbyte.length)
            flags.get(id / Flagbyte.length).set(id % Flagbyte.length, status);
        else
            throw new IllegalArgumentException("ID: " + id + " is not a valid flag!");
    }

    public boolean checkFlag(int id) {
        if (flags.size() > id / Flagbyte.length)
            return flags.get(id / Flagbyte.length).is(id % Flagbyte.length);
        else
            throw new IllegalArgumentException("ID: " + id + " is not a valid flag!");
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int len = in.readInt();
        flags = new ArrayList<Flagbyte>(len);
        for (int i = 0; i < len; i++) {
            flags.add((Flagbyte) in.readObject());
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(flags.size());
        for (Flagbyte flagbyte : flags) {
            out.writeObject(flagbyte);
        }
    }

}
