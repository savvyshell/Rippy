package mysql;

import java.util.ArrayList;
import java.util.Map;

import cfg.Post;

public class MySQLTools {

	MySQLBridge conn;
	String dbname = MySQLBridge.dbname;
	
	public MySQLTools(MySQLBridge m) {
		this.conn = m;
	}
	
	// Creation Functions
	public boolean createTable(String table, ArrayList<String> keys) {
		if (doesTableExist(table)) {
			System.out.println("`" + table + "` Table already exists :: createTable()");
			return false;
		}
		String q = "CREATE TABLE `"+dbname+"`.`"+table+"` ( `id` INT NOT NULL AUTO_INCREMENT ";
		String cols = "";
		for (int i = 0; i < keys.size(); i++) {
			cols += ", `"+keys.get(i)+"` TEXT NOT NULL ";
		}
		q += cols + ", PRIMARY KEY (`id`)) ENGINE = InnoDB;";
		conn.query(q);
		System.out.println("`" + table + "`" + " Table initialized.");
		return true;
	}
	
	public boolean createCol(String table, String col) {
		if (!doesTableExist(table) || doesColumnExist(table, col)) {
			System.out.println("`"+table+"`." + col + " - Table or Column already exist :: createCol()");
			return false;
		}
		String q = "ALTER TABLE `keyTable` ADD "+col+" TEXT NOT NULL";
		conn.query(q);
		System.out.println("Col `" + col + "` added to " + table);
		return true;
	}
	
	public boolean createRow(String table, Post p, String byCol, String byValue) {
		if (!doesTableExist(table)) {
			System.out.println("`" + table + "` - Table does not exist :: createRow()");
			return false;
		}
		Map<String, String> pRow = p.fields;
		
		if (doesRowExist(table, byCol, byValue)) {
			return false;
		}
		
		String keys = "";
		String values = "";
		for (Map.Entry<String, String> row : pRow.entrySet()) {
			String col = row.getKey();
			String val = row.getValue();
			
			val = val.replaceAll("'", "\\\\'");
			val = val.replaceAll("\n", "");
			
			keys += col + ",";
			values += "'" + val + "',";
		}
		
		keys = keys.substring(0, keys.length()-1);
		values = values.substring(0, values.length()-1);
		String q = String.format("INSERT INTO %s (%s) VALUES (%s)", table, keys, values);
		conn.query(q);
		System.out.println("Inserting row... " + q);
		return true;
	}

	// Updater Functions
	public boolean updateRow(String table, Post p, String byCol) {
		if (!doesTableExist(table) || !doesColumnExist(table, byCol) || !doesRowExist(table, byCol, p.fields.get(byCol))) {
			System.out.println("`" + table + "` - Table|Col|Row does not exist :: updateRow()");
			return false;
		}
		Map<String, String> pRow = p.fields;
		
		String byValue = "";
		
		String set="";
		for (Map.Entry<String, String> row : pRow.entrySet()) {
			String col = row.getKey();
			String val = row.getValue();
			if (col.equals(byCol)) {
				byValue = val;
			}
			val = val.replace("'", "\'");
			set += String.format("%s='%s',", col, val);
		}
		set = set.substring(0, set.length()-1);
		if (byValue.length() <= 0)
			return false;
		String q = String.format("UPDATE %s SET %s WHERE %s='%s'", table, set, byCol, byValue);
		System.out.println("Updating row... " + p.toString());
		conn.query(q);
		return true;
	}
	
	// Exists Functions
	public boolean doesTableExist(String table) {
		String q = "SELECT TABLE_NAME FROM information_schema.tables WHERE table_schema = '"+dbname+"' AND table_name = '"+table+"' LIMIT 1;";
		return (getNumRows(conn.query(q)) > 0);
	}
	
	public boolean doesColumnExist(String table, String column) {
		String q = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = '"+dbname+"'AND TABLE_NAME = '"+table+"' AND COLUMN_NAME = '"+column+"'";
		return (getNumRows(conn.query(q)) > 0);
	}
	
	public boolean doesRowExist(String table, String keyField, String byField) {
		String q = "SELECT id FROM "+table+" WHERE "+keyField+" = '" + byField + "'";
		return (getNumRows(conn.query(q)) > 0);
	}
	
	public int getNumRows(ArrayList<Map<String, String>> results) {
		return results.size();
	}
	
	// Data Functions
	
	public ArrayList<String> getProcessedURLs(String table) {
		ArrayList<String> arr = new ArrayList<String>();
		
		String q = "SELECT url FROM `"+table+"` WHERE 1";
		ArrayList<Map<String, String>> results = conn.query(q);
		for (int i = 0; i < results.size(); i++) {
			arr.add(results.get(i).get("url"));
		}
		
		return arr;
	}
	
}
