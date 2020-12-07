package cfg;

import java.util.ArrayList;
import java.util.Map;

public class SC extends C {

	public C targetCC;
	public ArrayList<Map<String, String>> links; 
	
	public SC(C targetCC) {
		super();
		this.targetCC = targetCC;
		super.setType(C.Type.subCategory);
	}
	
	
	
}
