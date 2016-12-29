package com.nowicki.raycaster.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Camera implements KeyListener {

	// position
	protected double xPos;
	protected double yPos;
	
	// view direction
	protected double xDir = -1;
	protected double yDir = 0;
	
	// 
	protected double xPlane = 0.0;
	protected double yPlane = 0.66;

	public final double MOVE_SPEED = 0.08;
	public final double ROTATION_SPEED = .045;
	
	private boolean left, right, up, down;

	public Camera() {
		this(Level.MAP_WIDTH / 2, Level.MAP_HEIGHT / 2);
	}

	public Camera(double xPos, double yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		case KeyEvent.VK_UP:
			up = true;
			break;
		case KeyEvent.VK_DOWN:
			down = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		case KeyEvent.VK_UP:
			up = false;
			break;
		case KeyEvent.VK_DOWN:
			down = false;
			break;
		case KeyEvent.VK_D:
			Settings.debug = !Settings.debug;
			break;
		case KeyEvent.VK_F:
			Settings.toggleFloor();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void update(int[][] map) {
		
		if (Settings.debug) {
			System.out.println("Pos ("+xPos+","+yPos+") Dir ("+xDir+","+yDir+") l r u d "+left+" "+right+" "+up+" "+down);
		}
		
		if (up) {
			if (map[(int) (xPos + xDir * MOVE_SPEED)][(int) yPos] == 0)
				xPos += xDir * MOVE_SPEED;
			if (map[(int) xPos][(int) (yPos + yDir * MOVE_SPEED)] == 0)
				yPos += yDir * MOVE_SPEED;
		}
		if (down) {
			if (map[(int) (xPos - xDir * MOVE_SPEED)][(int) yPos] == 0)
				xPos -= xDir * MOVE_SPEED;
			if (map[(int) xPos][(int) (yPos - yDir * MOVE_SPEED)] == 0)
				yPos -= yDir * MOVE_SPEED;
		}
		if (right) {
			double oldxDir = xDir;
			xDir = xDir * Math.cos(-ROTATION_SPEED) - yDir * Math.sin(-ROTATION_SPEED);
			yDir = oldxDir * Math.sin(-ROTATION_SPEED) + yDir * Math.cos(-ROTATION_SPEED);
			double oldxPlane = xPlane;
			xPlane = xPlane * Math.cos(-ROTATION_SPEED) - yPlane * Math.sin(-ROTATION_SPEED);
			yPlane = oldxPlane * Math.sin(-ROTATION_SPEED) + yPlane * Math.cos(-ROTATION_SPEED);
		}
		if (left) {
			double oldxDir = xDir;
			xDir = xDir * Math.cos(ROTATION_SPEED) - yDir * Math.sin(ROTATION_SPEED);
			yDir = oldxDir * Math.sin(ROTATION_SPEED) + yDir * Math.cos(ROTATION_SPEED);
			double oldxPlane = xPlane;
			xPlane = xPlane * Math.cos(ROTATION_SPEED) - yPlane * Math.sin(ROTATION_SPEED);
			yPlane = oldxPlane * Math.sin(ROTATION_SPEED) + yPlane * Math.cos(ROTATION_SPEED);
		}
	}

}
