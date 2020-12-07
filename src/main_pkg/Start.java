package main_pkg;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import anon.Chrome;
import anon.WebDriverHandler;
import parallelism.RProcess;
import sources.AllMallStores_com;
import sources.BranchNear_com;
import sources.DMVGlossary_com;
import tools.Check;

public class Start {

	public static void main(String[] args) {
		
		String pc = "D:/Program Files x64/Selenium/WebDrivers/chromedriver.exe";
		String pcFF = "D:/Program Files x64/Selenium/WebDrivers/geckodriver.exe";
		String lt = "C:/Selenium/WebDrivers/chromedriver.exe";
		
		System.setProperty("webdriver.chrome.driver", pc);
		System.setProperty("webdriver.gecko.driver", pcFF);
		
		//PostalLocations_com postalLocations = new PostalLocations_com();		
		//RProcess p = new RProcess(postalLocations);
		
		//BranchNear_com branchNear = new BranchNear_com();
		//RProcess p = new RProcess(branchNear);
		
		//AllMallStores_com allmallstores = new AllMallStores_com();
		//RProcess p = new RProcess(allmallstores);
		
		DMVGlossary_com dmv = new DMVGlossary_com();
		RProcess p = new RProcess(dmv);
		
		/*String url = "https://www.bankbranchlocator.com/banks-in-abbeville-al.html";
		WebDriverHandler wd = new WebDriverHandler(WebDriverHandler.BrowserType.Chrome);
		WebDriver w = wd.getWebDriver();
		w.get(url);
		
		if (Check.isValidPrePage(w, branchNear.cityTown)) {
			System.out.println("Is Valid Page --> " + w.getCurrentUrl());
		} else {
			System.out.println("Not valid page --> " + w.getCurrentUrl());
		}*/
		
		//Chrome c = new Chrome();
		//WebDriver w = c.getWebDriver();
		//w.get("https://whatismyipaddress.com");
		
		// File Handling Example
		/*FileIO fio = new FileIO();
		String fname = "test.txt";
		fio.createFile(fname);
		fio.writeFile(fname, "First Line");
		fio.appendFile(fname, "Another Line");
		
		ArrayList<String> lines = fio.readFile(fname);
		for(int i = 0; i < lines.size(); i++) {
			System.out.println(lines.get(i));
		}
		
		System.out.println(Arr.doesItExist(lines, "Another "));*/
		
	}
	
}
