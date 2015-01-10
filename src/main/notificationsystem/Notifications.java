package main.notificationsystem;

import java.util.ArrayList;

public class Notifications {
	private static ArrayList<Notification> notifications = new ArrayList<Notification>();
	
	public static int Seconds(int Ticks) {
		return (1000 / 20) * Ticks;
	}
	
	public static void Notify(String Text, int Time) {
		if (!notifications.contains(new Notification(Text, Time))) notifications.add(new Notification(Text, Time));
	}
	
	public static void tick() {
		ArrayList<Notification> ToKill = new ArrayList<Notification>();
		for (Notification not : notifications) {
			not.killOff();
			if (not.getLifetime() <= 0) {
				ToKill.add(not);
			}
		}
		if (!ToKill.isEmpty()) {
			ArrayList<Notification> newList = new ArrayList<Notification>();
			for (Notification not : notifications) {
				if (!ToKill.contains(not)) {
					newList.add(not);
				}
			}
			notifications = newList;
		}
	}
	
	public static ArrayList<Notification> getNotifications() {
		return notifications;
	}
}