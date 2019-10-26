package com.nowicki.raycaster.engine.shader;

public interface Shader {

	public void setEnabled(boolean enabled);
	
	public boolean isEnabled();
	
	public void apply(int buffer[], int width, int height);
}
