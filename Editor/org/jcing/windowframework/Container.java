package org.jcing.windowframework;

public interface Container {

	public void add(Component c);

	public void sort();

	public Window getWin();

	public int getWidth();

	public int getHeight();

	public int setWithXBounds(int x, int width);

	public int setWithYBounds(int y, int heigth);

}
