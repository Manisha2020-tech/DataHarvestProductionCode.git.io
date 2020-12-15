package com.audaharvest.common;

import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class WebDriverUtil {
	
	public WebDriver getFirefoxDriver() {
		String phantomJSPath = System.getProperty("user.home") + "/phantomjs.exe";		
		Capabilities caps = new DesiredCapabilities();	    
		((DesiredCapabilities) caps).setJavascriptEnabled(true);                
	    ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
	    String [] phantomJsArgs = {"--ignore-ssl-errors=true", "--web-security=false"};
	    ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_GHOSTDRIVER_CLI_ARGS, phantomJsArgs);
	    ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomJSPath);
	    ((DesiredCapabilities) caps).setCapability("ACCEPT_SSL_CERTS", true);
		WebDriver driver = new  PhantomJSDriver(caps);
		driver.manage().window().setSize(new Dimension(1280, 1024));
		
        return driver;
	}

}
