package MFAC;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hamcrest.CoreMatchers;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import API_Calls.*;
import Data_Structures.*;
import TestingFunctions.Helper_Functions;
import SupportClasses.ThreadLogger;
import SupportClasses.Set_Environment;

@Listeners(SupportClasses.TestNG_TestListener.class)
//@Listeners(SupportClasses.TestNG_ReportListener.class)

public class MFAC{
	static String LevelsToTest = "23"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.
	
	@BeforeClass
	public static void beforeClass() {
		Set_Environment.SetLevelsToTest(LevelsToTest);
	}
	
	@DataProvider (parallel = true) //make sure to add <suite name="..." data-provider-thread-count="12"> to the .xml for speed.
	public static Iterator<Object[]> dp(Method m) {
	    List<Object[]> data = new ArrayList<>();
		
	    for (int i = 0; i < ThreadLogger.LevelsToTest.length(); i++) {
	    	String strLevel = "" + ThreadLogger.LevelsToTest.charAt(i);
			MFAC_Data MFAC_D = MFAC_Data.LoadVariables(strLevel);
			
			switch (m.getName()) { //Based on the method that is being called the array list will be populated. This will make the TestNG Pass/Fail results more relevant.
				case "AddressVelocity":
					if (!MFAC_D.Level.contentEquals("1")){
						data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.AVelocityURL, MFAC_D.AddressVelocityThreshold});
						data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.AVelocityURL, MFAC_D.AddressVelocityThreshold});
					}
	    			if (!MFAC_D.Level.contentEquals("6") && !MFAC_D.Level.contentEquals("7")){
	    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.DVelocityURL, MFAC_D.AddressVelocityThreshold});
	    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.DVelocityURL, MFAC_D.AddressVelocityThreshold});
	    			}
	    			break;
	    		case "IssuePin":
	    			if (!MFAC_D.Level.contentEquals("1")){
	    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.AIssueURL});
	    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.AIssueURL});
	    			}
	    			if (!MFAC_D.Level.contentEquals("6") && !MFAC_D.Level.contentEquals("7")){
	    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.DIssueURL});
	    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.DIssueURL});
	    			}
	    			break;
	    		case "DetermineLockoutTime"://only need to test API call as this is a helper test to determine current lockouts set.
	    			data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.AIssueURL});
	    			data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.AIssueURL});
	    			break;
	    		case "IssuePinVelocity":
	    			if (!MFAC_D.Level.contentEquals("1")){
	    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.AIssueURL, MFAC_D.PinVelocityThresholdPostcard});
	    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.AIssueURL, MFAC_D.PinVelocityThresholdPhone});
	    			}
	    			if (!MFAC_D.Level.contentEquals("6") && !MFAC_D.Level.contentEquals("7")){
	    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.DIssueURL, MFAC_D.PinVelocityThresholdPostcard});
	    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.DIssueURL, MFAC_D.PinVelocityThresholdPhone});
	    			}
	    			break;
	    		case "VerifyPinValid":
	    		case "VerifyPinNoLongerValid":
	    		case "IssuePinExpiration":
	    			if (!MFAC_D.Level.contentEquals("1")){
	    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.AIssueURL, MFAC_D.AVerifyURL});
	    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.AIssueURL, MFAC_D.AVerifyURL});	
	    			}
	    			if (!MFAC_D.Level.contentEquals("6") && !MFAC_D.Level.contentEquals("7")){
	    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.DIssueURL, MFAC_D.DVerifyURL});
	    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.DIssueURL, MFAC_D.DVerifyURL});
	    			}
	    			break;
	    		case "VerifyPinVelocity":
	    			for (int j = 1 ; j < MFAC_D.PinVelocityThresholdPhone + 1; j++) {
		    			if (!MFAC_D.Level.contentEquals("1")){
		    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.AIssueURL, MFAC_D.AVerifyURL, MFAC_D.PinVelocityThresholdPhone, j});
		    			}
		    			if (!MFAC_D.Level.contentEquals("6") && !MFAC_D.Level.contentEquals("7")){
		    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.DIssueURL, MFAC_D.DVerifyURL, MFAC_D.PinVelocityThresholdPhone, j});
		    			}
	    			}
	    			for (int j = 1 ; j < MFAC_D.PinVelocityThresholdPostcard + 1; j++) {
		    			if (!MFAC_D.Level.contentEquals("1")){
		    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.AIssueURL, MFAC_D.AVerifyURL, MFAC_D.PinVelocityThresholdPostcard, j});
		    			}
		    			if (!MFAC_D.Level.contentEquals("6") && !MFAC_D.Level.contentEquals("7")){
		    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.DIssueURL, MFAC_D.DVerifyURL, MFAC_D.PinVelocityThresholdPostcard, j});
		    			}
	    			}
	    			break;
	    		case "AdditionalEnrollmentExpiration":
	    			if (!MFAC_D.Level.contentEquals("1")){
	    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.AIssueURL, MFAC_D.AVerifyURL});
	    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.AIssueURL, MFAC_D.AVerifyURL});
	    			}
	    			if (!MFAC_D.Level.contentEquals("6") && !MFAC_D.Level.contentEquals("7")){
	    				data.add(new Object[] {MFAC_D.OrgPostcard, MFAC_D.OrgPhone, MFAC_D.OAuth_Token, MFAC_D.DIssueURL, MFAC_D.DVerifyURL});
	    				data.add(new Object[] {MFAC_D.OrgPhone, MFAC_D.OrgPostcard, MFAC_D.OAuth_Token, MFAC_D.DIssueURL, MFAC_D.DVerifyURL});
	    			}
	    			break;
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@Test(dataProvider = "dp", priority = 2)
	public void AddressVelocity(String OrgName, String OAuth_Token, String VelocityURL, int AddressVelocityThreshold) {//220496 Address Velocity
		Helper_Functions.PrintOut("Verify that the address velocity of " + AddressVelocityThreshold + " is reached and the correct error code is received. This is to replicate too many requests for pin at a given address.", false);
		String UserName = MFAC_Helper_Functions.UserName(), Response;
		try {
			for (int i = 0; i < AddressVelocityThreshold; i++){
				Helper_Functions.PrintOut("  #" + (i + 1) + " Address Request.", false);
				Response = MFAC_API_Endpoints.AddressVelocityAPI(UserName, OrgName, VelocityURL, OAuth_Token);
				assertThat(Response, containsString("ALLOW"));
			}
			
			Helper_Functions.PrintOut("  #" + (AddressVelocityThreshold + 1) + " Address Request.", false);
			Response = MFAC_API_Endpoints.AddressVelocityAPI(UserName, OrgName, VelocityURL, OAuth_Token);
			assertThat(Response, CoreMatchers.allOf(containsString("DENY"), containsString("Unfortunately, too many failed attempts for registration have occurred. Please try again later.")));
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 2)
	public void IssuePin(String OrgName, String OAuth_Token, String IssueURL){//220459 IssuePin
		Helper_Functions.PrintOut("Verify that the user is able to request a pin.", false);
		String Response = null, UserName = MFAC_Helper_Functions.UserName();
		
		try {
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, OrgName, IssueURL, OAuth_Token);

			assertThat(Response, CoreMatchers.allOf(containsString("pinOTP"), containsString("pinExpirationDate")));//pin should be generated//pin expiration time should be present.
			
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			Integer.parseInt(Pin);//checking to see if an integer was returned.
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 2)
	public void IssuePinVelocity(String OrgName, String OAuth_Token, String IssueURL, int PinVelocityThreshold){//220459 IssuePin
		Helper_Functions.PrintOut("Verify that the user can request up to " + PinVelocityThreshold + " pin numbers before unable to request more.", false);
		String Response = null, UserName = MFAC_Helper_Functions.UserName();
		
		try {
			for (int i = 0; i < PinVelocityThreshold; i++){
				Helper_Functions.PrintOut("  #" + (i + 1) + " Pin Request.", false);
				Response = MFAC_API_Endpoints.IssuePinAPI(UserName, OrgName, IssueURL, OAuth_Token);
				assertThat(Response, CoreMatchers.allOf(containsString("pinOTP"), containsString("pinExpirationDate")));
			}
			
			Helper_Functions.PrintOut("  #" + (PinVelocityThreshold + 1) + " Pin Request.", false);
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, OrgName, IssueURL, OAuth_Token);
			//033018 - updated value from DENY to 5700, updated to match with what USRC uses.
			assertThat(Response, CoreMatchers.allOf(containsString("5700"), containsString("Unfortunately, you have exceeded your attempts for verification. Please try again later.")));
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp", priority = 2)
	public void VerifyPinValid(String OrgName, String OAuth_Token, String IssueURL, String VerifyURL){//220462 Verify Pin
		Helper_Functions.PrintOut("VerifyPinValid: Verify that user is able to request a pin and then verify that can recieve success when using the generated pin.", false);
		String Response = null, UserName = MFAC_Helper_Functions.UserName();
		
		try {
			Response =  MFAC_API_Endpoints.IssuePinAPI(UserName, OrgName, IssueURL, OAuth_Token);
			assertThat(Response, containsString("pinExpirationDate"));
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			
			//Test verify pin on valid request
			Response = MFAC_API_Endpoints.VerifyPinAPI(UserName, OrgName, Pin, VerifyURL, OAuth_Token);
			assertThat(Response, containsString("Success"));
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 2)
	public void VerifyPinVelocity(String OrgName, String OAuth_Token, String IssueURL, String VerifyURL, int PinVelocityThreshold, int Attempts){//220462 Verify Pin
		Helper_Functions.PrintOut("VerifyPinThreshold: Validate the Verify pin call when PinVelocity is " + PinVelocityThreshold + " and user makes " + Attempts + " Attempts, then enters valid pin.", false);
		String Response = null, UserName = MFAC_Helper_Functions.UserName();
		
		try {
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, OrgName, IssueURL, OAuth_Token);
			assertThat(Response, containsString("pinExpirationDate"));
			String ValidPin = MFAC_Helper_Functions.ParsePIN(Response);
			//Test verify pin on valid request
			for (int i = 0; i < Attempts; i++) {
				Response = MFAC_API_Endpoints.VerifyPinAPI(UserName, OrgName, "1111", VerifyURL, OAuth_Token);
				assertThat(Response, containsString("PIN.FAILURE"));
			}
			
			Response = MFAC_API_Endpoints.VerifyPinAPI(UserName, OrgName, ValidPin, VerifyURL, OAuth_Token);
			if (Attempts < PinVelocityThreshold) {
				assertThat(Response, containsString("Success"));
			}else {
				assertThat(Response, containsString("PIN.FAILURE"));
			}
			
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test(dataProvider = "dp", priority = 2)
	public void VerifyPinNoLongerValid(String OrgName, String OAuth_Token, String IssueURL, String VerifyURL){//220462 Verify Pin
		Helper_Functions.PrintOut("VerifyPinNoLongerValid: Verify that when user requests a second pin that the first is no longer valid.", false);
		String Response = null, UserName = MFAC_Helper_Functions.UserName();
		
		try {
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, OrgName, IssueURL, OAuth_Token);
			assertThat(Response, containsString("pinOTP"));//just to make sure valid response
			String Pin = MFAC_Helper_Functions.ParsePIN(Response);
			Integer.parseInt(Pin);
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, OrgName, IssueURL, OAuth_Token);
			assertThat(Response, containsString("pinOTP"));//just to make sure valid response
			String PinTwo = MFAC_Helper_Functions.ParsePIN(Response);
			Integer.parseInt(PinTwo);
			//Test that the first pin is no longer valid
			Response = MFAC_API_Endpoints.VerifyPinAPI(UserName, OrgName, Pin, VerifyURL, OAuth_Token);
			assertThat(Response, containsString("PIN.FAILURE"));
			//Test verify pin on valid request
			Response = MFAC_API_Endpoints.VerifyPinAPI(UserName, OrgName, PinTwo, VerifyURL, OAuth_Token);
			assertThat(Response, containsString("Success"));
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

}