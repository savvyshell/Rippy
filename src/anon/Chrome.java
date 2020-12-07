package anon;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Chrome extends Browser {

	public WebDriver webDriver;
	
	public WebDriver getWebDriver() {
		ChromeOptions options = new ChromeOptions();
		String proxy = "83.149.70.159:13012";
		options.addArguments("--proxy-server=" + proxy);
		webDriver = new ChromeDriver(options);
		return webDriver;
	}
	
	public WebDriver restart() {
		webDriver.quit();
		return getWebDriver();
	}
	
}
