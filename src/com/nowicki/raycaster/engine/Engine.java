package com.nowicki.raycaster.engine;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nowicki.raycaster.engine.Light.LightLocation;
import com.nowicki.raycaster.engine.Settings.DrawMode;
import com.nowicki.raycaster.engine.Settings.SkyMode;
import com.nowicki.raycaster.engine.shader.RainShader;
import com.nowicki.raycaster.engine.shader.Shader;
import com.nowicki.raycaster.engine.shader.StormShader;

public class Engine {
	
	// number of floors - vertical elements one on another
	public static final int FLOORS = 3;

	private int[] buffer;
	private double[] zBuffer;
	private Level level;

	private int width;
	private int height;

	private double movement = 0;
	private int verticalDisplace = 0;
	private int gunVerticalDisplace = 0;
	private int gunHorizontalDisplace = 0;
	private Weapon weapon;
	private long frame = 0;
	
	private RainShader rainShader;
	private StormShader stormShader;
	private List<Shader> shaders = new ArrayList<Shader>();
	
	public Engine(int widht, int height, Weapon weapon) {
		this.width = widht;
		this.height = height;
		this.weapon = weapon;
		this.zBuffer = new double[widht];
		
		stormShader = new StormShader();
		rainShader = new RainShader();
		shaders.add(stormShader);
		shaders.add(rainShader);
	}

	public void tick(Camera camera, double frameTime) {
		
		camera.update(level, frameTime);
		
		// update rain & storm shader - rains only if on ground floor we have no ceiling above
		// storm shader disabled, because it's annoying in the long run ;)
		rainShader.setEnabled(!level.getElement(0, (int)camera.xPos, (int)camera.yPos).isCeilingVisible());
		stormShader.setEnabled(false);

		// look up/down amount same for every pixel
		int yShear = (int) (height * camera.yShear);
		
		// floor/ceiling shade distance adjusted wuth ySher -identical for every column
		double floorShadeDistance = Settings.FOG_DISTANCE_FLOOR + (Settings.FOG_DISTANCE_FLOOR * camera.yShear);
		
		// odd shearing amount causes texture mapping to break
		if (yShear % 2 != 0) {
			yShear += (yShear < 0) ? - 1 : 1;
		}

		drawCeilingAndFloor(camera, yShear);
		
		if (Settings.walkingEffect) {
			if (camera.isPlayerMoving()) {
				movement += 0.2;
				verticalDisplace = (int) (height * Settings.WALKING_EFFECT_SCALE * Math.sin(movement));
				gunVerticalDisplace = (int) (2 * Math.sin(movement));
				gunHorizontalDisplace = (int) (8 * Math.cos(movement));
			}
		} else {
			verticalDisplace = 0;
		}
		
		// level can consist of FLOORS (3), draw up to down
		// floors, ceiling and sprites applied only to ground floor
		for (int floor=FLOORS-1; floor>=0; floor--) {
			
			for (int x=0; x<width; x++) {
				double cameraX = 2 * (x) / (double)(width) - 1; // in <-1, 1> coordinates
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
					sideDistX = (camera.xPos - mapX) * deltaDistX;
				} else {
					stepX = 1;
					sideDistX = (mapX + 1.0 - camera.xPos) * deltaDistX;
				}
				if (rayDirY < 0) {
					stepY = -1;
					sideDistY = (camera.yPos - mapY) * deltaDistY;
				} else {
					stepY = 1;
					sideDistY = (mapY + 1.0 - camera.yPos) * deltaDistY;
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
					hit = level.isWall(floor, mapX, mapY);
				}
				
				if (side == 0) {
					wallDistance = (mapX - camera.xPos + (1 - stepX) / 2) / rayDirX;
				} else {
					wallDistance = (mapY - camera.yPos + (1 - stepY) / 2) / rayDirY;
				}
				
				// z-buffer holds distances to walls for each stripe (same for whole column)
				// so it is used to determine if given sprite is visible or not
				// relevant only on ground floor
				zBuffer[x] = wallDistance;
				
				int lineHeight = (int) (height / wallDistance);
				
				// calculate lowest and highest pixel to fill in current stripe
				// simple equation would be ((+/-)lineHeight) / 2 + height / 2
				// consider look up/down (yShear) in calculation
				// consider floor (each of the same height equal to lineHeight)
				int drawStartNotClipped = -lineHeight / 2 + (height - ((lineHeight-1) * 2 * floor) + yShear) / 2;
				int drawEndNotClipped = lineHeight / 2 + (height - ((lineHeight-1) * 2 * floor)  + yShear) / 2;
				
				// clip calculated values to screen
				int drawStart = clipVertically(drawStartNotClipped);
				int drawEnd = clipVertically(drawEndNotClipped);
				
				// element which was hit by the ray
				Element element = level.getElement(floor, mapX, mapY);
				
				// if nothing was hit (ray went over the level, we can skip to next iteration
				// warning - non-bounded level impossible on floor == 0 (ground)
				if (element == null) {
					continue;
				}
				
				// texture coordinates in 0...1 
				double wallX, wallY;
				
				// texture coordinates in real pixels
				int u, v;
				
				if (Settings.walls == DrawMode.SOLID) {
					int color = element.getColor(side);
					
					if (Settings.shading) {
						color = GraphicsHelper.fadeToBlack(color, wallDistance, Settings.FOG_DISTANCE);
					}
					
					if (Settings.walkingEffect) {
						drawStart = clipVertically(drawStart + verticalDisplace);
						drawEnd = clipVertically(drawEnd + verticalDisplace);
					}
					
					drawLine(x, drawStart, drawEnd, color);
				}
				else if (Settings.walls == DrawMode.TEXTURED) {
					Texture wallTexture = element.getWallTexture(side);
					int textureWidth = wallTexture.getWidth();
					
					wallX = (side == 0) ? (camera.yPos + wallDistance * rayDirY) : (camera.xPos + wallDistance * rayDirX);
					wallX -= Math.floor(wallX);
					
					u = (int) (wallX * textureWidth) % textureWidth;
					
					for (int y=drawStart; y<drawEnd; y++) {
						wallY = ((((double)y*2 - (height + yShear) + lineHeight)) / lineHeight) / 2;
						
						while (wallY < 0) {
							wallY++;
						}
						wallY -= Math.floor(wallY);
						
						
						int texel;
						if (!Settings.textureFiltering) {
							v = (((y*2 - (height + yShear) + lineHeight) * textureWidth) / lineHeight) / 2;
							// required adjust, since on upper floors coordinate (y-dependent) were negative
							while (v < 0) {
								v += textureWidth;
							}
							texel = wallTexture.getPixel(u, v);
						}
						else {
							texel = wallTexture.getPixelWithFiltering(wallX, wallY);
						}
						
						if (side == 0) {
							// TODO optimize: pre-generate darker texture version
							texel = new Color(texel).darker().getRGB();
						}
						
						// optimize - move it higher, don't apply texture mapping but put black pixel if
						// wallDistance >= FOG_DISTANCE
						if (Settings.shading) {
							int shadedTexel = GraphicsHelper.fadeToBlack(texel, wallDistance, Settings.FOG_DISTANCE);
							
							if (Settings.lights) {
				    			for (Light light : level.getLights()) {
				    				if (light.getLocation() == LightLocation.WALL || light.getLocation() == LightLocation.ALL) {
					    				double intensity = light.getIntensity((double)mapX + wallX, (double)mapY + wallY);
					    				if (intensity > 0) {
					    					shadedTexel = GraphicsHelper.mixColors(shadedTexel, texel, intensity);
					    				}
				    				}
				    			}
				    		}
							
							texel = shadedTexel;
						}
						
						int y1 = y;
						if (Settings.walkingEffect) {
							y1 = clipVertically(y1 + verticalDisplace);
						}
						
						buffer[y1*width+x] = texel;
					}
				
					if (floor == 0) {
						if (Settings.floors == DrawMode.TEXTURED) {
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
							
							for (int y=drawEnd; y<height + Math.abs(yShear + verticalDisplace); y++) {
								double currentDist = (height+yShear) / (2.0 * y - (height + yShear));
				
						        double weight = Math.abs(currentDist / (wallDistance + (wallDistance * camera.yShear)));
						        
						        // coordinates of point where the ray hits the floor
						        double floorX = Math.abs(weight * floorXWall + (1.0 - weight) * camera.xPos);
						        double floorY = Math.abs(weight * floorYWall + (1.0 - weight) * camera.yPos);
								
								Element floorElement = level.getElement(floor, (int)floorX, (int)floorY);
								
								// may be null if we look "behind" the level - can ignore that
								if (floorElement != null) {
									if (floorElement.isFloorVisible() || floorElement.isCeilingVisible()) {
										
										Texture floorTexture = floorElement.getFloorTexture();
										Texture ceilingTexture = floorElement.getCeilingTexture();
										textureWidth = floorTexture.getWidth();
										
								        int floorTexel;
								        int ceilingTexel;
								        
								        if (!Settings.textureFiltering) {
								        	
								        	u = (int) (floorX * textureWidth) % textureWidth;
									        v = (int) (floorY * textureWidth) % textureWidth;
									        
									        floorTexel = floorTexture.getPixel(u, v);
										    ceilingTexel = ceilingTexture.getPixel(u, v); 
								        } else {
								        	
								        	double tx = floorX - Math.floor(floorX);
									        double ty = floorY - Math.floor(floorY);
								        	
								        	floorTexel = floorTexture.getPixelWithFiltering(tx, ty);
									        ceilingTexel = ceilingTexture.getPixelWithFiltering(tx, ty); 
								        }
								        
								    	int y1 = y + verticalDisplace;
								    	int y2 = y - verticalDisplace;
								        
								    	if (y1 < height && floorElement.isFloorVisible()) {
								    		
								    		if (Settings.shading) {
									    		int shadedTexel = GraphicsHelper.fadeToBlack(floorTexel, currentDist, floorShadeDistance);
									    		
									    		if (Settings.lights) {
									    			for (Light light : level.getLights()) {
									    				if (light.getLocation() == LightLocation.FLOOR || light.getLocation() == LightLocation.ALL) {
										    				double intensity = light.getIntensity(floorX, floorY);
										    				if (intensity > 0) {
										    					shadedTexel = GraphicsHelper.mixColors(shadedTexel, floorTexel, intensity);
										    				}
									    				}
									    			}
									    		}
									    		
									    		floorTexel = shadedTexel;
								    		}
								    		
								    		buffer[y1*width+x] = floorTexel; 
								    	}
								    	
								    	// second condition -> don't draw over walls
								        if ((height+yShear-y2) >= 0 && (height+yShear-y2) < (drawStart + verticalDisplace) && floorElement.isCeilingVisible()) {
								        	if (Settings.shading) {
									    		int shadedTexel = GraphicsHelper.fadeToBlack(ceilingTexel, currentDist , floorShadeDistance);
									    		
									    		if (Settings.lights) {
									    			for (Light light : level.getLights()) {
									    				if (light.getLocation() == LightLocation.CEILING || light.getLocation() == LightLocation.ALL) {
										    				double intensity = light.getIntensity(floorX, floorY);
										    				if (intensity > 0) {
										    					shadedTexel = GraphicsHelper.mixColors(shadedTexel, ceilingTexel, intensity);
										    				}
									    				}
									    			}
									    		}
									    		
									    		ceilingTexel = shadedTexel;
									    	}
								        	buffer[(height+yShear-y2)*width+x] = ceilingTexel;
								        }
									}
								}
							}
						}
					}
				}
			}
		}

		if (Settings.sprites) {
			
			// calculate distance between sprinte and camera
			level.getSprites().stream().forEach(s -> s.distanceToCamera = MathHelper.distanceBetweenPoints(camera.xPos, camera.yPos, s.xPosition, s.yPosition));
			
			// sort sprites by distance to camera, we can ignore 0 condition in comparator here
			level.getSprites().sort((s1, s2) -> (s2.distanceToCamera > s1.distanceToCamera) ? 1 : -1 );
			
			// iterate over every sprite
			// TODO - select only visible for processing at some point
			for (Sprite sprite : level.getSprites()) {
				
				// distance relative to the camera
				double spriteX = sprite.xPosition - camera.xPos;
				double spriteY = sprite.yPosition - camera.yPos;
				
				// transform - multiply by inversion of camera matrix. TODO - move to sprite class?
				double invDet = 1.0 / (camera.xPlane * camera.yDir - camera.xDir * camera.yPlane);
				sprite.xTransformed = invDet * (camera.yDir * spriteX - camera.xDir * spriteY);
				sprite.yTransformed = invDet * (-camera.yPlane * spriteX + camera.xPlane * spriteY);
			
			    // center point of the sprite on screen and its height
			    int spriteScreenX = (int) ((width / 2) * (1 + sprite.xTransformed  / sprite.yTransformed));
			    int spriteScreenY = (height + yShear) / 2;
			    
			    // assume sprites are squares, so only one dimension is needed to be calculated
			    // but store in separate vars for the future
			    int spriteHeight = (int) Math.abs(height / sprite.yTransformed);
			    int spriteWidth = spriteHeight;
				
			    int drawStartY = clipVertically(-spriteHeight / 2 + (height + yShear) / 2);
			    int drawEndY = clipVertically(spriteHeight / 2 + (height + yShear) / 2);
			    int drawStartX = clipHorizontally(-spriteWidth / 2 + spriteScreenX);
			    int drawEndX = clipHorizontally(spriteWidth / 2 + spriteScreenX);

			    Texture texture = sprite.getTexture();
			    
			    // draw the sprite
			    for (int x=drawStartX; x<drawEndX; x++) {
			    	// if is in front of the camera (yTransformed > 0) but before the wall
			    	if (sprite.yTransformed > 0 && sprite.yTransformed < zBuffer[x]) {
				    	for (int y=drawStartY; y<drawEndY; y++) {
				    		int u = (x - (-spriteWidth / 2 + spriteScreenX)) * texture.getWidth() / spriteWidth;
				    		int v = (y - (-spriteHeight / 2 + spriteScreenY)) * texture.getHeight() / spriteHeight;
				    		int texel = texture.getPixel(u, v);
				    		if (texel != Color.BLACK.getRGB()) {
				    			
				    			// apply effects if required
				    			int y1 = clipVertically(y + verticalDisplace);
				    			if (Settings.shading) {
				    				texel = GraphicsHelper.fadeToBlack(texel, sprite.distanceToCamera, Settings.FOG_DISTANCE);
				    			}
				    			
				    			buffer[y1*width+x] = texel;
				    		}
				    	}
			    	}
			    }
			}
		}
		
		// apply enabled shaders 
		shaders.stream().filter(s -> s.isEnabled()).forEach(shader -> shader.apply(buffer, width, height));
		
		if (Settings.showWeapon) {
			drawWeapon(frameTime);
		}
		
		frame++;
			
	}

	private int clipHorizontally(int x) {
		return (x >= width) ? width-1 : (x < 0) ? 0 : x;
	}
	
	private int clipVertically(int y) {
		return (y >= height) ? height-1 : (y < 0) ? 0 : y;
	}

	private void drawLine(int x, int drawStart, int drawEnd, int color) {
		// possible optimize: draw n (line height) pixels with step == screen width
		for (int y=drawStart; y<drawEnd; y++) {
			buffer[y * width + x] = color;
		}
	}
	
	private void drawCeilingAndFloor(Camera camera, int yShear) {
		switch (Settings.floors) {
		case SOLID:
			int half = width * (height + yShear) / 2;
			Arrays.fill(buffer, 0, half, Settings.CEILING_COLOUR);
			Arrays.fill(buffer, half + 1, buffer.length, Settings.FLOOR_COLOUR);
			
			if (Settings.shading) {
				half = (height+yShear)/2;
				for (int y=0; y<half; y++) {
					int color = GraphicsHelper.fadeToBlack(Settings.CEILING_COLOUR, y, half * 0.8);
					for (int x=0; x<width; x++) {
						buffer[y*width+x]=color;
					}
				}
				for (int y=height-1; y>half; y--) {
					int color = GraphicsHelper.fadeToBlack(Settings.FLOOR_COLOUR, y, half * 0.8);
					for (int x=0; x<width; x++) {
						buffer[y*width+x]=color;
					}
				}
			}
			break;
		default:
			drawSky(camera, yShear);
		}
	}
	
	private void drawSky(Camera camera, int yShear) {
		
		Arrays.fill(buffer, 0);
		
		switch (Settings.sky) {
			case SIMPLE:
			case SIMPLE_STRETCHED:
				drawSkySimple(camera, yShear);
				break;
			case SPHERE:
				drawSkySphere(camera, yShear);
				break;
			default:
		}
		
	}
	
	private void drawSkySimple(Camera camera, int yShear) {
		Texture sky = level.getSkyTexture();
		double tStart = (double)sky.getWidth() / 0.2 * ((Math.atan2(camera.xDir + camera.xPlane, camera.yDir + camera.yPlane) / Math.PI) + 1);
	
		int ty = 0;
		int texel = Color.BLACK.getRGB();
		int skyHeight = sky.getHeight();
		
		// apply look up/down correction
		if (Settings.sky == SkyMode.SIMPLE_STRETCHED) {
			if (yShear >= 0) {
				skyHeight += yShear / 2;
			} else {
				ty = -yShear / 2;
			}
		}
		
		for (int y=0; y<skyHeight; y++) {
			int tx = (int) tStart % sky.getWidth();
			for (int x=0; x<width; x++) {
				
				if (Settings.sky == SkyMode.SIMPLE) {
					texel = sky.getPixel(tx++, ty);
				} else if (Settings.sky == SkyMode.SIMPLE_STRETCHED) {
					texel = sky.getPixel((double)tx++ / sky.getWidth(), (double)ty / skyHeight);
				}
				
				int y1 = clipVertically(y + (verticalDisplace / 2));
				
				buffer[y1*width+x] = texel;
				
				if (tx == sky.getWidth()) {
					tx = 0;
				}
			}
			ty++;
		}
	}
	
	private void drawSkySphere(Camera camera, int yShear) {
		Texture sky = level.getSkyTexture();
		
		int cx = width / 2;
		int cy = height / 2;
		int hl = height / 2;
	
		// TODO - fix Pi/180 rad problem
		double cameraDirection = camera.xDir;
		
		for (int y=0; y<hl; y++) {
			for (int x=0; x<width; x++) {
				
				double nx = (double)(x - cx) / cx;
				double ny = (double)(y - cy) / cy;
				
				// adjust nx to the camera
				nx = ((nx + cameraDirection + 2) % 2) - 1;
				
				// real spere mapping would be (Math.asin(nx)/Math.PI + 0.5) but it's slow
				int u = (int) ((nx/2 + 0.5) * sky.getWidth()) % sky.getWidth();
				int v = (int) (2 * (ny/2 + 0.5) * sky.getHeight()) % sky.getHeight();
				
				try {
					buffer[y*width+x] = sky.getPixel(u, v);
				} catch (Exception e) {
					System.out.println(camera.xDir + " " + nx + " " + ny);
					throw e;
				}
			}
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
				if (pixel != 0xFF00FFFF) {
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
