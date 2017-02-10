package com.nowicki.raycaster.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import com.nowicki.raycaster.controls.KeyboardController;
import com.nowicki.raycaster.engine.Camera;
import com.nowicki.raycaster.engine.Element.Entry;
import com.nowicki.raycaster.engine.Engine;
import com.nowicki.raycaster.engine.Level;
import com.nowicki.raycaster.engine.Settings;
import com.nowicki.raycaster.engine.Texture;
import com.nowicki.raycaster.engine.Weapon;

public class RaycasterDisplay extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	
	public static final String FRAME_TITLE = "Raycaster demo";
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 400;
	public static final int FPS_LIMIT = 50;
	public static final long FRAME_TIME_MILIS = 1000 / FPS_LIMIT;
	
	private GraphicsDevice screen;

	private boolean running;
	private Thread thread;
	private BufferedImage frame;
	
	private Engine engine;
	private Camera camera;
	private Map<Entry, Texture> textures = new HashMap<>();
	private Weapon weapon;
	
	private KeyboardController keyboardController;
	
	private int windowBarHeight;
	private long fps;

	public RaycasterDisplay() throws IOException {		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		screen = ge.getDefaultScreenDevice();
		
		loadTextures();
		loadWeapons();
		Level level = new Level("data/raycaster/level.txt", textures);
		camera = new Camera(3, 10);
		
		engine = new Engine(WIDTH, HEIGHT, camera, weapon);
		engine.setLevel(level);
		
		
		keyboardController = new KeyboardController(engine, camera, this);
		addKeyListener(keyboardController);
		
		frame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		engine.setBuffer(((DataBufferInt)frame.getRaster().getDataBuffer()).getData());

		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setResizable(false);
		setTitle(FRAME_TITLE);
		setDefaultLookAndFeelDecorated(true);
		setBackground(Color.BLACK);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		windowBarHeight = (int) (HEIGHT - getContentPane().getSize().getHeight());
		setSize(WIDTH, HEIGHT + windowBarHeight);
		
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	private void loadTextures() throws IOException {
		textures.put(Entry.WALL_1_GREYSTONE, new Texture("data/raycaster/pics/greystone.png"));
		textures.put(Entry.WALL_2_BLUESTONE, new Texture("data/raycaster/pics/bluestone.png"));
		textures.put(Entry.WALL_3_COLORSTONE, new Texture("data/raycaster/pics/colorstone.png"));
		textures.put(Entry.WALL_4_WOOD, new Texture("data/raycaster/pics/wood.png"));
		textures.put(Entry.WALL_5_BRICK, new Texture("data/raycaster/pics/redbrick.png"));
		textures.put(Entry.WALL_6_BRICK_WITH_EAGLE, new Texture("data/raycaster/pics/eagle.png"));
		textures.put(Entry.BARREL, new Texture("data/raycaster/pics/barrel.png"));
		textures.put(Entry.PILLAR, new Texture("data/raycaster/pics/pillar.png"));
		textures.put(Entry.CEILING_LAMP, new Texture("data/raycaster/pics/greenlight.png"));
		textures.put(Entry.DEFAULT_FLOOR, new Texture("data/raycaster/pics/floor.png"));
		textures.put(Entry.DEFAULT_CEILING, new Texture("data/raycaster/pics/ceiling.png"));
		textures.put(Entry.RED_CARPET, new Texture("data/raycaster/pics/redcarpet.png"));
		textures.put(Entry.GRASS, new Texture("data/raycaster/pics/grass.png"));
		textures.put(Entry.SKY, new Texture("data/raycaster/pics/sky.png"));
	}
	
	private void loadWeapons() throws IOException {
		weapon = new Weapon("data/raycaster/pics/shotgun.png", 
				new int[] { 0, 1, 2, 3, 3, 4, 4, 5, 5, 4, 4, 3 },
				new int[] { 87, 172, 256, 386, 480, 596 });
	}

	@Override
	public void run() {
		running = true;
		runDemo();
	}
	
	public void stop() {
		running = false;
		screen.setFullScreenWindow(null);
	}
	
	public void runDemo() {
		long start, diff, sleepTime;
		double frameTime = 1.0;

		while (running) {
			
			start = System.nanoTime();
	
			engine.tick(frameTime);
			drawFrame();
			
			diff = System.nanoTime() - start;
			sleepTime = FRAME_TIME_MILIS - (diff / 1000000);
			frameTime = 1.0;
			if (sleepTime < 0) {
				sleepTime = 0;
				frameTime = (diff / 1000000) / FRAME_TIME_MILIS;
			}
			
			if (Settings.debug) {
				fps = (diff != 0) ? 1000000000 / diff : FPS_LIMIT;
				if (fps > FPS_LIMIT) {
					fps = FPS_LIMIT;
				}
				System.out.println("frameTime " + diff + " ns, sleepTime " + sleepTime +" ms , FPS "+fps);
			}
			
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		
		System.exit(0);
	}
	
	private void drawFrame() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			bs = getBufferStrategy();
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(frame, 0, 0, getWidth(), getHeight(), null);
		if (Settings.debug) {
			drawDebugInfo(g);
		}
		bs.show();
	}

	private void drawDebugInfo(Graphics g) {
		String debugInfo = ((fps < 10) ? "0" : "") + fps + " fps";
		g.setColor(Color.YELLOW);
		g.drawString(debugInfo, 2, windowBarHeight +g.getFontMetrics().getHeight());
	}

	public void toggleFullscreen() {
		Settings.toggleFullscreen();
		if (Settings.fullScreen == false) {
			screen.setFullScreenWindow(null);
			setSize(WIDTH, HEIGHT);
		}
		else {
			screen.setFullScreenWindow(this);
		}
	}

}
