package tools;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Sel {
	
	public static String getTextByText(WebDriver webDriver, String parentElement, String txt, String start, String endChar, boolean verbose) {
		String xpath = "//"+parentElement+"[contains(., '"+txt+"')]";
		String val = getText(webDriver.findElement(By.xpath(xpath)), verbose);

		val = val.substring(val.indexOf(start) + start.length()).trim();
		
		if (endChar.length() > 0)
			val = val.substring(0, val.indexOf(endChar));
		
		return val;
	}
	
	public static String getTextByXPath(WebDriver webDriver, String xpath, boolean verbose) {
		WebElement titleElem = webDriver.findElement(By.xpath(xpath));
		String txt = Sel.getText(titleElem, verbose);
		return txt;
	}
	
	public static String getText(WebElement elem) {
		String txt = (elem.getText().length() <= 0 ? elem.getAttribute("innerHTML").trim() : elem.getText().trim());
		System.out.println(txt);
		return txt;
	}
	
	public static String getText(WebElement elem, boolean print) {
		String txt = (elem.getText().length() <= 0 ? elem.getAttribute("innerHTML").trim() : elem.getText().trim());
		if (print)
			System.out.println(txt);
		return txt;
	}
	
	public static ArrayList<String> getList(WebElement[] elems, boolean verbose) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < elems.length; i++) {
			String txt = getText(elems[i], verbose);
			list.add(txt);
		}
		return list;
	}
	
	public static ArrayList<String> getListWithList(List<WebElement> elems, boolean verbose) {
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < elems.size(); i++) {
			String txt = getText(elems.get(i), verbose);
			list.add(txt);
		}
		return list;
	}
	
	public static ArrayList<String> getListOnCondition(List<WebElement> elems, String[] arr, boolean verbose) {
		ArrayList<String> lst = new ArrayList<String>();
		for (int i = 0; i < elems.size(); i++) {
			String txt = getText(elems.get(i), verbose).toLowerCase();
			for (int x = 0; x < arr.length; x++) {
				if (txt.contains(arr[x])) {
					lst.add(txt);
				}
			}
		}
		return lst;
	}
	
	public static List<WebElement> getMultiXPathDoubleProng(WebDriver webDriver, String xpath1, String xpath2) {
		List<WebElement> lst = webDriver.findElements(By.xpath(xpath1));
		if (lst.size() == 0) {
			try {
				lst = webDriver.findElements(By.xpath(xpath2));
			} catch (InvalidSelectorException e) {
				e.printStackTrace();
			}
		}
		return lst;
	}
	
}
