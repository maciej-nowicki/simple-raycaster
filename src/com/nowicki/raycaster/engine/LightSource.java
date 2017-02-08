package com.nowicki.raycaster.engine;

import java.awt.Color;

public class LightSource {

	// coordinages in level position
	double xPosition;
	double yPosition;

	// light color
	Color color;

	public LightSource(double xPosition, double yPosition, Color color) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.color = color;
	}
}
