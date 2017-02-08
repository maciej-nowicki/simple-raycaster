package com.nowicki.raycaster.engine.shader;

import java.awt.Color;

import com.nowicki.raycaster.engine.GraphicsHelper;

public class StormShader extends AbstarctShader {

	
	public static final double LIGHTNING_PROBABILITY = 0.005;
	public static final Color LIGHTNING_COLOR = Color.WHITE;
	
	public static final int LIGHTNING_EFFECT_LENGTH_IN_FRAMES = 5;
	public static final double [] LIGHTNING_INTENSITY_SEQUENCE = {1.0, 0.8, 1.0, 0.7, 0.4};

	private boolean displayingLightning = false;
	private int frame = 0;
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		displayingLightning = false;
		frame = 0;
	}

	@Override
	public void apply(int[] buffer, int width, int height) {
		
		if (!displayingLightning) {
			if (Math.random() < LIGHTNING_PROBABILITY) {
				displayingLightning = true;
				frame = 0;
			}
		}
		
		if (displayingLightning) {
			for (int i=0; i<buffer.length; i++) {
				buffer[i] = GraphicsHelper.mixColors(new Color(buffer[i]), LIGHTNING_COLOR, LIGHTNING_INTENSITY_SEQUENCE[frame]);
			}
			
			if (++frame == LIGHTNING_EFFECT_LENGTH_IN_FRAMES) {
				displayingLightning = false;
			}
		}
	}

}
