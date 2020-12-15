package com.audaharvest.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.audaharvest.constants.Constants;
import com.audaharvest.iservices.ICSVFileWriter;
import com.audaharvest.iservices.IValidatorService;
import com.audaharvest.model.Vehicle;
import com.audaharvest.services.validators.MileageValidator;
import com.audaharvest.services.validators.PriceValidator;
import com.audaharvest.services.validators.StockValidator;
import com.audaharvest.services.validators.VinValidator;


public class CSVFileWriter implements ICSVFileWriter{
	
	
	public void writeCSVFile(String fileName, String garbageFileName, Set<Future<Vehicle>> set, Map<String, String> urlMap){
		
		if (fileName == null) {
			throw new IllegalArgumentException("File is null, can't export.");
		}
		
		FileWriter fw = null;
		FileWriter fw2 = null;
		
		long count1 = 1;
		long count2 = 1;
		int vinCount = 0;
		int kmCount = 0;
		int badKMCount = 0;
		int stockCount = 0;
		int badStockCount = 0;
		int priceCount = 0;
		int badPriceCount = 0;
		int transCount = 0;
		int engineCount = 0;
		int sellerLocationCount = 0;
		int bodyStyleCount = 0;
		int dealerNameCount = 0;
		int dealerCityCount = 0;
		int dealerProvinceCount = 0;
		int dealerZipCodeCount = 0;
		int minYear = Integer.parseInt(Constants.MIN_YEAR);
		int maxYear = Calendar.getInstance().get(Calendar.YEAR) + 1 ;
		int priceLength = 0;
		
		String[] htmlTagsForFields = {"strong","span","label","dt.", "dd.","tr.","li.","p.","div."};
		Map<String, String> vehicleKeyStoredMap = new HashMap<String, String>();
		GarbageCleaner garbageCleaner = new GarbageCleaner();
		List<IValidatorService> validators = new ArrayList();
		validators.add(new PriceValidator());
		validators.add(new MileageValidator());
		validators.add(new StockValidator());
		validators.add(new VinValidator());
		try {
			
			fw = new FileWriter(fileName);
			fw2 = new FileWriter(garbageFileName);
			fw.append(Constants.FILE_HEADER);
			fw.append(Constants.NEW_LINE_SEPERATOR);
			fw2.append(Constants.FILE_HEADER);
			fw2.append(Constants.NEW_LINE_SEPERATOR);
			
			for(Future<Vehicle> list: set) {
				String featureString = null;
				String descString = null;
				String trimString = null;
				String otherDetail = null;
				boolean newLink = false;
				boolean detailLink = false;
				boolean validatorResult = false;
				String vehicleKey = list.get().getVin()+list.get().getStockNumber()+list.get().getPrice()+list.get().getMileage()+list.get().getModelCode()+list.get().getYear()+list.get().getMake()+list.get().getModel();
				for(IValidatorService validator : validators) {
					validatorResult = validator.validate(list);
					if(!validatorResult)
						break;
				}
				
				if((list.get().getTrim()!= null) && (!list.get().getTrim().isEmpty())) {
					trimString = garbageCleaner.descCleanUp(list.get().getTrim());
				}
				if((list.get().getVehicleDesc()!= null) && (!list.get().getVehicleDesc().isEmpty())) {
					descString = garbageCleaner.descCleanUp(list.get().getVehicleDesc()).replace("$", "");
				}
				if((list.get().getVehicleFeatures()!= null) && (!list.get().getVehicleFeatures().isEmpty())) {
					featureString = garbageCleaner.featureCleanUp(list.get().getVehicleFeatures()).replace("$", "");
				}
				if((list.get().getMakeModelYearDetail() != null) && (!list.get().getMakeModelYearDetail().isEmpty())) {
					otherDetail = garbageCleaner.descCleanUp(list.get().getMakeModelYearDetail()).replace("$", "");
				}
				if ((list.get().getUrl()!= null) && (!list.get().getUrl().isEmpty())) {
					String urlTrunc = list.get().getUrl().replace(list.get().getDomain(), "").toLowerCase();
					newLink = garbageCleaner.newLinkCleanUp(urlTrunc);
				}
				detailLink = garbageCleaner.detailLinkCheck(list.get().getMileage(), list.get().getPrice(), list.get().getStockNumber(), list.get().getVin());
				boolean fieldsPresent = garbageCleaner.mandatoryFieldsCheck(list.get().getYear(), list.get().getMake(), list.get().getModel(), list.get().getSellerPhoneNo());
				boolean status = ((urlMap.get(Constants.IMAGE_URL)!= null) && (!urlMap.get(Constants.IMAGE_URL).isEmpty())) ? list.get().isStatus() : false;
				//boolean latestAdsPresent = garbageCleaner.latestAdsPresent(urlMap, list.get().getListingDate());
				/*if((fieldsPresent) && (Integer.parseInt(list.get().getYear()) >= minYear) && (Integer.parseInt(list.get().getYear()) <= maxYear)
						&& (validatorResult)
						&& (!vehicleKeyStoredMap.containsKey(vehicleKey))
						&& (!newLink) && (!detailLink) && ((urlMap.get(Constants.LISTING_DATE) == null || urlMap.get(Constants.LISTING_DATE).isEmpty())? true : latestAdsPresent)) {*/
				if((fieldsPresent) && (Integer.parseInt(list.get().getYear()) >= minYear) && (Integer.parseInt(list.get().getYear()) <= maxYear)
						&& (validatorResult)
						&& (!vehicleKeyStoredMap.containsKey(vehicleKey))
						&& (!newLink) && (!detailLink) && (!status) && (!list.get().isStatus())) {
					fw.append(String.valueOf(count1++));
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getYear() != null) ? list.get().getYear() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getMake() != null) ? list.get().getMake() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getModel() != null) ? list.get().getModel() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getSellerPhoneNo() != null) ? list.get().getSellerPhoneNo() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((trimString != null) ? trimString : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getTransmission() != null) ? list.get().getTransmission() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getEngine() != null) ? list.get().getEngine().replace(":", "") : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append(((list.get().getPrice() != null) && (!list.get().getPrice().equals("$"))) ? list.get().getPrice() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append(((list.get().getMileage() != null) && (!list.get().getMileage().equals("km"))) ? list.get().getMileage().replace(":", "") : "");
					fw.append(Constants.COMMA_DELIMITER);
					String fieldDN = list.get().getDealerName();
					String fieldDC = list.get().getDealerCity();
					String fieldDP = list.get().getDealerProvince();
					String fieldDZP = list.get().getDealerZipCode();
					String fieldSL = list.get().getLocation();
					for(String str : htmlTagsForFields) {
						if ((list.get().getDealerName() == null) || list.get().getDealerName().contains(str)) {
							fieldDN = "";
						}
						if ((list.get().getDealerCity() == null) || list.get().getDealerCity().contains(str)) {
							fieldDC = "";
						}
						if ((list.get().getDealerProvince() == null) || list.get().getDealerProvince().contains(str)) {
							fieldDP = "";
						}
						if ((list.get().getDealerZipCode() == null) || list.get().getDealerZipCode().contains(str)) {
							fieldDZP = "";
						}
						if ((list.get().getLocation() == null) || list.get().getLocation().contains(str)) {
							fieldSL = "";
						}
					}
					//fw.append((list.get().getDealerName() != null) ? list.get().getDealerName() : "");
					fw.append(fieldDN);
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getDealerCity() != null) ? list.get().getDealerCity() : "");
					fw.append(fieldDC);
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getDealerProvince() != null) ? list.get().getDealerProvince() : "");
					fw.append(fieldDP);
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getDealerZipCode() != null) ? list.get().getDealerZipCode() : "");
					fw.append(fieldDZP);
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getStockNumber() != null) ? list.get().getStockNumber() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getVin() != null) ? list.get().getVin() : "");
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getVehicleFeatures() != null) ? featureString : "");
					fw.append((featureString != null) ? featureString : "");
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getVehicleDesc() != null) ? descString : "");
					fw.append((descString != null) ? descString : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((otherDetail != null) ? otherDetail : "");
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getLocation() != null) ? list.get().getLocation() : "");
					fw.append(fieldSL);
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getDealerFlag() != null) ? list.get().getDealerFlag() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append(list.get().getUrl());
					//fw.append(list.get().getDomain());
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getBodystyle() != null) ? list.get().getBodystyle() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getExtColour() != null) ? list.get().getExtColour() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getIntColour() != null) ? list.get().getIntColour() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getDrivetrain() != null) ? list.get().getDrivetrain() : "");				
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getDoors() != null) ? list.get().getDoors() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getPassengers() != null) ? list.get().getPassengers() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getFuel() != null) ? list.get().getFuel() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.get().getModelCode() !=null) ? list.get().getModelCode() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((urlMap.get(Constants.PUB_CODE) != null) ? urlMap.get(Constants.PUB_CODE) : "");
					//fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getListingDate() !=null) ? list.get().getListingDate() : "");
					fw.append(Constants.NEW_LINE_SEPERATOR);
					vehicleKeyStoredMap.put(vehicleKey, "");
					if (Constants.WOWO_LOG_FIELD_COUNT.equals("ON")) {
						if ((list.get().getVin() != null) && (!list.get().getVin().isEmpty())) {
							vinCount++;
						}
						if ((list.get().getStockNumber() !=null) && (!list.get().getStockNumber().isEmpty())) {
							stockCount++;
							String str = list.get().getStockNumber();
							String[] strArray = str.trim().split(" ");
							if(strArray.length > 2)
								badStockCount++;
						}
						if ((list.get().getMileage() !=null) && (!list.get().getMileage().isEmpty())) {
							kmCount++;
							if(list.get().getMileage().length() > 8)
								badKMCount++;
						}
						if ((list.get().getPrice() !=null) && (!list.get().getPrice().isEmpty()) && (!list.get().getPrice().equals("$"))) {
							priceCount++;
							if(list.get().getPrice().length() > 11)
								badPriceCount++;
						}
						if ((list.get().getTransmission() !=null) && (!list.get().getTransmission().isEmpty())) {
							transCount++;
						}
						if ((list.get().getEngine() !=null) && (!list.get().getEngine().isEmpty())) {
							engineCount++;
						}
						if ((list.get().getLocation() !=null) && (!list.get().getLocation().isEmpty())) {
							sellerLocationCount++;
						}
						if ((list.get().getBodystyle() !=null) && (!list.get().getBodystyle().isEmpty())) {
							bodyStyleCount++;
						}
						if ((list.get().getDealerName() !=null) && (!list.get().getDealerName().isEmpty())) {
							dealerNameCount++;
						}
						if ((list.get().getDealerCity() !=null) && (!list.get().getDealerCity().isEmpty())) {
							dealerCityCount++;
						}
						if ((list.get().getDealerProvince() !=null) && (!list.get().getDealerProvince().isEmpty())) {
							dealerProvinceCount++;
						}
						if ((list.get().getDealerZipCode() !=null) && (!list.get().getDealerZipCode().isEmpty())) {
							dealerZipCodeCount++;
						}
					}
					 
				} else {
					fw2.append(String.valueOf(count2++));
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getYear() != null) ? list.get().getYear() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getMake() != null) ? list.get().getMake() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getModel() != null) ? list.get().getModel() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getSellerPhoneNo() != null) ? list.get().getSellerPhoneNo() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getTrim() != null) ? list.get().getTrim() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getTransmission() != null) ? list.get().getTransmission() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getEngine() != null) ? list.get().getEngine() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getPrice() != null) ? list.get().getPrice() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getMileage() != null) ? list.get().getMileage() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getDealerName() != null) ? list.get().getDealerName() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getDealerCity() != null) ? list.get().getDealerCity() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getDealerProvince() != null) ? list.get().getDealerProvince() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getDealerZipCode() != null) ? list.get().getDealerZipCode() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getStockNumber() != null) ? list.get().getStockNumber() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getVin() != null) ? list.get().getVin() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getVehicleFeatures() != null) ? list.get().getVehicleFeatures() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getVehicleDesc() != null) ? list.get().getVehicleDesc() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getMakeModelYearDetail() != null) ? list.get().getMakeModelYearDetail() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getLocation() != null) ? list.get().getLocation() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getDealerFlag() != null) ? list.get().getDealerFlag() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append(list.get().getUrl());
					//fw2.append(list.get().getDomain());
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getBodystyle() != null) ? list.get().getBodystyle() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getExtColour() != null) ? list.get().getExtColour() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getIntColour() != null) ? list.get().getIntColour() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getDrivetrain() != null) ? list.get().getDrivetrain() : "");				
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getDoors() != null) ? list.get().getDoors() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getPassengers() != null) ? list.get().getPassengers() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getFuel() != null) ? list.get().getFuel() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.get().getModelCode() !=null) ? list.get().getModelCode() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((urlMap.get(Constants.PUB_CODE) != null) ? urlMap.get(Constants.PUB_CODE) : "");
					//fw2.append(Constants.COMMA_DELIMITER);
					//fw2.append((list.get().getListingDate() !=null) ? list.get().getListingDate() : "");
					fw2.append(Constants.NEW_LINE_SEPERATOR);
					vehicleKeyStoredMap.put(vehicleKey, "");
				}
				
			}			
			if (Constants.WOWO_LOG_FIELD_COUNT.equals("ON")) {
				Logger logger = LogManager.getLogger(CSVFileWriter.class);
				logger.debug(urlMap.get(Constants.ROOT_URL)+ " |" + --count1 +" |" + --count2 + " |"+ vinCount + " |" + kmCount + ", Bad km count - "+ badKMCount + " |" + stockCount + ", Bad stock count - " + badStockCount + " |"+ priceCount + ", Bad price count - "+ badPriceCount +" |" 
							+ sellerLocationCount + " |" + bodyStyleCount + " |" + transCount + " |"+ engineCount +" |" + dealerNameCount + " |" + dealerCityCount + " |" 
							+ dealerProvinceCount + " |" + dealerZipCodeCount + " |" + urlMap.get(Constants.PUB_CODE));
			}			
		} catch (Exception e) {
			System.out.println("Error in CSV file writing!");
			e.printStackTrace();
		} finally {
			try {
				fw.flush();
				fw.close();
				fw2.flush();
				fw2.close();
			} catch (IOException ioe) {
				System.out.println("Error while flushing/closing file!");
				ioe.printStackTrace();
			}
		}
		
	}
	
	
	public void writeCSVFileInventoryPage(String fileName, String garbageFileName, Set<Vehicle> set, Map<String, String> urlMap){
		
		if (fileName == null) {
			throw new IllegalArgumentException("File is null, can't export.");
		}
		
		FileWriter fw = null;
		FileWriter fw2 = null;
		
		long count1 = 1;
		long count2 = 1;
		int vinCount = 0;
		int kmCount = 0;
		int badKMCount = 0;
		int stockCount = 0;
		int badStockCount = 0;
		int priceCount = 0;
		int badPriceCount = 0;
		int transCount = 0;
		int engineCount = 0;
		int sellerLocationCount = 0;
		int bodyStyleCount = 0;
		int dealerNameCount = 0;
		int dealerCityCount = 0;
		int dealerProvinceCount = 0;
		int dealerZipCodeCount = 0;
		int minYear = Integer.parseInt(Constants.MIN_YEAR);
		int maxYear = Calendar.getInstance().get(Calendar.YEAR) + 1 ;
		int priceLength = 0;
		
		String[] htmlTagsForFields = {"strong","span","label","dt.", "dd.","tr.","li.","p.","div."};
		Map<String, String> vehicleKeyStoredMap = new HashMap<String, String>();
		GarbageCleaner garbageCleaner = new GarbageCleaner();
		
		try {
			
			fw = new FileWriter(fileName);
			fw2 = new FileWriter(garbageFileName);
			fw.append(Constants.FILE_HEADER);
			fw.append(Constants.NEW_LINE_SEPERATOR);
			fw2.append(Constants.FILE_HEADER);
			fw2.append(Constants.NEW_LINE_SEPERATOR);
			
			for(Vehicle list: set) {
				String descString = null;
				String featureString = null;
				String mileageVal = "";
				String vehicleKey = list.getVin()+list.getStockNumber()+list.getPrice()+list.getMileage()+list.getModelCode()+list.getYear()+list.getMake()+list.getModel();
				if ((list.getPrice()!= null) && (!list.getPrice().isEmpty())) {
					priceLength = list.getPrice().replaceAll("\\D+", "").trim().length();
				}
				if ((list.getVehicleDesc()!= null) && (!list.getVehicleDesc().isEmpty())) {
					descString = list.getVehicleDesc().replaceAll("(km|Km|KM|Kilometre|Kilometer|Kilometres|Basic warranty|warranty|$)", "");
				}
				if ((list.getVehicleFeatures()!= null) && (!list.getVehicleFeatures().isEmpty())) {
					featureString = list.getVehicleFeatures().replaceAll("(km|Km|KM|Kilometre|Kilometer|Kilometres|Basic warranty|warranty|$)", "");
				}
				if ((list.getMileage()!= null) && (!list.getMileage().isEmpty())) {
					mileageVal = list.getMileage().replaceAll("\\D+", "").trim();
				}
				boolean fieldsPresent = garbageCleaner.mandatoryFieldsCheck(list.getYear(), list.getMake(), list.getModel(), list.getSellerPhoneNo());
				if((fieldsPresent)
						&& (Integer.parseInt(list.getYear()) >= minYear) && (Integer.parseInt(list.getYear()) <= maxYear)
						&& (priceLength <= 8) 
						&& (!mileageVal.equals("0")) && (mileageVal.length() <= 8)
						&& (!vehicleKeyStoredMap.containsKey(vehicleKey))) {
					
					fw.append(String.valueOf(count1++));
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getYear() != null) ? list.getYear() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getMake() != null) ? list.getMake() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getModel() != null) ? list.getModel() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getSellerPhoneNo() != null) ? list.getSellerPhoneNo() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getTrim() != null) ? list.getTrim() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getTransmission() != null) ? list.getTransmission() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getEngine() != null) ? list.getEngine().replace(":", "") : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append(((list.getPrice() != null) && (!list.getPrice().equals("$"))) ? list.getPrice() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append(((list.getMileage() != null) && (!list.getMileage().equals("km"))) ? list.getMileage().replace(":", "") : "");
					fw.append(Constants.COMMA_DELIMITER);
					String fieldDN = list.getDealerName();
					String fieldDC = list.getDealerCity();
					String fieldDP = list.getDealerProvince();
					String fieldDZP = list.getDealerZipCode();
					String fieldSL = list.getLocation();
					for(String str : htmlTagsForFields) {
						if ((list.getDealerName() == null) || list.getDealerName().contains(str)) {
							fieldDN = "";
						}
						if ((list.getDealerCity() == null) || list.getDealerCity().contains(str)) {
							fieldDC = "";
						}
						if ((list.getDealerProvince() == null) || list.getDealerProvince().contains(str)) {
							fieldDP = "";
						}
						if ((list.getDealerZipCode() == null) || list.getDealerZipCode().contains(str)) {
							fieldDZP = "";
						}
						if ((list.getLocation() == null) || list.getLocation().contains(str)) {
							fieldSL = "";
						}
					}
					//fw.append((list.get().getDealerName() != null) ? list.get().getDealerName() : "");
					fw.append(fieldDN);
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getDealerCity() != null) ? list.get().getDealerCity() : "");
					fw.append(fieldDC);
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getDealerProvince() != null) ? list.get().getDealerProvince() : "");
					fw.append(fieldDP);
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getDealerZipCode() != null) ? list.get().getDealerZipCode() : "");
					fw.append(fieldDZP);
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getStockNumber() != null) ? list.getStockNumber() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getVin() != null) ? list.getVin() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getVehicleFeatures() != null) ? featureString : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getVehicleDesc() != null) ? descString : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getMakeModelYearDetail() != null) ? list.getMakeModelYearDetail() : "");
					fw.append(Constants.COMMA_DELIMITER);
					//fw.append((list.get().getLocation() != null) ? list.get().getLocation() : "");
					fw.append(fieldSL);
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getDealerFlag() != null) ? list.getDealerFlag() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((!list.getUrl().contains("http") || list.getUrl().contains("inventory")) ? list.getDomain(): list.getUrl());
					//fw.append(list.get().getDomain());
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getBodystyle() != null) ? list.getBodystyle() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getExtColour() != null) ? list.getExtColour() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getIntColour() != null) ? list.getIntColour() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getDrivetrain() != null) ? list.getDrivetrain() : "");				
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getDoors() != null) ? list.getDoors() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getPassengers() != null) ? list.getPassengers() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getFuel() != null) ? list.getFuel() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((list.getModelCode() !=null) ? list.getModelCode() : "");
					fw.append(Constants.COMMA_DELIMITER);
					fw.append((urlMap.get(Constants.PUB_CODE) != null) ? urlMap.get(Constants.PUB_CODE) : "");
					fw.append(Constants.NEW_LINE_SEPERATOR);
					vehicleKeyStoredMap.put(vehicleKey, "");
					if (Constants.WOWO_LOG_FIELD_COUNT.equals("ON")) {
						if ((list.getVin() != null) && (!list.getVin().isEmpty())) {
							vinCount++;
						}
						if ((list.getStockNumber() !=null) && (!list.getStockNumber().isEmpty())) {
							stockCount++;
							String str = list.getStockNumber();
							String[] strArray = str.trim().split(" ");
							if(strArray.length > 2)
								badStockCount++;
						}
						if ((list.getMileage() !=null) && (!list.getMileage().isEmpty())) {
							kmCount++;
							if(list.getMileage().length() > 8)
								badKMCount++;
						}
						if ((list.getPrice() !=null) && (!list.getPrice().isEmpty()) && (!list.getPrice().equals("$"))) {
							priceCount++;
							if(list.getPrice().length() > 11)
								badPriceCount++;
						}
						if ((list.getTransmission() !=null) && (!list.getTransmission().isEmpty())) {
							transCount++;
						}
						if ((list.getEngine() !=null) && (!list.getEngine().isEmpty())) {
							engineCount++;
						}
						if ((list.getLocation() !=null) && (!list.getLocation().isEmpty())) {
							sellerLocationCount++;
						}
						if ((list.getBodystyle() !=null) && (!list.getBodystyle().isEmpty())) {
							bodyStyleCount++;
						}
						if ((list.getDealerName() !=null) && (!list.getDealerName().isEmpty())) {
							dealerNameCount++;
						}
						if ((list.getDealerCity() !=null) && (!list.getDealerCity().isEmpty())) {
							dealerCityCount++;
						}
						if ((list.getDealerProvince() !=null) && (!list.getDealerProvince().isEmpty())) {
							dealerProvinceCount++;
						}
						if ((list.getDealerZipCode() !=null) && (!list.getDealerZipCode().isEmpty())) {
							dealerZipCodeCount++;
						}
					}
					 
				} else {
					fw2.append(String.valueOf(count2++));
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getYear() != null) ? list.getYear() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getMake() != null) ? list.getMake() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getModel() != null) ? list.getModel() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getSellerPhoneNo() != null) ? list.getSellerPhoneNo() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getTrim() != null) ? list.getTrim() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getTransmission() != null) ? list.getTransmission() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getEngine() != null) ? list.getEngine() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getPrice() != null) ? list.getPrice() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getMileage() != null) ? list.getMileage() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getDealerName() != null) ? list.getDealerName() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getDealerCity() != null) ? list.getDealerCity() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getDealerProvince() != null) ? list.getDealerProvince() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getDealerZipCode() != null) ? list.getDealerZipCode() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getStockNumber() != null) ? list.getStockNumber() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getVin() != null) ? list.getVin() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getVehicleFeatures() != null) ? list.getVehicleFeatures() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getVehicleDesc() != null) ? list.getVehicleDesc() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getMakeModelYearDetail() != null) ? list.getMakeModelYearDetail() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getLocation() != null) ? list.getLocation() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getDealerFlag() != null) ? list.getDealerFlag() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((!list.getUrl().contains("http") || list.getUrl().contains("inventory")) ? list.getDomain(): list.getUrl()); 
					//fw2.append(list.get().getDomain());
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getBodystyle() != null) ? list.getBodystyle() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getExtColour() != null) ? list.getExtColour() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getIntColour() != null) ? list.getIntColour() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getDrivetrain() != null) ? list.getDrivetrain() : "");				
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getDoors() != null) ? list.getDoors() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getPassengers() != null) ? list.getPassengers() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getFuel() != null) ? list.getFuel() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((list.getModelCode() !=null) ? list.getModelCode() : "");
					fw2.append(Constants.COMMA_DELIMITER);
					fw2.append((urlMap.get(Constants.PUB_CODE) != null) ? urlMap.get(Constants.PUB_CODE) : "");
					fw2.append(Constants.NEW_LINE_SEPERATOR);
					vehicleKeyStoredMap.put(vehicleKey, "");
				}
				
			}			
			if (Constants.WOWO_LOG_FIELD_COUNT.equals("ON")) {
				Logger logger = LogManager.getLogger(CSVFileWriter.class);
				logger.debug(urlMap.get(Constants.ROOT_URL)+ " |" + --count1 +" |" + --count2 + " |"+ vinCount + " |" + kmCount + ", Bad km count - "+ badKMCount + " |" + stockCount + ", Bad stock count - " + badStockCount + " |"+ priceCount + ", Bad price count - "+ badPriceCount +" |" 
							+ sellerLocationCount + " |" + bodyStyleCount + " |" + transCount + " |"+ engineCount +" |" + dealerNameCount + " |" + dealerCityCount + " |" 
							+ dealerProvinceCount + " |" + dealerZipCodeCount + " |" + urlMap.get(Constants.PUB_CODE));
			}			
		} catch (Exception e) {
			System.out.println("Error in CSV file writing!");
			e.printStackTrace();
		} finally {
			try {
				fw.flush();
				fw.close();
				fw2.flush();
				fw2.close();
			} catch (IOException ioe) {
				System.out.println("Error while flushing/closing file!");
				ioe.printStackTrace();
			}
		}
		
	}
}
