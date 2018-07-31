package de.jcing.operator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.jcing.options.Flagbyte;
import de.jcing.options.Flagsystem;
import de.jcing.options.JTable;

public class State implements Externalizable {

	public static final int FLAGS = 0;
	public static final int INTEGERS = 1;
	public static final int DOUBLES = 2;
	public static final int STRINGS = 3;

	private Flagbyte usedUtils;
	private Flagsystem flags;
	private JTable<Integer> ints;
	private JTable<Double> doubles;
	private JTable<String> strings;

	public State() {
		usedUtils = new Flagbyte();
	}

	public void enableUtillity(int utillity) {
		if (!usedUtils.is(utillity)) {
			usedUtils.set(utillity, true);
			switch (utillity) {
			case FLAGS:
				flags = new Flagsystem();
				break;
			case INTEGERS:
				ints = new JTable<Integer>();
				break;
			case DOUBLES:
				doubles = new JTable<Double>();
				break;
			case STRINGS:
				strings = new JTable<String>();
				break;
			}
		}
	}

	public boolean isEnabled(int utillity) {
		return usedUtils.is(utillity);
	}

	public Flagsystem getFlags() {
		return flags;
	}

	public JTable<Integer> getInts() {
		return ints;
	}

	public JTable<Double> getDoubles() {
		return doubles;
	}

	public JTable<String> getStrings() {
		return strings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		usedUtils = (Flagbyte) in.readObject();
		if (usedUtils.is(0))
			flags = (Flagsystem) in.readObject();
		if (usedUtils.is(1))
			ints = (JTable<Integer>) in.readObject();
		if (usedUtils.is(2))
			doubles = (JTable<Double>) in.readObject();
		if (usedUtils.is(3))
			strings = (JTable<String>) in.readObject();

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(usedUtils);
		if (usedUtils.is(0))
			out.writeObject(flags);
		if (usedUtils.is(1))
			out.writeObject(ints);
		if (usedUtils.is(2))
			out.writeObject(doubles);
		if (usedUtils.is(3))
			out.writeObject(strings);
	}

}
