package hp.Seals.APITest;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;
import org.testng.*;
import org.testng.annotations.*;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.APIPath;
import apiConfig.HeaderConfigs;
import apiVerification.APIVerification;
import baseTest.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.*;



public class MaintenanceEventsApiDbTest extends BaseTest 
{
	final static Logger   logger = LogManager.getLogger(MaintenanceEventsApiDbTest.class);

	HeaderConfigs         header = new HeaderConfigs();
	ExcelFileSetFromProp  xlProp = new ExcelFileSetFromProp();

	List<List> 	       row = new ArrayList<List>();
	String[]  serialNumber = null ;
	String[] productNumber = null; 
	String       startDate = "" ;
	String         endDate = "" ;
	String            date = "";
	String[]      api_name = null;

	Map<String, String> hMapData = null;
	Map<Integer, List<String>> myMap = new HashMap<Integer, List<String>>();
	List<String> list = null;

	List<String> excelList = null;
	Map<Integer, List<String>> excelMap = new HashMap<Integer, List<String>>();


	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	@BeforeTest
	public void readExcel()
	{
		//readParameter_fromJenkins();

		String excelPath = "./Data/MaintenanceEventsData.xlsx";

		String sheetName = "Sheet1";

		Excel_Utility excel = null;
		try {
			excel = new Excel_Utility(excelPath,sheetName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Read dynamic row count of excel sheet from Properties file
		Map<String,String>  map1 = ExcelFileSetFromProp.propFileRead();
		int rowCount = 0;	
		if(map1.get("excelSheetRowCount") != null) {
			rowCount = Integer.parseInt(map1.get("excelSheetRowCount"));
		}

		for(int i = 1 ; i <= rowCount  ; i++)  // for(int i = 0 ; i<excel.getRowCount(); i++)
		{   
			List list = new ArrayList();
			for( int j = 0; j < 5 ; j++ ) {
				list.add(excel.getCellData(i, j));		
			}
			row.add(list);
		}
	}


	/* 
	 * ********************Get the Printer GetMaintenanceEvents API Information ******************************
	 * 
	 */

	@Test
	public void TC_Total_Events_CountFromMaintenanceEventsAPIvsDB()
	{	
		List serialNoList = new ArrayList();
//		if( ! hMapData.containsValue("GetMaintenanceEvents") )	{
//			throw new SkipException("Skipping 'TC_Total_Events_CountFromMaintenanceEventsAPIvsDB' test Method because resource was not available.");
//		}
//		//if( hMapData.containsValue("GetMaintenanceEvents") )	{
//		
//		test.assignCategory("GetMaintenanceEvents");	
//		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare Total_Events value from GetMaintenanceEvents Api from API Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0  )  {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.PINK));

			for(int i = 0; i < row.size(); i++) {
				List     rowvalue = row.get(i);
				String  serial_no = rowvalue.get(0).toString();
				String product_no = rowvalue.get(1).toString();
				String newStartDate = changedStartDateFormat(rowvalue.get(2).toString()) ;
				String newEndDate = changedEndDateFormat(rowvalue.get(2).toString(), rowvalue.get(3).toString());
				
				logger.info("**** New StartDate:>> " + newStartDate);
				logger.info("**** New EndDate:>> " + newEndDate);
				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  AND based on Serial_no = " + serial_no + ", Product_No =" + product_no);
				
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on Serial_no = " + serial_no + ", Product_No = "  + product_no 
						+ " , newStartDate = " + newStartDate  + " and newEndDate =  " + newEndDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_MAINTENANCE_EVENTS.toString(),
								rowvalue.get(0).toString(),rowvalue.get(1).toString(), newStartDate,newEndDate ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetMaintenanceEvents",rowvalue.get(0).toString(),
						rowvalue.get(1).toString(),rowvalue.get(2).toString(),rowvalue.get(3).toString());

				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerification.verifyKey_Response(response, "total_Events");

				APIVerification.validateTotal_EventsCountObjectWithDB(response,"total_Events", 
						serial_no, product_no, newStartDate, newEndDate, serialNoList);

				this.readParamsFromExcel(i, serial_no, product_no, rowvalue.get(2).toString() ,rowvalue.get(3).toString());
//			}
//			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::>> ", ExtentColor.INDIGO) );
//			test.log(Status.PASS,"" + excelMap);
//
//		} else {
//
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading Parameters from Parameterized job *** ",ExtentColor.PINK));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//
//				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );
//				String newStartDate = changedStartDateFormat(startDate) ;
//				String newEndDate = changedEndDateFormat( startDate, endDate );
//				logger.info("**** New StartDate= " + newStartDate + "\n**** New EndDate= " + newEndDate);
//
//				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + 
//						"    AND based on Serial_no = " + serialNumber[i] + "  ,product_no = " + productNumber[i] 
//								+ " , StartDate = " + newStartDate + "  and EndDate = " + newEndDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_MAINTENANCE_EVENTS.toString(),
//						serialNumber[i], productNumber[i] , newStartDate ,newEndDate ));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetMaintenanceEvents",serialNumber[i], productNumber[i] ,newStartDate ,newEndDate);
//
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerification.validateTotal_EventsCountObjectWithDB(response,"total_Events" ,serialNumber[i], productNumber[i] ,newStartDate ,newEndDate ,serialNoList);
//			}
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );
//			logger.info(".....................Parameter Passing successful ..................." );
//			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK));
		}
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + serialNoList, ExtentColor.RED) );
	//}
  }


/*
 *   ************************************ Private Methods ******************************************
 */
private void  readParameter_fromJenkins()
{
	String 				sn = System.getProperty("param1"); 	
	String 				pn = System.getProperty("param2"); 	
	String 			 sDate = System.getProperty("param3");	
	String           eDate = System.getProperty("param4"); 
	String         apiname = System.getProperty("param");
	String       eventcode = System.getProperty("param5");
	String   detectiondate = System.getProperty("param6");

	logger.info("Serial No		:= " + sn);
	logger.info("Product No		:= " + pn);
	logger.info("Start Date		:= " + sDate);
	logger.info("End Date		:= " + eDate);
	logger.info("API NAME		:= " + apiname);
	logger.info("EVENT CODE		:= " + eventcode);
	logger.info("DETECTION DATE	:= " + detectiondate);

	//test.log(Status.INFO,"Job Executes on Which API=> " +  apiname );

	if( !(sn.isEmpty()) ) {
		serialNumber = sn.split(",");			
		for (int i = 0; i < serialNumber.length; i++) {      
			logger.info("Serial Numbers are==> " + serialNumber[i] );
		}			
	}
	//logger.info("Serial Numbers are==> " + serialNumber );

	if( !(pn.isEmpty()) ) {
		productNumber = pn.split(",");
		for (int i=0; i < serialNumber.length; i++) {      
			logger.info("Product Numbers are==> " + productNumber[i] );
		}			
	}


	startDate = sDate;
	endDate = eDate;

	if( apiname != null ) 
	{
		api_name = apiname.split(",");
		hMapData = new HashMap<String, String>();
		for(String str : api_name) {  //iterate through an array
			hMapData.put( "key_" + str , str );    //split the data by :
		}
		logger.info("String Array to HashMap: " + hMapData);	
	}

	if(serialNumber != null) {

		for(int i = 0; i < serialNumber.length ; i++ )
		{
			list = new ArrayList<String>();
			list.add( serialNumber[i] );
			list.add( productNumber[i] );
			list.add( startDate );
			list.add( endDate );
			logger.info("Data Lists: " + list);	

			myMap.put(i+1,list);			
		}
	}

}// end private method

/*
 * Reading parameters data from excel
 */

private void readParamsFromExcel(int i, String srNo, String prodNo, String startDate, String endDate) {
	excelList = new ArrayList<String>();
	excelList.add( srNo);
	excelList.add( prodNo);
	excelList.add( startDate);				
	excelList.add( endDate);				
	logger.info("Excelsheet Data Lists: " + excelList);
	excelMap.put(i+1,excelList);
}
//End Private Method
/*
 * Start Date is changed into EXample: 2020-01-05T00:00:00Z
 * End Dade is changed into same day,month and year of the start date with 24hr format Example:2020-01-05T23:59:59Z
 */

private static String changedStartDateFormat(String startDate ) 
{
	String[] dateParts = startDate.split("T");
	//		for (String a : dateParts) 
	//           		System.out.println("Splits Start DATE: " + a.toString());		
	String s1 = dateParts[0];
	//logger.info("\n Change start DATE: " + s1 );

	String startDateTime = s1 + "T" + "00:00:00Z"; 
	//logger.info("\n New StartDate ----->  " + startDateTime);

	return startDateTime;
}

private static String changedEndDateFormat(String startDate , String endDate)  
{
	String[] dateParts = startDate.split("T");
	//		for (String a : dateParts) 
	//           		logger.info("Splits Start DATE: " + a.toString());		
	String s1 = dateParts[0];
	//logger.info("\n Changed start DATE: " + s1 );

	// Operation on end Date
	String[] datePart = endDate.split("T");
	//		for (String st : datePart) 
	//           		logger.info("Splits End DATE: " + st.toString());		
	String s2 = datePart[0];
	s2 = s1;
	//logger.info("\n Changed END DATE: " + s2  + "\t");

	String endDateTime = s2 + "T23:59:59Z"; 
	//logger.info("\n New EndDate ----->  " + endDateTime);

	return endDateTime;
}



} // End Class




