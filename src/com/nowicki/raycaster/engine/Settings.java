package com.nowicki.raycaster.engine;

public class Settings {

	enum DrawMode {
		NONE, SOLID, SHADED, TEXTURED;
	}
	
	public static boolean debug = false;
	public static DrawMode floors = DrawMode.SOLID;
	
	public static void toggleFloor() {
		if (floors == DrawMode.SOLID) {
			floors = DrawMode.NONE;
		}
		else {
			floors = DrawMode.SOLID;
		}
	}
}
