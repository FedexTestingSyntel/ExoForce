package SupportClasses;

import java.util.ArrayList;    //The below needed for tracking the status of the tests.
import java.util.Iterator;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import SupportClasses.ThreadLogger;
import TestingFunctions.Helper_Functions;

public class TestNG_TestListener implements ITestListener{
	
	@Override
    public void onStart(ITestContext arg0) {
    }
	
	@Override
    public void onTestStart(ITestResult arg0) {
    }

    @Override
    public void onTestSuccess(ITestResult arg0) {
    	TestResults(arg0);
    }

    @Override
    public void onTestFailure(ITestResult arg0) {
    	TestResults(arg0);
    }

    @Override
    public void onTestSkipped(ITestResult arg0) {
    	TestResults(arg0);
    }

    @Override
    public void onFinish(ITestContext arg0) {
         //remove all skipped tests from the results.
         try {
             Iterator<ITestResult> SkippedTestCase = arg0.getSkippedTests().getAllResults().iterator();
             while (SkippedTestCase.hasNext()) {
            	 SkippedTestCase.remove();
             }
         }catch(Exception e) {}

        
        Iterator<ITestResult> PassedTestCase = arg0.getPassedTests().getAllResults().iterator();
        int passedcount = 1;
        while (PassedTestCase.hasNext()) {
        	if (passedcount == 1) {
        		Helper_Functions.PrintOut("Passed Scenarios", false);
        	}
            ITestResult Test = PassedTestCase.next();
            Helper_Functions.PrintOut(passedcount + ") " + Test.getAttribute("ExecutionLog").toString(), false);
            passedcount++;
         }
        
        Iterator<ITestResult> FailedTestCase = arg0.getFailedTests().getAllResults().iterator();
        int failedcount = 1;
        while (FailedTestCase.hasNext()) {
        	if (failedcount == 1) {
        		 Helper_Functions.PrintOut("\nFailed Scenarios", false);
        	}
             ITestResult Test = FailedTestCase.next();
             Helper_Functions.PrintOut(failedcount + ") " + Test.getAttribute("ExecutionLog").toString(), false);
             failedcount++;
         }
         
    		
		try {
			Helper_Functions.MoveOldLogs();
		} catch (Exception e) {}
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
        // TODO Auto-generated method stub
    }
    
    private void TestResults(ITestResult arg0) {
    	String AttemptLogs = ThreadLogger.getInstance().ReturnLogString();
    	arg0.setAttribute("ExecutionLog", AttemptLogs);// this will save the trace in a collapsable format
    	//arg0.setAttribute("ExecutionLog", ThreadLogger.getInstance().ReturnLogString());
    	
    	ArrayList<String> CurrentLogs = ThreadLogger.getInstance().ReturnLogs();
    	String TestCompleteData = "";		
    	for (int i = 0; i < CurrentLogs.size(); i++){
		    TestCompleteData += CurrentLogs.get(i) + System.lineSeparator();
		}
    	ThreadLogger.ThreadLog.add(TestCompleteData + System.lineSeparator());
    }
}