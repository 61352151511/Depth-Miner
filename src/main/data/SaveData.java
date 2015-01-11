package main.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import main.DepthMiner;
import main.world.Chunk;
import main.world.WorldGenerator;

public class SaveData {
	public static ArrayList<Chunk> loadedChunks = new ArrayList<Chunk>();;
	
	public static void saveChunk(Chunk chunk) {
		try {
			new File("world/").mkdirs();
			FileOutputStream fileOut = new FileOutputStream("world/chunk" + chunk.getId() + ".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(chunk);
			out.close();
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveLoadedChunks() {
		Iterator<Chunk> loadedChunksIterator = loadedChunks.iterator();
		while (loadedChunksIterator.hasNext()) {
			Chunk chunk = loadedChunksIterator.next();
			saveChunk(chunk);
		}
	}
	
	public static void saveImportantData(HashMap<String, Object> importantData) {
		try {
			new File("world/").mkdirs();
			FileOutputStream fileOut = new FileOutputStream("world/savedata.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(importantData);
			out.close();
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked") public static HashMap<String, Object> loadImportantData() {
		HashMap<String, Object> retHash = new HashMap<String, Object>();
		try {
			FileInputStream fileIn = new FileInputStream("world/savedata.ser");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			retHash = (HashMap<String, Object>) in.readObject();
			in.close();
			fileIn.close();
		}
		catch (IOException e) {}
		catch (ClassNotFoundException e) {}
		return retHash;
	}
	
	public static boolean chunkLoaded(int id) {
		Iterator<Chunk> loadedChunksIterator = loadedChunks.iterator();
		while (loadedChunksIterator.hasNext()) {
			Chunk chunk = loadedChunksIterator.next();
			if (chunk.getId() == id) return true;
		}
		return false;
	}
	
	public static void unloadChunk(int id) {
		Iterator<Chunk> loadedChunksIterator = loadedChunks.iterator();
		while (loadedChunksIterator.hasNext()) {
			Chunk chunk = loadedChunksIterator.next();
			if (chunk.getId() == id) {
				saveChunk(chunk);
				loadedChunksIterator.remove();
			}
		}
	}
	
	public static Chunk getChunk(int id) {
		Iterator<Chunk> loadedChunksIterator = loadedChunks.iterator();
		while (loadedChunksIterator.hasNext()) {
			Chunk chunk = loadedChunksIterator.next();
			if (chunk.getId() == id) return chunk;
		}
		return null;
	}
	
	public static void addLoadedChunk(Chunk chunk) {
		if (!chunkLoaded(chunk.getId())) {
			loadedChunks.add(chunk);
		}
	}
	
	public static void loadChunk(int id) {
		if (!chunkLoaded(id)) {
			try {
				FileInputStream fileIn = new FileInputStream("world/chunk" + id + ".ser");
				ObjectInputStream in = new ObjectInputStream(fileIn);
				Chunk chunk = (Chunk) in.readObject();
				loadedChunks.add(chunk);
				in.close();
				fileIn.close();
			} catch (IOException e) {
				WorldGenerator.generateChunk(DepthMiner.getChunkSize(), id);
			} catch (ClassNotFoundException e) {
				WorldGenerator.generateChunk(DepthMiner.getChunkSize(), id);
			}
		}
	}
	
	public static void reset() {
		loadedChunks = new ArrayList<Chunk>();
	}
}