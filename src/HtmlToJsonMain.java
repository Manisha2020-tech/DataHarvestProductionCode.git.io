import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class HtmlToJsonMain {

	public static void main(String[] args) {
		Document doc = null;
		try {	
			Connection con = Jsoup.connect("https://www.sherwoodhonda.ca/auto/used-2017-jeep-grand-cherokee-trailhawk-sherwood-park-ab/36509187/")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
					.referrer("http:www.google.com")
					.ignoreContentType(true)
					.followRedirects(true)
					.timeout(60*1000);
			Connection.Response res = con.execute();
			
				if(res.statusCode() == 200) {
				doc = con.get();
				}
				
				String viewSource = doc.text();
				/*JSONObject obj = new JSONObject("...");
				String vin = obj.get("VinNumber").toString();*/
				System.out.println(viewSource);
			} catch (Exception e){
				e.printStackTrace();
			}

	}

}
