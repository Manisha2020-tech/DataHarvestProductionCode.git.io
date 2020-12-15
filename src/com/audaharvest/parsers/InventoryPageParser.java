package com.audaharvest.parsers;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.audaharvest.common.CommonUtil;
import com.audaharvest.common.Matrix;
import com.audaharvest.constants.Constants;
import com.audaharvest.iservices.IVehicleService;
import com.audaharvest.model.Vehicle;
import com.audaharvest.services.MatrixService;
import com.audaharvest.services.fieldutils.MileageUtil;
import com.audaharvest.services.fieldutils.PriceUtil;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

public class InventoryPageParser {
	CommonUtil util = new CommonUtil();
	PriceUtil priceValidator = new PriceUtil();
	MileageUtil mileageUtil = new MileageUtil();
	private String[] makeArray;
	
	private int minYear = Integer.parseInt(Constants.MIN_YEAR);
	private int maxYear = Calendar.getInstance().get(Calendar.YEAR) + 1 ;
	
	public List<Vehicle> getVehicleDatas(String url, Map<String, String> urlMap, String[] manufacCodeArray) {
		Document doc = null;
		
		makeArray = manufacCodeArray;
		int size = 0;
		if((urlMap.get(Constants.GROUP).equals("G3")) && (urlMap.get(Constants.DOWNLOAD_PAGE) != null) &&(urlMap.get(Constants.DOWNLOAD_PAGE).equals("Download"))) {			
			File input1 = null;
			File input2 = null;
			String pageName = url;
			String downloadedFilePathHTML = System.getProperty("user.home") + "\\"+pageName+".html";
			String downloadedFilePathHTM = System.getProperty("user.home") + "\\"+pageName+".htm";

			try {
				input1 = new File(downloadedFilePathHTML);
				if(input1.exists()) {
					doc = Jsoup.parse(input1, "UTF-8");
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			try {
				input2 = new File(downloadedFilePathHTM);
				if(input2.exists()) {
					doc = Jsoup.parse(input2, "UTF-8");
				}
				size = doc.select(urlMap.get(Constants.BLOCK_DATA_FINDER)).size();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else if((urlMap.get(Constants.GROUP).equals("G3"))) {
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
		}		
		
		List<Vehicle> vehicleList = new ArrayList<Vehicle>();
		for (int block=0; block<size; block++) {
			String html= doc.select(urlMap.get(Constants.BLOCK_DATA_FINDER)).get(block).outerHtml();
			Document doc2 = Jsoup.parse(html);
			Vehicle vehicle = new Vehicle();
			vehicle.setUrl(urlMap.get(Constants.URL));
			vehicle.setDomain(urlMap.get(Constants.ROOT_URL));
			getBlockDataVal(doc2, vehicle, urlMap);
			if(vehicle.getMake() == null || vehicle.getMake().isEmpty())
				getMakeVal(doc2, vehicle, urlMap);
			if(vehicle.getModel() == null || vehicle.getModel().isEmpty())
				getModelVal(doc2, vehicle, urlMap);
			if(vehicle.getYear() == null || vehicle.getYear().isEmpty())
				getYearVal(doc2, vehicle, urlMap);
			if(vehicle.getSellerPhoneNo() == null || vehicle.getSellerPhoneNo().isEmpty())
				getSalesNoVal(doc, vehicle, urlMap);
			if(vehicle.getTrim() == null || vehicle.getTrim().isEmpty())
				getTrimVal(doc2, vehicle, urlMap);
			if(vehicle.getTransmission() == null || vehicle.getTransmission().isEmpty())
				getTransVal(doc2, vehicle, urlMap);
			if(vehicle.getEngine() == null || vehicle.getEngine().isEmpty())
				getEngineVal(doc2, vehicle, urlMap);
			if(vehicle.getPrice() == null || vehicle.getPrice().isEmpty())
				getPriceVal(doc2, vehicle, urlMap);
			if(vehicle.getMileage() == null || vehicle.getMileage().isEmpty())
				getMileageVal(doc2, vehicle, urlMap);			
			if(vehicle.getStockNumber() == null || vehicle.getStockNumber().isEmpty())
				getStockVal(doc2, vehicle, urlMap);
			if(vehicle.getVin() == null || vehicle.getVin().isEmpty())
				getVINVal(doc2, vehicle, urlMap);
			if(vehicle.getVehicleFeatures() == null || vehicle.getVehicleFeatures().isEmpty())
				getFeaturesVal(doc2, vehicle, urlMap);
			if(vehicle.getLocation() == null || vehicle.getLocation().isEmpty())
				getLocationVal(doc, vehicle, urlMap);
			if(vehicle.getVehicleDesc() == null || vehicle.getVehicleDesc().isEmpty())
				getDescVal(doc2, vehicle, urlMap);
			if(vehicle.getBodystyle() == null || vehicle.getBodystyle().isEmpty())
				getBodyStyleVal(doc2, vehicle, urlMap);
			if(vehicle.getExtColour() == null || vehicle.getExtColour().isEmpty())
				getExtColourVal(doc2, vehicle, urlMap);
			if(vehicle.getIntColour() == null || vehicle.getIntColour().isEmpty())
				getIntColourVal(doc2, vehicle, urlMap);
			if(vehicle.getDoors() == null || vehicle.getDoors().isEmpty()) {
				getDoorVal(doc2,vehicle,urlMap);
			}
			if(vehicle.getDrivetrain() == null || vehicle.getDrivetrain().isEmpty()){
				getDrivetrainVal(doc2,vehicle,urlMap);
			}
			getDealerDetailsVal(doc2, vehicle, urlMap);
			getPrivateDealerFlagVal(vehicle, urlMap);
			getMakeModelYearDetailVal(doc2, vehicle, urlMap);
			if(vehicle.getMake() == null || vehicle.getMake().isEmpty()) {				
				getMakeValWithSize(doc, vehicle, urlMap, block);
				
			}
			if(vehicle.getMakeModelYearDetail() == null || vehicle.getMakeModelYearDetail().isEmpty()) {	
				getMakeModelYearDetailValWithSize(doc, vehicle, urlMap, block);
			}
			vehicleList.add(vehicle);
		}
		return vehicleList;
	}
	
	public void getBlockDataVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		Map<String, String> urlMapTruncated = vehicleFeatureTextsFromUrlMap(urlMap);
		Map<String, Integer> paramsIndexMap = new HashMap<String, Integer>();
		String blockInString = null;
		
		try {
			if((urlMap.get(Constants.BLOCK_DATA_FINDER) != null) && (!urlMap.get(Constants.BLOCK_DATA_FINDER).isEmpty())) {
				blockInString = doc.select(urlMap.get(Constants.BLOCK_DATA_FINDER)).get(0).text();			
				for(String key : urlMapTruncated.values()) {
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
					String newStr = tempStr.substring(val, util.nextMaxInSortedArray(val, arrays)).replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replaceAll("(Stock|stock|:|#|Number|number|STK|stk|Inventaire|inventaire|\\u00a0|\\|)", "").trim();
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
	
	public void getMakeVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if(doc.select(urlMap.get(Constants.MANUFACTURER)).size() > 0) {
				String headerPara = doc.select(urlMap.get(Constants.MANUFACTURER)).get(0).text();
				for (int i=0; i<makeArray.length;i++) {
					if(headerPara.toLowerCase().contains(makeArray[i].toLowerCase())) {
						vehicle.setMake(makeArray[i]);
						int makePos = headerPara.toLowerCase().indexOf(vehicle.getMake().toLowerCase());
						int len = makePos+makeArray[i].length();
						String headerParaTruncated = headerPara.substring(len, headerPara.length()).trim();
						String[] headerParaTruncatedArray = headerParaTruncated.split(" ");
						//vehicle.setModel(headerParaTruncatedArray[0]);
						vehicle.setModel(headerParaTruncatedArray[0].replaceAll("(\\u00a0)", ""));
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
		try {
			if(((vehicle.getMake() == null) || (vehicle.getMake().isEmpty())) && (doc.select(urlMap.get(Constants.MANUFACTURER)).size() > 0)) {
				String st = doc.select(urlMap.get(Constants.MANUFACTURER)).get(0).text().replaceAll("()", "");
				vehicle.setMake(st);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getMakeValWithSize(Document doc, Vehicle vehicle, Map<String, String> urlMap, int j) {
		try {
			if(doc.select(urlMap.get(Constants.MANUFACTURER)).size() > 0) {
				String headerPara = doc.select(urlMap.get(Constants.MANUFACTURER)).get(j).text();
				for (int i=0; i<makeArray.length;i++) {
					if(headerPara.toLowerCase().contains(makeArray[i].toLowerCase())) {
						vehicle.setMake(makeArray[i]);
						int makePos = headerPara.toLowerCase().indexOf(vehicle.getMake().toLowerCase());
						int len = makePos+makeArray[i].length();
						String headerParaTruncated = headerPara.substring(len, headerPara.length()).trim();
						String[] headerParaTruncatedArray = headerParaTruncated.split(" ");
						//vehicle.setModel(headerParaTruncatedArray[0]);
						vehicle.setModel(headerParaTruncatedArray[0].replaceAll("(\\u00a0)", ""));
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
		/*try {
			if(((vehicle.getMake() == null) || (vehicle.getMake().isEmpty())) && (doc.select(urlMap.get(Constants.MANUFACTURER)).size() > 0)) {
				String st = doc.select(urlMap.get(Constants.MANUFACTURER)).get(0).text().replaceAll("()", "");
				vehicle.setMake(st);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	public void getModelVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if(((vehicle.getModel() == null) || (vehicle.getModel().isEmpty())) && (doc.select(urlMap.get(Constants.MODEL)).size() > 0)) {
				String st = doc.select(urlMap.get(Constants.MODEL)).get(0).text();
				vehicle.setModel(st);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	public void getYearVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if(((vehicle.getYear() == null) || (vehicle.getYear().isEmpty())) && (doc.select(urlMap.get(Constants.YEAR)).size() > 0)) {
				//String st = doc.select(urlMap.get(Constants.YEAR)).get(0).text().replaceAll("\\D+", "");
				String st = doc.select(urlMap.get(Constants.YEAR)).get(0).text();
				Pattern p = Pattern.compile("(\\d{4})");
				Matcher m = p.matcher(st);
				while(m.find()) {
					if((Integer.parseInt(m.group(0)) >= minYear) && (Integer.parseInt(m.group(0)) <= maxYear)) {
						vehicle.setYear(m.group(0));
						break;
					}				
				}
				//vehicle.setYear(st);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void getSalesNoVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {		
		String phoneNo = null;
		String salesNo = null;
		
		try {			
			if ((vehicle.getSellerPhoneNo() == null) && (doc.select(urlMap.get(Constants.SALES_NUMBER)).size() > 0)) {
				phoneNo =  doc.select(urlMap.get(Constants.SALES_NUMBER)).get(0).text();
				
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
	
	public void getTrimVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((vehicle.getTrim() == null) && (doc.select(urlMap.get(Constants.TRIM)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.TRIM)).get(0).text();
					vehicle.setTrim(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getTransVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((vehicle.getTransmission() == null) && (doc.select(urlMap.get(Constants.TRANSMISSION)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.TRANSMISSION)).get(0).text();
					vehicle.setTransmission(st);				
			}
			if(vehicle.getTransmission() == null || vehicle.getTransmission().isEmpty()) {
				Element el = doc.select(urlMap.get(Constants.TRANSMISSION)).get(0);
				String val =  el.attr("content");
				vehicle.setTransmission(val);
			 }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getEngineVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((vehicle.getEngine() == null) && (doc.select(urlMap.get(Constants.ENGINE)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.ENGINE)).get(0).text();
					vehicle.setEngine(st);
				if(vehicle.getEngine() == null || vehicle.getEngine().isEmpty()) {
					Element el = doc.select(urlMap.get(Constants.ENGINE)).get(0);
					String val =  el.attr("content");
					vehicle.setEngine(val);
				 }
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getPriceVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {			
			if (((vehicle.getPrice() == null) || vehicle.getPrice().isEmpty() || (vehicle.getPrice().equals("$"))) && (doc.select(urlMap.get(Constants.PRICE)).size() > 0)) {
				String searchedPrice = doc.select(urlMap.get(Constants.PRICE)).get(0).text().replace(".00", "").replaceAll("\\D+", "").trim();
				if(searchedPrice.length() <= 6) {
					vehicle.setPrice("$"+searchedPrice);
				} else {
					String priceStr = doc.select(urlMap.get(Constants.PRICE)).get(0).text();
					String val = priceValidator.searchPrice(priceStr);
					vehicle.setPrice("$"+val);
				}
				if(vehicle.getPrice() == null || vehicle.getPrice().equals("$")) {
					Element el = doc.select(urlMap.get(Constants.PRICE)).get(0);
					String val = el.attr("value").replace(".00", "").replaceAll("\\D+", "").trim();
					//Added for meta tag
					if (val == null || val.isEmpty())
						val = el.attr("content").replace(".00", "").replaceAll("\\D+", "").trim();
					vehicle.setPrice("$"+val);
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	public void getMileageVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if(vehicle.getMileage() == null && urlMap.get(Constants.MILEAGE).contains(Constants.SEARCH_MILEAGE)) {
				String kmParam = urlMap.get(Constants.MILEAGE).replaceAll("("+Constants.SEARCH_MILEAGE+"|\\(|\\))", "");
				String searchedMileage = doc.select(kmParam).get(0).text();
				String val = mileageUtil.searchMileage(searchedMileage);
				vehicle.setMileage(val);
			}
			if ((vehicle.getMileage() == null) && (doc.select(urlMap.get(Constants.MILEAGE)).size() > 0)) {
				String mileage =  doc.select(urlMap.get(Constants.MILEAGE)).get(0).text().replace(",", "").replaceAll("\\D+", "").trim();
				vehicle.setMileage(mileage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void getLocationVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {			
			try {
				if ((vehicle.getLocation() == null) && (doc.select(urlMap.get(Constants.LOCATION)).size() > 0)) {
					String location =  doc.select(urlMap.get(Constants.LOCATION)).get(0).text().replaceAll("(^"+urlMap.get(Constants.LOCATION)+")(:?)", "").replace("|", "").trim();
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
	public void getStockVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if(((vehicle.getStockNumber() == null) || vehicle.getStockNumber().isEmpty()) && (doc.select(urlMap.get(Constants.STOCK_NUM)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.STOCK_NUM)).get(0).text().replaceAll("(^"+urlMap.get(Constants.STOCK_NUM)+")(:?)", "").replaceAll("(Stock|stock|:|#|Number|number|STK|stk|Inventaire|inventaire|STOCK|\\u00a0)", "").trim();
				vehicle.setStockNumber(st);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getVINVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((vehicle.getVin() == null) && (doc.select(urlMap.get(Constants.VIN)).size() > 0)){				
				Element el = doc.select(urlMap.get(Constants.VIN)).get(0);
				String idVal = el.attr("id");
				String val = idVal.replaceAll("(VIN|Vin|vin|-|,|#|:)", "").trim();
				vehicle.setVin(val);				
			}
			if((vehicle.getVin() == null || vehicle.getVin().isEmpty()) && (doc.select(urlMap.get(Constants.VIN)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.VIN)).get(0).text().replaceAll("(VIN|Vin|,|#|:)", "").trim();
				String[] stArray = st.split(" ");
				if(util.vinCheck(stArray[0]))
					vehicle.setVin(stArray[0]);				
			}
			if(vehicle.getVin() == null || vehicle.getVin().isEmpty()) {
				Element el = doc.select(urlMap.get(Constants.VIN)).get(0);
				String val =  el.attr("content");
				vehicle.setVin(val);
			 }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getDrivetrainVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((vehicle.getDrivetrain() == null || vehicle.getDrivetrain().isEmpty()) && (doc.select(urlMap.get(Constants.DRIVETRAIN)).size() > 0)){				
				String val = doc.select(urlMap.get(Constants.DRIVETRAIN)).get(0).text().trim();
				vehicle.setDrivetrain(val);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getFeaturesVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((urlMap.get(Constants.VEHICLE_FEATURES) != null) && (doc.select(urlMap.get(Constants.VEHICLE_FEATURES)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.VEHICLE_FEATURES)).text().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
				vehicle.setVehicleFeatures(st);			
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getDescVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((urlMap.get(Constants.VEHICLE_DESC) != null) && (doc.select(urlMap.get(Constants.VEHICLE_DESC)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.VEHICLE_DESC)).get(0).text().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
				vehicle.setVehicleDesc(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getDealerDetailsVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
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
				String val = el.text();
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
	public void getPrivateDealerFlagVal(Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if ((urlMap.get(Constants.DEALER_FLAG) != null) || (!urlMap.get(Constants.DEALER_FLAG).isEmpty())) {
				vehicle.setDealerFlag(urlMap.get(Constants.DEALER_FLAG));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getBodyStyleVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((vehicle.getBodystyle() == null) && (doc.select(urlMap.get(Constants.BODYSTYLE)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.BODYSTYLE)).get(0).text();
					vehicle.setBodystyle(st);				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getExtColourVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((vehicle.getExtColour() == null) && (doc.select(urlMap.get(Constants.EXTERIOR_COLOR)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.EXTERIOR_COLOR)).get(0).text();
					vehicle.setExtColour(st);			
			}
			if(vehicle.getExtColour() == null || vehicle.getExtColour().isEmpty()) {
				Element el = doc.select(urlMap.get(Constants.EXTERIOR_COLOR)).get(0);
				String val =  el.attr("content");
				vehicle.setExtColour(val);
			 }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getIntColourVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((vehicle.getIntColour() == null) && (doc.select(urlMap.get(Constants.INTERIOR_COLOR)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.INTERIOR_COLOR)).get(0).text();
					vehicle.setIntColour(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getDoorVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if(vehicle.getDoors() == null || vehicle.getDoors().isEmpty()) {
				Element el = doc.select(urlMap.get(Constants.DOORS)).get(0);
				String val =  el.attr("content");
				vehicle.setDoors(val);
			 }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void getMakeModelYearDetailVal(Document doc, Vehicle vehicle, Map<String, String> urlMap) {
		try {
			if((vehicle.getMakeModelYearDetail() == null) && (doc.select(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL)).get(0).text().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
					vehicle.setMakeModelYearDetail(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void getMakeModelYearDetailValWithSize(Document doc, Vehicle vehicle, Map<String, String> urlMap, int j) {
		try {
			if((vehicle.getMakeModelYearDetail() == null) && (doc.select(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL)).size() > 0)){
				String st = doc.select(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL)).get(j).text().replaceAll("(\r\n|\r|\n|<br>|\\u00a0|\\|)", " ").trim();
					vehicle.setMakeModelYearDetail(st);		
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public Map<String, String> vehicleFeatureTextsFromUrlMap(Map<String, String> urlMap) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(Constants.MANUFACTURER, urlMap.get(Constants.MANUFACTURER));
		map.put(Constants.MODEL, urlMap.get(Constants.MODEL));
		map.put(Constants.YEAR, urlMap.get(Constants.YEAR));	
		map.put(Constants.BODYSTYLE, urlMap.get(Constants.BODYSTYLE));
		map.put(Constants.ENGINE, urlMap.get(Constants.ENGINE));
		map.put(Constants.TRANSMISSION, urlMap.get(Constants.TRANSMISSION));		
		map.put(Constants.DRIVETRAIN, urlMap.get(Constants.DRIVETRAIN));
		map.put(Constants.EXTERIOR_COLOR, urlMap.get(Constants.EXTERIOR_COLOR));
		map.put(Constants.INTERIOR_COLOR, urlMap.get(Constants.INTERIOR_COLOR));
		map.put(Constants.FUEL, urlMap.get(Constants.FUEL));
		map.put(Constants.PRICE, urlMap.get(Constants.PRICE));
		map.put(Constants.STOCK_NUM, urlMap.get(Constants.STOCK_NUM));
		map.put(Constants.VIN, urlMap.get(Constants.VIN));
		map.put(Constants.LOCATION, urlMap.get(Constants.LOCATION));
		map.put(Constants.MILEAGE, urlMap.get(Constants.MILEAGE));
		map.put(Constants.MODEL_CODE, urlMap.get(Constants.MODEL_CODE));
		if(urlMap.get(Constants.TRIM)!= null && !urlMap.get(Constants.TRIM).isEmpty())
			map.put(Constants.TRIM, urlMap.get(Constants.TRIM));
		map.put(Constants.DOORS, urlMap.get(Constants.DOORS));
		map.put(Constants.PASSENGERS, urlMap.get(Constants.PASSENGERS));
		map.put(Constants.SALES_NUMBER, urlMap.get(Constants.SALES_NUMBER));
		if(urlMap.get(Constants.UNWANTED_BLOCK_DATA)!= null && !urlMap.get(Constants.UNWANTED_BLOCK_DATA).isEmpty()) {
			String unwantedBlockData = urlMap.get(Constants.UNWANTED_BLOCK_DATA);
			String[] array = unwantedBlockData.split("\\^");
			for (int i=0; i<array.length; i++) {
				map.put(array[i], array[i]);
			}
		}
		return map;
	}
}
