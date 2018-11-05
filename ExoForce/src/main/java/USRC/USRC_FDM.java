package USRC;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import Data_Structures.USRC_Data;
import SupportClasses.Set_Environment;
import SupportClasses.ThreadLogger;
import TestNG.MFAC;
import TestingFunctions.Helper_Functions;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import API_Calls.*;
import Data_Structures.*;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class USRC_FDM {

	static String LevelsToTest = "2"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

	@BeforeClass
	public void beforeClass() {
		Set_Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true)
	public Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
	    
	    for (int i = 0; i < ThreadLogger.LevelsToTest.length(); i++) {
	    	String strLevel = "" + ThreadLogger.LevelsToTest.charAt(i);
	    	USRC_Data USRC_D = USRC_Data.LoadVariables(strLevel);
	    	MFAC_Data MFAC_D = MFAC_Data.LoadVariables(strLevel);
	    	
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "EndtoEndEnrollment":
				//data.add(new Object[] {USRC_D.Level, USRC_D.REGCCreateNewUserURL, USRC_D.LoginUserURL, USRC_D.EnrollmentURL, USRC_D.OAuth_Token, USRC_Data.ContactDetailsList.get(0), MFAC_D.OrgPhone});
				data.add(new Object[] {USRC_D, USRC_D.FDMPostcard_PinType, MFAC_D, MFAC_D.OrgPostcard});
				break;
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@Test (dataProvider = "dp", priority = 1, description = "380527")
	public void EndtoEndEnrollment(USRC_Data USRC_Details, String USRC_Org, MFAC_Data MFAC_Details, String MFAC_Org) {
		String Cookie = null, UUID = null, fdx_login_fcl_uuid[] = {"",""};
		try {
			//1 - Login, get cookies and uuid
			String UserID = "L" + USRC_Details.Level + "FDM" + Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(2);
			String Password = "Test1234";
			
			//create the new user
			String ContactDetails[] = USRC_Data.ContactDetailsList.get(0);
			String Response = USRC_API_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
			//check to make sure that the userid was created.
			assertThat(Response, containsString("successful\":true"));
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.LoginUserURL, UserID, Password);
			Cookie = fdx_login_fcl_uuid[0];
			UUID = fdx_login_fcl_uuid[1];
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
			Helper_Functions.PrintOut("Enrollment call", false);
			Response = USRC_API_Endpoints.Enrollment(USRC_Details.EnrollmentURL, USRC_Details.OAuth_Token, ContactDetails, Cookie);

			assertThat(Response, containsString("enrollmentOptionsList"));
			
			//3 - request a pin
			Helper_Functions.PrintOut("Request pin through USRC", false);
			String ShareID = ContactDetails[11];
			Response = USRC_API_Endpoints.CreatePin(USRC_Details.CreatePinURL, USRC_Details.OAuth_Token, Cookie, ShareID, USRC_Org);
			assertThat(Response, containsString("successful\":true"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			Helper_Functions.PrintOut("Requesting pin through MFAC", false);
			String UserName = UUID + "-" + ShareID;
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, MFAC_Org, MFAC_Details.AIssueURL, MFAC_Details.OAuth_Token);
			//get the pin from the MFAC call
			String Pin = MFAC.ParsePIN(Response);
			
			//5 - enroll the above pin through USRC
			Helper_Functions.PrintOut("Verify pin through USRC", true);
			Response = USRC_API_Endpoints.VerifyPin(USRC_Details.VerifyPinURL, USRC_Details.OAuth_Token, Cookie, ShareID, Pin, USRC_Org);
			assertThat(Response, containsString("responseMessage\":\"Success"));

			//6 - Verify the above enrollment completed succsessfully.
			Helper_Functions.PrintOut("Check recipient profile for new FDM user through USRC", false);
			Response = USRC_API_Endpoints.RecipientProfile(USRC_Details.LoginUserURL, Cookie);
			
			Helper_Functions.PrintOut(UserID + "/" + Password + "--" + fdx_login_fcl_uuid[1] + "--" + USRC_Org, false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	@Test
	public void EndtoEndEnrollmentAddAddresses() {
		int contact = 1;
		String Contact[] = null, response = null, Org = "POSTAL", Cookie = null, tempUser = null, fdx_login_fcl_uuid[] = {"",""};
		//Org = "SMS";//will overwrite
		try {
			//1 - Login, get cookies and uuid
			
			for(int i = 0; i < 3; i++) { //incase the address is maxed out will try the next addresses in the contact list.
				
				try {
					Contact = USRC.ContactList.get(contact);
					String[] UserContact = USRC.ContactList.get(1);
					Contact[0] = UserContact[0];
					Contact[1] = UserContact[1];
					Contact[2] = UserContact[2];
					Contact[3] = UserContact[3];
					Contact[4] = UserContact[4];
					//tempUser = USRC.CreateNewUser(Contact, Level, USRC.Password);
					tempUser = "L3FDM100218T150103";    //in case need to register a specific user
					fdx_login_fcl_uuid = USRCLogin(tempUser, USRC.Password, Level);
					Cookie = fdx_login_fcl_uuid[0];
					USRC.Cookie = Cookie;
					USRC.UUID = fdx_login_fcl_uuid[1];
					PrintOut("UUID is " + fdx_login_fcl_uuid[1]);
					i = 4;
				}catch (Exception e) {
					PrintOut("Error whith given user, creating new user.");
					contact = contact++ % USRC.ContactList.size();
				};
				
				
			//2 - do the enrollment call. Note that the enrollment call will store the ShareID
				PrintOut("Enrollment call");
				response = USRC.Enrollment(Contact); ///"enrollmentOptionsList":["POSTAL","EXAM","SMS"]
				Assert.assertTrue(response.contains(Org));	
			}

			//3 - request a pin
			PrintOut("Request pin through USRC");
			String UserName = USRC.UUID + "-" + Contact[11];
			response = USRC.CreatePin(Contact[11], Org);
			Assert.assertTrue(response.contains("true") && response.contains("successful"));
		
			//4 - request a pin through MFAC as cannot see the pin generated from the above
			PrintOut("Requesting pin through MFAC");
			if (Org.contentEquals("SMS")) {
				response = IssuePinExternal(UserName, "FDM-PHONE-PIN", Level);
			}else if (Org.contentEquals("POSTAL")) {
				response = IssuePinExternal(UserName, "FDM-POSTCARD-PIN", Level);
			}
		
			//5 - enroll the above pin through USRC
			PrintOut("Verify pin through USRC");
			response = USRC.VerifyPin(Contact[11], response, Org);
			Assert.assertTrue(response.contains("responseMessage") && response.contains("Success"));
			PrintOut("Check recipient profile for new FDM user through USRC");
			response = USRC.RecipientProfile(Cookie, Integer.parseInt(Level));
			
			PrintOut(tempUser + "/" + USRC.Password + "--" + fdx_login_fcl_uuid[1] + "--" + Org);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
}
