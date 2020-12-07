package cfg;

import java.util.ArrayList;

public interface Category {

	public String getTitle();
	public void setTitle(String title);
	
	public String getTag();
	public void setTag(String tag);
	
	public ArrayList<C> getRelations();
	public void addRelation(C rel);
	
	public String getType();
	public void setType(String type);
	
	public String getXPath();
	public void setXPath(String xpath);
	
	public String getValue();
	public void setValue(String val);
	
}
