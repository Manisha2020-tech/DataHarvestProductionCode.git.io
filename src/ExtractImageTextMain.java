import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.audaharvest.common.ExtractTextFromImage;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class ExtractImageTextMain {

	public static void main(String[] args) {
		
		ExtractImageTextMain app = new ExtractImageTextMain();
		/*try {
	        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
	            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

	        } };

	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			
			URL url = new URL("https://veritasmotorsinc.ca/images/banner_sold.png");
			BufferedImage image = ImageIO.read(url);
			Graphics2D g2 = image.createGraphics();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			ImageIO.write(image, "png", baos);
			
			String str = new String(baos.toString());
			
			System.out.println(image);
	        
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		try {
			
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
	            public void checkServerTrusted(X509Certificate[] certs, String authType) { }

	        } };

	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			
			ExtractTextFromImage extractTextFromImage = new ExtractTextFromImage();
			boolean sold = extractTextFromImage.getImageText("https://veritasmotorsinc.ca/images/banner_sold.png");
			System.out.println(sold);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getImgText(String imageLocation) {
		ITesseract instance = new Tesseract();
		
		 try 
	      {
	         String imgText = instance.doOCR(new File(imageLocation));
	         System.out.println(imgText);
	         return imgText;
	      } 
	      catch (TesseractException e) 
	      {
	         e.getMessage();
	         return "Error while reading image";
	      }
	}
	
	private static String getViewSource(String url) {
		StringBuilder sb = new StringBuilder();
		try {
			URL  strUrl = new URL(url);
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
	
	

}
