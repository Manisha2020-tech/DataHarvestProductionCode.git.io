package com.audaharvest.services;

import java.awt.Robot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.audaharvest.common.ExcelFileReader;
import com.audaharvest.common.SSLTool;
import com.audaharvest.common.WebDriverUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.iservices.ICSVFileWriter;
import com.audaharvest.iservices.IDataHarvester;
import com.audaharvest.json.model.Nodes;
import com.audaharvest.model.Vehicle;
import com.audaharvest.parsers.InventoryPageParser;
import com.audaharvest.parsers.JsonInventoryPageParser;
import com.audaharvest.parsers.JsonParser;
import com.audaharvest.parsers.Parser;
import com.audaharvest.parsers.ParserFactory;
import com.audaharvest.parsers.ParserJS;
import com.audaharvest.parsers.ParserJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.awt.event.KeyEvent;


public class DataHarvester implements IDataHarvester{
	private ExcelFileReader excelReader;
	private Map<String, Map<String, String>> sitesParamsMap;
	private List<String> manufacCodesList;
	private String manufacCodesPath;
	private String urlsParamsDefsPath;
	private String message="";

	public DataHarvester(String manufacCodesPath, String urlsParamsDefsPath) {
		this.excelReader = new ExcelFileReader();
		this.manufacCodesPath = manufacCodesPath;
		this.urlsParamsDefsPath = urlsParamsDefsPath;
		init();
	}
	
	public void init() {
		try {
			manufacCodesList = excelReader.getExcelData(manufacCodesPath);
			sitesParamsMap = excelReader.urlsParamsMap(urlsParamsDefsPath);
			
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	
	public void startScrapping() {
		System.out.println("Number of urls in config file (UrlsParamsDefs) :"+sitesParamsMap.size());
		if (Constants.WOWO_LOG_FIELD_COUNT.equals("ON")) {
			Logger logger = LogManager.getLogger(DataHarvester.class);
			logger.debug("Website | Total records(main) | Total records(Garbage) | VIN Count | Kilometer Count | Stock Count | Price Count | Location Count |"
						+ "Body Style Count | Trans Count | Engine Count | Dealer Name Count | Dealer City Count | Dealer Province Count | Dealer Zip Code Count | Pub Code | Listing Date");
		}
		for (String keyStr: sitesParamsMap.keySet()) {
			Map<String, String> urlMap = sitesParamsMap.get(keyStr);
			Map<String, String> blockDataTextsMap = vehicleFeatureTextsFromUrlMap(urlMap);
			String url = urlMap.get(Constants.URL);
			String rootUrl = urlMap.get(Constants.ROOT_URL);
			try {
				if((urlMap.get(Constants.GROUP)!= null) && (urlMap.get(Constants.GROUP).equals("jsonparser"))) {
					Set<Vehicle> set = (Set<Vehicle>) (new JsonParser()).getVehicles(urlMap.get(Constants.URL),urlMap.get(Constants.BLOCK_DATA_FINDER),urlMap);
					ICSVFileWriter csvWriter = new CSVFileWriter();
					String truncatedDomainName = rootUrl.replaceAll("(http://www.|https://www.|http://|https://|/)", "");
					String currentTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
					String pubCode ="";
					if(urlMap.get(Constants.PUB_CODE)!= null && !urlMap.get(Constants.PUB_CODE).isEmpty()){
						pubCode = urlMap.get(Constants.PUB_CODE);
					}
					String fileName = System.getProperty("user.home")+"/mainfiles/"+pubCode+truncatedDomainName+currentTime+".csv";
					String garbageFileName = System.getProperty("user.home")+"/garbage/"+"GARBAGE"+pubCode+truncatedDomainName+currentTime+".csv";
					csvWriter.writeCSVFileInventoryPage(fileName, garbageFileName, set, urlMap);
				}
				
				//Jason inventory with html ad jsoup selector is used. Can be extended.	
				else if((urlMap.get(Constants.GROUP)!= null) && (urlMap.get(Constants.GROUP).equals("G2JsonInventoryHtmlAd")) && (urlMap.get(Constants.DOWNLOAD_PAGE).equals("Download"))) {
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrappingFromJsonInventoryHtmlAd(url, rootUrl, urlMap, set, blockDataTextsMap);
				//Jason inventory with json ad	
				} else if((urlMap.get(Constants.GROUP)!= null) && (urlMap.get(Constants.GROUP).equals("G2JsonInventoryJsonAd")) && (urlMap.get(Constants.DOWNLOAD_PAGE).equals("Download"))) {
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrappingFromJsonInventoryJsonAd(url, rootUrl, urlMap, set, blockDataTextsMap);
				//Link build using Phantom and ad is in json page
				} else if((urlMap.get(Constants.GROUP)!= null) && (urlMap.get(Constants.GROUP).equals("G4LinkJsonAd"))) {
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrappingFromG4LinkJsonAd(url, rootUrl, urlMap, set, blockDataTextsMap);
				//Jason link builder from download json source with different path variable
				} else if((urlMap.get(Constants.GROUP)!= null) && (urlMap.get(Constants.GROUP).equals("JsonLinkBuilderG4"))) {
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrappingFromJsonSource(url, rootUrl, urlMap, set, blockDataTextsMap);
				//Jason inventory pages not individual ad pages in jason form
				} else if((urlMap.get(Constants.GROUP)!= null) && (urlMap.get(Constants.GROUP).equals("JI"))) {
					Set<Vehicle> set = new HashSet<Vehicle>();
					doWebScrappingFromJsonInventoryPage(url, rootUrl, urlMap, set, blockDataTextsMap);
				//Only WIX page
				} else if((urlMap.get(Constants.GROUP)!= null) && (urlMap.get(Constants.GROUP).equals("G2WIX"))) {
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrappingFromWIXPage(url, rootUrl, urlMap, set, blockDataTextsMap);
				//Inventory and G3 and download page
				} else if((urlMap.get(Constants.DOWNLOAD_PAGE) != null) && (urlMap.get(Constants.GROUP).equals("G3")) && (urlMap.get(Constants.DOWNLOAD_PAGE).equals("Download"))) {
					Set<Vehicle> set = new HashSet<Vehicle>();
					doWebScrappingFromInventoryAndDownloadPage(url, rootUrl, urlMap, set, blockDataTextsMap);
				//Only Inventory page
				} else if((urlMap.get(Constants.DOWNLOAD_PAGE) != null) && (urlMap.get(Constants.GROUP).equals("G4")) && (urlMap.get(Constants.DOWNLOAD_PAGE).equals("Download"))) {
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrappingFromSeleniumDriversAndDownloadPage(url, rootUrl, urlMap, set, blockDataTextsMap);
				//Only Inventory page
				} else if((urlMap.get(Constants.GROUP)!= null) && (urlMap.get(Constants.GROUP).equals("G3"))) {
					Set<Vehicle> set = new HashSet<Vehicle>();
					doWebScrappingFromInventoryPage(url, rootUrl, manufacCodesPath, urlMap, set, blockDataTextsMap);
				//Only download page
				} else if((urlMap.get(Constants.DOWNLOAD_PAGE)!= null) && (urlMap.get(Constants.DOWNLOAD_PAGE).equals("Download"))) {
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrappingFromDownloadedPage(url, rootUrl, manufacCodesPath, urlMap, set, blockDataTextsMap);
				//Links using Selenium. First try with G4
				} else if((urlMap.get(Constants.DOWNLOAD_PAGE)!= null) && (urlMap.get(Constants.DOWNLOAD_PAGE).equals("getLinksUsingSelenium"))) {
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrappingFromJSPage(url, rootUrl, manufacCodesPath, urlMap, set, blockDataTextsMap);
				//For JS pages mostly
				} else if ((urlMap.get(Constants.GROUP)!= null) && (urlMap.get(Constants.GROUP).equals("G4"))) {
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrappingFromSeleniumDrivers(url, rootUrl, urlMap, set, blockDataTextsMap);
				} else {
				//All the rest G, G2
					Set<Future<Vehicle>> set = new HashSet<Future<Vehicle>>();
					doWebScrapping(url, rootUrl, manufacCodesPath, urlMap, set, blockDataTextsMap);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//Web scrapping will start for each url passed
	public void doWebScrapping(String url, String rootUrl, String manufacCodesPath, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap){
		
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
		CountDownLatch latch1 = null;
		CountDownLatch latch2 = null;
		Document doc = null;
		Set<String> allValidUrls;
		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
		if(Constants.WEBSCRAPPER_MODE.equals("AUTO")) {
			urlMap = checkWithTrainingData(urlMap);
		}
		if(urlMap.get(Constants.URL).contains(Constants.INVENTORY_URLS)) {
			allValidUrls = getAllInventoryUrls(urlMap);
			} else {
				try {
				//doc = (Document) Jsoup.connect(url).timeout(20*1000).get();	
				Connection con = Jsoup.connect(url)
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
						.referrer("http://www.google.com")
						.ignoreContentType(true)
						.followRedirects(true)
						.timeout(60*1000);
				Connection.Response res = con.execute();

					if(res.statusCode() == 200) {
					doc = con.get();
					}
				} catch (IOException e){
					e.printStackTrace();
				}
			allValidUrls = getAllUrls(doc, rootUrl, urlMap);
			allValidUrls.add(url);
			}
			//allValidUrls.add(url);
			System.out.println("All valid urls: " + allValidUrls.size());
			System.out.println("Now sending urls to multiple threads to parse all urls for header links. Please wait...");
			latch1 = new CountDownLatch(allValidUrls.size());
			ExecutorService eService = Executors.newFixedThreadPool(10);
			Iterator<String> iterator = allValidUrls.iterator();
			
			while(iterator.hasNext()) {
				String urlStr = iterator.next();				
				Runnable worker = new RunnableService(urlStr, latch1, makeArray, urlMap);
				if(Constants.THREAD_DELAY.equals("True")) {
					try {
						eService.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				eService.execute(worker);
			}
			eService.shutdown();
			try {
				latch1.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			ConcurrentMap<String, String> headerLinks = RunnableService.getHeaderLinks();
			int size = headerLinks.keySet().size();
			latch2 = new CountDownLatch(size);
			System.out.println("Total links found: " + size);
			checkVehicleCount(size,urlMap,allValidUrls.iterator().next());
			System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
			//WebDriver driver = null;
			/*if(Constants.SELENIUM_REQUIRED.equals("Yes")) {
				WebDriverUtil webDriverUtil = new WebDriverUtil();
				driver = webDriverUtil.getFirefoxDriver();
			}*/
			ExecutorService executor = Executors.newFixedThreadPool(10);
			Iterator<String> itr = headerLinks.keySet().iterator();
			while(itr.hasNext()) {
				ParserFactory factory = new ParserFactory();
				Parser parser = factory.createParser(urlMap.get(Constants.GROUP));
				parser.setUrl(itr.next());
				parser.setLatch(latch2);
				parser.setMakeArray(makeArray);
				parser.setUrlMap(urlMap);
				parser.setUrlMapVehicleFeatures(blockDataTextsMap);
				/*if(driver != null)
					parser.setSeleniumDriver(driver);*/
				if(Constants.SELENIUM_REQUIRED.equals("Yes")) {
					WebDriverUtil webDriverUtil = new WebDriverUtil();
					WebDriver driver = webDriverUtil.getFirefoxDriver();
					parser.setSeleniumDriver(driver);
				}
				Callable<Vehicle> callable = parser;				
				Future<Vehicle> future = executor.submit(callable);
				set.add(future);
				if(Constants.THREAD_DELAY.equals("True")) {
					try {
						executor.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} 		
			try {
				latch2.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			executor.shutdown();
			writeFile(rootUrl, urlMap, set);
	}
	//To get all urls from parent url
	public Set<String> getAllUrls(Document doc, String rootUrl, Map<String, String> urlMap){
		Set<String> urlSet = new HashSet<String>();
		Elements links = doc.select("a[href]");
		for(Element link : links){
			if(link.attr("abs:href").toLowerCase().contains(rootUrl.toLowerCase())) {
				urlSet.add(link.attr("abs:href"));
			}
		}
		return urlSet;
	}
	//To get all inventory urls 
	public Set<String> getAllInventoryUrls(Map<String, String> urlMap){
		Set<String> urlSet = new HashSet<String>();
		String[] inventoryUrlArr;
		String urlStr = urlMap.get(Constants.URL).replaceAll("("+Constants.INVENTORY_URLS+"|\\(|\\))", "");
		if(urlStr.contains("^")) {
			inventoryUrlArr = urlStr.split("\\^");
			for(String str: inventoryUrlArr){
				urlSet.add(str.trim());
			}
		} else {
			urlSet.add(urlStr.trim());
		}
		
		return urlSet;
	}
	
	public Map<String, String> vehicleFeatureTextsFromUrlMap(Map<String, String> urlMap) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constants.MANUFACTURER, urlMap.get(Constants.MANUFACTURER));
		map.put(Constants.MODEL, urlMap.get(Constants.MODEL));
		map.put(Constants.YEAR, urlMap.get(Constants.YEAR));	
		map.put(Constants.BODYSTYLE, urlMap.get(Constants.BODYSTYLE));
		map.put(Constants.ENGINE, urlMap.get(Constants.ENGINE));
		map.put(Constants.TRANSMISSION, urlMap.get(Constants.TRANSMISSION));		
		map.put(Constants.DRIVETRAIN, urlMap.get(Constants.DRIVETRAIN));
		map.put(Constants.EXTERIOR_COLOR, urlMap.get(Constants.EXTERIOR_COLOR));
		map.put(Constants.INTERIOR_COLOR, urlMap.get(Constants.INTERIOR_COLOR));
		map.put(Constants.FUEL, urlMap.get(Constants.FUEL));
		map.put(Constants.PRICE, urlMap.get(Constants.PRICE));
		map.put(Constants.STOCK_NUM, urlMap.get(Constants.STOCK_NUM));
		map.put(Constants.VIN, urlMap.get(Constants.VIN));
		map.put(Constants.LOCATION, urlMap.get(Constants.LOCATION));
		map.put(Constants.MILEAGE, urlMap.get(Constants.MILEAGE));
		map.put(Constants.MODEL_CODE, urlMap.get(Constants.MODEL_CODE));
		if(urlMap.get(Constants.TRIM)!= null && !urlMap.get(Constants.TRIM).isEmpty())
			map.put(Constants.TRIM, urlMap.get(Constants.TRIM));
		map.put(Constants.DOORS, urlMap.get(Constants.DOORS));
		map.put(Constants.PASSENGERS, urlMap.get(Constants.PASSENGERS));
		map.put(Constants.SALES_NUMBER, urlMap.get(Constants.SALES_NUMBER));
		if(urlMap.get(Constants.UNWANTED_BLOCK_DATA)!= null && !urlMap.get(Constants.UNWANTED_BLOCK_DATA).isEmpty()) {
			String unwantedBlockData = urlMap.get(Constants.UNWANTED_BLOCK_DATA);
			String[] array = unwantedBlockData.split("\\^");
			for (int i=0; i<array.length; i++) {
				map.put(array[i], array[i]);
			}
		}
		return map;
	}
	
	//Web scrapping will start for each url passed
	public void doWebScrappingFromDownloadedPage(String url, String rootUrl, String manufacCodesPath, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap){
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
		CountDownLatch latch1 = null;
		CountDownLatch latch2 = null;
		Document doc = null;
		File input1 = null;
		File input2 = null;
		String pageName = null;
		String downloadedFilePathHTML = null;
		String downloadedFilePathHTM = null;
		if(!urlMap.get(Constants.URL).contains("http")) {
			downloadedFilePathHTML = System.getProperty("user.home") + "\\"+urlMap.get(Constants.URL)+".html";
			downloadedFilePathHTM = System.getProperty("user.home") + "\\"+urlMap.get(Constants.URL)+".htm";
		} else {
			pageName = downloadPage(urlMap);
			downloadedFilePathHTML = System.getProperty("user.home") + "\\"+pageName+".html";
			downloadedFilePathHTM = System.getProperty("user.home") + "\\"+pageName+".htm";
		}
		
		/*String downloadedFilePathHTML = System.getProperty("user.home") + "\\"+"a.html";
		String downloadedFilePathHTM = System.getProperty("user.home") + "\\"+"a.htm";*/
		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
		try {
			input1 = new File(downloadedFilePathHTML);
			if(input1.exists()) {
				doc = Jsoup.parse(input1, "UTF-8", rootUrl+"/");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			input2 = new File(downloadedFilePathHTM);
			if(input2.exists()) {
				doc = Jsoup.parse(input2, "UTF-8", rootUrl+"/");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		ConcurrentMap<String, String> headerLinks = getHeaderLinksFromDownloadPage(doc, rootUrl, urlMap);
		int size = headerLinks.keySet().size();
		latch2 = new CountDownLatch(size);
		System.out.println("Total links found: " + size);
		System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		Iterator<String> itr = headerLinks.keySet().iterator();
		while(itr.hasNext()) {
			ParserFactory factory = new ParserFactory();
			Parser parser = factory.createParser(urlMap.get(Constants.GROUP));
			parser.setUrl(itr.next());
			parser.setLatch(latch2);
			parser.setMakeArray(makeArray);
			parser.setUrlMap(urlMap);
			parser.setUrlMapVehicleFeatures(blockDataTextsMap);
			Callable<Vehicle> callable = parser;				
			Future<Vehicle> future = executor.submit(callable);
			set.add(future);
			if(Constants.THREAD_DELAY.equals("True")) {
				try {
					executor.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} 		
		try {
			latch2.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		writeFile(rootUrl, urlMap, set);	
	}
	
	public ConcurrentMap<String, String> getHeaderLinksFromDownloadPage(Document doc, String rootUrl, Map<String, String> urlMap) {
		ConcurrentMap<String, String> cMap = new ConcurrentHashMap<String, String>();
		if(urlMap.get(Constants.LINK_FINDER)!= null && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains("searchbyattribute")){
			Pattern pattern = Pattern.compile("(\\\"(.*?)\\\")");
			String text = urlMap.get(Constants.LINK_FINDER);
			Matcher matcher = pattern.matcher(text);
			String[] array = new String[3];
			int count=0;
			while(matcher.find()) {
				String matchStr = matcher.group(0).substring(1, matcher.group(0).length()-1);
				array[count]=matchStr;
				System.out.println(array[count]);
				count++;
			}
			
			int lastIndex = urlMap.get(Constants.LINK_FINDER).lastIndexOf("(");
			String attr = urlMap.get(Constants.LINK_FINDER).substring(17, lastIndex);
			//String query = urlMap.get(Constants.LINK_FINDER).replaceAll("("+Constants.SEARCH_BY_ATTRIBUTE+attr+"|\\(|\\))", "");
			if(doc.select(array[0]).size() > 0) {
				Elements links = doc.select(array[0]);
				for(Element link : links){
					String urlText = link.attr(attr);
					cMap.put(array[1]+urlText, "");
					System.out.println(array[1]+urlText);
				}
			}
			/*int lastIndex = urlMap.get(Constants.LINK_FINDER).lastIndexOf("(");
			String attr = urlMap.get(Constants.LINK_FINDER).substring(17, lastIndex);
			String query = urlMap.get(Constants.LINK_FINDER).replaceAll("("+Constants.SEARCH_BY_ATTRIBUTE+attr+"|\\(|\\))", "");
			if(doc.select(query).size() > 0) {
				Elements links = doc.select(query);
				for(Element link : links){
					String urlText = link.attr(attr);
					if(urlText.contains(urlMap.get(Constants.ROOT_URL))){
						cMap.put(urlText, "");
						System.out.println(urlText);
					} else {
						cMap.put(urlMap.get(Constants.ROOT_URL)+urlText, "");
						System.out.println(urlMap.get(Constants.ROOT_URL)+urlText);
					}
				}
			}*/
		} else if (urlMap.get(Constants.LINK_FINDER)!= null && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains("onclick")) {
			cMap = getOnclickUrls(doc, urlMap);
		} else if (urlMap.get(Constants.LINK_FINDER)!= null && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains("linkbytext")){
			String text = urlMap.get(Constants.LINK_FINDER).replaceAll("("+Constants.LINK_BY_TEXT+"|\\(|\\))", "");
			Elements aLinks = doc.getElementsByTag("a");
			for(Element link : aLinks){
				if(link.text().contains(text)){
					cMap.put(link.attr("abs:href"), "");
				} 
			}			
		} else if (urlMap.get(Constants.LINK_FINDER)!= null && doc.select(urlMap.get(Constants.LINK_FINDER)).size() > 0) {
			Elements links = doc.select(urlMap.get(Constants.LINK_FINDER));
			for(Element link : links){
				cMap.put(link.attr("abs:href"), "");
			}
		} else  {
			Elements links = doc.select("a[href]");
			for(Element link : links){
				if(link.attr("abs:href").toLowerCase().contains(rootUrl.toLowerCase())) {
					cMap.put(link.attr("abs:href"), "");
				}
			}
		}
		return cMap;
	}
	
	public ConcurrentMap<String, String> getOnclickUrls(Document doc, Map<String, String> urlMap) {
		ConcurrentMap<String, String> cMap = new ConcurrentHashMap<String, String>();
		try {
			Pattern pattern = Pattern.compile("(\\\"(.*?)\\\")");
			Matcher matcher = pattern.matcher(urlMap.get(Constants.LINK_FINDER));
			String[] array = new String[3];
			int count=0;
			while(matcher.find()) {
				String matchStr = matcher.group(0).substring(1, matcher.group(0).length()-1);
				array[count]=matchStr;
				count++;
			}
			String onclickText = urlMap.get(Constants.LINK_FINDER).trim().substring(0, 7);
			Elements links = doc.getElementsByAttribute(onclickText);
			for(Element link : links){
				if(link.attr(onclickText).contains(array[0]) && link.attr(onclickText).contains(array[2])) {
					Matcher match = Pattern.compile(Pattern.quote(array[0]) + "(.*?)"+ Pattern.quote(array[2])).matcher(link.attr(onclickText));
					/*while(match.find()){
						String[] strArray = match.group(0).split(",");
						if(array[1].equals("null")) {
							//System.out.println(urlMap.get(Constants.ROOT_URL)+strArray[1].replace("'", "").trim());
							cMap.put(urlMap.get(Constants.ROOT_URL)+strArray[1].replace("'", "").trim(), "");
						} else {
							//System.out.println(urlMap.get(Constants.ROOT_URL)+array[1]+strArray[1].replace("'", "").trim());
							cMap.put(urlMap.get(Constants.ROOT_URL)+array[1]+strArray[1].replace("'", "").trim(), "");
						}
					}*/
					while(match.find()){
						//System.out.println(match.group(0));
						String str = match.group(0).replace(array[0], "").replace(array[2], "").trim();
						System.out.println(str);
						if(str.toLowerCase().contains(urlMap.get(Constants.ROOT_URL))) {
							if(array[1].equals("null")) {
								cMap.put(str, "");
							} else {
								cMap.put(array[1]+str, "");
							}
						} else {
							if(array[1].equals("null")) {
								cMap.put(urlMap.get(Constants.ROOT_URL)+str, "");
							} else {
								cMap.put(urlMap.get(Constants.ROOT_URL)+array[1]+str, "");
							}
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cMap;		
	}
	
	public String downloadPage(Map<String, String> urlMap) {
		String pageName = null;
		try {
			WebDriver driver;
			/*File file = new File("C:/Users/inndkrj/Downloads/chromedriver_win32/chromedriver.exe");
			System.setProperty("webdriver.chrome.driver", file.getAbsolutePath());*/
			String downloadPath = System.getProperty("user.home");
			FirefoxProfile profile = new FirefoxProfile();
			profile.setAcceptUntrustedCertificates(true);
			profile.setPreference("browser.download.folderList", 2);
			profile.setPreference("browser.download.dir", downloadPath+"\\");
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk","text/csv,application/x-msexcel,application/excel,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml");
			profile.setPreference("network.http.connection-timeout", 10);
			profile.setPreference("network.http.connection-retry-timeout", 10);
			driver = new FirefoxDriver(profile);
			driver.navigate().to(urlMap.get(Constants.URL));
			Thread.sleep(15000);
			WebDriverWait wait = new WebDriverWait(driver, 60);
			WebElement we = driver.findElement(By.tagName("body"));
			//System.out.println(driver.getTitle());
			//pageName = driver.getTitle().replace("|", "_").replace("<br />", "(br _)").replace(".", "");
			String title = driver.getTitle().replace("|", "_").replace("<br />", "(br _)");
			int lastIndex = title.lastIndexOf(".");
			if(lastIndex > 0) {
				pageName = title.replace(String.valueOf(title.charAt(lastIndex)), "");
			} else {
				pageName = title;
			}
			//System.out.println(pageName);
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			for(int i=0; i<3; i++) {
				Thread.sleep(2000);
				jse.executeScript("scroll(0, 3000);");
			}			
			((JavascriptExecutor) driver).executeScript("return window.stop");
			Thread.sleep(2000);
			Actions action = new Actions(driver);
			action.moveToElement(we);
			action.contextClick(we).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ENTER).click().build().perform();
			Thread.sleep(1000);
			Robot object = new Robot();
			object.keyPress(KeyEvent.VK_ENTER);
			object.keyRelease(KeyEvent.VK_ENTER);
			Thread.sleep(8000);
			driver.quit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return pageName;
	}
	
	public void doWebScrappingFromInventoryPage(String url, String rootUrl, String manufacCodesPath, Map<String, String> urlMap, Set<Vehicle> set, Map<String, String> blockDataTextsMap){
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
	
		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
			Set<String> allValidUrls = new HashSet<String>();
			if(urlMap.get(Constants.URL).contains(Constants.INVENTORY_URLS)) {
				allValidUrls = getAllInventoryUrls(urlMap);
			} else {
				allValidUrls.add(url);
			}
			
			System.out.println("All valid urls: " + allValidUrls.size());
			System.out.println("Now sending each inventory page for parsing. Please wait...");
			Iterator<String> iterator = allValidUrls.iterator();
			InventoryPageParser inventoryParser = new InventoryPageParser();
			List<Vehicle> list = new ArrayList<Vehicle>();
			while(iterator.hasNext()) {
				String urlStr = iterator.next();				
				list = inventoryParser.getVehicleDatas(urlStr, urlMap, makeArray);
					set.addAll(list);
			}
			
			ICSVFileWriter csvWriter = new CSVFileWriter();
			String truncatedDomainName = rootUrl.replaceAll("(http://www.|https://www.|http://|https://|/)", "");
			String currentTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			String pubCode ="";
			if(urlMap.get(Constants.PUB_CODE)!= null && !urlMap.get(Constants.PUB_CODE).isEmpty()){
				pubCode = urlMap.get(Constants.PUB_CODE);
			}
			String fileName = System.getProperty("user.home")+"/mainfiles/"+pubCode+truncatedDomainName+currentTime+".csv";
			String garbageFileName = System.getProperty("user.home")+"/garbage/"+"GARBAGE"+pubCode+truncatedDomainName+currentTime+".csv";
			csvWriter.writeCSVFileInventoryPage(fileName, garbageFileName, set, urlMap);
	}
	
	public void doWebScrappingFromJSPage(String url, String rootUrl, String manufacCodesPath, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap){
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
		CountDownLatch latch2 = null;
		String[] makeArray = new String[manufacCodesList.size()] ;
		makeArray =  manufacCodesList.toArray(makeArray);
		
		
		ConcurrentMap<String, String> headerLinks = getLinksUsingSelenium(url, urlMap);
		int size = headerLinks.keySet().size();
		latch2 = new CountDownLatch(size);
		System.out.println("Total links found: " + size);
		System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		Iterator<String> itr = headerLinks.keySet().iterator();
		while(itr.hasNext()) {
			ParserFactory factory = new ParserFactory();
			Parser parser = factory.createParser(urlMap.get(Constants.GROUP));
			parser.setUrl(itr.next());
			parser.setLatch(latch2);
			parser.setMakeArray(makeArray);
			parser.setUrlMap(urlMap);
			parser.setUrlMapVehicleFeatures(blockDataTextsMap);
			Callable<Vehicle> callable = parser;				
			Future<Vehicle> future = executor.submit(callable);
			set.add(future);				
		} 		
		try {
			latch2.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		writeFile(rootUrl, urlMap, set);	
	}
	
	public void doWebScrappingFromInventoryAndDownloadPage(String url, String rootUrl, Map<String, String> urlMap, Set<Vehicle> set, Map<String, String> blockDataTextsMap){
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
		String pageName = null;
		if(!urlMap.get(Constants.URL).contains("http") && !urlMap.get(Constants.URL).contains("inventory")) {
			pageName = urlMap.get(Constants.URL);
		} else {
			pageName = downloadPage(urlMap);
		}
		//String pageName = downloadPage(urlMap);
		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
		
		InventoryPageParser inventoryParser = new InventoryPageParser();
		List<Vehicle> list = inventoryParser.getVehicleDatas(pageName, urlMap, makeArray);
		set.addAll(list);
		ICSVFileWriter csvWriter = new CSVFileWriter();
		String truncatedDomainName = rootUrl.replaceAll("(http://www.|https://www.|http://|https://|/)", "");
		String currentTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		String pubCode ="";
		if(urlMap.get(Constants.PUB_CODE)!= null && !urlMap.get(Constants.PUB_CODE).isEmpty()){
			pubCode = urlMap.get(Constants.PUB_CODE);
		}
		String fileName = System.getProperty("user.home")+"/mainfiles/"+pubCode+truncatedDomainName+currentTime+".csv";
		String garbageFileName = System.getProperty("user.home")+"/garbage/"+"GARBAGE"+pubCode+truncatedDomainName+currentTime+".csv";
		csvWriter.writeCSVFileInventoryPage(fileName, garbageFileName, set, urlMap);
	}
	
	public ConcurrentMap<String, String> getLinksUsingSelenium(String url, Map<String, String> urlMap) {
		ConcurrentMap<String, String> cMap = new ConcurrentHashMap<String, String>();
		WebDriverUtil webDriverUtil = new WebDriverUtil();
		WebDriver driver = webDriverUtil.getFirefoxDriver();
		try {	
	        driver.get(url);			
			List<WebElement> list = driver.findElements(By.linkText(urlMap.get(Constants.LINK_FINDER)));			
			for(WebElement e : list){
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", e);
				String link = e.getAttribute("href");
				cMap.put(link, "");
			}
			driver.quit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cMap;		
	}
	
	//Web scrapping will start for each url passed
	public void doWebScrappingFromSeleniumDrivers(String url, String rootUrl, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap){
		
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
		CountDownLatch latch1 = null;
		CountDownLatch latch2 = null;
		WebDriverUtil webDriverUtil = new WebDriverUtil();
		WebDriver driver = webDriverUtil.getFirefoxDriver();
		Set<String> allValidUrls;
		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
		if(urlMap.get(Constants.URL).contains(Constants.INVENTORY_URLS)) {
			allValidUrls = getAllInventoryUrlsUsingSelenium(urlMap);
		} else {
			allValidUrls = getAllUrlsUsingSelenium(driver, rootUrl, urlMap);
			allValidUrls.add(url);
		}
			//allValidUrls.add(url);
			System.out.println("All valid urls: " + allValidUrls.size());
			System.out.println("Now sending urls to multiple threads to parse all urls for header links. Please wait...");
			latch1 = new CountDownLatch(allValidUrls.size());
			ExecutorService eService = Executors.newFixedThreadPool(10);
			Iterator<String> iterator = allValidUrls.iterator();
			while(iterator.hasNext()) {
				String urlStr = iterator.next();
				Runnable worker = new RunnableServiceJS(urlStr, latch1, makeArray, urlMap);
				if(Constants.THREAD_DELAY.equals("True")) {
					try {
						eService.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				eService.execute(worker);
			}
			eService.shutdown();
			try {
				latch1.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			ConcurrentMap<String, String> headerLinks = RunnableServiceJS.getHeaderLinksJS();
			int size = headerLinks.keySet().size();
			latch2 = new CountDownLatch(size);
			System.out.println("Total links found: " + size);
			System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
			
			ExecutorService executor = Executors.newFixedThreadPool(10);
			Iterator<String> itr = headerLinks.keySet().iterator();
			while(itr.hasNext()) {
				ParserFactory factory = new ParserFactory();
				ParserJS parser = factory.createJSParser(urlMap.get(Constants.GROUP));
				parser.setUrl(itr.next());
				parser.setLatch(latch2);
				parser.setMakeArray(makeArray);
				parser.setUrlMap(urlMap);
				parser.setUrlMapVehicleFeatures(blockDataTextsMap);
				Callable<Vehicle> callable = parser;				
				Future<Vehicle> future = executor.submit(callable);
				set.add(future);
				if(Constants.THREAD_DELAY.equals("True")) {
					try {
						executor.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} 		
			try {
				latch2.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			executor.shutdown();
			writeFile(rootUrl, urlMap, set);
			driver.quit();
		
	}
	
	public void doWebScrappingFromSeleniumDriversAndDownloadPage (String url, String rootUrl, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap) {
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
		CountDownLatch latch1 = null;
		CountDownLatch latch2 = null;
		Document doc = null;
		File input1 = null;
		File input2 = null;
		String pageName = null;
		String downloadedFilePathHTML = null;
		String downloadedFilePathHTM = null;
		if(!urlMap.get(Constants.URL).contains("http")) {
			downloadedFilePathHTML = System.getProperty("user.home") + "\\"+urlMap.get(Constants.URL)+".html";
			downloadedFilePathHTM = System.getProperty("user.home") + "\\"+urlMap.get(Constants.URL)+".htm";
		} else {
			pageName = downloadPage(urlMap);
			downloadedFilePathHTML = System.getProperty("user.home") + "\\"+pageName+".html";
			downloadedFilePathHTM = System.getProperty("user.home") + "\\"+pageName+".htm";
		}
		
		/*String downloadedFilePathHTML = System.getProperty("user.home") + "\\"+"a.html";
		String downloadedFilePathHTM = System.getProperty("user.home") + "\\"+"a.htm";*/
		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
		try {
			input1 = new File(downloadedFilePathHTML);
			if(input1.exists()) {
				doc = Jsoup.parse(input1, "UTF-8", rootUrl+"/");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			input2 = new File(downloadedFilePathHTM);
			if(input2.exists()) {
				doc = Jsoup.parse(input2, "UTF-8", rootUrl+"/");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		ConcurrentMap<String, String> headerLinks = getHeaderLinksFromDownloadPage(doc, rootUrl, urlMap);
		int size = headerLinks.keySet().size();
		latch2 = new CountDownLatch(2);
		System.out.println("Total links found: " + size);
		System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Iterator<String> itr = headerLinks.keySet().iterator();
		int j = 0;
		while(itr.hasNext() &&  j <2) {
			ParserFactory factory = new ParserFactory();
			ParserJS parser = factory.createJSParser(urlMap.get(Constants.GROUP));
			parser.setUrl(itr.next());
			parser.setLatch(latch2);
			parser.setMakeArray(makeArray);
			parser.setUrlMap(urlMap);
			parser.setUrlMapVehicleFeatures(blockDataTextsMap);
			Callable<Vehicle> callable = parser;				
			Future<Vehicle> future = executor.submit(callable);
			set.add(future);
			if(Constants.THREAD_DELAY.equals("True")) {
				try {
					executor.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			j++;
		} 		
		try {
			latch2.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		writeFile(rootUrl, urlMap, set);
		
	}
	
	//To get all urls from parent url
	public Set<String> getAllUrlsUsingSelenium(WebDriver driver, String rootUrl, Map<String, String> urlMap){
		Set<String> urlSet = new HashSet<String>();
		List<WebElement> links = driver.findElements(By.cssSelector("a"));
		for(WebElement link : links){
			if(link.getAttribute("href").toLowerCase().contains(rootUrl.toLowerCase())) {
				urlSet.add(link.getAttribute("href"));
			}
		}
		return urlSet;
	}
	
	//To get all inventory urls 
	public Set<String> getAllInventoryUrlsUsingSelenium(Map<String, String> urlMap){
		Set<String> urlSet = new HashSet<String>();
		String[] inventoryUrlArr;
		String urlStr = urlMap.get(Constants.URL).replaceAll("("+Constants.INVENTORY_URLS+"|\\(|\\))", "");
		if(urlStr.contains("^")) {
			inventoryUrlArr = urlStr.split("\\^");
			for(String str: inventoryUrlArr){
				urlSet.add(str.trim());
			}
		} else {
			urlSet.add(urlStr.trim());
		}
		
		return urlSet;
	}
	
	public void doWebScrappingFromWIXPage(String url, String rootUrl, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap){
		
		/*SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();*/
		CountDownLatch latch1 = null;
		CountDownLatch latch2 = null;
		Document doc = null;
		Set<String> allValidUrls;
		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
		if(urlMap.get(Constants.URL).contains(Constants.INVENTORY_URLS)) {
			allValidUrls = getAllInventoryUrls(urlMap);
			} else {
				try {	
				Connection con = Jsoup.connect(url)
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
						.referrer("http://www.google.com")
						.ignoreContentType(true)
						.followRedirects(true)
						.timeout(60*1000);

				Connection.Response res = con.execute();

					if(res.statusCode() == 200) {
					doc = con.get();
					}
				} catch (IOException e){
					e.printStackTrace();
				}
			allValidUrls = getAllUrls(doc, rootUrl, urlMap);
			allValidUrls.add(url);
		}
			System.out.println("All valid urls: " + allValidUrls.size());
			System.out.println("Now sending urls to multiple threads to parse all urls for header links. Please wait...");
			latch1 = new CountDownLatch(allValidUrls.size());
			ExecutorService eService = Executors.newFixedThreadPool(10);
			Iterator<String> iterator = allValidUrls.iterator();
			while(iterator.hasNext()) {
				String urlStr = iterator.next();
				Runnable worker = new RunnableServiceWix(urlStr, latch1, makeArray, urlMap);
				if(Constants.THREAD_DELAY.equals("True")) {
					try {
						eService.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				eService.execute(worker);
			}
			eService.shutdown();
			try {
				latch1.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			ConcurrentMap<String, String> headerLinks = RunnableServiceWix.getHeaderLinks();
			int size = headerLinks.keySet().size();
			latch2 = new CountDownLatch(size);
			System.out.println("Total links found: " + size);
			System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
			ExecutorService executor = Executors.newFixedThreadPool(10);
			Iterator<String> itr = headerLinks.keySet().iterator();
			while(itr.hasNext()) {
				ParserFactory factory = new ParserFactory();
				Parser parser = factory.createParser(urlMap.get(Constants.GROUP));
				parser.setUrl(itr.next());
				parser.setLatch(latch2);
				parser.setMakeArray(makeArray);
				parser.setUrlMap(urlMap);
				parser.setUrlMapVehicleFeatures(blockDataTextsMap);
				/*if(driver != null)
					parser.setSeleniumDriver(driver);*/
				if(Constants.SELENIUM_REQUIRED.equals("Yes")) {
					WebDriverUtil webDriverUtil = new WebDriverUtil();
					WebDriver driver = webDriverUtil.getFirefoxDriver();
					parser.setSeleniumDriver(driver);
				}
				Callable<Vehicle> callable = parser;				
				Future<Vehicle> future = executor.submit(callable);
				set.add(future);
				if(Constants.THREAD_DELAY.equals("True")) {
					try {
						executor.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} 		
			try {
				latch2.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			executor.shutdown();
			writeFile(rootUrl, urlMap, set);
	}
	
	public void doWebScrappingFromJsonInventoryPage(String url, String rootUrl, Map<String, String> urlMap, Set<Vehicle> set, Map<String, String> blockDataTextsMap){
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
	
		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
			Set<String> allValidUrls = new HashSet<String>();
			if(urlMap.get(Constants.URL).contains(Constants.INVENTORY_URLS)) {
				allValidUrls = getAllInventoryUrls(urlMap);
			} else {
				allValidUrls.add(url);
			}
			
			System.out.println("All valid urls: " + allValidUrls.size());
			System.out.println("Now sending each inventory page for parsing. Please wait...");
			Iterator<String> iterator = allValidUrls.iterator();
			JsonInventoryPageParser jsonInventoryParser = new JsonInventoryPageParser();
			List<Vehicle> list = new ArrayList<Vehicle>();
			while(iterator.hasNext()) {
				String urlStr = iterator.next();				
				list = jsonInventoryParser.getVehicleDatas(urlStr, urlMap, makeArray);
					set.addAll(list);
			}
			
			ICSVFileWriter csvWriter = new CSVFileWriter();
			String truncatedDomainName = rootUrl.replaceAll("(http://www.|https://www.|http://|https://|/)", "");
			String currentTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			String pubCode ="";
			if(urlMap.get(Constants.PUB_CODE)!= null && !urlMap.get(Constants.PUB_CODE).isEmpty()){
				pubCode = urlMap.get(Constants.PUB_CODE);
			}
			String fileName = System.getProperty("user.home")+"/mainfiles/"+pubCode+truncatedDomainName+currentTime+".csv";
			String garbageFileName = System.getProperty("user.home")+"/garbage/"+"GARBAGE"+pubCode+truncatedDomainName+currentTime+".csv";
			csvWriter.writeCSVFileInventoryPage(fileName, garbageFileName, set, urlMap);
	}
	
	//Web scrapping will start for Json Source
	public void doWebScrappingFromJsonSource(String url, String rootUrl, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap){
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
		CountDownLatch latch1 = null;
		CountDownLatch latch2 = null;
		WebDriverUtil webDriverUtil = new WebDriverUtil();
		WebDriver driver = webDriverUtil.getFirefoxDriver();
		String downloadedFileJson = System.getProperty("user.home") + "\\"+urlMap.get(Constants.URL)+".json";
		String[] makeArray = new String[manufacCodesList.size()];
		List<Nodes> nodeList = null;
		try {            
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            nodeList = objectMapper.readValue(new File (downloadedFileJson), new TypeReference<List<Nodes>>(){});
            
		}  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		makeArray =  manufacCodesList.toArray(makeArray);
		ConcurrentMap<String, String> headerLinks = new ConcurrentHashMap<String, String>();
		for(int i =0; i < nodeList.size(); i++) {
			String make = nodeList.get(i).getMake().getName().toLowerCase();
			String model = nodeList.get(i).getModel().getName().toLowerCase();
			Integer year = nodeList.get(i).getYear();
			String endPoint = year+"-"+make+"-"+model+"-id"+nodeList.get(i).getId();
			headerLinks.put(urlMap.get(Constants.ROOT_URL)+"/"+make+"/"+model+"/"+year+"/"+endPoint, "");
			//System.out.println(headerLinks);
		}
		
		//ConcurrentMap<String, String> headerLinks = getHeaderLinksFromDownloadPage(doc, rootUrl, urlMap);
		int size = headerLinks.keySet().size();
		latch2 = new CountDownLatch(size);
		System.out.println("Total links found: " + size);
		System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		Iterator<String> itr = headerLinks.keySet().iterator();
		while(itr.hasNext()) {
			ParserFactory factory = new ParserFactory();
			ParserJS parser = factory.createJSParser(urlMap.get(Constants.GROUP));
			parser.setUrl(itr.next());
			parser.setLatch(latch2);
			parser.setMakeArray(makeArray);
			parser.setUrlMap(urlMap);
			parser.setUrlMapVehicleFeatures(blockDataTextsMap);
			Callable<Vehicle> callable = parser;				
			Future<Vehicle> future = executor.submit(callable);
			set.add(future);
			if(Constants.THREAD_DELAY.equals("True")) {
				try {
					executor.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} 		
		try {
			latch2.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		writeFile(rootUrl, urlMap, set);
		driver.quit();	
	}
	
	public void doWebScrappingFromG4LinkJsonAd(String url, String rootUrl, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap){
		
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
		CountDownLatch latch1 = null;
		CountDownLatch latch2 = null;
		WebDriverUtil webDriverUtil = new WebDriverUtil();
		WebDriver driver = webDriverUtil.getFirefoxDriver();
		Set<String> allValidUrls;
		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
		if(urlMap.get(Constants.URL).contains(Constants.INVENTORY_URLS)) {
			allValidUrls = getAllInventoryUrlsUsingSelenium(urlMap);
		} else {
			allValidUrls = getAllUrlsUsingSelenium(driver, rootUrl, urlMap);
			allValidUrls.add(url);
		}
			//allValidUrls.add(url);
			System.out.println("All valid urls: " + allValidUrls.size());
			System.out.println("Now sending urls to multiple threads to parse all urls for header links. Please wait...");
			latch1 = new CountDownLatch(allValidUrls.size());
			ExecutorService eService = Executors.newFixedThreadPool(10);
			Iterator<String> iterator = allValidUrls.iterator();
			while(iterator.hasNext()) {
				String urlStr = iterator.next();
				Runnable worker = new RunnableServiceJS(urlStr, latch1, makeArray, urlMap);
				if(Constants.THREAD_DELAY.equals("True")) {
					try {
						eService.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				eService.execute(worker);
			}
			eService.shutdown();
			try {
				latch1.await();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			ConcurrentMap<String, String> headerLinks = RunnableServiceJS.getHeaderLinksJS();
			int size = headerLinks.keySet().size();
			latch2 = new CountDownLatch(size);
			System.out.println("Total links found: " + size);
			System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
			
			ExecutorService executor = Executors.newFixedThreadPool(10);
			Iterator<String> itr = headerLinks.keySet().iterator();
			while(itr.hasNext()) {
				ParserFactory factory = new ParserFactory();
				ParserJson parser = factory.createJsonParser(urlMap.get(Constants.GROUP));
				parser.setUrl(itr.next());
				parser.setLatch(latch2);
				parser.setMakeArray(makeArray);
				parser.setUrlMap(urlMap);
				parser.setUrlMapVehicleFeatures(blockDataTextsMap);
				Callable<Vehicle> callable = parser;				
				Future<Vehicle> future = executor.submit(callable);
				set.add(future);
				if(Constants.THREAD_DELAY.equals("True")) {
					try {
						executor.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} 		
			try {
				latch2.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			executor.shutdown();
			writeFile(rootUrl, urlMap, set);
			driver.quit();
		
	}
	
	public void doWebScrappingFromJsonInventoryJsonAd(String url, String rootUrl, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap){
		
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();

		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
		CountDownLatch latchForLinks = new CountDownLatch(1);
		ExecutorService eService = Executors.newFixedThreadPool(1);
		Runnable worker = new RunnableServiceJsonInventory(latchForLinks, makeArray, urlMap);
		eService.execute(worker);
		try {
			latchForLinks.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ConcurrentMap<String, String> headerLinks = RunnableServiceJsonInventory.getHeaderLinks();		
		int size = headerLinks.keySet().size();
		CountDownLatch latchForDetailPage = new CountDownLatch(size);
		System.out.println("Total links found: " + size);
		System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
		
		eService.shutdown();
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		Iterator<String> itr = headerLinks.keySet().iterator();
		while(itr.hasNext()) {
			ParserFactory factory = new ParserFactory();
			ParserJson parser = factory.createJsonParser(urlMap.get(Constants.GROUP));
			parser.setUrl(itr.next());
			parser.setLatch(latchForDetailPage);
			parser.setMakeArray(makeArray);
			parser.setUrlMap(urlMap);
			parser.setUrlMapVehicleFeatures(blockDataTextsMap);
			Callable<Vehicle> callable = parser;				
			Future<Vehicle> future = executor.submit(callable);
			set.add(future);
			if(Constants.THREAD_DELAY.equals("True")) {
				try {
					executor.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} 
		try {
			latchForDetailPage.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		executor.shutdown();
		writeFile(rootUrl, urlMap, set);
	}
	
	public void doWebScrappingFromJsonInventoryHtmlAd(String url, String rootUrl, Map<String, String> urlMap, Set<Future<Vehicle>> set, Map<String, String> blockDataTextsMap){
		
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();

		String[] makeArray = new String[manufacCodesList.size()];
		makeArray =  manufacCodesList.toArray(makeArray);
		CountDownLatch latchForLinks = new CountDownLatch(1);
		ExecutorService eService = Executors.newFixedThreadPool(1);
		Runnable worker = new RunnableServiceJsonInventory(latchForLinks, makeArray, urlMap);
		eService.execute(worker);
		try {
			latchForLinks.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ConcurrentMap<String, String> headerLinks = RunnableServiceJsonInventory.getHeaderLinks();		
		int size = headerLinks.keySet().size();
		CountDownLatch latchForDetailPage = new CountDownLatch(size);
		System.out.println("Total links found: " + size);
		System.out.println("Executor threads start for fetching datas from different urls. Waiting for result...");
		
		eService.shutdown();
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		Iterator<String> itr = headerLinks.keySet().iterator();
		while(itr.hasNext()) {
			ParserFactory factory = new ParserFactory();
			Parser parser = factory.createParser(urlMap.get(Constants.GROUP));
			parser.setUrl(itr.next());
			parser.setLatch(latchForDetailPage);
			parser.setMakeArray(makeArray);
			parser.setUrlMap(urlMap);
			parser.setUrlMapVehicleFeatures(blockDataTextsMap);
			Callable<Vehicle> callable = parser;				
			Future<Vehicle> future = executor.submit(callable);
			set.add(future);
			if(Constants.THREAD_DELAY.equals("True")) {
				try {
					executor.awaitTermination(Integer.parseInt(Constants.THREAD_DELAY_TIME), TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} 
		try {
			latchForDetailPage.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		executor.shutdown();
		writeFile(rootUrl, urlMap, set);
	}
	
	private void writeFile(String rootUrl, Map<String, String> urlMap, Set<Future<Vehicle>> set) {
		ICSVFileWriter csvWriter = new CSVFileWriter();
		String truncatedDomainName = rootUrl.replaceAll("(http://www.|https://www.|http://|https://|/)", "");
		String currentTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		String pubCode ="";
		if(urlMap.get(Constants.PUB_CODE)!= null && !urlMap.get(Constants.PUB_CODE).isEmpty()){
			pubCode = urlMap.get(Constants.PUB_CODE);
		}
		
		String fileName = System.getProperty("user.home")+"/mainfiles/"+pubCode+truncatedDomainName+currentTime+".csv";
		String garbageFileName = System.getProperty("user.home")+"/garbage/"+"GARBAGE"+pubCode+truncatedDomainName+currentTime+".csv";
		csvWriter.writeCSVFile(fileName, garbageFileName, set, urlMap);
		
	}
	
	private Map<String, String> checkWithTrainingData(Map<String, String> urlMap) {
		Set<String> allValidUrls;
		Document docT = null;
		Document doc = null;
		Document docForNewUrl = null;
		ExcelFileReader efr = new ExcelFileReader();
		ConcurrentMap<String, Map<String, String>> urlMapTrainingData = null;
		Map<String, String> urlMapModified = null;
		try {
			urlMapTrainingData = efr.urlsParamsMapTrainingData();			
			if(urlMap.get(Constants.URL).contains(Constants.INVENTORY_URLS)) {
				allValidUrls = getAllInventoryUrls(urlMap);	
				try {
					Connection con = Jsoup.connect(allValidUrls.iterator().next())
							.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
							.referrer("http://www.google.com")
							.ignoreContentType(true)
							.followRedirects(true)
							.timeout(60*1000);

					Connection.Response res = con.execute();

						if(res.statusCode() == 200) {
							docForNewUrl = con.get();
						}
					} catch (IOException e){
						e.printStackTrace();
				}
				for (String keyStr: urlMapTrainingData.keySet()) {
					Map<String, String> urlMapTraining = urlMapTrainingData.get(keyStr);

					try {					
						Connection con = Jsoup.connect(keyStr)
								.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
								.referrer("http://www.google.com")
								.ignoreContentType(true)
								.followRedirects(true)
								.timeout(60*1000);

						Connection.Response res = con.execute();

						if(res.statusCode() == 200)
							docT = con.get();
					} catch (IOException e){
					e.printStackTrace();
					}
					try {
						if(docT.select(urlMapTraining.get(Constants.LINK_FINDER)).size() > 5 && docForNewUrl.select(urlMapTraining.get(Constants.LINK_FINDER)).size() > 5) {
							System.out.println("Match found!!");
							urlMapModified = exchangeWithTrainingData(urlMap, urlMapTraining);
							System.out.println(urlMapModified);
							break;
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					
				}
			} else {
			try {
				Connection con = Jsoup.connect(urlMap.get(Constants.URL))
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
						.referrer("http://www.google.com")
						.ignoreContentType(true)
						.followRedirects(true)
						.timeout(60*1000);

				Connection.Response res = con.execute();

					if(res.statusCode() == 200) {
					doc = con.get();
					}
				} catch (IOException e){
					e.printStackTrace();
				}
			allValidUrls = getAllUrls(doc, urlMap.get(Constants.ROOT_URL), urlMap);
			allValidUrls.add(urlMap.get(Constants.URL));
			
			for (String keyStr: urlMapTrainingData.keySet()) {
				Map<String, String> urlMapTraining = urlMapTrainingData.get(keyStr);
				for(String  validUrl: allValidUrls) {
					try {
						Connection con = Jsoup.connect(validUrl)
								.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
								.referrer("http://www.google.com")
								.ignoreContentType(true)
								.followRedirects(true)
								.timeout(60*1000);

						Connection.Response res = con.execute();

							if(res.statusCode() == 200) {
								docForNewUrl = con.get();
							}
						} catch (IOException e){
							e.printStackTrace();
						}
					
					try {					
						Connection con = Jsoup.connect(keyStr)
								.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
								.referrer("http://www.google.com")
								.ignoreContentType(true)
								.followRedirects(true)
								.timeout(60*1000);

						Connection.Response res = con.execute();

						if(res.statusCode() == 200)
							docT = con.get();
					} catch (IOException e){
					e.printStackTrace();
					}
					try {
						if(docT.select(urlMapTraining.get(Constants.LINK_FINDER)).size() > 5 && docForNewUrl.select(urlMapTraining.get(Constants.LINK_FINDER)).size() > 5) {
							System.out.println("Match found!!");
							urlMapModified = exchangeWithTrainingData(urlMap, urlMapTraining);
							System.out.println(urlMapModified);
							return urlMapModified;
						}
					} catch(Exception e) {
						e.printStackTrace();
					}				
					
				}			
			}
			
		}
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return urlMapModified;
		
	}
	
	private Map<String, String> exchangeWithTrainingData(Map<String, String> urlMap, Map<String,String> urlMapTraining) {
		
		urlMap.put(Constants.GROUP, urlMapTraining.get(Constants.GROUP));
		if (urlMap.get(Constants.MANUFACTURER) == null || (urlMap.get(Constants.MANUFACTURER).isEmpty())) {
			urlMap.put(Constants.MANUFACTURER, urlMapTraining.get(Constants.MANUFACTURER));
		}
		if (urlMap.get(Constants.MODEL) == null || (urlMap.get(Constants.MODEL).isEmpty())) {
			urlMap.put(Constants.MODEL, urlMapTraining.get(Constants.MODEL));
		}
		if (urlMap.get(Constants.YEAR) == null || (urlMap.get(Constants.YEAR).isEmpty())) {
			urlMap.put(Constants.YEAR, urlMapTraining.get(Constants.YEAR));
		}
		if (urlMap.get(Constants.BODYSTYLE) == null || (urlMap.get(Constants.BODYSTYLE).isEmpty())) {
			urlMap.put(Constants.BODYSTYLE, urlMapTraining.get(Constants.BODYSTYLE));
		}
		if (urlMap.get(Constants.DOORS) == null || (urlMap.get(Constants.DOORS).isEmpty())) {
			urlMap.put(Constants.DOORS, urlMapTraining.get(Constants.DOORS));
		}
		if (urlMap.get(Constants.PASSENGERS) == null || (urlMap.get(Constants.PASSENGERS).isEmpty())) {
			urlMap.put(Constants.PASSENGERS, urlMapTraining.get(Constants.PASSENGERS));
		}
		if (urlMap.get(Constants.ENGINE) == null || (urlMap.get(Constants.ENGINE).isEmpty())) {
			urlMap.put(Constants.ENGINE, urlMapTraining.get(Constants.ENGINE));
		}
		if (urlMap.get(Constants.FUEL) == null || (urlMap.get(Constants.FUEL).isEmpty())) {
			urlMap.put(Constants.FUEL, urlMapTraining.get(Constants.FUEL));
		}
		if (urlMap.get(Constants.EXTERIOR_COLOR) == null || (urlMap.get(Constants.EXTERIOR_COLOR).isEmpty())) {
			urlMap.put(Constants.EXTERIOR_COLOR, urlMapTraining.get(Constants.EXTERIOR_COLOR));
		}
		if (urlMap.get(Constants.INTERIOR_COLOR) == null || (urlMap.get(Constants.INTERIOR_COLOR).isEmpty())) {
			urlMap.put(Constants.INTERIOR_COLOR, urlMapTraining.get(Constants.INTERIOR_COLOR));
		}
		if (urlMap.get(Constants.DRIVETRAIN) == null || (urlMap.get(Constants.DRIVETRAIN).isEmpty())) {
			urlMap.put(Constants.DRIVETRAIN, urlMapTraining.get(Constants.DRIVETRAIN));
		}
		if (urlMap.get(Constants.SALES_NUMBER) == null || (urlMap.get(Constants.SALES_NUMBER).isEmpty())) {
			urlMap.put(Constants.SALES_NUMBER, urlMapTraining.get(Constants.SALES_NUMBER));
		}
		if (urlMap.get(Constants.TRANSMISSION) == null || (urlMap.get(Constants.TRANSMISSION).isEmpty())) {
			urlMap.put(Constants.TRANSMISSION, urlMapTraining.get(Constants.TRANSMISSION));
		}
		if (urlMap.get(Constants.STOCK_NUM) == null || (urlMap.get(Constants.STOCK_NUM).isEmpty())) {
			urlMap.put(Constants.STOCK_NUM, urlMapTraining.get(Constants.STOCK_NUM));
		}
		if (urlMap.get(Constants.PRICE) == null || (urlMap.get(Constants.PRICE).isEmpty())) {
			urlMap.put(Constants.PRICE, urlMapTraining.get(Constants.PRICE));
		}
		if (urlMap.get(Constants.VIN) == null || (urlMap.get(Constants.VIN).isEmpty())) {
			urlMap.put(Constants.VIN, urlMapTraining.get(Constants.VIN));
		}
		if (urlMap.get(Constants.MILEAGE) == null || (urlMap.get(Constants.MILEAGE).isEmpty())) {
			urlMap.put(Constants.MILEAGE, urlMapTraining.get(Constants.MILEAGE));
		}
		if (urlMap.get(Constants.LOCATION) == null || (urlMap.get(Constants.LOCATION).isEmpty())) {
			urlMap.put(Constants.LOCATION, urlMapTraining.get(Constants.LOCATION));
		}
		if (urlMap.get(Constants.COUNTRY_CODE) == null || (urlMap.get(Constants.COUNTRY_CODE).isEmpty())) {
			urlMap.put(Constants.COUNTRY_CODE, urlMapTraining.get(Constants.COUNTRY_CODE));
		}
		if (urlMap.get(Constants.MODEL_CODE) == null || (urlMap.get(Constants.MODEL_CODE).isEmpty())) {
			urlMap.put(Constants.MODEL_CODE, urlMapTraining.get(Constants.MODEL_CODE));
		}
		if (urlMap.get(Constants.LINK_FINDER) == null || (urlMap.get(Constants.LINK_FINDER).isEmpty())) {
			urlMap.put(Constants.LINK_FINDER, urlMapTraining.get(Constants.LINK_FINDER));
		}
		if (urlMap.get(Constants.BLOCK_DATA_FINDER) == null || (urlMap.get(Constants.BLOCK_DATA_FINDER).isEmpty())) {
			urlMap.put(Constants.BLOCK_DATA_FINDER, urlMapTraining.get(Constants.BLOCK_DATA_FINDER));
		}
		if (urlMap.get(Constants.TRIM) == null || (urlMap.get(Constants.TRIM).isEmpty())) {
			urlMap.put(Constants.TRIM, urlMapTraining.get(Constants.TRIM));
		}
		if (urlMap.get(Constants.VEHICLE_FEATURES) == null || (urlMap.get(Constants.VEHICLE_FEATURES).isEmpty())) {
			urlMap.put(Constants.VEHICLE_FEATURES, urlMapTraining.get(Constants.VEHICLE_FEATURES));
		}
		if (urlMap.get(Constants.VEHICLE_DESC) == null || (urlMap.get(Constants.VEHICLE_DESC).isEmpty())) {
			urlMap.put(Constants.VEHICLE_DESC, urlMapTraining.get(Constants.VEHICLE_DESC));
		}
		if (urlMap.get(Constants.DOWNLOAD_PAGE) == null || (urlMap.get(Constants.DOWNLOAD_PAGE).isEmpty())) {
			urlMap.put(Constants.DOWNLOAD_PAGE, urlMapTraining.get(Constants.DOWNLOAD_PAGE));
		}
		if (urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL) == null || (urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL).isEmpty())) {
			urlMap.put(Constants.MAKE_MODEL_YEAR_DETAIL, urlMapTraining.get(Constants.MAKE_MODEL_YEAR_DETAIL));
		}
		if (urlMap.get(Constants.DEALER_FLAG) == null || (urlMap.get(Constants.DEALER_FLAG).isEmpty())) {
			urlMap.put(Constants.DEALER_FLAG, urlMapTraining.get(Constants.DEALER_FLAG));
		}
		if (urlMap.get(Constants.LOCALIZED_COUNTRY_CODE) == null || (urlMap.get(Constants.LOCALIZED_COUNTRY_CODE).isEmpty())) {
			urlMap.put(Constants.LOCALIZED_COUNTRY_CODE, urlMapTraining.get(Constants.LOCALIZED_COUNTRY_CODE));
		}
		if (urlMap.get(Constants.UNWANTED_BLOCK_DATA) == null || (urlMap.get(Constants.UNWANTED_BLOCK_DATA).isEmpty())) {
			urlMap.put(Constants.UNWANTED_BLOCK_DATA, urlMapTraining.get(Constants.UNWANTED_BLOCK_DATA));
		}
		
		return urlMap;
	}
	
	public void checkVehicleCount(int size, Map<String, String> urlMap,String url) {
		Document doc = null;
		if(urlMap.get(Constants.LISTING_DATE)!=null && !urlMap.get(Constants.LISTING_DATE).isEmpty()) {
			try {
				//doc = (Document) Jsoup.connect(url).timeout(20*1000).get();	
				Connection con = Jsoup.connect(url)
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
						.referrer("http://www.google.com")
						.ignoreContentType(true)
						.followRedirects(true)
						.timeout(60*1000);
				Connection.Response res = con.execute();

					if(res.statusCode() == 200) {
					doc = con.get();
					}
				} catch (IOException e){
					e.printStackTrace();
				}
			
			String count = doc.select(urlMap.get(Constants.LISTING_DATE)).text();
			Pattern p = Pattern.compile("\\b[\\d]+\\b");
			Matcher m = p.matcher(count);
			while(m.find()) {
				String c = m.group();
				if(c.trim().equalsIgnoreCase(""+size)) {
					message = "Count is correct = "+size;
				}else{
					message = "AdVoulume check "+c+" out of "+size;
				}
			}
		}
	}
	
	public String getCountMessage() {
		return message;
	}
}
