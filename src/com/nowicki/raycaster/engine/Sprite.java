package com.nowicki.raycaster.engine;

public class Sprite {

	// coordinages in level position
	double xPosition;
	double yPosition;
	
	// distance to current camera position
	double distanceToCamera;
	
	double cameraWallDistanceFactor;

	// coordinates transformed by the camera view
	double xTransformed;
	double yTransformed;

	private Texture texture;
	
	public Sprite(double xPosition, double yPosition, Texture texture) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.texture = texture;
	}

	public Texture getTexture() {
		return texture;
	}

}
