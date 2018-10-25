package SupportClasses;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class Set_Environment {

	   public static void SetLevelsToTest(String Levels) {
		   if (ThreadLogger.LevelsToTest == null) {
			   ThreadLogger.LevelsToTest = Levels;
			   System.err.println("Levels set to " + Levels);
		   }else {
			   System.err.println("Levels has already been set from XML. Will execute with " + ThreadLogger.LevelsToTest);
		   }
	   }
	   
		@Test (priority = 0)//will run on data sent in XML to configure levels
		@Parameters("Level")
		public void testMethod(@Optional String Levels) {
			if (Levels != null) {
				ThreadLogger.LevelsToTest = Levels;
			}
		}
	
}
