package MFAC;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import API_Calls.*;
import Data_Structures.*;
import TestingFunctions.Helper_Functions;
import SupportClasses.ThreadLogger;
import SupportClasses.Set_Environment;

@Listeners(SupportClasses.TestNG_TestListener.class)
//@Listeners(SupportClasses.TestNG_ReportListener.class)

public class MFAC_Expiration{
	static String LevelsToTest = "2"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.
	static ArrayList<String[]> ExpirationData = new ArrayList<String[]>();
	
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
	    		case "IssuePinExpirationValidate":
	    			if (ExpirationData.size() > 0) {
	    				ExpirationData.sort((o1, o2) -> o1[1].compareTo(o2[1]));
	    			}
	    			
	    			for (int j = 0 ; j < ExpirationData.size(); j++) {
	    				if (ExpirationData.get(j)[0].contains("IssuePinExpiration")) {
	    					data.add(ExpirationData.get(j));
	    					ExpirationData.remove(j);
	    					j--;
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
	    		case "AdditionalEnrollmentExpirationValidate":
	    			if (ExpirationData.size() > 0) {
	    				ExpirationData.sort((o1, o2) -> o1[1].compareTo(o2[1]));
	    			}
	    			for (int j = 0 ; j < ExpirationData.size(); j++) {
	    				if (ExpirationData.get(j)[0].contains("AdditionalEnrollmentExpiration")) {
	    					data.add(ExpirationData.get(j));
	    					ExpirationData.remove(j);
	    					j--;
	    				}
	    			}
	    			break;	
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@SuppressWarnings("deprecation")  //add due to the date comparison 
	@Test(dataProvider = "dp", priority = 1)
	public void IssuePinExpiration(String OrgName, String OAuth_Token, String IssueURL, String VerifyURL){
		Helper_Functions.PrintOut("Verify that after a pin is expired it can no longer be used to complete registration.", false);
		String Response = null, UserName = MFAC_Helper_Functions.UserName();
		
		try {
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, OrgName, IssueURL, OAuth_Token);
			assertThat(Response, containsString("pinExpirationDate")); 
			
			Date CurrrentTime = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a zzz");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			dateFormat.format(CurrrentTime);
			Date ExpirationTime = MFAC_Helper_Functions.GetExpiration(Response);
		
			if (ExpirationTime.getDate() == CurrrentTime.getDate() && ExpirationTime.getMonth() == CurrrentTime.getMonth()) {
				String Expiration[] = new String[] {"IssuePinExpiration:  " + Response, dateFormat.format(ExpirationTime).toString(), UserName, OrgName, MFAC_Helper_Functions.ParsePIN(Response), VerifyURL, OAuth_Token, "PIN.FAILURE"};
				ExpirationData.add(Expiration);
				Helper_Functions.PrintOut("Will be validated after expiration in later test. --IssuePinExpirationValidate--", false);
			}else {
				String LongExpirationMessage = "Not Validating the Expiration at this time, need to verify seperatly once the expiration has passed. Here is the CST time it will expire. " + ExpirationTime;
				Helper_Functions.PrintOut(LongExpirationMessage, true);
			}
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp",dependsOnMethods = "IssuePinExpiration", priority = 3)
	public void IssuePinExpirationValidate(String Result, String ExpirationResponse, String UserName, String OrgName, String Pin, String VerifyURL, String OAuth_Token, String Expected){
		String Response = null;
		Helper_Functions.PrintOut("Check that once the expiration time is over the user can no longer complete registration with expired pin number.", false);
		try {
			Date CurrrentTime = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a zzz");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			dateFormat.format(CurrrentTime);
			Date ExpirationTime = null;
			try {
				ExpirationTime = dateFormat.parse(ExpirationResponse);
			} catch (Exception e1) {
				e1.printStackTrace();
			};
			
			Thread currentThread = Thread.currentThread();
			long ThreadID = currentThread.getId();
			SimpleDateFormat TF = new SimpleDateFormat("HH:mm:ss");
			while (ExpirationTime.compareTo(CurrrentTime) == 1){
				Helper_Functions.Wait(60);
				CurrrentTime = new Date();
				System.out.println("(" + ThreadID + " C:" + TF.format(CurrrentTime) + "->E:" + TF.format(ExpirationTime) + ") Waiting for Expiration " + OrgName );   //added to keep watch from gui and see progress. Will not update into file
			};
			Helper_Functions.Wait(60);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			dateFormat.format(CurrrentTime);
			Helper_Functions.PrintOut("Attempting to validate after expiraiton time has passed.\nCurrent Time: " + CurrrentTime, true);
			//Test verify pin on valid request from the different org
			Response = MFAC_API_Endpoints.VerifyPinAPI(UserName, OrgName, Pin, VerifyURL, OAuth_Token);
			assertThat(Response, containsString(Expected));//expected will either be success of pin failure based on scenario.
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")  //add due to the date comparison 
	@Test(dataProvider = "dp", priority = 1)
	public void AdditionalEnrollmentExpiration(String OrgName, String SecondOrg, String OAuth_Token, String IssueURL, String VerifyURL){
		Helper_Functions.PrintOut("Verify that the user recieves the updated expiration time when changing enrollment method.", false);
		
		String Response = null, UserName = MFAC_Helper_Functions.UserName(), Pin = null;
		
		try {
			Response = MFAC_API_Endpoints.IssuePinAPI(UserName, OrgName, IssueURL, OAuth_Token);
			assertThat(Response, containsString("pinExpirationDate"));
			
			Date CurrrentTime = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a zzz");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			dateFormat.format(CurrrentTime);
			Date ExpirationTime = MFAC_Helper_Functions.GetExpiration(Response);
			Date SecondExpirationTime = null;
			if (ExpirationTime.getDate() == CurrrentTime.getDate() && ExpirationTime.getMonth() == CurrrentTime.getMonth()) {
				Response =  MFAC_API_Endpoints.IssuePinAPI(UserName, SecondOrg, IssueURL, OAuth_Token);
				assertThat(Response, containsString("pinExpirationDate"));
				Pin = MFAC_Helper_Functions.ParsePIN(Response);
				SecondExpirationTime = MFAC_Helper_Functions.GetExpiration(Response);
			}

			if (SecondExpirationTime == null) {
				String LongExpirationMessage = "Not Validing the Expiraiton at this time, need to verify seperatly once the expiration has passed. Here is the CST time it will expire. " + ExpirationTime;
				Helper_Functions.PrintOut(LongExpirationMessage, true);
			}else if (SecondExpirationTime.compareTo(ExpirationTime) == 1) { //make sure second expiration is after initial would expire
				String Expire[] = new String[] {"AdditionalEnrollmentExpiration: " + Response, dateFormat.format(ExpirationTime).toString(), UserName, SecondOrg, Pin, VerifyURL, OAuth_Token, "Success"};
				ExpirationData.add(Expire);
			}else {
				//Test verify pin on valid request from the different org, this will not wait for the old one to have expired.
				Response = MFAC_API_Endpoints.VerifyPinAPI(UserName, SecondOrg, Pin, VerifyURL, OAuth_Token);
				assertThat(Response, containsString("Success"));
			}
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test(dataProvider = "dp", dependsOnMethods = "AdditionalEnrollmentExpiration", priority = 3)
	public void AdditionalEnrollmentExpirationValidate(String Result, String ExpirationResponse, String UserName, String OrgName, String Pin, String VerifyURL, String OAuth_Token, String Expected){
		String Response = null;
		Helper_Functions.PrintOut("Verify that the user can switch enrollment methods mid process and still complete registration.", false);
		try {
			Date CurrrentTime = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a zzz");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			dateFormat.format(CurrrentTime);
			Date ExpirationTime = null;
			try {
				ExpirationTime = dateFormat.parse(ExpirationResponse);
			} catch (Exception e1) {
				e1.printStackTrace();
			};
			
			Thread currentThread = Thread.currentThread();
			long ThreadID = currentThread.getId();
			SimpleDateFormat TF = new SimpleDateFormat("HH:mm:ss");
			while (ExpirationTime.compareTo(CurrrentTime) == 1){
				Helper_Functions.Wait(60);
				CurrrentTime = new Date();
				System.out.println("(" + ThreadID + " C:" + TF.format(CurrrentTime) + "->E:" + TF.format(ExpirationTime) + ") Waiting for Expiration " + OrgName );   //added to keep watch from gui and see progress. Will not update into file
			};
			Helper_Functions.Wait(60);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			dateFormat.format(CurrrentTime);
			Helper_Functions.PrintOut("Attempting to validate after expiraiton time has passed.\nCurrent Time: " + CurrrentTime, true);
			//Test verify pin on valid request from the different org
			Response = MFAC_API_Endpoints.VerifyPinAPI(UserName, OrgName, Pin, VerifyURL, OAuth_Token);
			assertThat(Response, containsString(Expected));//expected will either be success of pin failure based on scenario.
		}catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
}