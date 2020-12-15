package com.audaharvest.services.fieldutils;

public class PriceUtil {
	
	public String searchPrice(String str) {
		int len = str.length();
		String price = "";
		StringBuilder sb = new StringBuilder();
		try {
			int index = str.indexOf("$");		
			while(index < len-1) {
				char c = str.charAt(index+1);
				String cStr = String.valueOf(c);
				if(cStr.matches("[0-9, ,,.]")) {
					sb.append(price + cStr);
				} else {
					break;
				}			
				index++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString().trim();		
	}
	
	public String priceWithDollarEnd(String str) {
		StringBuilder sb = new StringBuilder();		
		String price = "";
		try {
			int index = str.indexOf("$");
			
			//Goes backward
			while(index >= 0) {
				char c = str.charAt(index-1);
				String cStr = String.valueOf(c);
				if(cStr.matches("[0-9, ,,.,\\u00a0]")) {
					sb.append(price + cStr);
				} else {
					break;
				}			
				index--;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.reverse().toString().trim();
		
	}
	
	
	public String removePriceText(String str) {
		
		if(!str.contains("$"))
			return str;

		StringBuilder sb = new StringBuilder();		
		int len = str.length();
		String price = "";
		try {
			int index = str.indexOf("$");				
			while(index < len-1) {
				char c = str.charAt(index+1);
				String cStr = String.valueOf(c);
				if(cStr.matches("[0-9, ,,.]")) {
					sb.append(price + cStr);
				} else {
					break;
				}			
				index++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		String cleanedText = str.replace("$"+sb.toString(), "");
		return removePriceText(cleanedText);

	}
}
