package com.nowicki.raycaster.engine;

import java.awt.Color;
import java.util.Arrays;

import com.nowicki.raycaster.engine.Settings.DrawMode;

public class Engine {

	private int[] buffer;
	private Level level;

	private int width;
	private int height;
	
	private int backgroundColor;
	private int colorA;
	private int colorB;

	public Engine(int widht, int height) {
		this.width = widht;
		this.height = height;
	
		backgroundColor = Color.BLACK.getRGB();
		colorA = new Color(0, 200, 0).getRGB();
		colorB = new Color(0, 180, 0).getRGB();
	}

	public void tick(Camera camera) {
		if (Settings.floors != DrawMode.NONE) {
			drawCeiling();
			drawFloor();
		} else {
			Arrays.fill(buffer, backgroundColor);
		}
		
		
		camera.update(level.getMap());
		
		for (int x=0; x<width; x++) {
			double cameraX = 2 * x / (double)(width)  - 1;
			double rayPosX = camera.xPos;
			double rayPosY = camera.yPos;
			double rayDirX = camera.xDir + camera.xPlane * cameraX;
			double rayDirY = camera.yDir + camera.yPlane * cameraX;
		
			int mapX = (int) camera.xPos;
			int mapY = (int) camera.yPos;
			
			double sideDistX;
			double sideDistY;
			
			// length of ray from one x or y-side to next x or y-side
			double deltaDistX = Math.sqrt(1 + (rayDirY * rayDirY) / (rayDirX * rayDirX));
			double deltaDistY = Math.sqrt(1 + (rayDirX * rayDirX) / (rayDirY * rayDirY));
			double perpWallDist;

			// what direction to step in x or y-direction (either +1 or -1)
			int stepX;
			int stepY;

			boolean hit = false; 
			int side = 0; // was a NS or a EW wall hit?
			
			// calculate step and initial sideDist
			if (rayDirX < 0) {
				stepX = -1;
				sideDistX = (rayPosX - mapX) * deltaDistX;
			} else {
				stepX = 1;
				sideDistX = (mapX + 1.0 - rayPosX) * deltaDistX;
			}
			if (rayDirY < 0) {
				stepY = -1;
				sideDistY = (rayPosY - mapY) * deltaDistY;
			} else {
				stepY = 1;
				sideDistY = (mapY + 1.0 - rayPosY) * deltaDistY;
			}
			
			while (!hit) {
				// jump to next map square, OR in x-direction, OR in y-direction
				if (sideDistX < sideDistY) {
					sideDistX += deltaDistX;
					mapX += stepX;
					side = 0;
				} else {
					sideDistY += deltaDistY;
					mapY += stepY;
					side = 1;
				}
				// Check if ray has hit a wall
				hit = level.isObstacle(mapX, mapY);
			}
			
			if (side == 0) {
				perpWallDist = (mapX - rayPosX + (1 - stepX) / 2) / rayDirX;
			} else {
				perpWallDist = (mapY - rayPosY + (1 - stepY) / 2) / rayDirY;
			}
			
			// Calculate height of line to draw on screen
			int lineHeight = (int) (height / perpWallDist);

			// calculate lowest and highest pixel to fill in current stripe
			int drawStart = -lineHeight / 2 + height / 2;
			if (drawStart < 0)
				drawStart = 0;
			int drawEnd = lineHeight / 2 + height / 2;
			if (drawEnd >= height)
				drawEnd = height - 1;
			
			int color = (side == 1) ? colorA : colorB;
			
			drawLine(x, drawStart, drawEnd, color);
		}
			
	}

	private void drawLine(int x, int drawStart, int drawEnd, int color) {
		for (int y = drawStart; y < drawEnd; y++) {
			buffer[(y * width) + x] = color;
		}
	}

	private void drawCeiling() {
		for (int i = 0; i < buffer.length / 2; i++) {
			buffer[i] = Color.GRAY.getRGB();
		}
	}

	private void drawFloor() {
		for (int i = buffer.length / 2; i < buffer.length; i++) {
			buffer[i] = Color.LIGHT_GRAY.getRGB();
		}
	}

	public void setBuffer(int[] buffer) {
		this.buffer = buffer;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

}
