package com.nowicki.raycaster.engine.shader;

public abstract class AbstractShader implements Shader {

	protected boolean enabled;

	public AbstractShader() {
		this(true);
	}
	
	public AbstractShader(boolean enabled) {
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

}
