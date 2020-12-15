package com.audaharvest.parsers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.audaharvest.common.WebDriverUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.iservices.ICSVFileWriter;
import com.audaharvest.model.Vehicle;
import com.audaharvest.services.CSVFileWriter;

public class JsonParser {

public Set<Vehicle> getVehicles(String path, String jsonarrayKey,Map<String, String> urlMap) throws IOException, JSONException {
	
	if(urlMap.get(Constants.DOWNLOAD_PAGE).equals("Download")) {
		
	}else {
		
	}
	//path = rootURL
	//jsonarrayKey = complete json
	//urlmap
	Set<Vehicle> set =  new HashSet<Vehicle>();
	ExecutorService executor = Executors.newFixedThreadPool(10);
//		File f = new File("C:\\Users\\Khushboo.Agarwal\\Downloads\\ONMKF_new.json");
	File f = new File(System.getProperty("user.home")+"/"+path);
BufferedReader br = new BufferedReader(new FileReader(f));
StringBuilder fileDta = new StringBuilder();
String line = br.readLine();
while(line!=null) {
	fileDta.append(line);
	fileDta.append("\n");
	line = br.readLine();
}

JSONObject jsono = new JSONObject(fileDta.toString());
	
//	JSONArray array =jsono.getJSONArray("results");
JSONArray array =jsono.getJSONArray(jsonarrayKey);
	for(int i = 0; i< array.length(); i++) {
		Vehicle v = new Vehicle();
		JSONObject obj = (JSONObject) array.get(i);
		v.setUrl("https://www.mikeknappford.com/inventory/listings/used?stockID=3723");
		v.setExtColour(obj.getString(urlMap.get(Constants.EXTERIOR_COLOR)));
		v.setIntColour(obj.getString(urlMap.get(Constants.INTERIOR_COLOR)));
		v.setMake(obj.getString(urlMap.get(Constants.MANUFACTURER)));
		v.setModel(obj.getString(urlMap.get(Constants.MODEL)));
		v.setYear(getValue(urlMap.get(Constants.YEAR), obj));
		v.setBodystyle(obj.getString(urlMap.get(Constants.BODYSTYLE)));
		v.setDoors(""+obj.getInt(urlMap.get(Constants.DOORS)));
//		v.setPassengers(obj.getString(urlMap.get(Constants.PASSENGERS)));
		v.setEngine(obj.getString(urlMap.get(Constants.ENGINE)));
		v.setFuel(obj.getString(urlMap.get(Constants.FUEL)));
		v.setDrivetrain(obj.getString(urlMap.get(Constants.DRIVETRAIN)));
//		v.setSellerPhoneNo(obj.getString(urlMap.get(Constants.SALES_NUMBER)));
//		v.setSellerPhoneNo(urlMap.get(Constants.SALES_NUMBER));
		v.setSellerPhoneNo(getValue(urlMap.get(Constants.SALES_NUMBER), obj));
		v.setTransmission(obj.getString(urlMap.get(Constants.TRANSMISSION)));
		v.setStockNumber(obj.getString(urlMap.get(Constants.STOCK_NUM)));
		v.setPrice(obj.getString(urlMap.get(Constants.PRICE)));
		v.setVin(obj.getString(urlMap.get(Constants.VIN)));
		v.setMileage(""+obj.getInt(urlMap.get(Constants.MILEAGE)));
//		v.setLocation(obj.getString(urlMap.get(Constants.LOCATION)));
		v.setLocation(urlMap.get(Constants.LOCATION));
//		v.setCountryCode(obj.getString(urlMap.get(Constants.COUNTRY_CODE)));
		v.setCountryCode(urlMap.get(Constants.COUNTRY_CODE));
//		v.setModelCode(obj.getString(urlMap.get(Constants.MODEL_CODE)));
		v.setModelCode(urlMap.get(Constants.MODEL_CODE));
		v.setTrim(obj.getString(urlMap.get(Constants.TRIM)));
//		v.setVehicleFeatures(obj.getString(urlMap.get(Constants.VEHICLE_FEATURES)));
		v.setVehicleDesc(obj.getString(urlMap.get(Constants.VEHICLE_DESC)));
//		v.setDealerName(obj.getString(urlMap.get(Constants.DEALER_NAME)));
		v.setDealerName(Constants.DEALER_NAME);
//		v.setDealerCity(obj.getString(urlMap.get(Constants.DEALER_CITY)));
		v.setDealerCity(Constants.DEALER_CITY);
//		v.setDealerProvince(obj.getString(urlMap.get(Constants.DEALER_PROVINCE)));
		v.setDealerProvince(Constants.DEALER_PROVINCE);
//		v.setMakeModelYearDetail(obj.getString(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL)));
//		v.setDealerFlag(obj.getString(urlMap.get(Constants.DEALER_FLAG)));
		v.setDealerFlag(Constants.DEALER_FLAG);
//		v.setCountryCode(obj.getString(urlMap.get(Constants.COUNTRY_CODE)));
		v.setCountryCode(Constants.COUNTRY_CODE);
		//all string and regex 
		set.add(v);
	}
	return set;
}




public String getValue(String key, JSONObject obj) {
	String value = "";
	try {
		value = obj.getString(key);
	}catch(JSONException e) {
		if(e.getMessage().contains("not a string")) {
			try {
				value = ""+obj.getInt(key);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}
	if(value.isEmpty()) {
		value = key;
	}
	return value;
	
}

/*public Set<String> getUrls() {
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
	return all
}*/

}
