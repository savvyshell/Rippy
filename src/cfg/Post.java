package cfg;

import java.util.HashMap;
import java.util.Map;

public class Post {

	public static int count = 0;
	public int jid = 0;
	public Map<String, String> fields = new HashMap<String, String>();
	
	public Post() {
		this.jid = Post.count;
		Post.count += 1;
	}
	
	public String toString() {
		String s = "";
		for (Map.Entry<String, String> row : fields.entrySet()) {
			s += String.format("[%s='%s'], ", row.getKey(), row.getValue());
		}
		s = s.substring(0, s.length()-2);
		return s;
	}
	
}
