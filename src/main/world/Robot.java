package main.world;

import java.io.Serializable;

import main.DepthMiner;
import main.data.Data;

public class Robot implements Serializable {

	private static final long serialVersionUID = 3362107943765452993L;
	private boolean anchored;
	private int fallTime = 0;
	private Position position = new Position(0, -200);
	
	public boolean isAnchored() { return anchored; }
	public void toggleAnchor() { anchored = !anchored; }
	public Position getPosition() { return position; }
	public int getX() { return position.getX(); }
	public int getY() { return position.getY(); }
	public int getBlockX() { return getX() / 50; }
	public int getBlockY() { return getY() / 50; }
	
	public void moveLeft() { position.setX(position.getX() - 1); }
	public void moveRight() { position.setX(position.getX() + 1); }
	public void moveUp() {
		if (!anchored) {
			position.setY(position.getY() - 1);
			fallTime = 0;
		}
	}
	public void moveDown() {
		if (!anchored) {
			position.setY(position.getY() + Math.max(1, Math.min(4, fallTime / 50)));
		}
	}
	
	/*
	 * 0 - Left
	 * 1 - Right
	 * 2 - Up
	 * 3 - Down
	 */
	public void update(boolean[] keys, boolean inventoryOpen) {
		if (keys[0] && !keys[1] && !inventoryOpen) {
			boolean C1 = true;
			boolean C2 = true;
			int XCheckFor = (position.getX() / 50) - 1;
			int YCheckFor1 = getBlockY();
			int YCheckFor2 = position.getY() % 50 == 0 ? getBlockY() - 1 : getBlockY() + 1;
			if (position.getY() > -50 && position.getY() < 0) YCheckFor2 = getBlockY() - 1;
			Block block1 = Data.getBlock(new Position(XCheckFor, YCheckFor1));
			Block block2 = Data.getBlock(new Position(XCheckFor, YCheckFor2));
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
				if (position.getX() % 50 == 0) {
					if (position.getY() % 50 == 0) {
						if (!C1) moveLeft();
					} else {
						if (!C1 && !C2) moveLeft();
					}
				} else {
					moveLeft();
				}
			} else {
				moveLeft();
			}
		}
		
		if (keys[1] && !keys[0] && !inventoryOpen) {
			boolean C1 = true;
			boolean C2 = true;
			int XCheckFor = getBlockX() + 1;
			int YCheckFor1 = getBlockY();
			int YCheckFor2 = position.getY() % 50 == 0 ? getBlockY() - 1 : getBlockY() + 1;
			if (position.getY() > -50 && position.getY() < 0) YCheckFor2 = getBlockY() - 1;
			Block block1 = Data.getBlock(new Position(XCheckFor, YCheckFor1));
			Block block2 = Data.getBlock(new Position(XCheckFor, YCheckFor2));
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
				if (position.getX() % 50 == 0) {
					if (position.getY() % 50 == 0) {
						if (!C1) moveRight();
					} else {
						if (!C1 && !C2) moveRight();
					}
				} else {
					moveRight();
				}
			} else {
				moveRight();
			}
		}
		
		if (keys[2] && !keys[3] && !inventoryOpen) {
			boolean C1 = true;
			boolean C2 = true;
			int YCheckFor = ((position.getY() + 50) / 50) - 1;
			if (position.getY() % 50 == 0) YCheckFor --;
			int XCheckFor1 = (position.getX() / 50);
			int XCheckFor2 = position.getX() % 50 == 0 ? getBlockX() - 1 : getBlockX() + 1;
			Block block1 = Data.getBlock(new Position(XCheckFor1, YCheckFor));
			Block block2 = Data.getBlock(new Position(XCheckFor2, YCheckFor));
			if (block1 != null) {
				if (block1.getType().equalsIgnoreCase("Air")) C1 = false;
			}
			if (block2 != null) {
				if (block2.getType().equalsIgnoreCase("Air")) C2 = false;
			}
			if (YCheckFor > -1) {
				if (position.getX() % 50 == 0) {
					if (!C1) moveUp();
				} else {
					if (!C1 && !C2) moveUp();
				}
			} else {
				moveUp();
			}
		}
		
		if (position.getX() < 0) {
			position.setX(0);
		}
		if (position.getX() > DepthMiner.getFrameWidth() - 50) {
			position.setX(DepthMiner.getFrameWidth() - 50);
		}
		
		if ((!inventoryOpen && !keys[2]) || inventoryOpen) {			
			boolean C1 = true;
			boolean C2 = true;
			int YCheckFor = (position.getY() + 50) / 50;
			int XCheckFor1 = getBlockX();
			int XCheckFor2 = (position.getX() + 50) / 50;
			Block block1 = Data.getBlock(new Position(XCheckFor1, YCheckFor));
			Block block2 = Data.getBlock(new Position(XCheckFor2, YCheckFor));
			if (block1 != null) {
				if (!block1.getType().equalsIgnoreCase("Air")) C1 = false;
			}
			if (block2 != null) {
				if (!block2.getType().equalsIgnoreCase("Air")) C2 = false;
			}
			if (position.getY() >= -50) {
				if (position.getX() % 50 == 0) {
					if (C1) {
						moveDown();
						fallTime ++;
					} else {
						fallTime = 0;
					}
				} else {
					if (C1 && C2) {
						moveDown();
						fallTime ++;
					} else {
						fallTime = 0;
					}
				}
			} else {
				moveDown();
				fallTime ++;
			}
		}
	}
}