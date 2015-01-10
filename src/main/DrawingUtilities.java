package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

public class DrawingUtilities {
	public static void drawMissingTexture(Graphics g, int x, int y, int width, int height) {
		Color oldColor = g.getColor();
		g.setColor(Color.PINK);
		g.fillRect(x, y, width / 2, height / 2);
		g.fillRect(x + (width / 2), y + (height / 2), width / 2, height / 2);
		g.setColor(Color.BLACK);
		g.fillRect(x + (width / 2), y, width / 2, height / 2);
		g.fillRect(x, y + (height / 2), width / 2, height / 2);
		g.setColor(oldColor);
	}
	
	public static void attemptImageDraw(Graphics g, Image img, int x, int y, int width, int height, ImageObserver observer) {
		if (img != null) {
			g.drawImage(img, x, y, width, height, null);
		} else {
			drawMissingTexture(g, x, y, width, height);
		}
	}
}