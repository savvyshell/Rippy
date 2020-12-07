package tools;

import java.util.ArrayList;

public class Arr {

	public static boolean doesItExist(ArrayList<String> arr, String elem) {
		for(int i = 0; i<arr.size(); i++) {
			if (arr.get(i).equalsIgnoreCase(elem)) {
				return true;
			}
		}
		return false;
	}
	
}
