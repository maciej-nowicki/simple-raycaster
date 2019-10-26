package com.nowicki.raycaster.engine;

// TODO separate camera class and controller (key listener) responsibilites

public class Camera {

	// position on the map in double precision coordinates
	protected double xPos;
	protected double yPos;
	
	// view direction <-1, +1> x & y must be perpendicular
	protected double xDir = -1;
	protected double yDir = 0;
	
	// camera plane
	protected double xPlane = 0;
	protected double yPlane = 0.66;
	
	// y-shearing (look up-down) amount 
	protected double yShear = 0;

	// default factors for camera movement
	public static final double MOVE_SPEED = 0.08;
	public static final double RUN_SPEED = MOVE_SPEED * 1.5;
	public static final double ROTATION_SPEED = 0.040;
	public static final double LOOK_UP_DOWN_SPEED = 0.040;
	public static final double LOOK_UP_LIMIT = 0.4;
	public static final double LOOK_DOWN_LIMIT = 0.8;
	
	// actual camera movement factor (should be updated basing on frame time)
	public double movingSpeed = MOVE_SPEED;
	private double rotatingSpeed = ROTATION_SPEED;
	private double lookingUpDownSpeed = LOOK_UP_DOWN_SPEED;
	
	// states as boolean - some are mutually exclusive, but some might happen at once
	// impl as separate boolean values is the easiest one
	public boolean rotatingLeft, rotatingRight, movingForward, movingBackward;
	public boolean lookingUp, lookingDown;
	
	public Camera(double xPos, double yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
	public boolean isPlayerMoving() {
		return (movingBackward || movingForward);
	}

	public void update(Level level, double frameTime) {
		
		double movementAmount = movingSpeed * frameTime;
		double rotationAmount = rotatingSpeed * frameTime;
		double upDownAmout = lookingUpDownSpeed * frameTime;
		
		if (movingForward) {
			if (!level.isObstacle((int) (xPos + xDir * movementAmount), (int) yPos)) {
				xPos += xDir * movementAmount;
			}
			if (!level.isObstacle((int) xPos, (int) (yPos + yDir * movementAmount))) {
				yPos += yDir * movementAmount;
			}
		}
		if (movingBackward) {
			if (!level.isObstacle((int) (xPos - xDir * movementAmount), (int) yPos)) {
				xPos -= xDir * movementAmount;
			}
			if (!level.isObstacle((int) xPos, (int) (yPos - yDir * movementAmount))) {
				yPos -= yDir * movementAmount;
		}
		}
		
		if (rotatingRight) {
			rotateZ(-rotationAmount);
		}
		else if (rotatingLeft) {
			rotateZ(rotationAmount);
		}
		
		if (lookingUp) {
			if (yShear < LOOK_UP_LIMIT) {
				yShear += upDownAmout;
			}
		}
		else if (lookingDown) {
			if (-yShear < LOOK_DOWN_LIMIT) {
				yShear -= upDownAmout;
			}
		}
	}
	
	/**
	 * Multuply by rotation matrix
	 * <pre>
	 * [ cos(angle) -sin(angle) ]
	 * [ sin(angle)  cos(angle) ]
	 * </prer>
	 * @param angle
	 */
	private void rotateZ(double angle) {
		double oldxDir = xDir;
		xDir = xDir * Math.cos(angle) - yDir * Math.sin(angle);
		yDir = oldxDir * Math.sin(angle) + yDir * Math.cos(angle);
		double oldxPlane = xPlane;
		xPlane = xPlane * Math.cos(angle) - yPlane * Math.sin(angle);
		yPlane = oldxPlane * Math.sin(angle) + yPlane * Math.cos(angle);
	}
	
	public void centerLook() {
		yShear = 0;
	}

}
