package com.nowicki.raycaster.engine;

import java.awt.Color;

public class Light {

	// coordinages in level position
	double xPosition;
	double yPosition;

	// light color
	int color;
	
	// light radius in level coordinates
	double radius = 0.7;
	
	// lighht center intensity
	double intensity = 0.8;

	public Light(double xPosition, double yPosition, Color color) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.color = color.getRGB();
	}
	
	public int getColor() {
		return color;
	}
	
	public double getIntensity(double x, double y) {
		double distance = MathHelper.distanceBetweenPoints(x, y, xPosition, yPosition);
		if (distance > radius) {
			return 0;
		}
		return intensity * ((radius - distance) / radius);
	}
}
