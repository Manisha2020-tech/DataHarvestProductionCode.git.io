import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.audaharvest.common.WebDriverUtil;

public class LienSearch {

	public static void main(String[] args) {
		WebDriver driver  = null;
		try {
			WebDriverUtil driverUtil = new WebDriverUtil();
			driver = driverUtil.getFirefoxDriver();			
			driver.get("http://www.rdprm.gouv.qc.ca/Consultation/accueil");
			String header = driver.findElement(By.xpath("/html/body/app-root/app-accueil/div/div/div/div/div")).getText();
			//driver.findElement(By.xpath("/html/body/app-root/app-accueil/div/div/div/div/div")).sendKeys("VIN");
			//driver.findElement(By.xpath("//input[@value='login']")).click();			
			System.out.println(header);
		} catch(Exception e) {
			e.printStackTrace();
		}
        driver.quit();

	}

}
