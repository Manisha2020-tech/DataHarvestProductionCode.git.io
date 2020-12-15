package com.audaharvest.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.audaharvest.common.CommonUtil;
import com.audaharvest.common.SSLTool;
import com.audaharvest.constants.Constants;
import com.audaharvest.json.model.Nodes;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class RunnableServiceJsonInventory implements Runnable {
	private static ConcurrentMap<String, String> headerLinks;
	private CountDownLatch latch;
	private String[] makeArray;
	private Map<String, String> urlMap;
	private Document doc;
	
	public RunnableServiceJsonInventory(CountDownLatch latch, String[] makeArray, Map<String, String> urlMap){
		this.latch = latch;
		this.makeArray = makeArray;
		this.headerLinks = new ConcurrentHashMap<String, String>();
		this.urlMap = urlMap;
	}
	@Override
	public void run() {	
		try {		
			
			if((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains(Constants.LINKS_BY_PATH_VARIABLE_IN_JSON_PAGE.toLowerCase())) {
				String downloadedFileJson = System.getProperty("user.home") + "\\"+urlMap.get(Constants.URL);
				// parsing file "JSONExample.json" 
				
				 Object obj = new JSONParser().parse(new FileReader(downloadedFileJson));
				 JSONObject jo = (JSONObject) obj;				  
				 JSONArray ja = (JSONArray) jo.get("");
				
				String newStr =  urlMap.get(Constants.LINK_FINDER).replace(Constants.LINKS_BY_PATH_VARIABLE_IN_JSON_PAGE+"(", "");
				String[] linkStrArr = newStr.split("\\^");
				String adUrls = null;
				//Get proper ad url format
				for (int i=0; i<linkStrArr.length; i++) {
					if(linkStrArr[i].contains("adUrls=")) {
						adUrls= linkStrArr[i].replaceAll("(adUrls=|\\))", "");	
					}
				}
				//System.out.println(adUrls);
				String[] paramsArray = new String[linkStrArr.length-1];
				Map<String, String> paramsMap = new HashMap<String, String>();
				//Get Parameter from config files
				for (int i=0; i<linkStrArr.length-1; i++) {
					int k = i+1;
					String param = linkStrArr[i].replaceAll("param"+k+"=", "");
					paramsArray[i]=param;
					paramsMap.put("param"+k, param);
					//System.out.println(param);
				}
				
				//Get the parameter's values from JSON object and build replace params with values in strOutput
				HashMap<String, String> paramsValueMap = new HashMap<String, String>();				
				for(int i=0; i<ja.size(); i++ ) {
					JSONObject objectInArray = (JSONObject) ja.get(i);
					String value = null;
					String strOutput = adUrls;
					for(Map.Entry<String, String> entry: paramsMap.entrySet()) {
						String param = entry.getKey().toString();
						String val = entry.getValue();
						value = objectInArray.get(val).toString();
						strOutput = strOutput.replace(param, value);
						paramsValueMap.put(param, value);
					}
					//System.out.println(strOutput);
					headerLinks.put(strOutput, "");
				}					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		latch.countDown();
		
	}
	
	public static ConcurrentMap<String, String> getHeaderLinks(){
		return headerLinks;
	}
	
	private String readJsonFile(String fileName) {
		String text = null;
		try(Stream<String> stream = Files.lines(Paths.get(fileName))) {
			text = stream.collect(Collectors.joining(" "));
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return text;
	}
	private String getViewSource(Document doc) {
		String pageSource = null;
			try {
				pageSource = doc.html();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return pageSource;
		
	}
	private String getViewSource(String pageUrl) {
		StringBuilder sb = null;
		try {
			CommonUtil cUtil = new CommonUtil();
			cUtil.sslCert();
			URL  strUrl = new URL(pageUrl);
	        sb = new StringBuilder();
			HttpsURLConnection ucon = (HttpsURLConnection) strUrl.openConnection();
			Thread.sleep(2000);
			BufferedReader br = new BufferedReader(new InputStreamReader(ucon.getInputStream(),"UTF-8"));
			Thread.sleep(2000);
			String line;
			
			while((line=br.readLine())!= null) {
				sb.append(line);
				sb.append("\n");
			}
			br.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
		
	}
}
