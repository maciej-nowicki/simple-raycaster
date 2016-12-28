package com.nowicki.raycaster;

import com.nowicki.raycaster.display.RaycasterDisplay;
import com.nowicki.raycaster.engine.Engine;

public class RaycasterDemo {

	public static void main(String[] args) {
		Engine engine = new Engine();
		RaycasterDisplay display = new RaycasterDisplay(engine);
		display.runDemo();
	}
}
