package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//import com.relevantcodes.extentreports.LogStatus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

public class Excel_Utility {
	//public static void main(String[] args) throws Exception {
		//String excelPath = "./Data/TestData.xlsx";
		//String sheetName = "Sheet1";
		static XSSFSheet sheet;
		public Excel_Utility(String excelPath,String sheetName) throws FileNotFoundException, IOException
		{
		  try 
		  {
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excelPath));
		//	        String sheetname=workbook.getSheetName(0);
		//	        System.out.println(sheetname);

		sheet = workbook.getSheet(sheetName);
	
		Iterator<Row> itr = sheet.iterator();    //iterating over excel file
		while (itr.hasNext())
		{
			Row row = itr.next();
			Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
			while (cellIterator.hasNext())
			{
				Cell cell = cellIterator.next();
				switch (cell.getCellType())
				{
				case Cell.CELL_TYPE_STRING:    //field that represents string cell type
					//System.out.print(cell.getStringCellValue() +"\t\t");
					break;
				case Cell.CELL_TYPE_NUMERIC:    //field that represents number cell type
					//System.out.print(cell.getNumericCellValue() + "\t\t");
					break;
				default:
				}
			}
			//System.out.println("");
		}
	 }catch(Exception e){
			//System.out.println(e.getMessage());
			//System.out.println(e.getCause());
			e.printStackTrace();
		}
		
}
		
		public static Object getCellData(int rowNo,int columnNo)
		{
			//System.out.println("rowNo: " + rowNo + " and columnNo: "+ columnNo);
			DataFormatter formater = new DataFormatter();
			Object val = formater.formatCellValue(sheet.getRow(rowNo).getCell(columnNo));
			//System.out.print(" ::Cell value:: "+val);
			return val;
		}
		public static int getRowCount() 
		{
			int rowCount = sheet.getPhysicalNumberOfRows();
			//System.out.println("Row Count... "+rowCount);
			return rowCount;
			
		}	
		
}
