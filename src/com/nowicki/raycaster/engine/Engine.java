package com.nowicki.raycaster.engine;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;

import com.nowicki.raycaster.engine.Settings.DrawMode;

public class Engine {

	private int[] buffer;
	private Level level;

	private int width;
	private int height;

	private double movement = 0;
	private int verticalDisplace = 0;
	private Map<Element, Texture> textures;
	
	public Engine(int widht, int height, Map<Element, Texture> textures) {
		this.width = widht;
		this.height = height;
		this.textures = textures;
	
	}

	public void tick(Camera camera, double frameTime) {
		
		drawCeilingAndFloor();
		
		camera.update(level.getMap(), frameTime);
		
		if (Settings.walkingEffect) {
			if (camera.isPlayerMoving()) {
				movement += 0.2;
				verticalDisplace = (int) (height * Settings.WALKING_EFFECT_SCALE * Math.sin(movement));
			}
		}
		
		for (int x=0; x<width; x++) {
			double cameraX = 2 * (x) / (double)(width)  - 1; // in <-1, 1> coordinates
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
			double wallDistance;

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
					side = 0; // x axis wall
				} else {
					sideDistY += deltaDistY;
					mapY += stepY;
					side = 1; // y axis wall
				}
				// Check if ray has hit a wall
				hit = level.isObstacle(mapX, mapY);
			}
			
			if (side == 0) {
				wallDistance = (mapX - rayPosX + (1 - stepX) / 2) / rayDirX;
			} else {
				wallDistance = (mapY - rayPosY + (1 - stepY) / 2) / rayDirY;
			}
			
			// Calculate height of line to draw on screen
			int lineHeight = (int) (height / wallDistance);

			// calculate lowest and highest pixel to fill in current stripe
			int drawStart = -lineHeight / 2 + height / 2;
			if (drawStart < 0)
				drawStart = 0;
			int drawEnd = lineHeight / 2 + height / 2;
			if (drawEnd >= height)
				drawEnd = height - 1;
			
			// element which was hit by the ray
			Element element = level.getElement(mapX, mapY);
			
			if (Settings.walls == DrawMode.SOLID_SHADED || Settings.walls == DrawMode.SOLID) {
				int color = element.getColor(side);
				
				if (Settings.walls == DrawMode.SOLID_SHADED) {
					color = fadeToBlack(color, wallDistance, 20);
				}
				
				if (Settings.walkingEffect) {
					drawStart = clipVertically(drawStart + verticalDisplace);
					drawEnd = clipVertically(drawEnd + verticalDisplace);
				}
				
				drawLine(x, drawStart, drawEnd, color);
			}
			else if (Settings.walls == DrawMode.TEXTURED || Settings.walls == DrawMode.TEXTURED_SHADED) {
				Texture texture = textures.get(element);
				
				double wallX = (side == 0) ? (rayPosY + wallDistance * rayDirY) : (rayPosX + wallDistance * rayDirX);
				wallX -= Math.floor(wallX);
				
				int u = (int) (wallX * texture.getSize());
				if (side == 0 && rayDirX > 0)
					u = texture.getSize() - u - 1;
				if (side == 1 && rayDirY < 0)
					u = texture.getSize() - u - 1;
				
				for (int y=drawStart; y<drawEnd; y++) {
					int v = (((y*2 - height + lineHeight) << 6) / lineHeight) / 2;
					int texel = texture.getPixels()[v * texture.getSize() + u];
					if (side == 0) {
						// TODO optimize: pre-generate darker texture version
						texel = new Color(texel).darker().getRGB();
					}
					
					if (Settings.walls == DrawMode.TEXTURED_SHADED) {
						texel = fadeToBlack(texel, wallDistance, 15);
					}
					
					int y1 = y;
					if (Settings.walkingEffect) {
						y1 = clipVertically(y1 + verticalDisplace);
					}
					
					buffer[y1*width+x] = texel;
				}
			}
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
	
	private int clipVertically(int y) {
		return (y >= height) ? height-1 : (y < 0) ? 0 : y;
	}

	private void drawLine(int x, int drawStart, int drawEnd, int color) {
		// possible optimize: draw n (line height) pixels with step == screen width?
		for (int y=drawStart; y<drawEnd; y++) {
			buffer[y * width + x] = color;
		}
	}
	
	private void drawCeilingAndFloor() {
		switch (Settings.floors) {
		case SOLID:
			Arrays.fill(buffer, 0, buffer.length / 2, Settings.CEILING_COLOUR);
			Arrays.fill(buffer, buffer.length / 2 + 1, buffer.length, Settings.FLOOR_COLOUR);
			break;
		case SOLID_SHADED:
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
