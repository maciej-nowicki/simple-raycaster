package com.nowicki.raycaster.engine.shader;

import java.awt.Color;
import java.util.Arrays;

public class DistortShader extends AbstractShader {

	int frame = 0;
	
	public DistortShader(boolean enabled) {
		super(enabled);
	}

	@Override
	public void apply(int[] buffer, int width, int height) {
		
		int [] utouched = Arrays.copyOf(buffer, buffer.length);
		for (int column=0; column<width; column++) {
			blitColumn(column, (int) (4.0 * Math.sin((frame*4 + column)/25.0)), width, height, utouched, buffer);
		}
		
		utouched = Arrays.copyOf(buffer, buffer.length);
		for (int row=0; row<height; row++) {
			blitRow(row, (int) (4.0 * Math.sin((frame*2 + row)/25.0)), width, height, utouched, buffer);
		}
		
		blur((int) (Math.random() * 4) + 1, width, height, buffer);
		
		frame++;
	}
	
	private void blitColumn(int x, int delta, int width, int height, int[] source, int[] target) {
		for (int y=0; y<height; y++) {
			if ((y+delta)*width+x > 0 && (y+delta)*width+x < source.length) {
				target[y*width+x] = source[(y+delta)*width+x];
			} else {
				target[y*width+x] = Color.BLACK.getRGB();
			}
		}
	}
	
	private void blitRow(int y, int delta, int width, int height, int[] source, int[] target) {
		for (int x=0; x<width; x++) {
			if (y*width+x+delta > 0 && (y)*width+x+delta < source.length) {
				target[y*width+x] = source[(y)*width+x+delta];
			} else {
				target[y*width+x] = Color.BLACK.getRGB();
			}
		}
	}
	
	private void blur(int radius, int width, int height, int[] buffer) {
		
		if (radius == 0) {
			return;
		}
		
		for (int y=0+radius; y<height-radius; y++) {
			for (int x=0+radius; x<width-radius; x++) {
				int sumR = 0;
				int sumG = 0;
				int sumB = 0;
				int cnt = 0;
				
				for (int v=y-radius; v<y+radius; v++) {
					for (int u=x-radius; u<x+radius; u++) {
						Color c = new Color(buffer[v*width+u]);
						sumR += c.getRed();
						sumG += c.getGreen();
						sumB += c.getBlue();
						cnt ++;
					}
				}
				
				buffer[y*width+x] = new Color(sumR / cnt, sumG / cnt, sumB / cnt).getRGB();
			}
		}
	}

}
