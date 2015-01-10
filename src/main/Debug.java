package main;

public class Debug {
	public static void debug(Object... olist) {
		System.out.println("***********************");
		int Count = 0;
		for (Object o : olist) {
			System.out.println("[" + Count + "] " + String.valueOf(o));
			Count ++;
		}
		System.out.println("***********************");
	}
}