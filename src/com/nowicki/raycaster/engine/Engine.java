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
	private int gunVerticalDisplace = 0;
	private int gunHorizontalDisplace = 0;
	private Map<Element, Texture> textures;
	private Weapon weapon;
	private long frame = 0;
	
	public Engine(int widht, int height, Map<Element, Texture> textures, Weapon weapon) {
		this.width = widht;
		this.height = height;
		this.textures = textures;
		this.weapon = weapon;
	}

	public void tick(Camera camera, double frameTime) {
		
		camera.update(level, frameTime);

		// look up/down amount same for every pixel
		int yShear = (int) (height * camera.yShear);
		
		// TODO - bugfix - odd shearing amount causes texture mapping to break (revisit)
		if (yShear % 2 != 0) {
			yShear += (yShear < 0) ? - 1 : 1;
		}

		drawCeilingAndFloor(yShear);
		
		if (Settings.walkingEffect) {
			if (camera.isPlayerMoving()) {
				movement += 0.2;
				verticalDisplace = (int) (height * Settings.WALKING_EFFECT_SCALE * Math.sin(movement));
				gunVerticalDisplace = (int) (2 * Math.sin(movement));
				gunHorizontalDisplace = (int) (8 * Math.cos(movement));
			}
		}
		
		for (int x=0; x<width; x++) {
			double cameraX = 2 * (x) / (double)(width)  - 1; // in <-1, 1> coordinates
			double rayPosX = camera.xPos;
			double rayPosY = camera.yPos;
			double rayDirX = camera.xDir + camera.xPlane * cameraX;
			double rayDirY = camera.yDir + camera.yPlane * cameraX;
		
			// where are we on the map?
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
			
			int lineHeight = (int) (height / wallDistance);
			
			// calculate lowest and highest pixel to fill in current stripe
			// consider look up/down (yShear) in calculation
			int drawStart = -lineHeight / 2 + (height + yShear) / 2;
			int drawEnd = lineHeight / 2 + (height + yShear) / 2;
			
			drawStart = clipVertically(drawStart);
			drawEnd = clipVertically(drawEnd);
			
			// element which was hit by the ray
			Element element = level.getElement(mapX, mapY);
			
			double wallX = 0.0;
			
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
				Texture wallTexture = textures.get(element);
				int textureWidth = wallTexture.getSize();
				int u, v;
				
				wallX = (side == 0) ? (rayPosY + wallDistance * rayDirY) : (rayPosX + wallDistance * rayDirX);
				wallX -= Math.floor(wallX);
				
				u = (int) (wallX * textureWidth);
				if (side == 0 && rayDirX > 0) {
					u = textureWidth - u - 1;
				}
				if (side == 1 && rayDirY < 0) {
					u = textureWidth - u - 1;
				}
				
				for (int y=drawStart; y<drawEnd; y++) {
					v = (((y*2 - (height + yShear) + lineHeight) * textureWidth) / lineHeight) / 2;
					int texel = wallTexture.getPixel(u, v);
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
			
				if (Settings.floors == DrawMode.TEXTURED || Settings.floors == DrawMode.TEXTURED_SHADED) {
					double floorXWall, floorYWall;
					if (side == 0 && rayDirX > 0) {
						floorXWall = mapX;
						floorYWall = mapY + wallX;
					} else if (side == 0 && rayDirX < 0) {
						floorXWall = mapX + 1.0;
						floorYWall = mapY + wallX;
					} else if (side == 1 && rayDirY > 0) {
						floorXWall = mapX + wallX;
						floorYWall = mapY;
					} else {
						floorXWall = mapX + wallX;
						floorYWall = mapY + 1.0;
					}
					
					double  currentDist;
					
					Texture floorTexture = textures.get(Element.FLOOR);
					Texture ceilingTexture = textures.get(Element.CEILING);
					textureWidth = floorTexture.getSize();
					
					for (int y=drawEnd+1; y<height+Math.abs(yShear); y++) {
						currentDist = (height+yShear) / (2.0 * y - (height + yShear));
		
				        double weight = currentDist / (wallDistance + (wallDistance * camera.yShear));
				        
				        double currentFloorX = weight * floorXWall + (1.0 - weight) * camera.xPos;
				        double currentFloorY = weight * floorYWall + (1.0 - weight) * camera.yPos;
		
				        u = (int) (currentFloorX * textureWidth) % textureWidth;
				        v = (int) (currentFloorY * textureWidth) % textureWidth;
		
				        int floorTexel = floorTexture.getPixel(u, v);
				        int ceilingTexel = ceilingTexture.getPixel(u, v); 
				        
				    	if (Settings.floors == DrawMode.TEXTURED_SHADED) {
				    		floorTexel = fadeToBlack(floorTexel, (height + yShear)-y, (height + yShear)/2);
				    		ceilingTexel = fadeToBlack(ceilingTexel, (height + yShear)-y, (height + yShear)/2);
				    	}
				    	
				    	int y1 = y + verticalDisplace;
				    	int y2 = y - verticalDisplace;
				        
				    	if (y1 < height) {
				    		buffer[y1*width+x] = floorTexel; 
				    	}
				        if ((height+yShear-y2) >= 0) {
				        	buffer[(height+yShear-y2)*width+x] = ceilingTexel;
				        }
					}
				}
			
			}
		}
		
		if (Settings.showWeapon) {
			drawWeapon(frameTime);
		}
		
		frame++;
			
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
	
	private void drawCeilingAndFloor(int yShear) {
		int half;
		switch (Settings.floors) {
		case SOLID:
			half = width * (height + yShear) / 2;
			Arrays.fill(buffer, 0, half, Settings.CEILING_COLOUR);
			Arrays.fill(buffer, half + 1, buffer.length, Settings.FLOOR_COLOUR);
			break;
		case SOLID_SHADED:
			half = (height+yShear)/2;
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
	
	private void drawWeapon(double frameSkip) {
		// TODO optimize!
		if (weapon.isShooting() && frame % 3 == 0) {
			weapon.nextFrame();
		}
		int gunImageWidth = weapon.getFrameWidth();
		int gunImageHeight = weapon.getFrameHeight();
		int[] gunPixels = weapon.getFrame();
		
		int startX = ((width - gunImageWidth) / 2) + gunHorizontalDisplace; 
		int startY = (height - gunImageHeight + 10) + gunVerticalDisplace;
		int u = 0;
		int v = 0;
		
		for (int y=startY; y<height; y++) {
			for (int x=startX; x<(startX+gunImageWidth); x++) {
				int pixel = gunPixels[v*gunImageWidth+u];
				if (pixel != -16711681) { // TODO brute-force transparency
					buffer[y*width+x] = pixel;
				}
				u++;
			}
			u = 0;
			v++;
		}
	}

	public void setBuffer(int[] buffer) {
		this.buffer = buffer;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Weapon getWeapon() {
		return weapon;
	}

}
