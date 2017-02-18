package com.nowicki.raycaster.engine.shader;

import java.util.Arrays;

import com.nowicki.raycaster.engine.GraphicsHelper;

public class MotionBlurShader extends AbstractShader {

	private int [] historyBuffer = null;
	
	public MotionBlurShader(boolean enabled) {
		super(enabled);
	}

	@Override
	public void apply(int[] buffer, int width, int height) {
		boolean historyExists = historyBuffer != null;
		int [] bufferCopy = Arrays.copyOf(buffer, buffer.length);
		if (historyExists) {
			for (int i=0; i<buffer.length; i++) {
				buffer[i] = GraphicsHelper.mixColors(buffer[i], historyBuffer[i], 0.3);
			}
		}
		historyBuffer = bufferCopy;
	}

}
