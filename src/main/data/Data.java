package main.data;

import java.io.File;
import java.util.HashMap;

import main.world.Block;
import main.world.Position;
import main.world.Robot;

public class Data {
	private static int generatedLayers = -1;
	private static int depth = -200;
	private static Robot robot = new Robot();
	private static HashMap<String, Integer> minedBlocks = new HashMap<String, Integer>();
	
	public static int getGeneratedLayers() { return generatedLayers; }
	public static void setGeneratedLayers(int id) { generatedLayers = id; }
	public static int getDepth() { return depth; }
	public static void setDepth(int newDepth) { depth = newDepth; }
	public static boolean layerGenerated(int layer) { return layer <= generatedLayers; }
	public static boolean blockNull(Position position) { return getBlock(position) == null; }
	public static Robot getRobot() { return robot; }
	public static boolean isBlockMined(Position position) { return getBlock(position).getType().equalsIgnoreCase("Air"); }
	public static HashMap<String, Integer> getMinedBlocks() { return minedBlocks; }
	
	public static Block getBlock(Position position) {
		if (SaveData.chunkLoaded(position.getY())) {
			return SaveData.getChunk(position.getY()).getBlock(position.getX());
		}
		return null;
	}
	
	public static void mineBlock(Position position) {
		minedBlocks.put(getBlock(position).getType(), minedBlocks.get(getBlock(position).getType()) == null ? 0 : minedBlocks.get(getBlock(position).getType()) + 1);
		getBlock(position).setType("Air");
	}
	
	public static void save() {
		HashMap<String, Object> saveData = new HashMap<String, Object>() {
			private static final long serialVersionUID = 6210513336277597868L;
			{
				put("Generated Layers", generatedLayers);
				put("Depth", depth);
				put("Robot", robot);
				put("Mined Blocks", minedBlocks);
			}
		};
		SaveData.saveImportantData(saveData);
	}
	
	@SuppressWarnings("unchecked") public static void load() {
		HashMap<String, Object> loadData = SaveData.loadImportantData();
		if (loadData.containsKey("Generated Layers")) generatedLayers = (int) loadData.get("Generated Layers");
		if (loadData.containsKey("Depth")) depth = (int) loadData.get("Depth");
		if (loadData.containsKey("Robot")) robot = (Robot) loadData.get("Robot");
		if (loadData.containsKey("Mined Blocks")) minedBlocks = (HashMap<String, Integer>) loadData.get("Mined Blocks");
	}
	
	public static void reset() {
		generatedLayers = -1;
		depth = -200;
		robot = new Robot();
		SaveData.reset();
		
		File save = new File("world/");
		String[] files = save.list();
		int len = files.length;
		for (int i = 0; i < len; i ++) {
			File file = new File(save, files[i]);
			file.delete();
		}
		save.delete();
	}
}