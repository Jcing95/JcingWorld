package org.jcing.windowframework;

import java.awt.Font;
import java.awt.Graphics;

public class TextField extends Component {

	protected int fontSize = 14;
	protected Font font = new Font("Verdana", Font.PLAIN, fontSize);

	protected StringBuffer text;

	protected int xPadding, yPadding;

	public TextField(int x, int y, String str) {
		super(x, y, 14 * str.length() + 10, 14 + 10);
		text = new StringBuffer();
		text.append(str);
		height = fontSize + 10;
		width = (int) (img.getGraphics().getFontMetrics(font).stringWidth(str) + 10);
		xPadding = 5;
		yPadding = height / 2 + 5;
	}

	@Override
	public void paint(Graphics g) {
		g.setFont(font);
		g.drawString(text.toString(), xPadding, yPadding);
	}


}
