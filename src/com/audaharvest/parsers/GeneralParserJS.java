package com.audaharvest.parsers;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

import com.audaharvest.common.WebDriverUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.iservices.IVehicleService;
import com.audaharvest.model.Vehicle;
import com.audaharvest.utils.ParsingUtils;

public class GeneralParserJS extends ParserJS implements IVehicleService{	

	private String viewSource = null;
	//Performs the main task in getting the vehicle details
		public Vehicle getVehicleDetails(){
			WebDriverUtil webDriverUtil = new WebDriverUtil();
			WebDriver driver = webDriverUtil.getFirefoxDriver();
			driver.get(url);
			viewSource = driver.getPageSource();
			driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

			/*try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}*/
			try {				
				
				vehicle.setUrl(url);
				vehicle.setDomain(urlMap.get(Constants.ROOT_URL));
				getBlockDataByParentNode(driver);
				if((vehicle.getYear() == null) || (vehicle.getYear().isEmpty())) {
					getVehicleYear(driver);
				}
				if((vehicle.getMake() == null) || (vehicle.getMake().isEmpty())) {
					getVehicleMake(driver);
				}
				if((vehicle.getModel() == null) || (vehicle.getModel().isEmpty())) {
					getVehicleModel(driver);
				}
				if((vehicle.getMake() == null) || (vehicle.getYear() == null) || (vehicle.getModel() == null)) {
					getMakeModelYearByBlock(driver);
				}
				if((vehicle.getMake() == null) || (vehicle.getYear() == null) || (vehicle.getModel() == null)) {
					getMakeModelYear(driver);
				}
				getTrimValue(driver);
				getSalesNo(driver);
				getPriceDetail(driver);
				getMileage(driver);
				getLocation(driver);
				if(vehicle.getStockNumber() == null) {
					getStockNumber(driver);
				}
				if(vehicle.getVin() == null) {
					getVINumber(driver);
				}
				getFeatures(driver);
				getDescription(driver);
				getDealerInfo(driver);
				getPrivateDealerFlag();
				if(vehicle.getTransmission() == null || vehicle.getTransmission().isEmpty())
					getTransUsingSelect(driver);
				if(vehicle.getEngine() == null || vehicle.getEngine().isEmpty())
					getEngineUsingSelect(driver);
				if(vehicle.getBodystyle() == null || vehicle.getBodystyle().isEmpty())
					getBodyStyleUsingSelect(driver);
				if(vehicle.getExtColour() == null || vehicle.getExtColour().isEmpty())
					getExtColourUsingSelect(driver);
				if(vehicle.getIntColour() == null || vehicle.getIntColour().isEmpty())
					getIntColourUsingSelect(driver);
				if((urlMap.get(Constants.DOORS) != null) && !urlMap.get(Constants.DOORS).isEmpty() && (vehicle.getDoors() == null || vehicle.getDoors().isEmpty()))
					getDoorsUsingSelect(driver);
				if((urlMap.get(Constants.PASSENGERS) != null) && !urlMap.get(Constants.PASSENGERS).isEmpty() && (vehicle.getPassengers() == null || vehicle.getPassengers().isEmpty()))
					getPassengersUsingSelect(driver);
				if((urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL) != null) && !urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL).isEmpty()) {
					if(urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL).contains(Constants.GET_BY_REGEX)) {
						String param = urlMap.get(Constants.MAKE_MODEL_YEAR_DETAIL).replaceAll(Constants.GET_BY_REGEX+"\\(", "");
					    String selector = param.substring(0, param.length()-1);
						vehicle.setMakeModelYearDetail(ParsingUtils.getByRegexSelector(selector, viewSource));
					}else {
						getMakeModelYearDetail(driver);
					}
				}
				getListingDate(driver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Parsed Link: " + vehicle.getUrl());
			latch.countDown();
			driver.quit();
			return vehicle;
		}
		
		@Override
		public Vehicle call() throws Exception {
			return getVehicleDetails();
		}
}
