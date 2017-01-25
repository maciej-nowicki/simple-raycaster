package com.nowicki.raycaster.engine;

import java.awt.Color;

public enum Element {

	// block elements
	EMPTY('0', false, false, false, null), 
	INACCESSIBLE('i', true, false, false, null), 
	WALL_1('1', true, true, false, new Color(0, 200, 0)), 
	WALL_2('2', true, true, false, new Color(0, 0, 200)), 
	WALL_3('3', true, true, false, new Color(57, 140, 66)), 
	WALL_WOOD('4', true, true, false, Color.YELLOW), 
	
	// sprites
	BARREL('b', false, false, true, null),
	PILLAR('p', false, false, true, null),
	
	// special
	FLOOR('-', true, true, false, null), 
	CEILING('-', true, true, false, null);

	
	private char id;
	private boolean obstacle;
	private boolean solid;
	private boolean sprite;
	private int color;
	private int colorDarker;
	private Texture texture;
	
	private Element(char id, boolean obstacle, boolean solid, boolean sprite, Color color) {
		this.id = id;
		this.obstacle = obstacle;
		this.solid = solid;
		this.sprite = sprite;
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
		return solid;
	}
	
	public boolean isObstacle() {
		return obstacle;
	}
	
	public boolean isSprite() {
		return sprite;
	}
	
	public static Element fromValue(char id) {
		for (Element element : values()) {
			if (id == element.id) {
				return element;
			}
		}
		return null;
	}
	


}
