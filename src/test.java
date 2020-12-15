import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.audaharvest.common.WebDriverUtil;
import com.audaharvest.constants.Constants;
import com.audaharvest.iservices.ICSVFileWriter;
import com.audaharvest.model.Vehicle;
import com.audaharvest.services.CSVFileWriter;

public class test {
public static void main(String[] s) throws IOException, JSONException {

	

//	driver.getPageSource();
//	System.out.println(driver.findElement(By.cssSelector("script + title")));
//	System.out.println(driver.findElement(By.cssSelector("script + title")).getText());
//	System.out.println(driver.findElement(By.tagName("title")));
//	System.out.println(driver.findElement(By.tagName("title")).getText());
	System.out.println("Ended");
	
	
	//json file
	
	File f = new File("C:\\Users\\Khushboo.Agarwal\\Downloads\\ONMKF_new.json");
	BufferedReader br = new BufferedReader(new FileReader(f));
	StringBuilder fileDta = new StringBuilder();
	String line = br.readLine();
	while(line!=null) {
		fileDta.append(line);
		fileDta.append("\n");
		line = br.readLine();
	}
	
	JSONObject jsono = new JSONObject(fileDta.toString());
	
	FileWriter fw = new FileWriter(new File("C:\\Users\\Khushboo.Agarwal\\Downloads\\ONMKF_new3.json"));
	fw.write(fileDta.toString());
	fw.close();
	JSONObject json = new JSONObject("{\r\n" + 
			"    \"2020\": 00,\r\n" + 
			"    \"body_style\": \"Sedan\",\r\n" + 
			"    \"color_ext\": \"Gray\",\r\n" + 
			"    \"color_int\": \"USED\",\r\n" + 
			"    \"cylinders\": 4,\r\n" + 
			"    \"description\": \" <b>Bluetooth,  Heated Seats,  Remote Keyless Entry,  Air Conditioning,  Steering Wheel Audio Control!<\\/b><br> <br> Check out the large selection of pre-owned vehicles at Mike Knapp Ford!<br> <br>   Great trunk space and excellent passenger legroom is what makes this 2014 Hyundai Elantra stand out. This  2014 Hyundai Elantra is for sale today in Welland. <br> <br>The 2014 Hyundai Elantra is one of the top picks for an economical compact sedan. For this years Hyundai Elantra sedan receives numerous changes, including slight cosmetic modifications inside and out, a new Sport trim level with a more powerful engine, upgraded infotainment features and a quieter cabin.This  sedan has 148,349 kms. It's  gray in colour  . It has a 6 speed auto transmission and is powered by a  148HP 1.8L 4 Cylinder Engine.   This vehicle has been upgraded with the following features: Bluetooth,  Heated Seats,  Remote Keyless Entry,  Air Conditioning,  Steering Wheel Audio Control,  Cruise Control. <br> <br>To apply right now for financing use this link : <a href=https:\\/\\/www.mikeknappford.com\\/creditFrame.php target=_blank>https:\\/\\/www.mikeknappford.com\\/creditFrame.php<\\/a><br><br> <br><br> Buy this vehicle now for the lowest bi-weekly payment of <b>$55.57<\\/b> with $0 down for 84 months @ 6.99% APR O.A.C. ( Plus applicable taxes -  Plus applicable fees  ).  See dealer for details. <br> <br><br>At Mike Knapp Ford's Pre-Owned Centre, we strive to offer the most competitive prices on our pre-owned vehicles every time. We search the internet and constantly compare our prices to ensure our guests get the best price, in a timely manner, with no aggravation. We do not artificially inflate our prices in the hope of winning a negotiating contest with our customers. With today's accessible information available online, the market does the negotiating for us and more importantly, for YOU! All pre-owned vehicles at Mike Knapp Ford Sales are carefully selected through a multi-point inspection process to guarantee you the best quality pre-owned vehicle. Our vehicles are fully re-conditioned and certified by factory trained technicians. A complete CarProof Report of the vehicle's history is always readily available. We offer Major Bank fully open financing agreements with very low rates OAC! We have established a separate credit department to assist you if you have had any past credit issues, including bankruptcy or divorce. We invite you to fill out a confidential credit application at www.MikeKnappFord.com Come in and see why we want to be your #1 Used Car Dealer in Welland, St Catharines, Niagara Falls, Hamilton, Cayuga, Mississauga, Burlington, Oakville, Kitchener, Waterloo, Guelph, London, Windsor, Chatham London & Toronto! At Mike Knapp Ford ... Our commitment is not to build just customers ..... But great, longtime relationships; by earning your trust, respect & meeting your expectations every day every time! AT MIKE KNAPP FORD WE ARE HERE TO SERVE YOU!<br> Come by and check out our fleet of 50+ used cars and trucks and 50+ new cars and trucks for sale in Welland.  o~o \",\r\n" + 
			"    \"doors\": 4,\r\n" + 
			"    \"drivetrain\": \"FWD\",\r\n" + 
			"    \"engine\": \"148HP 1.8L 4 Cylinder Engine\",\r\n" + 
			"    \"engine_size\": \"1.8\",\r\n" + 
			"    \"equipment\": [{\r\n" + 
			"            \"equip\": \"Wheels: 16\\\" x 6.5\\\"J Steel w\\/Cover\",\r\n" + 
			"            \"id\": 101771\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Heated Front Bucket Seats\",\r\n" + 
			"            \"id\": 101772\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Premium Cloth Seating Surfaces\",\r\n" + 
			"            \"id\": 101773\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"AM\\/FM\\/XM\\/CD\\/MP3 Audio System w\\/6 Speakers\",\r\n" + 
			"            \"id\": 101774\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"4-Wheel Disc Brakes\",\r\n" + 
			"            \"id\": 101775\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"6 Speakers\",\r\n" + 
			"            \"id\": 101776\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Air Conditioning\",\r\n" + 
			"            \"id\": 101777\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Electronic Stability Control\",\r\n" + 
			"            \"id\": 101778\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front Bucket Seats\",\r\n" + 
			"            \"id\": 101779\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Tachometer\",\r\n" + 
			"            \"id\": 101780\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"ABS brakes\",\r\n" + 
			"            \"id\": 101781\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Brake assist\",\r\n" + 
			"            \"id\": 101782\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"CD player\",\r\n" + 
			"            \"id\": 101783\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Delay-off headlights\",\r\n" + 
			"            \"id\": 101784\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Driver door bin\",\r\n" + 
			"            \"id\": 101785\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Driver vanity mirror\",\r\n" + 
			"            \"id\": 101786\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Dual front impact airbags\",\r\n" + 
			"            \"id\": 101787\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Dual front side impact airbags\",\r\n" + 
			"            \"id\": 101788\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front anti-roll bar\",\r\n" + 
			"            \"id\": 101789\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front reading lights\",\r\n" + 
			"            \"id\": 101790\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front wheel independent suspension\",\r\n" + 
			"            \"id\": 101791\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Fully automatic headlights\",\r\n" + 
			"            \"id\": 101792\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Heated door mirrors\",\r\n" + 
			"            \"id\": 101793\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Heated front seats\",\r\n" + 
			"            \"id\": 101794\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Illuminated entry\",\r\n" + 
			"            \"id\": 101795\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"MP3 decoder\",\r\n" + 
			"            \"id\": 101796\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Occupant sensing airbag\",\r\n" + 
			"            \"id\": 101797\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Outside temperature display\",\r\n" + 
			"            \"id\": 101798\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Overhead airbag\",\r\n" + 
			"            \"id\": 101799\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Overhead console\",\r\n" + 
			"            \"id\": 101800\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Panic alarm\",\r\n" + 
			"            \"id\": 101801\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Passenger door bin\",\r\n" + 
			"            \"id\": 101802\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Passenger vanity mirror\",\r\n" + 
			"            \"id\": 101803\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Power door mirrors\",\r\n" + 
			"            \"id\": 101804\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Power steering\",\r\n" + 
			"            \"id\": 101805\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Power windows\",\r\n" + 
			"            \"id\": 101806\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Rear anti-roll bar\",\r\n" + 
			"            \"id\": 101807\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Rear window defroster\",\r\n" + 
			"            \"id\": 101808\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Remote keyless entry\",\r\n" + 
			"            \"id\": 101809\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Security system\",\r\n" + 
			"            \"id\": 101810\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Speed control\",\r\n" + 
			"            \"id\": 101811\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Speed-sensing steering\",\r\n" + 
			"            \"id\": 101812\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Split folding rear seat\",\r\n" + 
			"            \"id\": 101813\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Steering wheel mounted audio controls\",\r\n" + 
			"            \"id\": 101814\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Telescoping steering wheel\",\r\n" + 
			"            \"id\": 101815\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Tilt steering wheel\",\r\n" + 
			"            \"id\": 101816\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Traction control\",\r\n" + 
			"            \"id\": 101817\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Trip computer\",\r\n" + 
			"            \"id\": 101818\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Variably intermittent wipers\",\r\n" + 
			"            \"id\": 101819\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"AM\\/FM radio: XM\",\r\n" + 
			"            \"id\": 101820\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Bumpers: body-colour\",\r\n" + 
			"            \"id\": 101821\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Display: digital\\/analog\",\r\n" + 
			"            \"id\": 101822\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Wheels: 16\\\" x 6.5\\\"J Steel w\\/Cover\",\r\n" + 
			"            \"id\": 102756\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Heated Front Bucket Seats\",\r\n" + 
			"            \"id\": 102757\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Premium Cloth Seating Surfaces\",\r\n" + 
			"            \"id\": 102758\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"AM\\/FM\\/XM\\/CD\\/MP3 Audio System w\\/6 Speakers\",\r\n" + 
			"            \"id\": 102759\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"4-Wheel Disc Brakes\",\r\n" + 
			"            \"id\": 102760\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"6 Speakers\",\r\n" + 
			"            \"id\": 102761\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Air Conditioning\",\r\n" + 
			"            \"id\": 102762\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Electronic Stability Control\",\r\n" + 
			"            \"id\": 102763\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front Bucket Seats\",\r\n" + 
			"            \"id\": 102764\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Tachometer\",\r\n" + 
			"            \"id\": 102765\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"ABS brakes\",\r\n" + 
			"            \"id\": 102766\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Brake assist\",\r\n" + 
			"            \"id\": 102767\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"CD player\",\r\n" + 
			"            \"id\": 102768\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Delay-off headlights\",\r\n" + 
			"            \"id\": 102769\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Driver door bin\",\r\n" + 
			"            \"id\": 102770\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Driver vanity mirror\",\r\n" + 
			"            \"id\": 102771\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Dual front impact airbags\",\r\n" + 
			"            \"id\": 102772\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Dual front side impact airbags\",\r\n" + 
			"            \"id\": 102773\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front anti-roll bar\",\r\n" + 
			"            \"id\": 102774\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front reading lights\",\r\n" + 
			"            \"id\": 102775\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front wheel independent suspension\",\r\n" + 
			"            \"id\": 102776\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Fully automatic headlights\",\r\n" + 
			"            \"id\": 102777\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Heated door mirrors\",\r\n" + 
			"            \"id\": 102778\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Heated front seats\",\r\n" + 
			"            \"id\": 102779\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Illuminated entry\",\r\n" + 
			"            \"id\": 102780\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"MP3 decoder\",\r\n" + 
			"            \"id\": 102781\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Occupant sensing airbag\",\r\n" + 
			"            \"id\": 102782\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Outside temperature display\",\r\n" + 
			"            \"id\": 102783\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Overhead airbag\",\r\n" + 
			"            \"id\": 102784\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Overhead console\",\r\n" + 
			"            \"id\": 102785\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Panic alarm\",\r\n" + 
			"            \"id\": 102786\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Passenger door bin\",\r\n" + 
			"            \"id\": 102787\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Passenger vanity mirror\",\r\n" + 
			"            \"id\": 102788\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Power door mirrors\",\r\n" + 
			"            \"id\": 102789\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Power steering\",\r\n" + 
			"            \"id\": 102790\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Power windows\",\r\n" + 
			"            \"id\": 102791\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Rear anti-roll bar\",\r\n" + 
			"            \"id\": 102792\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Rear window defroster\",\r\n" + 
			"            \"id\": 102793\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Remote keyless entry\",\r\n" + 
			"            \"id\": 102794\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Security system\",\r\n" + 
			"            \"id\": 102795\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Speed control\",\r\n" + 
			"            \"id\": 102796\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Speed-sensing steering\",\r\n" + 
			"            \"id\": 102797\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Split folding rear seat\",\r\n" + 
			"            \"id\": 102798\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Steering wheel mounted audio controls\",\r\n" + 
			"            \"id\": 102799\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Telescoping steering wheel\",\r\n" + 
			"            \"id\": 102800\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Tilt steering wheel\",\r\n" + 
			"            \"id\": 102801\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Traction control\",\r\n" + 
			"            \"id\": 102802\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Trip computer\",\r\n" + 
			"            \"id\": 102803\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Variably intermittent wipers\",\r\n" + 
			"            \"id\": 102804\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"AM\\/FM radio: XM\",\r\n" + 
			"            \"id\": 102805\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Bumpers: body-colour\",\r\n" + 
			"            \"id\": 102806\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Display: digital\\/analog\",\r\n" + 
			"            \"id\": 102807\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Wheels: 16\\\" x 6.5\\\"J Steel w\\/Cover\",\r\n" + 
			"            \"id\": 112210\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Heated Front Bucket Seats\",\r\n" + 
			"            \"id\": 112211\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Premium Cloth Seating Surfaces\",\r\n" + 
			"            \"id\": 112212\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"AM\\/FM\\/XM\\/CD\\/MP3 Audio System w\\/6 Speakers\",\r\n" + 
			"            \"id\": 112213\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"4-Wheel Disc Brakes\",\r\n" + 
			"            \"id\": 112214\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"6 Speakers\",\r\n" + 
			"            \"id\": 112215\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Air Conditioning\",\r\n" + 
			"            \"id\": 112216\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Electronic Stability Control\",\r\n" + 
			"            \"id\": 112217\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front Bucket Seats\",\r\n" + 
			"            \"id\": 112218\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Tachometer\",\r\n" + 
			"            \"id\": 112219\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"ABS brakes\",\r\n" + 
			"            \"id\": 112220\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Brake assist\",\r\n" + 
			"            \"id\": 112221\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"CD player\",\r\n" + 
			"            \"id\": 112222\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Delay-off headlights\",\r\n" + 
			"            \"id\": 112223\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Driver door bin\",\r\n" + 
			"            \"id\": 112224\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Driver vanity mirror\",\r\n" + 
			"            \"id\": 112225\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Dual front impact airbags\",\r\n" + 
			"            \"id\": 112226\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Dual front side impact airbags\",\r\n" + 
			"            \"id\": 112227\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front anti-roll bar\",\r\n" + 
			"            \"id\": 112228\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front reading lights\",\r\n" + 
			"            \"id\": 112229\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Front wheel independent suspension\",\r\n" + 
			"            \"id\": 112230\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Fully automatic headlights\",\r\n" + 
			"            \"id\": 112231\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Heated door mirrors\",\r\n" + 
			"            \"id\": 112232\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Heated front seats\",\r\n" + 
			"            \"id\": 112233\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Illuminated entry\",\r\n" + 
			"            \"id\": 112234\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"MP3 decoder\",\r\n" + 
			"            \"id\": 112235\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Occupant sensing airbag\",\r\n" + 
			"            \"id\": 112236\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Outside temperature display\",\r\n" + 
			"            \"id\": 112237\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Overhead airbag\",\r\n" + 
			"            \"id\": 112238\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Overhead console\",\r\n" + 
			"            \"id\": 112239\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Panic alarm\",\r\n" + 
			"            \"id\": 112240\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Passenger door bin\",\r\n" + 
			"            \"id\": 112241\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Passenger vanity mirror\",\r\n" + 
			"            \"id\": 112242\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Power door mirrors\",\r\n" + 
			"            \"id\": 112243\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Power steering\",\r\n" + 
			"            \"id\": 112244\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Power windows\",\r\n" + 
			"            \"id\": 112245\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Rear anti-roll bar\",\r\n" + 
			"            \"id\": 112246\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Rear window defroster\",\r\n" + 
			"            \"id\": 112247\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Remote keyless entry\",\r\n" + 
			"            \"id\": 112248\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Security system\",\r\n" + 
			"            \"id\": 112249\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Speed control\",\r\n" + 
			"            \"id\": 112250\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Speed-sensing steering\",\r\n" + 
			"            \"id\": 112251\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Split folding rear seat\",\r\n" + 
			"            \"id\": 112252\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Steering wheel mounted audio controls\",\r\n" + 
			"            \"id\": 112253\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Telescoping steering wheel\",\r\n" + 
			"            \"id\": 112254\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Tilt steering wheel\",\r\n" + 
			"            \"id\": 112255\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Traction control\",\r\n" + 
			"            \"id\": 112256\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Trip computer\",\r\n" + 
			"            \"id\": 112257\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Variably intermittent wipers\",\r\n" + 
			"            \"id\": 112258\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"AM\\/FM radio: XM\",\r\n" + 
			"            \"id\": 112259\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Bumpers: body-colour\",\r\n" + 
			"            \"id\": 112260\r\n" + 
			"        }, {\r\n" + 
			"            \"equip\": \"Display: digital\\/analog\",\r\n" + 
			"            \"id\": 112261\r\n" + 
			"        }\r\n" + 
			"    ],\r\n" + 
			"    \"fuel\": \"Gasoline\",\r\n" + 
			"    \"image_last_update\": \"2020-10-28 04:00:02\",\r\n" + 
			"    \"images\": [{\r\n" + 
			"            \"default_pic\": \"1\",\r\n" + 
			"            \"image_key\": \"b_5f7b7c158a667_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26582\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b12e8e94_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26583\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b1531867_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26584\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b17480e0_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26585\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b18d192b_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26586\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b1a8796f_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26587\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b1c13e83_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26588\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b1dbb445_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26589\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b1f62b60_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26591\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b20ed6f4_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26592\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b22b207e_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26594\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b2424432_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26595\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b25a86fa_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26597\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b273b915_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26599\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b28d2583_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26601\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b2a764b7_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26603\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b2c5d14d_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26605\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b2de754b_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26606\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b2f7fafe_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26608\r\n" + 
			"        }, {\r\n" + 
			"            \"default_pic\": \"\",\r\n" + 
			"            \"image_key\": \"b_5f734b3116640_122097185_2014_Hyundai_Elantra.jpg\",\r\n" + 
			"            \"image_recid\": 26610\r\n" + 
			"        }\r\n" + 
			"    ],\r\n" + 
			"    \"kms\": 148349\r\n" + 
			"}\r\n" + 
			"");
//	String body = json.getString("body_style");
//	System.out.println(body);
	Set<Vehicle> set = new HashSet<>();
	
	JSONArray array =jsono.getJSONArray("results");
	for(int i = 0; i< array.length(); i++) {
		Vehicle v = new Vehicle();
		JSONObject obj = (JSONObject) array.get(i);
		String body1 = obj.getString("body_style");
		v.setBodystyle(body1);
		//all string and regex 
		System.out.println(body1);
		set.add(v);
	}
	
//	writeFile("https://www.mikeknappford.com/", urlMap, set);
	
	
}
private Set<Vehicle> getVehicles() throws IOException, JSONException {
	Set<Vehicle> set = new HashSet<>();
		File f = new File("C:\\Users\\Khushboo.Agarwal\\Downloads\\ONMKF_new.json");
BufferedReader br = new BufferedReader(new FileReader(f));
StringBuilder fileDta = new StringBuilder();
String line = br.readLine();
while(line!=null) {
	fileDta.append(line);
	fileDta.append("\n");
	line = br.readLine();
}

JSONObject jsono = new JSONObject(fileDta.toString());
	
	JSONArray array =jsono.getJSONArray("results");
	for(int i = 0; i< array.length(); i++) {
		Vehicle v = new Vehicle();
		JSONObject obj = (JSONObject) array.get(i);
		String body1 = obj.getString("body_style");
		v.setBodystyle(body1);
		//all string and regex 
		System.out.println(body1);
		set.add(v);
	}
	return set;
}

private static void writeFile(String rootUrl, Map<String, String> urlMap, Set<Future<Vehicle>> set) {
	ICSVFileWriter csvWriter = new CSVFileWriter();
	String truncatedDomainName = rootUrl.replaceAll("(http://www.|https://www.|http://|https://|/)", "");
	String currentTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
	String pubCode ="";
	if(urlMap.get(Constants.PUB_CODE)!= null && !urlMap.get(Constants.PUB_CODE).isEmpty()){
		pubCode = urlMap.get(Constants.PUB_CODE);
	}
	
	String fileName = System.getProperty("user.home")+"/mainfiles/"+pubCode+truncatedDomainName+currentTime+".csv";
	String garbageFileName = System.getProperty("user.home")+"/garbage/"+"GARBAGE"+pubCode+truncatedDomainName+currentTime+".csv";
	csvWriter.writeCSVFile(fileName, garbageFileName, set, urlMap);
	
}
}
