package org.jcing.windowframework;

public class Stringmanager {
	protected StringBuffer string;
	protected int markedStart;
	protected int markedEnd;
	protected int pos;
	boolean marked;

	public Stringmanager() {
		string = new StringBuffer();
		markedStart = 0;
		marked = false;
		markedEnd = 0;

	}

	public void deleteCharacter() {
		if (marked)
			string.delete(markedStart, markedEnd);
		else
			string.delete(pos - 1, pos);
	}

	public void addString(String letter) {
		string.insert(pos, letter);
	}
}
