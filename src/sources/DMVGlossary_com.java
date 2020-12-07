package sources;

import java.util.ArrayList;
import java.util.HashMap;
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

public class DMVGlossary_com extends Manifest {
	
	// CC
	public CC office;

	// Initial SC
	public SC state;
	
	// SC
	public SC county;
	
	public DMVGlossary_com() {
		super();
		super.setBaseURL("https://www.dmvnearme.net/");
		super.setTableName("DMVGlossary_net");
		
		// Initialize Categories
		office = new CC();
		office.setTitle("office");
		office.setTag("-office/");
		office.setXPath("//h3[contains(text(), 'Contact info')]");
		
		state = new SC(office);
		state.setTitle("state");
		state.setTag("~offices by state");
		state.setXPath("//h2[contains(text(), 'offices by state')]/following-sibling::div/div[@class='row']/div/h4/a");
		//state.xpath2 = "//div[@class='table1_flex']/li/a";
		state.urls.add("https://www.dmvnearme.net/en/drivers-license");
		state.urls.add("https://www.dmvnearme.net/en/identification-cards");
		state.urls.add("https://www.dmvnearme.net/en/registration");
		state.urls.add("https://www.dmvnearme.net/en/titling");
		state.urls.add("https://www.dmvnearme.net/en/plates");
		state.urls.add("https://www.dmvnearme.net/en/cdl-written");
		state.urls.add("https://www.dmvnearme.net/en/cdl-driving");
		
		county = new SC(office);
		county.setTitle("county");
		county.setTag("~by county");
		county.setXPath("//h2[contains(text(), 'by county')]/following-sibling::div/div[@class='row']/div/h4/a");
		county.prePage = true;
		county.prePageTag = "-county"; // -county
		county.prePageXPath = "//div[@class='info-oficina']/h3/a";
		
		// Set Relations -- > SubCategories
		state.addRelation(county);
		
		county.addRelation(office);
		
		// Set Relations --> ContentCategory Cols
		office.addRelation(county);
		
		// Relate back to Manifest
		scList.add(county);
		initialNode = state;
		endNode = office;
	}

	public void processContentPage(Map<String, String> row) {
		
		// + Process Stores to JSON
		String url = webDriver.getCurrentUrl();
		
		String title = "";
		String type = "";
		/*
		 * Driver's License // drivers-license
		 * Identification Cards // identification-cards
		 * Registration // registration
		 * Titling // titling
		 * Plates // plates
		 * CDL Written // cdl-written
		 * CDL Driving // cdl-driving
		 * */
		
		String preURL = row.get("prevURL").toLowerCase();
		if (preURL.contains("/drivers-license/")) {
			type = "Drivers License";
		} else if (preURL.contains("/identification-cards/")) {
			type = "Identification Cards";
		} else if (preURL.contains("/registration/")) {
			type = "Registration";
		} else if (preURL.contains("/titling/")) {
			type = "Titling";
		} else if (preURL.contains("/plates/")) {
			type = "Plates";
		} else if (preURL.contains("/cdl-written/")) {
			type = "CDL-Written";
		} else if (preURL.contains("/cdl-driving/")) {
			type = "CDL-Driving";
		}

		String paymentOptions = "";
		String covid19info = "";
		String hoursInfo = "";
		String state = "";
		String county = "";
		
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
		
		JSONObject nearbyOffices = new JSONObject();
		
		//String nextHref = "";
		while (true) {
			try {
				String titleXPath = "//h1";
				title = Sel.getTextByXPath(webDriver, titleXPath, false);
				
				if (title.equals("This page isn’t working") || title.equals("This site can’t be reached")) {
					System.out.println("Trying again...");
					webDriver.get(url);
					Thread.sleep(3000);
					continue;
				} else {
					break;
				}
			} catch (Exception e) {}
		}

		System.out.println("URL: " + url);
		System.out.println("Title: " + title);
		System.out.println("Type: " + type);
		
		try {
			String xpath = "//div[contains(text(), 'Payment')]/following-sibling::div[1]";
			paymentOptions = Sel.getTextByXPath(webDriver, xpath, false).trim();
		} catch (Exception e) {}
		
		try {
			String xpath = "//span[contains(text(), 'CONTACT PHONE')]/following-sibling::span";
			phone = Sel.getTextByXPath(webDriver, xpath, false).trim();
		} catch (Exception e) {}
		
		try {
			String xpath = "//div[contains(text(), 'COVID-19 INFO')]/following-sibling::div[1]";
			covid19info = Sel.getTextByXPath(webDriver, xpath, false).trim();
		} catch (Exception e) {}
		
		System.out.println("paymentOptions: " + paymentOptions);
		System.out.println("Phone: " + phone);
		System.out.println("COVID-19 Info: " + covid19info);
		
		// addressParams
		try {			
			streetAddress = Sel.getTextByXPath(webDriver, "//div[contains(text(), 'Address')]/following-sibling::div[1]", false);
			
			addressLocality = Sel.getTextByXPath(webDriver, "(//h2[contains(text(), 'offices in')])[1]", false);
			String[] arr = addressLocality.split(" ");
			addressLocality = arr[arr.length -2].replace(",", "").trim();
			addressRegion = arr[arr.length-1].trim();
			
			state = addressRegion;
			county = addressLocality;
			
			postalCode = Sel.getTextByXPath(webDriver, "//div[contains(text(), 'ZIP')]/following-sibling::div[1]/a", false);
			
			addressParams.put("address", streetAddress);
			addressParams.put("locality", addressLocality);
			addressParams.put("region", addressRegion);
			addressParams.put("postalcode", postalCode);
		} catch (Exception e) {}
		
		System.out.println("Address Params: " + addressParams);
		
		try {
			String h = Sel.getTextByXPath(webDriver, "(//div[contains(text(), 'Hours')]/following-sibling::div[1])[1]", false);			
			hours.put("Hours", h);
			hoursInfo = h;
		} catch (Exception e) {}

		System.out.println("Hours: " + hours);
		System.out.println("Extra Data: " + extraData);
		
		try {
			JSONArray a = new JSONArray();
			String xpath = "//div[@class='info-oficina']/h3/a";
			
			List<WebElement> lst = webDriver.findElements(By.xpath(xpath));
			for (int i = 0; i < lst.size(); i++) {
				String officeTitle = Sel.getText(lst.get(i));
				
				JSONObject info = new JSONObject();
				info.put("title", officeTitle);
				a.put(info);
			}
			nearbyOffices.put("offices", a);
		} catch (Exception e) {}
	
		
		System.out.println("Nearby Offices: " + nearbyOffices);
		
		// Setting Post Params for Database
		Post p = new Post();
		p.fields.put("url", url);
		p.fields.put("title", title);
		p.fields.put("type", type);
		
		p.fields.put("covid19Info", covid19info);
		p.fields.put("paymentOptions", paymentOptions);
		
		p.fields.put("state", state);
		p.fields.put("county", county);
		p.fields.put("phone", phone);
		
		p.fields.put("addressParams", addressParams.toString());
		p.fields.put("hours", hours.toString());
		p.fields.put("hoursInfo", hoursInfo);

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
			
		} else if (Check.isRelatedFromTo(county, office) && from == county && to == office) {
			countyToOffice();
		}
	}
	
	private void stateToCounty() {
		display.fromTo(state, county);
	}
	
	private void countyToOffice() {
		display.fromTo(county, office);
	}
	
}
