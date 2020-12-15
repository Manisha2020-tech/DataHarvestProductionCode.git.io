package com.audaharvest.parsers;

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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.audaharvest.common.CommonUtil;
import com.audaharvest.common.Matrix;
import com.audaharvest.common.WebDriverUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.model.Vehicle;
import com.audaharvest.services.MatrixService;
import com.audaharvest.services.fieldutils.MileageUtil;
import com.audaharvest.services.fieldutils.PriceUtil;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;


public abstract class ParserJS implements Callable<Vehicle>{
	
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
	//private String mileage = null;
	private String location = null;
	private String modelCode = null;
	private String salesNo = null;
	private String trim = null;
	
	private String tag;
	
	public ParserJS() {
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
				stockNumber = doc.select(tag+":matches(^"+urlMap.get(Constants.STOCK_NUM)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replace("|", "").trim();
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
				String mileage = doc.select(tag+":matches(^"+urlMap.get(Constants.MILEAGE)+")").get(0).text().replaceAll("(^"+urlMap.get(Constants.MILEAGE)+")(:?)", "").replace("|", "").replaceAll("\\D+", "").trim();
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
	
	public void getBlockDataByParentNode(WebDriver driver) {
		MatrixService matBuilder = new MatrixService();
		Map<String, Integer> paramsIndexMap = new HashMap<String, Integer>();
		String blockInString = null;
		String engineParent = null;
		String transParent = null;
		
		try {
			/*if((urlMap.get(Constants.BLOCK_DATA_FINDER) == null) || (urlMap.get(Constants.BLOCK_DATA_FINDER).isEmpty())) {
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
			} else {*/
				blockInString = driver.findElements(By.cssSelector(urlMap.get(Constants.BLOCK_DATA_FINDER))).get(0).getText();			
				for(String key : urlMapVehicleFeatures.values()) {
					if(blockInString.contains(key)) {
						int position = blockInString.indexOf(key);
						paramsIndexMap.put(key, position);
					}
				
				}
			//}
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
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replaceAll("(Stock|stock|:|#|Number|number|STK|stk|Inventaire|inventaire)", "").replace("|", "").trim();
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
	public void getMakeModelYearByBlock(WebDriver driver) {
		try {
			if(driver.findElements(By.cssSelector(urlMap.get(Constants.MANUFACTURER))).size() > 0) {
				String headerPara = driver.findElements(By.cssSelector(urlMap.get(Constants.MANUFACTURER))).get(0).getText();
				for (int i=0; i<makeArray.length;i++) {
					if(headerPara.contains(makeArray[i])) {
						vehicle.setMake(makeArray[i]);
						int makePos = headerPara.toLowerCase().indexOf(vehicle.getMake().toLowerCase());
						int len = makePos+makeArray[i].length();
						//String headerParaTruncated = headerPara.substring(len, headerPara.length()).replace("|", "").trim();
						String headerParaTruncated = headerPara.substring(len, headerPara.length()).replaceAll("(\r\n|\r|\n|,|<br>|\\u00a0|\\|)", " ").trim();
						String[] headerParaTruncatedArray = headerParaTruncated.split(" ");
						//vehicle.setModel(headerParaTruncatedArray[0].replaceAll("(\\u00a0)", ""));
						vehicle.setModel(headerParaTruncatedArray[0]);
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
	
	public void getMakeModelYear(WebDriver driver){
		try {
			List<WebElement> headerEl = null;
			if((urlMap.get(Constants.LOCALIZED_COUNTRY_CODE) != null) && (urlMap.get(Constants.LOCALIZED_COUNTRY_CODE).equals(Constants.FRANCE_CODE))) {
				for(int k=0; k<headerTags.length;k++) {
					for (int i=0; i<makeArray.length;i++) {
						if(driver.findElements(By.cssSelector(headerTags[k]+":matches(^"+makeArray[i]+")")).size() > 0) {
							headerEl = driver.findElements(By.cssSelector(headerTags[k]+":matches(^"+makeArray[i]+")"));
							if(headerEl.size() > 0 ){
								vehicle.setMake(makeArray[i]);
								break;
							}
						} else if (driver.findElements(By.cssSelector(headerTags[k]+":matches("+makeArray[i]+")")).size() > 0) {
							headerEl = driver.findElements(By.cssSelector(headerTags[k]+":matches("+makeArray[i]+")"));
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
					String headerPara = headerEl.get(0).getText().replaceAll(",", "");
					int makePos = headerPara.toLowerCase().indexOf(vehicle.getMake().toLowerCase());
					int len = makePos+vehicle.getMake().length();
					String headerParaTruncated = headerPara.substring(len, headerPara.length()).trim();
					String[] headerParaTruncatedArray = headerParaTruncated.split(" ");
					vehicle.setModel(headerParaTruncatedArray[0]);
					Pattern p = Pattern.compile("(\\d{4})");
					Matcher m = p.matcher(headerPara);
					while(m.find()) {
						if((Integer.parseInt(m.group(0)) >= minYear) && (Integer.parseInt(m.group(0)) <= maxYear)) {
							vehicle.setYear(m.group(0));
							break;
						}				
					}
				}
			} else {
				for(int k=0; k<headerTags.length;k++) {
					for (int i=0; i<makeArray.length;i++) {
						if(driver.findElements(By.cssSelector(headerTags[k]+":matches((^\\d{4})(\\s)("+makeArray[i]+"))")).size() > 0) {
							headerEl = driver.findElements(By.cssSelector(headerTags[k]+":matches((^\\d{4})(\\s)("+makeArray[i]+"))"));
							if(headerEl.size() > 0 ){
								vehicle.setMake(makeArray[i]);
								break;
							}
						} else if (driver.findElements(By.cssSelector(headerTags[k]+":matches((\\d{4})(\\s)("+makeArray[i]+"))")).size() > 0) {
							headerEl = driver.findElements(By.cssSelector(headerTags[k]+":matches((\\d{4})(\\s)("+makeArray[i]+"))"));
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
					String headerPara = headerEl.get(0).getText().replaceAll(",", "");
					int makePos = headerPara.toLowerCase().indexOf(vehicle.getMake().toLowerCase());
					int len = makePos+vehicle.getMake().length();
					String headerParaTruncated = headerPara.substring(len, headerPara.length()).replaceAll("(\\u00a0)", " ").trim();
					String[] headerParaTruncatedArray = headerParaTruncated.split(" ");
					vehicle.setModel(headerParaTruncatedArray[0]);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
				
	}
	
	public void getVehicleYear(WebDriver driver) {
		try {
			if(((vehicle.getYear() == null) || (vehicle.getYear().isEmpty())) && (driver.findElements(By.cssSelector(urlMap.get(Constants.YEAR))).size() > 0)) {
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.YEAR))).get(0).getText().replaceAll("\\D+", "");
				vehicle.setYear(st);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getVehicleMake(WebDriver driver) {
		try {
			if(((vehicle.getMake() == null) || (vehicle.getMake().isEmpty())) && (driver.findElements(By.cssSelector(urlMap.get(Constants.MANUFACTURER))).size() > 0)) {
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.MANUFACTURER))).get(0).getText().replaceAll("()", "");
				vehicle.setMake(st);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getVehicleModel(WebDriver driver) {
		try {
			if(((vehicle.getModel() == null) || (vehicle.getModel().isEmpty())) && (driver.findElements(By.cssSelector(urlMap.get(Constants.MODEL))).size() > 0)) {
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.MODEL))).get(0).getText();
				vehicle.setModel(st);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getSalesNo(WebDriver driver) {
		String phoneNo = null;
		
		try {			
			if ((vehicle.getSellerPhoneNo() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.SALES_NUMBER))).size() > 0)) {
				phoneNo =  driver.findElements(By.cssSelector(urlMap.get(Constants.SALES_NUMBER))).get(0).getText();
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
			/*if ((vehicle.getSellerPhoneNo() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.SALES_NUMBER))).size() > 0)) {
				phoneNo = driver.findElements(By.cssSelector(urlMap.get(Constants.SALES_NUMBER))).get(0).getAttribute(urlMap.get(Constants.SALES_NUMBER));
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}
			if (((vehicle.getSellerPhoneNo() == null) || (vehicle.getSellerPhoneNo().isEmpty())) && (driver.findElements(By.cssSelector(urlMap.get(Constants.SALES_NUMBER))).size() > 0)) {
				WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.SALES_NUMBER))).get(0);
				//Added for hidden fields
				phoneNo =  el.getAttribute("value");
				//Added for meta tag
				if(phoneNo == null || phoneNo.isEmpty())
					phoneNo = el.getAttribute("content");
				
				Iterator<PhoneNumberMatch> phoneNumExists = PhoneNumberUtil.getInstance().findNumbers(phoneNo, urlMap.get(Constants.COUNTRY_CODE)).iterator();
				while(phoneNumExists.hasNext()){
					salesNo = phoneNumExists.next().rawString();
					break;
				}
				vehicle.setSellerPhoneNo(salesNo);
			}*/
		try {
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
	
	public void getPriceDetail(WebDriver driver) {
		try {
			/*if((vehicle.getPrice() == null) || vehicle.getPrice().isEmpty() || (vehicle.getPrice().equals("$"))) {
				for (int i=0; i<newHtmlTags.length; i++) {
				Elements e = doc.select(newHtmlTags[i]+":matches(^"+urlMap.get(Constants.PRICE)+")");
				if(e.size() > 0) {
					String searchedPrice =  e.select(newHtmlTags[i]+":matches(^"+urlMap.get(Constants.PRICE)+")").get(0).text().replace(".00", "").replaceAll("\\D+", "").trim();
					vehicle.setPrice("$"+searchedPrice);
					break;
					}
				}
			}*/
			if(vehicle.getPrice() == null && urlMap.get(Constants.PRICE).contains(Constants.PRICE_IN_FRENCH)) {
				String priceParam = urlMap.get(Constants.PRICE).replaceAll("("+Constants.PRICE_IN_FRENCH+"|\\(|\\))", "");
				String searchedPrice = driver.findElements(By.cssSelector(priceParam)).get(0).getText();
				String val = priceUtil.priceWithDollarEnd(searchedPrice);
				vehicle.setPrice(val);
			}
			
			if (((vehicle.getPrice() == null) || vehicle.getPrice().isEmpty() || (vehicle.getPrice().equals("$"))) && (driver.findElements(By.cssSelector(urlMap.get(Constants.PRICE))).size() > 0)) {
				String searchedPrice = driver.findElements(By.cssSelector(urlMap.get(Constants.PRICE))).get(0).getText().replace(".00", "").replaceAll("\\D+", "").trim();
				if(searchedPrice.length() <= 6) {
					vehicle.setPrice("$"+searchedPrice);
				} else {
					String priceStr = driver.findElements(By.cssSelector(urlMap.get(Constants.PRICE))).get(0).getText();
					String val = priceUtil.searchPrice(priceStr);
					if(!val.isEmpty() && val != null)
						vehicle.setPrice("$"+val);
				}
			}
			if(((vehicle.getPrice() == null) || vehicle.getPrice().isEmpty() || vehicle.getPrice().equals("$")) && (driver.findElements(By.cssSelector(urlMap.get(Constants.PRICE))).size() > 0)){
				WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.PRICE))).get(0);
				//String val = el.attr("value").replaceAll("(.00|\\s+|\\u00a0)", "").replaceAll("\\D+", "").trim();
				String val = el.getAttribute("value").replace(".00", "").replaceAll("\\D+", "").trim();
				//Added for meta tag
				if (val == null || val.isEmpty())
					val = el.getAttribute("content").replace(".00", "").replaceAll("\\D+", "").trim();
				vehicle.setPrice("$"+val);				
			}
			

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getMileage(WebDriver driver) {
		try {
			if(vehicle.getMileage() == null && urlMap.get(Constants.MILEAGE).contains(Constants.SEARCH_MILEAGE)) {
				String param = urlMap.get(Constants.MILEAGE).replaceAll(Constants.SEARCH_MILEAGE+"\\(", "");
				String kmParam = param.substring(0, param.length()-1);				
				String searchedMileage = driver.findElements(By.cssSelector(kmParam)).get(0).getText();
				String val = mileageUtil.searchMileage(searchedMileage);
				vehicle.setMileage(val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if((vehicle.getMileage() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.MILEAGE))).size() > 0)) {
				String mileage =  driver.findElements(By.cssSelector(urlMap.get(Constants.MILEAGE))).get(0).getText();
				if(mileage != null && !mileage.isEmpty()) {
					String mile = mileageUtil.searchMileage(mileage);
					if(mile.matches("[0-9]+(,[0-9]+)*,?")) {
						vehicle.setMileage(mile);
					}						
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {			
			if ((vehicle.getMileage() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.MILEAGE))).size() > 0)) {
				String mileage =  driver.findElements(By.cssSelector(urlMap.get(Constants.MILEAGE))).get(0).getText().replace(",", "").replace(".00", "").replaceAll("\\D+", "").trim();
				if(!(mileage.length() > 8))
					vehicle.setMileage(mileage);
			}
			if(((vehicle.getMileage() == null) || vehicle.getMileage().isEmpty()) && (driver.findElements(By.cssSelector(urlMap.get(Constants.MILEAGE))).size() > 0)){
				WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.MILEAGE))).get(0);
				String val = el.getAttribute("value").replaceAll("(\\s+|\\u00a0)", "");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.getAttribute("content").replaceAll("(\\s+|\\u00a0)", "");
				vehicle.setMileage(val);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void getLocation(WebDriver driver) {
		try {			
			
			try {
				if ((vehicle.getLocation() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.LOCATION))).size() > 0)) {
					location =  driver.findElements(By.cssSelector(urlMap.get(Constants.LOCATION))).get(0).getText().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", "");
					vehicle.setLocation(location);
				}
			} catch(Exception e) {
					e.printStackTrace();
			}
			if ((vehicle.getLocation() == null) && (urlMap.get(Constants.LOCATION) != null)) {
				vehicle.setLocation(urlMap.get(Constants.LOCATION));
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void getStockNumber(WebDriver driver) {
		try {
			if(((vehicle.getStockNumber() == null) || vehicle.getStockNumber().isEmpty()) && (driver.findElements(By.cssSelector(urlMap.get(Constants.STOCK_NUM))).size() > 0)){
				//String st = doc.select(urlMap.get(Constants.STOCK_NUM)).get(0).text().replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replace("|", "").trim();
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.STOCK_NUM))).get(0).getText().replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replaceAll("(Stock|stock|:|#|Number|number|STK|stk|Inventaire|inventaire|STOCK)", "").trim();
				//String[] stArray = st.split(" ");
				vehicle.setStockNumber(st);
			}
			if(((vehicle.getStockNumber() == null) || vehicle.getStockNumber().isEmpty()) && (driver.findElements(By.cssSelector(urlMap.get(Constants.STOCK_NUM))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.STOCK_NUM))).get(0).getAttribute(urlMap.get(Constants.STOCK_NUM));
				vehicle.setStockNumber(st);				
			}
			if(((vehicle.getStockNumber() == null) || vehicle.getStockNumber().isEmpty()) && (driver.findElements(By.cssSelector(urlMap.get(Constants.STOCK_NUM))).size() > 0)){
				WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.STOCK_NUM))).get(0);
				String val = el.getAttribute("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.getAttribute("content");
				vehicle.setStockNumber(val);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getVINumber(WebDriver driver) {
		try {
			//Added for VIN in href
			try {
				if(((vehicle.getVin() == null) || vehicle.getVin().isEmpty()) && (driver.findElements(By.cssSelector(urlMap.get(Constants.VIN))).size() > 0)){
					WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.VIN))).get(0);
					String hrefText = el.getAttribute("href");
					if(hrefText.toLowerCase().contains("vin")) {
						int vinPos = hrefText.toLowerCase().indexOf("vin");
						int len = vinPos + 4;
						String val = hrefText.substring(len, len+17);
						vehicle.setVin(val);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if((vehicle.getVin() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.VIN))).size() > 0)){
					String st = driver.findElements(By.cssSelector(urlMap.get(Constants.VIN))).get(0).getText().replaceAll("(VIN|Vin|,|#|:|de série|\\u00a0)", "").trim();
					String[] stArray = st.split(" ");
					if (!(stArray[0].toUpperCase().contains("XXX")) && (stArray[0].length() > 2))
						vehicle.setVin(stArray[0]);				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if(((vehicle.getVin() == null) || vehicle.getVin().isEmpty()) && (driver.findElements(By.cssSelector(urlMap.get(Constants.VIN))).size() > 0)){
					String st = driver.findElements(By.cssSelector(urlMap.get(Constants.VIN))).get(0).getAttribute((urlMap.get(Constants.VIN)));
					if (!(st.toUpperCase().contains("XXX")) && (st.length() > 2))
						vehicle.setVin(st);				
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(((vehicle.getVin() == null) || vehicle.getVin().isEmpty()) && (driver.findElements(By.cssSelector(urlMap.get(Constants.VIN))).size() > 0)){
				WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.VIN))).get(0);
				String val = el.getAttribute("value");
				//Added for VIN in meta tag
				if (val == null || val.isEmpty()) {
					String str = el.getAttribute("content");
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
					String str = el.getAttribute("alt");
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
					String str = el.getAttribute("title");
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
					String str = el.getAttribute("src");
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
	
	public void getFeatures(WebDriver driver) {
		try {
			if((urlMap.get(Constants.VEHICLE_FEATURES) != null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.VEHICLE_FEATURES))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.VEHICLE_FEATURES))).get(0).getText().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
				vehicle.setVehicleFeatures(st);
			}
			if((vehicle.getVehicleFeatures() != null) &&(urlMap.get(Constants.VEHICLE_FEATURES) != null) && (driver.findElements(By.xpath(urlMap.get(Constants.VEHICLE_FEATURES))).size() > 0)){
				String st = driver.findElements(By.xpath(urlMap.get(Constants.VEHICLE_FEATURES))).get(0).getText().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
				vehicle.setVehicleFeatures(st);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getDescription(WebDriver driver) {
		try {
			if((urlMap.get(Constants.VEHICLE_DESC) != null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.VEHICLE_DESC))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.VEHICLE_DESC))).get(0).getText().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
				//Added for meta tag
				if(st == null || st.isEmpty()) {
					WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.VEHICLE_DESC))).get(0);
					st = el.getAttribute("content").replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
				}
				vehicle.setVehicleDesc(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getDealerInfo(WebDriver driver){
		try {
			if ((urlMap.get(Constants.DEALER_NAME) != null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.DEALER_NAME))).size() > 0)) {
				WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.DEALER_NAME))).get(0);
				String val = el.getText();
				//Added for hidden field
				if(val == null || val.isEmpty())
					val = el.getAttribute("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.getAttribute("content");
				
				vehicle.setDealerName(val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((urlMap.get(Constants.DEALER_NAME) != null) && (vehicle.getDealerName() == null)) {
			vehicle.setDealerName(urlMap.get(Constants.DEALER_NAME));
		}
		try {
			if ((urlMap.get(Constants.DEALER_CITY) != null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.DEALER_CITY))).size() > 0)) {
				WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.DEALER_CITY))).get(0);
				String val = el.getText();
				if(val == null || val.isEmpty())
					val = el.getAttribute("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.getAttribute("content");
				vehicle.setDealerCity(val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((urlMap.get(Constants.DEALER_CITY) != null) && (vehicle.getDealerCity() == null)) {
			vehicle.setDealerCity(urlMap.get(Constants.DEALER_CITY));
		}
		try {
			if ((urlMap.get(Constants.DEALER_PROVINCE) != null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.DEALER_PROVINCE))).size() > 0)) {
				WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.DEALER_PROVINCE))).get(0);
				String val = el.getText();
				if(val == null || val.isEmpty())
					val = el.getAttribute("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.getAttribute("content");
				vehicle.setDealerProvince(val);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((urlMap.get(Constants.DEALER_PROVINCE) != null) && (vehicle.getDealerProvince() == null)) {
			vehicle.setDealerProvince(urlMap.get(Constants.DEALER_PROVINCE));
		}
		try {
			if ((urlMap.get(Constants.DEALER_POSTAL_CODE) != null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.DEALER_POSTAL_CODE))).size() > 0)) {
				WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.DEALER_POSTAL_CODE))).get(0);
				String val = el.getText();
				if(val == null || val.isEmpty())
					val = el.getAttribute("value");
				//Added for meta tag
				if(val == null || val.isEmpty())
					val = el.getAttribute("content");
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
	
	public void getTransUsingSelect(WebDriver driver) {
		try {
			if((vehicle.getTransmission() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.TRANSMISSION))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.TRANSMISSION))).get(0).getText();
					vehicle.setTransmission(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getEngineUsingSelect(WebDriver driver) {
		try {
			if((vehicle.getEngine() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.ENGINE))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.ENGINE))).get(0).getText();
					vehicle.setEngine(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getBodyStyleUsingSelect(WebDriver driver) {
		try {
			if((vehicle.getBodystyle() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.BODYSTYLE))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.BODYSTYLE))).get(0).getText();
					vehicle.setBodystyle(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getExtColourUsingSelect(WebDriver driver) {
		try {
			if((vehicle.getExtColour() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.EXTERIOR_COLOR))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.EXTERIOR_COLOR))).get(0).getText();
					vehicle.setExtColour(st);			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getIntColourUsingSelect(WebDriver driver) {
		try {
			if((vehicle.getIntColour() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.INTERIOR_COLOR))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.INTERIOR_COLOR))).get(0).getText();
					vehicle.setIntColour(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getDoorsUsingSelect(WebDriver driver) {
		try {
			if((vehicle.getDoors() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.DOORS))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.DOORS))).get(0).getText();
					vehicle.setDoors(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getPassengersUsingSelect(WebDriver driver) {
		try {
			if((vehicle.getPassengers() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.PASSENGERS))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.PASSENGERS))).get(0).getText();
					vehicle.setPassengers(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getMakeModelYearDetail(WebDriver driver) {
		try {
			if((vehicle.getMakeModelYearDetail() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL))).get(0).getText().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
				if(st == null || st.isEmpty()) {
					WebElement el = driver.findElements(By.cssSelector(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL))).get(0);
					st = el.getAttribute("content").replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
				}
				vehicle.setMakeModelYearDetail(st);	
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getListingDate(WebDriver driver) {
		try {
			if((vehicle.getListingDate() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.LISTING_DATE))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.LISTING_DATE))).get(0).getText();
					vehicle.setListingDate(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getTrimValue(WebDriver driver) {		
		try {
			if((vehicle.getTrim() == null) && (driver.findElements(By.cssSelector(urlMap.get(Constants.TRIM))).size() > 0)){
				String st = driver.findElements(By.cssSelector(urlMap.get(Constants.TRIM))).get(0).getText().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ");
					vehicle.setTrim(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
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

	public WebDriver getSeleniumDriver() {
		return seleniumDriver;
	}

	public void setSeleniumDriver(WebDriver seleniumDriver) {
		this.seleniumDriver = seleniumDriver;
	}

	public abstract Vehicle getVehicleDetails();
	
}
