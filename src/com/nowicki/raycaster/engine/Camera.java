package com.nowicki.raycaster.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.nowicki.raycaster.display.RaycasterDisplay;

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

	// default factors for camera movement
	public final double MOVE_SPEED = 0.08;
	public final double RUN_SPEED = MOVE_SPEED * 1.5;
	public final double ROTATION_SPEED = 0.040;
	
	// actual camera movement factor (should be updated basing on frame time)
	private double movingSpeed = MOVE_SPEED;
	private double rotatingSpeed = ROTATION_SPEED;
	
	
	private boolean rotatingLeft, rotatingRight, movingForward, movingBackward;
	private RaycasterDisplay display;

	public Camera(double xPos, double yPos, RaycasterDisplay display) {
		this.xPos = xPos;
		this.yPos = yPos;
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
		case KeyEvent.VK_Q:
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

	public void update(int[][] map, double frameTime) {
		
		if (Settings.debug) {
			System.out.println("Pos ("+xPos+","+yPos+") Dir ("+xDir+","+yDir+") l r u d "+rotatingLeft+" "+rotatingRight+" "+movingForward+" "+movingBackward);
		}
		
		double movementAmount = movingSpeed * frameTime;
		double rotationAmount = rotatingSpeed * frameTime;
		
		if (movingForward) {
			if (map[(int) (xPos + xDir * movementAmount)][(int) yPos] == 0)
				xPos += xDir * movementAmount;
			if (map[(int) xPos][(int) (yPos + yDir * movementAmount)] == 0)
				yPos += yDir * movementAmount;
		}
		if (movingBackward) {
			if (map[(int) (xPos - xDir * movementAmount)][(int) yPos] == 0)
				xPos -= xDir * movementAmount;
			if (map[(int) xPos][(int) (yPos - yDir * movementAmount)] == 0)
				yPos -= yDir * movementAmount;
		}
		
		if (rotatingRight) {
			rotateZ(-rotationAmount);
		}
		if (rotatingLeft) {
			rotateZ(rotationAmount);
		}
	}
	
	/**
	 * Multuply by rotation matrix
	 * 
	 * [ cos(angle) -sin(angle) ]
	 * [ sin(angle)  cos(angle) ]
	 * 
	 * @param angle
	 */
	public void rotateZ(double angle) {
		double oldxDir = xDir;
		xDir = xDir * Math.cos(angle) - yDir * Math.sin(angle);
		yDir = oldxDir * Math.sin(angle) + yDir * Math.cos(angle);
		double oldxPlane = xPlane;
		xPlane = xPlane * Math.cos(angle) - yPlane * Math.sin(angle);
		yPlane = oldxPlane * Math.sin(angle) + yPlane * Math.cos(angle);
	}

}
