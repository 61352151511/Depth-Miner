package main;

import java.io.*;

import main.notificationsystem.Notifications;
import main.world.Blocks;

public class GameData {
	public static void saveMinedBlocks(Blocks minedBlocks, boolean notify) {
		try {
			FileOutputStream fileOut = new FileOutputStream("savedata.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(minedBlocks);
			out.close();
			fileOut.close();
			if (notify) Notifications.Notify("Saved!", Notifications.Seconds(3));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Blocks loadMinedBlocks() {
		Blocks loadedBlocks = null;
		try {
			FileInputStream fileIn = new FileInputStream("savedata.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			loadedBlocks = (Blocks) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
			return new Blocks();
		} catch (ClassNotFoundException e) {
			System.out.println("No save data found.");
			e.printStackTrace();
			return new Blocks();
		}
		return loadedBlocks;
	}
}