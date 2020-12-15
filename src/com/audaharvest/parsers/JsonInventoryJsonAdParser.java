package com.audaharvest.parsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;


import com.audaharvest.common.CommonUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.iservices.IVehicleService;
import com.audaharvest.model.Vehicle;
import com.audaharvest.utils.ParsingUtils;

public class JsonInventoryJsonAdParser extends ParserJson implements IVehicleService {
	
		private String matchingText;
		private String matchingFeature;

		public Vehicle getVehicleDetails() {
	
		try {
			
			vehicle.setUrl(url);
			vehicle.setDomain(urlMap.get(Constants.ROOT_URL));
			String viewSource = getViewSource(url);
			if((urlMap.get(Constants.BLOCK_DATA_FINDER) != null) && !urlMap.get(Constants.BLOCK_DATA_FINDER).isEmpty()) {
				String[] strArr = urlMap.get(Constants.BLOCK_DATA_FINDER).split("\\^");
				Matcher matcher = Pattern.compile(Pattern.quote(strArr[0]) + "(.*?)"+ Pattern.quote(strArr[1])).matcher(viewSource);
				while(matcher.find()) {
					matchingText = matcher.group(0);
					break;
				}
				getValuesFromJsonSource(ParsingUtils.getCleanJson(matchingText));
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
				getFeaturesFromJsonSource(ParsingUtils.getCleanJson(matchingFeature));
			}
			} catch (Exception ex) {
				ex.printStackTrace();
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

}
