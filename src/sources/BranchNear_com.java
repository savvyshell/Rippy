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

public class BranchNear_com extends Manifest {
	
	// CC
	public CC branch;

	// Initial SC
	public SC state;
	
	// SC
	public SC cityTown;
	
	public BranchNear_com() {
		super();
		super.setBaseURL("https://www.bankbranchlocator.com/");
		super.setTableName("BranchNear_com");
		
		// Initialize Categories
		branch = new CC();
		branch.setTitle("Branch");
		branch.setTag("~BANK INFORMATION");
		branch.setXPath("//h3[contains(text(), 'BANK INFORMATION')]");
		
		state = new SC(branch);
		state.setTitle("State");
		state.setTag("~SEARCH BANKS BY STATE");
		state.setXPath("//ul/li/div/a[@class='litem']");
		
		cityTown = new SC(branch);
		cityTown.setTitle("City/Town");
		cityTown.setTag("-banks.html");
		cityTown.setXPath("//div[@class='leftdiv']/ul/li/div/a[@class='litem']");
		cityTown.prePage = true;
		cityTown.prePageTag = "banks-in-";
		cityTown.prePageXPath = "//div[@class='left_branches']//div[@class='near_title']/a";
		
		// Set Relations -- > SubCategories
		state.addRelation(cityTown);
		
		cityTown.addRelation(branch);
		
		// Set Relations --> ContentCategory Cols
		branch.addRelation(cityTown);
		
		// Relate back to Manifest
		scList.add(cityTown);
		initialNode = state;
		endNode = branch;
	}

	public void processContentPage(Map<String, String> row) {
		
		String url = webDriver.getCurrentUrl();
		
		String title = "";
		
		// Branch Information
		String branchName = "";
		String serviceType = "";
		String stateAndCounty = "";
		String cityOrTown = "";
		
		// Bank Information
		String bankName = "";
		String bankType = "";
		String fdicInsurance = "";
		String routingNumber = "";
		String onlineBanking = "";
		String branchCount = "";
		
		// addressParams
		String streetAddress = ""; // 9223 Rhody Dr
		String addressLocality = ""; // Chimacum
		String addressRegion = ""; // WA
		String postalCode = ""; // 98325
		
		// contactNumbers
		String phone = "";
		
		JSONObject addressParams = new JSONObject();
		
		JSONArray branchHours = new JSONArray();
		JSONObject hours = new JSONObject();
		
		JSONObject extraData = new JSONObject();
		
		JSONObject nearbyBranches = new JSONObject();
		JSONObject nearbyBanks = new JSONObject();
		
		//String nextHref = "";
		try {
			String branchXPath = "//span[@class='dtab' and contains(text(), 'Branch Name:')]/following-sibling::span[1]";
			branchName = Sel.getTextByXPath(webDriver, branchXPath, false);
			String bankXPath = "//span[@class='rtab' and contains(text(), 'Bank Name:')]/following-sibling::span[1]/a";
			//nextHref = webDriver.findElement(By.xpath(bankXPath)).getAttribute("href");
			bankName = Sel.getTextByXPath(webDriver, bankXPath, false);
			title = bankName + " " + branchName;
		} catch (Exception e) {}

		System.out.println("URL: " + url);
		System.out.println("Title: " + title);
		
		try {
			String xpath = "//span[@class='dtab' and contains(text(), 'State & County:')]/following-sibling::span[1]";
			stateAndCounty = Sel.getTextByXPath(webDriver, xpath, false);
			stateAndCounty = stateAndCounty.replace(" - ", "-").trim();
		} catch (Exception e) {}
		
		try {
			String xpath = "//span[@class='dtab' and contains(text(), 'Service Type:')]/following-sibling::span[1]";
			serviceType = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//span[@class='dtab' and contains(text(), 'City or Town:')]/following-sibling::span[1]";
			cityOrTown = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//span[@class='dtab' and contains(text(), 'Phone Number:')]/following-sibling::span[1]";
			phone = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		System.out.println("Branch Name: " + branchName);
		System.out.println("State/County: " + stateAndCounty);
		System.out.println("Service Type: " + serviceType);
		System.out.println("City or Town: " + cityOrTown);
		System.out.println("Phone: " + phone);
		
		// addressParams
		try {
			String xpath = "//span[@class='dtab' and contains(text(), 'Office Address:')]/following-sibling::span[1]";
			String fullStreetAddress = Sel.getTextByXPath(webDriver, xpath, false);
			
			String[] arrAddr = fullStreetAddress.split(",");
			
			streetAddress = arrAddr[0];
			addressLocality = arrAddr[1];
			addressRegion = (arrAddr[2].split(" "))[0];
			postalCode = (arrAddr[2].split(" "))[1];
			
			addressParams.put("address", streetAddress);
			addressParams.put("locality", addressLocality);
			addressParams.put("region", addressRegion);
			addressParams.put("postalcode", postalCode);
		} catch (Exception e) {}
		
		System.out.println("Address Params: " + addressParams);
		
		try {
			String xpath = "//span[@class='rtab' and contains(text(), 'Bank Type:')]/following-sibling::span[1]";
			bankType = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//span[@class='rtab' and contains(text(), 'FDIC Insurance:')]/following-sibling::span[1]";
			fdicInsurance = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//span[@class='rtab' and contains(text(), 'Routing Number:')]/following-sibling::span[1]";
			routingNumber = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//span[@class='rtab' and contains(text(), 'Online Banking:')]/following-sibling::span[1]";
			onlineBanking = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		try {
			String xpath = "//span[@class='rtab' and contains(text(), 'Branch Count:')]/following-sibling::span[1]";
			branchCount = Sel.getTextByXPath(webDriver, xpath, false);
		} catch (Exception e) {}
		
		
		System.out.println("Bank Name: " + bankName);
		System.out.println("Bank Type: " + bankType);
		System.out.println("FDIC Insurance: " + serviceType);
		System.out.println("Routing Number: " + cityOrTown);
		System.out.println("Online Banking: " + phone);
		System.out.println("Branch Count: " + phone);
		
		try {
			String xpath = "//span[@class='dvalue hourvalue']";
			ArrayList<String> branchHoursLst = Sel.getListWithList(webDriver.findElements(By.xpath(xpath)), false);
			branchHours = Manipulation.listToJSONArray(branchHoursLst);
			hours.put("Branch Hours", branchHours);
		} catch (Exception e) {}

		System.out.println("Hours: " + hours);
		System.out.println("Extra Data: " + extraData);
		
		try {
			JSONArray a = new JSONArray();
			String xpath = "//div[@class='near_branches']";
			
			List<WebElement> lst = webDriver.findElements(By.xpath(xpath));
			for (int i = 0; i < lst.size(); i++) {
				
				WebElement titleElem = lst.get(i).findElement(By.xpath("//div[@class='near_title']/a"));
				WebElement awayElem = lst.get(i).findElement(By.xpath("//div[@class='near_title']/span"));
				WebElement addrElem = lst.get(i).findElement(By.xpath("//div[@class='undertitletext']"));
				
				String branchTitle = Sel.getText(titleElem);
				String distanceAway = Sel.getText(awayElem);
				String addr = Sel.getText(addrElem);
				
				JSONObject info = new JSONObject();
				info.put("title", branchTitle);
				info.put("distanceAway", distanceAway);
				info.put("addr", addr);
				a.put(info);
			}
			nearbyBranches.put("branches", a);
		} catch (Exception e) {}
		
		try {
			JSONArray a = new JSONArray();
			String xpath = "//div[@class='right_branches']";
			
			List<WebElement> lst = webDriver.findElements(By.xpath(xpath));
			for (int i = 0; i < lst.size(); i++) {
				
				WebElement titleElem = lst.get(i).findElement(By.xpath("//div[@class='near_title']/a"));
				WebElement awayElem = lst.get(i).findElement(By.xpath("//div[@class='near_title']/span"));
				WebElement addrElem = lst.get(i).findElement(By.xpath("//div[@class='undertitletext']"));
				
				String branchTitle = Sel.getText(titleElem);
				String distanceAway = Sel.getText(awayElem);
				String addr = Sel.getText(addrElem);
				
				JSONObject info = new JSONObject();
				info.put("title", branchTitle);
				info.put("distanceAway", distanceAway);
				info.put("addr", addr);
				a.put(info);
			}
			nearbyBanks.put("banks", a);
		} catch (Exception e) {}
		
		System.out.println("Nearby Branches: " + nearbyBranches);
		System.out.println("Nearby Banks: " + nearbyBanks);
		
		// Setting Post Params for Database
		Post p = new Post();
		p.fields.put("url", url);
		p.fields.put("title", title);
		
		p.fields.put("branchName", branchName);
		p.fields.put("serviceType", serviceType);
		p.fields.put("stateCounty", stateAndCounty);
		p.fields.put("cityOrTown", cityOrTown);
		p.fields.put("phone", phone);

		p.fields.put("bankName", bankName);
		p.fields.put("bankType", bankType);
		p.fields.put("fdicInsurance", fdicInsurance);
		p.fields.put("routingNumber", routingNumber);
		p.fields.put("onlineBanking", onlineBanking);
		p.fields.put("branchCount", branchCount);
		
		p.fields.put("addressParams", addressParams.toString());
		p.fields.put("hours", hours.toString());

		p.fields.put("nearbyBranches", nearbyBranches.toString());
		p.fields.put("nearbyBanks", nearbyBanks.toString());
		p.fields.put("extraData", extraData.toString());
		
		this.insertPost(p);
		this.posts.add(p);
	}
	
	// CUSTOM NAVIGATION FOR IN-BETWEENS
	@Override
	public void goToCategoryFrom(C from, C to) {
		// State --> County, City --> Postal Office
		
		if (Check.isRelatedFromTo(state, cityTown) && from == state && to == cityTown) {
			stateToCityTown();
			
		} else if (Check.isRelatedFromTo(cityTown, branch) && from == cityTown && to == branch) {
			cityTownToBranch(cityTown);
			
		}
	}
	
	private void stateToCityTown() {
		display.fromTo(state, cityTown);
	}
	
	private void cityTownToBranch(C from) {
		display.fromTo(from, branch);
	}
	
	
	
}
