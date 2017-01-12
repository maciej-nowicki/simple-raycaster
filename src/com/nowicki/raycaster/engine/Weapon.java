package com.nowicki.raycaster.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Weapon {
	
	private int [][] pixels;
	private int [] frameSequence;
	private int [] framesWidth;
	private int frameHeight;
	private int f;
	
	private boolean shooting = false;

	public Weapon(String filename, int[] frameSequence, int... framesEnds) throws IOException {
		this.pixels = new int[framesEnds.length][];
		this.framesWidth = new int[framesEnds.length];
		this.frameSequence = frameSequence;
		this.f = 0;

		BufferedImage image = ImageIO.read(new File(filename));
		this.frameHeight = image.getHeight();
		
		int frameWidth = 0;
		int frameStart = 0;
		for (int i=0; i<pixels.length; i++) {
			frameWidth = framesEnds[i] - frameStart;
			framesWidth[i] = frameWidth;
			pixels[i] = image.getRGB(frameStart, 0, frameWidth, image.getHeight(), null, 0, frameWidth);	
			frameStart = framesEnds[i]-1;
		}
	}
	
	public int[] getFrame() {
		return pixels[frameSequence[f]];
	}
	
	public int getFrameWidth() {
		return framesWidth[frameSequence[f]];
	}

	public int getFrameHeight() {
		return frameHeight;
	}
	
	public void nextFrame() {
		if (f != frameSequence.length -1) {
			f++;
		}
		else {
			f = 0;
			shooting = false;
		}
	}

	public boolean isShooting() {
		return shooting;
	}

	public void setShooting(boolean shooting) {
		this.shooting = shooting;
	}
	
	
}
