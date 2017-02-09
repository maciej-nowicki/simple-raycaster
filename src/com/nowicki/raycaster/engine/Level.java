package com.nowicki.raycaster.engine;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nowicki.raycaster.engine.Element.Entry;
import com.nowicki.raycaster.engine.Element.EntryType;
import com.nowicki.raycaster.engine.Light.LightLocation;

public class Level {

	public static int DEFAULT_MAP_WIDTH = 20;
	public static int DEFAULT_MAP_HEIGHT = 20;
	
	private Element[][][] map;
	private List<Sprite> sprites = new ArrayList<>();
	private List<Light> lights = new ArrayList<>();
	
	private Texture sky;
	private int width;
	private int height;
	
	public Level(String filename, Map<Entry, Texture> textures) {
		try {
			sky = textures.get(Entry.SKY);
			
			String content = new String(Files.readAllBytes(Paths.get(filename)));
			String [] lines = content.split("\n");
			height = lines.length;
			for (int j=0; j<height; j++) {
				String [] entries = lines[j].split(",");
				width = entries.length;
				if (map == null) {
					map = new Element[Engine.FLOORS][width][height];
				}
				for (int i=0; i<width; i++) {
	
					
					String entryId = entries[i];
					String [] floorEntries = entryId.split(";");
					for (int f=0; f<Engine.FLOORS; f++) {
						
						Set<Entry> entriesSet = new HashSet<>();
						if (floorEntries.length > f) {
							String floorEntryId = floorEntries[f];
							for (int p=0; p<floorEntryId.length(); p++) {
								Entry entry = Entry.fromValue(floorEntryId.charAt(p));
								if (entry.getType() == EntryType.SPRITE) {
									addSprite(textures.get(entry), i, j);
								} else if (entry.getType() == EntryType.LIGHT) {
									addLight(entry, i, j);
								}
								else {
									entriesSet.add(entry);
								}
							}
							
							map[f][i][j] = new Element(entriesSet, textures);
						}
						else {
							entriesSet.add(Entry.EMPTY);
							map[f][i][j] = new Element(entriesSet, textures);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addLight(Entry entry, int i, int j) {
		Light light = new Light(i + 0.5, j + 0.5, new Color(entry.getColor(1)));
		
		if (entry.hasProperty("location")) {
			switch (entry.getProperty("location")) {
			case "wall":
				light.setLocation(LightLocation.WALL);
				break;
			case "floor":
				light.setLocation(LightLocation.FLOOR);
				break;
			case "all":
				light.setLocation(LightLocation.ALL);
				break;
			}
		}
		
		if (entry.hasProperty("radius")) {
			light.setRadius(entry.getDoubleProperty("radius"));
		}
		
		if (entry.hasProperty("intensity")) {
			light.setIntensity(entry.getDoubleProperty("intensity"));
		}
		
		lights.add(light);
	}
	
	private void addSprite(Texture texture, int i, int j) {
		Sprite sprite = new Sprite(i + 0.5, j + 0.5, texture);
		sprites.add(sprite);
	}
	
	public Element[][][] getMap() {
		return map;
	}
	
	public List<Sprite> getSprites() {
		return sprites;
	}
	
	public List<Light> getLights() {
		return lights;
	}

	public boolean isWall(int floor, int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return true;
		}
		return map[floor][x][y].isWall();
	}

	public boolean isObstacle(int x, int y) {
		return map[0][x][y].isObstacle();
	}
	
	public Element getElement(int floor, int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return null;
		}
		return map[floor][x][y];
	}
	
	public Texture getSkyTexture() {
		return sky;
	}
}
