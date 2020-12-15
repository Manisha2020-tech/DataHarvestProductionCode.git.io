import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.audaharvest.common.SSLTool;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class G3PartitionMain {
	
	public static void main(String[] args) {
		
		//System.out.println(System.getProperty("os.name"));
		//All it does is print out all the properties provided by your Java implementations. It'll give you an idea of what you can find out about your Java environment via properties
		//System.getProperties().list(System.out);
		SSLTool sslTool = new SSLTool();
		sslTool.disableCertificateValidation();
		Document doc = null;
		try {
			Connection con = Jsoup.connect("https://www.autoannoncextra.com/automobiles/annonce-ford-fusion-3572192.html")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
					.referrer("http://www.google.com")
					.ignoreContentType(true)
					.followRedirects(true)
					.timeout(20*1000);

			Connection.Response res = con.execute();

			if(res.statusCode() == 200) {
				doc = con.get();
			}
			String dateStr = doc.select("tr:contains(Date)").get(0).text();
			Translate.execute(dateStr, Language.ENGLISH);
			System.out.println(dateStr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
