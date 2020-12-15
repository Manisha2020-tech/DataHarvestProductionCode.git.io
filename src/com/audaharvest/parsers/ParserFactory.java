package com.audaharvest.parsers;

import com.audaharvest.constants.Constants;

public class ParserFactory {
	
	public Parser createParser(String group) {
		Parser parser = null;
		
		if(group.equals(Constants.GROUP_G1)) {
			parser = new GeneralParser();
		}
		if(group.equals(Constants.GROUP_G2)) {
			parser = new GroupLevelParser();
		}
		if(group.equals(Constants.GROUP_G2_WIX)) {
			parser = new GroupLevelParser();
		}
		if(group.equals(Constants.GROUP_G2_JSON_INVENTORY_HTML_AD)) {
			parser = new GroupLevelParser();
		}
		
		return parser;
	}
	
	public ParserJS createJSParser(String group) {
		ParserJS parserJS = null;
		
		if(group.equals(Constants.GROUP_G4)) {
			parserJS = new GeneralParserJS();
		}
		
		if(group.equals(Constants.GROUP_JASON_LINK_BUILDER_G4)) {
			parserJS = new GeneralParserJS();
		}
		
		
		
		return parserJS;
	}
	
	public ParserJson createJsonParser(String group) {
		ParserJson parserJson = null;
		
		if(group.equals(Constants.GROUP_G4_LINK_JSON_AD)) {
			parserJson = new G4LinkJsonAdParser();
		}
		
		if(group.equals(Constants.GROUP_G2_JSON_INVENTORY_JSON_AD)) {
			parserJson = new JsonInventoryJsonAdParser();
		}
		
		return parserJson;
		
	}
}
