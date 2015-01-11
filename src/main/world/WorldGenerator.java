package main.world;

import java.util.ArrayList;
import java.util.Random;

import main.data.Data;
import main.data.SaveData;

public class WorldGenerator {
	private static Random generator = new Random(System.currentTimeMillis());
	
	public static void generateChunk(int width, int id) {
		if (id > Data.getGeneratedLayers()) {
			ArrayList<Block> blocks = new ArrayList<Block>();
			if (id == 0) {
				for (int i = 0; i <= width; i ++) {
					blocks.add(new Block(i, id, "Grass"));
				}
			} else if (id > 0 && id <= 2) {
				for (int i = 0; i <= width; i ++) {
					blocks.add(new Block(i, id, "Dirt"));
				}
			} else {
				for (int i = 0; i <= width; i ++) {
					blocks.add(new Block(i, id, getNextStone(id)));
				}
			}
			Chunk chunk = new Chunk(id, blocks);
			SaveData.addLoadedChunk(chunk);
			SaveData.saveChunk(chunk);
			Data.setGeneratedLayers(id);
		}
	}
	
	private static String getNextStone(int id) {
		int nextRand1 = generator.nextInt(100);
		if (nextRand1 < 8) {
			int nextRand2 = generator.nextInt(100);
			if (nextRand2 < 5 && id > 18) return "Diamond";
			if (nextRand2 < 20 && id > 11) return "Gold";
			if (nextRand2 < 45 && id > 3) return "Coal";
			if (nextRand2 >= 45) return "Iron";
		}
		return "Stone";
	}
}