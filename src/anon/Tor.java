package anon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Tor extends Browser {

	WebDriver webDriver;
	WebDriverWait wait;
	Process torProcess;
	
	public static boolean torRunning = false;
	private String torBinaryPath = "C:\\Users\\siava\\Desktop\\Tor Browser\\Browser\\firefox.exe";
	
	public WebDriver getWebDriver() {
		
		if (!Tor.torRunning) {
			// Tor Process
			Runtime runTime = Runtime.getRuntime();
		    try {
				torProcess = runTime.exec(torBinaryPath + " -n");
				System.out.println("Tor Process Started");
				Tor.torRunning = true;
			} catch (IOException e) { e.printStackTrace(); System.exit(0); }
		}
	    
	    FirefoxProfile profile = new FirefoxProfile();
	    profile.setPreference("network.proxy.type", 1);
	    profile.setPreference("network.proxy.socks", "127.0.0.1");
	    profile.setPreference("network.proxy.socks_port", 9150);
	    FirefoxOptions firefoxOptions = new FirefoxOptions();
	    firefoxOptions.setProfile(profile);
	    
	    webDriver = new FirefoxDriver(firefoxOptions);
	    webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	    //webDriver.manage().window().maximize();
	    wait = new WebDriverWait(webDriver, 30);
	    
	    webDriver.get("http://whatismyipaddress.com/");
	    return webDriver;
	}
	
	public WebDriver restart() {
		try {
			afterClass();
			Tor.torRunning = false;
			return getWebDriver();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	public void refreshTorIdentity(String userName) {
	    try (Socket socket = new Socket("127.0.0.1", 9151)) {
	        OutputStream output = socket.getOutputStream();
	        String authenticationCommand = String.format("AUTHENTICATE \"%s\"\r\n", userName);
	        output.write(authenticationCommand.getBytes());
	        output.write("SIGNAL NEWNYM\r\n".getBytes());
	        InputStream input = socket.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	        String line = reader.readLine();
	        if (!line.contains("250")) {
	            System.out.println("Unable to signal new user to server.");
	        }
	    } catch (UnknownHostException ex) {
	        System.out.println("Server not found: " + ex.getMessage());
	    } catch (IOException ex) {
	        System.out.println("I/O error: " + ex.getMessage());
	    }
	}
	
	public void afterClass() throws InterruptedException {
	    webDriver.quit();
	    torProcess.descendants().forEach(ph -> {
	        ph.destroy();
	    });
	    torProcess.destroyForcibly();
	}
	
}
