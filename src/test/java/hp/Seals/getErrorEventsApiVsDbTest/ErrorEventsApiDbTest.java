package hp.Seals.getErrorEventsApiVsDbTest;

import java.io.*;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.SkipException;
import org.testng.annotations.*;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.HeaderConfigs;
import apiConfig.APIPath;
import baseTest.BaseTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.ExcelFileSetFromProp;
import utils.Excel_Utility;
import utils.UtilityApiMethods;


public class ErrorEventsApiDbTest extends BaseTest {

	final static Logger logger = LogManager.getLogger(ErrorEventsApiDbTest.class);

	HeaderConfigs header = new HeaderConfigs();
	ExcelFileSetFromProp  xlProp = new ExcelFileSetFromProp();

	List<List> row = new ArrayList<List>();

	String[] 	serialNumber 	= null ;
	String[] 	productNumber 	= null; 
	String 		startDate 		= "" ;
	String 		endDate 		= "" ;
	String 		date 			= "";
	String[] 	api_name		= null;
	String[] 	eventCode 		= null;
	String 		detectionDate 	= "";

	Map<String, String> hMapData = null;
	Map<Integer, List<String>> myMap = new HashMap<Integer, List<String>>();
	List<String> list = null;

	List<String> excelList = null;
	Map<Integer, List<String>> excelMap = new HashMap<Integer, List<String>>();

	@BeforeTest
	public void readExcel()
	{
		//readParameter_fromJenkins();

		String excelPath = "./Data/getErrorEventsTestData.xlsx";
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
			for( int j = 0; j < 8 ; j++ ) {
				list.add(excel.getCellData(i, j));		
			}
			row.add(list);
		}
	}

	@AfterTest
	public void endClass() {
		logger.info("<=== All Test Methods are executed ===>");
		//test.log(Status.INFO,"<=== All Test Methods are executed ===>");
	}

	/* 
	 * ***********Compare 'error_Code' count from GetErrorEvents Api response with Seals DB *****************
	 * 
	 */
	@SuppressWarnings("rawtypes")
	//@Test
	public void TC_compareErrorCodeCountFromAPIvsDB()
	{	
		List listOfSerialNo = new ArrayList();
		if(! hMapData.containsValue("GetErrorEvents") )	{	
			throw new SkipException("Skipping 'TC_compareErrorCodeCountFromAPIvsDB' test Method because resource was not available.");
		}
		test.assignCategory("GetErrorEvents");
		test.log(Status.INFO, MarkupHelper.createLabel("***Compare 'error_Code' object count from GetErrorEvents Api response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0 )  {
			test.log(Status.PASS,MarkupHelper.createLabel(" *** Reading Parameters from Excel Sheet *** ", ExtentColor.PINK ));

			for(int i = 0; i < row.size() ; i++)
			{			
				List rowvalue    = row.get(i);
				String sn        = rowvalue.get(0).toString();
				String pn        = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String endDate   = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),sn,pn,startDate, endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents", sn, pn, startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);		

				APIVerificationErrorEventsPage.validateErrorCountObjectWithDB(response,"alert" ,"error_Code",sn, pn, startDate, endDate, listOfSerialNo);

				this.readParamsFromExcel(i, sn, pn, startDate, endDate);	

			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );
		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));

			for(int i = 0 ; i < serialNumber.length; i++) {

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
								+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
								+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),serialNumber[i] ,productNumber[i] ,startDate ,endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",serialNumber[i] ,productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationErrorEventsPage.verifyObjectsCountFromResponse(response,"alert" ,"error_Code");

				APIVerificationErrorEventsPage.validateErrorCountObjectWithDB(response,"alert" ,"error_Code",serialNumber[i] ,productNumber[i], startDate , endDate, listOfSerialNo);
			}
			logger.info(".....................Parameter Passing successful ..................." );
			test.log(Status.PASS,MarkupHelper.createLabel(" ***** Parameter Passing successful ******** ", ExtentColor.PINK));

			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );
		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
	}



	//****************************** Compare error_Code value from API and DB ***********************************
	@SuppressWarnings("rawtypes")
	//@Test
	public void TC_compareErrorCodeValuesFromAPIvsDB()
	{	
		List listOfSerialNo = new ArrayList();
		if(! hMapData.containsValue("GetErrorEvents") )	{
			throw new SkipException("Skipping 'TC_compareErrorCodeValuesFromAPIvsDB' test Method because resource was not available.");
		}
		//if( hMapData.containsValue("GetErrorEvents") ) {	
		test.assignCategory("GetErrorEvents");
		test.log(Status.INFO, MarkupHelper.createLabel("***Compare 'error_Code' value from GetErrorEvents Api response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0 )  {
			test.log(Status.PASS,MarkupHelper.createLabel(" *** Reading Parameters from Excel Sheet *** ", ExtentColor.PINK ));

			for(int i = 0; i < row.size() ; i++) {			
				List rowvalue    = row.get(i);
				String sn        = rowvalue.get(0).toString();
				String pn        = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String endDate   = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),sn,pn,startDate, endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",sn,pn,startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);		

				APIVerificationErrorEventsPage.validateErrorCodeWithDB(response,"alert","error_Code",sn,pn,startDate, endDate, listOfSerialNo);

				this.readParamsFromExcel(i, sn, pn, startDate, endDate);	
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO ));

		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));

			for(int i = 0 ; i < serialNumber.length; i++) {

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
								+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
								+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),serialNumber[i] ,productNumber[i] ,startDate ,endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",serialNumber[i] ,productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationErrorEventsPage.validateErrorCodeWithDB(response,"alert","error_Code",serialNumber[i] ,productNumber[i] ,startDate ,endDate, listOfSerialNo);
			}
			logger.info(".....................Parameter Passing successful ..................." ); 
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));

			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap, ExtentColor.INDIGO) );
		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
	}


	/* 
	 * ***********Compare 'severity' values from GetErrorEvents Api response with Seals DB *****************
	 * 
	 */
	//@Test
	public void TC_compareSeverityValuesFromAPIvsDB()
	{	
		List<String> listOfSerialNo = new ArrayList<>();
		if(! hMapData.containsValue("GetErrorEvents") )	{
			throw new SkipException("Skipping 'TC_compareSeverityValuesFromAPIvsDB' test Method because resource was not available.");
		}
		//if( hMapData.containsValue("GetErrorEvents") ) {	
		test.assignCategory("GetErrorEvents");
		test.log(Status.INFO, MarkupHelper.createLabel("***Compare 'severity' value from GetErrorEvents Api response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0 )  {
			test.log(Status.PASS,MarkupHelper.createLabel(" *** Reading Parameters from Excel Sheet *** ", ExtentColor.PINK ));

			for(int i = 0; i < row.size() ; i++) {			
				List rowvalue    = row.get(i);
				String sn        = rowvalue.get(0).toString();
				String pn        = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String endDate   = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),sn,pn,startDate, endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",sn,pn,startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);			
				//APIVerificationErrorEventsPage.verifyGetErrorEventsKeyValue(response,"alert" ,"severity",sn,pn,startDate, endDate);  //, listOfSerialNo);

				APIVerificationErrorEventsPage.compareSeverityValuesFromApiWithDB(response, "alert" ,"severity",sn,pn,startDate, endDate, listOfSerialNo);	

				this.readParamsFromExcel(i, sn,pn,startDate, endDate);	
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO ));

		}
		else {

			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));

			for(int i = 0 ; i < serialNumber.length; i++) {

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
								+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
								+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),serialNumber[i] ,productNumber[i] ,startDate ,endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",serialNumber[i] ,productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationErrorEventsPage.compareSeverityValuesFromApiWithDB(response, "alert" ,"severity",serialNumber[i] ,productNumber[i],startDate, endDate, listOfSerialNo);
			}
			logger.info(".....................Parameter Passing successful ..................." ); 
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));

			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap, ExtentColor.INDIGO) );
		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
	}

	/* 
	 * ***********Compare 'event_Occurred_TS' values from GetErrorEvents Api response with Seals DB *****************
	 * 
	 */
	//@Test
	public void TC_compareEventTSvaluesFromAPIvsDB()
	{	
		List<String> listOfSerialNo = new ArrayList<>();
		if(! hMapData.containsValue("GetErrorEvents") )	{
			throw new SkipException("Skipping 'TC_compareEventTSvaluesFromAPIvsDB' test Method because resource was not available.");
		}
		//if( hMapData.containsValue("GetErrorEvents") ) {	
		test.assignCategory("GetErrorEvents");
		test.log(Status.INFO, MarkupHelper.createLabel("***Compare 'event_Occurred_TS' value from GetErrorEvents Api response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0 )  {
			test.log(Status.PASS,MarkupHelper.createLabel(" *** Reading Parameters from Excel Sheet *** ", ExtentColor.PINK ));

			for(int i = 0; i < row.size() ; i++) {			
				List rowvalue    = row.get(i);
				String sn        = rowvalue.get(0).toString();
				String pn        = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String endDate   = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),sn,pn,startDate, endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",sn,pn,startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);			
				//APIVerificationErrorEventsPage.verifyGetErrorEventsKeyValue(response,"alert" ,"event_Occurred_TS",sn,pn,startDate, endDate);  //, listOfSerialNo);

				APIVerificationErrorEventsPage.compareEventOccuredTSvaluesWithDB(response, "alert" ,"event_Occurred_TS",sn,pn,startDate, endDate, listOfSerialNo);	

				this.readParamsFromExcel(i, sn,pn,startDate, endDate);	
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO ));
		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));

			for(int i = 0 ; i < serialNumber.length; i++) {

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
								+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
								+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),serialNumber[i] ,productNumber[i] ,startDate ,endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",serialNumber[i] ,productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationErrorEventsPage.compareEventOccuredTSvaluesWithDB(response, "alert" ,"event_Occurred_TS", serialNumber[i] ,productNumber[i],startDate, endDate, listOfSerialNo);
			}
			logger.info(".....................Parameter Passing successful ..................." ); 
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));

			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap, ExtentColor.INDIGO) );
		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
	}

	/* 
	 * ***********Compare 'short_Description' values from GetErrorEvents Api response with Seals DB *****************
	 * 
	 */
	@Test
	public void TC_compareShortDescriptionValuesFromAPIvsDB()
	{	
		List<String> listOfSerialNo = new ArrayList<>();

//		if(! hMapData.containsValue("GetErrorEvents") )	{
//			throw new SkipException("Skipping 'TC_compareShortDescriptionValuesFromAPIvsDB' test Method because resource was not available.");
//		}
//		//if( hMapData.containsValue("GetErrorEvents") ) {	
//		test.assignCategory("GetErrorEvents");
//		test.log(Status.INFO, MarkupHelper.createLabel("***Compare 'short_Description' values from GetErrorEvents Api Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0 )  {
//			test.log(Status.PASS,MarkupHelper.createLabel(" *** Reading Parameters from Excel Sheet *** ", ExtentColor.PINK ));

			for(int i = 0; i < row.size() ; i++) {			
				List rowvalue    = row.get(i);
				String sn        = rowvalue.get(0).toString();
				String pn        = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String endDate   = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),sn,pn,startDate, endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",sn,pn,startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationErrorEventsPage.compareShortDescriptionFromAPIvsDB(response, sn, pn, startDate, endDate, listOfSerialNo);
				//APIVerificationErrorEventsPage.getShortDescriptionValueFromAPI(response, sn, pn, startDate, endDate);
				
				APIVerificationErrorEventsPage.compareShortDescriptnFromAPIvsDB(response, sn, pn, startDate, endDate, listOfSerialNo);	
				
	}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
	}


	/* 
	 * ***********Compare 'long_Description' values from GetErrorEvents Api response with Seals DB *****************
	 * 
	 */
	@Test
	public void TC_compareLongDescriptionValuesFromAPIvsDB()
	{	
		List<String> listOfSerialNo = new ArrayList<>();

//		if(! hMapData.containsValue("GetErrorEvents") )	{
//			throw new SkipException("Skipping 'TC_compareLongDescriptionValuesFromAPIvsDB' test Method because resource was not available.");
//		}
//		//if( hMapData.containsValue("GetErrorEvents") ) {	
//		test.assignCategory("GetErrorEvents");
//		test.log(Status.INFO, MarkupHelper.createLabel("***Compare 'long_Description' values from GetErrorEvents Api Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0 )  {
//			test.log(Status.PASS,MarkupHelper.createLabel(" *** Reading Parameters from Excel Sheet *** ", ExtentColor.PINK ));

			for(int i = 0; i < row.size() ; i++) {			
				List rowvalue    = row.get(i);
				String sn        = rowvalue.get(0).toString();
				String pn        = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String endDate   = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),sn,pn,startDate, endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",sn,pn,startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);			

				APIVerificationErrorEventsPage.compareLongDescriptionValueFromAPIvsDB(response, sn, pn, startDate, endDate, listOfSerialNo);	

//				this.readParamsFromExcel(i, sn, pn, startDate, endDate);
//			}
//			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO ));
//		}
//		else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));
//
//			for(int i = 0 ; i < serialNumber.length; i++) {
//
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//								+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
//								+ " , StartDate= " + startDate  + " and EndDate= " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),serialNumber[i] ,productNumber[i] ,startDate ,endDate));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetErrorEvents",serialNumber[i] ,productNumber[i] ,startDate ,endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationErrorEventsPage.compareLongDescriptionValuesFromAPIvsDB(response,  serialNumber[i] ,productNumber[i],startDate, endDate, listOfSerialNo);
//			
//			}
//			logger.info(".....................Parameter Passing successful ..................." ); 
//			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));

//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap, ExtentColor.INDIGO) );
		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
	}









	/*
	 *  ******************************************* Private Methods ******************************************
	 */
	private void  readParameter_fromJenkins()
	{
		String sn 		= System.getProperty("param1"); 	
		String pn		= System.getProperty("param2"); 	
		String sDate 	= System.getProperty("param3");	
		String eDate 	= System.getProperty("param4"); 
		String apiname 	= System.getProperty("param");

		String eventcode = System.getProperty("param5");
		String detectiondate = System.getProperty("param6");

		logger.info("Serial No	:= " + sn);
		logger.info("Product No	:= " + pn);
		logger.info("Start Date	:= " + sDate);
		logger.info("End Date	:= " + eDate);
		logger.info("API NAME	:= " + apiname);
		logger.info("EVENT CODE	:= " + eventcode);
		logger.info("DETECTION DATE:= " + detectiondate);

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


	//End Private Method

}
