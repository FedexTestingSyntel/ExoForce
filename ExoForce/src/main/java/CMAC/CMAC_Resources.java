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
	static String organizationUUID = "6000";
	
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
			//if there are no projects for the organization create some.
	    	if (applicationUUIDs.size() == 0) {
	    		CMAC_Projects.CreateProjectsExternal(strLevel, 5, organizationUUID);
			}
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
				case "CreateResource":
					for(int j = 0; j < applicationUUIDs.size(); j++) {
						String Resources[] = CreateResourceString(j + 1, "Cre");
						isCertified = GetCertified(j);
						data.add(new Object[] {CMAC_D.Create_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j), Resources, isCertified});
					}
					break;
				case "UpdateResource":
					for(int j = applicationUUIDs.size() - 1; j >= 0 ; j--) {//note, reversed the order of the call to be oposite from the create call.
						String Resources[] = CreateResourceString(j + 1, "Update");
						isCertified = GetCertified(j);
						data.add(new Object[] {CMAC_D.Update_Resource_URL, CMAC_D.Retrieve_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j), Resources, isCertified});
					}
					break;
				case "RetrieveResource":
					for(int j = 0; j < applicationUUIDs.size(); j++) {
						data.add(new Object[] {CMAC_D.Retrieve_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j)});
					}
					break;
				case "DeleteResource":
					for(int j = 0; j < applicationUUIDs.size(); j++) {
						//check if the given project has resources already loaded.
						if (CMAC_API_Endpoints.RetrieveResource_API(CMAC_D.Retrieve_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j)).contains("endpointUUID")) {
							data.add(new Object[] {CMAC_D.Delete_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j)});
						}else {
							String Resources[] = CreateResourceString(j + 1, "ToDel");
							isCertified = GetCertified(j);
							CreateResource(CMAC_D.Create_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j), Resources, isCertified);
							data.add(new Object[] {CMAC_D.Delete_Resource_URL, CMAC_D.OAuth_Token, applicationUUIDs.get(j)});
						}
					}
					break;
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@Test(dataProvider = "dp_resources", priority = 1, description = "380557 - Create Resource")
	public void CreateResource(String URL, String OAuth_Token, String applicationUUID, String endpointUUIDs[], String isCertified) {
		String Response = "";
		
		Response = CMAC_API_Endpoints.CreateResource_API(URL, OAuth_Token, applicationUUID, endpointUUIDs, isCertified);
		
		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}

		//need to add a check here to see if there are any errors.
		
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
		
		//verify the resources are deleted		Please note that the below error message is not confirmed may be updated later.
		Response_Variables = new String[] {"transactionId", "errors", "code", "RESOURCES_DONT_EXIST", "message", "\"Unable to delete. Resources with applicationUUID don't exist."};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
			
	}

	@Test(dataProvider = "dp_resources", priority = 2, description = "380672 - Update Resource")
	public void UpdateResource(String UpdateURL, String RetrieveURL, String OAuth_Token, String applicationUUID, String endpointUUIDs[], String isCertified) {
		String Response;
		Response = CMAC_API_Endpoints.UpdateResource_API(UpdateURL, OAuth_Token, applicationUUID, endpointUUIDs, isCertified);

		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		//now check that the project has been updated
		Response = CMAC_API_Endpoints.RetrieveResource_API(RetrieveURL, OAuth_Token, applicationUUID);
		
		//check the generic values of the response to see if updated
		Response_Variables = new String[] {"transactionId", "output", "resources", "endpointUUID", "applicationUUID", "isCertified", isCertified};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		
		//check that the specific resource names have been updated
		for(int i = 0; i < endpointUUIDs.length; i++) {
			assertThat(Response, CoreMatchers.containsString(endpointUUIDs[i]));
		}
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
	
	public String GetCertified(int i) {
		if (i % 2 == 0) {
			return "true";
		}else {
			return "false";
		}
	}
}