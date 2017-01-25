package com.nowicki.raycaster.engine;

import java.awt.Color;

public enum Element {

	EMPTY('0', null), 
	WALL_1('1', new Color(0, 200, 0)), WALL_2('2', new Color(0, 0, 200)), WALL_3('3',new Color(57, 140, 66)), 
	WALL_WOOD('4', Color.YELLOW), BARREL('b', null), INACCESSIBLE('i', null), FLOOR('-', null), CEILING('-', null);

	private char id;
	private int color;
	private int colorDarker;
	private Texture texture;
	
	private Element(char id, Color color) {
		this.id = id;
		if (color != null) {
			this.color = color.getRGB();
			this.colorDarker = new Color((int) (color.getRed() * 0.9), (int) (color.getGreen() * 0.9),
					(int) (color.getBlue() * 0.9)).getRGB();
		}
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public int getColor(int side) {
		return (side == 1) ? color : colorDarker;
	}
	
	public boolean isWall() {
		return this != EMPTY && this != INACCESSIBLE;
	}
	
	public boolean isObstacle() {
		return this != EMPTY;
	}
	
	public static Element fromValue(char id) {
		for (Element element : values()) {
			if (id == element.id) {
				return element;
			}
		}
		throw new IllegalArgumentException("Illegal element id: '" + id + "'");
	}

}
