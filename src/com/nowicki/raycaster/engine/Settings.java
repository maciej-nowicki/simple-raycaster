package com.nowicki.raycaster.engine;

import java.awt.Color;

public class Settings {

	enum DrawMode {
		NONE, SOLID, SHADED, TEXTURED;
	}
	
	public static final int CEILING_COLOUR = new Color(40, 40, 40).getRGB();
	public static final int FLOOR_COLOUR = new Color(20, 20, 20).getRGB();
	
	public static boolean debug = false;
	public static DrawMode floors = DrawMode.SOLID;
	public static DrawMode walls = DrawMode.SHADED;
	
	public static void toggleFloor() {
		if (floors == DrawMode.SOLID) {
			floors = DrawMode.NONE;
		}
		else {
			floors = DrawMode.SOLID;
		}
	}
	
	public static void toggleWalls() {
		if (walls == DrawMode.SOLID) {
			walls = DrawMode.SHADED;
		}
		else {
			walls = DrawMode.SOLID;
		}
	}
}
