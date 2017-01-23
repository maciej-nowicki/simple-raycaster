package com.nowicki.raycaster.engine;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Texture {

	private final int [] pixels;
	private final int size;
	private final BufferedImage image;
	
	public Texture(String filename) throws IOException {
		this.image = ImageIO.read(new File(filename));
		this.pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		this.size = image.getWidth();
	}
	
	public int getPixel(int u, int v) {
		return pixels[v * size + u];
	}
	
	public int [] getPixels() {
		return pixels;
	}
	
	public int [] getPixelsResized(int width, int height) {
		Image imageResized = image.getScaledInstance(width, height, Image.SCALE_FAST);
		BufferedImage bufferedImageResized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		bufferedImageResized.getGraphics().drawImage(imageResized, 0, 0 , null);
		return bufferedImageResized.getRGB(0,  0, width, height, null, 0, width);
	}
	
	public int getSize() {
		return size;
	}
}
