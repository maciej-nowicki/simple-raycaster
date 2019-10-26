package com.nowicki.raycaster.engine;

import java.awt.Color;

public class GraphicsHelper {

	public static int mixColors(int c1, int c2, double ratio) {
		return mixColors(new Color(c1), new Color(c2), ratio);
	}

	public static int mixColors(Color c1, Color c2, double ratio) {
		int r = (int) ((double) c1.getRed() * (1 - ratio) + (double) c2.getRed() * ratio);
		int g = (int) ((double) c1.getGreen() * (1 - ratio) + (double) c2.getGreen() * ratio);
		int b = (int) ((double) c1.getBlue() * (1 - ratio) + (double) c2.getBlue() * ratio);
		return new Color(r, g, b).getRGB();
	}

	public static int fadeToBlack(int color, double current, double max) {
		if (current < 0) {
			return color;
		}
		if (current >= max) {
			return Color.BLACK.getRGB();
		}
		Color c = new Color(color);
		double amount = (max - current) / max;
		int r = (int) (c.getRed() * amount);
		int g = (int) (c.getGreen() * amount);
		int b = (int) (c.getBlue() * amount);
		return new Color(r, g, b).getRGB();
	}
}
