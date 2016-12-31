package com.nowicki.raycaster.engine;

import java.awt.Color;
import java.util.Arrays;

import com.nowicki.raycaster.engine.Settings.DrawMode;

public class Engine {

	private int[] buffer;
	private Level level;

	private int width;
	private int height;

	private int frame;
	
	public Engine(int widht, int height) {
		this.width = widht;
		this.height = height;
	
	}

	public void tick(Camera camera) {
		
		drawCeilingAndFloor();
		
		camera.update(level.getMap());
		
		for (int x=0; x<width; x++) {
			double cameraX = 2 * x / (double)(width)  - 1; // in <-1, 1> coordinates
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
			
			Element element = level.getElement(mapX, mapY);
			int color = (side == 1) ? element.getColor1AsRGB() : element.getColor2AsRGB();
			
			if (Settings.walls == DrawMode.SHADED) {
				color = fadeToBlack(color, perpWallDist, 20);
			}
			
			drawLine(x, drawStart, drawEnd, color);
		}
			
	}

	private int fadeToBlack(int color, double current, double max) {
		Color c = new Color(color);
		int r = normalize((c.getRed() * (max - current)) / max);
		int g = normalize((c.getGreen() * (max - current)) / max);
		int b = normalize((c.getBlue() * (max - current)) / max);
		return new Color(r, g, b).getRGB();
	}

	private int normalize(double value) {
		return (value > 255) ? 255 : (value < 0) ? 0 : (int) value;
	}

	private void drawLine(int x, int drawStart, int drawEnd, int color) {
		for (int y = drawStart; y < drawEnd; y++) {
			buffer[(y * width) + x] = color;
		}
	}
	
	private void drawCeilingAndFloor() {
		switch (Settings.floors) {
		case SOLID:
			Arrays.fill(buffer, 0, buffer.length / 2, Settings.CEILING_COLOUR);
			Arrays.fill(buffer, buffer.length / 2 + 1, buffer.length, Settings.FLOOR_COLOUR);
			break;
		case SHADED:
			int half = height/2;
			for (int y=0; y<half; y++) {
				int color = fadeToBlack(Settings.CEILING_COLOUR, y, half * 0.8);
				for (int x=0; x<width; x++) {
					buffer[y*width+x]=color;
				}
			}
			for (int y=height-1; y>half; y--) {
				int color = fadeToBlack(Settings.FLOOR_COLOUR, y, half * 0.8);
				for (int x=0; x<width; x++) {
					buffer[y*width+x]=color;
				}
			}
			break;
		default:
			Arrays.fill(buffer, 0);
		}
	}

	public void setBuffer(int[] buffer) {
		this.buffer = buffer;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

}
