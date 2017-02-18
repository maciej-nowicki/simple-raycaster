package com.nowicki.raycaster.engine.shader;

import java.util.Arrays;

import com.nowicki.raycaster.engine.GraphicsHelper;

public class MotionBlurShader extends AbstarctShader {

	private int [] historyBuffer = null;
	
	@Override
	public void apply(int[] buffer, int width, int height) {
		boolean enabled = historyBuffer != null;
		int [] bufferCopy = Arrays.copyOf(buffer, buffer.length);
		if (enabled) {
			for (int i=0; i<buffer.length; i++) {
				buffer[i] = GraphicsHelper.mixColors(buffer[i], historyBuffer[i], 0.3);
			}
		}
		historyBuffer = bufferCopy;
	}

}
