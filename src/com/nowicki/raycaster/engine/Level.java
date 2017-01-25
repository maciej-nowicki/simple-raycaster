package com.nowicki.raycaster.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Level {

	public static int DEFAULT_MAP_WIDTH = 20;
	public static int DEFAULT_MAP_HEIGHT = 20;
	
	private Element[][] map;
	private List<Sprite> sprites = new ArrayList<>();
	
	public Level(String filename, Map<Element, Texture> textures) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)));
			String [] lines = content.split("\n");
			for (int j=0; j<lines.length; j++) {
				String [] entries = lines[j].split(",");
				if (map == null) {
					map = new Element[entries.length][lines.length];
				}
				for (int i=0; i<entries.length; i++) {
					String entry = entries[i];
					for (int p=0; p<entry.length(); p++) {
						Element element = Element.fromValue(entry.charAt(p));
						if (!element.isSprite()) {
							map[i][j] = element;
						} else {
							Sprite sprite = new Sprite(i + 0.5, j + 0.5, textures.get(element));
							sprites.add(sprite);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Element[][] getMap() {
		return map;
	}
	
	public List<Sprite> getSprites() {
		return sprites;
	}
	
	public boolean isWall(int x, int y) {
		return map[x][y].isWall();
	}

	public boolean isObstacle(int x, int y) {
		return map[x][y].isObstacle();
	}
	
	public Element getElement(int x, int y) {
		return map[x][y];
	}
}
