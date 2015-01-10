package main.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HashMapUtilities {
	public static <T> List<T> valuesToArrayList(HashMap<?, T> TheHashMap) {
		ArrayList<T> RetList = new ArrayList<>();
		for (T Value : TheHashMap.values()) {
			RetList.add(Value);
		}
		return RetList;
	}
	
	public static <T> List<T> keysToArrayList(HashMap<T, ?> TheHashMap) {
		ArrayList<T> RetList = new ArrayList<>();
		for (T Value : TheHashMap.keySet()) {
			RetList.add(Value);
		}
		return RetList;
	}
	
	public static <T, S> boolean containsKey(HashMap<T, ?> TheHashMap, S Compare) {
		for (T Value : TheHashMap.keySet()) {
			if (Value.equals(Compare)) {
				return true;
			}
		}
		return false;
	}
	
	public static <T, S, U> S get(HashMap<T, S> TheHashMap, U Request) {
		int Count = 0;
		for (T Key : TheHashMap.keySet()) {
			Count ++;
			if (Key.equals(Request)) break;
		}
		int Count2 = 0;
		for (S Value : TheHashMap.values()) {
			Count2 ++;
			if (Count == Count2) return Value;
		}
		return null;
	}
}