package com.audaharvest.services;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audaharvest.constants.Constants;
import com.audaharvest.services.fieldutils.PriceUtil;

public class GarbageCleaner {
	PriceUtil priceUtil = new PriceUtil();
	public boolean mandatoryFieldsCheck(String year, String make, String model, String phoneNo) {
		boolean fieldsPresent = false;
			if((year != null && !year.isEmpty())
				&& (make != null && !make.isEmpty())
				&& (model != null && !model.isEmpty())
				&& (phoneNo != null && !phoneNo.isEmpty())) {
				fieldsPresent = true;
			}
		
		return fieldsPresent;
		
	}
	
	public String descCleanUp(String str) {
		
		String descString = str.replaceAll("(NOT DRIVABLE|not drivable|km|Km|KM|Kilometre|Kilometer|Kilometres|Basic warranty|warranty|Miles|Rebuilt|REBUILT|rebuilt|require substantial|REQUIRE SUBSTANTIAL|REPAIR|SALVAGE|race car|Project Car|PROJECT|Project|YES|yes|Yes|Salvage|salvage|Basic warranty|warranty|Yes|yes|Oui|\\u00a0)", "");		
		if(str.contains("$")) {
			descString = priceUtil.removePriceText(descString);
		}
		
		return descString;
		
	}
	
	public String featureCleanUp(String str) {
		
		String featureString = str.replaceAll("(NOT DRIVABLE|not drivable|km|Km|KM|Kilometre|Kilometer|Kilometres|Basic warranty|warranty|Miles|Rebuilt|REBUILT|rebuilt|require substantial|REQUIRE SUBSTANTIAL|REPAIR|SALVAGE|race car|Project Car|PROJECT|Project|YES|yes|Yes|Salvage|salvage|Basic warranty|warranty|Yes|yes|Oui|\\u00a0)", "");
		if(str.contains("$")) {
			featureString = priceUtil.removePriceText(featureString);
		}
		
		return featureString;
		
	}
	
	public String trimCleanUp(String str) {
		
		String trimString = str;
		if(str.contains("$")) {
			trimString = priceUtil.removePriceText(str);
		}
		
		return trimString;
		
	}
	
	public boolean newLinkCleanUp(String str) {
		boolean newLink = false;		
			String urlTrunc = str;
			// Identifying new inventories
			if (urlTrunc.contains("new") || urlTrunc.contains("neuf") || urlTrunc.contains("nouvelles") 
					|| urlTrunc.contains("/showroom")  || urlTrunc.contains("showroom/") || urlTrunc.contains("-showroom") 
					|| urlTrunc.contains("-promo")  || urlTrunc.contains("promotion/") || urlTrunc.contains("/article") 
					|| urlTrunc.contains("/blog") || urlTrunc.contains("disclaimer") || urlTrunc.contains("/demande-de-carproof/")
					|| urlTrunc.contains("/demande-de-prix/") || urlTrunc.contains("/demo/") || urlTrunc.contains("inventory.html?reset=1")) {
				newLink = true;
			}
				
		return newLink;
		
	}
	
	public boolean detailLinkCheck(String mileage, String price, String stock, String vin) {
		boolean detailLink = false;
		
		// Identifying whether or not it is a detail link

		if (((mileage == null) || (mileage.isEmpty()))
				&& ((price == null) || (price.isEmpty()))
				&& ((stock == null) || (stock.isEmpty()))
				&& ((vin == null) || (vin.isEmpty()))) {
				detailLink = true;
		}
		return detailLink;
		
	}
	
	public boolean latestAdsPresent(Map<String, String> urlMap, String str) {
		boolean newAd = false;
		if(urlMap.get(Constants.LISTING_DATE) == null || urlMap.get(Constants.LISTING_DATE).isEmpty()) {
			return newAd = true;
		}
		
		Pattern pattern = Pattern.compile("((?<!\\S)[2-7](?!\\S) jours)|(heurs)|(heures)|(1 jour)");
		//Pattern pattern = Pattern.compile("((?<!\\S)\\d(?!\\S) jours)|(heurs)|(heures)");
		Matcher matcher = pattern.matcher(str);		
		while(matcher.find()) {
			newAd = true;
			break;
		} 				
		/*String urlTrunc = str;
		// Identifying old ads
		if (urlTrunc.contains("heurs") || urlTrunc.contains("heures") || urlTrunc.contains("1 jour") || urlTrunc.contains("2 jours") 
				|| urlTrunc.contains("3 jours")  || urlTrunc.contains("4 jours") || urlTrunc.contains("5 jours") 
				|| urlTrunc.contains("6 jours")  || urlTrunc.contains("7 jours") || urlTrunc.contains("yesterday") || urlTrunc.contains("hier")) {
			newAd = true;
		}*/
				
		return newAd;
		
	}

}
