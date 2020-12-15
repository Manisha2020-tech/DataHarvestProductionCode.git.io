import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.poi.util.SystemOutLogger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.audaharvest.constants.Constants;
import com.audaharvest.services.fieldutils.MileageUtil;

public class JSONMain {

	public static void main(String[] args) {		

		try {
			String filePath = "C://Users//inndkrj//Desktop//vikashvehiclevin.txt";
			FileInputStream fin = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fin);
			/*URL google = new URL ("http://www.google.com");
	      	InputStreamReader isr = new InputStreamReader(google.openStream());*/	       
			BufferedReader inputLine = new BufferedReader(isr);
			String tmp;
			while((tmp = inputLine.readLine()) != null) {
				inputLine.read();
				System.out.println(tmp);
				if(tmp.contains("P0139")) {
					break;
				}

			}
			isr.close();
		}
		catch(FileNotFoundException fe)
		{
			System.out.println("FileNotFoundException : " + fe);
		}
		catch(IOException ioe)
		{
			System.out.println("IOException : " + ioe);
		}
		/*Document doc = null;
		try {
			//doc = (Document) Jsoup.connect(url).timeout(20*1000).get();	
			Connection con = Jsoup.connect("http://www.gaslighthd.com/categories/pre-owned/")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.referrer("http://www.google.com")
					.ignoreContentType(true)
					.followRedirects(true)
					.timeout(60*1000);

			Connection.Response res = con.execute();

				if(res.statusCode() == 200) {
				doc = con.get();
				}
			} catch (IOException e){
				e.printStackTrace();
			}
		Element div = doc.select("div").first();
		for(int i=0; i< 10 ; i++) {
			System.out.println(doc.select("h3").get(i));
			System.out.println(doc.select("h3 + p").get(i));
		}*/

		/*Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);               
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
        ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:/software/phantomjs-2.1.1-windows/bin/phantomjs.exe");
        //WebDriver   driver = new  PhantomJSDriver(caps);
        WebDriver   driver = new  PhantomJSDriver(caps);

		driver.get("http://www.knightfordlincoln.ca/inventory/used-2012-ford-fiesta-s-fwd-hatchback-3fadp4tj1cm206427");
		driver.manage().window().setSize(new Dimension(1920, 1080));
		String pageContent = driver.findElement(By.cssSelector("[itemprop='sku']")).getAttribute("content");
		System.out.println(pageContent);

		driver.quit();
		 */
		/*String str = " 00 76,000";
		boolean isNumber = str.matches("[0-9]+(,[0-9]+)*,?");
		System.out.println(isNumber);*/
		/*Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setJavascriptEnabled(true);               
        ((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
        ((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "C:/software/phantomjs-2.1.1-windows/bin/phantomjs.exe");
        WebDriver   driver = new  PhantomJSDriver(caps);
        driver.get("http://palmerstonmotors.ca/Pre-Owned/adid/13516974/");
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS); 
        String list = driver.findElement(By.xpath("//*[@id='vehicle_information']/div/div[2]/table/tbody/tr[1]/td[2]")).getText();
        System.out.println(list);
        driver.quit();*/
		/*for(WebElement e : list){
			//((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", e);
			//String link = e.getAttribute("href");
			//System.out.println(e.getText());
			try {
			if(e.getText() != null && e.getText().toString().contains("In Stock")) {
				System.out.println(e.getAttribute("href"));
			}

			} catch (Exception el) {
				el.printStackTrace();
			}

		}*/

		/*        driver.get("http://palmerstonmotors.ca/Pre-Owned/adid/13517025/");
        WebElement str = driver.findElement(By.xpath("//*[@id='rmjs-1']"));
        System.out.println(str.getText());*/
		/*String pipeString = "Engine | Color | KM | km";
		String rText = pipeString.replaceAll("(KM|\\|)", "");
		System.out.println(rText);*/

	}

}
