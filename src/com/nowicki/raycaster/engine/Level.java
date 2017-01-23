package com.nowicki.raycaster.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Level {

	public static int DEFAULT_MAP_WIDTH = 20;
	public static int DEFAULT_MAP_HEIGHT = 20;
	
	private Element[][] map;
	private List<Sprite> sprites = new ArrayList<>();
	
	public Level() {
		initDummyMap();
	}

	public Level(String filename) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)));
			String [] lines = content.split("\n");
			for (int j=0; j<lines.length; j++) {
				String [] entries = lines[j].split(",");
				if (map == null) {
					map = new Element[entries.length][lines.length];
				}
				for (int i=0; i<entries.length; i++) {
					map[i][j] = Element.fromValue(Integer.parseInt(entries[i]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			initDummyMap();
		}
	}
	
	private void initDummyMap() {
		map = new Element[DEFAULT_MAP_WIDTH][DEFAULT_MAP_HEIGHT];
		for (int y=0; y<DEFAULT_MAP_HEIGHT; y++) {
			for (int x=0; x<DEFAULT_MAP_WIDTH; x++) {
				if (x == 0 || y == 0 || x == DEFAULT_MAP_WIDTH-1 || y == DEFAULT_MAP_HEIGHT - 1) {
					map[x][y] = Element.WALL_1;
				}
				else {
					map[x][y] = Element.EMPTY;
				}
			}
		}
	}
	
	public Element[][] getMap() {
		return map;
	}
	
	public List<Sprite> getSprites() {
		return sprites;
	}

	public boolean isObstacle(int x, int y) {
		return map[x][y].isObstacle();
	}
	
	public Element getElement(int x, int y) {
		return map[x][y];
	}
}
