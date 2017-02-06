package com.nowicki.raycaster.engine.shader;

import java.awt.Color;

public abstract class AbstarctShader implements Shader {

	protected boolean enabled;

	public AbstarctShader() {
		this(true);
	}
	
	public AbstarctShader(boolean enabled) {
		setEnabled(enabled);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	protected int mixColors(Color c1, Color c2, double ratio) {
		int r = (int) ((double)c1.getRed() * (1 - ratio) + (double)c2.getRed() * ratio);
		int g = (int) ((double)c1.getGreen() * (1 - ratio) + (double)c2.getGreen() * ratio);
		int b = (int) ((double)c1.getBlue() * (1 - ratio) + (double)c2.getBlue() * ratio);
		return new Color(r,g,b).getRGB();
	}

}
