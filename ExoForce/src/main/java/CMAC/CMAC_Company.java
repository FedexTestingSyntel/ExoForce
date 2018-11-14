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

public class CMAC_Company{
	static String LevelsToTest = "2"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.
	static ArrayList<String[]> CompanyList = new ArrayList<String[]>();
	//this is a list of when multiple resources are added. Will be initialized in before class.
	
	
	@BeforeClass
	public void beforeClass() {
		Set_Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp_company(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    String isCompatibleProvider = "";
	    
	    for (int i = 0; i < ThreadLogger.LevelsToTest.length(); i++) {
	    	String strLevel = "" + ThreadLogger.LevelsToTest.charAt(i);
	    	CMAC_Data CMAC_D = CMAC_Data.LoadVariables(strLevel);
	    	
	    	
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "CreateCompany":
				for(int j = 1; j < 3; j++) {
				String	company_cd = "CD_"+j +"_" + Helper_Functions.CurrentDateTime();
				String	organizationUUID = "ORG_"+j + "_"+Helper_Functions.CurrentDateTime();;
					isCompatibleProvider = GetCompatibleProvider(j);
						data.add(new Object[] {CMAC_D.Create_Company_URL, CMAC_D.OAuth_Token, company_cd, organizationUUID, isCompatibleProvider});
					}
					break;
			case "RetrieveCompanybyCompanycd":
			case "RetrieveCompanybyOrganizationUUID":
				for(String[] company: CompanyList) {
					data.add( new Object[] {CMAC_D.Retrieve_Company_URL, CMAC_D.OAuth_Token, company});
				}
				break;
			case "DeleteCompanybyCompanycd":
				for(int j = 0; j < CompanyList.size(); j++) {
					if (j % 2 == 0) {
						data.add( new Object[] {CMAC_D.Retrieve_Company_URL, CMAC_D.OAuth_Token, CompanyList.get(j)});
					}
				}
				break;
			case "DeleteCompanybyOrganizationUUID":
				for(int j = 0; j < CompanyList.size(); j++) {
					if (j% 2 == 1) {
						data.add( new Object[] {CMAC_D.Retrieve_Company_URL, CMAC_D.OAuth_Token, CompanyList.get(j)});
					}
				}
				break;
			case "UpdateCompany"://Pruthvi: not yet finished.
				for(int j = 0; j < CompanyList.size(); j++) {
					if (j % 2 == 0) {
						
					}
				
					data.add( new Object[] {CMAC_D.Update_Company_URL, CMAC_D.Retrieve_Project_URL, CMAC_D.OAuth_Token, CompanyList.get(j)});
				}
				break;
			}//end switch MethodName
		}
		return data.iterator();
	}
	public String GetCompatibleProvider(int i) {
		if (i % 2 == 0) {
			return "true";
		}else {
			return "false";
	}
}
	
	@Test(dataProvider = "dp_company", priority = 1, description = "380565")
	public void CreateCompany(String Create_URL, String OAuth_Token, String company_cd, String organizationUUID, String isCompatibleProvider) {
		String Response = "";
		
		Response = CMAC_API_Endpoints.CreateCompany_API(Create_URL, OAuth_Token, company_cd, organizationUUID, isCompatibleProvider);
		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		CompanyList.add(new String[] {company_cd, organizationUUID, isCompatibleProvider});
		
	}
	
	@Test(dataProvider = "dp_company", priority = 2, dependsOnMethods = "CreateCompany", description = "380581 - byCompanycd")
	public void RetrieveCompanybyCompanycd(String URL, String OAuth_Token, String[] company) {
		String Response;
		
		Response = CMAC_API_Endpoints.RetrieveCompany_API(URL, OAuth_Token, company[0]);
		
			if (!Response.contains("\"output\":{}")) {
			String[] Response_Variables = {"transactionId", "output", "isCompatibleProvider", company[2]};
			for(int i = 0; i < Response_Variables.length; i++) {
				assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
			}
		}
	}
	
	@Test(dataProvider = "dp_company", priority = 2, dependsOnMethods = "CreateCompany", description = "380581 - byOrganizationUUID")   
	public void RetrieveCompanybyOrganizationUUID(String URL, String OAuth_Token, String[] company) {
		String Response = "";
		Response = CMAC_API_Endpoints.RetrieveCompany_API(URL, OAuth_Token, company[1]);

		String[] Response_Variables = {"transactionId", "output", "isCompatibleProvider", company[2]};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
	}
	
	@Test(dataProvider = "dp_company", priority = 2, dependsOnMethods = "CreateCompany", description = "380675", enabled  = false)
	public void UpdateCompany(String URL, String RetrieveURL, String OAuth_Token, String[] company) {
		String Response;
		Response = CMAC_API_Endpoints.UpdateCompany_API(URL, OAuth_Token, company);

		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		//now check that the project has been updated
		Response = CMAC_API_Endpoints.RetrieveCompany_API(RetrieveURL, OAuth_Token, company[0]);
		
		assertThat(Response, containsString("\"company_cd\":\"" + company[0]));
		assertThat(Response, containsString("\"organizationUUID\":\"" + company[1]));
		assertThat(Response, containsString("\"isCompatibleProvider\":\"" + company[2]));
	}

	//the delete call depends on there being prjects to delete, due to priority will run after the create projects tests.
	@Test(dataProvider = "dp_company", priority = 3,  dependsOnMethods = "CreateCompany", description = "380695 - byCompanycd")
	public void DeleteCompanybyCompanycd(String URL, String OAuth_Token, String[] company) {
		String Response;
		Response = CMAC_API_Endpoints.DeleteCompany_API(URL, OAuth_Token, company[0]);
			
		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		
		//now check that the company has been removed
		Response = CMAC_API_Endpoints.DeleteCompany_API(URL, OAuth_Token, company[0]);
		
		Response_Variables = new String[] {"transactionId", "errors", "code", "COMPANY_DOESNT_EXIST", "message", "The company you are trying to modify info for doesn't exist."};
		//Note: "COMPANY_NOT_EXIST", "message", "Unable to delete. Company doesn't exist." are not confirmed by business team, may change.
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
			
	}
	
	@Test(dataProvider = "dp_company", priority = 3,  dependsOnMethods = "CreateCompany", description = "380695 - bybyOrganizationUUID")
	public void DeleteCompanybyOrganizationUUID(String URL, String OAuth_Token, String[] company) {
		String Response;
		Response = CMAC_API_Endpoints.DeleteCompany_API(URL, OAuth_Token, company[1]);
			
		String[] Response_Variables = {"transactionId", "output", "status", "SUCCESS"};
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
		
		//now check that the company has been removed
		Response = CMAC_API_Endpoints.DeleteCompany_API(URL, OAuth_Token, company[1]);
		
		Response_Variables = new String[] {"transactionId", "errors", "code", "COMPANY_DOESNT_EXIST", "message", "The company you are trying to modify info for doesn't exist."};
		//Note: "COMPANY_NOT_EXIST", "message", "Unable to delete. Company doesn't exist." are not confirmed by business team, may change.
		for(int i = 0; i < Response_Variables.length; i++) {
			assertThat(Response, CoreMatchers.containsString(Response_Variables[i]));
		}
			
	}
	
}

