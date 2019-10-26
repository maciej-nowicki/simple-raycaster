package com.nowicki.raycaster.engine.shader;

import java.awt.Color;

import com.nowicki.raycaster.engine.GraphicsHelper;

public class RainShader extends AbstractShader {
	
	public static final Color RAIN_COLOR = Color.LIGHT_GRAY;
	public static final int MAX_DROP_LENGTH = 25;
	public static final double RAIN_DROP_PROBABILITY = 0.001;
	
	@Override
	public void apply(int[] buffer, int width, int height) {
		for (int i=0; i<buffer.length; i++) {
			if (Math.random() < RAIN_DROP_PROBABILITY) {
				
				int dropLenght = (int) (Math.random() * MAX_DROP_LENGTH);
				double dropOpacity = Math.random() / 2;
			
				for (int j=0; j<dropLenght; j++) {
					int ptr = i + (j*width);
					if (ptr < buffer.length) {
						buffer[ptr] = GraphicsHelper.mixColors(new Color(buffer[ptr]), RAIN_COLOR, dropOpacity);
					}
				}
			}
		}
	}

}
