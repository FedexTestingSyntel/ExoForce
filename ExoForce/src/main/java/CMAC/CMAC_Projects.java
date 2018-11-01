package CMAC;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.CoreMatchers;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import TestingFunctions.Helper_Functions;
import API_Calls.*;
import Data_Structures.*;
import SupportClasses.Set_Environment;
import SupportClasses.ThreadLogger;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class CMAC_Projects{
	static String LevelsToTest = "2"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.
	static ArrayList<String[]> ResourceList = new ArrayList<String[]>();//this is a list of when multiple resources are added. Will be initialized in before class.
	static String organizationUUID = "20000";
	
	@BeforeClass
	public void beforeClass() {
		Set_Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    
	    for (int i = 0; i < ThreadLogger.LevelsToTest.length(); i++) {
	    	String strLevel = "" + ThreadLogger.LevelsToTest.charAt(i);
	    	CMAC_Data CMAC_D = CMAC_Data.LoadVariables(strLevel);
	    	
			ArrayList<String> applicationUUIDs = GetAll_ApplicationUUID(CMAC_D.Retrieve_Project_URL, CMAC_D.OAuth_Token, organizationUUID);
			String applicationUUID, ProjectName, laType= "propreitary", laVersion= "2", laTimeStamp;
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "CreateProject":
				for(int j = 1; j < 5; j++) {
					ProjectName = "Proj_Creation" + j + " " + Helper_Functions.CurrentDateTime();
					laTimeStamp = Helper_Functions.CurrentDateTime(true);
					applicationUUID = Helper_Functions.CurrentDateTime().replace("T", "") + j;
					data.add(new Object[] {CMAC_D.Create_Project_URL, CMAC_D.Retrieve_Project_URL, CMAC_D.OAuth_Token, organizationUUID, applicationUUID, ProjectName, laType, laVersion, laTimeStamp});
				}
				break;
			case "RetrieveProjectDetails":
				//need to parse the applicationUUID and run multiple. Will try the application ids that were recently created.
				for (String AppUUID: applicationUUIDs) {
					data.add( new Object[] {CMAC_D.Retrieve_Project_URL, CMAC_D.OAuth_Token, AppUUID});
				}
				break;
			case "RetrieveProjectSummary":
				data.add( new Object[] {CMAC_D.Retrieve_Project_URL, CMAC_D.OAuth_Token, organizationUUID});
				break;
			case "DeleteProject":
				for(int j = 0; j < applicationUUIDs.size(); j++) {
					data.add( new Object[] {CMAC_D.Delete_Project_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j)});
				}
				break;
			case "UpdateProject":
				for(int j=0;j<applicationUUIDs.size();j++) {
					ProjectName = "Proj_Updated" + Helper_Functions.CurrentDateTime();
					laTimeStamp = Helper_Functions.CurrentDateTime(true);
					applicationUUID = applicationUUIDs.get(j);
					data.add( new Object[] {CMAC_D.Update_Project_URL, CMAC_D.Retrieve_Project_URL, CMAC_D.OAuth_Token, organizationUUID, applicationUUID, ProjectName, laTimeStamp, laType, laVersion});
				}
				break;
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", priority = 1, description = "380527")
	public void CreateProject(String Create_URL, String Retrieve_URL, String OAuth_Token, String organizationUUID, String applicationUUID, String projectName, String laType, String laVersion, String laTimeStamp) {
		String Response = "";
		
		Response = CMAC_API_Endpoints.CreateProject_API(Create_URL, OAuth_Token, organizationUUID, applicationUUID, projectName, laType, laVersion, laTimeStamp);
		String[] Response_Variables = {"transactionId", "output", "status"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}

		//now check that the project has been create with same data that was sent
		Response = CMAC_API_Endpoints.RetrieveProject_API(Retrieve_URL, OAuth_Token, applicationUUID);
			
		assertThat(Response, containsString("\"applicationUUID\":\"" + applicationUUID));
		assertThat(Response, containsString("\"projectName\":\"" + projectName));
		assertThat(Response, containsString("\"laType\":\"" + laType));
		assertThat(Response, containsString("\"laVersion\":\"" + laVersion));
		assertThat(Response, containsString("\"laTimeStamp\":\"" + laTimeStamp));
	}
	
	@Test(dataProvider = "dp", priority = 2, description = "380579 - Summery")
	public void RetrieveProjectSummary(String URL, String OAuth_Token, String organizationUUID) {
		String Response;
		
		Response = CMAC_API_Endpoints.RetrieveProject_API(URL, OAuth_Token, organizationUUID);
		
		//if the organization contains projects check the variables returned.
		if (!Response.contains("\"output\":{}")) {
			String[] Response_Variables = {"transactionId", "output", "projects", "applicationUUID", "organizationUUID", "projectName", "laType", "laVersion", "laTimeStamp"};
			for(int i = 0; i < Response_Variables.length; i++) {
				assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
			}
		}
		
		//add something here to test multiple projects
	}
	
	@Test(dataProvider = "dp", priority = 2, description = "380579 - Details")   
	public void RetrieveProjectDetails(String URL, String OAuth_Token, String applicationUUID) {
		String Response = "";
		Response = CMAC_API_Endpoints.RetrieveProject_API(URL, OAuth_Token, applicationUUID);

		String[] Response_Variables = {"transactionId", "output", "projects", "applicationUUID", "organizationUUID", "projectName", "laType", "laVersion", "laTimeStamp"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
	}

	@Test(dataProvider = "dp", priority = 2, description = "380670")
	public void UpdateProject(String URL, String RetrieveURL, String OAuth_Token, String organizationUUID, String applicationUUID, String projectName, String laTimeStamp, String laType, String laVersion) {
		String Response;
		Response = CMAC_API_Endpoints.UpdateProject_API(URL, OAuth_Token, organizationUUID, applicationUUID, laTimeStamp, laType, laVersion, projectName);

		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		//now check that the project has been updated
		Response = CMAC_API_Endpoints.RetrieveProject_API(RetrieveURL, OAuth_Token, applicationUUID);
		
		assertThat(Response, containsString("\"applicationUUID\":\"" + applicationUUID));
		assertThat(Response, containsString("\"projectName\":\"" + projectName));
		assertThat(Response, containsString("\"laType\":\"" + laType));
		assertThat(Response, containsString("\"laVersion\":\"" + laVersion));
		assertThat(Response, containsString("\"laTimeStamp\":\"" + laTimeStamp));
	}
	
	//the delete call depends on there being prjects to delete, due to priority will run after the create projects tests.
	@Test(dataProvider = "dp", priority = 3,  description = "380687")
	public void DeleteProject(String URL, String OAuth_Token, String applicationUUID) {
		String Response;
		Response = CMAC_API_Endpoints.DeleteProject_API(URL, OAuth_Token, applicationUUID);
			
		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		
		//now check that the project has been removed
		Response = CMAC_API_Endpoints.DeleteProject_API(URL, OAuth_Token, applicationUUID);
		
		Response_Variables = new String[] {"transactionId", "errors", "code", "PROJECT_NOT_EXIST", "message", "Unable to delete. Project with applicationUUID doesn't exist."};
		//Note: "PROJECT_NOT_EXIST", "message", "Unable to delete. Project with applicationUUID doesn't exist." are not confirmed by business team, may change.
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
			
	}

	///////Helper Functions///////////////
	
	//WARNING, will return empty if there are no application uuids part of the organization.
	public static ArrayList<String> GetAll_ApplicationUUID(String RetrieveURL, String OAuth_Token, String organizationUUID) {
		ArrayList<String> applicationUUIDs = new ArrayList<String>();
		
		String ResponseUUIDs = CMAC_API_Endpoints.RetrieveProject_API(RetrieveURL, OAuth_Token, organizationUUID);
		while (ResponseUUIDs.contains("applicationUUID")) {
			String start_string = "applicationUUID\":\"";
			int start_pos = ResponseUUIDs.indexOf(start_string) + start_string.length();
			for (int i = start_pos ; i < ResponseUUIDs.length(); i++) {
				String test = ResponseUUIDs.substring(i, i + 3);
				if(test.contentEquals("\",\"")){
					String applicationUUID = ResponseUUIDs.substring(start_pos, i);
					ResponseUUIDs = ResponseUUIDs.replace("applicationUUID\":\"" + applicationUUID, "");
					applicationUUIDs.add(applicationUUID);
					break;
				}
			}
		}
		return applicationUUIDs;
	}
}