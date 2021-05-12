package hp.Seals.GetSolutionApiVsDB;

import org.testng.annotations.*;
import utils.*;
import java.io.*;
import java.util.*;
import apiConfig.*;
import apiVerification.APIVerification;

import org.apache.log4j.*;
import org.testng.*;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import baseTest.BaseTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;

@SuppressWarnings("unused")
public class GetSolutionAPIvsDBTest extends BaseTest 
{
	final static Logger logger   = LogManager.getLogger(GetSolutionAPIvsDBTest.class);

	HeaderConfigs header 		 = new HeaderConfigs();
	ExcelFileSetFromProp  xlProp = new ExcelFileSetFromProp();
	static PostgreSqlConnectionDb objSql = new PostgreSqlConnectionDb();
	
	@SuppressWarnings("rawtypes")
	List<List> row = new ArrayList<List>();

	String[] 	serialNumber 	= null ;
	String[] 	productNumber 	= null; 
	String 		startDate 		= "" ;
	String 		endDate 		= "" ;
	String 		date 			= "";
	String[] 	api_name 		= null;
	String[]	eventCode		= null;
	String 		detectionDate	= "";
	String 		event_type 		= "";

	List<String> lt = null;
	Map<Integer, List<String>> eventMap = new HashMap<Integer, List<String>>();

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
		
		String excelPath = "./Data/GetSolutionData.xlsx";
		String sheetName = "Sheet1";

		Excel_Utility excel = null;
		try {
			excel = new Excel_Utility(excelPath,sheetName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
	 * ********* Compare 'event_Code'  and 'update_TS' values from GetSolution Api response and Seals DB **********
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test	
	public void TC_compareEventCodeAndUpdateTSFromApiWithDB()
	{	
		HashSet<String> serialNoList = new HashSet<String>();
//		if(! hMapData.containsValue("GetSolution") ) {
//			throw new SkipException("Skipping 'TC_compareEventCodeAndUpdateTSFromApiWithDB' test Method because resource was not available.");	
//		}
//		test.assignCategory("GetSolution");
//		test.log(Status.INFO, MarkupHelper.createLabel(" **** Compare 'event_Code'  and 'update_TS'  from API Response with Seals DB ****",ExtentColor.GREEN));
//		if(serialNumber == null || eventCode == null || serialNumber.length == 0  )	
//		{
//			test.log(Status.PASS, MarkupHelper.createLabel( "************* Reading Parameters from Excel Sheet ************** ", ExtentColor.BLUE));

			for(int i = 0 ; i < row.size() ; i++) {
				List rowvalue 		= row.get(i);	
				String sn 			= rowvalue.get(0).toString();
				String pn 			= rowvalue.get(1).toString();
				String eventCode	= rowvalue.get(2).toString();
				String detectionDate= rowvalue.get(3).toString();
				String event_type 	= rowvalue.get(4).toString();

				test.log(Status.PASS ,MarkupHelper.createLabel( "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  eventCode = " + eventCode  + "  and  detectionDate = " + detectionDate, ExtentColor.PINK) );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , eventCode= " + eventCode  + " and detectionDate= " + detectionDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate ));		

				UtilityApiMethods.verifyResponseCode1(response,200,"GetSolution",sn, pn,eventCode, detectionDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				//APIVerificationGetSolutionPage.getEventCode_UpdateTsValuesFromAPI(response, "event_Code", "update_TS", sn, pn, eventCode, detectionDate,event_type);

				APIVerificationGetSolutionPage.compareEventCodeAndUpdateTsFromAPIandDB(response, "event_Code", "update_TS", sn, pn, eventCode, detectionDate, event_type, serialNoList);

				this.readParamsFromExcelForGetSolution(i, sn, pn, eventCode, detectionDate );
			}
//			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );
//
//		} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading Parameters from Parameterized job *** ",ExtentColor.CYAN));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//				
//				test.log(Status.PASS ,MarkupHelper.createLabel( "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//						+ " ,  eventCode = " + eventCode[i]  + "  and  detectionDate = " + detectionDate, ExtentColor.PINK) );
//
//				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION , 
//								serialNumber[i],productNumber[i], eventCode[i], detectionDate  ));
//
//				UtilityApiMethods.verifyResponseCode1(response, 200, "GetSolution" ,productNumber[i] ,serialNumber[i], eventCode[i], detectionDate  ) ;
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				//APIVerificationGetSolutionPage.getEventCode_UpdateTsValuesFromAPI(response, "event_Code", "update_TS", serialNumber[i], productNumber[i], eventCode[i], detectionDate);
//
//				APIVerificationGetSolutionPage.compareEventCodeAndUpdateTsFromAPIandDB(response, "event_Code", "update_TS", serialNumber[i], productNumber[i], eventCode[i], detectionDate, event_type, serialNoList);
//			}
//			logger.info("Parameterized List of User inputs data::>> " + eventMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + eventMap,ExtentColor.INDIGO) );
//		}	
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + serialNoList, ExtentColor.RED) );
	}

	/* 
	 * ********* Compare 'short_Description' value based on 'event_Code' and 'update_TS' from API with DB *******************
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test	
	public void TC_compareShortDescriptionValueFromApiWithDB()
	{	
		HashSet<String> serialNoList = new HashSet<String>();
//		if(! hMapData.containsValue("GetSolution") ) {
//			throw new SkipException("Skipping 'TC_compareShortDescriptionValueFromApiWithDB' test Method because resource was not available.");	
//		}
//
//		test.assignCategory("GetSolution");
//		test.log(Status.INFO, MarkupHelper.createLabel(" **** Compare 'short_Description' value based on 'event_Code' and 'update_TS' from API Response with Seals DB ****",ExtentColor.GREEN));
//		if(serialNumber == null || eventCode == null || serialNumber.length == 0  )	
//		{
//			test.log(Status.PASS, MarkupHelper.createLabel( "************* Reading Parameters from Excel Sheet ************** ", ExtentColor.BLUE));

			for(int i = 0 ; i < row.size() ; i++)	{
				List rowvalue 		= row.get(i);	
				String sn 			= rowvalue.get(0).toString();
				String pn 			= rowvalue.get(1).toString();
				String eventCode	= rowvalue.get(2).toString();
				String detectionDate= rowvalue.get(3).toString();
				String eventtype 	= rowvalue.get(4).toString();

				test.log(Status.PASS ,MarkupHelper.createLabel( "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  eventCode = " + eventCode  + "  and  detectionDate = " + detectionDate, ExtentColor.PINK) );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , eventCode= " + eventCode  + " and detectionDate= " + detectionDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate ));		

				UtilityApiMethods.verifyResponseCode1(response,200,"GetSolution",sn, pn,eventCode, detectionDate);
				UtilityApiMethods.Validate_ResponseTime(response);
				
				//APIVerificationGetSolutionPage.validateKeysFromGetSolutionAPI(response, "solutionJson" ,"event_Code", "update_TS","short_Description", sn, pn, eventCode, detectionDate, eventtype);	
				//objSql.getKeyValueFromSealsDB( pn, eventCode , eventtype, "short_Description" );
						
				//APIVerificationGetSolutionPage.compareKeyValueFromAPIandDB(response,"solutionJson" ,"event_Code", "update_TS",
				//		"short_Description", sn, pn, eventCode, detectionDate, eventtype, serialNoList);

				//objSql.getShort_descriptionFromDB( pn, eventCode , eventtype);
				APIVerificationGetSolutionPage.compareShortDescriptionFromAPIandDB(response, "event_Code", "update_TS",
						"short_Description", sn, pn, eventCode, detectionDate, eventtype, serialNoList);	
				
				
				//this.readParamsFromExcelForGetSolution(i, sn, pn, eventCode, detectionDate );
			}
//		//	logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );
//
//		} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "***** Reading Parameters from Parameterized job ***** ",ExtentColor.CYAN));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//				
//				test.log(Status.PASS ,MarkupHelper.createLabel( "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//						+ " ,  eventCode = " + eventCode[i]  + "  and  detectionDate = " + detectionDate, ExtentColor.PINK) );
//			
//				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION , 
//								serialNumber[i],productNumber[i], eventCode[i], detectionDate  ));
//
//				UtilityApiMethods.verifyResponseCode1(response, 200, "GetSolution" ,productNumber[i] ,serialNumber[i], eventCode[i], detectionDate  ) ;
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationGetSolutionPage.compareShortDescriptionFromAPIandDB(response,"event_Code", "update_TS",
//						"short_Description", serialNumber[i], productNumber[i], eventCode[i], detectionDate, event_type, serialNoList);
//			}
//			logger.info("Parameterized List of User inputs data::>> " + eventMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + eventMap,ExtentColor.INDIGO) );
//		}	
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + serialNoList, ExtentColor.RED) );
	}

	/* 
	 * ********* Compare 'severity' value based on 'event_Code' and 'update_TS' from API with DB *******************
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test	
	public void TC_compareSeverityValueFromApiWithDB()
	{	
		HashSet<String> serialNoList = new HashSet<String>();
//		if(! hMapData.containsValue("GetSolution")) {
//			throw new SkipException("Skipping 'TC_compareSeverityValueFromApiWithDB' test Method because resource was not available.");	
//		}
//		test.assignCategory("GetSolution");	
//		test.log(Status.INFO, MarkupHelper.createLabel(" **** Compare 'severity' value based on 'event_Code' and 'update_TS' from API Response with Seals DB ****",ExtentColor.GREEN));
//		
//		if(serialNumber == null || eventCode == null || serialNumber.length == 0  )	
//		{
//			test.log(Status.PASS, MarkupHelper.createLabel( "****************** Reading Parameters from Excel Sheet ****************** ", ExtentColor.BLUE));

			for(int i = 0 ; i < row.size() ; i++) {
				List rowvalue 		= row.get(i);	
				String sn 			= rowvalue.get(0).toString();
				String pn 			= rowvalue.get(1).toString();
				String eventCode	= rowvalue.get(2).toString();
				String detectionDate= rowvalue.get(3).toString();
				String event_type 	= rowvalue.get(4).toString();

				test.log(Status.PASS ,MarkupHelper.createLabel( "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  eventCode = " + eventCode  + "  and  detectionDate = " + detectionDate, ExtentColor.PINK) );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , eventCode= " + eventCode  + " and detectionDate= " + detectionDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate ));		

				UtilityApiMethods.verifyResponseCode1(response,200,"GetSolution",sn, pn,eventCode, detectionDate);
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationGetSolutionPage.validateKeysFromGetSolutionAPI(response, "solutionJson" ,"event_Code", "update_TS","severity", sn, pn, eventCode, detectionDate, event_type);

				APIVerificationGetSolutionPage.compareKeyValueFromAPIandDB(response,"solutionJson" ,"event_Code", 
						"update_TS", "severity", sn, pn, eventCode, detectionDate, event_type, serialNoList);

				this.readParamsFromExcelForGetSolution(i, sn, pn, eventCode, detectionDate );
			}
//			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );
//
//		} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading Parameters from Parameterized job *** ",ExtentColor.CYAN));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//
//				test.log(Status.PASS ,MarkupHelper.createLabel( "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//						+ " ,  eventCode = " + eventCode[i]  + "  and  detectionDate = " + detectionDate, ExtentColor.PINK) );
//
//				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION , 
//								serialNumber[i],productNumber[i], eventCode[i], detectionDate  ));
//
//				UtilityApiMethods.verifyResponseCode1(response, 200, "GetSolution" ,productNumber[i] ,serialNumber[i], eventCode[i], detectionDate  ) ;
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationGetSolutionPage.compareKeyValueFromAPIandDB(response,"solutionJson" ,"event_Code", "update_TS",
//						"severity", serialNumber[i], productNumber[i], eventCode[i], detectionDate, event_type, serialNoList);
//			}
//			logger.info("Parameterized List of User inputs data::>> " + eventMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + eventMap,ExtentColor.INDIGO) );
//		}	
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + serialNoList, ExtentColor.RED) );
	}



	/*
	 ******************************************** Private Methods *************************************************
	 */
	private void  readParameter_fromJenkins()
	{
		String sn 			 = System.getProperty("param1"); 	
		String pn 			 = System.getProperty("param2"); 	
		String sDate		 = System.getProperty("param3");	
		String eDate 		 = System.getProperty("param4"); 
		String apiname 		 = System.getProperty("param");
		String eventcode 	 = System.getProperty("param5");
		String detectiondate = System.getProperty("param6");
		String eventType 	 = System.getProperty("param7");

		logger.info("Serial No		:= " + sn);
		logger.info("Product No		:= " + pn);
		logger.info("Start Date		:= " + sDate);
		logger.info("End Date		:= " + eDate);
		logger.info("API NAME		:= " + apiname);
		logger.info("EVENT CODE		:= " + eventcode);
		logger.info("DETECTION DATE := " + detectiondate);
		logger.info("EVENT TYPE 	:= " + eventType);

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
			for (int i = 0; i < serialNumber.length; i++) {      
				logger.info("Product Numbers are==> " + productNumber[i] );
			}			
		}

		startDate = sDate;
		endDate   = eDate;

		if( apiname != null ) {
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
				//logger.info("Data Lists: " + lt );	
				eventMap.put(i+1,lt);			
			}
		}

		// needed for result from DB for getSolution API		
		if(!(eventcode.isEmpty())) {
			event_type = eventType;
		}

	} // End of private method readParameter_fromJenkins

	/*
	 * Reading parameters data from excel
	 */

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
