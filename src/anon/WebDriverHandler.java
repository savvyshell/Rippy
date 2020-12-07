package anon;

import org.openqa.selenium.WebDriver;

public class WebDriverHandler {

	public Browser browser;
	
	public static class BrowserType {
		public static String Chrome = "Chrome";
		public static String TorBrowser = "Tor";
	}
	
	public WebDriverHandler(String type) {
		setBrowserType(type);
	}
	
	public void setBrowserType(String type) {
		if (type.equals(BrowserType.Chrome)) {
			browser = new Chrome();
		} else if (type.equals(BrowserType.TorBrowser)) {
			browser = new Tor();
		}
	}
	
	public WebDriver getWebDriver() {
		return this.browser.getWebDriver();
	}
	
	public Browser getBrowser() {
		return this.browser;
	}
	
}
