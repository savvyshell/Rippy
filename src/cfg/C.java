package cfg;

import java.util.ArrayList;
import java.util.Map;

public class C implements Category {
	
	public static class Type {
		public static String subCategory = "SC";
		public static String contentCategory = "CC";
	}
	
	// If Init == multiple URLs?
	public ArrayList<String> urls = new ArrayList<String>();
	
	public String title;
	public String type;
	public String value = "";
	
	public String tag = ""; // Identifying if on right page
	public String xpath = ""; // List of links
	public String xpath2 = ""; // backup xpath
	
	public boolean prePage = false;
	public String prePageXPath = "";
	public String prePageTag = "";
	
	public String callback = "";
	
	ArrayList<C> relations;
	
	public ArrayList<Map<String, String>> fields;
	
	public C() {
		this.relations = new ArrayList<C>();
	}
	
	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public ArrayList<C> getRelations() {
		return relations;
	}

	@Override
	public void addRelation(C rel) {
		relations.add(rel);
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getXPath() {
		return xpath;
	}

	@Override
	public void setXPath(String xpath) {
		this.xpath = xpath;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String val) {
		this.value = val;
	}

}
