package tools;

import org.openqa.selenium.WebDriver;

import cfg.C;

public class Check {
	
	public static String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	public static String[] times = {"am", "pm", "closed", "hours"};
	
	public static boolean isDay(String txt) {
		txt = txt.toLowerCase();
		for (int i = 0; i < days.length; i++) {
			if (txt.contains(days[i].toLowerCase())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isTime(String txt) {
		txt = txt.toLowerCase();
		if (txt.contains("am") || txt.contains("pm") || txt.contains("closed") || txt.contains("hours")) {
			return true;
		}
		return false;
	}
	
	public static boolean isCC(C c) {
		if (c.getType().equals(C.Type.contentCategory)) {
			return true;
		}
		return false;
	}
	
	public static boolean isSC(C c) {
		if (c.getType().equals(C.Type.subCategory)) {
			return true;
		}
		return false;
	}
	
	public static boolean isNextCC(C c) {
		if (c.getRelations().size() == 1) {
			if (c.getRelations().get(0).getType().equals(C.Type.contentCategory)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNextSC(C c) {
		if (c.getRelations().get(0).getType().equals(C.Type.subCategory)) {
			return true;
		}
		return false;
	}
	
	public static boolean isRelatedFromTo(C from, C to) {
		for (int i = 0; i < from.getRelations().size(); i ++) {
			C cRel = from.getRelations().get(i);
			if (cRel.getTitle().equals(to.getTitle())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValidPage(WebDriver webDriver, C category) {
		String tag = category.getTag();
		if (tag.contains("~")) {
			return sourceCheck(tag.substring(1), webDriver.getPageSource());
		} else {
			return tagCheck(tag, webDriver.getCurrentUrl());
		}
	}
	
	public static boolean isValidPrePage(WebDriver webDriver, C category) {
		String tag = category.prePageTag;
		if (tag.contains("~")) {
			return sourceCheck(tag.substring(1), webDriver.getPageSource());
		} else {
			return tagCheck(tag, webDriver.getCurrentUrl());
		}
	}
	
	public static boolean tagCheck(String tag, String url) {
		return url.contains(tag);
	}
	
	public static boolean sourceCheck(String sourceTag, String pageSource) {
		return pageSource.contains(sourceTag);
	}
	
}
