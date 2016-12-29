package com.nowicki.raycaster.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.nowicki.raycaster.display.RaycasterDisplay;

public class Camera implements KeyListener {

	// position
	protected double xPos;
	protected double yPos;
	
	// view direction <-1, +1>
	protected double xDir = -1;
	protected double yDir = 0;
	
	// 
	protected double xPlane = 0;
	protected double yPlane = 0.66;

	public final double MOVE_SPEED = 0.08;
	public final double ROTATION_SPEED = .040;
	
	private boolean movingLeft, movingRight, movingForward, movingBackward;
	private RaycasterDisplay display;

	public Camera(double xPos, double yPos, RaycasterDisplay display) {
		this.xPos = xPos;
		this.yPos = yPos;
		this.display = display;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			movingLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			movingRight = true;
			break;
		case KeyEvent.VK_UP:
			movingForward = true;
			break;
		case KeyEvent.VK_DOWN:
			movingBackward = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			movingLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			movingRight = false;
			break;
		case KeyEvent.VK_UP:
			movingForward = false;
			break;
		case KeyEvent.VK_DOWN:
			movingBackward = false;
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
		case KeyEvent.VK_Q:
			display.stop();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void update(int[][] map) {
		
		if (Settings.debug) {
			System.out.println("Pos ("+xPos+","+yPos+") Dir ("+xDir+","+yDir+") l r u d "+movingLeft+" "+movingRight+" "+movingForward+" "+movingBackward);
		}
		
		if (movingForward) {
			if (map[(int) (xPos + xDir * MOVE_SPEED)][(int) yPos] == 0)
				xPos += xDir * MOVE_SPEED;
			if (map[(int) xPos][(int) (yPos + yDir * MOVE_SPEED)] == 0)
				yPos += yDir * MOVE_SPEED;
		}
		if (movingBackward) {
			if (map[(int) (xPos - xDir * MOVE_SPEED)][(int) yPos] == 0)
				xPos -= xDir * MOVE_SPEED;
			if (map[(int) xPos][(int) (yPos - yDir * MOVE_SPEED)] == 0)
				yPos -= yDir * MOVE_SPEED;
		}
		if (movingRight) {
			double oldxDir = xDir;
			xDir = xDir * Math.cos(-ROTATION_SPEED) - yDir * Math.sin(-ROTATION_SPEED);
			yDir = oldxDir * Math.sin(-ROTATION_SPEED) + yDir * Math.cos(-ROTATION_SPEED);
			double oldxPlane = xPlane;
			xPlane = xPlane * Math.cos(-ROTATION_SPEED) - yPlane * Math.sin(-ROTATION_SPEED);
			yPlane = oldxPlane * Math.sin(-ROTATION_SPEED) + yPlane * Math.cos(-ROTATION_SPEED);
		}
		if (movingLeft) {
			double oldxDir = xDir;
			xDir = xDir * Math.cos(ROTATION_SPEED) - yDir * Math.sin(ROTATION_SPEED);
			yDir = oldxDir * Math.sin(ROTATION_SPEED) + yDir * Math.cos(ROTATION_SPEED);
			double oldxPlane = xPlane;
			xPlane = xPlane * Math.cos(ROTATION_SPEED) - yPlane * Math.sin(ROTATION_SPEED);
			yPlane = oldxPlane * Math.sin(ROTATION_SPEED) + yPlane * Math.cos(ROTATION_SPEED);
		}
	}

}
