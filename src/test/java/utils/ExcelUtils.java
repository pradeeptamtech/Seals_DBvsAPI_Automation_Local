package utils;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	
	static XSSFWorkbook workbook;	
	static  XSSFSheet sheet;

	public ExcelUtils(String excelPath,String excelSheetname)
	{
		try {
				workbook = new XSSFWorkbook();
				sheet = workbook.getSheet(excelSheetname);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
			e.printStackTrace();
		}
	}

	public static void getCellData(int rows,int columns)
	{
		DataFormatter formater = new DataFormatter();
		Object val = formater.formatCellValue(sheet.getRow(rows).getCell(columns));
		System.out.println(val);
	}
	public static void getRowCount() 
	{
		int rowCount = sheet.getPhysicalNumberOfRows();
		System.out.println("Row Count... "+rowCount);
	}
	

}


