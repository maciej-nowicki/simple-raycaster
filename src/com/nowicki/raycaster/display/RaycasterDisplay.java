package com.nowicki.raycaster.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import com.nowicki.raycaster.engine.Camera;
import com.nowicki.raycaster.engine.Element;
import com.nowicki.raycaster.engine.Engine;
import com.nowicki.raycaster.engine.Level;
import com.nowicki.raycaster.engine.Settings;
import com.nowicki.raycaster.engine.Texture;

public class RaycasterDisplay extends JFrame implements Runnable {

	private static final long serialVersionUID = 1L;
	
	public static final String FRAME_TITLE = "Raycaster demo";
	
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final int FPS_LIMIT = 50;
	public static final long FRAME_TIME_MILIS = 1000 / FPS_LIMIT;

	private boolean running;
	private Thread thread;
	private BufferedImage frame;
	
	private Engine engine;
	private Camera camera;
	private Map<Element, Texture> textures = new HashMap<>();
	
	private long fps;

	public RaycasterDisplay() throws IOException {
		Level level = new Level("data/raycaster/level.txt");
		loadTextures();
		
		engine = new Engine(WIDTH, HEIGHT, textures);
		engine.setLevel(level);
		
		camera = new Camera(3, 10, this);
		addKeyListener(camera);
		
		frame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		engine.setBuffer(((DataBufferInt)frame.getRaster().getDataBuffer()).getData());

		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setResizable(false);
		setTitle(FRAME_TITLE);
		setDefaultLookAndFeelDecorated(true);
		setBackground(Color.black);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	private void loadTextures() throws IOException {
		textures.put(Element.WALL_1, new Texture("data/raycaster/pics/greystone.png"));
		textures.put(Element.WALL_2, new Texture("data/raycaster/pics/bluestone.png"));
		textures.put(Element.WALL_3, new Texture("data/raycaster/pics/colorstone.png"));
		textures.put(Element.WALL_WOOD, new Texture("data/raycaster/pics/wood.png"));
		textures.put(Element.BARREL, new Texture("data/raycaster/pics/barrel.png"));
	}

	@Override
	public void run() {
		running = true;
		runDemo();
	}
	
	public void stop() {
		running = false;
	}
	
	public void runDemo() {
		long start, diff, sleepTime;

		while (running) {
			
			start = System.nanoTime();
	
			engine.tick(camera, 1);
			drawFrame();
			
			diff = System.nanoTime() - start;
			sleepTime = FRAME_TIME_MILIS - (diff / 1000000);
			if (sleepTime < 0) {
				sleepTime = 0;
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
		g.drawString(debugInfo, 600, 30);
	}

	public void toggleFullscreen() {
		Settings.toggleFullscreen();
		if (Settings.fullScreen == false) {
			setSize(WIDTH, HEIGHT);
		}
		else {
			setLocation(0, 0);
			setSize((int)getToolkit().getScreenSize().getWidth(), (int)getToolkit().getScreenSize().getHeight());
		}
	}

}
