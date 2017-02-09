package com.nowicki.raycaster.engine;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Element {

	public enum EntryType {
		EMPTY, INACCESSIBLE, WALL, FLOOR, CEILING, SPRITE, SKY, LIGHT;
	};
	
	public enum Entry {
		
		EMPTY('0', EntryType.EMPTY, true, null),
		INACCESSIBLE('i', EntryType.INACCESSIBLE, false, null),
		
		// floors and ceilings 
		DEFAULT_FLOOR('-', EntryType.FLOOR, true, null), 
		DEFAULT_CEILING('-', EntryType.CEILING, true, null),
		INVISIBLE_FLOOR('?', EntryType.FLOOR, false, null), 
		INVISIBLE_CEILING('!', EntryType.CEILING, false, null), 
		RED_CARPET('c', EntryType.FLOOR, true, null),
		GRASS('g', EntryType.FLOOR, true, null),

		// wall elements
		WALL_1_GREYSTONE('1', EntryType.WALL, true, new Color(0, 200, 0)), 
		WALL_2_BLUESTONE('2', EntryType.WALL, true, new Color(0, 0, 200)), 
		WALL_3_COLORSTONE('3', EntryType.WALL, true, new Color(57, 140, 66)), 
		WALL_4_WOOD('4', EntryType.WALL, true, Color.YELLOW), 
		WALL_5_BRICK('5', EntryType.WALL, true, Color.RED),
		WALL_6_BRICK_WITH_EAGLE('6', EntryType.WALL, true, Color.RED),
		
		// sprites
		BARREL('b', EntryType.SPRITE, true, null),
		PILLAR('p', EntryType.SPRITE, true, null),
		CEILING_LAMP('l', EntryType.SPRITE, true, null),
		
		// lights
		FLOOR_SPOTLIGHT('*', EntryType.LIGHT, true, Color.WHITE, "location=floor"),
		WALL_SPOTLIGHT('#', EntryType.LIGHT, true, Color.WHITE, "location=wall;radius=0.4"),
		WALL_SPOTLIGHT_INTENSE('%', EntryType.LIGHT, true, Color.WHITE, "location=wall;radius=0.4;intensity=1.2"),
		WALL_SPOTLIGHT_LARGE('$', EntryType.LIGHT, true, Color.WHITE, "location=wall;radius=0.6"),
		ALL_SPOTLIGHT_LARGE('@', EntryType.LIGHT, true, Color.WHITE, "location=all;radius=1.8"),
		
		// special
		SKY('-', EntryType.SKY, true, null);
		
		private char id;
		private EntryType type;
		private boolean visible;
		private int color;
		private int colorDarker;
		private Map<String, String> properties = new HashMap<>();
		
		private Entry(char id, EntryType type, boolean visible, Color color) {
			this.id = id;
			this.type = type;
			this.visible = visible;
			if (color != null) {
				this.color = color.getRGB();
				this.colorDarker = new Color((int) (color.getRed() * 0.9), (int) (color.getGreen() * 0.9),
						(int) (color.getBlue() * 0.9)).getRGB();
			}
		}
		
		private Entry(char id, EntryType type, boolean visible, Color color, String properties) {
			this(id, type, visible, color);
			if (properties != null) {
				String [] entries = properties.split(";");
				for (String entry : entries) {
					String key = entry.split("=")[0].trim();
					String value = entry.split("=")[1].trim();
					this.properties.put(key, value);
				}
			}
		}

		public EntryType getType() {
			return type;
		}

		public int getColor(int side) {
			return (side == 1) ? color : colorDarker;
		}
		
		public boolean isVisible() {
			return visible;
		}
		
		public boolean hasProperty(String propertyName) {
			return properties.containsKey(propertyName);
		}
		
		public String getProperty(String propertyName) {
			return properties.get(propertyName);
		}
		
		public String getProperty(String propertyName, String defaultValue) {
			String value = getProperty(propertyName);
			return (value != null) ? value : defaultValue;
		}
		
		public Double getDoubleProperty(String propertyName) {
			String value = getProperty(propertyName);
			return (value != null) ? Double.valueOf(value) : null;
		}
		
		public Double getDoubleProperty(String propertyName, Double defaultValue) {
			Double value = getDoubleProperty(propertyName);
			return (value != null) ? value : defaultValue;
		}
		
		public static Entry fromValue(char id) {
			for (Entry entry : values()) {
				if (id == entry.id) {
					return entry;
				}
			}
			return null;
		}
		
	};
	
	private boolean obstacle = false;
	private boolean wall = false;
	
	private int color;
	private int colorDarker;
	private Texture texture;
	
	private boolean floorVisible = true;
	private Texture floorTexture;
	
	private boolean ceilingVisible = true;
	private Texture ceilingTexture;

	public Element(Set<Entry> entries, Map<Entry, Texture> textures) {
		
		floorTexture = textures.get(Entry.DEFAULT_FLOOR);
		ceilingTexture = textures.get(Entry.DEFAULT_CEILING);
		
		for (Entry entry : entries) {
			switch (entry.getType()) {
			case INACCESSIBLE:
				obstacle = true;
				break;
			case FLOOR:
				if (textures.containsKey(entry)) {
					floorTexture = textures.get(entry);
				}
				floorVisible = entry.isVisible();
				break;
			case CEILING:
				if (textures.containsKey(entry)) {
					ceilingTexture = textures.get(entry);
				}
				ceilingVisible = entry.isVisible();
				break;
			case WALL:
				obstacle = true;
				wall = true;
				color = entry.getColor(0);
				colorDarker = entry.getColor(1);
				texture = textures.get(entry);
				break;
			default:
			}
		}
	}
	
	public boolean isObstacle() {
		return obstacle;
	}
	
	public boolean isWall() {
		return wall;
	}
	
	public int getColor(int side) {
		return (side == 1) ? color : colorDarker;
	}
	
	public Texture getWallTexture(int side) {
		return texture;
	}
	
	public boolean isFloorVisible() {
		return floorVisible;
	}

	public Texture getFloorTexture() {
		return floorTexture;
	}

	public boolean isCeilingVisible() {
		return ceilingVisible;
	}

	public Texture getCeilingTexture() {
		return ceilingTexture;
	}
	
	


}
