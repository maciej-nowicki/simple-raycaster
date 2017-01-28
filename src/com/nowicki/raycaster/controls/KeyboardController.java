package com.nowicki.raycaster.controls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.nowicki.raycaster.display.RaycasterDisplay;
import com.nowicki.raycaster.engine.Camera;
import com.nowicki.raycaster.engine.Engine;
import com.nowicki.raycaster.engine.Settings;

public class KeyboardController implements KeyListener {

	private Camera camera;
	private Engine engine;
	private RaycasterDisplay display;
	
	public KeyboardController(Engine engine, Camera camera, RaycasterDisplay display) {
		this.engine = engine;
		this.camera = camera;
		this.display = display;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			camera.rotatingLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			camera.rotatingRight = true;
			break;
		case KeyEvent.VK_UP:
			camera.movingForward = true;
			break;
		case KeyEvent.VK_DOWN:
			camera.movingBackward = true;
			break;
		case KeyEvent.VK_SHIFT:
			camera.movingSpeed = Camera.RUN_SPEED;
			break;
		case KeyEvent.VK_Q:
			camera.lookingUp = true;
			break;
		case KeyEvent.VK_Z:
			camera.lookingDown = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			camera.rotatingLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			camera.rotatingRight = false;
			break;
		case KeyEvent.VK_UP:
			camera.movingForward = false;
			break;
		case KeyEvent.VK_DOWN:
			camera.movingBackward = false;
			break;
		case KeyEvent.VK_SHIFT:
			camera.movingSpeed = Camera.MOVE_SPEED;
			break;
		case KeyEvent.VK_Q:
			camera.lookingUp = false;
			break;
		case KeyEvent.VK_Z:
			camera.lookingDown = false;
			break;
		case KeyEvent.VK_A:
			camera.centerLook();
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
}
