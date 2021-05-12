package utils;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Excel_Utility_Test {
	public static void main(String[] args) throws FileNotFoundException, IOException {

		//String projDir  = System.getProperty("user.dir");
		//System.out.println("Path..."+ projDir);
		String excelPath = "./Data/TestData.xlsx";
		String sheetName = "Sheet1";

		Excel_Utility excel = new Excel_Utility(excelPath,sheetName);
		excel.getRowCount();
		excel.getCellData(1, 0);
		excel.getCellData(1, 1);

	}
}



