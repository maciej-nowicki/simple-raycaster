package com.nowicki.raycaster.engine;

public class Level {

	public static final int MAP_WIDTH = 20;
	public static final int MAP_HEIGHT = 20;
	
	public static final int EMPTY = 0;
	public static final int WALL = 1;

	private int[][] map = new int[MAP_WIDTH][MAP_HEIGHT];
	
	public Level() {
		initDummyMap();
	}
	
	public int[][] getMap() {
		return map;
	}
	
	private void initDummyMap() {
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
