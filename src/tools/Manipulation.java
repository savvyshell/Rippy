package tools;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Manipulation {
	
	public static JSONArray listToJSONArray(ArrayList<String> lst) {
		JSONArray a = new JSONArray();
		for (int i = 0; i < lst.size(); i++) {
			a.put(lst.get(i));
		}
		return a;
	}
	
	public static JSONObject listToJSON(ArrayList<String> lst) {
		JSONObject j = new JSONObject();
		JSONArray a = new JSONArray();
		for (int i = 0; i < lst.size(); i++) {
			a.put(lst.get(i));
		}
		j.append("arr", a);
		return j;
	}
	
	public static <T>List<List<T>> chopIntoParts( final List<T> ls, final int iParts )
	{
	    final List<List<T>> lsParts = new ArrayList<List<T>>();
	    final int iChunkSize = ls.size() / iParts;
	    int iLeftOver = ls.size() % iParts;
	    int iTake = iChunkSize;

	    for( int i = 0, iT = ls.size(); i < iT; i += iTake )
	    {
	        if( iLeftOver > 0 )
	        {
	            iLeftOver--;

	            iTake = iChunkSize + 1;
	        }
	        else
	        {
	            iTake = iChunkSize;
	        }

	        lsParts.add( new ArrayList<T>( ls.subList( i, Math.min( iT, i + iTake ) ) ) );
	    }

	    return lsParts;
	}
	
}
