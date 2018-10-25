package TestingFunctions;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import SupportClasses.ThreadLogger;
import jxl.Sheet;
import jxl.Workbook;

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

	public static ArrayList<String[]> getExcelData(String fileName, String sheetName) {
		//Note, may face issues if the file is an .xlsx, save it as a xls and works

		ArrayList<String[]> data = new ArrayList<>();
		try {
			
			
			/*
			FileInputStream fsIP= new FileInputStream(new File(fileName));                 
			HSSFWorkbook wb = new HSSFWorkbook(fsIP);
			HSSFSheet worksheet = wb.getSheetAt(0);
			for(int i = 1; i< wb.getNumberOfSheets() + 1;i++) {
				//PrintOut("CurrentSheet: " + worksheet.getSheetName(), false);  //for debugging if getting errors with sheet not found
				if (worksheet.getSheetName().contentEquals(sheetName)) {
					break;
				}
				worksheet = wb.getSheetAt(i);
			}
			DataFormatter dataFormatter = new DataFormatter();
			
			worksheet.forEach(row -> {
				ArrayList<String> buffer = new ArrayList<>();
	            row.forEach(cell -> {
	                String cellValue = dataFormatter.formatCellValue(cell);
	                buffer.add(cellValue);
	            });
	            String[] stringArray = buffer.toArray(new String[0]);
	            data.add(stringArray);
	        });
			//Close the InputStream  
			fsIP.close(); 
			//Open FileOutputStream to write updates
			FileOutputStream output_file =new FileOutputStream(new File(fileName));  
			//write changes
			wb.write(output_file);
			//close the stream
			output_file.close();
			wb.close();
			*/
			
			FileInputStream fs = new FileInputStream(fileName);
			Workbook wb = Workbook.getWorkbook(fs);
			Sheet sh = wb.getSheet(sheetName);
			
			int totalNoOfCols = sh.getColumns();
			int totalNoOfRows = sh.getRows();
			
			for (int i= 0 ; i < totalNoOfRows; i++) { //change to start at 1 if want to ignore the first row.
				String buffer[] = new String[totalNoOfCols];
				for (int j=0; j < totalNoOfCols; j++) {
					String CellContents = sh.getCell(j, i).getContents();
					if (CellContents == null) {
						CellContents = "";
					}
					buffer[j] = CellContents;
				}
				data.add(buffer);
			}
			wb.close();
			fs.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
}//End Class