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
    	//This will remove the "NameOfSetEnvTest" method from the test results.
    	//This is the method that sets the environment variable from XML and does not need to be reported.
    	 Iterator<ITestResult> PassedTestCase = arg0.getPassedTests().getAllResults().iterator();
    	 String NameOfSetEnvTest = "SetEvironmentLevel";
         while (PassedTestCase.hasNext()) {
             ITestResult EnvironmentTestCheck = PassedTestCase.next();
             ITestNGMethod method = EnvironmentTestCheck.getMethod();
             if (method.getMethodName().contentEquals(NameOfSetEnvTest)) {
                 System.out.println("Removing:" + EnvironmentTestCheck.getTestClass().toString());
                 PassedTestCase.remove();
             }
         }
         
         //remove all skipped tests from the results.
         try {
             Iterator<ITestResult> SkippedTestCase = arg0.getSkippedTests().getAllResults().iterator();
             while (SkippedTestCase.hasNext()) {
            	 SkippedTestCase.remove();
             }
         }catch(Exception e) {}

        Helper_Functions.PrintOut("Passed Scenarios", false);
 		PassedTestCase = arg0.getPassedTests().getAllResults().iterator();
        while (PassedTestCase.hasNext()) {
             ITestResult Test = PassedTestCase.next();
             Helper_Functions.PrintOut(Test.getAttribute("ExecutionLog").toString(), false);
         }
        Helper_Functions.PrintOut("\nFailed Scenarios", false);
        Iterator<ITestResult> FailedTestCase = arg0.getFailedTests().getAllResults().iterator();
        while (FailedTestCase.hasNext()) {
             ITestResult Test = FailedTestCase.next();
             Helper_Functions.PrintOut(Test.getAttribute("ExecutionLog").toString(), false);
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