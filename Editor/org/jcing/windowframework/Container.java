package org.jcing.windowframework;

public interface Container {

	public void add(Component c);

	public void sort();

	public Window getWin();

	public int getWidth();

	public int getHeight();

	public int setWithXBounds(int x);
	public int setWithYBounds(int x);

}
