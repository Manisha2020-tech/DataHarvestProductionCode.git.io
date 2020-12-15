package com.audaharvest.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.audaharvest.constants.Constants;
import com.audaharvest.model.ConstantParameters;

public class ExcelFileReader {

	public List<String> getExcelData(String path) throws IOException {
		List list = new ArrayList();
		FileInputStream inputStream = new FileInputStream(new File(path));
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = 	workbook.getSheet("Sheet1");
		int rowStart = firstSheet.getFirstRowNum();
		int rowEnd = firstSheet.getLastRowNum();		
		for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {			
			Row r = firstSheet.getRow(rowNum);
			int lastColumn = r.getLastCellNum();
				for (int cn=0; cn < lastColumn; cn++) {
					Cell c = r.getCell(cn);
					list.add(c.getStringCellValue());
				}
		}
		/*Iterator<Row> itr = firstSheet.iterator();
		while(itr.hasNext()) {
			Row nextRow = (Row) itr.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			//makeModelObj = new MakeModel();			
			while(cellIterator.hasNext()) {
				Cell nextCell = cellIterator.next();
				int columnIndex = nextCell.getColumnIndex();
				switch (columnIndex) {
					case 1:
						//System.out.println(getCellValue(nextCell));
						list.add((String) getCellValue(nextCell));
						//makeModelObj.setMake((String) getCellValue(nextCell));
						break;
				}
			}
			//list.add(makeModelObj);
		}*/
		
		return list;		
	}
	
	private Object getCellValue(Cell cell) {		
		switch (cell.getCellType()) {		
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
			
		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
			
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		}
		
		return null;		
	}
	
	public ConcurrentMap<String, Map<String, String>> urlsParamsMap(String path) throws IOException {
		ConcurrentMap<String, Map<String, String>> cMap = new ConcurrentHashMap<String, Map<String, String>>();
		FileInputStream inputStream = new FileInputStream(new File(path));
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = 	workbook.getSheet(Constants.SHEET_NAME);
		int rowStart = firstSheet.getFirstRowNum() + 1;
		int rowEnd = firstSheet.getLastRowNum() + 1;
		for (int rowNum = Integer.parseInt(ConstantParameters.getRowStart()); rowNum < Integer.parseInt(ConstantParameters.getRowEnd()); rowNum++) {
			Map<String, String> map = new HashMap<String, String>();
			Row r = firstSheet.getRow(rowNum);
			if ((r.getCell(0) != null) && (!r.getCell(0).getStringCellValue().isEmpty())) {
			map.put(Constants.GROUP, r.getCell(0).getStringCellValue());
			}
			map.put(Constants.URL, r.getCell(1).getStringCellValue());
			map.put(Constants.ROOT_URL, r.getCell(2).getStringCellValue());
			if ((r.getCell(3) != null) && (!r.getCell(3).getStringCellValue().isEmpty())) {
				map.put(Constants.MANUFACTURER, r.getCell(3).getStringCellValue());
			}
			if ((r.getCell(4) != null) && (!r.getCell(4).getStringCellValue().isEmpty())) {
				map.put(Constants.MODEL, r.getCell(4).getStringCellValue());
			}
			if ((r.getCell(5) != null) && (!r.getCell(5).getStringCellValue().isEmpty())) {
				map.put(Constants.YEAR, r.getCell(5).getStringCellValue());
			}
			if ((r.getCell(6) != null) && (!r.getCell(6).getStringCellValue().isEmpty())) {
				map.put(Constants.BODYSTYLE, r.getCell(6).getStringCellValue());
			}
			if ((r.getCell(7) != null) && (!r.getCell(7).getStringCellValue().isEmpty())) {
				map.put(Constants.DOORS, r.getCell(7).getStringCellValue());
			}
			if ((r.getCell(8) != null) && (!r.getCell(8).getStringCellValue().isEmpty())) {
				map.put(Constants.PASSENGERS, r.getCell(8).getStringCellValue());
			}
			if ((r.getCell(9) != null) && (!r.getCell(9).getStringCellValue().isEmpty())) {
				map.put(Constants.ENGINE, r.getCell(9).getStringCellValue());
			}
			if ((r.getCell(10) != null) && (!r.getCell(10).getStringCellValue().isEmpty())) {
				map.put(Constants.FUEL, r.getCell(10).getStringCellValue());
			}
			if ((r.getCell(11) != null) && (!r.getCell(11).getStringCellValue().isEmpty())) {
				map.put(Constants.EXTERIOR_COLOR, r.getCell(11).getStringCellValue());
			}
			if ((r.getCell(12) != null) && (!r.getCell(12).getStringCellValue().isEmpty())) {
				map.put(Constants.INTERIOR_COLOR, r.getCell(12).getStringCellValue());
			}
			if ((r.getCell(13) != null) && (!r.getCell(13).getStringCellValue().isEmpty())) {
				map.put(Constants.DRIVETRAIN, r.getCell(13).getStringCellValue());
			}
			if ((r.getCell(14) != null) && (!r.getCell(14).getStringCellValue().isEmpty())) {
				map.put(Constants.SALES_NUMBER, r.getCell(14).getStringCellValue());
			}
			if ((r.getCell(15) != null) && (!r.getCell(15).getStringCellValue().isEmpty())) {
				map.put(Constants.TRANSMISSION, r.getCell(15).getStringCellValue());
			}
			if ((r.getCell(16) != null) && (!r.getCell(16).getStringCellValue().isEmpty())) {
				map.put(Constants.STOCK_NUM, r.getCell(16).getStringCellValue());
			}
			if ((r.getCell(17) != null) && (!r.getCell(17).getStringCellValue().isEmpty())) {
				map.put(Constants.PRICE, r.getCell(17).getStringCellValue());
			}
			if ((r.getCell(18) != null) && (!r.getCell(18).getStringCellValue().isEmpty())) {
				map.put(Constants.VIN, r.getCell(18).getStringCellValue());
			}
			if ((r.getCell(19) != null) && (!r.getCell(19).getStringCellValue().isEmpty())) {
				map.put(Constants.MILEAGE, r.getCell(19).getStringCellValue());
			}
			if ((r.getCell(20) != null) && (!r.getCell(20).getStringCellValue().isEmpty())) {
				map.put(Constants.LOCATION, r.getCell(20).getStringCellValue());
			}
			if ((r.getCell(21) != null) && (!r.getCell(21).getStringCellValue().isEmpty())) {
				map.put(Constants.COUNTRY_CODE, r.getCell(21).getStringCellValue());
			}
			if ((r.getCell(22) != null) && (!r.getCell(22).getStringCellValue().isEmpty())) {
				map.put(Constants.MODEL_CODE, r.getCell(22).getStringCellValue());
			}
			if ((r.getCell(23) != null) && (!r.getCell(23).getStringCellValue().isEmpty())) {
				map.put(Constants.LINK_FINDER, r.getCell(23).getStringCellValue());
			}
			if ((r.getCell(24) != null) && (!r.getCell(24).getStringCellValue().isEmpty())) {
				map.put(Constants.BLOCK_DATA_FINDER, r.getCell(24).getStringCellValue());
			}
			if ((r.getCell(25) != null) && (!r.getCell(25).getStringCellValue().isEmpty())) {
				map.put(Constants.TRIM, r.getCell(25).getStringCellValue());
			}
			if ((r.getCell(26) != null) && (!r.getCell(26).getStringCellValue().isEmpty())) {
				map.put(Constants.VEHICLE_FEATURES, r.getCell(26).getStringCellValue());
			}
			if ((r.getCell(27) != null) && (!r.getCell(27).getStringCellValue().isEmpty())) {
				map.put(Constants.VEHICLE_DESC, r.getCell(27).getStringCellValue());
			}
			if ((r.getCell(28) != null) && (!r.getCell(28).getStringCellValue().isEmpty())) {
				map.put(Constants.DEALER_NAME, r.getCell(28).getStringCellValue());
			}
			if ((r.getCell(29) != null) && (!r.getCell(29).getStringCellValue().isEmpty())) {
				map.put(Constants.DEALER_CITY, r.getCell(29).getStringCellValue());
			}
			if ((r.getCell(30) != null) && (!r.getCell(30).getStringCellValue().isEmpty())) {
				map.put(Constants.DEALER_PROVINCE, r.getCell(30).getStringCellValue());
			}
			if ((r.getCell(31) != null) && (!r.getCell(31).getStringCellValue().isEmpty())) {
				map.put(Constants.DEALER_POSTAL_CODE, r.getCell(31).getStringCellValue());
			}
			if ((r.getCell(32) != null) && (!r.getCell(32).getStringCellValue().isEmpty())) {
				map.put(Constants.DOWNLOAD_PAGE, r.getCell(32).getStringCellValue());
			}
			if ((r.getCell(33) != null) && (!r.getCell(33).getStringCellValue().isEmpty())) {
				map.put(Constants.MAKE_MODEL_YEAR_DETAIL, r.getCell(33).getStringCellValue());
			}
			if ((r.getCell(34) != null) && (!r.getCell(34).getStringCellValue().isEmpty())) {
				map.put(Constants.DEALER_FLAG, r.getCell(34).getStringCellValue());
			}
			if ((r.getCell(35) != null) && (!r.getCell(35).getStringCellValue().isEmpty())) {
				map.put(Constants.LOCALIZED_COUNTRY_CODE, r.getCell(35).getStringCellValue());
			}
			if ((r.getCell(36) != null) && (!r.getCell(36).getStringCellValue().isEmpty())) {
				map.put(Constants.PUB_CODE, r.getCell(36).getStringCellValue());
			}
			if ((r.getCell(37) != null) && (!r.getCell(37).getStringCellValue().isEmpty())) {
				map.put(Constants.UNWANTED_BLOCK_DATA, r.getCell(37).getStringCellValue());
			}
			if ((r.getCell(38) != null) && (!r.getCell(38).getStringCellValue().isEmpty())) {
				map.put(Constants.LISTING_DATE, r.getCell(38).getStringCellValue());
			}
			
			if ((r.getCell(39) != null) && (!r.getCell(39).getStringCellValue().isEmpty())) {
				map.put(Constants.IMAGE_URL, r.getCell(39).getStringCellValue());
			}
			if ((r.getCell(40) != null) && (!r.getCell(40).getStringCellValue().isEmpty())) {
				map.put(Constants.VEHICLE_STATUS, r.getCell(40).getStringCellValue());
			}

			cMap.put(r.getCell(1).getStringCellValue(), map);
					
		}
		return cMap;
	}
	
	public ConcurrentMap<String, Map<String, String>> urlsParamsMapTrainingData() throws IOException {
		String verifiedDataUrlsParamsDefsPath = System.getProperty("user.home") + "/TrainingDataUrlsParamsDefs.xlsx";		
		ConcurrentMap<String, Map<String, String>> cMap = new ConcurrentHashMap<String, Map<String, String>>();	
		FileInputStream fis = new FileInputStream(new File(verifiedDataUrlsParamsDefsPath));
		Workbook workbookFis = new XSSFWorkbook(fis);
		Sheet firstSheetFis = 	workbookFis.getSheet(Constants.TRAINING_SHEET_NAME);
		int rowStartFis = firstSheetFis.getFirstRowNum() + 1;
		int rowEndFis = firstSheetFis.getLastRowNum() + 1;
		
		for (int rowNum = Integer.parseInt(ConstantParameters.getTrainingDataRowStart()); rowNum < Integer.parseInt(ConstantParameters.getTrainingDataRowEnd()); rowNum++) {
			Map<String, String> map = new HashMap<String, String>();
			Row r = firstSheetFis.getRow(rowNum);
			map.put(Constants.GROUP, r.getCell(0).getStringCellValue());
			map.put(Constants.URL, r.getCell(1).getStringCellValue());
			map.put(Constants.ROOT_URL, r.getCell(2).getStringCellValue());
			
			if ((r.getCell(3) != null) && (!r.getCell(3).getStringCellValue().isEmpty())) {
				map.put(Constants.MANUFACTURER, r.getCell(3).getStringCellValue());
			}
			if ((r.getCell(4) != null) && (!r.getCell(4).getStringCellValue().isEmpty())) {
				map.put(Constants.MODEL, r.getCell(4).getStringCellValue());
			}
			if ((r.getCell(5) != null) && (!r.getCell(5).getStringCellValue().isEmpty())) {
				map.put(Constants.YEAR, r.getCell(5).getStringCellValue());
			}
			map.put(Constants.BODYSTYLE, r.getCell(6).getStringCellValue());
			map.put(Constants.DOORS, r.getCell(7).getStringCellValue());
			map.put(Constants.PASSENGERS, r.getCell(8).getStringCellValue());
			map.put(Constants.ENGINE, r.getCell(9).getStringCellValue());
			map.put(Constants.FUEL, r.getCell(10).getStringCellValue());
			map.put(Constants.EXTERIOR_COLOR, r.getCell(11).getStringCellValue());
			map.put(Constants.INTERIOR_COLOR, r.getCell(12).getStringCellValue());
			map.put(Constants.DRIVETRAIN, r.getCell(13).getStringCellValue());
			map.put(Constants.SALES_NUMBER, r.getCell(14).getStringCellValue());
			map.put(Constants.TRANSMISSION, r.getCell(15).getStringCellValue());
			map.put(Constants.STOCK_NUM, r.getCell(16).getStringCellValue());
			map.put(Constants.PRICE, r.getCell(17).getStringCellValue());
			map.put(Constants.VIN, r.getCell(18).getStringCellValue());
			map.put(Constants.MILEAGE, r.getCell(19).getStringCellValue());
			map.put(Constants.LOCATION, r.getCell(20).getStringCellValue());
			map.put(Constants.COUNTRY_CODE, r.getCell(21).getStringCellValue());
			map.put(Constants.MODEL_CODE, r.getCell(22).getStringCellValue());
			if ((r.getCell(23) != null) && (!r.getCell(23).getStringCellValue().isEmpty())) {
				map.put(Constants.LINK_FINDER, r.getCell(23).getStringCellValue());
			}
			if ((r.getCell(24) != null) && (!r.getCell(24).getStringCellValue().isEmpty())) {
				map.put(Constants.BLOCK_DATA_FINDER, r.getCell(24).getStringCellValue());
			}
			if ((r.getCell(25) != null) && (!r.getCell(25).getStringCellValue().isEmpty())) {
				map.put(Constants.TRIM, r.getCell(25).getStringCellValue());
			}
			if ((r.getCell(26) != null) && (!r.getCell(26).getStringCellValue().isEmpty())) {
				map.put(Constants.VEHICLE_FEATURES, r.getCell(26).getStringCellValue());
			}
			if ((r.getCell(27) != null) && (!r.getCell(27).getStringCellValue().isEmpty())) {
				map.put(Constants.VEHICLE_DESC, r.getCell(27).getStringCellValue());
			}
			if ((r.getCell(37) != null) && (!r.getCell(37).getStringCellValue().isEmpty())) {
				map.put(Constants.UNWANTED_BLOCK_DATA, r.getCell(37).getStringCellValue());
			}
			cMap.put(r.getCell(1).getStringCellValue(), map);
		}
		return cMap;
		
	}
	
	
}
