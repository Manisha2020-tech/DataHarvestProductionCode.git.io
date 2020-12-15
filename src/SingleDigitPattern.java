import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleDigitPattern {

	public static void main(String[] args) {
		
		//Pattern pattern = Pattern.compile("((?<!\\S)\\d(?!\\S) jours)|(heurs)|(heures)");
		Pattern pattern = Pattern.compile("((?<!\\S)[2-7](?!\\S) jours)|(heurs)|(heures)|(1 jour)");	
		Matcher matcher = pattern.matcher("2 jour");
		while(matcher.find()) {
			System.out.println("True");
		}

	}

}
