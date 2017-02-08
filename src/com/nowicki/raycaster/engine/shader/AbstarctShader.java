package com.nowicki.raycaster.engine.shader;

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

}
