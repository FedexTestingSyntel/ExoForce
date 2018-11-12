package Data_Structures;

import API_Calls.General_API_Calls;

public class CMAC_Data {
	
	//general variables for validation
	public String Level = "";
	public String OAuth_Token_URL = "";
	public String OAuth_Token_Client_ID = "";
	public String OAuth_Token_Client_Secret = "";
	public String OAuth_Token = "";
	//Project CRUD
	public String Create_Project_URL = "";
	public String Retrieve_Project_URL = "";
	public String Update_Project_URL = "";
	public String Delete_Project_URL = "";
	//Resource CRUD
	public String Create_Resource_URL = "";
	public String Retrieve_Resource_URL = "";
	public String Update_Resource_URL = "";
	public String Delete_Resource_URL = "";
	//Company CRUD
	public String Create_Company_URL = "";
	public String Retrieve_Company_URL = "";
	public String Update_Company_URL = "";
	public String Delete_Company_URL = "";
	
	//Stores the data for each individual level
	private static CMAC_Data DataClass[] = new CMAC_Data[8];
		
	public static CMAC_Data LoadVariables(String Level){
		int intLevel = Integer.parseInt(Level);
		//if the level details were already loaded then return detail.
		if (DataClass[intLevel] != null) {
			return DataClass[intLevel];
		}
		
		CMAC_Data DC = new CMAC_Data();
		DC.Level = Level;
		
		//get the domain section of the different end points.
		String LevelIdentifier[] = null;
  		switch (Level) {
  		case "1":
  			LevelIdentifier = new String[] {"", ""}; break;
  		case "2":
  			LevelIdentifier = new String[] {"https://cmac-dev.app.wtcdev2.paas.fedex.com", ""}; break;
  		case "3":
  			LevelIdentifier = new String[] {"", ""}; break;
  		case "4":
  			LevelIdentifier = new String[] {"", ""}; break;
  		case "5":
  			LevelIdentifier = new String[] {"", ""}; break;
  		case "6":
  			LevelIdentifier = new String[] {"", ""}; break;
  		case "7":
  			LevelIdentifier = new String[] {"", ""}; break;
		}
  		
  		//Project
		DC.Create_Project_URL = LevelIdentifier[0] + "/cmac/v3/projects";
		DC.Retrieve_Project_URL = LevelIdentifier[0] + "/cmac/v3/projects/{UUID}";
		DC.Update_Project_URL = LevelIdentifier[0] + "/cmac/v3/updateProject";
		DC.Delete_Project_URL = LevelIdentifier[0] + "/cmac/v3/projects/{applicationUUID}";
		//Resources
		DC.Create_Resource_URL = LevelIdentifier[0] + "/cmac/v3/resources";
		DC.Retrieve_Resource_URL = LevelIdentifier[0] + "/cmac/v3/resources/{UUID}";
		DC.Update_Resource_URL = LevelIdentifier[0] + "/cmac/v3/updateResource";
		DC.Delete_Resource_URL = LevelIdentifier[0] + "/cmac/v3/resources/{applicationUUID}";
		//Company
		DC.Create_Company_URL = LevelIdentifier[0] + "/cmac/v3/company";
		DC.Retrieve_Company_URL = LevelIdentifier[0] + "/cmac/v3/company/{UUID}";
		DC.Update_Company_URL = LevelIdentifier[0] + "/cmac/v3/updateCompany";
		DC.Delete_Company_URL = LevelIdentifier[0] + "/cmac/v3/company/{UUID}";
		
		//Based on the method that is being called the array list will be populated. This will make the TestNG Pass/Fail results more relevant.
		switch (Level) {
		case "1":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "2":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "3":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "4":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "5":
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "6"://need to update these values manually, do not share
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		case "7"://need to update these values manually, do not share
			DC.OAuth_Token_Client_ID = "";
			DC.OAuth_Token_Client_Secret ="";
			break;
		}
		
		if (DC.OAuth_Token_Client_ID != "") {
			DC.OAuth_Token = General_API_Calls.getAuthToken(DC.OAuth_Token_URL, DC.OAuth_Token_Client_ID , DC.OAuth_Token_Client_Secret);
		}
		
		DataClass[intLevel] = DC;
		
		return DC;
	}
}
