package USRC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import Data_Structures.USRC_Data;
import SupportClasses.Set_Environment;
import SupportClasses.ThreadLogger;
import TestingFunctions.Helper_Functions;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import API_Calls.*;

@Listeners(SupportClasses.TestNG_TestListener.class)

public class USRC_General {

	static String LevelsToTest = "3"; //Can but updated to test multiple levels at once if needed. Setting to "23" will test both level 2 and level 3.

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
	    	
			switch (m.getName()) { //Based on the method that is being called the array list will be populated.
			case "CreateUsers":
				for (int j = 0 ; j < 1; j++) {
					data.add(new Object[] {USRC_D, j});
				}
				break;
			case "CheckLogin":
				data.add(new Object[] {USRC_D, "L3WIDMUS122618T110230wpct", "Test1234"});
			}//end switch MethodName
		}
		return data.iterator();
	}
	
	@Test (dataProvider = "dp")
	public void CreateUsers(USRC_Data USRC_Details, int ContactPosition) {
		String UUID = null, fdx_login_fcl_uuid[] = {"",""};
			//1 - Login, get cookies and uuid
			String UserID = "L" + USRC_Details.Level + "UpdatePassword" + Helper_Functions.CurrentDateTime() + Helper_Functions.getRandomString(2);
			String Password = "Test1234";
			
			//create the new user
			String ContactDetails[] = USRC_Data.ContactDetailsList.get(ContactPosition % USRC_Data.ContactDetailsList.size());
			ContactDetails[4] = "Saqqqqwwwweeeerrrr.OSV@FEDEX.COM";
			String Response = USRC_API_Endpoints.NewFCLUser(USRC_Details.REGCCreateNewUserURL, ContactDetails, UserID, Password);
			
			//check to make sure that the userid was created.
			assertThat(Response, containsString("successful\":true"));
			
			//get the cookies and the uuid of the new user
			fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.LoginUserURL, UserID, Password);
			UUID = fdx_login_fcl_uuid[1];
			
			Helper_Functions.PrintOut(UserID + "/" + Password + "--" + UUID, false);

	}

	
	public final static Lock Excellock = new ReentrantLock();//prevent excel ready clashes
	@Test (dataProvider = "dp")
	public void CheckLogin(USRC_Data USRC_Details, String UserID, String Password) {
		String UUID = null, Cookies = null, fdx_login_fcl_uuid[] = {"",""};
		//get the cookies and the uuid of the user
		fdx_login_fcl_uuid = USRC_API_Endpoints.Login(USRC_Details.LoginUserURL, UserID, Password);
		Cookies = fdx_login_fcl_uuid[0];
		UUID = fdx_login_fcl_uuid[1];
		Helper_Functions.PrintOut(UserID + "/" + Password + "--" + UUID, false);
		
		String ContactDetails = USRC_API_Endpoints.ViewUserProfileWIDM(USRC_Details.ViewUserProfileWIDMURL, Cookies);
		String ContactDetailsParsed[] = USRC_API_Endpoints.Parse_ViewUserProfileWIDM(ContactDetails);
		ContactDetailsParsed[0] = UUID;
		ContactDetailsParsed[1] = UserID;
		ContactDetailsParsed[2] = Password;
		Helper_Functions.PrintOut("Contact Details: " + Arrays.toString(ContactDetailsParsed), true);
		try {
			Excellock.lock();
			//Read the spreadsheet that needs to be updated
			String FileName = "C:\\Users\\5159473\\Documents\\GitHub\\Data\\TestingData.xls";
			FileInputStream fsIP= new FileInputStream(new File(FileName));  
			//Access the workbook                  
			HSSFWorkbook wb = new HSSFWorkbook(fsIP);
			//Access the worksheet, so that we can update / modify it. 
			HSSFSheet worksheet = wb.getSheetAt(0);
			for(int i = 1; i< wb.getNumberOfSheets() + 1;i++) {
				//PrintOut("CurrentSheet: " + worksheet.getSheetName(), false);  //for debugging if getting errors with sheet not found
				if (worksheet.getSheetName().contentEquals("L" + USRC_Details.Level)) {
					break;
				}
				worksheet = wb.getSheetAt(i);
			}
			
			for (int j = 0; j < worksheet.getLastRowNum() + 1; j++) {
				if (worksheet.getRow(j).getCell(1) != null && worksheet.getRow(j).getCell(1).getStringCellValue().contentEquals(UserID)) {
					for (int k = 0; k < ContactDetailsParsed.length; k++) {
						if (worksheet.getRow(j).getCell(k) == null) {//if cell not present create it
							worksheet.getRow(j).createCell(k);
						}
						Cell cell = null; 
						cell = worksheet.getRow(j).getCell(k);
						cell.setCellValue(ContactDetailsParsed[k]);
					}
					
				}
			}
			//Close the InputStream  
			fsIP.close(); 
			//Open FileOutputStream to write updates
			FileOutputStream output_file =new FileOutputStream(new File(FileName));  
			//write changes
			wb.write(output_file);
			//close the stream
			output_file.close();
			wb.close();
		}catch (Exception e) {
			Helper_Functions.PrintOut(e.getMessage(), true);
		}finally {
			Excellock.unlock();
		}
	}



}
