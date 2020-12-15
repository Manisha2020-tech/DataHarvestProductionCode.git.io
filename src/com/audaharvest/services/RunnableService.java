package com.audaharvest.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.audaharvest.common.CommonUtil;
import com.audaharvest.common.SSLTool;
import com.audaharvest.constants.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class RunnableService implements Runnable{
	private static ConcurrentMap<String, String> headerLinks;
	private String url;
	private CountDownLatch latch;
	private String[] makeArray;
	private Map<String, String> urlMap;
	private Document doc;
	
	public RunnableService(String url, CountDownLatch latch, String[] makeArray, Map<String, String> urlMap){
		this.url = url;
		this.latch = latch;
		this.makeArray = makeArray;
		this.headerLinks = new ConcurrentHashMap<String, String>();
		this.urlMap = urlMap;
	}
	@Override
	public void run() {	
		try {
			SSLTool sslTool = new SSLTool();
			sslTool.disableCertificateValidation();
			System.out.println(url);
			Connection con = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:25.0) Gecko/20100101 Firefox/41.0")
					.referrer("http://www.google.com")
					.ignoreContentType(true)
					.followRedirects(true)
					.timeout(180*1000);

			Connection.Response res = con.execute();
			
			if(res.statusCode() == 200) {
				doc = con.get();
			}
			
			if((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains(Constants.LINKS_BY_PATH_VARIABLE_IN_JSON_PAGE.toLowerCase())) {
				String newStr =  urlMap.get(Constants.LINK_FINDER).replace(Constants.LINKS_BY_PATH_VARIABLE_IN_JSON_PAGE+"(", "");
				String[] linkStrArr = newStr.split("\\^");
				String adUrls = linkStrArr[linkStrArr.length - 1].replaceAll("(adUrls=|\\))", "");
				int adlen = adUrls.length();
				
				String[] paramsArray = new String[linkStrArr.length-1];
				
				//Get Parameter from config files
				for (int i=0; i<linkStrArr.length-1; i++) {
					int k = i+1;
					String param = linkStrArr[i].replaceAll("param"+k+"=", "");
					paramsArray[i]=param;
					//System.out.println(param);
				}
				//Now get actual parameters to find urls
				String[] actualParams = new String[linkStrArr.length-1];
				int[] actualParamsHeight = new int[linkStrArr.length-1]; //Find height. e.g. results.hits.id height=2
				String[] actualParamsHeirarchy = null;
				for(int i=0; i < paramsArray.length; i++) {
					actualParamsHeirarchy = paramsArray[i].split("\\.");
					actualParams[i] = actualParamsHeirarchy[actualParamsHeirarchy.length-1];
					actualParamsHeight[i] = actualParamsHeirarchy.length - 1;
					//System.out.println(actualParams[i]);
					//System.out.println(actualParamsHeirarchy[i]);
				}
				
				//Index positions of parameters in urls
				int[] paramIndexArr = new int[linkStrArr.length-1];
				for(int o=1; o<=paramsArray.length; o++) {
					int index = adUrls.indexOf("param"+o);
					paramIndexArr[o-1] = index;
				}							
				/*JSONObject root = new JSONObject(docString);
				JSONObject obj = root.getJSONObject("results");
				JSONArray array = obj.getJSONArray("hits");				
				System.out.println(array.getJSONObject(0).get("id"));*/				
				ObjectMapper objectMapper = new ObjectMapper();
				//Putting in json tree like structure
				JsonNode jNode = objectMapper.readTree(doc.text());
				//System.out.println(jNode.get("results").get("hits").size());
				//System.out.println(jNode.get("results").get("hits").size());
				
				//Iterating over hierarchy to get list of car objects
				for(int i=0; i < actualParamsHeirarchy.length-1; i++) {
					jNode = jNode.get(actualParamsHeirarchy[i]);
				}
				//int size = jNode.get(actualParamsHeirarchy[0]).get(actualParamsHeirarchy[1]).size();
				
				int size = jNode.size();
				
				for (int i=0; i<size; i++) {
					try {
						StringBuilder sb = new StringBuilder();
						//String[] fieldValArray = new String[linkStrArr.length-2];
						/*for(int j=0; j < paramsArray.length; j++) {
							String val= doc.select(elmSelect.replace("i", String.valueOf(i)) +" "+ paramsArray[j]).get(0).text();
							fieldValArray[j] = val;
						}*/
						//Getting value from json data
						/*String value1 = jNode.get(actualParamsHeirarchy[0]).get(actualParamsHeirarchy[1]).get(i).get(actualParams[0]).asText();
						String value2 = jNode.get(actualParamsHeirarchy[0]).get(actualParamsHeirarchy[1]).get(i).get(actualParams[1]).asText();*/
						String value1 = jNode.get(i).get(actualParams[0]).asText();
						String value2 = jNode.get(i).get(actualParams[1]).asText();
						//int l = 0;
						//Re-building url with each character and replacing params with actual value
						/*for(int k=0; k < adlen ; k++ ) {
							if(k == paramIndexArr[l]) {
								k= k+6;
								sb.append(fieldValArray[l]);
								l = l+1;
							} else {
								sb.append(adUrls.charAt(k));
							}
						}*/
						String ad = adUrls.replace("param1", value1).replace("param2", value2);
						System.out.println(ad);
						headerLinks.put(ad, "");
						} catch (Exception e) {
							e.printStackTrace();
						}
					
				}
				
			} else if((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains(Constants.LINKS_BY_PATH_VARIABLE.toLowerCase())) {
				String newStr =  urlMap.get(Constants.LINK_FINDER).replace(Constants.LINKS_BY_PATH_VARIABLE+"(", "");
				String[] linkStrArr = newStr.split("\\^");
				String adUrls = linkStrArr[linkStrArr.length - 1].replaceAll("(adUrls=|\\))", "");
				int adlen = adUrls.length();
				String elmSelect = linkStrArr[0].replace("elems=", "");
				
				String[] paramsArray = new String[linkStrArr.length-2];
				
				//Get Parameter from config files
				for (int i=2, j=0; i<linkStrArr.length; i++, j++) {
					int k= i-1;
					String param = linkStrArr[i-1].replaceAll("param"+k+"=", "");
					paramsArray[j]=param;
				}
				//Index positions of parameters in urls
				int[] paramIndexArr = new int[linkStrArr.length-2];
				for(int o=1; o<=paramsArray.length; o++) {
					int index = adUrls.indexOf("param"+o);
					paramIndexArr[o-1] = index;
				}
									
				Elements elms = doc.select(elmSelect.replace(":eq(i)", ""));
				//Iterate over all elements to make urls
				for (int i=0; i< elms.size(); i++) {
					try {
					StringBuilder sb = new StringBuilder();
					String[] fieldValArray = new String[linkStrArr.length-2];
					for(int j=0; j < paramsArray.length; j++) {
						String val= doc.select(elmSelect.replace("i", String.valueOf(i)) +" "+ paramsArray[j]).get(0).text();
						fieldValArray[j] = val;
					}
					int l = 0;
					//Re-building url with each character and replacing params with actual value
					for(int k=0; k < adlen ; k++ ) {
						if(k == paramIndexArr[l]) {
							k= k+6;
							sb.append(fieldValArray[l]);
							l = l+1;
						} else {
							sb.append(adUrls.charAt(k));
						}
					}
					//System.out.println(sb.toString());
					headerLinks.put(sb.toString(), "");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}			
				
				
			} else if((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains(Constants.WIX_LINK_FROM_VIEW_SOURCE.toLowerCase())) {
				String viewSource = getViewSource(doc);
				int lastIndex = urlMap.get(Constants.LINK_FINDER).lastIndexOf(")");
				String newStr =  urlMap.get(Constants.LINK_FINDER).replace(String.valueOf(urlMap.get(Constants.LINK_FINDER).charAt(lastIndex)),"");
				String[] linkStrArr = newStr.replace(Constants.LINK_FROM_VIEW_SOURCE+"(", "").split("\\^");
				Matcher matcher = Pattern.compile(Pattern.quote(linkStrArr[0]) + "(.*?)"+ Pattern.quote(linkStrArr[2])).matcher(viewSource);
				ConcurrentHashMap<String, String> cMap = new ConcurrentHashMap<String, String>();
				while(matcher.find()) {
					String str = matcher.group(0).replaceAll(linkStrArr[0], "").replace(linkStrArr[2], "");
					if(str.toLowerCase().contains(urlMap.get(Constants.ROOT_URL))) {
						if(linkStrArr[1].equals("null")) {
							cMap.put(str, "");
							System.out.println(str);
						} else {
							cMap.put(linkStrArr[1]+str, "");
							System.out.println(linkStrArr[1]+str);
						}
					} else {
						if(linkStrArr[1].equals("null")) {
							cMap.put(urlMap.get(Constants.ROOT_URL)+str, "");
							System.out.println(urlMap.get(Constants.ROOT_URL)+str);
						} else {
							cMap.put(urlMap.get(Constants.ROOT_URL)+linkStrArr[1]+str, "");
							System.out.println(urlMap.get(Constants.ROOT_URL)+linkStrArr[1]+str);
						}
					}
				}
				CommonUtil cUtil = new CommonUtil();
				cUtil.sslCert();
				Document tempDoc = null;
				Iterator<String> itr = cMap.keySet().iterator();
				while (itr.hasNext()) {
					Connection conn = Jsoup.connect(itr.next())
							.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
							.referrer("http://www.google.com")
							.ignoreContentType(true)
							.followRedirects(true)
							.timeout(60*1000);

					Connection.Response response = conn.execute();

					if(response.statusCode() == 200) {
						tempDoc = con.get();
					}
					
					String href = tempDoc.select("[as=fetch]").get(1).attr("href");
					System.out.println(href);
					headerLinks.put(href, "");
				}
				
			} else if((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains(Constants.LINK_FROM_VIEW_SOURCE.toLowerCase())) {
				String viewSource = getViewSource(doc);
				int lastIndex = urlMap.get(Constants.LINK_FINDER).lastIndexOf(")");
				String newStr =  urlMap.get(Constants.LINK_FINDER).replace(String.valueOf(urlMap.get(Constants.LINK_FINDER).charAt(lastIndex)),"");
				String[] linkStrArr = newStr.replace(Constants.LINK_FROM_VIEW_SOURCE+"(", "").split("\\^");
				Matcher matcher = Pattern.compile(Pattern.quote(linkStrArr[0]) + "(.*?)"+ Pattern.quote(linkStrArr[2])).matcher(viewSource);
				while(matcher.find()) {
					String str = matcher.group(0).replaceAll(linkStrArr[0], "").replace(linkStrArr[2], "");
					if(str.toLowerCase().contains(urlMap.get(Constants.ROOT_URL))) {
						if(linkStrArr[1].equals("null")) {
							headerLinks.put(str, "");
							System.out.println(str);
						} else {
							headerLinks.put(linkStrArr[1]+str, "");
							System.out.println(linkStrArr[1]+str);
						}
					} else {
						if(linkStrArr[1].equals("null")) {
							headerLinks.put(urlMap.get(Constants.ROOT_URL)+str, "");
							System.out.println(urlMap.get(Constants.ROOT_URL)+str);
						} else {
							headerLinks.put(urlMap.get(Constants.ROOT_URL)+linkStrArr[1]+str, "");
							System.out.println(urlMap.get(Constants.ROOT_URL)+linkStrArr[1]+str);
						}
					}
				}
			} else if ((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains("searchbyattribute")) {				
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
						headerLinks.put(array[1]+urlText, "");
						System.out.println(array[1]+urlText);
					}
				}
				
			} else if (urlMap.get(Constants.LINK_FINDER)!= null && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains("linkbytext")){
				String text = urlMap.get(Constants.LINK_FINDER).replaceAll("("+Constants.LINK_BY_TEXT+"|\\(|\\))", "");
				Elements aLinks = doc.select("a[href]");
				for(Element link : aLinks){
					if(link.text().contains(text)){
						headerLinks.put(link.attr("abs:href"), "");
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
				Elements links = doc.getElementsByAttribute(onclickText);
				for(Element link : links){
					if(link.attr(onclickText).contains(array[0]) && link.attr(onclickText).contains(array[2])) {
						Matcher match = Pattern.compile(Pattern.quote(array[0]) + "(.*?)"+ Pattern.quote(array[2])).matcher(link.attr(onclickText));
						while(match.find()){
							//System.out.println(match.group(0));
							String str = match.group(0).replace(array[0], "").replace(array[2], "").trim();
							System.out.println(str);
							if(str.toLowerCase().contains(urlMap.get(Constants.ROOT_URL))) {
								if(array[1].equals("null")) {
									headerLinks.put(str, "");
								} else {
									headerLinks.put(array[1]+str, "");
								}
							} else {
								if(array[1].equals("null")) {
									headerLinks.put(urlMap.get(Constants.ROOT_URL)+str, "");
								} else {
									headerLinks.put(urlMap.get(Constants.ROOT_URL)+array[1]+str, "");
								}
							}
							
						}
					}
					
				}
				
				
			} else if ((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains("hrefjspopup")) {				
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
				String hrefText = urlMap.get(Constants.LINK_FINDER).trim().substring(0, 4);
				Elements links = doc.getElementsByAttribute(hrefText);
				for(Element link : links){
					if(link.attr(hrefText).contains(array[0]) && link.attr(hrefText).contains(array[2])) {
						Matcher match = Pattern.compile(Pattern.quote(array[0]) + "(.*?)"+ Pattern.quote(array[2])).matcher(link.attr(hrefText));
						while(match.find()){
							//System.out.println(match.group(0));
							String str = match.group(0).replace(array[0], "").replace(array[2], "").trim();
							if(str.toLowerCase().contains(urlMap.get(Constants.ROOT_URL))) {
								if(array[1].equals("null")) {
									headerLinks.put(str, "");
								} else {
									headerLinks.put(array[1]+str, "");
								}
							} else {
								if(array[1].equals("null")) {
									System.out.println(str);
									headerLinks.put(str, "");
								} else {
									System.out.println(array[1]+str);
									headerLinks.put(array[1]+str, "");
								}
							}
							
						}
					}
					
				}			
				
			} else if((urlMap.get(Constants.LINK_FINDER) != null) && !(urlMap.get(Constants.LINK_FINDER).isEmpty())) {
				Elements aLinks = doc.getElementsByTag("a");
				for(Element link : aLinks){
					if(link.text().contains(urlMap.get(Constants.LINK_FINDER))){
						headerLinks.put(link.attr("abs:href"), "");
					} 
				}
				try {
					if(doc.select(urlMap.get(Constants.LINK_FINDER)).size() > 0) {
						Elements links = doc.select(urlMap.get(Constants.LINK_FINDER));
						for(Element link : links){
							headerLinks.put(link.attr("abs:href"), "");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if(doc.getElementsByAttribute(urlMap.get(Constants.LINK_FINDER)).size() > 0) {
						Elements links = doc.getElementsByAttribute(urlMap.get(Constants.LINK_FINDER));
						for(Element link : links){
							String domain = urlMap.get(Constants.ROOT_URL);
							String urlText = link.attr(urlMap.get(Constants.LINK_FINDER));
							if(urlText.contains(urlMap.get(Constants.ROOT_URL))){
								headerLinks.put(urlText, "");
							} else {
								headerLinks.put(domain+urlText, "");
							} 
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			} else if ((urlMap.get(Constants.LOCALIZED_COUNTRY_CODE) != null) && (urlMap.get(Constants.LOCALIZED_COUNTRY_CODE).equals(Constants.FRANCE_CODE))) {				
				for(int i=0; i<makeArray.length;i++) {
					if((doc != null) && (doc.select("a:matches(^"+makeArray[i]+")").size() >0)) {
						Elements links = doc.select("a:matches(^"+makeArray[i]+")");
						for(Element link : links){
							String txt = link.text();
							Pattern p = Pattern.compile("(\\d{4})");
							Matcher m = p.matcher(txt);
							if(m.find()) {
								headerLinks.put(link.attr("abs:href"), "");
							}
						}
					} else if((doc != null) && (doc.select("a:matches("+makeArray[i]+")").size() >0)) {
						Elements links = doc.select("a:matches("+makeArray[i]+")");
						for(Element link : links){
							String txt = link.text();
							Pattern p = Pattern.compile("(\\d{4})");
							Matcher m = p.matcher(txt);
							if(m.find()) {
								headerLinks.put(link.attr("abs:href"), "");
							}
						}
					}
				}
				
			} else {
				for(int i=0; i<makeArray.length;i++) {
					if ((doc != null) && (doc.select("a:matches((^\\d{4})(\\s)("+makeArray[i]+"))").size() > 0)) {
						Elements links = doc.select("a:matches((^\\d{4})(\\s)("+makeArray[i]+"))");
						for(Element link : links){
							headerLinks.put(link.attr("abs:href"), "");
						}
					} else if((doc != null) && (doc.select("a:matches((\\d{4})(\\s)("+makeArray[i]+"))").size() >0)) {
						Elements links = doc.select("a:matches((\\d{4})(\\s)("+makeArray[i]+"))");
						for(Element link : links){
							headerLinks.put(link.attr("abs:href"), "");
						}
					}
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
