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
import API_Calls.*;
import Data_Structures.*;
import SupportClasses.Set_Environment;
import SupportClasses.ThreadLogger;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class CMAC_Resources{
	static String LevelsToTest = "2"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.
	static ArrayList<String[]> ResourceList = new ArrayList<String[]>();//this is a list of when multiple resources are added. Will be initialized in before class.
	static ArrayList<String> applicationUUIDToDelete = new ArrayList<String>();
	static String organizationUUID = "30001";
	
	@BeforeClass
	public void beforeClass() {		//implemented as a before class so the OAUTH tokens are only generated once.
		Set_Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp_resources(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    String isCertified = "";
	    
	    for (int i = 0; i < ThreadLogger.LevelsToTest.length(); i++) {
	    	String strLevel = "" + ThreadLogger.LevelsToTest.charAt(i);
	    	CMAC_Data CMAC_D = CMAC_Data.LoadVariables(strLevel);
			
	    	ArrayList<String> applicationUUIDs = CMAC_Projects.GetAll_ApplicationUUID(CMAC_D.Retrieve_Project_URL, CMAC_D.OAuth_Token, organizationUUID);
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "CreateResource":
				case "UpdateResource":
					for(int j = 0; j < applicationUUIDs.size(); j++) {
						String Resources[] = CreateResourceString(j + 1, "Cre");
						if (j % 2 == 0) {
							isCertified = "true";
						}else {
							isCertified = "false";
						}
						data.add(new Object[] {CMAC_D.Create_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j), Resources, isCertified});
					}
					break;
				case "RetrieveResource":
					for(int j = 0; j < applicationUUIDs.size(); j++) {
						data.add(new Object[] {CMAC_D.Retrieve_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j)});
					}
					break;
				case "DeleteResource":
					for(int j = 0; j < applicationUUIDs.size(); j++) {
						data.add(new Object[] {CMAC_D.Delete_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j)});
					}
					break;
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@Test(dataProvider = "dp_resources", priority = 2, description = "380557 - Create Resource")
	public void CreateResource(String URL, String OAuth_Token, String applicationUUID, String endpointUUIDs[], String isCertified) {
		String Response = "";
		
		Response = CMAC_API_Endpoints.CreateResource_API(URL, OAuth_Token, applicationUUID, endpointUUIDs, isCertified);
		
		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}

		//need to add a check here to see if there are any errors.
		//applicationUUIDToDelete.add(applicationUUID);//update this to store the applicationUUID of the created project. Will be deleted later as part of the delete tests.
	}
	
	@Test(dataProvider = "dp_resources", priority = 2, description = "380583 - Retrieve Resources")
	public void RetrieveResource(String URL, String OAuth_Token, String applicationUUID) {
		String Response;
		
		Response = CMAC_API_Endpoints.RetrieveResource_API(URL, OAuth_Token, applicationUUID);
		
		//if the organization contains projects check the variables returned.
		if (!Response.contains("\"output\":{}")) {
			String[] Response_Variables = {"transactionId", "output", "resources", "endpointUUID", "applicationUUID", "isCertified"};
			for(int i = 0; i < Response_Variables.length; i++) {
				assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
			}
		}
		
		//add something here to test multiple resources, or no resources
	}

	@Test(dataProvider = "dp_resources", priority = 3, description = "380692 - Delete Resource")   ///, dependsOnMethods = "CreateResource"
	public void DeleteResource(String URL, String OAuth_Token, String applicationUUID) {
		String Response;
		Response = CMAC_API_Endpoints.DeleteResource_API(URL, OAuth_Token, applicationUUID);
			
		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		
		//now check that the project has been removed
		Response = CMAC_API_Endpoints.DeleteResource_API(URL, OAuth_Token, applicationUUID);
		
		Response_Variables = new String[] {"transactionId", "errors", "code", "", "message", ""};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
			
	}

	//need to create UpdateResource   current ICD is not correct, must update the below 
	@Test(dataProvider = "dp_resources", priority = 2, description = "380672 - Update Resource")
	public void UpdateResource(String RetrieveURL, String OAuth_Token, String applicationUUID, String endpointUUIDs[], String isCertified) {
		String Response;
		Response = CMAC_API_Endpoints.UpdateResource_API(RetrieveURL, OAuth_Token, applicationUUID, endpointUUIDs, isCertified);

		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		//now check that the project has been updated
		Response = CMAC_API_Endpoints.RetrieveResource_API(RetrieveURL, OAuth_Token, applicationUUID);
		
		/*
		assertThat(Response, containsString("\"applicationUUID\":\"" + applicationUUID));
		assertThat(Response, containsString("\"projectName\":\"" + projectName));
		assertThat(Response, containsString("\"latype\":\"" + latype));
		assertThat(Response, containsString("\"laversion\":\"" + laversion));
		assertThat(Response, containsString("\"latimeStamp\":\"" + latimeStamp));
		*/
	}
	
	
	///////Helper Functions///////////////
		
	//NumberOfResources = number of resources in the list
	//AppendToResource = string to append to the end of the resource name. Used in differentiating value.
	public String[] CreateResourceString(int NumberOfResources, String AppendToResource) {
		String Resources[] = new String[] {"Ship", "Track", "Pickup", "Rate", "Prefrences", "Freight", "Supplies"};
		
		String ResourceList[] = new String[NumberOfResources];
		
		for(int i = 0; i < NumberOfResources ; i++) {
			ResourceList[i] = Resources[i % Resources.length] + i + AppendToResource;
		}
		return ResourceList;
	}
}