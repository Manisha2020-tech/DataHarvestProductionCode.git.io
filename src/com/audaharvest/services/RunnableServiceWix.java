package com.audaharvest.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Iterator;
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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.audaharvest.common.CommonUtil;
import com.audaharvest.constants.Constants;

public class RunnableServiceWix implements Runnable{
	private static ConcurrentMap<String, String> headerLinks;
	private String url;
	private CountDownLatch latch;
	private String[] makeArray;
	private Map<String, String> urlMap;

	
	public RunnableServiceWix(String url, CountDownLatch latch, String[] makeArray, Map<String, String> urlMap){
		this.url = url;
		this.latch = latch;
		this.makeArray = makeArray;
		this.headerLinks = new ConcurrentHashMap<String, String>();
		this.urlMap = urlMap;
	}
	@Override
	public void run() {	
		try {
			Document doc = null;
			CommonUtil cUtil = new CommonUtil();
			cUtil.sslCert();
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
			
			if((urlMap.get(Constants.LINK_FINDER) != null) && urlMap.get(Constants.LINK_FINDER).toLowerCase().contains(Constants.WIX_LINK_FROM_VIEW_SOURCE.toLowerCase())) {
				String viewSource = getViewSource(doc);
				int lastIndex = urlMap.get(Constants.LINK_FINDER).lastIndexOf(")");
				String newStr =  urlMap.get(Constants.LINK_FINDER).replace(String.valueOf(urlMap.get(Constants.LINK_FINDER).charAt(lastIndex)),"");
				String[] linkStrArr = newStr.replace(Constants.WIX_LINK_FROM_VIEW_SOURCE+"(", "").split("\\^");
				Matcher matcher = Pattern.compile(Pattern.quote(linkStrArr[0]) + "(.*?)"+ Pattern.quote(linkStrArr[2])).matcher(viewSource);
				ConcurrentHashMap<String, String> cMap = new ConcurrentHashMap<String, String>();
				/*while(matcher.find()) {
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
						tempDoc = conn.get();
					}
					
					String href2 = tempDoc.select("[as=fetch]").get(1).attr("href");
					System.out.println(href2);
					headerLinks.put(href2, "");
				}*/
				
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
			BufferedReader br = new BufferedReader(new InputStreamReader(ucon.getInputStream(),"UTF-8"));
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
