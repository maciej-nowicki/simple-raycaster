package com.nowicki.raycaster.engine;

public class MathHelper {

	/**
	 * Distance between two points in 2d space -> a^2 + b^2 = distance^2
	 * @param ax point1 x
	 * @param ay point1 y
	 * @param bx point2 x
	 * @param by point2 y
	 * @return distance
	 */
	public static double distanceBetweenPoints(double ax, double ay, double bx, double by) {
		return Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
	}
	
	public static double fractional(double d) {
		return d - Math.floor(d);
	}
}
