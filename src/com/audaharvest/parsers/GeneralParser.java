package com.audaharvest.parsers;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

import com.audaharvest.common.SSLTool;
import com.audaharvest.common.WebDriverUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.iservices.IVehicleService;
import com.audaharvest.model.ConstantParameters;
import com.audaharvest.model.Vehicle;

public class GeneralParser extends Parser implements IVehicleService{
	
	private Document doc;
	
	public GeneralParser() {
	}

	//Performs the main task in getting the vehicle details
		public Vehicle getVehicleDetails(){
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
				
				vehicle.setUrl(url+locale);
				vehicle.setDomain(urlMap.get(Constants.ROOT_URL));
				getBlockData(doc);
				if((vehicle.getYear() == null) || (vehicle.getYear().isEmpty())) {
					getVehicleYear(doc);
				}
				if((vehicle.getMake() == null) || (vehicle.getMake().isEmpty())) {
					getVehicleMake(doc);
				}
				if((vehicle.getModel() == null) || (vehicle.getModel().isEmpty())) {
					getVehicleModel(doc);
				}
				if((vehicle.getMake() == null) || (vehicle.getYear() == null) || (vehicle.getModel() == null)) {
					getMakeModelYearByBlock(doc);
				}
				if((vehicle.getMake() == null) || (vehicle.getYear() == null) || (vehicle.getModel() == null)) {
					getMakeModelYear(doc);
				}
				getTrimValue(doc);
				getSalesNo(doc);
				getPriceDetail(doc);
				getMileage(doc);
				getLocation(doc);
				if(vehicle.getStockNumber() == null) {
					getStockNumber(doc);
				}
				if(vehicle.getVin() == null) {
					getVINumber(doc);
				}
				if(Constants.SELENIUM_REQUIRED.equals("Yes")) {
					seleniumDriver.get(url);
					getFeaturesUsingSelenium(seleniumDriver);
					seleniumDriver.quit();
				}
				if(vehicle.getVehicleFeatures() == null || vehicle.getVehicleFeatures().isEmpty()) {
					getFeatures(doc);
				}				
				getDescription(doc);
				getDealerInfo(doc);
				getPrivateDealerFlag();
				if(vehicle.getTransmission() == null || vehicle.getTransmission().isEmpty())
					getTransUsingSelect(doc);
				if(vehicle.getEngine() == null || vehicle.getEngine().isEmpty())
					getEngineUsingSelect(doc);
				if(vehicle.getBodystyle() == null || vehicle.getBodystyle().isEmpty())
					getBodyStyleUsingSelect(doc);
				if(vehicle.getExtColour() == null || vehicle.getExtColour().isEmpty())
					getExtColourUsingSelect(doc);
				if(vehicle.getIntColour() == null || vehicle.getIntColour().isEmpty())
					getIntColourUsingSelect(doc);
				if((urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL) != null) && !urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL).isEmpty()) {
					getMakeModelYearDetail(doc);
				}
				//getListingDate(doc);
				if((urlMap.get(Constants.IMAGE_URL) != null) && !urlMap.get(Constants.IMAGE_URL).isEmpty()) {
					getProdImageUrl(doc);
				}
				if((urlMap.get(Constants.VEHICLE_STATUS) != null) && !urlMap.get(Constants.VEHICLE_STATUS).isEmpty()) {
					getVehicleStatus(doc);
				}
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

}
