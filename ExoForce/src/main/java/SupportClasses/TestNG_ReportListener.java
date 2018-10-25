package SupportClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;
import TestingFunctions.Helper_Functions;

public class TestNG_ReportListener implements IReporter {
	
	int totalTestCount = 0;
	int totalTestPassed = 0;
	int totalTestFailed = 0;
	int totalTestSkipped = 0;
	
	//This is the customize email able report template file path.
	private static final String emailableReportTemplateFile = System.getProperty("user.dir") + "/src/main/java/XMLExecution/customize-emailable-report-template.html";
	
	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		
		try{
			//Name of the test suit from the XML
			String Application = xmlSuites.get(0).getName().substring(0, 4);
			
			// Get content data in TestNG report template file.
			String customReportTemplateStr = this.readEmailabelReportTemplate();
			
			// Create custom report title.
			String customReportTitle = this.getCustomReportTitle(Application + " TestNG Report");
			
			// Create test suite summary data.
			String customSuiteSummary = this.getTestSuiteSummary(suites);
			
			// Create test methods summary data.
			String customTestMethodSummary = this.getTestMehodSummary(suites);
			
			// Replace report title place holder with custom title.
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$TestNG_Custom_Report_Title\\$", Matcher.quoteReplacement(customReportTitle));
			
			// Replace test suite place holder with custom test suite summary.
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$Test_Case_Summary\\$", Matcher.quoteReplacement(customSuiteSummary));
			
			// Replace test methods place holder with custom test method summary.
			customReportTemplateStr = customReportTemplateStr.replaceAll("\\$Test_Case_Detail\\$", Matcher.quoteReplacement(customTestMethodSummary));
			
			// Write replaced test report content to custom-emailable-report.html.
			String ReportName = Helper_Functions.CurrentDateTime() + " L" + ThreadLogger.LevelsToTest + " " + Application + " Report";
			String ReportTitle = "L" + ThreadLogger.LevelsToTest + " " + Application;
			outputDirectory = System.getProperty("user.dir") + "\\SavedLogs\\" + Application;
			outputDirectory += String.format("\\" + ReportName + " T%sP%sF%s.html", totalTestCount, totalTestPassed, totalTestFailed);
			
			File targetFile = new File(outputDirectory);
			customReportTemplateStr = "<!DOCTYPE html><html><head><title>" + ReportTitle + "</title><meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\" /></head>" + customReportTemplateStr + "</html>";
			
			//Create folder directory for writing the report.
			FileWriter fw = null;
			try {
				targetFile.createNewFile();
				fw = new FileWriter(targetFile);
				fw.write(customReportTemplateStr);
				System.out.println("Report Saved: " + outputDirectory + ".html");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Warning, Unable to create directory for: " + targetFile);
			}finally {
				fw.flush();
				fw.close();
			}
			
			//CreatePDFReport(outputDirectory, customReportTemplateStr);
			//Need to work on this, something is most likely wrong with HTML format.    //java.lang.IllegalArgumentException: The number of columns in PdfPTable constructor must be greater than zero.
		
	    	//Need to work on this to send out the email report.
	    	//java.net.URL classUrl = this.getClass().getResource("com.sun.mail.util.TraceInputStream");
	    	//System.out.println(classUrl.getFile());
			/*
			String SenderEamil, SenderPassword, RecipientEmail;
			ArrayList<String[]> PersonalData = new ArrayList<String[]>();
			PersonalData = Helper_Functions.getExcelData(".\\Data\\Load_Your_UserIds.xls",  "Data");//create your own file with the specific data
			for(String s[]: PersonalData) {
				if (s[0].contentEquals("GMAIL")) {
					SenderEamil = s[1];
					SenderPassword = s[2];
				}else if(s[0].contentEquals("MYEMAIL")){
					RecipientEmail = s[1];
				}
			}
	    	sendPDFReportByGMail(SenderAddress, SenderPassword, RecipientEmail, ReportTitle, customReportTemplateStr, outputDirectory);
	    	*/
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/* Read template content. */
	private String readEmailabelReportTemplate(){
		StringBuffer retBuf = new StringBuffer();
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			file = new File(TestNG_ReportListener.emailableReportTemplateFile);
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			
			String line = br.readLine();
			while(line!=null){
				retBuf.append(line);
				line = br.readLine();
			}
			
		}catch (Exception ex) {
			ex.printStackTrace();
		}finally{
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return retBuf.toString();
	}
	
	/* Build custom report title. */
	private String getCustomReportTitle(String title){
		StringBuffer retBuf = new StringBuffer();
		retBuf.append(title + " " + this.getDateInStringFormat(new Date()));
		return retBuf.toString();
	}
	
	/* Build test suite summary data. */
	private String getTestSuiteSummary(List<ISuite> suites){
		StringBuffer retBuf = new StringBuffer();
		
		try{
			for(ISuite tempSuite: suites){
				retBuf.append("<tr><td colspan=11><center><b>" + tempSuite.getName() + "</b></center></td></tr>");
				
				Map<String, ISuiteResult> testResults = tempSuite.getResults();
				
				for (ISuiteResult result : testResults.values()) {
					
					retBuf.append("<tr>");
					ITestContext testObj = result.getTestContext();
					totalTestPassed = testObj.getPassedTests().getAllMethods().size();
					totalTestSkipped = testObj.getSkippedTests().getAllMethods().size();
					totalTestFailed = testObj.getFailedTests().getAllMethods().size();
					totalTestCount = totalTestPassed + totalTestSkipped + totalTestFailed;
					
					/* Test name. */
					retBuf.append("<td>" + testObj.getName() + "</td>");
					/* Total method count. */
					retBuf.append("<td>" + totalTestCount + "</td>");
					/* Passed method count. */
					retBuf.append("<td bgcolor=green>" + totalTestPassed+ "</td>");
					/* Skipped method count. */
					retBuf.append("<td bgcolor=yellow>" + totalTestSkipped + "</td>");
					/* Failed method count. */
					retBuf.append("<td bgcolor=red>" + totalTestFailed + "</td>");
					/* Get browser type. */
					String browserType = tempSuite.getParameter("browserType");
					if(browserType==null || browserType.trim().length()==0){
						browserType = "Chrome";
					}
					/* Append browser type. */
					retBuf.append("<td>" + browserType + "</td>");
					/* Start Date*/
					Date startDate = testObj.getStartDate();
					retBuf.append("<td>" + this.getDateInStringFormat(startDate) + "</td>");
					/* End Date*/
					Date endDate = testObj.getEndDate();
					retBuf.append("<td>" + this.getDateInStringFormat(endDate) + "</td>");
					/* Execute Time */
					long deltaTime = endDate.getTime() - startDate.getTime();
					String deltaTimeStr = this.convertDeltaTimeToString(deltaTime);
					retBuf.append("<td>" + deltaTimeStr + "</td>");
					/* Include groups. */
					retBuf.append("<td>" + this.stringArrayToString(testObj.getIncludedGroups()) + "</td>");
					/* Exclude groups. */
					retBuf.append("<td>" + this.stringArrayToString(testObj.getExcludedGroups()) + "</td>");
					retBuf.append("</tr>");
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retBuf.toString();
	}

	/* Get date string format value. */
	private String getDateInStringFormat(Date date){
		StringBuffer retBuf = new StringBuffer();
		if(date==null){
			date = new Date();
		}
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		retBuf.append(df.format(date));
		return retBuf.toString();
	}
	
	/* Convert long type deltaTime to format hh:mm:ss:mi. */
	private String convertDeltaTimeToString(long deltaTime){
		StringBuffer retBuf = new StringBuffer();
		
		long milli = deltaTime;
		long seconds = deltaTime / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		retBuf.append(hours + ":" + minutes + ":" + seconds + ":" + milli);
		
		return retBuf.toString();
	}
	
	/* Get test method summary info. */
	private String getTestMehodSummary(List<ISuite> suites){
		StringBuffer retBuf = new StringBuffer();
		
		try{
			for(ISuite tempSuite: suites){
				retBuf.append("<tr><td colspan=7><center><b>" + tempSuite.getName() + "</b></center></td></tr>");
				
				Map<String, ISuiteResult> testResults = tempSuite.getResults();
				
				for (ISuiteResult result : testResults.values()) {
					
					ITestContext testObj = result.getTestContext();

					String testName = testObj.getName();
					
					/* Get failed test method related data. */
					IResultMap testFailedResult = testObj.getFailedTests();
					String failedTestMethodInfo = this.getTestMethodReport(testName, testFailedResult, false, false);
					retBuf.append(failedTestMethodInfo);
					
					/* Get skipped test method related data. */
					IResultMap testSkippedResult = testObj.getSkippedTests();
					String skippedTestMethodInfo = this.getTestMethodReport(testName, testSkippedResult, false, true);
					retBuf.append(skippedTestMethodInfo);
					
					/* Get passed test method related data. */
					IResultMap testPassedResult = testObj.getPassedTests();
					String passedTestMethodInfo = this.getTestMethodReport(testName, testPassedResult, true, false);
					retBuf.append(passedTestMethodInfo);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return retBuf.toString();
	}
	
	/* Get failed, passed or skipped test methods report. */
	private String getTestMethodReport(String testName, IResultMap testResultMap, boolean passedReault, boolean skippedResult){
		ArrayList<String[]> ResultList = new ArrayList<String[]>();
		StringBuffer retStrBuf = new StringBuffer();
		
		
		String resultTitle = testName;
		
		String color = "green";
		
		if(skippedResult){
			resultTitle += " - Skipped ";
			color = "yellow";
		}else if(passedReault){
			resultTitle += " - Passed ";
			color = "green";
		}else{
			resultTitle += " - Failed ";
			color = "red";
		}
		
		retStrBuf.append("<tr bgcolor=" + color + "><td colspan=7><center><b>" + resultTitle + "</b></center></td></tr>");
			
		Set<ITestResult> testResultSet = testResultMap.getAllResults();
			
		for(ITestResult testResult : testResultSet){
			StringBuffer sortingStrBuf = new StringBuffer();
			String Application = "", testMethodName = "", startDateStr = "", executeTimeStr = "", paramStr = "", reporterMessage = "", exceptionMessage = "";
			
			//Get Application name, should be the same as the tesitng class name
			Application = testResult.getTestClass().getName();
			Application = Application.substring(Application.lastIndexOf(".") + 1, Application.length());
				
			//Get testMethodName
			testMethodName = testResult.getMethod().getMethodName();
				
			//Get startDateStr
			long startTimeMillis = testResult.getStartMillis();
			startDateStr = this.getDateInStringFormat(new Date(startTimeMillis));

			//Get Execute time.
			long deltaMillis = testResult.getEndMillis() - testResult.getStartMillis();
			executeTimeStr = this.convertDeltaTimeToString(deltaMillis);

			//Get parameter list.
			Object paramObjArr[] = testResult.getParameters();
			for(Object paramObj : paramObjArr){
				try {
					paramStr += paramObj.toString() + "<br />";
				}catch (Exception e) {
					paramStr += paramObj + "<br />";
				}
			}
				
			//This is a custom variable that is set in the TestListener for trace of execution.
			Object val = testResult.getAttribute("ExecutionLog");
			String ExecutionLog = val.toString().replaceAll("\n", "<br />");
			reporterMessage = ExecutionLog;
			
			//Get exception message.
			Throwable exception = testResult.getThrowable();
			if(exception!=null){
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				exception.printStackTrace(pw);
				
				exceptionMessage = sw.toString();
			}
			
			sortingStrBuf.append("<tr bgcolor=" + color + ">");
			
			/* Add test class name. */
			sortingStrBuf.append("<td>" + Application + "</td>");
			/* Add test method name. */
			sortingStrBuf.append("<td>" + testMethodName + "</td>");
			/* Add start time. */
			sortingStrBuf.append("<td>" + startDateStr + "</td>");
			/* Add execution time. */
			sortingStrBuf.append("<td>" + executeTimeStr + "</td>");
			/* Add parameter. */
			sortingStrBuf.append("<td>" + paramStr + "</td>");
			/* Add reporter message. */
			sortingStrBuf.append("<td>" + reporterMessage + "</td>");
			/* Add exception message. */
			sortingStrBuf.append("<td>" + exceptionMessage + "</td>");;
			
			sortingStrBuf.append("</tr>");

			ResultList.add(new String[] {Application, sortingStrBuf.toString()});
		}

		Collections.sort(ResultList,new Comparator<String[]>() {
			public int compare(String[] strings, String[] otherStrings) {
				return strings[0].compareTo(otherStrings[0]);
			}
		});

		for (String[] sa : ResultList) {
			retStrBuf.append(sa[1]);
		}

		return retStrBuf.toString();
	}
	
	/* Convert a string array elements to a string. */
	private String stringArrayToString(String strArr[]) {
		StringBuffer retStrBuf = new StringBuffer();
		if(strArr!=null){
			for(String str : strArr){
				retStrBuf.append(str + " ");
			}
		}
		return retStrBuf.toString();
	}
}