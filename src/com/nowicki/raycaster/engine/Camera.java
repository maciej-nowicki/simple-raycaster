package com.nowicki.raycaster.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.nowicki.raycaster.display.RaycasterDisplay;

// TODO separate camera class and controller (key listener) responsibilites

public class Camera implements KeyListener {

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
	public final double MOVE_SPEED = 0.08;
	public final double RUN_SPEED = MOVE_SPEED * 1.5;
	public final double ROTATION_SPEED = 0.040;
	public final double LOOK_UP_DOWN_SPEED = 0.040;
	public final double LOOK_UP_LIMIT = 0.4;
	public final double LOOK_DOWN_LIMIT = 0.8;
	
	// actual camera movement factor (should be updated basing on frame time)
	private double movingSpeed = MOVE_SPEED;
	private double rotatingSpeed = ROTATION_SPEED;
	private double lookingUpDownSpeed = LOOK_UP_DOWN_SPEED;
	
	// states as boolean - some are mutually exclusive, but some might happen at once
	// impl as separate boolean values is the easiest one
	private boolean rotatingLeft, rotatingRight, movingForward, movingBackward;
	private boolean lookingUp, lookingDown;
	
	private RaycasterDisplay display;
	private Engine engine;

	public Camera(double xPos, double yPos, Engine engine, RaycasterDisplay display) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.engine = engine;
		this.display = display;
	}
	
	public boolean isPlayerMoving() {
		return (movingBackward || movingForward);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			rotatingLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			rotatingRight = true;
			break;
		case KeyEvent.VK_UP:
			movingForward = true;
			break;
		case KeyEvent.VK_DOWN:
			movingBackward = true;
			break;
		case KeyEvent.VK_SHIFT:
			movingSpeed = RUN_SPEED;
			break;
		case KeyEvent.VK_Q:
			lookingUp = true;
			break;
		case KeyEvent.VK_Z:
			lookingDown = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			rotatingLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			rotatingRight = false;
			break;
		case KeyEvent.VK_UP:
			movingForward = false;
			break;
		case KeyEvent.VK_DOWN:
			movingBackward = false;
			break;
		case KeyEvent.VK_SHIFT:
			movingSpeed = MOVE_SPEED;
			break;
		case KeyEvent.VK_Q:
			lookingUp = false;
			break;
		case KeyEvent.VK_Z:
			lookingDown = false;
			break;
		case KeyEvent.VK_A:
			centerLook();
			break;
		case KeyEvent.VK_CONTROL:
			engine.getWeapon().setShooting(true);
			break;
		case KeyEvent.VK_D:
			Settings.debug = !Settings.debug;
			break;
		case KeyEvent.VK_F:
			Settings.toggleFloor();
			break;
		case KeyEvent.VK_W:
			Settings.toggleWalls();
			break;
		case KeyEvent.VK_G:
			Settings.toggleShowWeapon();
			break;
		case KeyEvent.VK_S:
			Settings.toggleWalkingEffect();
			break;
		case KeyEvent.VK_X:
			Settings.toggleSprites();
			break;
		case KeyEvent.VK_T:
			Settings.toggleTextureFiltering();
			break;
		case KeyEvent.VK_ESCAPE:
			display.stop();
			break;
		case KeyEvent.VK_ENTER:
			display.toggleFullscreen();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
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
	
	private void centerLook() {
		yShear = 0;
	}

}
