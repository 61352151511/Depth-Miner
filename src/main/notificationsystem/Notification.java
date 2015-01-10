package main.notificationsystem;

public class Notification {
	private String text;
	private int Lifetime;
	
	protected Notification(String text, int Lifetime) {
		this.text = text;
		this.Lifetime = Lifetime;
	}
	
	public String getText() { return this.text; }
	public int getLifetime() { return this.Lifetime; }
	public void killOff() {
		Lifetime --;
	}
}