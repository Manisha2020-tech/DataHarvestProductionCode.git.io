package com.audaharvest.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

public class ParsingUtils {

	public static String getByRegexSelector(String selector, String ViewSource) {
		String[] strArr = selector.split("\\^");
		Matcher matcher = Pattern.compile(Pattern.quote(strArr[0]) + "(.*?)"+ Pattern.quote(strArr[1])).matcher(ViewSource);
		while(matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}
	
	public static String getCleanJson(String input) {
        input = input.replaceAll("\\\\n|\\\\x20|\\|", " ").replaceAll("\\s+"," ");
        input = input.replaceAll("\\\\x2F", "/");
        input = input.replaceAll("\\b(null)\\b", "");
        return StringEscapeUtils.unescapeJava(input).toString();
	}

	
//	public static String getCleanJson(String input) {
//		input = input.replaceAll("\\\\n", " ");
//		input = input.replaceAll("\\\\x20", " ");
//		input = input.replaceAll("\\\\x2F", "/");
//		input = input.replaceAll("\\b(null)\\b", "");
//		return StringEscapeUtils.unescapeJava(input).toString();
//	}
}
