package cfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import mysql.MySQLBridge;
import mysql.MySQLTools;
import tools.Display;

public class Manifest {
	
	public String tableName;
	
	public Display display;
	public WebDriver webDriver;
	
	public C initialNode; // SC
	public C endNode; // CC target
	
	public String baseURL;
	
	public ArrayList<C> scList;
	public ArrayList<Post> posts;
	
	public boolean mysqlInit = false;
	public MySQLBridge mysqlBridge;
	public MySQLTools mysqlTools;
	
	public Manifest() {
		scList = new ArrayList<C>();
		posts = new ArrayList<Post>();
	}
	
	public void insertPost(Post p) {
		initMySQL(p);
		
		if (p.fields.get("title") == "") {
			System.out.println("Failed to post due to empty fields.");
			return;
		}
		
		mysqlTools.createRow(getTableName(), p, "url", p.fields.get("url"));
	}
	
	public void updatePost(Post p) {}
	
	public void initMySQL(Post p) {
		if (!mysqlInit) {
			ArrayList<String> keys = new ArrayList<String>(p.fields.keySet());
			mysqlTools.createTable(getTableName(), keys);
			mysqlInit = true;
		}
	}
	
	public String getBaseURL() {
		return baseURL;
	}
	
	public void setBaseURL(String url) {
		this.baseURL = url;
	}

	public void goToCategoryFrom(C from, C to) {}
	
	public void setDisplay(Display d) {
		this.display = d;
	}
	
	public void setWebDriver(WebDriver webDriver) {
		this.webDriver = webDriver;
	}
	
	public void processContentPage(Map<String, String> row) {}
	
	public void setMySQLBridge(MySQLBridge m) {
		this.mysqlBridge = m;
	}
	
	public void setMySQLTools(MySQLTools t) {
		this.mysqlTools = t;
	}
	
	public void setTableName(String tName) {
		this.tableName = tName;
	}
	
	public String getTableName() {
		return this.tableName;
	}
	
}
