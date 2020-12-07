package parallelism;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import anon.Browser;
import anon.WebDriverHandler;
import cfg.C;
import cfg.Manifest;
import mysql.MySQLBridge;
import mysql.MySQLTools;
import tools.Check;
import tools.Display;
import tools.Manipulation;
import tools.Nav;

public class RProcess implements Runnable {
	
	public static boolean test = false; // false
	public static boolean testCC = false; // false
	public static boolean init = false; // false
	public static String preTestCCURL = "https://www.dmvnearme.net/en/identification-cards/al-alabama/autauga-county";
	public static String testCCurl = "https://www.dmvnearme.net/en/prattville-driver-s-license-office/367";
	
	public static int count = 0;
	public static int ThreadCount = 3;
	public static int processPageTriesLimit = 3;
	
	public Manifest m;
	public MySQLBridge mysqlBridge;
	public MySQLTools mysqlTools;
	public Display display;
	public int id = 0;
	public int processPageTries = 0;
	
	private String browserType = WebDriverHandler.BrowserType.Chrome; // custom
	private WebDriverHandler webDriverHandler;
	private Browser browser;
	private WebDriver webDriver;
	private ArrayList<Map<String, String>> endNodeLinks = new ArrayList<Map<String, String>>();
	private ArrayList<JobsQueueManager> jM = new ArrayList<JobsQueueManager>();
	private ArrayList<Map<String, String>> initialLinks;
	
	public RProcess(Manifest manifest) {
		this.m = manifest;
		this.id = count;
		RProcess.count += 1;
		
		webDriverHandler = new WebDriverHandler(browserType);
		browser = webDriverHandler.getBrowser();
		this.webDriver = browser.getWebDriver();
		this.m.setWebDriver(webDriver);
		
		this.display = new Display(this);
		this.m.setDisplay(this.display);
		
		this.mysqlBridge = new MySQLBridge();
		this.mysqlTools = new MySQLTools(this.mysqlBridge);
		
		this.m.setMySQLBridge(mysqlBridge);
		this.m.setMySQLTools(mysqlTools);
		
		if (testCC) {
			runTestCC();
		}
		
		if (!init)
			init();
	}
	
	public void init() {
		init = true;
		
		boolean urls = m.initialNode.urls.size() > 0;
		if (urls) {
			ArrayList<Map<String, String>> tempLst = new ArrayList<Map<String, String>>();
			for (int i = 0; i < m.initialNode.urls.size(); i++) {
				String url = m.initialNode.urls.get(i);
				tempLst = getInitialLinks(url);
				System.out.println(tempLst.size());
				if (initialLinks == null) {
					initialLinks = tempLst;
					System.out.println("initializing tempLst");
				} else {
					initialLinks.addAll(tempLst);
					System.out.println("addAll tempLst");
				}
			}
		} else {
			initialLinks = getInitialLinks(m.getBaseURL());
		}
		
		List<List<Map<String, String>>> list = Manipulation.chopIntoParts(initialLinks, RProcess.ThreadCount);
		System.out.println(initialLinks.size());
		for (int i = 0; i < list.size(); i++) {
			List<Map<String, String>> lst = list.get(i);
			RProcess p = new RProcess(m);
			p.initialLinks = (ArrayList<Map<String, String>>) lst;
			
			Thread t = new Thread(p);
			t.start();
		}
		this.webDriver.close();
	}

	public void runTestCC() {
		Map<String, String> row = new HashMap<String, String>();
		row.put("href", testCCurl);
		row.put("prevURL", preTestCCURL);
		row.put("text", "");
		webDriver.get(testCCurl);
		m.processContentPage(row);	
	}
	
	public void run() {
		// cur : m.initialNode
		
		// Loop through all Init -> SC | SC -> SC | SC
		display.list("Initial Links", initialLinks);
		addEndNodeLinks(initialLinks, m.initialNode);
		
		// Caching Level 1
		display.header("Caching Level 1...");
		ArrayList<String> urls = mysqlTools.getProcessedURLs(m.getTableName());
		ArrayList<Integer> remove = new ArrayList<Integer>();
		for (int i = 0; i < urls.size(); i++) {
			String url = urls.get(i);
			for (int x = 0; x < endNodeLinks.size(); x++) {
				String endNodeURL = endNodeLinks.get(x).get("href");
				if (endNodeURL.equalsIgnoreCase(url)) {
					remove.add(x);
				}
			}
		}
		display.std("Removing URL list size of: " + remove.size());
		for (int i = 0; i < remove.size(); i++) {
			endNodeLinks.remove(remove.get(i));
		}
		
		// Processing each CC page
		display.header("EndNodeLinks Gathered");
		for (int i = 0; i < endNodeLinks.size(); i++) {
			Map<String, String> row = endNodeLinks.get(i);
			String url = row.get("href");
			String prevURL = row.get("prevURL");
			String txt = row.get("text");
			display.listElem("Processing... <enl> " + url, 0);
			if (mysqlTools.doesRowExist(m.getTableName(), "url", url)) {
				display.std("Already Exists");
				continue;
			}
			Nav.get(webDriver, webDriver.getCurrentUrl());
			m.processContentPage(row);	
		}
		display.std("EndNodeLinks Processing Complete!");

	}
	
	private int scLinksTries = 0;
	private int addEndNodeLinksDepth = 0;
	public void addEndNodeLinks(ArrayList<Map<String, String>> links, C from) {
		
		if (Check.isNextCC(from)) { // scLinks <==> endNodeLinks
			display.header(from.getTitle() + " --> CC Next :: Ending SC");
			if (from.prePage) { // if it contains category of links before actual content
				display.std(from.getTitle() + " PrePage Processing...");
				for (int i = 0; i < links.size(); i++) {
					while (true) {
						try {
							webDriver.get(links.get(i).get("href"));
							break;
						} catch (TimeoutException e1) {
							try { Thread.sleep(10000); } catch (Exception e) {}
							System.out.println("Timeout...");
							continue;
						} catch (WebDriverException e2) {
							System.out.println("WebDriverException");
							webDriver = webDriverHandler.getBrowser().restart();
							try { Thread.sleep(20000); } catch (Exception e0) {}
						}
					}
					
					if (!Check.isValidPrePage(webDriver, from)) {
						display.std(from.getTitle() + " - not valid page :: " + webDriver.getCurrentUrl());
						continue;
					} else {
						display.std(from.getTitle() + " - valid page :: " + webDriver.getCurrentUrl());
					}
					
					endNodeLinks.addAll(getSCLinks(from, true));
					if (test && i == 1)
						break;
				}
			} else {
				display.std(from.getTitle() + " adding end node links without pre-processing.");
				endNodeLinks.addAll(links);
			}
			return;
		}
		
		for (int i = 0; i < links.size(); i++) {
			String iLink = links.get(i).get("href");
			for (int x = 0; x < from.getRelations().size(); x++) {
				webDriver.get(iLink);
				C nextSC = from.getRelations().get(x);
				
				// Navigate To
				m.goToCategoryFrom(from, nextSC);
				
				// Get Links For
				ArrayList<Map<String, String>> scLinks = getSCLinks(nextSC, false);
				
				if (Check.isNextCC(nextSC)) {
					display.header("Next CC from: " + nextSC.getTitle());
					addEndNodeLinks(scLinks, nextSC);
				}
				
				// Recurse if next SC
				if (Check.isNextSC(nextSC)) {
					display.header(nextSC.getTitle() + " --> SC Next :: ...");
					// if multiple categories
					for (int y = 0; y < nextSC.getRelations().size(); y++) {
						addEndNodeLinks(scLinks, nextSC);
					}
				}
				if (test)
					break; // test
			}
			if (test)
				break; // test
		}
		display.std("End Node Links Process Complete :: " + from.getTitle() + " (" + addEndNodeLinksDepth + ")");
		addEndNodeLinksDepth += 1;
	}
	
	int initialIndex = 0;
	public ArrayList<Map<String, String>> getInitialLinks(String url) {
		System.out.println("getInitialLinks() :: Processing URL: " + url);
		webDriver.get(url);
		
		ArrayList<Map<String, String>> initialLinks = new ArrayList<Map<String, String>>();
		if (Check.isValidPage(webDriver, m.initialNode)) { // on initial node?
			
			List<WebElement> linksElem = webDriver.findElements(By.xpath(m.initialNode.getXPath()));
			if (linksElem.size() == 0) {
				linksElem = webDriver.findElements(By.xpath(m.initialNode.xpath2));
			}
			display.header("Initial Node Links List --> " + m.initialNode.getTitle());
			for(int i = 0; i < linksElem.size(); i++) {
				Map<String, String> row = new HashMap<String, String>();
				String href = linksElem.get(i).getAttribute("href");
				String text = (linksElem.get(i).getText().length() <= 0 ? linksElem.get(i).getAttribute("innerHTML").trim() : linksElem.get(i).getText().trim());
				//display.listElem(href + " :: " + text, 0);
				
				row.put("href", href);
				row.put("text", text);
				initialLinks.add(row);
			}

		} else {
			display.std("Error Processing Initial Node :: " + m.initialNode.getTitle());
			// Could also mean webDriver is messed up
			display.std("WebDriver may have failed... trying again :: getInitialLinks()");
			try { Thread.sleep(3000); } catch (InterruptedException e) {}
			initialLinks = getInitialLinks(url);
		}
		return initialLinks;
	}
	
	public ArrayList<Map<String, String>> getSCLinks(C sc, boolean prePage) {
		ArrayList<Map<String, String>> scLinks = new ArrayList<Map<String, String>>();
		if ( (Check.isValidPage(webDriver, sc)) || (prePage && Check.isValidPrePage(webDriver, sc)) ) {
			String xpath = "";
			if (prePage) {
				xpath = sc.prePageXPath;
			} else {
				xpath = sc.getXPath();
			}
			System.out.println(xpath + " :: " + webDriver.getCurrentUrl() + " :: " + sc.getTitle() + " :: getSCLinks()");
			List<WebElement> linksElem = webDriver.findElements(By.xpath(xpath));
			display.header("SC Node Links List --> " + sc.getTitle() + " of size: " + linksElem.size());
			for(int i = 0; i < linksElem.size(); i++) {
				Map<String, String> row = new HashMap<String, String>();
				String href = linksElem.get(i).getAttribute("href");
				String text = (linksElem.get(i).getText().length() <= 0 ? linksElem.get(i).getAttribute("innerHTML").trim() : linksElem.get(i).getText().trim());
				display.listElem(href + " :: " + text, 0);
				
				row.put("href", href);
				row.put("text", text);
				row.put("prevURL", webDriver.getCurrentUrl());
				scLinks.add(row);
			}
			
			if (linksElem.size() == 0 && scLinksTries <= 3) { // webDriver probably broke
				display.std("WebDriver may have failed... trying again :: getSCLinks() on try:" + scLinksTries);
				scLinksTries += 1;
				try { Thread.sleep(3000); } catch (InterruptedException e) {}
				Nav.get(webDriver, webDriver.getCurrentUrl());
				scLinks = getSCLinks(sc, prePage);
			} else {
				scLinksTries = 0;
			}

		} else {
			display.std("Error Processing SC Node :: " + sc.getTitle() + " :: getSCLinks()");
			display.std("WebDriver may have failed... trying again :: getSCLinks()");
			try { Thread.sleep(3000); } catch (InterruptedException e) {}
			Nav.get(webDriver, webDriver.getCurrentUrl());
			scLinks = getSCLinks(sc, prePage);
		}
		return scLinks;
	}
	
}
