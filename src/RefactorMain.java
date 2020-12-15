import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.audaharvest.common.ExcelFileReader;
import com.audaharvest.constants.Constants;
import com.audaharvest.model.Vehicle;

public class RefactorMain {

	private static int count=1;

	public static void main(String[] args) {
		String manufacCodesPath = System.getProperty("user.home") + "/ManufacturerCodes.xlsx";
		List<String> manufacCodesList = null;
		String excelDataPath = System.getProperty("user.home") + "/NBSRVsackvillerv.com20171013110434.csv";
		int lastIndex = excelDataPath.lastIndexOf("/");
		//System.out.println(excelDataPath +"Has Last index: "+lastIndex);
		//System.out.println(excelDataPath.substring(lastIndex+1, lastIndex+6));
		String pubCode = excelDataPath.substring(lastIndex+1, lastIndex+6);
		String fileName = System.getProperty("user.home")+"/NBSRVsackvillerv.com20171013110435.csv";
		ExcelFileReader excelReader = new ExcelFileReader();
		try {
			manufacCodesList = excelReader.getExcelData(manufacCodesPath);	
			String[] makeArray = new String[manufacCodesList.size()];
			makeArray =  manufacCodesList.toArray(makeArray);
			List<Vehicle> adList = refactorData(excelDataPath, makeArray);
			writeInCSVFile(fileName, adList, pubCode);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static List<Vehicle> refactorData(String excelDataPath, String[] makeArray) {
		List<Vehicle> list = new ArrayList<Vehicle>();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy ="|";
		int minYear = Integer.parseInt(Constants.MIN_YEAR);
		int maxYear = Calendar.getInstance().get(Calendar.YEAR) + 1 ;
		List<Vehicle> adList = new ArrayList<Vehicle>();
		try {
			br = new BufferedReader(new FileReader(excelDataPath));
			while((line = br.readLine()) != null) {
				
				Vehicle vehicle = new Vehicle();
				String[] array = line.toString().split("\\|");
				String headerPara = array[16].replaceAll("(Year|Manufacturer|Model|:)", "");
				for (int i=0; i<makeArray.length;i++) {
					if(headerPara.contains(makeArray[i])) {
						vehicle.setMake(makeArray[i]);
						int makePos = headerPara.toLowerCase().indexOf(vehicle.getMake().toLowerCase());
						int len = makePos+makeArray[i].length();
						String headerParaTruncated = headerPara.substring(len, headerPara.length()).replaceAll("(\\u00a0|,|<br>|\\|,)", " ").trim();
						String[] headerParaTruncatedArray = headerParaTruncated.split(" ");
						vehicle.setModel(headerParaTruncatedArray[0]);
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

				vehicle.setSellerPhoneNo(array[4]);
				vehicle.setTrim(array[5]);
				vehicle.setTransmission(array[6]);
				vehicle.setEngine(array[7]);
				vehicle.setPrice(array[8]);
				vehicle.setMileage(array[9]);
				vehicle.setDealerName(array[10]);
				vehicle.setDealerCity(array[11]);
				vehicle.setDealerProvince(array[12]);
				vehicle.setDealerZipCode(array[13]);
				vehicle.setStockNumber(array[14]);
				vehicle.setVin(array[15]);
				vehicle.setVehicleFeatures(array[16]);
				vehicle.setVehicleDesc(array[17]);
				vehicle.setMakeModelYearDetail(array[18]);
				vehicle.setLocation(array[19]);
				vehicle.setDealerFlag(array[20]);
				vehicle.setUrl(array[21]);
				vehicle.setBodystyle(array[22]);
				vehicle.setExtColour(array[23]);
				vehicle.setIntColour(array[24]);
				vehicle.setDrivetrain(array[25]);
				vehicle.setDoors(array[26]);
				vehicle.setPassengers(array[27]);
				vehicle.setFuel(array[28]);
				vehicle.setModelCode(array[29]);
				//vehicle.set(array[30]);
				adList.add(vehicle);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return adList;
		
	}
	
	public static void writeInCSVFile(String fileName, List<Vehicle> adList, String pubCode) throws IOException{
		if (fileName == null) {
			throw new IllegalArgumentException("File is null, can't export.");
		}
		
		FileWriter fw = new FileWriter(fileName);
		fw.append(Constants.FILE_HEADER);
		fw.append(Constants.NEW_LINE_SEPERATOR);		
		adList.stream().skip(1).forEach(item -> {
			try {
				fw.append(String.valueOf(count++));
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getYear());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getMake());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getModel());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getSellerPhoneNo());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getTrim());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getTransmission());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getEngine());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getPrice());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getMileage());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getDealerName());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getDealerCity());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getDealerProvince());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getDealerZipCode());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getStockNumber());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getVin());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getVehicleFeatures());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getVehicleDesc());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getMakeModelYearDetail());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getLocation());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getDealerFlag());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getUrl());
				//fw.append(list.get().getDomain());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getBodystyle());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getExtColour());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getIntColour());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getDrivetrain());				
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getDoors());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getPassengers());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getFuel());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(item.getModelCode());
				fw.append(Constants.COMMA_DELIMITER);
				fw.append(pubCode);
				fw.append(Constants.NEW_LINE_SEPERATOR);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
	}

}
