package com.audaharvest.parsers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.audaharvest.constants.Constants;
import com.audaharvest.model.Vehicle;

public class InventoryPageFlatParser {
	Document doc = null;
	int size;
	public List<Vehicle> getVehicleDatas(String url, Map<String, String> urlMap, String[] manufacCodeArray) {
		try {
			Connection con = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.referrer("http://www.google.com")
					.ignoreContentType(true)
					.followRedirects(true)
					.timeout(20*1000);

			Connection.Response res = con.execute();

			if(res.statusCode() == 200) {
				doc = con.get();
			}
			size = doc.select(urlMap.get(Constants.BLOCK_DATA_FINDER)).size();
		} catch(Exception e) {
			e.printStackTrace();
		}
		for(int i=0; i< size; i++) {
			String makeModelYear = urlMap.get(Constants.MANUFACTURER);
			String kilometer = urlMap.get(Constants.MILEAGE);
		}
		return null;		
	}

}
