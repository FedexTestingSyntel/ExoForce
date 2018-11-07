package TestingFunctions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import SupportClasses.ThreadLogger;

public class Helper_Functions{
	public static String Passed = "Passed", Failed = "Fail", Skipped = "Skipped";

	public static String CurrentDateTime() {
		Date curDate = new Date();
    	SimpleDateFormat Dateformatter = new SimpleDateFormat("MMddyy");
    	SimpleDateFormat Timeformatter = new SimpleDateFormat("HHmmss");
    	return Dateformatter.format(curDate) + "T" + Timeformatter.format(curDate);
	}
	
	public static String CurrentDateTime(boolean t) {
		Date curDate = new Date();
    	SimpleDateFormat DateTime= new SimpleDateFormat("MM-dd-yy HH:mm:ss:SS");
    	return DateTime.format(curDate);
	}
	
	public static void Wait(long Seconds) {
		try {
			Thread.sleep(Seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void PrintOut(String Text, boolean TimeStamp){
		long ThreadID = Thread.currentThread().getId();
		
		if (TimeStamp) {
			Text = CurrentDateTime() + ": " + Text;
			System.out.println(ThreadID + " " + Text); 
		}else {
			System.out.println(Text);
		}
		
		Text = Text.replaceAll("\n", System.lineSeparator());
		ThreadLogger.getInstance().UpdateLogs(Text);//Store the all values that are printed for a given thread.
	}
	
    public static void MoveOldLogs(){
    	String main_Source = "." + File.separator + "SavedLogs";
    	File main_dir = new File(main_Source);
    	if(main_dir.isDirectory()) {
    	    File[] content_main = main_dir.listFiles();
    	    for(int j = 0; j < content_main.length; j++) {
    	    	try {
    	        	String PathSource = content_main[j].getPath();
    	        	String PathDestination = content_main[j].getPath() + File.separator + "Old";
    	        	File source_dir = new File(PathSource);
    	        	File destination_dir = new File(PathDestination);
    	        	int year = Calendar.getInstance().get(Calendar.YEAR);
    	        	int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    	        	int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    	        	if(source_dir.isDirectory()) {
    	        	    File[] content_subfolder = source_dir.listFiles();
    	        	    for(int i = 0; i < content_subfolder.length; i++) {
    	        	    	if (content_subfolder[i].isDirectory()){
    	        	    		break; //do not need to seach within subfolders.
    	        	    	}
    	        	    	
    	        	    	try {
    	        	        	if (!destination_dir.exists()) {// if the directory does not exist, create it
    	        	        	    PrintOut("Creating directory: " + destination_dir.getName(), true);
    	        	        	    try{
    	        	        	    	destination_dir.mkdir();
    	        	        	        PrintOut(PathDestination + " DIR created", true);  
    	        	        	    } catch(SecurityException se){}        
    	        	        	}
    	        	        	BasicFileAttributes attr = Files.readAttributes(Paths.get(content_subfolder[i].getPath()), BasicFileAttributes.class);
    	        	        	String creationtime = " " + attr.creationTime();
    	        	        	if (!creationtime.contains(Integer.toString(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day))){//if the file was created before today
    	        	        		String localPathDestination = PathDestination + File.separator + creationtime.substring(1, 8);
    	        	        		File old_month_dir = new File(localPathDestination);
    	        	        		if (!old_month_dir.exists()) {// if the directory does not exist for the old month then create it
    	            	        	    PrintOut("Creating directory: " + old_month_dir.getName(), true);
    	            	        	    try{
    	            	        	    	old_month_dir.mkdir();
    	            	        	        PrintOut(localPathDestination + " DIR created", true);  
    	            	        	    } catch(SecurityException se){}
    	            	        	}
    	        	        		//Files.move(from, to, CopyOption... options).
    	        	        		Files.move(Paths.get(content_subfolder[i].getPath()), Paths.get(content_subfolder[i].getPath().replace(PathSource, localPathDestination)), StandardCopyOption.REPLACE_EXISTING);
    	        	        	}
    	    				}catch (Exception e) {}
    	        	    }
    	        	}
				}catch (Exception e) {}
    	    }//end for finding each individual app
    	}
    }//end MoveOldScreenshots
    
	public static String getRandomString(int Length) {
        //String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String SALTCHARS = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < Length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
	
	public static String getRandomStringAlpha(int Length) {
        //String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < Length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
	
	public static String ParseValueFromResponse(String s, String ident) {
		if(s.contains(ident)) {
			String TransactionIdStart = ident + "\":\"";
			int TransactionIdEnd;
			for (TransactionIdEnd = s.indexOf(TransactionIdStart) + TransactionIdStart.length(); TransactionIdEnd < s.length(); TransactionIdEnd++) {
				if (s.substring(TransactionIdEnd, TransactionIdEnd + 1).contentEquals("\"")) {
					break;
				}
			}
			return s.substring(s.indexOf(TransactionIdStart) + TransactionIdStart.length(), TransactionIdEnd);
		}
		return null;
	}
}//End Class