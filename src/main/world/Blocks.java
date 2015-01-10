package main.world;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import main.DepthMiner;
import main.utilities.HashMapUtilities;

public class Blocks implements Serializable {
	private static final long serialVersionUID = 5654403921564391807L;
	private HashMap<Position, Block> BlockLocations = new HashMap<>();
	private HashMap<Integer, Integer> LayerWidths = new HashMap<>();
	private HashMap<String, Integer> MinedBlocks = new HashMap<>();
	private ArrayList<Integer> GeneratedLayers = new ArrayList<>();
	private int[] RobotLocation = new int[] {0, -200};
	private boolean RobotAnchored = false;
	private Random Generator = new Random(System.currentTimeMillis());
	private int depth = -200;
	
	/* FOR SAVE DATA */
	
	public void setBlockLocations(HashMap<Position, Block> BlockLocations) { this.BlockLocations = BlockLocations; }
	public HashMap<Position, Block> getBlockLocations() { return this.BlockLocations; }
	public void setMinedBlocks(HashMap<String, Integer> MinedBlocks) { this.MinedBlocks = MinedBlocks; }
	public HashMap<String, Integer> getMinedBlocks() { return this.MinedBlocks; }
	public void setGeneratedLayers(ArrayList<Integer> GeneratedLayers) { this.GeneratedLayers = GeneratedLayers; }
	public ArrayList<Integer> getGeneratedLayers() { return this.GeneratedLayers; }
	public void setRobotLocation(int[] RobotLocation) { this.RobotLocation = RobotLocation; }
	public int[] getRobotLocation() { return this.RobotLocation; }
	public void setRobotAnchored(boolean RobotAnchored) { this.RobotAnchored = RobotAnchored; }
	public boolean getRobotAnchored() { return this.RobotAnchored; }
	public void setGenerator(Random Generator) { this.Generator = Generator; }
	public Random getGenerator() { return this.Generator; }
	public void setDepth(int depth) { this.depth = depth; }
	public int getDepth() { return depth; }
	
	/* OTHER CODE */
	public void generateLayer(int Layer, int Start, int Width) { generateLayer(Layer, Start, Width, false); }
	public void generateLayer(int Layer, int Start, int Width, boolean Force) {
		if (!GeneratedLayers.contains(Layer) || Force) {
			if (Layer == 0) {
				for (int i = Start; i <= Width; i ++) {
					BlockLocations.put(new Position(i, Layer), new Block(i, Layer, "Grass"));
				}
			} else if (Layer > 0 && Layer <= 2) {
				for (int i = Start; i <= Width; i ++) {
					BlockLocations.put(new Position(i, Layer), new Block(i, Layer, "Dirt"));
				}
			} else {
				for (int i = Start; i <= Width; i ++) {
					BlockLocations.put(new Position(i, Layer), new Block(i, Layer, getNextStone(Layer)));
				}
			}
			if (!GeneratedLayers.contains(Layer)) GeneratedLayers.add(Layer);
			LayerWidths.put(Layer, Width);
		}
	}
	
	private String getNextStone(int Layer) {
		int NextRand1 = Generator.nextInt(100);
		if (NextRand1 < 8) {
			int NextRand2 = Generator.nextInt(100);
			if (NextRand2 < 5 && Layer > 18) return "Diamond";
			if (NextRand2 < 20 && Layer > 11) return "Gold";
			if (NextRand2 < 45 && Layer > 3) return "Coal";
			if (NextRand2 >= 45) return "Iron";
		}
		return "Stone";
	}
	
	public Block getBlock(int X, int Y) { return getBlock(new Position(X, Y)); }
	public Block getBlock(Position pos) {
		return HashMapUtilities.containsKey(BlockLocations, pos) ? HashMapUtilities.get(BlockLocations, pos) : null;
	}
	public boolean layerGenerated(int Layer) { return GeneratedLayers.contains(Layer); }
	public int layerWidth(int Layer) {
		if (layerGenerated(Layer)) {
			if (LayerWidths.containsKey(Layer)) return LayerWidths.get(Layer);
		}
		return 0;
	}
	public boolean blockNull(int X, int Y) { return getBlock(X, Y) == null; }
	
	
	
	public ArrayList<Block> getBlocks() {
		return (ArrayList<Block>) HashMapUtilities.valuesToArrayList(BlockLocations);
	}
	public void MineBlock(int X, int Y) {
		if (!blockNull(X, Y)) {
			String Type = getBlock(X, Y).getType();
			if (Type != "Air") {
				if (MinedBlocks.containsKey(Type)) {
					MinedBlocks.put(Type, MinedBlocks.get(Type) + 1);
				} else {
					MinedBlocks.put(Type, 1);
				}
			}
			getBlock(X, Y).setType("Air");
		}
	}
	public void Reset() {
		RobotAnchored = false;
		Generator.setSeed(System.currentTimeMillis());
		BlockLocations.clear();
		GeneratedLayers.clear();
		MinedBlocks.clear();
		RobotLocation = new int[] {0, -200};
		depth = -200;
	}
	
	public int getRobotX() { return RobotLocation[0]; }
	public int getRobotY() { return RobotLocation[1]; }
	public int getRobotBlockX() { return getRobotX() / 50; }
	public int getRobotBlockY() { return getRobotY() / 50; }
	
	public void moveRobotLeft() { RobotLocation[0] --; }
	public void moveRobotRight() { RobotLocation[0] ++; }
	public void moveRobotUp() { if (!RobotAnchored) RobotLocation[1] --; }
	public void moveRobotDown() { if (!RobotAnchored) RobotLocation[1] ++; }
	public void toggleRobotAnchor() { RobotAnchored = !RobotAnchored; }
	public boolean isRobotAnchored() { return RobotAnchored; }
	
	public boolean isMined(int X, int Y) {
		if (!blockNull(X, Y)) {
			return getBlock(X, Y).getType().equalsIgnoreCase("Air");
		}
		return false;
	}
	
	/*
	 * 0 - Left
	 * 1 - Right
	 * 2 - Up
	 * 3 - Down
	 */
	public void updateRobot(boolean[] keys, boolean inventoryOpen) {
		if (keys[0] && !keys[1] && !inventoryOpen) {
			boolean C1 = true;
			boolean C2 = true;
			int XCheckFor = (RobotLocation[0] / 50) - 1;
			int YCheckFor1 = getRobotBlockY();
			int YCheckFor2 = RobotLocation[1] % 50 == 0 ? getRobotBlockY() - 1 : getRobotBlockY() + 1;
			if (RobotLocation[1] > -50 && RobotLocation[1] < 0) YCheckFor2 = getRobotBlockY() - 1;
			Block block1 = getBlock(XCheckFor, YCheckFor1);
			Block block2 = getBlock(XCheckFor, YCheckFor2);
			if (block1 != null) {
				if (block1.getType().equalsIgnoreCase("Air")) C1 = false;
			} else {
				C1 = false;
			}
			if (block2 != null) {
				if (block2.getType().equalsIgnoreCase("Air")) C2 = false;
			} else {
				C2 = false;
			}
			if (YCheckFor1 >= 0 && YCheckFor2 >= -1) {
				if (RobotLocation[0] % 50 == 0) {
					if (RobotLocation[1] % 50 == 0) {
						if (!C1) moveRobotLeft();
					} else {
						if (!C1 && !C2) moveRobotLeft();
					}
				} else {
					moveRobotLeft();
				}
			} else {
				moveRobotLeft();
			}
		}
		
		if (keys[1] && !keys[0] && !inventoryOpen) {
			boolean C1 = true;
			boolean C2 = true;
			int XCheckFor = getRobotBlockX() + 1;
			int YCheckFor1 = getRobotBlockY();
			int YCheckFor2 = RobotLocation[1] % 50 == 0 ? getRobotBlockY() - 1 : getRobotBlockY() + 1;
			if (RobotLocation[1] > -50 && RobotLocation[1] < 0) YCheckFor2 = getRobotBlockY() - 1;
			Block block1 = getBlock(XCheckFor, YCheckFor1);
			Block block2 = getBlock(XCheckFor, YCheckFor2);
			if (block1 != null) {
				if (block1.getType().equalsIgnoreCase("Air")) C1 = false;
			} else {
				C1 = false;
			}
			if (block2 != null) {
				if (block2.getType().equalsIgnoreCase("Air")) C2 = false;
			} else {
				C2 = false;
			}
			if (YCheckFor1 >= 0 && YCheckFor2 >= -1) {
				if (RobotLocation[0] % 50 == 0) {
					if (RobotLocation[1] % 50 == 0) {
						if (!C1) moveRobotRight();
					} else {
						if (!C1 && !C2) moveRobotRight();
					}
				} else {
					moveRobotRight();
				}
			} else {
				moveRobotRight();
			}
		}
		
		if (keys[2] && !keys[3] && !inventoryOpen) {
			boolean C1 = true;
			boolean C2 = true;
			int YCheckFor = ((RobotLocation[1] + 50) / 50) - 1;
			if (RobotLocation[1] % 50 == 0) YCheckFor --;
			int XCheckFor1 = (RobotLocation[0] / 50);
			int XCheckFor2 = RobotLocation[0] % 50 == 0 ? getRobotBlockX() - 1 : getRobotBlockX() + 1;
			Block block1 = getBlock(XCheckFor1, YCheckFor);
			Block block2 = getBlock(XCheckFor2, YCheckFor);
			if (block1 != null) {
				if (block1.getType().equalsIgnoreCase("Air")) C1 = false;
			}
			if (block2 != null) {
				if (block2.getType().equalsIgnoreCase("Air")) C2 = false;
			}
			if (YCheckFor > -1) {
				if (RobotLocation[0] % 50 == 0) {
					if (!C1) moveRobotUp();
				} else {
					if (!C1 && !C2) moveRobotUp();
				}
			} else {
				moveRobotUp();
			}
		}
		
		if (RobotLocation[0] < 0) {
			RobotLocation[0] = 0;
		}
		if (RobotLocation[0] > DepthMiner.getFrameWidth() - 50) {
			RobotLocation[0] = DepthMiner.getFrameWidth() - 50;
		}
		
		if ((!inventoryOpen && !keys[2]) || inventoryOpen) {			
			boolean C1 = true;
			boolean C2 = true;
			int YCheckFor = (RobotLocation[1] + 50) / 50;
			int XCheckFor1 = getRobotBlockX();
			int XCheckFor2 = (RobotLocation[0] + 50) / 50;
			Block block1 = getBlock(XCheckFor1, YCheckFor);
			Block block2 = getBlock(XCheckFor2, YCheckFor);
			if (block1 != null) {
				if (!block1.getType().equalsIgnoreCase("Air")) C1 = false;
			}
			if (block2 != null) {
				if (!block2.getType().equalsIgnoreCase("Air")) C2 = false;
			}
			if (RobotLocation[1] >= -50) {
				if (RobotLocation[0] % 50 == 0) {
					if (C1) moveRobotDown();
				} else {
					if (C1 && C2) moveRobotDown();
				}
			} else {
				moveRobotDown();
			}
		}
	}
}