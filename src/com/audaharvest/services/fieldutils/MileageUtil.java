package com.audaharvest.services.fieldutils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MileageUtil {
	public static String searchMileage(String str) {
		int len = str.length();
		int index = 0;
		String kms = "";
		
		if (str.contains("Kilometers")) {
			index = str.indexOf("Kilometers");
		} else if (str.contains("kilometers")) {
			index = str.indexOf("kilometers");
		} else if (str.contains("klms")) {
			index = str.indexOf("klms");
		} else if (str.contains("klm")) {
			index = str.indexOf("klm");
		} else if(str.contains("kms")) {
			index = str.indexOf("kms"); 
		} else if (str.contains("Km")) {
			index = str.indexOf("Km");
		} else if (str.contains("KM")) {
			index = str.indexOf("KM");
		} else if (str.contains("km")) {
			index = str.indexOf("km");
		} else if (str.contains("miles")) {
			index = str.indexOf("miles");
		} else if (str.contains("mile")) {
			index = str.indexOf("mile");
		} else if (str.contains("Miles")) {
			index = str.indexOf("Miles");
		} else if (str.contains("Mile")) {
			index = str.indexOf("Mile");
		} else if (str.contains("MI")) {
			index = str.indexOf("MI");
		}
		StringBuilder sb = new StringBuilder();
		while(index > 0 && str.charAt(index-1) == ' ') {
            index--;
        }
		while(index > 0) {
			char c = str.charAt(index-1);
			String cStr = String.valueOf(c);
			if(cStr.matches("[0-9,. ]")) {
				sb.append(kms + cStr);
			} else {
				break;
			}			
			index--;
		}
		//check for space separated prices avoid case "kj123 24km"
		String[] prices = sb.reverse().toString().trim().split(" ");
		if(!str.contains(" "+prices[0]+" "+prices[1])) {
			StringBuilder sb1 = new StringBuilder();
			for(int i= 1; i<prices.length; i++) {
				sb1.append(prices[i]);
				return sb1.toString().trim();
			}
		}else {
			sb.reverse();
		}
		
		if(sb.charAt(sb.length() - 1) == ',')
			sb.setCharAt(sb.length() - 1, ' ');
		if(sb.charAt(sb.length() - 1) == '.')
			sb.setCharAt(sb.length() - 1, ' ');
		return sb.reverse().toString().trim();		
	}
	
	public static void main(String[] a) {
		String s = "skdhgfksh hh123 374km skdhav";
		System.out.println(searchMileage(s));
	}
}
