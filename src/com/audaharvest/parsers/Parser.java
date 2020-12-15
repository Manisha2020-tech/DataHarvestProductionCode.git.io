package com.audaharvest.parsers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.audaharvest.common.CommonUtil;
import com.audaharvest.common.ExtractTextFromImage;
import com.audaharvest.common.Matrix;
import com.audaharvest.common.WebDriverUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.model.Vehicle;
import com.audaharvest.services.MatrixService;
import com.audaharvest.services.fieldutils.MileageUtil;
import com.audaharvest.services.fieldutils.PriceUtil;
import com.audaharvest.utils.ImageToTextProjectApplication;
import com.audaharvest.utils.ParsingUtils;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;


public abstract class Parser implements Callable<Vehicle> {
	
	protected String url;
	private String[] makeArray;
	protected CountDownLatch latch = null;
	protected Map<String, String> urlMap;
	private Map<String, String> urlMapVehicleFeatures;
	protected WebDriver seleniumDriver;
	protected Vehicle vehicle;
	protected String[] htmlTags = {"tr", "dl", "li","p"}; // Removing div/span/label as of now
	private String[] newHtmlTags = {"tr", "dl", "li", "p", "div"};
	private String[] headerTags = {"h1", "h2", "h3", "h4", "title", "div"};
	protected String[] htmlTagsForParentNode = {"strong","span","label","dt", "dd","tr","li","p"};
	int minYear = Integer.parseInt(Constants.MIN_YEAR);
	int maxYear = Calendar.getInstance().get(Calendar.YEAR) + 1 ;
	
	private String make = null;
	private String model = null;
	private String year = null;
	private String sellerPhoneNo = null;
	private String engine = null;
	private String transmission = null;
	private String stockNumber = null;
	private String bodystyle = null;
	private String doors = null;
	private String passengers = null;
	private String cylinders = null;
	private String fuel = null;
	private String extColour = null;
	private String intColour = null;
	private String drivetrain = null;
	private String price = null;
	private String vin = null;
	private String mileage = null;
	private String location = null;
	private String modelCode = null;
	private String salesNo = null;
	private String trim = null;
	
	private String tag;
	
	public Parser() {
		vehicle = new Vehicle();
	}
	CommonUtil util = new CommonUtil();
	PriceUtil priceUtil = new PriceUtil();
	MileageUtil mileageUtil =  new MileageUtil();
	//Search vehicle details which are in block
	public void getBlockData(Document doc) {
		MatrixService matBuilder = new MatrixService();		
		Matrix matrix = matBuilder.tagParamsMatrix(doc, htmlTags, urlMapVehicleFeatures);
		Map<String, Integer> map = matBuilder.maxColsFilled(matrix, htmlTags);
		tag = (String) map.keySet().toArray()[0];
		
		if(tag == null){tag = "div";}
		
		try {			
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.BODYSTYLE)+")").size() > 0){								
				bodystyle = doc.select(tag+":matches(^"+urlMap.get(Constants.BODYSTYLE)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.BODYSTYLE)+")(:?)", "").replace("|", "").trim();
				vehicle.setBodystyle(bodystyle);
			}
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.DOORS)+")").size() > 0){
				doors = doc.select(tag+":matches(^"+urlMap.get(Constants.DOORS)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.DOORS)+")(:?)", "").replace("|", "").trim();
				vehicle.setDoors(doors);
			}				
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.PASSENGERS)+")").size() > 0){
				passengers = doc.select(tag+":matches(^"+urlMap.get(Constants.PASSENGERS)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.PASSENGERS)+")(:?)", "").trim();
				vehicle.setPassengers(passengers);
			} 					
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.ENGINE)+")").size() > 0){
				engine = doc.select(tag+":matches(^"+urlMap.get(Constants.ENGINE)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.ENGINE)+")(:?)", "").replace("|", "").trim();
				vehicle.setEngine(engine);
			} 
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.FUEL)+")").size() > 0){
				fuel = doc.select(tag+":matches(^"+urlMap.get(Constants.FUEL)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.FUEL)+")(:?)", "").replace("|", "").trim();
				vehicle.setFuel(fuel);
			} 
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.EXTERIOR_COLOR)+")").size() > 0){
				extColour = doc.select(tag+":matches(^"+urlMap.get(Constants.EXTERIOR_COLOR)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.EXTERIOR_COLOR)+")(:?)", "").trim();
				vehicle.setExtColour(extColour);
			} 
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.INTERIOR_COLOR)+")").size() > 0){
				intColour = doc.select(tag+":matches(^"+urlMap.get(Constants.INTERIOR_COLOR)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.INTERIOR_COLOR)+")(:?)", "").trim();
				vehicle.setIntColour(intColour);
			}
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.DRIVETRAIN)+")").size() > 0){
				drivetrain = doc.select(tag+":matches(^"+urlMap.get(Constants.DRIVETRAIN)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.DRIVETRAIN)+")(:?)", "").replace("|", "").trim();
				vehicle.setDrivetrain(drivetrain);
			}
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.TRANSMISSION)+")").size() > 0){
				transmission = doc.select(tag+":matches(^"+urlMap.get(Constants.TRANSMISSION)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.TRANSMISSION)+")(:?)", "").replace("|", "").trim();
				vehicle.setTransmission(transmission);
			}
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.STOCK_NUM)+")").size() > 0){
				stockNumber = doc.select(tag+":matches(^"+urlMap.get(Constants.STOCK_NUM)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replace("|", "").replace(".", "").trim();
				vehicle.setStockNumber(stockNumber);
			}
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.PRICE)+")").size() > 0){
				price = doc.select(tag+":matches(^"+urlMap.get(Constants.PRICE)+")").get(0).text().replace(".00", "").replaceAll("\\D+", "").trim();
				vehicle.setPrice("$"+price);
			} 
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.VIN)+")").size() > 0){
				vin = doc.select(tag+":matches(^"+urlMap.get(Constants.VIN)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.VIN)+")(:?)", "").trim();
				if (!(vin.toUpperCase().contains("XXX")) && (vin.length() > 2))
				vehicle.setVin(vin);
			}
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.MILEAGE)+")").size() > 0){
				mileage = doc.select(tag+":matches(^"+urlMap.get(Constants.MILEAGE)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.MILEAGE)+")(:?)", "").replace("|", "").replaceAll("\\D+", "").trim();
				vehicle.setMileage(mileage+"km");
			}
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.MODEL_CODE)+")").size() > 0){
				modelCode = doc.select(tag+":matches(^"+urlMap.get(Constants.MODEL_CODE)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.MODEL_CODE)+")(:?)", "").replace("|", "").trim();
				vehicle.setModelCode(modelCode);
			}
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.LOCATION)+")").size() > 0){
				location = doc.select(tag+":matches(^"+urlMap.get(Constants.LOCATION)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.LOCATION)+")(:?)", "").replace("|", "").trim();
				vehicle.setLocation(location);
			}
			if(doc.select(tag+":matches(^"+urlMap.get(Constants.TRIM)+")").size() > 0){
				trim = doc.select(tag+":matches(^"+urlMap.get(Constants.TRIM)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.TRIM)+")(:?)", "").replace("|", "").trim();
				vehicle.setTrim(trim);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}			
			
	}
	
	public void getBlockDataByParentNode(Document doc) {
		MatrixService matBuilder = new MatrixService();
		Map<String, Integer> paramsIndexMap = new HashMap<String, Integer>();
		String blockInString = null;
		String engineParent = null;
		String transParent = null;
		
		try {
			if((urlMap.get(Constants.BLOCK_DATA_FINDER) == null) || (urlMap.get(Constants.BLOCK_DATA_FINDER).isEmpty())) {
				Matrix matrix = matBuilder.tagParamsMatrix(doc, htmlTagsForParentNode, urlMapVehicleFeatures);
				Map<String, Integer> map = matBuilder.maxColsFilled(matrix, htmlTagsForParentNode);
				tag = (String) map.keySet().toArray()[0];
			
				if(tag == null){tag = "div";}
			
				if((doc.select(tag+":contains("+urlMap.get(Constants.TRANSMISSION)+")").get(0).parent().tagName() == "dt") || (doc.select(tag+":contains("+urlMap.get(Constants.ENGINE)+")").get(0).parent().tagName() == "dt")) {
					tag = "dl";
					engineParent = doc.select(tag+":contains("+urlMap.get(Constants.ENGINE)+")").text();
					transParent = doc.select(tag+":contains("+urlMap.get(Constants.TRANSMISSION)+")").text();
				} else if((doc.select(tag+":contains("+urlMap.get(Constants.TRANSMISSION)+")").get(0).parent().tagName() == "td") || (doc.select(tag+":contains("+urlMap.get(Constants.ENGINE)+")").get(0).parent().tagName() == "td")) {
					tag = "tbody";
					engineParent = doc.select(tag+":contains("+urlMap.get(Constants.ENGINE)+")").text();
					transParent = doc.select(tag+":contains("+urlMap.get(Constants.TRANSMISSION)+")").text();
				} else if((doc.select(tag+":contains("+urlMap.get(Constants.TRANSMISSION)+")").get(0).parent().tagName() == "li") || (doc.select(tag+":contains("+urlMap.get(Constants.ENGINE)+")").get(0).parent().tagName() == "li")) {
					tag = "ul";
					engineParent = doc.select(tag+":contains("+urlMap.get(Constants.ENGINE)+")").text();
					transParent = doc.select(tag+":contains("+urlMap.get(Constants.TRANSMISSION)+")").text();
				} else {
					engineParent = doc.select(tag+":contains("+urlMap.get(Constants.ENGINE)+")").get(0).parent().text();
					transParent = doc.select(tag+":contains("+urlMap.get(Constants.TRANSMISSION)+")").get(0).parent().text();
				}
				
				for(String key : urlMapVehicleFeatures.values()) {
					if(engineParent.contains(key) || transParent.contains(key)) {
						if(engineParent.contains(key)) {
							blockInString = engineParent;
							int position = engineParent.indexOf(key);
							paramsIndexMap.put(key, position);
						} else if (transParent.contains(key)){
							blockInString = transParent;
							int position = transParent.indexOf(key);
							paramsIndexMap.put(key, position);
						}

					}
					
				}
			} else {
				blockInString = doc.select(urlMap.get(Constants.BLOCK_DATA_FINDER)).get(0).text();			
				for(String key : urlMapVehicleFeatures.values()) {
					if(blockInString.contains(key)) {
						int position = blockInString.indexOf(key);
						paramsIndexMap.put(key, position);

					}
				
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
				
				if(key.equals(urlMap.get(Constants.PRICE))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replace(".00", "").replaceAll("\\D+", "").trim();
					vehicle.setPrice("$"+newStr);
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
				if(key.equals(urlMap.get(Constants.INTERIOR_COLOR))){
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.INTERIOR_COLOR)+")(:?)", "").replace("|", "").trim();
					vehicle.setIntColour(newStr);
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
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.MILEAGE)+")(:?)", "").replace("|", "").replaceAll("\\D+", "").trim();
					vehicle.setMileage(newStr+"km");
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
				
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	//This will run only if default method fails
	public void getMakeModelYearByBlock(Document doc) {
		try {
			if(doc.select(urlMap.get(Constants.MANUFACTURER)).size() > 0) {
				String headerPara = doc.select(urlMap.get(Constants.MANUFACTURER)).get(0).text().replaceAll("(\\u00a0|,|<br>|\\||,|-)", "");
				for (int i=0; i<makeArray.length;i++) {
					if(headerPara.contains(makeArray[i])) {
						vehicle.setMake(makeArray[i]);
						int makePos = headerPara.toLowerCase().indexOf(vehicle.getMake().toLowerCase());
						int len = makePos+makeArray[i].length();
						//String headerParaTruncated = headerPara.substring(len, headerPara.length()).replace("|", "").trim();
						String headerParaTruncated = headerPara.substring(len, headerPara.length()).replaceAll("(\\u00a0|,|<br>|\\|,|-)", " ").trim();
						String[] headerParaTruncatedArray = headerParaTruncated.split(" ");
						//vehicle.setModel(headerParaTruncatedArray[0].replaceAll("(\\u00a0)", ""));
						if(vehicle.getModel() == null || vehicle.getModel().isEmpty()) {
							vehicle.setModel(headerParaTruncatedArray[0]);
						}
						Pattern p = Pattern.compile("(\\d{4})");
						Matcher m = p.matcher(headerPara);
						while(m.find()) {
							if((Integer.parseInt(m.group(0)) >= minYear) && (Integer.parseInt(m.group(0)) <= maxYear)) {
								vehicle.setYear(m.group(0));
								break;
							}				
						}
						break;
					}
					
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getMakeModelYear(Document doc){
		try {
			Elements headerEl = null;
			if((urlMap.get(Constants.LOCALIZED_COUNTRY_CODE) != null) && (urlMap.get(Constants.LOCALIZED_COUNTRY_CODE).equals(Constants.FRANCE_CODE))) {
				for(int k=0; k<headerTags.length;k++) {
					for (int i=0; i<makeArray.length;i++) {
						if(doc.select(headerTags[k]+":matches(^"+makeArray[i]+")").size() > 0) {
							headerEl = doc.select(headerTags[k]+":matches(^"+makeArray[i]+")");
							if(headerEl.size() > 0 ){
								vehicle.setMake(makeArray[i]);
								break;
							}
						} else if (doc.select(headerTags[k]+":matches("+makeArray[i]+")").size() > 0) {
							headerEl = doc.select(headerTags[k]+":matches("+makeArray[i]+")");
							if(headerEl.size() > 0 ){
								vehicle.setMake(makeArray[i]);
								break;
							}
						}
					}
					if(vehicle.getMake() != null)
						break;
				}
				if(headerEl.size() > 0) {				
					String headerPara = headerEl.get(0).text().replaceAll(",", "");
					int makePos = headerPara.toLowerCase().indexOf(vehicle.getMake().toLowerCase());
					int len = makePos+vehicle.getMake().length();
					String headerParaTruncated = headerPara.substring(len, headerPara.length()).trim();
					String[] headerParaTruncatedArray = headerParaTruncated.split(" ");
					if(vehicle.getModel() == null || vehicle.getModel().isEmpty()) {
						vehicle.setModel(headerParaTruncatedArray[0]);
					}
					Pattern p = Pattern.compile("(\\d{4})");
					Matcher m = p.matcher(headerPara);
					while(m.find()) {
						if((Integer.parseInt(m.group(0)) >= minYear) && (Integer.parseInt(m.group(0)) <= maxYear)) {
							vehicle.setYear(m.group(0));
							break;
						}				
					}
					/*Pattern p = Pattern.compile("(\\d{4})");
				Matcher m = p.matcher(headerText);
				if(m.find()) {
					vehicle.setYear(m.group(0));
				}
				String[] splitText = headerText.split(" ");
				vehicle.setModel(splitText[1]);*/
				}
			} else {
				for(int k=0; k<headerTags.length;k++) {
					for (int i=0; i<makeArray.length;i++) {
						if(doc.select(headerTags[k]+":matches((^\\d{4})(\\s)("+makeArray[i]+"))").size() > 0) {
							headerEl = doc.select(headerTags[k]+":matches((^\\d{4})(\\s)("+makeArray[i]+"))");
							if(headerEl.size() > 0 ){
								vehicle.setMake(makeArray[i]);
								break;
							}
						} else if (doc.select(headerTags[k]+":matches((\\d{4})(\\s)("+makeArray[i]+"))").size() > 0) {
							headerEl = doc.select(headerTags[k]+":matches((\\d{4})(\\s)("+makeArray[i]+"))");
							if(headerEl.size() > 0 ){
								vehicle.setMake(makeArray[i]);
								break;
							}
						}
					}
					if(vehicle.getMake() != null)
						break;
				}
				if(headerEl.size() > 0) {			
					String headerPara = headerEl.get(0).text().replaceAll(",", "");
					int makePos = headerPara.toLowerCase().indexOf(vehicle.getMake().toLowerCase());
					int len = makePos+vehicle.getMake().length();
					String headerParaTruncated = headerPara.substring(len, headerPara.length()).replaceAll("(\\u00a0)", " ").trim();
					String[] headerParaTruncatedArray = headerParaTruncated.split(" ");
					if(vehicle.getModel() == null || vehicle.getModel().isEmpty()) {
						vehicle.setModel(headerParaTruncatedArray[0]);
					}
					Pattern p = Pattern.compile("(\\d{4})");
					//String headerParaReversed = util.reverseString(headerPara);
					Matcher m = p.matcher(headerPara);
					if(m.find()) {
						if((Integer.parseInt(m.group(0)) >= minYear) && (Integer.parseInt(m.group(0)) <= maxYear)) {
							vehicle.setYear(m.group(0));
						}				
					}
					/*String[] splitText = headerText.split(vehicle.getMake());
				String text1 = splitText[0].trim();
				String text2 = splitText[1].trim();
				String[] splittext1 = text1.split(" ");
				String[] splittext2 = text2.split(" ");
				vehicle.setYear(splittext1[splittext1.length - 1]);
				vehicle.setModel(splittext2[0]);*/
				}
			}

			if(urlMap.get(Constants.MODEL).contains(Constants.GET_BY_REGEX)) {
				String param = urlMap.get(Constants.MODEL).replaceAll(Constants.GET_BY_REGEX+"\\(", "");
	            String selector = param.substring(0, param.length()-1);
				vehicle.setModel(ParsingUtils.getCleanJson(ParsingUtils.getByRegexSelector(selector, getViewSource(doc.baseUri()))));
			}
			
			if(urlMap.get(Constants.MANUFACTURER).contains(Constants.GET_BY_REGEX)) {
				String param = urlMap.get(Constants.MANUFACTURER).replaceAll(Constants.GET_BY_REGEX+"\\(", "");
	            String selector = param.substring(0, param.length()-1);
	            String matchMake = ParsingUtils.getCleanJson(ParsingUtils.getByRegexSelector(selector, getViewSource(doc.baseUri())));
	            for (int i=0; i<makeArray.length;i++) {
	            	if(matchMake.matches("(.*)"+makeArray[i]+"(.*)")) {
	            		vehicle.setMake(makeArray[i]);
	            		break;
	            	}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}
	
	public void getVehicleYear(Document doc) {
		try {
			if(((vehicle.getYear() == null) || (vehicle.getYear().isEmpty())) && (doc.select(urlMap.get(Constants.YEAR)).size() > 0)) {
				String st = doc.select(urlMap.get(Constants.YEAR)).get(0).text();
				Pattern p = Pattern.compile("(\\d{4})");
				Matcher m = p.matcher(st);
				while(m.find()) {
					if((Integer.parseInt(m.group(0)) >= minYear) && (Integer.parseInt(m.group(0)) <= maxYear)) {
						vehicle.setYear(m.group(0));
						break;
					}				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getVehicleMake(Document doc) {
		try {
			if(((vehicle.getMake() == null) || (vehicle.getMake().isEmpty())) && (doc.select(urlMap.get(Constants.MANUFACTURER)).size() > 0)) {
				String st = doc.select(urlMap.get(Constants.MANUFACTURER)).get(0).text();
				vehicle.setMake(st);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getVehicleModel(Document doc) {
		try {
			if(((vehicle.getModel() == null) || (vehicle.getModel().isEmpty())) && (doc.select(urlMap.get(Constants.MODEL)).size() > 0)) {
				String st = doc.select(urlMap.get(Constants.MODEL)).get(0).text().replaceAll("(\\u00a0|,|<br>|\\||,|-)", "");
				vehicle.setModel(st);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getSalesNo(Document doc) {
		String phoneNo = null;		
		try {
			if((vehicle.getSellerPhoneNo() == null) && (urlMap.get(Constants.SALES_NUMBER).toLowerCase().contains(Constants.PHONE_BY_DIRECT_LINK.toLowerCase()))) {
				int lastIndex = urlMap.get(Constants.SALES_NUMBER).lastIndexOf(")");
				String closedBracket = urlMap.get(Constants.SALES_NUMBER).substring(0, lastIndex);
				String param = closedBracket.replace(Constants.PHONE_BY_DIRECT_LINK+"(", "");
				String attr = doc.select(param).attr("href");
				//String viewSource = getViewSource(attr);
				/*String viewSource = getURLSource(attr);
				Pattern p = Pattern.compile("\\d{1}-\\d{3}-\\d{3}-\\d{4}|\\d{3}-\\d{3}-\\d{4}");
				Matcher m = p.matcher(viewSource);
				while(m.find()) {
					vehicle.setSellerPhoneNo(m.group(0));
					break;				
				}*/
				WebDriverUtil webDriverUtil = new WebDriverUtil();
				WebDriver driver = webDriverUtil.getFirefoxDriver();
				driver.get(attr);
				String viewSource = getViewSource(driver);
				Pattern p = Pattern.compile("\\d{1}-\\d{3}-\\d{3}-\\d{4}|\\d{3}-\\d{3}-\\d{4}");
				Matcher m = p.matcher(viewSource);
				while(m.find()) {
					vehicle.setSellerPhoneNo(m.group(0));
					break;				
				}
				driver.quit();
				
			}
			if((vehicle.getSellerPhoneNo() == null) && (urlMap.get(Constants.SALES_NUMBER).toLowerCase().contains(Constants.PHONE_BY_CLICK.toLowerCase()))) {
				String newStr =  urlMap.get(Constants.SALES_NUMBER).replace(Constants.PHONE_BY_CLICK+"(", "");
				String[] linkStrArr = newStr.split("\\^");
				String adUrls = linkStrArr[linkStrArr.length - 1].replaceAll("(phoneUrl=|\\))", "");
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
				Elements elms = null;
				try {
					elms = doc.select(elmSelect);
				} catch (Exception e) {
					
				}
				
				StringBuilder sb = new StringBuilder();
				//Iterate over all elements to make urls
				try {				
				String[] fieldValArray = new String[linkStrArr.length-2];
				for(int j=0; j < paramsArray.length; j++) {
					//String val = doc.select(paramsArray[j]).get(0).text();
					//fieldValArray[j] = val;
					String val = null;
					Elements el = doc.select(paramsArray[j]);
					if(val != null || !el.attr("href").isEmpty()) {
						val = el.attr("href");
					} else if(val != null || !el.attr("value").isEmpty()) {
						val = el.attr("value");
					}
					
					fieldValArray[j] = val;
				}
				int l = 0;
				//Re-building url with each character and replacing params with actual value
				for(int k=0; k < adlen ; k++ ) {
					if(k == paramIndexArr[l]) {
						k= k+5;
						sb.append(fieldValArray[l]);
						l = l+1;
					} else {
						sb.append(adUrls.charAt(k));
					}
				}
				System.out.println(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				String viewSource = getViewSource(sb.toString());
				Document phoneDoc = Jsoup.parse(viewSource);
				if(elmSelect != null && !elmSelect.isEmpty() && phoneDoc.select(elmSelect).size() > 0) {
					String phoneNoString = phoneDoc.select(elmSelect).get(0).text();
					Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNoString, urlMap.get(Constants.COUNTRY_CODE)).iterator();
					while(phoneNumExists.hasNext()){
						salesNo = phoneNumExists.next().rawString();
						break;
					}
					
				} else {
					Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(viewSource, urlMap.get(Constants.COUNTRY_CODE)).iterator();
					while(phoneNumExists.hasNext()){
						salesNo = phoneNumExists.next().rawString();
						break;
					}
				}
				vehicle.setSellerPhoneNo(salesNo);

				/*int lastIndex = urlMap.get(Constants.SALES_NUMBER).lastIndexOf(")");
				String newStr =  urlMap.get(Constants.SALES_NUMBER).replace(String.valueOf(urlMap.get(Constants.SALES_NUMBER).charAt(lastIndex)),"");
				String[] linkStrArr = newStr.replace(Constants.PHONE_BY_CLICK+"(", "").split("\\^");
				String param1 = Arrays.asList(linkStrArr).stream().filter(text -> text.contains("param1")).findFirst().get().replace("param1=", "");
				String param2 = Arrays.asList(linkStrArr).stream().filter(text -> text.contains("param2")).findFirst().get().replace("param2=", "");
				String urlParam = Arrays.asList(linkStrArr).stream().filter(text -> text.contains("phoneUrl=")).findFirst().get().replace("phoneUrl=", "");
				try {
					String val1 = null;
					String val2 = null;
					if (doc.select(param1).size() > 0) {
						val1= doc.select(param1).get(0).attr("value");
					}
					if (doc.select(param2).size() > 0) {
						val2= doc.select(param2).get(0).attr("value");
					}
					String phoneUrlFormed = urlParam.replace("param1", val1).replace("param2", val2);
					//String phoneUrlFormed = urlParam.replace("param1", val1);
					String viewSource = getViewSource(phoneUrlFormed);
					
					Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(viewSource, urlMap.get(Constants.COUNTRY_CODE)).iterator();
					while(phoneNumExists.hasNext()){
						salesNo = phoneNumExists.next().rawString();
						break;
					}
					vehicle.setSellerPhoneNo(salesNo);

				} catch(Exception e) {
					e.printStackTrace();					
				}*/
			}
			if((vehicle.getSellerPhoneNo() == null) && (urlMap.get(Constants.SALES_NUMBER).toLowerCase().contains(Constants.PHONE_FROM_VIEW_SOURCE.toLowerCase()))) {
				String viewSource = getViewSource(doc.baseUri());
				int lastIndex = urlMap.get(Constants.SALES_NUMBER).lastIndexOf(")");
				String newStr =  urlMap.get(Constants.SALES_NUMBER).replace(String.valueOf(urlMap.get(Constants.SALES_NUMBER).charAt(lastIndex)),"");
				String[] linkStrArr = newStr.replace(Constants.PHONE_FROM_VIEW_SOURCE+"(", "").split("\\^");
				Matcher matcher = Pattern.compile(Pattern.quote(linkStrArr[0]) + "(.*?)"+ Pattern.quote(linkStrArr[2])).matcher(viewSource);
				while(matcher.find()) {
					phoneNo = matcher.group(0).replaceAll(linkStrArr[0], "").replace(linkStrArr[2], "");
					vehicle.setSellerPhoneNo(phoneNo);
				}				
			}
			if((vehicle.getSellerPhoneNo() == null) && (urlMap.get(Constants.SALES_NUMBER).toLowerCase().contains(Constants.DO_NOT_VALIDATE_SALES_NUM.toLowerCase()))) {
				String param = urlMap.get(Constants.SALES_NUMBER).replaceAll("("+Constants.DO_NOT_VALIDATE_SALES_NUM+"|\\(|\\))", "");
				if((doc.select(param).size() > 0)) {
					phoneNo =  doc.select(param).get(0).text();
					vehicle.setSellerPhoneNo(phoneNo);					
				}				
			}
			if((vehicle.getSellerPhoneNo() == null) && (doc.select("div:contains("+urlMap.get(Constants.SALES_NUMBER)+")").size() > 0)) {
				for (int i=0; i<newHtmlTags.length; i++) {
					Elements e = doc.select(newHtmlTags[i]+":matches(^"+urlMap.get(Constants.SALES_NUMBER)+")");
					if(e.size() > 0) {
						phoneNo =  e.select(newHtmlTags[i]+":matches(^"+urlMap.get(Constants.SALES_NUMBER)+")").get(0).text();
						break;
					}
				}
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			} 
			if ((vehicle.getSellerPhoneNo() == null) && (doc.select("div:contains("+urlMap.get(Constants.SALES_NUMBER)+")").size() > 0)) {
				phoneNo =  doc.select("div:contains("+urlMap.get(Constants.SALES_NUMBER)+")").get(0).text();
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
			if ((vehicle.getSellerPhoneNo() == null) && (doc.select("[class~=^"+urlMap.get(Constants.SALES_NUMBER)+"]").size() > 0)) {
				phoneNo =  doc.select("[class~=^"+urlMap.get(Constants.SALES_NUMBER)+"]").get(0).text();
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
			if ((vehicle.getSellerPhoneNo() == null) && (doc.select("[id~=^"+urlMap.get(Constants.SALES_NUMBER)+"]").size() > 0)) {
				phoneNo =  doc.select("[id~=^"+urlMap.get(Constants.SALES_NUMBER)+"]").get(0).text();
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
			if ((vehicle.getSellerPhoneNo() == null) && (doc.select(urlMap.get(Constants.SALES_NUMBER)).size() > 0)) {
				phoneNo =  doc.select(urlMap.get(Constants.SALES_NUMBER)).get(0).text();
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
			if ((vehicle.getSellerPhoneNo() == null) && (doc.getElementsByAttribute(urlMap.get(Constants.SALES_NUMBER)).size() > 0)) {
				phoneNo = doc.getElementsByAttribute(urlMap.get(Constants.SALES_NUMBER)).get(0).attr(urlMap.get(Constants.SALES_NUMBER));
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
			if (((vehicle.getSellerPhoneNo() == null) || (vehicle.getSellerPhoneNo().isEmpty())) && (doc.select(urlMap.get(Constants.SALES_NUMBER)).size() > 0)) {
				Element el = doc.select(urlMap.get(Constants.SALES_NUMBER)).get(0);
				//Added for hidden fields
				phoneNo =  el.attr("value");
				//Added for meta tag
				if(phoneNo == null || phoneNo.isEmpty())
					phoneNo = el.attr("content");
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
			if ((vehicle.getSellerPhoneNo() == null) && (urlMap.get(Constants.SALES_NUMBER) != null)) {
				phoneNo = urlMap.get(Constants.SALES_NUMBER);
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public void getPriceDetail(Document doc) {
		try {
			if(urlMap.get(Constants.PRICE).contains(Constants.GET_BY_REGEX)) {
				String param = urlMap.get(Constants.PRICE).replaceAll(Constants.GET_BY_REGEX+"\\(", "");
                String selector = param.substring(0, param.length()-1);
				vehicle.setPrice(ParsingUtils.getByRegexSelector(selector, getViewSource(doc.baseUri())));
			}
			if (vehicle.getPrice() == null && urlMap.get(Constants.PRICE).contains(Constants.SEARCH_ELEM_FRONT_END_TEXT)) {			
				Pattern pattern = Pattern.compile("(\\\"(.*?)\\\")");
				String text = urlMap.get(Constants.PRICE);
				Matcher matcher = pattern.matcher(text);
				String[] array = new String[3];
				int count=0;
				while(matcher.find()) {
					String matchStr = matcher.group(0).substring(1, matcher.group(0).length()-1);
					array[count]=matchStr;
					count++;
				}

				String searchedPriceElem = doc.select(array[0]).get(0).text();
				if(array[1].equals("null")) {
					int firstIndex= searchedPriceElem.indexOf(array[2]);
					String val = searchedPriceElem.substring(firstIndex-10, firstIndex);
					String sval = priceUtil.searchPrice(val);
					vehicle.setPrice(sval);
				}
				
				/*int firstIndex= searchedPriceElem.indexOf(array[1]);
				int lastIndex= searchedPriceElem.indexOf(array[2]);
				String val = searchedPriceElem.substring(firstIndex+array[1].length(), lastIndex);
				String sval = priceUtil.searchPrice(val);
				vehicle.setPrice(sval);*/
			}
			if(vehicle.getPrice() == null && urlMap.get(Constants.PRICE).contains(Constants.PRICE_IN_FRENCH)) {
				String priceParam = urlMap.get(Constants.PRICE).replaceAll("("+Constants.PRICE_IN_FRENCH+"|\\(|\\))", "");
				String searchedPrice = doc.select(priceParam).get(0).text();
				String val = priceUtil.priceWithDollarEnd(searchedPrice);
				vehicle.setPrice(val);
			}
			if (((vehicle.getPrice() == null) || vehicle.getPrice().isEmpty() || (vehicle.getPrice().equals("$"))) && (doc.select("[class~=^"+urlMap.get(Constants.PRICE)+"]").size() > 0)) {
				String searchedPrice = doc.select("[class~=^"+urlMap.get(Constants.PRICE)+"]").get(0).text().replace(".00", "").replaceAll("\\D+", "").trim();
				vehicle.setPrice("$"+searchedPrice);
			}
			if (((vehicle.getPrice() == null) || vehicle.getPrice().isEmpty() || (vehicle.getPrice().equals("$"))) && (doc.select("[id~=^"+urlMap.get(Constants.PRICE)+"]").size() > 0)) {
				String searchedPrice = doc.select("[id~=^"+urlMap.get(Constants.PRICE)+"]").get(0).text().replace(".00", "").replaceAll("\\D+", "").trim();
				vehicle.setPrice("$"+searchedPrice);
			}
			if (((vehicle.getPrice() == null) || vehicle.getPrice().isEmpty() || (vehicle.getPrice().equals("$"))) && (doc.select(urlMap.get(Constants.PRICE)).size() > 0)) {
				String searchedPrice = doc.select(urlMap.get(Constants.PRICE)).get(0).text().replace(".00", "").replaceAll("\\D+", "").trim();
				if(searchedPrice.length() <= 6) {
					vehicle.setPrice("$"+searchedPrice);
				} else {
					String priceStr = doc.select(urlMap.get(Constants.PRICE)).get(0).text();
					String val = priceUtil.searchPrice(priceStr);
					vehicle.setPrice("$"+val);
				}
			}
			if(((vehicle.getPrice() == null) || vehicle.getPrice().isEmpty() || vehicle.getPrice().equals("$")) && (doc.select(urlMap.get(Constants.PRICE)).size() > 0)){
				Element el = doc.select(urlMap.get(Constants.PRICE)).get(0);
				//String val = el.attr("value").replaceAll("(.00|\\s+|\\u00a0)", "").replaceAll("\\D+", "").trim();
				String val = el.attr("value").replace(".00", "").replaceAll("\\D+", "").trim();
				//Added for meta tag
				if (val == null || val.isEmpty())
					val = el.attr("content").replace(".00", "").replaceAll("\\D+", "").trim();
				vehicle.setPrice("$"+val);				
			}
			if(((vehicle.getPrice() == null) || vehicle.getPrice().isEmpty() || vehicle.getPrice().equals("$")) && (doc.select(urlMap.get(Constants.PRICE)).size() > 0)){
				String priceStr = doc.select(urlMap.get(Constants.PRICE)).get(0).text().trim();
				String val = priceUtil.searchPrice(priceStr);
				vehicle.setPrice(val);				
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getMileage(Document doc) {
		try {
			/*if ((vehicle.getMileage() == null) && (doc.select("body:contains("+urlMap.get(Constants.MILEAGE)+")").size() > 0)) {
				Elements elem = doc.getElementsMatchingOwnText(urlMap.get(Constants.MILEAGE));
				mileage = elem.get(0).text().replace(",", "").replaceAll("(^"+urlMap.get(Constants.MILEAGE)+")(:?)", "");
				vehicle.setMileage(mileage);
			} */
			if(vehicle.getMileage() == null && urlMap.get(Constants.MILEAGE).contains(Constants.SEARCH_MILEAGE)) {
				String param = urlMap.get(Constants.MILEAGE).replaceAll(Constants.SEARCH_MILEAGE+"\\(", "");
				String kmParam = param.substring(0, param.length()-1);
				String searchedMileage = doc.select(kmParam).get(0).text();
				String val = mileageUtil.searchMileage(searchedMileage);
				vehicle.setMileage(val);
			}
			if ((vehicle.getMileage() == null) && (doc.select("[class~=^"+urlMap.get(Constants.MILEAGE)+"]").size() > 0)) {
				mileage =  doc.select("[class~=^"+urlMap.get(Constants.MILEAGE)+"]").get(0).text().replace(",", "").replaceAll("\\D+", "").trim();
				vehicle.setMileage(mileage);
			}
			if ((vehicle.getMileage() == null) && (doc.select("[id~=^"+urlMap.get(Constants.MILEAGE)+"]").size() > 0)) {
				mileage =  doc.select("[id~=^"+urlMap.get(Constants.MILEAGE)+"]").get(0).text().replace(",", "").replaceAll("\\D+", "").trim();
				vehicle.setMileage(mileage);
			}
			if ((vehicle.getMileage() == null) && (doc.select(urlMap.get(Constants.MILEAGE)).size() > 0)) {
				mileage =  doc.select(urlMap.get(Constants.MILEAGE)).get(0).text().replace(",", "").replace(".00","").replaceAll("\\D+", "").trim();
				if(!(mileage.length() > 8))
					vehicle.setMileage(mileage);
				if(vehicle.getMileage() ==  null || vehicle.getMileage().isEmpty()) {
					mileage =  doc.select(urlMap.get(Constants.MILEAGE)).get(0).text();
					if(mileage != null && !mileage.isEmpty()) {
						String mile = mileageUtil.searchMileage(mileage);
						if(mile.matches("[0-9]+(,[0-9]+)*,?")) {
							vehicle.setMileage(mile);
						}						
					}			
					
				}
			}
			if(((vehicle.getMileage() == null) || vehicle.getMileage().isEmpty()) && (doc.select(urlMap.get(Constants.MILEAGE)).size() > 0)){
				Element el = doc.select(urlMap.get(Constants.MILEAGE)).get(0);
				String val = el.attr("value").replaceAll("(\\s+|\\u00a0)", "");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.attr("content").replaceAll("(\\s+|\\u00a0)", "");
				vehicle.setMileage(val);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void getLocation(Document doc) {
		try {			
			if ((vehicle.getLocation() == null) && (doc.select("div:contains("+urlMap.get(Constants.LOCATION)+")").size() > 0)) {
				if(doc.select("div:matches(^"+urlMap.get(Constants.LOCATION)+")").size() > 0) {
					location = doc.select("div:matches(^"+urlMap.get(Constants.LOCATION)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.LOCATION)+")(:?)", "").replace("|", "").trim();
					vehicle.setLocation(location);
				}
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if ((vehicle.getLocation() == null) && (doc.select("div:contains("+urlMap.get(Constants.LOCATION)+")").size() > 0)) {
				location = doc.select("div:contains("+urlMap.get(Constants.LOCATION)+")").last().text().replaceAll("(^"+urlMap.get(Constants.LOCATION)+")(:?)", "").replace("|", "").trim();
				vehicle.setLocation(location);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if ((vehicle.getLocation() == null) && (doc.select("[class~=^"+urlMap.get(Constants.LOCATION)+"]").size() > 0)) {
				location =  doc.select("[class~=^"+urlMap.get(Constants.LOCATION)+"]").get(0).text().replaceAll("(^"+urlMap.get(Constants.LOCATION)+")(:?)", "").replace("|", "").trim();
				vehicle.setLocation(location);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			if ((vehicle.getLocation() == null) && (doc.select("[id~=^"+urlMap.get(Constants.LOCATION)+"]").size() > 0)) {
				location =  doc.select("[id~=^"+urlMap.get(Constants.LOCATION)+"]").get(0).text().replaceAll("(^"+urlMap.get(Constants.LOCATION)+")(:?)", "").replace("|", "").trim();
				vehicle.setLocation(location);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if ((vehicle.getLocation() == null) && (doc.select(urlMap.get(Constants.LOCATION)).size() > 0)) {
				location =  doc.select(urlMap.get(Constants.LOCATION)).get(0).text().replaceAll("(^"+urlMap.get(Constants.LOCATION)+")(:?)", "").replace("|", "").trim();				
				vehicle.setLocation(location);
				}
			} catch(Exception e) {
					e.printStackTrace();
		}
		try {
			if ((vehicle.getLocation() == null) && (doc.select(urlMap.get(Constants.LOCATION)).size() > 0)) {
				//location =  doc.select(urlMap.get(Constants.LOCATION)).get(0).text().replaceAll("(^"+urlMap.get(Constants.LOCATION)+")(:?)", "").replace("|", "").trim();
				Element el =  doc.select(urlMap.get(Constants.LOCATION)).get(0);
				String attribute = urlMap.get(Constants.LOCATION).replaceAll("(\\[|\\])","");
				String val = el.attr(attribute);
				if(val != null || !val.isEmpty()) {
					vehicle.setLocation(val);
				}
				
				}
			} catch(Exception e) {
					e.printStackTrace();
		}
		try {
			if ((vehicle.getLocation() == null) && (urlMap.get(Constants.LOCATION) != null)) {
				vehicle.setLocation(urlMap.get(Constants.LOCATION));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void getStockNumber(Document doc) {
		try {
			if(((vehicle.getStockNumber() == null) || vehicle.getStockNumber().isEmpty()) && (doc.select(urlMap.get(Constants.STOCK_NUM)).size() > 0)){
				//String st = doc.select(urlMap.get(Constants.STOCK_NUM)).get(0).text().replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replace("|", "").trim();
				String st = doc.select(urlMap.get(Constants.STOCK_NUM)).get(0).text().replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replaceAll("(Stock|stock|:|#|Number|number|STK|stk|Inventaire|inventaire|STOCK|Stock Nb:)", "").trim();
				//String[] stArray = st.split(" ");
				vehicle.setStockNumber(st);
			}
			if(((vehicle.getStockNumber() == null) || vehicle.getStockNumber().isEmpty()) && (doc.getElementsByAttribute(urlMap.get(Constants.STOCK_NUM)).size() > 0)){
				String st = doc.getElementsByAttribute(urlMap.get(Constants.STOCK_NUM)).get(0).attr(urlMap.get(Constants.STOCK_NUM));
				vehicle.setStockNumber(st);				
			}
			if(((vehicle.getStockNumber() == null) || vehicle.getStockNumber().isEmpty()) && (doc.select(urlMap.get(Constants.STOCK_NUM)).size() > 0)){
				Element el = doc.select(urlMap.get(Constants.STOCK_NUM)).get(0);
				String val = el.attr("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.attr("content");
				vehicle.setStockNumber(val);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getVINumber(Document doc) {
		try {
			//Added for VIN from script tag
			if((vehicle.getVin() == null) && (urlMap.get(Constants.VIN).toLowerCase().contains(Constants.VIN_FROM_VIEW_SOURCE.toLowerCase()))) {
				String viewSource = getViewSource(doc.baseUri());
				int lastIndex = urlMap.get(Constants.VIN).lastIndexOf(")");
				String newStr =  urlMap.get(Constants.VIN).replace(String.valueOf(urlMap.get(Constants.VIN).charAt(lastIndex)),"");
				String[] linkStrArr = newStr.replace(Constants.VIN_FROM_VIEW_SOURCE+"(", "").split("\\^");
				Matcher matcher = Pattern.compile(Pattern.quote(linkStrArr[0]) + "(.*?)"+ Pattern.quote(linkStrArr[2])).matcher(viewSource);
				while(matcher.find()) {
					String val = matcher.group(0).replaceAll(linkStrArr[0], "").replace(linkStrArr[2], "");
					vehicle.setVin(val);
				}				
			}
			//Added for VIN in href
			if(((vehicle.getVin() == null) || vehicle.getVin().isEmpty()) && (doc.select(urlMap.get(Constants.VIN)).size() > 0)){
				Element el = doc.select(urlMap.get(Constants.VIN)).get(0);
				String hrefText = el.attr("href");
				if(hrefText.toLowerCase().contains("vin")) {
					int vinPos = hrefText.toLowerCase().indexOf("vin");
					int len = vinPos + 4;
					String val = hrefText.substring(len, len+17);
					vehicle.setVin(val);
				}
			}
			if((vehicle.getVin() == null) && (doc.select(urlMap.get(Constants.VIN)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.VIN)).get(0).text().replaceAll("(VIN|Vin|,|#|:|de série|\\u00a0|ser|SER|Ser)", "").trim();
				String[] stArray = st.split(" ");
				if (!(stArray[0].toUpperCase().contains("XXX")) && (stArray[0].length() > 2))
					vehicle.setVin(stArray[0]);				
			}
			if(((vehicle.getVin() == null) || vehicle.getVin().isEmpty()) && (doc.getElementsByAttribute(urlMap.get(Constants.VIN)).size() > 0)){
				String st = doc.getElementsByAttribute(urlMap.get(Constants.VIN)).get(0).attr(urlMap.get(Constants.VIN));
				if (!(st.toUpperCase().contains("XXX")) && (st.length() > 2))
					vehicle.setVin(st);				
			}
			if(((vehicle.getVin() == null) || vehicle.getVin().isEmpty()) && (doc.select(urlMap.get(Constants.VIN)).size() > 0)){
				Element el = doc.select(urlMap.get(Constants.VIN)).get(0);
				String val = el.attr("value");
				//Added for VIN in meta tag
				if (val == null || val.isEmpty()) {
					String str = el.attr("content").replace(".", "");
					String[] strArray = str.split(" ");
					for(int i=0; i < strArray.length; i++) {
						if(util.vinCheck(strArray[i])){
						val = strArray[i];
						break;
						}
					}
				}
				
				//Added for VIN in alt attr
				if (val == null || val.isEmpty())
				{	
					String str = el.attr("alt").replace(".", "");
					String[] strArray = str.split(" ");
					for(int i=0; i < strArray.length; i++) {
						if(util.vinCheck(strArray[i])){
						val = strArray[i];
						break;
						}
					}					
					 
				}
				//Added for VIN in title attr
				if (val == null || val.isEmpty())
				{	
					String str = el.attr("title").replace(".", "");
					String[] strArray = str.split(" ");
					for(int i=0; i < strArray.length; i++) {
						if(util.vinCheck(strArray[i])){
						val = strArray[i];
						break;
						}
					}					
					 
				}
				//Added for VIN in src attr
				if (val == null || val.isEmpty())
				{	
					String str = el.attr("src").replace(".", "");
					String[] strArray = str.split(" ");
					for(int i=0; i < strArray.length; i++) {
						if(util.vinCheck(strArray[i])){
						val = strArray[i];
						break;
						}
					}					
					 
				}
				if (!(val.toUpperCase().contains("XXX")) && (val.length() > 2))
					vehicle.setVin(val);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getFeatures(Document doc) {
        try {
               if((urlMap.get(Constants.VEHICLE_FEATURES) != null) && urlMap.get(Constants.VEHICLE_FEATURES).contains(Constants.GET_FIELD_TEXT_WITH_SPACE)) {
                     String param = urlMap.get(Constants.VEHICLE_FEATURES).replaceAll(Constants.GET_FIELD_TEXT_WITH_SPACE+"\\(", "");
                     String fieldValue = param.substring(0, param.length()-1);
                     String feature = getFieldTextWithSpace(doc , fieldValue);
                     vehicle.setVehicleFeatures(feature);
                     return;
               }
               if((urlMap.get(Constants.VEHICLE_FEATURES) != null) && (doc.select(urlMap.get(Constants.VEHICLE_FEATURES)).size() > 0)){
                            String st = doc.select(urlMap.get(Constants.VEHICLE_FEATURES)).text().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
                            vehicle.setVehicleFeatures(st);

               }
        } catch(Exception e) {
               e.printStackTrace();
        }
	}

	public void getFeaturesUsingSelenium(WebDriver driver) {
		try {
			if((urlMap.get(Constants.VEHICLE_FEATURES) != null) && urlMap.get(Constants.VEHICLE_FEATURES).contains(Constants.GET_ALL_ELEMENTS)) {
				Pattern pattern = Pattern.compile(""+Constants.GET_ALL_ELEMENTS+"\\((.*?)\\)");
				Matcher matcher = pattern.matcher(urlMap.get(Constants.VEHICLE_FEATURES));
				while(matcher.find()) {
					List<WebElement> list = driver.findElements(By.cssSelector(matcher.group(1)));
					StringBuilder featureString = new StringBuilder();
					for(WebElement element : list) {
						featureString.append(element.getText().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim());
						featureString.append(" ");
					}
					vehicle.setVehicleFeatures(featureString.toString());
					return;
				}
			}
			if((urlMap.get(Constants.VEHICLE_FEATURES) != null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.VEHICLE_FEATURES))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.VEHICLE_FEATURES))).get(0).getText().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
				vehicle.setVehicleFeatures(st);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getDescription(Document doc) {
		try {
			if((urlMap.get(Constants.VEHICLE_DESC) != null) && (doc.select(urlMap.get(Constants.VEHICLE_DESC)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.VEHICLE_DESC)).text().replaceAll("(\r\n|\r|\n|<br>|\\|)", " ").trim();
				//Added for meta tag
				if(st == null || st.isEmpty()) {
					Element el = doc.select(urlMap.get(Constants.VEHICLE_DESC)).get(0);
					st = el.attr("content").replaceAll("(\r\n|\r|\n|<br>|\\|)", " ").trim();
				}
				vehicle.setVehicleDesc(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getDealerInfo(Document doc){
		try {
			if ((urlMap.get(Constants.DEALER_NAME) != null) && (doc.select(urlMap.get(Constants.DEALER_NAME)).size() > 0)) {
				Element el = doc.select(urlMap.get(Constants.DEALER_NAME)).get(0);
				String val = el.text();
				//Added for hidden field
				if(val == null || val.isEmpty())
					val = el.attr("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.attr("content");
				
				vehicle.setDealerName(val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((urlMap.get(Constants.DEALER_NAME) != null) && (vehicle.getDealerName() == null)) {
			vehicle.setDealerName(urlMap.get(Constants.DEALER_NAME));
		}
		try {
			if ((urlMap.get(Constants.DEALER_CITY) != null) && (doc.select(urlMap.get(Constants.DEALER_CITY)).size() > 0)) {
				Element el = doc.select(urlMap.get(Constants.DEALER_CITY)).get(0);
				String val = el.text().replaceAll("(\\|)", "");
				if(val == null || val.isEmpty())
					val = el.attr("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.attr("content");
				vehicle.setDealerCity(val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((urlMap.get(Constants.DEALER_CITY) != null) && (vehicle.getDealerCity() == null)) {
			vehicle.setDealerCity(urlMap.get(Constants.DEALER_CITY));
		}
		try {
			if ((urlMap.get(Constants.DEALER_PROVINCE) != null) && (doc.select(urlMap.get(Constants.DEALER_PROVINCE)).size() > 0)) {
				Element el = doc.select(urlMap.get(Constants.DEALER_PROVINCE)).get(0);
				String val = el.text();
				if(val == null || val.isEmpty())
					val = el.attr("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.attr("content");
				vehicle.setDealerProvince(val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((urlMap.get(Constants.DEALER_PROVINCE) != null) && (vehicle.getDealerProvince() == null)) {
			vehicle.setDealerProvince(urlMap.get(Constants.DEALER_PROVINCE));
		}
		try {
			if ((urlMap.get(Constants.DEALER_POSTAL_CODE) != null) && (doc.select(urlMap.get(Constants.DEALER_POSTAL_CODE)).size() > 0)) {
				Element el = doc.select(urlMap.get(Constants.DEALER_POSTAL_CODE)).get(0);
				String val = el.text();
				if(val == null || val.isEmpty())
					val = el.attr("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.attr("content");
				vehicle.setDealerZipCode(val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((urlMap.get(Constants.DEALER_POSTAL_CODE) != null) && (vehicle.getDealerZipCode() == null)) {
			vehicle.setDealerZipCode(urlMap.get(Constants.DEALER_POSTAL_CODE));
		}
	}
	
	public void getPrivateDealerFlag() {
		if ((urlMap.get(Constants.DEALER_FLAG) != null) || (!urlMap.get(Constants.DEALER_FLAG).isEmpty())) {
			vehicle.setDealerFlag(urlMap.get(Constants.DEALER_FLAG));
		}
	}
	
	public void getTransUsingSelect(Document doc) {
		try {
			if((vehicle.getTransmission() == null) && (doc.select(urlMap.get(Constants.TRANSMISSION)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.TRANSMISSION)).get(0).text();
					vehicle.setTransmission(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getDrivetrainUsingSelect(Document doc) {
		try {
			if((vehicle.getDrivetrain() == null) && (doc.select(urlMap.get(Constants.DRIVETRAIN)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.DRIVETRAIN)).get(0).text();
					vehicle.setDrivetrain(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getFuelUsingSelect(Document doc) {
		try {
			if((vehicle.getFuel() == null) && (doc.select(urlMap.get(Constants.FUEL)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.FUEL)).get(0).text();
					vehicle.setFuel(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getEngineUsingSelect(Document doc) {
		try {
			if((vehicle.getEngine() == null) && (doc.select(urlMap.get(Constants.ENGINE)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.ENGINE)).get(0).text();
					vehicle.setEngine(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getBodyStyleUsingSelect(Document doc) {
		try {
			if((vehicle.getBodystyle() == null) && (doc.select(urlMap.get(Constants.BODYSTYLE)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.BODYSTYLE)).get(0).text();
					vehicle.setBodystyle(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getExtColourUsingSelect(Document doc) {
		try {
			if((vehicle.getExtColour() == null) && (doc.select(urlMap.get(Constants.EXTERIOR_COLOR)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.EXTERIOR_COLOR)).get(0).text();
					vehicle.setExtColour(st);			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getIntColourUsingSelect(Document doc) {
		try {
			if((vehicle.getIntColour() == null) && (doc.select(urlMap.get(Constants.INTERIOR_COLOR)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.INTERIOR_COLOR)).get(0).text();
					vehicle.setIntColour(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getMakeModelYearDetail(Document doc) {
		try {
			if((vehicle.getMakeModelYearDetail() == null) && urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL).contains(Constants.GET_FIELD_TEXT_WITH_SPACE)) {
                String param = urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL).replaceAll(Constants.GET_FIELD_TEXT_WITH_SPACE+"\\(", "");
                String fieldValue = param.substring(0, param.length()-1);
                String title = getFieldTextWithSpace(doc , fieldValue);
                vehicle.setMakeModelYearDetail(title);
                return;
			}
			if((vehicle.getMakeModelYearDetail() == null) && (doc.select(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL)).get(0).text().replaceAll("(\r\n|\r|\n|<br>|BI WEEKLY|Back to inventory|\\u00a0|\\|)", " ").trim();
				if(st == null || st.isEmpty()) {
					Element el = doc.select(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL)).get(0);
					st = el.attr("content").replaceAll("(\r\n|\r|\n|<br>|BI WEEKLY|Back to inventory|\\u00a0|\\|)", " ").trim();
				}
				vehicle.setMakeModelYearDetail(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public void getListingDate(Document doc) {
		try {
			if((vehicle.getListingDate() == null) && (doc.select(urlMap.get(Constants.LISTING_DATE)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.LISTING_DATE)).get(0).text();
					vehicle.setListingDate(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public void getProdImageUrl(Document doc) {
		try {
			if((vehicle.getImageUrl() == null) && (doc.select(urlMap.get(Constants.IMAGE_URL)).size() > 0)){
				Element wElem = doc.select(urlMap.get(Constants.IMAGE_URL)).get(0);
				if(vehicle.getImageUrl() == null || vehicle.getImageUrl().isEmpty()) {
					ExtractTextFromImage extractTextFromImage = new ExtractTextFromImage();
					//ImageToTextProjectApplication imageToTextProjectApplication = new ImageToTextProjectApplication();
					String st = wElem.attr("abs:src");
					boolean status = extractTextFromImage.getImageText(st);
					vehicle.setImageUrl(st);
					vehicle.setStatus(status);
				}
				
				if(vehicle.getImageUrl() == null || vehicle.getImageUrl().isEmpty()) {
					String st = wElem.attr("content");
					vehicle.setImageUrl(st);
				}
						
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTrimValue(Document doc) {
		try {
			if((vehicle.getTrim() == null) && (doc.select(urlMap.get(Constants.TRIM)).size() > 0)){
				Element elem = doc.select(urlMap.get(Constants.TRIM)).get(0);
				if(vehicle.getTrim() == null || vehicle.getTrim().isEmpty()) {
					String str = elem.text().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ");
					vehicle.setTrim(str);	

				}
				if(vehicle.getTrim() == null || vehicle.getTrim().isEmpty()) {
					String str = elem.attr("value").replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ");
					vehicle.setTrim(str);
				}
			}
			/*if((vehicle.getTrim() == null) && (doc.select(urlMap.get(Constants.TRIM)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.TRIM)).get(0).text();
					vehicle.setTrim(st);				
			}*/
				
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getVehicleStatus(Document doc) {
		try {
			if(doc.select(urlMap.get(Constants.VEHICLE_STATUS)).size() > 0){
				if(url.equalsIgnoreCase("http://burtonautodeals.com/inventory-detail.php?id=3183")||url.equalsIgnoreCase("http://burtonautodeals.com/inventory-detail.php?id=3321")) {
					System.out.println("sold");
				}
                Element el = doc.select(urlMap.get(Constants.VEHICLE_STATUS)).get(0);
                String soldMatchRegex = ".*\\b(sold|vendu)\\b.*";
                if(el.attr("src").toLowerCase().matches(soldMatchRegex)) {
                       vehicle.setStatus(true);
                }
                if(el.text().toLowerCase().matches(soldMatchRegex)) {
                       vehicle.setStatus(true);
                }
          }
		} catch(Exception e) {
			e.printStackTrace();
		}
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
	
	private String getURLSource(String url) {
		StringBuilder stringBuilder = new StringBuilder();
		try {
		URL urlObject = new URL(url);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8")))
        {
            String inputLine;
            
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            
        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
		
	}
	
	private String getViewSource(WebDriver driver) {
		String pageSource = null;
			try {
				pageSource = driver.getPageSource();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		return pageSource;
		
	}
	
	public String getFieldTextWithSpace(Document doc , String fieldToFetch) {
        String text = "";
        Elements tags = doc.select(fieldToFetch).select("*");
        for (Element tag : tags) {
               for (TextNode tn : tag.textNodes()) {
                     String tagText = tn.text().trim();
                     if (tagText.length() > 0) {
                            text += tagText + " ";
                     }
               }
        }
        return text;
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

	public WebDriver getSeleniumDriver() {
		return seleniumDriver;
	}

	public void setSeleniumDriver(WebDriver seleniumDriver) {
		this.seleniumDriver = seleniumDriver;
	}

	public abstract Vehicle getVehicleDetails();
	
}
