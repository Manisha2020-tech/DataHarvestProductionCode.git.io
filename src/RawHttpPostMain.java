import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringJoiner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import org.apache.commons.codec.binary.Base64;

public class RawHttpPostMain {

	public static void main(String[] args) {
		
		File file = new File("C:\\Users\\inndkrj\\Desktop\\test_api_request2.json");

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			imageString();
			FileReader fis = new FileReader(file);
			br = new BufferedReader(fis);
			String line = null;
			
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.readLine();
			br.close();
			fis.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		HttpPost httpPost = new HttpPost("https://r3pi-dev.caplena.com/api");
		httpPost.setHeader("Authorization", "R3PIZ doubledowndoubleup");
		
		HttpClient client = HttpClientBuilder.create().build();
		
		try {
			/*String fileString = imageString();
			String jsonData = '{
				"images":[
					{"imagedata":
						
					}'
*/			StringEntity strEntity = new StringEntity(sb.toString());
			httpPost.setEntity(strEntity);
			HttpResponse response = client.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();
			String resString = EntityUtils.toString(httpEntity);
			System.out.println(resString);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public static String imageString() {
		StringJoiner joiner = new StringJoiner("^");
		File imageFile = new File("C:\\Users\\inndkrj\\Desktop\\carParts\\frontRight.jpg");
		
		 String encodedfile = null;
         try {
             FileInputStream fileInputStreamReader = new FileInputStream(imageFile);
             byte[] bytes = new byte[(int)imageFile.length()];
             fileInputStreamReader.read(bytes);
             encodedfile = new String( Base64.encodeBase64(bytes));
             
         } catch (FileNotFoundException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }

		/*byte[] bytes  = imageFile.toString().getBytes();
		Base64 base64 =  new Base64();
		String encodedfile = new String(base64.encode(bytes));
        joiner.add(encodedfile);*/
		System.out.println(encodedfile.toString());
		return encodedfile.toString();
		
	}

}
