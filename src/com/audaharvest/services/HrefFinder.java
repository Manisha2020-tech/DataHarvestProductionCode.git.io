package com.audaharvest.services;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HrefFinder implements Runnable{
	public static ConcurrentMap<String, String> pageLinks = new ConcurrentHashMap<String, String>();
	//private ConcurrentMap<String, String> map;
	private String href;
	private String rootUrl;
	private Document doc;
	private CountDownLatch latch;
	private int urlSetsize;
	org.apache.logging.log4j.Logger logger = LogManager.getLogger(HrefFinder.class);
	
	
	public HrefFinder() {
		
		//this.pageLinks = new ConcurrentHashMap<String, String>();
	}
	
	public HrefFinder(String href, String rootUrl, CountDownLatch latch) {
		this.href = href;
		this.rootUrl = rootUrl;
		//this.map = map;
		//this.pageLinks = new ConcurrentHashMap<String, String>();
		this.latch = latch;
		//this.hrefFinder = hrefFinder;
	}
	
	@Override
	public void run() {
		try {
			//doc = (Document) Jsoup.connect(url).timeout(20*1000).get();	
				Connection con = Jsoup.connect(href)
						.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
						.referrer("http://www.google.com")
						.ignoreContentType(true)
						.followRedirects(true)
						.timeout(20*1000);
	
				Connection.Response res = con.execute();
	
				if(res.statusCode() == 200) {
					doc = con.get();					
				
				}
				Elements links = doc.select("a[href]");
				for(Element link : links){
					if(link.attr("abs:href").toLowerCase().contains(rootUrl.toLowerCase()) && (!pageLinks.containsKey(link.attr("abs:href")))) {
						//System.out.println(link.attr("abs:href"));
						pageLinks.put(link.attr("abs:href"), "");
						logger.debug(link.attr("abs:href"), "");
						//System.out.println(count++);
					}
				}
			} catch (IOException e){
				e.printStackTrace();
		}
		/*urlSetsize--;
		if(urlSetsize == 0) {*/
			latch.countDown();
		//}
	}
	
	public ConcurrentMap<String, String> getPageLinks(){
		return pageLinks;
	}

	public int getUrlSetsize() {
		return urlSetsize;
	}

	public void setUrlSetsize(int urlSetsize) {
		this.urlSetsize = urlSetsize;
	}
	
	

}