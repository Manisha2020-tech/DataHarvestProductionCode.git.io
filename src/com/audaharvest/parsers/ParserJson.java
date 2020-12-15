package com.audaharvest.parsers;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.audaharvest.common.CommonUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.model.Vehicle;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;


public abstract class ParserJson implements Callable<Vehicle> {
	
	protected String url;
	private String[] makeArray;
	protected Map<String, String> urlMap;
	protected CountDownLatch latch = null;
	private Map<String, String> urlMapVehicleFeatures;
	protected Vehicle vehicle;
	
	public ParserJson() {
		vehicle = new Vehicle();
	}
	
	public void getValuesFromJsonSource(String blockInString) {
		Map<String, Integer> paramsIndexMap = new HashMap<String, Integer>();
		
		try {			
			for(String key : urlMapVehicleFeatures.values()) {
				if(blockInString.contains(key)) {
					int position = blockInString.indexOf(key);
					paramsIndexMap.put(key, position);

				}			
			}
			
			CommonUtil util = new CommonUtil();
			LinkedHashMap<String, Integer> sortedMapByValues = (LinkedHashMap<String, Integer>) util.sortByValues(paramsIndexMap);
			Integer[] mapValues = sortedMapByValues.values().toArray( new Integer[0]);			
			Integer[] arrays = new Integer[mapValues.length + 1];
			for(int i=0; i< arrays.length - 1; i++) {
				arrays[i] = Integer.valueOf(mapValues[i]);
			}
			arrays[arrays.length - 1] = Integer.valueOf(blockInString.length());

			for(Entry<String, Integer> entry: sortedMapByValues.entrySet()) {
				String key = entry.getKey();
				int val = entry.getValue();
				String tempStr = blockInString;
				
				if(key.equals(urlMap.get(Constants.YEAR))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.YEAR)+")(:?)", "").replaceAll("\\D+", "").trim();
					vehicle.setYear(newStr);
				}
				
				if(key.equals(urlMap.get(Constants.MANUFACTURER))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.MANUFACTURER)+")(:?)", "").replace("|", "").trim();
					vehicle.setMake(newStr);
				}
				
				if(key.equals(urlMap.get(Constants.MODEL))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.MODEL)+")(:?)", "").replace("|", "").trim();
					vehicle.setModel(newStr);
				}
				
				if(key.equals(urlMap.get(Constants.PRICE))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.PRICE)+")(:?)", "").replace("|", "").trim();
					double price = Double.parseDouble(newStr);
					DecimalFormat df = new DecimalFormat("#.00");
					vehicle.setPrice("$"+df.format(price));
				}
				if(key.equals(urlMap.get(Constants.BODYSTYLE))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.BODYSTYLE)+")(:?)", "").replace("|", "").trim();
					vehicle.setBodystyle(newStr);
				}
				if(key.equals(urlMap.get(Constants.ENGINE))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.ENGINE)+")(:?)", "").replace("|", "").trim();
					vehicle.setEngine(newStr);
				}
				if(key.equals(urlMap.get(Constants.TRANSMISSION))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.TRANSMISSION)+")(:?)", "").replace("|", "").trim();
					vehicle.setTransmission(newStr);
				}
				if(key.equals(urlMap.get(Constants.FUEL))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.FUEL)+")(:?)", "").replace("|", "").trim();
					vehicle.setFuel(newStr);
				}
				if(key.equals(urlMap.get(Constants.EXTERIOR_COLOR))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.EXTERIOR_COLOR)+")(:?)", "").replace("|", "").trim();
					vehicle.setExtColour(newStr);
				}
				try {
					if(key.equals(urlMap.get(Constants.INTERIOR_COLOR))){
						String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.INTERIOR_COLOR)+")(:?)", "").replace("|", "").trim();
						vehicle.setIntColour(newStr);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if(key.equals(urlMap.get(Constants.STOCK_NUM))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replaceAll("(Stock|stock|:|#|Number|number|STK|stk|Inventaire|inventaire|\\u00a0)", "").replace("|", "").trim();
					//String[] newStrArray = newStr.split(" ");
					vehicle.setStockNumber(newStr);
				}
				if(key.equals(urlMap.get(Constants.VIN))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.VIN)+")(:?)", "").replace("|", "").trim();
					if (!(newStr.toUpperCase().contains("XXX")) && (newStr.length() > 2))
					vehicle.setVin(newStr);
				}
				if(key.equals(urlMap.get(Constants.DRIVETRAIN))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.DRIVETRAIN)+")(:?)", "").replace("|", "").trim();
					vehicle.setDrivetrain(newStr);
				}
				if(key.equals(urlMap.get(Constants.MILEAGE))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.MILEAGE)+")(:?)", "").replace("|", "").trim();
					double mileage = Double.parseDouble(newStr);
					DecimalFormat df = new DecimalFormat("#.00");
					vehicle.setMileage(df.format(mileage));
				}
				if(key.equals(urlMap.get(Constants.TRIM))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.TRIM)+")(:?)", "").replace("|", "").trim();
					vehicle.setTrim(newStr);
				}
				if(key.equals(urlMap.get(Constants.DOORS))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.DOORS)+")(:?)", "").replace("|", "").trim();
					vehicle.setDoors(newStr);
				}
				if(key.equals(urlMap.get(Constants.PASSENGERS))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.PASSENGERS)+")(:?)", "").replace("|", "").trim();
					vehicle.setPassengers(newStr);
				}
				if(key.equals(urlMap.get(Constants.MODEL_CODE))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.MODEL_CODE)+")(:?)", "").replace("|", "").trim();
					vehicle.setModelCode(newStr);
				}
				if(key.equals(urlMap.get(Constants.VEHICLE_STATUS))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.VEHICLE_STATUS)+")(:?)", "").replace("|", "").trim();
					if(newStr.length()>0){
						String soldMatchRegex = ".*\\b(sold|vendu)\\b.*";
						if(newStr.toLowerCase().matches(soldMatchRegex)) {
							vehicle.setStatus(true);
						}
					}
				}
				if(key.equals(urlMap.get(Constants.SALES_NUMBER))) {
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.SALES_NUMBER)+")(:?)", "").replace("|", "").trim();
					vehicle.setSellerPhoneNo(newStr);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void getSalesNo() {
		String salesNo = null;
		try {			
			if ((vehicle.getSellerPhoneNo() == null) && (urlMap.get(Constants.SALES_NUMBER) != null)) {
				salesNo = urlMap.get(Constants.SALES_NUMBER);
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(salesNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void getFeaturesFromJsonSource(String featuresInString) {
		
		try {
			if ((vehicle.getVehicleFeatures() == null) && (urlMap.get(Constants.VEHICLE_FEATURES) != null)) {
				String modString = featuresInString.replaceAll("(equipment|group|Features|description|finance|\"|:|\\{|\\}|_|\\[|\\])", "");
				vehicle.setVehicleFeatures(" "+ modString);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	public void getLocation() {
		try {
			if ((vehicle.getLocation() == null) && (!urlMap.get(Constants.LOCATION).isEmpty()) && (urlMap.get(Constants.LOCATION) != null)) {
				vehicle.setLocation(urlMap.get(Constants.LOCATION));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getDealerInfo() {
		
		try {
			
			if ((!urlMap.get(Constants.DEALER_NAME).isEmpty()) && (urlMap.get(Constants.DEALER_NAME) != null) && (vehicle.getDealerName() == null)) {
				vehicle.setDealerName(urlMap.get(Constants.DEALER_NAME));
			}

			if ((!urlMap.get(Constants.DEALER_CITY).isEmpty()) && (urlMap.get(Constants.DEALER_CITY) != null) && (vehicle.getDealerCity() == null)) {
				vehicle.setDealerCity(urlMap.get(Constants.DEALER_CITY));
			}

			if ((!urlMap.get(Constants.DEALER_PROVINCE).isEmpty()) &&  (urlMap.get(Constants.DEALER_PROVINCE) != null) && (vehicle.getDealerProvince() == null)) {
				vehicle.setDealerProvince(urlMap.get(Constants.DEALER_PROVINCE));
			}

			if ((!urlMap.get(Constants.DEALER_POSTAL_CODE).isEmpty()) && (urlMap.get(Constants.DEALER_POSTAL_CODE) != null) && (vehicle.getDealerZipCode() == null)) {
				vehicle.setDealerZipCode(urlMap.get(Constants.DEALER_POSTAL_CODE));
			}

			if ((urlMap.get(Constants.DEALER_FLAG) != null) || (!urlMap.get(Constants.DEALER_FLAG).isEmpty())) {
				vehicle.setDealerFlag(urlMap.get(Constants.DEALER_FLAG));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String[] getMakeArray() {
		return makeArray;
	}

	public void setMakeArray(String[] makeArray) {
		this.makeArray = makeArray;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	public Map<String, String> getUrlMap() {
		return urlMap;
	}

	public void setUrlMap(Map<String, String> urlMap) {
		this.urlMap = urlMap;
	}

	public Map<String, String> getUrlMapVehicleFeatures() {
		return urlMapVehicleFeatures;
	}

	public void setUrlMapVehicleFeatures(Map<String, String> urlMapVehicleFeatures) {
		this.urlMapVehicleFeatures = urlMapVehicleFeatures;
	}


	public abstract Vehicle getVehicleDetails();

}
