package org.jcing.windowframework;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class TextField extends Component {

	protected int fontSize = 14;
	protected Font font = new Font("Verdana", Font.PLAIN, fontSize);

	protected StringBuffer text;

	protected int xPadding, yPadding;
	protected Color marked = Color.blue.darker().darker();
	protected Color markedText = Color.white.darker();

	public TextField(int x, int y, String str) {
		super(x, y, 14 * str.length() + 10, 14 + 10);
		text = new StringBuffer();
		text.append(str);
		height = fontSize + 10;
		width = (int) (img.getGraphics().getFontMetrics(font).stringWidth(str) + 10);
		xPadding = 5;
		yPadding = 5;
	}

	@Override
	public void paint(Graphics g) {
		g.setFont(font);
		g.setColor(marked);
		g.fillRect(xPadding - 1, yPadding - 1, width - 2 * xPadding + 2, height - 2 * yPadding + 2);
		g.setColor(markedText);
		g.drawString(text.toString(), xPadding, height / 2 + yPadding);
	}

}
