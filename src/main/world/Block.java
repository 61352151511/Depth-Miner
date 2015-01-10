package main.world;

import java.io.Serializable;

public class Block implements Serializable {
	private static final long serialVersionUID = -1250847108208120413L;
	private int X = 0;
	private int Y = 0;
	private String Type = "Grass";
	
	public Block(int X, int Y, String Type) {
		this.X = X;
		this.Y = Y;
		this.Type = Type;
	}
	
	public int getX() { return this.X; }
	public int getY() { return this.Y; }
	public String getType() { return this.Type; }
	public void setType(String Type) { this.Type = Type; }
}