import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SplitCheck {

	public static void main(String[] args) {
		String link  = "https://jamesbradenford.leadbox.info/param1/param2.json/param3";
		
		String[] paramsArray = new String[3];
		int[] indexArray= new int[3];
		paramsArray[0] = "param1";
		paramsArray[1] = "param2";
		paramsArray[2] = "param3";
		
		//String[] array = link.split("(param1|param2|param3)");
		Map<String, Integer> paramsIndexMap = new HashMap<String, Integer>();
		
		for(int i=0; i < paramsArray.length; i++) {
			int position = link.indexOf(paramsArray[i]);
			paramsIndexMap.put(paramsArray[i], position);
		}
		String strOutput = link;
		for(Map.Entry<String, Integer> replacement: paramsIndexMap.entrySet()) {
		    strOutput = strOutput.replace(replacement.getKey().toString(), replacement.getValue().toString());
		}
		System.out.println(strOutput);
		
		double price = Double.parseDouble("13999.49");
		DecimalFormat df = new DecimalFormat("#.00");
		
		
		System.out.println(df.format(price));

	}

}
