package com.audaharvest.parsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.audaharvest.common.CommonUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.iservices.IVehicleService;
import com.audaharvest.model.ConstantParameters;
import com.audaharvest.model.Vehicle;
import com.audaharvest.utils.ParsingUtils;

public class G4LinkJsonAdParser extends ParserJson implements IVehicleService {
	
		private String matchingText;
		private String matchingFeature;
		private String viewSource;
		public Vehicle getVehicleDetails() {
	
		try {
			
			vehicle.setUrl(url);
			vehicle.setDomain(urlMap.get(Constants.ROOT_URL));
			viewSource = getViewSource(url);
			if((urlMap.get(Constants.BLOCK_DATA_FINDER) != null) && !urlMap.get(Constants.BLOCK_DATA_FINDER).isEmpty()) {
				String[] strArr = urlMap.get(Constants.BLOCK_DATA_FINDER).split("\\^");
				Matcher matcher = Pattern.compile(Pattern.quote(strArr[0]) + "(.*?)"+ Pattern.quote(strArr[1])).matcher(viewSource);
				while(matcher.find()) {
					matchingText = matcher.group(0);
					break;
				}
				if(matchingText == null) {
					matcher = Pattern.compile(Pattern.quote(strArr[0]) + "(.*?)"+ Pattern.quote(strArr[1])).matcher(viewSource.replaceAll("\\r\\n|\\r|\\n", ""));
					while(matcher.find()) {
						matchingText = matcher.group(0);
						break;
					}
				}
				getValuesFromJsonSource(ParsingUtils.getCleanJson(matchingText));
			}
			if(urlMap.get(Constants.SALES_NUMBER).contains(Constants.GET_BY_REGEX)) {
				String param = urlMap.get(Constants.SALES_NUMBER).replaceAll(Constants.GET_BY_REGEX+"\\(", "");
                String selector = param.substring(0, param.length()-1);
				vehicle.setSellerPhoneNo(ParsingUtils.getByRegexSelector(selector, viewSource));
			}
			getSalesNo();
			getLocation();
			try {
				if((urlMap.get(Constants.VEHICLE_FEATURES) != null) && !urlMap.get(Constants.VEHICLE_FEATURES).isEmpty()) {
					String[] featureParam = urlMap.get(Constants.VEHICLE_FEATURES).split("\\^");
					Matcher matcher2 = Pattern.compile(Pattern.quote(featureParam[0]) + "(.*?)"+ Pattern.quote(featureParam[1])).matcher(viewSource);
					while(matcher2.find()) {
						matchingFeature = matcher2.group(0);
						break;
					}
					getFeaturesFromJsonSource(matchingFeature);
				}
				if(urlMap.get(Constants.VEHICLE_FEATURES).contains(Constants.GET_BY_REGEX)) {
					String param = urlMap.get(Constants.VEHICLE_FEATURES).replaceAll(Constants.GET_BY_REGEX+"\\(", "");
	                String selector = param.substring(0, param.length()-1);
	                vehicle.setVehicleFeatures(ParsingUtils.getByRegexSelector(selector, viewSource));
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if(vehicle.getVehicleFeatures()==null || vehicle.getVehicleFeatures().trim().length() == 0) {
					if((urlMap.get(Constants.VEHICLE_FEATURES) != null) && !urlMap.get(Constants.VEHICLE_FEATURES).isEmpty()) {
						Document doc = getJSoupDocument();
						Elements ele = doc.select(urlMap.get(Constants.VEHICLE_FEATURES));
						StringBuilder sb = new StringBuilder();
						for(int i =0 ; i< ele.size(); i++) {
							sb.append(ele.get(i).text()+" ");
						}
						vehicle.setVehicleFeatures(sb.toString());
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			getDealerInfo();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Parsed Link: " + vehicle.getUrl());
		latch.countDown();
		return vehicle;
	}

	@Override
	public Vehicle call() throws Exception {
		return getVehicleDetails();
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
	
	private Document getJSoupDocument() {
		Document doc = null;
		try {
			String locale = (ConstantParameters.getLanguageSelection().equals("True") ? "?locale=en_CA" : "");
			Connection con = Jsoup.connect(url+locale)
								.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
								.referrer("http://www.google.com")
								.ignoreContentType(true)
								.followRedirects(true)
								.timeout(60*1000);
			
			Connection.Response res = con.execute();
			
			if(res.statusCode() == 200) {
				doc = con.get();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}
}