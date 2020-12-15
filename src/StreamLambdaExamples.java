import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StreamLambdaExamples {

	public static void main(String[] args) {
		
		
		
		
	}
	
	private List<Users> getUsersList() {
		String sqlQuery = "Select NAME, EMAILID from USERS";
		Collection <Map <String, String>> rows = null;
		List<Users> usersList = new ArrayList<> ();
		
		rows.stream().map( (row) -> {
			Users user = new Users();
			user.setName("God");
			user.setEmailId("god@gmail.com");
			return user;
		}).forEach((userObj) -> {
			usersList.add(userObj);
		});
 		
		return usersList;
	}
	
	
	public class Users {
		private String name;
		private String emailId;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getEmailId() {
			return emailId;
		}
		public void setEmailId(String emailId) {
			this.emailId = emailId;
		}		
	}

}


