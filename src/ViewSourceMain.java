import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.audaharvest.common.WebDriverUtil;

import java.security.cert.X509Certificate;

public class ViewSourceMain {

	public static void main(String[] args) {
		WebDriverUtil driverUtil = new WebDriverUtil();
		Document doc = null;
		try {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

        } };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        //String vs = getViewSource("https://www.auto-land.ca/copy-of-2016-equinox-awd");
        String vs = getViewSource("https://www.transwesterntruck.com/inventory/?/listings/trucks/for-sale/30243101/2020-mack-anthem-64t?pcid=2001868847");
        //doc = Jsoup.parse(vs);
        /*WebDriver driver = driverUtil.getFirefoxDriver();
        driver.get("https://www.auto-land.ca/copy-of-2016-equinox-awd");
        String href = driver.findElement(By.xpath("/html/head/link[9]")).getAttribute("href");
        System.out.println(href);
        driver.quit();
        String pageSource2 = getViewSource(href);*/
        System.out.println(vs);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static String getViewSource(String url) {
		StringBuilder sb = new StringBuilder();
		try {
			URL  strUrl = new URL(url);
			HttpsURLConnection ucon = (HttpsURLConnection) strUrl.openConnection();
			//ucon.setRequestProperty("content-type", "text/html; Charset=utf-8");
			//ucon.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
			//ucon.setRequestProperty("accept-encoding:", "gzip, deflate, br");
			BufferedReader br = new BufferedReader(new InputStreamReader(ucon.getInputStream(),"UTF-8"));
			String line;
			
			while((line=br.readLine())!= null) {
				sb.append(line);
				sb.append("\n");
			}
			br.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
		
	}
	
	
	
}


