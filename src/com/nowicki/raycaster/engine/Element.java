package com.nowicki.raycaster.engine;

import java.awt.Color;

public enum Element {

	EMPTY(0, null), WALL_1(1, new Color(0, 200, 0)), WALL_2(2, new Color(0, 0, 200)), WALL_3(3, new Color(57, 140, 66)), WALL_WOOD(4, Color.YELLOW), BARREL(9, null);

	private int value;
	private int color1;
	private int color2;

	private Element(int value, Color color) {
		this.value = value;
		if (color != null) {
			color1 = color.getRGB();
			color2 = new Color((int) (color.getRed() * 0.9), (int) (color.getGreen() * 0.9),
					(int) (color.getBlue() * 0.9)).getRGB();
		}
	}
	
	public int getValue() {
		return value;
	}

	public int getColor1AsRGB() {
		return color1;
	}

	public int getColor2AsRGB() {
		return color2;
	}

	public static Element fromValue(int value) {
		for (Element element : values()) {
			if (value == element.value) {
				return element;
			}
		}
		throw new IllegalArgumentException();
	}

}
