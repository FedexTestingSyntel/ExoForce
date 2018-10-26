package Data_Structures;

public class CMAC_Data {
	public String OAuth_Token;
	public String Level;
	public String OAuthToken_URL;
	public String Client_ID;
	public String Client_Secret;
	public String Create_Project_URL;
	public String Retrieve_Project_URL;
	public String Update_Project_URL;
	public String Delete_Project_URL;
	public String Create_Credentials_Project_URL;
	public String Delete_Credential_URL;
	public String DeleteResource_URL;
	public String Create_Resource_Project_URL;
	
	/*  before hybrid redesign
	public String P_UserID[] = new String[1];
	public String P_User_Password[] = new String[1];
	public String P_Cookie[] = new String[1];
	public String P_UUID[] = new String[1];
	public String NonP_UserID[] = new String[1];
	public String NonP_User_Password[] = new String[1];
	public String NonP_Cookie[] = new String[1];
	public String NonP_UUID[] = new String[1];
	*/
	
	public static CMAC_Data LoadVariables(String Level){
		CMAC_Data DC = new CMAC_Data();
		switch (Level) { //Based on the method that is being called the array list will be populated. This will make the TestNG Pass/Fail results more relevant.
		case "1":
			break;
		case "2":
			DC.Level = Level;
			DC.OAuthToken_URL = "OAuthToken_URL";
			DC.Client_ID = "Client_ID";
			DC.Client_Secret = "Client_Secret";
			DC.Create_Project_URL = "https://cmac-dev.app.wtcdev2.paas.fedex.com/cmac/v3/projects";
			DC.Retrieve_Project_URL = "https://cmac-dev.app.wtcdev2.paas.fedex.com/cmac/v3/projects/{UUID}";
			DC.Update_Project_URL = "https://cmac-dev.app.wtcdev2.paas.fedex.com/cmac/v3/updateProject";
			DC.Delete_Project_URL = "https://cmac-dev.app.wtcdev2.paas.fedex.com/cmac/v3/projects/{applicationUUID}";
			break;
		case "3":
			break;
		case "4":
			break;
		case "5":
			break;
		case "6":
			break;
		case "7":
			break;
		}
		
		return DC;
	}
	
}
