package com.nowicki.raycaster.engine;

import java.awt.Color;

public class Settings {

	enum DrawMode {
		NONE, SOLID, TEXTURED;
		
		public DrawMode next() {
	        return values()[(ordinal()+1) % values().length];
	    }
	}
	
	enum SkyMode {
		NONE, SIMPLE, SIMPLE_STRETCHED, SPHERE;
		
		public SkyMode next() {
	        return values()[(ordinal()+1) % values().length];
	    }
	}
	
	public static final int CEILING_COLOUR = new Color(80, 80, 80).getRGB();
	public static final int FLOOR_COLOUR = new Color(50, 50, 50).getRGB();
	
	public static final double WALKING_EFFECT_SCALE = 0.015;
	
	public static final int FOG_DISTANCE = 15;
	
	public static boolean debug = false;
	public static boolean fullScreen = false;
	public static boolean walkingEffect = false;
	public static boolean sprites = false;
	public static boolean showWeapon = true;
	public static boolean textureFiltering = false;
	public static boolean shading = true;
	public static DrawMode floors = DrawMode.TEXTURED;
	public static DrawMode walls = DrawMode.TEXTURED;
	public static SkyMode sky = SkyMode.SIMPLE;
	
	public static void toggleFullscreen() {
		fullScreen = !fullScreen;
	}
	
	public static void toggleTextureFiltering() {
		textureFiltering = !textureFiltering;
	}
	
	public static void toggleSprites() {
		sprites = !sprites;
	}
	
	public static void toggleShading() {
		shading = !shading;
	}
	
	public static void toggleWalkingEffect() {
		walkingEffect = !walkingEffect;
	}
	
	public static void toggleShowWeapon() {
		showWeapon = !showWeapon;
	}
	
	public static void toggleFloor() {
		floors = floors.next();
	}
	
	public static void toggleWalls() {
		walls = walls.next();
	}
	
	public static void toggleSky() {
		sky = sky.next();
	}
}
