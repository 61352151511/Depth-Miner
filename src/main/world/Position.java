package main.world;

import java.io.Serializable;

public class Position implements Serializable {
	private static final long serialVersionUID = 6074533649414155724L;
	private int X = 0;
	private int Y = 0;
	
	public Position(int X, int Y) {
		this.X = X;
		this.Y = Y;
	}
	
	public int getX() { return X; }
	public int getY() { return Y; }
	public void setX(int X) { this.X = X; }
	public void setY(int Y) { this.Y = Y; }
	
	public boolean equals(Object comparison) {
		if (comparison == this) return true;
		if (comparison instanceof Position) {
			Position pos = (Position) comparison;
			return (pos.getX() == this.getX() && pos.getY() == this.getY());
		}
		return false;
	}
}