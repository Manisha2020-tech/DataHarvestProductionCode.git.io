

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class MainFile {

	public static void main(String[] args) {
		 String base64Coded = "<AttachmentBinary><FileName>damaged__roof__0.jpg</FileName><Source><Attachment>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</Attachment></Source><Category>RC</Category></AttachmentBinary>";
		  try {
		  String html = "<soapenv:Envelope xmlns:soapenv='http://schemas.xmlsoap.org/soap/envelope/' xmlns:ser='http://serviceinterface_v1.b2b.audatex.com'>"
					+ "<soapenv:Header/>"
					+ "<soapenv:Body>"
						+ "<ser:addAttachmentsToTaskRequest>"
		         			+ "<ser:parameter>"
		         				+ "<ser:name>loginId</ser:name>"
		         				+ "<ser:value>dhfl1</ser:value>"
		         			+ "</ser:parameter>"
		         			+ "<ser:parameter>"
		         				+ "<ser:name>password</ser:name>"
		         				+ "<ser:value>dhfl1</ser:value>"
		         			+ "</ser:parameter>"
		         			+ "<ser:parameter>"
		         				+ "<ser:name>taskId</ser:name>"
		         				+ "<ser:value>CD87D1CA-7342-179E-0DE2-D4CDDFCA5B40</ser:value>"
		         			+ "</ser:parameter>"
		         			+ "<ser:payload>"
		         			+ "<AttachmentBinaryList>"
		         				+ base64Coded
		         			+ "</AttachmentBinaryList>"
		         			+ "</ser:payload>"
		         		+ "</ser:addAttachmentsToTaskRequest>"
		         	+ "</soapenv:Body>"
		         	+ "</soapenv:Envelope>";
			System.out.println(html);
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost("https://www-a.audatex.net/b2b/services/AttachmentService_v1?wsdl");
			
			StringEntity strEntity = new StringEntity(html);
			
			post.setHeader("SOAPAction", "addAttachmentsToTaskRequest");
			strEntity.setContentType("text/xml");
			post.setEntity(strEntity);

	      HttpResponse response = client.execute(post);
	      //HttpEntity httpEntity = (HttpEntity) response.getEntity();
	      //String resString = EntityUtils.toString(httpEntity);
	      System.out.println(response);
		  } catch (Exception e){
			  e.printStackTrace();
		  }
		  

	}

}
