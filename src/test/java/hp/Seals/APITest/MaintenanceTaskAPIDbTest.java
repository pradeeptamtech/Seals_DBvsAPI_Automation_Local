package hp.Seals.APITest;

import org.testng.annotations.Test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.annotations.BeforeTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import org.apache.log4j.LogManager;  
import org.apache.log4j.Logger;  

import apiConfig.APIPath;
import apiConfig.HeaderConfigs;
import apiVerification.APIVerification;
import baseTest.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import utils.ExcelFileSetFromProp;
import utils.Excel_Utility;
import utils.ExtentReportListener;
import utils.UtilityApiMethods;

public class MaintenanceTaskAPIDbTest extends BaseTest {

	final static Logger logger = LogManager.getLogger(MaintenanceTaskAPIDbTest.class); 

	HeaderConfigs header = new HeaderConfigs();
	ExcelFileSetFromProp  xlProp = new ExcelFileSetFromProp();

	List<List> row = new ArrayList<List>();

	String[] serialNumber = null ;
	String[] productNumber = null; 
	String startDate = "" ;
	String endDate = "" ;
	String date = "";
	String[] api_name = null;
	String[] eventCode = null;
	String detectionDate = "";

	Map<String, String> hMapData = null;
	Map<Integer, List<String>> myMap = new HashMap<Integer, List<String>>();
	List<String> list = null;

	List<String> lt1 = null;
	Map<Integer, List<String>> mainteananceTasksMap = new HashMap<Integer, List<String>>();

	List<String> lt2 = null;
	Map<Integer, List<String>> entitledObligationMap = new HashMap<Integer, List<String>>();

	List<String> lt = null;
	Map<Integer, List<String>> eventMap = new HashMap<Integer, List<String>>();

	List<String> excelList = null;
	Map<Integer, List<String>> excelMap = new HashMap<Integer, List<String>>();


	@BeforeTest
	public void readExcel()
	{
		String excelPath = "./Data/MaintenanceData.xlsx";
		String sheetName = "Sheet1";
		//String excelPath = "./Data/TestData.xlsx";
		//String sheetName = "Sheet2";

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
			for( int j = 0; j < 8 ; j++ ) {
				list.add(excel.getCellData(i, j));		
			}
			row.add(list);
		}
	}


	/* 
	 * ********* Compare 'progress_Percentage' count from Response with Seals_DB for Get Maintenance task Api Test ******************* 
	 * 
	 */

	//@Test
	public void getMaintenance_progressPercentage_Count_CompareWithDB()
	{	
		if(hMapData.containsValue("GetMaintenanceTasks") )
		{		
			test.assignCategory("getMaintenance_progressPercentage_Count_CompareWithDB");	
			test.log(Status.INFO ,MarkupHelper.createLabel( "*** Compare 'progress_Percentage' count  from GetMaintenanceTasks APi reponse with Seals DB ***",ExtentColor.GREEN));

			if( serialNumber == null || serialNumber.length == 0  )  {
				test.log(Status.PASS,MarkupHelper.createLabel("************* Reading Parameters from Excel Sheet ************** ",ExtentColor.PINK ));			

				List listOfSerialNo = new ArrayList();
				for(int i = 0; i < row.size(); i++)
				{			
					List rowvalue = row.get(i);
					test.log(Status.PASS, "<============ Test Executed based on Serial_no === " + rowvalue.get(0));

					Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.
							apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),
									rowvalue.get(0).toString(),rowvalue.get(1).toString(),rowvalue.get(2).toString()));

					UtilityApiMethods.verifyResponseCode(response, 200, "GetMaintenanceTasks" , rowvalue.get(0).toString(),rowvalue.get(1).toString(),rowvalue.get(3).toString() ) ;
					UtilityApiMethods.Validate_ResponseTime(response);

					//UtilityApiMethods.ResponseKeyValidationFromJsonObject(response, "serial_Number");
					UtilityApiMethods.verifyKeyValueFromResponse(response, "list_maintenances", "progress_Percentage" );

					APIVerification.validateProgress_percentageCountApiWithDB(response, "list_maintenances", "progress_Percentage", rowvalue.get(0).toString(),
							rowvalue.get(1).toString(),rowvalue.get(2).toString(),rowvalue.get(3).toString(), listOfSerialNo);


				}
				logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
				test.log(Status.PASS," List of Serial no's are failed----> " + listOfSerialNo );

			} else {
				test.log(Status.PASS,MarkupHelper.createLabel("*** Reading Parameters from Parameterized job **** ",ExtentColor.PINK));
				List listOfSerialNo = new ArrayList();
				for(int i=0 ; i < serialNumber.length; i++) {

					test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
					logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

					Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),
							serialNumber[i],productNumber[i],date ));

					UtilityApiMethods.verifyResponseCode(response, 200, "GetMaintenanceTasks",serialNumber[i],productNumber[i],date  ) ;
					//UtilityApiMethods.responseCodeValidation(response, 200);
					UtilityApiMethods.Validate_ResponseTime(response);

					//UtilityApiMethods.ResponseKeyValidationFromJsonObject(response, "serial_Number");
					UtilityApiMethods.verifyKeyValueFromResponse(response, "list_maintenances", "progress_Percentage" );

					APIVerification.validateProgress_percentageCountApiWithDB(response, "list_maintenances", "progress_Percentage", serialNumber[i],productNumber[i],startDate, endDate, listOfSerialNo);

				}
			}
		}
	}

	/* 
	 * *****Compare "progress_Percentage" Value from Api response with Seals DB for Get Maintenance task Api Test *************************  
	 */

	//@Test
	public void getMaintenance_progressPercentage_Value_CompareWithDB()
	{
		test.log(Status.INFO , "*** Compare 'progress_Percentage' Value from response with DB  *********");
		List listOfSerialNo = new ArrayList();
		for(int i=0; i<row.size(); i++) {

			List rowvalue=row.get(i);
			test.log(Status.PASS , "<<<<<<<<<< Test Executed based on Serial_no = " + rowvalue.get(0));
			logger.info( "<<===== Test Executed based on Serial_no :: " + rowvalue.get(0));

			Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),rowvalue.get(0).toString(),rowvalue.get(1).toString(),rowvalue.get(2).toString()));

			UtilityApiMethods.responseCodeValidation(response, 200);
			UtilityApiMethods.Validate_ResponseTime(response);

			UtilityApiMethods.verifyKeyValueFromResponse(response, "list_maintenances", "progress_Percentage" );



		}
		test.log(Status.INFO	, "******* getMaintenance_progressPercentage_Value_CompareWithDB Test is Ended *******");
	}

	/*
	 *   ************************************ Private Methods *************************************************
	 */
	private void  readParameter_fromJenkins()
	{
		String sn = System.getProperty("param1"); 	
		String pn = System.getProperty("param2"); 	
		String sDate = System.getProperty("param3");	
		String eDate = System.getProperty("param4"); 
		String apiname = System.getProperty("param");

		String eventcode = System.getProperty("param5");
		String detectiondate = System.getProperty("param6");

		logger.info("Serial No:= " + sn);
		logger.info("Product No:= " + pn);
		logger.info("Start Date:= " + sDate);
		logger.info("End Date:= "  + eDate);
		logger.info("API NAME:= "  + apiname);
		logger.info("EVENT CODE:= "  + eventcode);
		logger.info("DETECTION DATE:= "  + detectiondate);

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

		// needed for GetMaintenanceTasks API
		date = sDate;
		if(serialNumber != null) {
			for(int i = 0; i < serialNumber.length ; i++ )
			{
				lt1 = new ArrayList<String>();
				lt1.add( serialNumber[i] );
				lt1.add( productNumber[i] );
				lt1.add( date );
				logger.info("Data Lists: " + lt1);	

				mainteananceTasksMap.put(i+1,lt1);			
			}
		}
		// needed for GetObligation API
		if(serialNumber != null) {
			for(int i = 0; i < serialNumber.length ; i++ )
			{
				lt2 = new ArrayList<String>();
				lt2.add( serialNumber[i] );
				lt2.add( productNumber[i] );
				logger.info("Data Lists: " + lt2);
				entitledObligationMap.put(i+1,lt2);			
			}
		}		

		// Needed for GetSolution API
		if( !(eventcode.isEmpty()) ) {
			eventCode = eventcode.split(",");
		}
		//		if( detectiondate != null) {
		//			detectionDate = detectiondate.split(",");
		//		}
		detectionDate = detectiondate;
		if(!(eventcode.isEmpty())) {
			for(int i = 0; i < productNumber.length ; i++ )
			{
				lt = new ArrayList<String>();
				lt.add( productNumber[i] );
				lt.add( serialNumber[i] );				
				lt.add( eventCode[i] );
				lt.add( detectionDate );
				logger.info("Data Lists: " + lt );	

				eventMap.put(i+1,lt);			
			}
		}
	}

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

	// reading date from Excel for getMaintenanceTasks API
	private void readParamsFromExcelForMainteneanceTasks(int i, String srNo, String prodNo, String date) {
		excelList = new ArrayList<String>();
		excelList.add( srNo);
		excelList.add( prodNo);
		excelList.add( date);				
		logger.info("Excelsheet Data Lists: " + excelList);
		mainteananceTasksMap.put(i+1 , excelList);
	}
	// reading date from Excel for getObligation API
	private void readParamsFromExcelForGetObligation(int i, String srNo, String prodNo) {
		excelList = new ArrayList<String>();
		excelList.add( srNo);
		excelList.add( prodNo);
		logger.info("Excelsheet Data Lists: " + excelList);
		entitledObligationMap.put(i+1, excelList);
	}

	// reading date from Excel for getSolution API
	private void readParamsFromExcelForGetSolution(int i, String srNo, String prodNo, String eventCode, String detectionDate) {
		excelList = new ArrayList<String>();
		excelList.add( srNo);
		excelList.add( prodNo);
		excelList.add( eventCode);
		excelList.add(detectionDate);
		logger.info("Excelsheet Data Lists: " + excelList);
		eventMap.put(i+1, excelList);
	}

	//End Private Method

}
