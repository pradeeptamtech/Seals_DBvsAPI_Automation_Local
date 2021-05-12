package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

public class ExcelFileSetFromProp 
{
	public static Map<String,String> fileProp = new HashMap<String,String>();
	public static Properties propExcel = new Properties();

	public static Map<String,String> propFileRead()
	{
		try {
			FileInputStream fis = new FileInputStream("./inputs/excel.properties");
			propExcel.load(fis);
			fileProp.put("excelSheetRowCount",propExcel.getProperty("excelSheetRowCount"));

			//FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"/inputs/excel.properties");
			//ResourceBundle rb = ResourceBundle.getBundle("./inputs/excel.properties");
			// System.out.println("" + rb.getString("excelSheetRowCount"));

		}catch(Exception e) {
			e.fillInStackTrace();
		}		 
		return fileProp;
	}

	public static Map<String,String> getConfigRead()
	{
		if(fileProp == null) {
			fileProp = propFileRead();
		}
		return fileProp;
	}

//	public static void main(String[] args) throws IOException {
//
//		FileInputStream fis = new FileInputStream("./inputs/excel.properties");
//		propExcel.load(fis);
//		fileProp.put("excelSheetRowCount",propExcel.getProperty("excelSheetRowCount"));
//		//System.out.println(fileProp);
//		
//		//ResourceBundle rb = ResourceBundle.getBundle("../inputs/excel.properties");
//		//System.out.println("" + rb.getString("excelSheetRowCount"));
//	}

}
