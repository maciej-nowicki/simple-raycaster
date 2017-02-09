package com.nowicki.raycaster.engine;

import java.awt.Color;

public class Light {
	
	public enum LightLocation {
		FLOOR, WALL, CEILING, ALL;
	}

	// coordinages in level position
	double xPosition;
	double yPosition;

	// light color
	int color;
	
	// location (on what element light is casted)
	LightLocation location;
	
	// light radius in level coordinates
	double radius = 0.7;
	
	// lighht center intensity
	double intensity = 0.8;

	public Light(double xPosition, double yPosition, Color color) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.color = color.getRGB();
	}
	
	public double getIntensity(double x, double y) {
		double distance = MathHelper.distanceBetweenPoints(x, y, xPosition, yPosition);
		if (distance > radius) {
			return 0;
		}
		return intensity * ((radius - distance) / radius);
	}
	
	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public void setLocation(LightLocation location) {
		this.location = location;
	}
	
	public LightLocation getLocation() {
		return location;
	}

	public int getColor() {
		return color;
	}
	
}
