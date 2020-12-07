package sources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import cfg.C;
import cfg.CC;
import cfg.Manifest;
import cfg.Post;
import cfg.SC;
import tools.Check;
import tools.Manipulation;
import tools.Sel;

public class PostalLocations_com extends Manifest {
	
	// CC
	public CC office;

	// Initial SC
	public SC state;
	
	// SC
	public SC county;
	public SC city;
	
	public PostalLocations_com() {
		super();
		super.setBaseURL("https://www.postallocations.com/");
		super.setTableName("PostalLocations_com");
		
		// Initialize Categories
		office = new CC();
		office.setTitle("Post Office");
		office.setTag("~Click on the Post Office");
		office.setXPath("//div[@class='list']/a");
		
		state = new SC(office);
		state.setTitle("State");
		state.setTag("~Browse States");
		state.setXPath("//div[@id='states']//ul/li/a");
		
		county = new SC(office);
		county.setTitle("County");
		county.setTag("county");
		county.setXPath("//ul[@id='myList']/li/a");
		county.prePage = true;
		county.prePageTag = "county";
		county.prePageXPath = "//div[@class='list']/a";
		
		city = new SC(office);
		city.setTitle("City");
		city.setTag("city");
		city.setXPath("//ul[@id='myList']/li/a");
		
		// Set Relations -- > SubCategories
		state.addRelation(county);
		state.addRelation(city);
		
		county.addRelation(office);
		city.addRelation(office);
		
		// Set Relations --> ContentCategory Cols
		office.addRelation(county);
		office.addRelation(city);
		
		// Relate back to Manifest
		scList.add(county);
		scList.add(city);
		initialNode = state;
		endNode = office;
	}

	public void processContentPage(Map<String, String> row) {
		
		String url = webDriver.getCurrentUrl();
		
		String title = "";
		String state = "";
		String city = "";
		String county = "";
		
		// addressParams
		String streetAddress = "";
		String addressLocality = "";
		String addressRegion = "";
		String postalCode = "";
		
		// contactNumbers
		String phone = "";
		String fax = "";
		String tty = "";
		String tollfree = "";
		
		JSONObject addressParams = new JSONObject();
		JSONObject contactNumbers = new JSONObject();
		
		JSONObject hours = new JSONObject();
		JSONArray retailHours = new JSONArray();
		JSONArray lobbyHours = new JSONArray();
		JSONArray lastCollectionTimes = new JSONArray();
		JSONArray bulkMailAcceptanceHours = new JSONArray();
		
		JSONObject servicesOffered = new JSONObject();
		
		JSONObject extraData = new JSONObject();
		boolean essentialGovtService = false;
		boolean poBoxAccessAvail = false;
		boolean processUSPassports = false;
		
		JSONObject nearbyOffices = new JSONObject();
		
		try {
			String titleXPath = "//h2[@class='title']";
			title = Sel.getTextByXPath(webDriver, titleXPath, false);
		} catch (Exception e) {}
		
		System.out.println("URL: " + url);
		System.out.println("Title: " + title);
		
		try {
			String xpath = "//span[@itemprop='name']";
			ArrayList<String> elems = Sel.getListWithList(webDriver.findElements(By.xpath(xpath)), false);
			state = elems.get(0);
			city = elems.get(1);
		} catch (Exception e) {}
		
		try {
			String xpath = "//div[@id='maplink']/p";
			county = Sel.getText(webDriver.findElement(By.xpath(xpath)), false).replace(" County", "");
		} catch (Exception e) {}
		
		System.out.println("County: " + county);
		System.out.println("State: " + state);
		System.out.println("City: " + city);
		
		try {
			streetAddress = Sel.getTextByXPath(webDriver, "//span[@itemprop='streetAddress']", false);
			addressLocality = Sel.getTextByXPath(webDriver, "//span[@itemprop='addressLocality']", false);
			addressRegion = Sel.getTextByXPath(webDriver, "//span[@itemprop='addressRegion']", false);
			postalCode = Sel.getTextByXPath(webDriver, "//span[@itemprop='postalCode']", false);
			
			addressParams.put("address", streetAddress);
			addressParams.put("locality", addressLocality);
			addressParams.put("region", addressRegion);
			addressParams.put("postalcode", postalCode);
		} catch (Exception e) {}
		
		try {
			String phoneXPath = "//a[contains(@href, 'tel')]/span";
			phone = Sel.getTextByXPath(webDriver, phoneXPath, false);
			contactNumbers.put("phone", phone);
		} catch (Exception e) {}
		
		try {
			String faxXPath = "//span[@itemprop='faxNumber']";
			fax = Sel.getTextByXPath(webDriver, faxXPath, false);
			contactNumbers.put("fax", fax);
		} catch (Exception e) {}
		
		try {
			tty = Sel.getTextByText(webDriver, "p", "TTY", "TTY: ", "\n", false);
			contactNumbers.put("tty", tty);
		} catch (Exception e) {}
		
		try {
			tollfree = Sel.getTextByText(webDriver, "p", "Toll-Free", "Toll-Free: ", "", false);
			contactNumbers.put("tollfree", tollfree);
		} catch (Exception e) {}
		
		String hrsXPath1 = "//div[contains(@class,'hours')]/text()[contains(., '_')]/following-sibling::p/span/child::node()/span[@class='hourtext']";
		String hrsXPath2 = "//div[contains(@class,'hours')]/text()[contains(., '_')]/following-sibling::p/span";
		
		System.out.println("Address Params: " + addressParams);
		System.out.println("Contact Numbers: " + contactNumbers);
		
		try {
			List<WebElement> retailHoursElemsLst = Sel.getMultiXPathDoubleProng(webDriver, 
					hrsXPath1.replace("_", "Retail Hours"), 
					hrsXPath2.replace("_", "Retail Hours"));
			ArrayList<String> retailHoursLst = Sel.getListOnCondition(retailHoursElemsLst, Check.times, false);
			retailHours = Manipulation.listToJSONArray(retailHoursLst);
			hours.put("Retail Hours", retailHours);
			//System.out.println("Retail Hours: " + retailHours);
		} catch (Exception e) {}
		
		try {
			List<WebElement> lstWeb = Sel.getMultiXPathDoubleProng(webDriver, 
					hrsXPath1.replace("_", "Lobby Hours"), 
					hrsXPath2.replace("_", "Lobby Hours"));
			ArrayList<String> lstArr = Sel.getListOnCondition(lstWeb, Check.times, false);
			lobbyHours = Manipulation.listToJSONArray(lstArr);
			hours.put("Lobby Hours", lobbyHours);
			//System.out.println("Lobby Hours: " + lobbyHours);
		} catch (Exception e) {}
		
		try {
			List<WebElement> lstWeb = Sel.getMultiXPathDoubleProng(webDriver, 
					hrsXPath1.replace("_", "Last Collection Times"), 
					hrsXPath2.replace("_", "Last Collection Times"));
			ArrayList<String> lstArr = Sel.getListOnCondition(lstWeb, Check.times, false);
			lstArr = new ArrayList<String>(lstArr.subList(0, 7));
			lastCollectionTimes = Manipulation.listToJSONArray(lstArr);
			hours.put("Last Collection Times", lastCollectionTimes);
			//System.out.println("Last Collection Times: " + lastCollectionTimes);
		} catch (Exception e) {}
		
		try {
			List<WebElement> lstWeb = Sel.getMultiXPathDoubleProng(webDriver, 
					hrsXPath1.replace("_", "Bulk Mail Acceptance Hours"), 
					hrsXPath2.replace("_", "Bulk Mail Acceptance Hours"));
			ArrayList<String> lstArr = Sel.getListOnCondition(lstWeb, Check.times, false);
			bulkMailAcceptanceHours = Manipulation.listToJSONArray(lstArr);
			hours.put("Bulk Mail Acceptance Hours", bulkMailAcceptanceHours);
			//System.out.println("Bulk Mail Acceptance Hours: " + bulkMailAcceptanceHours);
		} catch (Exception e) {}
		
		System.out.println("Hours: " + hours);
		
		try {
			ArrayList<String> lst = Sel.getListWithList(webDriver.findElements(By.xpath("//ul[@id='extrainfo2']/li")), false);
			JSONArray a = new JSONArray();
			for (int i = 0 ; i < lst.size(); i++) {
				a.put(lst.get(i));
			}
			servicesOffered.put("services", a);
		} catch (Exception e) {}
		
		System.out.println("Services Offered: " + servicesOffered);
		
		try {
			String usPassportsXPath = "//*[contains(text(), 'This facility does not process US Passports')]";
			WebElement webElem = webDriver.findElement(By.xpath(usPassportsXPath));
			processUSPassports = true;
		} catch (Exception e) {}
		
		try {
			String poBoxAccessXPath = "//*[contains(text(), 'PO Box Access Available')]";
			WebElement webElem = webDriver.findElement(By.xpath(poBoxAccessXPath));
			poBoxAccessAvail = true;
		} catch (Exception e) {}
		
		try {
			String essentialGovtXPath = "//*[contains(text(), 'The Postal Service is an essential government service')]";
			WebElement webElem = webDriver.findElement(By.xpath(essentialGovtXPath));
			essentialGovtService = true;
		} catch (Exception e) {}
		
		extraData.put("US Passports", processUSPassports);
		extraData.put("PO Box Avail", poBoxAccessAvail);
		extraData.put("Essential", essentialGovtService);
		
		System.out.println("Extra Data: " + extraData);
		
		try {
			JSONArray a = new JSONArray();
			ArrayList<String> lst = Sel.getListWithList(webDriver.findElements(By.xpath("//div[@id='nearby']/p")), false);
			for (int i = 0; i < lst.size(); i++) {
				if (i == 0)
					continue;
				
				String txt = lst.get(i);
				String[] split = txt.split("\n");
				String addrLocality = split[0];
				String addr = split[1];
				String distanceAway = split[2];
				
				JSONObject info = new JSONObject();
				info.put("locality", addrLocality);
				info.put("address", addr);
				info.put("distanceAway", distanceAway);
				a.put(info);
			}
			nearbyOffices.put("offices", a);
		} catch (Exception e) {}
		
		System.out.println("Nearby Offices: " + nearbyOffices);
		
		Post p = new Post();
		p.fields.put("url", url);
		p.fields.put("title", title);
		p.fields.put("county", county);
		p.fields.put("state", state);
		p.fields.put("city", city);
		p.fields.put("addressParams", addressParams.toString());
		p.fields.put("contactNumbers", contactNumbers.toString());
		p.fields.put("hours", hours.toString());
		p.fields.put("servicesOffered", servicesOffered.toString());
		p.fields.put("nearbyOffices", nearbyOffices.toString());
		p.fields.put("extraData", extraData.toString());
		
		this.insertPost(p);
		this.posts.add(p);
	}
	
	// CUSTOM NAVIGATION FOR IN-BETWEENS
	@Override
	public void goToCategoryFrom(C from, C to) {
		// State --> County, City --> Postal Office
		
		if (Check.isRelatedFromTo(state, county) && from == state && to == county) {
			stateToCounty();
			
		} else if (Check.isRelatedFromTo(state, city) && from == state && to == city) {
			stateToCity();
			
		} else if (Check.isRelatedFromTo(county, office) && from == county && to == office) {
			county_city_toOffice(county);
			
		} else if (Check.isRelatedFromTo(city, office) && from == city && to == office) {
			county_city_toOffice(city);
			
		}
	}
	
	private void stateToCounty() {
		display.fromTo(state, county);
		WebElement elem = webDriver.findElement(By.xpath("//a[text()='Browse by County']"));
		String href = elem.getAttribute("href");
		webDriver.get(href);
	}
	
	private void stateToCity() {
		display.fromTo(state, city);
		WebElement elem = webDriver.findElement(By.xpath("//a[text()='Browse by City']"));
		String href = elem.getAttribute("href");
		webDriver.get(href);
	}
	
	private void county_city_toOffice(C from) {
		display.fromTo(from, office);
	}
	
	
	
}
