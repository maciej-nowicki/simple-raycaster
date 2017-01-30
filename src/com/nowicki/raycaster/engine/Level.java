package com.nowicki.raycaster.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nowicki.raycaster.engine.Element.Entry;
import com.nowicki.raycaster.engine.Element.EntryType;

public class Level {

	public static int DEFAULT_MAP_WIDTH = 20;
	public static int DEFAULT_MAP_HEIGHT = 20;
	
	private Element[][] map;
	private List<Sprite> sprites = new ArrayList<>();
	
	private int width;
	private int height;
	
	public Level(String filename, Map<Entry, Texture> textures) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(filename)));
			String [] lines = content.split("\n");
			height = lines.length;
			for (int j=0; j<height; j++) {
				String [] entries = lines[j].split(",");
				width = entries.length;
				if (map == null) {
					map = new Element[width][height];
				}
				for (int i=0; i<width; i++) {
	
					Set<Entry> entriesSet = new HashSet<>();
					
					String entryId = entries[i];
					for (int p=0; p<entryId.length(); p++) {
						Entry entry = Entry.fromValue(entryId.charAt(p));
						if (entry.getType() == EntryType.SPRITE) {
							Sprite sprite = new Sprite(i + 0.5, j + 0.5, textures.get(entry));
							sprites.add(sprite);
						}
						else {
							entriesSet.add(entry);
						}
					}
					
					map[i][j] = new Element(entriesSet, textures);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Element[][] getMap() {
		return map;
	}
	
	public List<Sprite> getSprites() {
		return sprites;
	}
	
	public boolean isWall(int x, int y) {
		return map[x][y].isWall();
	}

	public boolean isObstacle(int x, int y) {
		return map[x][y].isObstacle();
	}
	
	public Element getElement(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return null;
		}
		return map[x][y];
	}
}
