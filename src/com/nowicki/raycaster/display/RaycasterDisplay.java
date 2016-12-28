package com.nowicki.raycaster.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;

import javax.swing.JFrame;

import com.nowicki.raycaster.engine.Engine;

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
	
	private boolean debug = true;
	private long fps;

	public RaycasterDisplay(Engine engine) {
		frame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		DataBuffer buffer = frame.getRaster().getDataBuffer();

		System.out.println(buffer.getDataType());
		
		engine.setBuffer(frame);

		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setResizable(false);
		setTitle(FRAME_TITLE);
		setBackground(Color.black);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void run() {
		running = true;
		runDemo();
	}
	
	public void runDemo() {
		long start, diff, sleepTime;

		while (running) {
			
			start = System.currentTimeMillis();
	
			drawFrame();
			
			diff = System.currentTimeMillis() - start;
			sleepTime = FRAME_TIME_MILIS - diff;
			if (sleepTime < 0) {
				sleepTime = 0;
			}
			
			if (debug) {
				fps = (diff != 0) ? 1000 / diff : FPS_LIMIT;
				if (fps > FPS_LIMIT) {
					fps = FPS_LIMIT;
				}
				System.out.println("Frame time: "+diff+" SleepTime: "+sleepTime+" FPS: "+fps);
			}
			
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
	}
	
	private void drawFrame() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			bs = getBufferStrategy();
		}
		
		Graphics g = bs.getDrawGraphics();
		g.drawImage(frame, 0, 0, null);
		if (debug) {
			drawDebugInfo(g);
		}
		bs.show();
	}

	private void drawDebugInfo(Graphics g) {
		String debugInfo = ((fps < 10) ? "0" : "") + fps + " fps";
		g.setColor(Color.YELLOW);
		g.drawString(debugInfo, 600, 20);
	}

}
