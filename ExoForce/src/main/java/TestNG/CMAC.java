package TestNG;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.hamcrest.CoreMatchers;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import TestingFunctions.Helper_Functions;
import API_Calls.*;
import Data_Structures.*;
import SupportClasses.ThreadLogger;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class CMAC{
	static String LevelsToTest = "2"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.
	static CMAC_Data DC[] = new CMAC_Data[8];//Stores the data for each individual level, please see the before class function below for more details.
	static ArrayList<String[]> ResourceList = new ArrayList<String[]>();//this is a list of when multiple resources are added. Will be initialized in before class.
	static ArrayList<String> applicationUUIDToDelete = new ArrayList<String>();
	
	@BeforeClass
	public void beforeClass() {		//implemented as a before class so the OAUTH tokens are only generated once.
		ThreadLogger.LevelsToTest = LevelsToTest;
		ArrayList<String[]> Excel_Data = Helper_Functions.getExcelData(".\\Data\\CMAC_Properties.xls",  "CMAC");//load the relevant information from excel file.
		String Headers[] = Excel_Data.get(0);
		for (int i = 0; i < LevelsToTest.length(); i++) {
			int Level = Integer.parseInt(LevelsToTest.charAt(i) + "");//the rows will correspond to the correct level. With the row 0 being the column titles.
			//below is each column that is expected in the excel and will be loaded.    08/24/18
			String EnvironmentInformation[] = Excel_Data.get(Level);
			DC[Level] = new CMAC_Data();
			for (int j = 0; j < EnvironmentInformation.length; j++) {//added as a precaution to remove spaces from the excel sheet
				EnvironmentInformation[j] = EnvironmentInformation[j].trim();
			}
			//EnvironmentInformation[0] = getAuthToken(EnvironmentInformation[2], EnvironmentInformation[3], EnvironmentInformation[4]);//add token to front of new array after it is generated
			Helper_Functions.PrintOut("Headers: " + Arrays.toString(Headers), true);
			Helper_Functions.PrintOut(Arrays.toString(EnvironmentInformation), true);//print out all of the urls and date for the level, this is just a reference point to executer
			for (int j = 0; j < Headers.length; j++) {
				switch (Headers[j]) { //Based on the method that is being called the array list will be populated. This will make the TestNG Pass/Fail results more relevant.
					case "OAuthToken":
						DC[Level].OAuth_Token = EnvironmentInformation[j];break;
					case "Level":
						DC[Level].Level = EnvironmentInformation[j];break;
					case "OAuthToken_URL":
						DC[Level].OAuthToken_URL = EnvironmentInformation[j];break;
					case "Client_ID":
						DC[Level].Client_ID = EnvironmentInformation[j];break;
					case "Client_Secret":
						DC[Level].Client_Secret = EnvironmentInformation[j];break;
					case "Create_Project_URL":
						DC[Level].Create_Project_URL = EnvironmentInformation[j];break;
					case "Retrieve_Project_URL":
						DC[Level].Retrieve_Project_URL = EnvironmentInformation[j];break;
					case "Update_Project_URL":
						DC[Level].Update_Project_URL = EnvironmentInformation[j];break;
					case "Delete_Project_URL":
						DC[Level].Delete_Project_URL = EnvironmentInformation[j];break;
				}//end switch Headers[j]
			}
		}
		Helper_Functions.PrintOut("\n\nThread -- Time (MMDDYY'T'HHMMSS): -- Current progress", false);
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();

		for (int i = 1; i < 8; i++) {
			if (DC[i] != null) {
				CMAC_Data c = DC[i];
				
				String organizationUUID = "10000", applicationUUID, ProjectName, latype = "propreitary", laversion = "2", latimeStamp;
				switch (m.getName()) { //Based on the method that is being called the array list will be populated.
					case "CreateProject":
						for(int j = 1; j < 5; j++) {
							ProjectName = "Proj_Creation" + j + " " + Helper_Functions.CurrentDateTime();
							latimeStamp = Helper_Functions.CurrentDateTime(true);
							applicationUUID = Helper_Functions.CurrentDateTime().replace("T", "") + j;
							data.add(new Object[] {c.Create_Project_URL, c.OAuth_Token, organizationUUID, applicationUUID, ProjectName, latype, laversion, latimeStamp});
						}
						break;
					case "RetrieveProjectDetails":
						//need to parse the applicationUUID and run multiple. Will try the application ids that were recently created.
						for (String AppUUID: applicationUUIDToDelete) {
							data.add( new Object[] {c.Retrieve_Project_URL, c.OAuth_Token, AppUUID});
						}
						break;
					case "RetrieveProjectSummary":
						data.add( new Object[] {c.Retrieve_Project_URL, c.OAuth_Token, organizationUUID});
						break;
					case "DeleteProject":
						for(int j=0;j<applicationUUIDToDelete.size();j++) {
							data.add( new Object[] {c.Delete_Project_URL, c.OAuth_Token, applicationUUIDToDelete.get(j)});
						}
						break;
					case "UpdateProject":
						for(int j=0;j<applicationUUIDToDelete.size();j++) {
							ProjectName = "Proj_DEL_Updated" + Helper_Functions.CurrentDateTime();
							latimeStamp = Helper_Functions.CurrentDateTime(true);
							data.add( new Object[] {c.Update_Project_URL, c.Retrieve_Project_URL, c.OAuth_Token, organizationUUID, applicationUUIDToDelete.get(j), ProjectName, latimeStamp, latype, laversion});
						}
						break;
				}//end switch MethodName
			}	
		}
			
		return data.iterator();
	}
		
	@Test(dataProvider = "dp", priority = 1, description = "380527")
	public void CreateProject(String URL, String OAuth_Token, String organizationUUID, String applicationUUID, String projectName, String latype, String laversion, String latimeStamp) {
		String Response = "";
		
		Response = CMAC_API_Endpoints.CreateProject_API(URL, OAuth_Token, organizationUUID, applicationUUID, projectName, latype, laversion, latimeStamp);
		String[] Response_Variables = {"transactionId", "output", "status"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}

		//need to add a check here to see if there are any errors.
		applicationUUIDToDelete.add(applicationUUID);//update this to store the applicationUUID of the created project. Will be deleted later as part of the delete tests.
	}
	
	@Test(dataProvider = "dp", priority = 2, description = "380579 - Summery")
	public void RetrieveProjectSummary(String URL, String OAuth_Token, String organizationUUID) {
		String Response;
		
		Response = CMAC_API_Endpoints.RetrieveProject_API(URL, OAuth_Token, organizationUUID);
		
		//if the organization contains projects check the variables returned.
		if (!Response.contains("\"output\":{}")) {
			String[] Response_Variables = {"transactionId", "output", "projects", "applicationUUID", "orgUUID", "projectName", "latype", "laversion", "latimeStamp"};
			for(int i = 0; i < Response_Variables.length; i++) {
				assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
			}
		}
		
		//add something here to test multiple projects
	}
	
	@Test(dataProvider = "dp", priority = 2, dependsOnMethods = "CreateProject", description = "380579 - Details")   
	public void RetrieveProjectDetails(String URL, String OAuth_Token, String applicationUUID) {
		String Response = "";
		
		Response = CMAC_API_Endpoints.RetrieveProject_API(URL, OAuth_Token, applicationUUID);

		String[] Response_Variables = {"transactionId", "output", "projects", "applicationUUID", "orgUUID", "projectName", "latype", "laversion", "latimeStamp"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
	}

	@Test(dataProvider = "dp", priority = 2, description = "380670")
	public void UpdateProject(String URL, String RetrieveURL, String OAuth_Token, String organizationUUID, String applicationUUID, String projectName, String latimeStamp, String latype, String laversion) {
		String Response;
		Response = CMAC_API_Endpoints.UpdateProject_API(URL, OAuth_Token, organizationUUID, applicationUUID, latimeStamp, latype, laversion, projectName);

		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		//now check that the project has been updated
		Response = CMAC_API_Endpoints.RetrieveProject_API(RetrieveURL, OAuth_Token, applicationUUID);
		
		assertThat(Response, containsString("\"applicationUUID\":\"" + applicationUUID));
		assertThat(Response, containsString("\"projectName\":\"" + projectName));
		assertThat(Response, containsString("\"latype\":\"" + latype));
		assertThat(Response, containsString("\"laversion\":\"" + laversion));
		assertThat(Response, containsString("\"latimeStamp\":\"" + latimeStamp));
	}
	
	@Test(dataProvider = "dp", priority = 3, dependsOnMethods = "CreateProject", description = "380687")
	public void DeleteProject(String URL, String OAuth_Token, String applicationUUID) {
		String Response;
		Response = CMAC_API_Endpoints.DeleteProject_API(URL, OAuth_Token, applicationUUID);
			
		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		
		//now check that the project has been removed
		Response = CMAC_API_Endpoints.DeleteProject_API(URL, OAuth_Token, applicationUUID);
		
		Response_Variables = new String[] {"transactionId", "errors", "code", "PROJNOTEXIST", "message", "Unable to delete. Project with applicationUUID doesn't exist"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
			
	}


	///////Helper Functions///////////////Condensed into single class
	public static String[] ParseStringToArray(String s, String Token) {
		int commas = s.replaceAll("[^,]","").length();
		String Temp[] = new String[commas + 1];
		StringTokenizer st1 = new StringTokenizer(s, Token);
	        for (int i = 0; st1.hasMoreTokens(); i++) {
	        	Temp[i] = st1.nextToken().replaceAll(" ", "");
	        }
	    return Temp;
	}
	
	//@Test
	public void DeleteAllApps() {	//this is used to delete all projects under an organization.
		CMAC_Data c = DC[2];//make sure to set the level
		String RetrieveURL = c.Retrieve_Project_URL,  DeleteURL = c.Delete_Project_URL,  OAuth_Token = c.OAuth_Token,  organizationUUID = "10000"; //change the organizaiton as needed

		String Response;
		String ResponseUUIDs = CMAC_API_Endpoints.RetrieveProject_API(RetrieveURL, OAuth_Token, organizationUUID);
		
		while (ResponseUUIDs.contains("applicationUUID")) {
			String applicationUUID = ResponseUUIDs.substring(ResponseUUIDs.indexOf("applicationUUID\":\"") + "applicationUUID\":\"".length(), ResponseUUIDs.indexOf("\",\"orgUUID\""));
			ResponseUUIDs = ResponseUUIDs.replace("applicationUUID\":\"" + applicationUUID  + "\",\"orgUUID", "");
			Response = CMAC_API_Endpoints.DeleteProject_API(DeleteURL, OAuth_Token, applicationUUID);
			
			String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
			for(int i = 0; i < Response_Variables.length; i++) {
				assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
			}
		
			//now check that the project has been removed
			Response = CMAC_API_Endpoints.DeleteProject_API(DeleteURL, OAuth_Token, applicationUUID);
		
			Response_Variables = new String[] {"transactionId", "errors", "code", "PROJNOTEXIST", "message", "Unable to delete. Project with applicationUUID doesn't exist"};
			for(int i = 0; i < Response_Variables.length; i++) {
				assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
			}
		}
		
		Helper_Functions.PrintOut("\nAll projects deleted form " + organizationUUID, true);
		CMAC_API_Endpoints.RetrieveProject_API(RetrieveURL, OAuth_Token, organizationUUID);
		
			
	}
	
}