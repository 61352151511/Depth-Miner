package main.world;

import java.io.Serializable;
import java.util.ArrayList;

public class Chunk implements Serializable {
	private static final long serialVersionUID = -2265361632258889482L;
	
	private int id;
	private ArrayList<Block> blocks = new ArrayList<Block>();
	
	public Chunk(int id, ArrayList<Block> blocks) {
		this.id = id;
		this.blocks = blocks;
	}
	
	public int getId() { return id; }
	public Block getBlock(int x) {
		if (blocks != null) {
			for (Block block : blocks) {
				if (block.getX() == x) return block;
			}
		}
		return null;
	}
}