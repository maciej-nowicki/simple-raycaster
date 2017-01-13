package com.nowicki.raycaster.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {

	private final int [] pixels;
	private final int size;
	
	public Texture(String filename) throws IOException {
		BufferedImage image = ImageIO.read(new File(filename));
		pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		this.size = image.getWidth();
	}
	
	public int getPixel(int u, int v) {
		return pixels[v * size + u];
	}
	
	public int [] getPixels() {
		return pixels;
	}
	
	public int getSize() {
		return size;
	}
}
