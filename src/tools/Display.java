package tools;

import java.util.ArrayList;
import java.util.Map;

import cfg.C;
import parallelism.RProcess;

public class Display {

	public RProcess p;
	
	private int padding = 3;
	private int verticalBorderLength = 25;
	private String verticalBorderChar = "-";
	
	public Display(RProcess process) {
		this.p = process;
	}
	
	public String getThread(int id) {
		return "Thread ["+id+"] :: ";
	}
	
	public void basicList(String title, ArrayList<String> lst) {
		header(title);
		for (int i = 0; i < lst.size(); i++) {
			listElem(lst.get(i), 0);
		}
	}
	
	public void list(String title, ArrayList<Map<String, String>> arr) {
		header(title);
		for (int i = 0; i < arr.size(); i++) {
			listElem(arr.get(i).get("href") + " :: " + arr.get(i).get("text"), 0);
		}
	}
	
	public void fromTo(C from, C to) {
		String o = String.format("\n%sMoving %s to %s\n%s", getPre(), from.getTitle(), to.getTitle(), getVerticalBorder());
		System.out.println(o);
	}
	
	public void std(String txt) {
		String o = String.format("%s%s", getPre(), txt);
		System.out.println(o);
	}
	
	public void header(String txt) {
		String o = String.format("\n%s%s\n%s", getPre(), txt, getVerticalBorder());
		System.out.println(o);
	}
	
	public void listElem(String txt, int level) {
		String pre = ">";
		for (int i = 0; i < level; i++) {
			pre += ">";
		}
		String o = String.format(pre + getPadded(level) + "%s%s", getPre(), txt);
		System.out.println(o);
	}
	
	public void error(String txt) {
		String o = String.format("%s\n%s%s\n%s", getVerticalBorder(), getPre(), txt, getVerticalBorder());
		System.out.println(o);
		System.exit(0);
	}
	
	// TOOLS
	public String getPre() {
		return "[" + p.id + "]: ";
	}
	
	public String getVerticalBorder() {
		String o = "";
		for (int i = 0; i < verticalBorderLength; i++) {
			o += verticalBorderChar;
		}
		return o;
	}
	
	public String getPadded(int level) {
		String o = "";
		for (int i = 0; i < (padding * (level + 1)); i++) {
			o+= " ";
		}
		return o;
	}
	
}
