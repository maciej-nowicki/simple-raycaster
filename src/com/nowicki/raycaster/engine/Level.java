package com.nowicki.raycaster.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Level {

	public static int MAP_WIDTH = 20;
	public static int MAP_HEIGHT = 20;
	
	public static final int EMPTY = 0;
	public static final int WALL = 1;

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
		map = new int[MAP_WIDTH][MAP_HEIGHT];
		for (int y=0; y<MAP_HEIGHT; y++) {
			for (int x=0; x<MAP_WIDTH; x++) {
				if (x == 0 || y == 0 || x == MAP_WIDTH-1 || y == MAP_HEIGHT - 1) {
					map[x][y] = WALL;
				}
				else {
					map[x][y] = EMPTY;
				}
			}
		}
	}

	public boolean isObstacle(int x, int y) {
		return map[x][y] != EMPTY;
	}
}
