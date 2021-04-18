package mysql;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MySQLBridge {

	PreparedStatement stmt;
	Connection conn;
	public static String dbname = "rippy";
	String url = "jdbc:mysql://144.204.27.226:3306/"+dbname+"?autoReconnect=true&useSSL=false&serverTimezone=CET#/information_schema";
	String servername = "144.204.27.226";
	String username = "xyz";
	String password = "xyz";
	
	boolean verbose = false;
	
	public MySQLBridge() {
		init();
	}
	
	void init() {
		try {
			this.conn = DriverManager.getConnection(url, username, password);
			System.out.println("Successfully connected (MySQL)!");
		} catch (Exception e) { e.printStackTrace(); }
	}
	
	private ResultSet execQuery(String query) {
		stmt = null;
		ResultSet rs = null;
		
		int tries = 0;
		while (true) {
			try {
				try {
					stmt = conn.prepareStatement(query);
				} catch (Exception e) {
					init();
					stmt = conn.prepareStatement(query);
				}
				if (query.contains("SELECT") || query.contains("SHOW")) {
					rs = stmt.executeQuery(query);
				} else {
					int res = stmt.executeUpdate(query);
					if (verbose)
						System.out.println("ExecUpdateCode: " + res);
					return null;
				}
				if (verbose)
					System.out.println("Executed Query!");
				return rs;
			} catch (SQLException e) {
				System.out.println(query);
				System.out.println("Error with executeQuery()!");
				e.printStackTrace();
				
				tries += 1;
				if (tries > 100) {
					break;
				}
			}
		}
		
		return null;
	}
	
	public ArrayList<Map<String, String>> query(String query) {

		ArrayList<Map<String, String>> table = new ArrayList<Map<String, String>>();
		Map<String, String> row = null;
		String params;
		String[] paramsArr;
		
		ResultSet rs = null;
		
		if (query.startsWith("SELECT") || query.startsWith("SHOW")) {
			rs = execQuery(query);
			params = query.substring(query.indexOf(" ") + 1, query.indexOf(" FROM")).trim();
			paramsArr = params.split(", ");
		} else {
			execQuery(query);
			return null;
		}

		try {
			while (rs.next()) {
				row = new HashMap<String, String>();
				for (int i = 0; i < paramsArr.length; i++) {
					String value = rs.getString(paramsArr[i]);
					row.put(paramsArr[i], value);
				}
				table.add(row);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try {
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return table;
	}
	
}
