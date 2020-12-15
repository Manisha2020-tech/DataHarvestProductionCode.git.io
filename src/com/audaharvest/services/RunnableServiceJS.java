package com.audaharvest.services;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.audaharvest.common.SSLTool;
import com.audaharvest.common.WebDriverUtil;
import com.audaharvest.constants.Constants;

public class RunnableServiceJS implements Runnable {
	private static ConcurrentMap<String, String> headerLinksJS;
	private String url;
	private CountDownLatch latch;
	private String[] makeArray;
	private Map<String, String> urlMap;
	private WebDriverUtil webDriverUtil;
	private WebDriver driver;
	
	public RunnableServiceJS(String url, CountDownLatch latch, String[] makeArray, Map<String, String> urlMap){
		this.url = url;
		this.latch = latch;
		this.makeArray = makeArray;
		this.headerLinksJS = new ConcurrentHashMap<String, String>();
		this.urlMap = urlMap;
		this.webDriverUtil = new WebDriverUtil();
		this.driver = webDriverUtil.getFirefoxDriver();
	}
	
	@Override
	public void run() {
		
		driver.get(url);
		
		if((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains(Constants.LINK_FROM_VIEW_SOURCE.toLowerCase())) {
			String viewSource = getViewSource(driver);
			int lastIndex = urlMap.get(Constants.LINK_FINDER).lastIndexOf(")");
			String newStr =  urlMap.get(Constants.LINK_FINDER).replace(String.valueOf(urlMap.get(Constants.LINK_FINDER).charAt(lastIndex)),"");
			String[] linkStrArr = newStr.replace(Constants.LINK_FROM_VIEW_SOURCE+"(", "").split("\\^");
			Matcher matcher = Pattern.compile(Pattern.quote(linkStrArr[0]) + "(.*?)"+ Pattern.quote(linkStrArr[2])).matcher(viewSource);
			while(matcher.find()) {
				String str = matcher.group(0).replaceAll(linkStrArr[0], "").replace(linkStrArr[2], "");
				if(str.toLowerCase().contains(urlMap.get(Constants.ROOT_URL))) {
					if(linkStrArr[1].equals("null")) {
						headerLinksJS.put(str, "");
						System.out.println(str);
					} else {
						headerLinksJS.put(linkStrArr[1]+str, "");
						System.out.println(linkStrArr[1]+str);
					}
				} else {
					if(linkStrArr[1].equals("null")) {
						headerLinksJS.put(urlMap.get(Constants.ROOT_URL)+str, "");
						System.out.println(urlMap.get(Constants.ROOT_URL)+str);
					} else {
						headerLinksJS.put(urlMap.get(Constants.ROOT_URL)+linkStrArr[1]+str, "");
						System.out.println(urlMap.get(Constants.ROOT_URL)+linkStrArr[1]+str);
					}
				}
			}
		} else if((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains("onclick")) {
			Pattern pattern = Pattern.compile("(\\\"(.*?)\\\")");
			Matcher matcher = pattern.matcher(urlMap.get(Constants.LINK_FINDER));
			String[] array = new String[3];
			int count=0;
			while(matcher.find()) {
				String matchStr = matcher.group(0).substring(1, matcher.group(0).length()-1);
				array[count]=matchStr;
				//System.out.println(array[count]);
				count++;
			}
			String onclickText = urlMap.get(Constants.LINK_FINDER).trim().substring(0, 7);
			List<WebElement> links = driver.findElements(By.xpath("//*[@onclick]"));
			for(WebElement link : links){
				if(link.getAttribute(onclickText).contains(array[0]) && link.getAttribute(onclickText).contains(array[2])) {
					Matcher match = Pattern.compile(Pattern.quote(array[0]) + "(.*?)"+ Pattern.quote(array[2])).matcher(link.getAttribute(onclickText));
					while(match.find()){
						//System.out.println(match.group(0));
						String str = match.group(0).replace(array[0], "").replace(array[2], "").trim();
						System.out.println(str);
						if(str.toLowerCase().contains(urlMap.get(Constants.ROOT_URL))) {
							if(array[1].equals("null")) {
								headerLinksJS.put(str, "");
							} else {
								headerLinksJS.put(array[1]+str, "");
							}
						} else {
							if(array[1].equals("null")) {
								headerLinksJS.put(urlMap.get(Constants.ROOT_URL)+str, "");
							} else {
								headerLinksJS.put(urlMap.get(Constants.ROOT_URL)+array[1]+str, "");
							}
						}
						
					}
				}
				
			}
			
		} else {
			if((urlMap.get(Constants.LINK_FINDER) != null) && !(urlMap.get(Constants.LINK_FINDER).isEmpty())) {			
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				List<WebElement> aLinks = driver.findElements(By.cssSelector("a"));
				for(WebElement link : aLinks){
					if(link.getText().contains(urlMap.get(Constants.LINK_FINDER))){
						System.out.println(link.getAttribute("href"));
						headerLinksJS.put(link.getAttribute("href"), "");
					} 
				}
				try {
					if(driver.findElements(By.cssSelector(urlMap.get(Constants.LINK_FINDER))).size() > 0) {
						List<WebElement> links = driver.findElements(By.cssSelector(urlMap.get(Constants.LINK_FINDER)));
						for(WebElement link : links){
							headerLinksJS.put(link.getAttribute("href"), "");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				/*try {
					if(driver.findElements(By.xpath("//*[@id='inline-style-27']")).size() > 0) {
						List<WebElement> links = driver.findElements(By.xpath("//*[@id='inline-style-27']"));
						for(WebElement link : links){
							headerLinksJS.put(link.getAttribute("href"), "");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}*/
				
			}
			
		}
		
		
		latch.countDown();
		driver.quit();
	}
	
	public static ConcurrentMap<String, String> getHeaderLinksJS(){
		return headerLinksJS;
	}
	
	private String getViewSource(WebDriver driver) {
		String pageSource = null;
			try {
				pageSource = driver.getPageSource();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return pageSource;
		
	}
	
}
