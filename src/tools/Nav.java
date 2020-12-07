package tools;

import org.openqa.selenium.WebDriver;

public class Nav {

	public static void get(WebDriver webDriver, String url) {
		int tries = 0;
		while (tries < 5) {
			try {
				webDriver.get(url);
				break;
			} catch (org.openqa.selenium.TimeoutException se) {
				try {
					System.out.println("Nav :: get() ... trying again ["+tries+"]");
					tries += 1;
					Thread.sleep(10000);
					continue;
				} catch (InterruptedException e) {
					System.out.println("Nav :: get()");
					e.printStackTrace();
				}
			}
		}
	}
	
}
