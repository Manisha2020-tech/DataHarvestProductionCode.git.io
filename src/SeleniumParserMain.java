import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.audaharvest.common.WebDriverUtil;

public class SeleniumParserMain {

	public static void main(String[] args) {
		WebDriverUtil driverUtil = new WebDriverUtil();
		Document doc = null;
		try {
			sslCert();
			Connection con = Jsoup.connect("https://www.auto-land.ca/copy-of-2016-equinox-awd")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.referrer("http://www.google.com")
					.ignoreContentType(true)
					.followRedirects(true)
					.timeout(60*1000);

			Connection.Response res = con.execute();

			if(res.statusCode() == 200) {
				doc = con.get();
			}
			/*WebDriver driver = driverUtil.getFirefoxDriver();
	        driver.get("https://www.auto-land.ca/copy-of-2016-equinox-awd");
	        String href = driver.findElement(By.xpath("/html/head/link[9]")).getAttribute("href");
	        System.out.println(href);
	        driver.quit();
	        String pageSource2 = getViewSource(href);
	        WebDriver driver2 = driverUtil.getFirefoxDriver();
	        String result = driver2.getPageSource();*/
	        /*doc = Jsoup.parse(pageSource2);
	        String text = doc.select("p:eq(25)").get(0).text();*/
	        
	        //String text = driver2.findElement(By.xpath("/p[25]")).getText();
	        //driver2.quit();
			System.out.println(doc);
			String href = doc.select("[as=fetch]").get(1).attr("href");
	        System.out.println(href);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static String getViewSource(String url) {
		StringBuilder sb = new StringBuilder();
		try {
			URL strUrl = new URL(url);
			HttpsURLConnection ucon = (HttpsURLConnection) strUrl.openConnection();
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
	
	private static void sslCert() {
		try {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

        } };
		SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	

}
