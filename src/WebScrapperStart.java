import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.audaharvest.iservices.IDataHarvester;
import com.audaharvest.model.ConstantParameters;
import com.audaharvest.services.DataHarvester;


//Audatex Dataharvest Software
//Version 1.6
public class WebScrapperStart {

	public static void main(String[] args) {
		try {
			Properties prop = new Properties();
			InputStream inpStream = new FileInputStream(new File(System.getProperty("user.home")+"/jscrapper.properties"));
			prop.load(inpStream);
			ConstantParameters.setRowStart(prop.getProperty("com.dataharvest.row.start"));
			ConstantParameters.setRowEnd(prop.getProperty("com.dataharvest.row.end"));
			ConstantParameters.setSheetName(prop.getProperty("com.dataharvest.sheet.name"));
			ConstantParameters.setThreadDelay(prop.getProperty("com.dataharvest.thread.delay"));
			ConstantParameters.setThreadDelayTime(prop.getProperty("com.dataharvest.thread.delay.time"));
			ConstantParameters.setSeleniumRequired(prop.getProperty("com.dataharvest.selenium.required"));
			ConstantParameters.setLoggingWOWO(prop.getProperty("com.dataharvest.logging.wowo"));
			ConstantParameters.setLogFilePath(prop.getProperty("com.audaharvest.log.file.path"));
			ConstantParameters.setWebScrapperMode(prop.getProperty("com.dataharvest.web.scrapper.mode"));
			ConstantParameters.setTrainingDataRowStart(prop.getProperty("com.dataharvest.training.data.row.start"));
			ConstantParameters.setTrainingDataRowEnd(prop.getProperty("com.dataharvest.training.data.row.end"));
			ConstantParameters.setTrainingDataSheetName(prop.getProperty("com.dataharvest.training.data.sheet.name"));
			ConstantParameters.setUrlsparamsdefsFile(prop.getProperty("com.dataharvest.urlsparamsdefs.file.name"));
			ConstantParameters.setManufacturerFile(prop.getProperty("com.dataharvest.manufacturer.file.name"));
			ConstantParameters.setLanguageSelection(prop.getProperty("com.dataharvest.language.selection"));
		} catch (IOException io) {
			io.printStackTrace();
		}
		String manufacCodesPath = System.getProperty("user.home") + "\\"+ConstantParameters.getManufacturerFile();
		String urlsParamsDefsPath = System.getProperty("user.home") + "\\"+ConstantParameters.getUrlsparamsdefsFile();		
		IDataHarvester harvest = new DataHarvester(manufacCodesPath, urlsParamsDefsPath);
		harvest.startScrapping();	
		System.out.println("Bravo! File sucessfully written!!");
		System.out.println(((DataHarvester)harvest).getCountMessage());
	}
}
