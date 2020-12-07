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

public class AllMallStores_com extends Manifest {
	
	// CC
	public CC postCC;

	// Initial SC
	public SC state;
	
	// SC
	public SC mall_outlet;
	
	public AllMallStores_com() {
		super();
		super.setBaseURL("https://www.mallscenters.com/");
		super.setTableName("AllMallStores_com");
		
		// Initialize Categories
		postCC = new CC();
		postCC.setTitle("PostCC");
		postCC.setTag("~BANK INFORMATION");
		postCC.setXPath("//h3[contains(text(), 'BANK INFORMATION')]");
		
		state = new SC(postCC);
		state.setTitle("Mall");
		state.setTag("~in US by State");
		state.setXPath("//ul[@class='table1_flex']/li/a");
		state.xpath2 = "//div[@class='table1_flex']/li/a";
		state.urls.add("https://www.mallscenters.com/malls/");
		state.urls.add("https://www.mallscenters.com/outlets/");
		
		mall_outlet = new SC(postCC);
		mall_outlet.setTitle("Mall/Outlet");
		mall_outlet.setTag("~hours, locations, store list");
		mall_outlet.setXPath("(//div[@class='list_malls'])[2]/div/div[@class='info']/a");
		
		// Set Relations -- > SubCategories
		state.addRelation(mall_outlet);
		
		mall_outlet.addRelation(postCC);
		
		// Set Relations --> ContentCategory Cols
		postCC.addRelation(mall_outlet);
		
		// Relate back to Manifest
		scList.add(mall_outlet);
		initialNode = state;
		endNode = postCC;
	}

	public void processContentPage(Map<String, String> row) {
		
		// + Process Stores to JSON
		String url = webDriver.getCurrentUrl();
		
		String title = "";
		String type = ""; // *Mall, *Outlet
		
		// Mall/Outlet info
		String state = "";
		String city = "";
		String numberOfStores = "";
		String source = "";
		String gpsCoords = "";
		
		// contactNumbers
		String phone = "";
		
		// addressParams
		String streetAddress = ""; // 9223 Rhody Dr
		String addressLocality = ""; // Chimacum
		String addressRegion = ""; // WA
		String postalCode = ""; // 98325
		
		JSONObject addressParams = new JSONObject();
		
		JSONArray postHours = new JSONArray();
		JSONObject hours = new JSONObject();
		
		JSONObject extraData = new JSONObject();
		
		JSONObject nearbyStores = new JSONObject();
		
		//String nextHref = "";
		try {
			String titleXPath = "//div[@id='page_title']/h1";
			title = Sel.getTextByXPath(webDriver, titleXPath, false);
			
			if (title.contains("Mall")) {
				type = "Mall";
			} else {
				type = "Outlet";
			}
		} catch (Exception e) {}

		System.out.println("URL: " + url);
		System.out.println("Title: " + title);
		System.out.println("Type: " + type);
		
		try {
			String xpath = "//tr/th[contains(text(), 'State')]/following-sibling::td/a";
			state = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//tr/th[contains(text(), 'Area/City')]/following-sibling::td";
			city = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//tr/th[contains(text(), 'Number of stores')]/following-sibling::td";
			numberOfStores = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//tr/th[contains(text(), 'www')]/following-sibling::td";
			source = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//tr/th[contains(text(), 'GPS Coordinates')]/following-sibling::td";
			gpsCoords = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//tr/th[contains(text(), 'Phone number')]/following-sibling::td";
			phone = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		System.out.println("State: " + title);
		System.out.println("City: " + city);
		System.out.println("Number of Stores: " + numberOfStores);
		System.out.println("Source: " + source);
		System.out.println("GPS Coords: " + gpsCoords);
		System.out.println("Phone: " + phone);
		
		// addressParams
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
		
		System.out.println("Address Params: " + addressParams);
		
		try {
			String pageSource = webDriver.getPageSource();
			if (pageSource.contains("has been completed demolished")) {
				extraData.put("Demolished", true);
			}
			
			String mon = Sel.getTextByXPath(webDriver, "//tr/td[contains(text(), 'Monday')]/following-sibling::td/time", false);
			String tues = Sel.getTextByXPath(webDriver, "//tr/td[contains(text(), 'Tuesday')]/following-sibling::td/time", false);
			String wed = Sel.getTextByXPath(webDriver, "//tr/td[contains(text(), 'Wednesday')]/following-sibling::td/time", false);
			String thurs = Sel.getTextByXPath(webDriver, "//tr/td[contains(text(), 'Thursday')]/following-sibling::td/time", false);
			String fri = Sel.getTextByXPath(webDriver, "//tr/td[contains(text(), 'Friday')]/following-sibling::td/time", false);
			String sat = Sel.getTextByXPath(webDriver, "//tr/td[contains(text(), 'Saturday')]/following-sibling::td/time", false);
			String sun = Sel.getTextByXPath(webDriver, "//tr/td[contains(text(), 'Sunday')]/following-sibling::td/time", false);
			
			JSONArray ja = new JSONArray();
			ja.put(mon);
			ja.put(tues);
			ja.put(wed);
			ja.put(thurs);
			ja.put(fri);
			ja.put(sat);
			ja.put(sun);

			hours.put("Hours", ja);
		} catch (Exception e) {}

		System.out.println("Hours: " + hours);
		System.out.println("Extra Data: " + extraData);
		
		try {
			JSONArray a = new JSONArray();
			String xpath = "(//p/strong[contains(text(), 'Current stores')]/parent::p/following-sibling::ul)[1]/li/a/span";
			
			List<WebElement> lst = webDriver.findElements(By.xpath(xpath));
			for (int i = 0; i < lst.size(); i++) {
				String storeTitle = Sel.getText(lst.get(i));
				//storeTitle = storeTitle.replaceAll("'","\\\\'");
				
				JSONObject info = new JSONObject();
				info.put("title", storeTitle);
				a.put(info);
			}
			nearbyStores.put("stores", a);
		} catch (Exception e) {}
	
		
		System.out.println("Nearby Stores: " + nearbyStores);
		
		// Setting Post Params for Database
		Post p = new Post();
		p.fields.put("url", url);
		p.fields.put("title", title);
		p.fields.put("type", type);
		
		p.fields.put("state", state);
		p.fields.put("city", city);
		p.fields.put("numberOfStores", numberOfStores);
		p.fields.put("source", source);
		p.fields.put("gpsCoords", gpsCoords);
		p.fields.put("phone", phone);
		
		p.fields.put("addressParams", addressParams.toString());
		p.fields.put("hours", hours.toString());

		p.fields.put("nearbyStores", nearbyStores.toString());
		p.fields.put("extraData", extraData.toString());
		
		this.insertPost(p);
		this.posts.add(p);
	}
	
	// CUSTOM NAVIGATION FOR IN-BETWEENS
	@Override
	public void goToCategoryFrom(C from, C to) {
		// State --> County, City --> Postal Office
		
		if (Check.isRelatedFromTo(state, mall_outlet) && from == state && to == mall_outlet) {
			stateToMall_Outlet();
			
		} else if (Check.isRelatedFromTo(mall_outlet, postCC) && from == mall_outlet && to == postCC) {
			mall_outletToPostCC();
			
		}
	}
	
	private void stateToMall_Outlet() {
		display.fromTo(state, mall_outlet);
	}
	
	private void mall_outletToPostCC() {
		display.fromTo(mall_outlet, postCC);
	}
	
	
	
}
