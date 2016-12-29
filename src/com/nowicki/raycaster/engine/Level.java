package com.nowicki.raycaster.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Level {

	public static int DEFAULT_MAP_WIDTH = 20;
	public static int DEFAULT_MAP_HEIGHT = 20;
	
	private int[][] map;
	
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
					map = new int[entries.length][lines.length];
				}
				for (int i=0; i<entries.length; i++) {
					map[i][j] = Integer.parseInt(entries[i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			initDummyMap();
		}
	}
	
	public int[][] getMap() {
		return map;
	}
	
	private void initDummyMap() {
		map = new int[DEFAULT_MAP_WIDTH][DEFAULT_MAP_HEIGHT];
		for (int y=0; y<DEFAULT_MAP_HEIGHT; y++) {
			for (int x=0; x<DEFAULT_MAP_WIDTH; x++) {
				if (x == 0 || y == 0 || x == DEFAULT_MAP_WIDTH-1 || y == DEFAULT_MAP_HEIGHT - 1) {
					map[x][y] = Element.WALL_1.getValue();
				}
				else {
					map[x][y] = Element.EMPTY.getValue();
				}
			}
		}
	}

	public boolean isObstacle(int x, int y) {
		return map[x][y] != Element.EMPTY.getValue();
	}
	
	public Element getElement(int x, int y) {
		return Element.fromValue(map[x][y]);
	}
}
