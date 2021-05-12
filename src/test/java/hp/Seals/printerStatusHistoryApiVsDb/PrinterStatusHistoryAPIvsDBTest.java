package hp.Seals.printerStatusHistoryApiVsDb;

import org.testng.annotations.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.*;
import org.testng.*;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.HeaderConfigs;
import apiConfig.APIPath;

import baseTest.BaseTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.*;



public class PrinterStatusHistoryAPIvsDBTest extends BaseTest 
{
	final static Logger  logger  = LogManager.getLogger(PrinterStatusHistoryAPIvsDBTest.class);

	HeaderConfigs         header = new HeaderConfigs();
	ExcelFileSetFromProp  xlProp = new ExcelFileSetFromProp();

	@SuppressWarnings("rawtypes")
	List<List> row = new ArrayList<List>();

	String[] 	 serialNumber = null ;
	String[] 	productNumber = null; 
	String          startDate = "" ;
	String            endDate = "" ;
	String               date = "";
	String[]         api_name = null;

	Map<String, String> hMapData = null;
	Map<Integer, List<String>> myMap = new HashMap<Integer, List<String>>();
	List<String> list = null;

	List<String> excelList = null;
	Map<Integer, List<String>> excelMap = new HashMap<Integer, List<String>>();


	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	@BeforeTest
	public void readExcel()
	{
		readParameter_fromJenkins();

		String excelPath = "./Data/PrinterStatusHistoryTestData.xlsx";
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

	/* 
	 * **** Compare number of 'serial_Number', 'start_TS' and 'status' value from PrinterStatusHistory API response and Seals DB *************
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test	
	public void TC_CompareNoOfStartTsStatusOfPrinterStatusHistoryAPIwithDB()
	{	
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetPrinterStatusHistory") ) {		
			throw new SkipException("Skipping 'TC_CompareNoOfStartTsStatusOfPrinterStatusHistoryAPIwithDB' test Method because resource was not available.");
		}

		test.assignCategory("GetPrinterStatusHistory");	
		test.log(Status.INFO, MarkupHelper.createLabel(" **** Compare number of 'serial_Number', 'start_TS' and 'status'  value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.CYAN));

			for(int i = 0; i < row.size(); i++ ) {
				List    rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 	      pn = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String   endDate = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath
						.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrinterStatusHistory",sn, pn,startDate,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationPrinterStatusPage.compareCountOfFieldsFromAPIandDB(response,"serial_Number","start_TS", "status", sn, pn,startDate,endDate, serialNoList);
				
				this.readParamsFromExcel(i, sn, pn, startDate, endDate);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

		} else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading Parameters from Parameterized job *** ",ExtentColor.CYAN));

			for(int i = 0 ; i < serialNumber.length ; i++) {

				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						serialNumber[i], productNumber[i] ,startDate ,endDate ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrinterStatusHistory",serialNumber[i], productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationPrinterStatusPage.compareCountOfFieldsFromAPIandDB(response,"serial_Number", "start_TS", "status", serialNumber[i], productNumber[i] , startDate, endDate, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );
		}

		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + serialNoList , ExtentColor.RED) );
	}

	/* 
	 * *** Compare 'serial_Number', 'start_TS' and 'status' value from PrinterStatusHistory Api response and Seals DB **********
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test	
	public void TC_CompareSnStartTsStatusFromPrinterStatusHistoryApiWithDB()
	{	
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetPrinterStatusHistory") ) {		
			throw new SkipException("Skipping 'TC_CompareSnStartTsStatusFromPrinterStatusHistoryApiWithDB' test Method because resource was not available.");
		}

		test.assignCategory("GetPrinterStatusHistory");	
		test.log(Status.INFO, MarkupHelper.createLabel(" **** Compare 'serial_Number', 'start_TS' and 'status' value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.CYAN));

			for(int i = 0; i < row.size(); i++ ) {
				List    rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 	      pn = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String   endDate = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						sn, pn, startDate, endDate ));

				UtilityApiMethods.responseCodeValidation(response, 200, "GetPrinterStatusHistory", sn, pn, startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationPrinterStatusPage.getSN_Start_TS_StatusValuesFromAPI( response,"serial_Number", "start_TS", "status", sn, pn,startDate,endDate);

				APIVerificationPrinterStatusPage.comparePrinterStatusHistoryFieldsFromApiAndDB(response,"serial_Number","start_TS", "status", sn, pn,startDate,endDate, serialNoList);
				this.readParamsFromExcel(i, sn, pn, startDate, endDate);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

		} else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading Parameters from Parameterized job *** ",ExtentColor.CYAN));

			for(int i = 0 ; i < serialNumber.length ; i++) {

				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						serialNumber[i], productNumber[i] ,startDate ,endDate ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrinterStatusHistory",serialNumber[i], productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationPrinterStatusPage.comparePrinterStatusHistoryFieldsFromApiAndDB(response,"serial_Number", "start_TS", "status", serialNumber[i], productNumber[i] ,startDate,endDate, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );

		}

		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + serialNoList , ExtentColor.RED) );

	}

	/* 
	 * *** Compare 'end_ts' value based on 'serial_Number', 'start_TS', 'status'  value from PrinterStatusHistory Api response and Seals DB **********
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test	
	public void TC_CompareEndTsFromPrinterStatusHistoryApiWithDB()
	{	
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetPrinterStatusHistory") ) {		
			throw new SkipException("Skipping 'TC_CompareEndTsFromPrinterStatusHistoryApiWithDB' test Method because resource was not available.");
		}

		test.assignCategory("GetPrinterStatusHistory");	
		test.log(Status.INFO, MarkupHelper.createLabel(" **** Compare 'end_ts' value based on 'serial_Number', 'start_TS', 'status' value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.CYAN));

			for(int i = 0; i < row.size(); i++ ) {
				List    rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 	      pn = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String   endDate = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						sn, pn, startDate, endDate ));

				UtilityApiMethods.responseCodeValidation(response, 200, "GetPrinterStatusHistory", sn, pn, startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationPrinterStatusPage.getSN_Start_TS_StatusValuesFromAPI( response,"serial_Number", "start_TS", "status", sn, pn,startDate,endDate);

				APIVerificationPrinterStatusPage.compareEndTsFromPrinterStatusHistoryApiAndDB(response,"serial_Number","start_TS", "status","end_TS", sn, pn,startDate,endDate, serialNoList);
				this.readParamsFromExcel(i, sn, pn, startDate, endDate);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

		} else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading Parameters from Parameterized job *** ",ExtentColor.CYAN));

			for(int i = 0 ; i < serialNumber.length ; i++) {

				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						serialNumber[i], productNumber[i] ,startDate ,endDate ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrinterStatusHistory",serialNumber[i], productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationPrinterStatusPage.compareEndTsFromPrinterStatusHistoryApiAndDB(response,"serial_Number", "start_TS", "status", "end_TS" , serialNumber[i], productNumber[i] ,startDate,endDate, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );

		}

		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + serialNoList , ExtentColor.RED) );

	}

	/* 
	 * *** Compare 'sub_Status' value based on 'serial_Number', 'start_TS', 'status'  value from PrinterStatusHistory Api response and Seals DB **********
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test	
	public void TC_CompareSubStatusFromPrinterStatusHistoryApiWithDB()
	{	
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetPrinterStatusHistory") ){		
			throw new SkipException("Skipping 'TC_CompareSubStatusFromPrinterStatusHistoryApiWithDB' test Method because resource was not available.");
		}

		test.assignCategory("GetPrinterStatusHistory");	
		test.log(Status.INFO, MarkupHelper.createLabel(" **** Compare 'sub_Status' value based on 'serial_Number', 'start_TS', 'status'  value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.CYAN));

			for(int i = 0; i < row.size(); i++ ) {
				List    rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 	      pn = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String   endDate = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						sn, pn, startDate, endDate ));

				UtilityApiMethods.responseCodeValidation(response, 200, "GetPrinterStatusHistory", sn, pn, startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationPrinterStatusPage.compareSubStatusFromPrinterStatusHistoryApiAndDB(response,"serial_Number","start_TS", "status","sub_Status", sn, pn, startDate, endDate, serialNoList);

				this.readParamsFromExcel(i, sn, pn, startDate, endDate);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

		} else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading Parameters from Parameterized job *** ",ExtentColor.CYAN));

			for(int i = 0 ; i < serialNumber.length ; i++) {

				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						serialNumber[i], productNumber[i] ,startDate ,endDate ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrinterStatusHistory",serialNumber[i], productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationPrinterStatusPage.compareSubStatusFromPrinterStatusHistoryApiAndDB(response,"serial_Number", "start_TS","status", "sub_Status", serialNumber[i], productNumber[i] ,startDate,endDate, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );

		}

		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + serialNoList , ExtentColor.RED) );

	}

	/* 
	 * *** Compare 'channel' value based on 'serial_Number', 'start_TS', 'status'  value from PrinterStatusHistory Api response and Seals DB **********
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test	
	public void TC_CompareChannelFromPrinterStatusHistoryApiWithDB()
	{	
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetPrinterStatusHistory") ) {		
			throw new SkipException("Skipping 'TC_CompareChannelFromPrinterStatusHistoryApiWithDB' test Method because resource was not available.");
		}

		test.assignCategory("GetPrinterStatusHistory");	
		test.log(Status.INFO, MarkupHelper.createLabel(" **** Compare 'channel' value based on 'serial_Number', 'start_TS', 'status' value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.CYAN));

			for(int i = 0; i < row.size(); i++ ) {
				List    rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 	      pn = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String   endDate = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						sn, pn, startDate, endDate ));

				UtilityApiMethods.responseCodeValidation(response, 200, "GetPrinterStatusHistory", sn, pn, startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);			
				//APIVerificationPrinterStatusPage.getChannelFromAPI(response,"serial_Number","start_TS", "status","channel", sn, pn, startDate, endDate);

				APIVerificationPrinterStatusPage.compareChannelFromAPIandDB(response,"serial_Number","start_TS", "status","channel", sn, pn, startDate, endDate, serialNoList);
				this.readParamsFromExcel(i, sn, pn, startDate, endDate);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

		} else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading Parameters from Parameterized job *** ",ExtentColor.CYAN));

			for(int i = 0 ; i < serialNumber.length ; i++) {

				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						serialNumber[i], productNumber[i] ,startDate ,endDate ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrinterStatusHistory",serialNumber[i], productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationPrinterStatusPage.compareChannelFromAPIandDB(response,"serial_Number","start_TS", "status","channel", serialNumber[i], productNumber[i] ,startDate,endDate, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );

		}

		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + serialNoList , ExtentColor.RED) );

	}



	/* 
	 * *********Compare Status and subStatus value from PrinterStatusHistory reponse and Seals DB **********
	 * 
	 */
	//@Test
	public void TC_PrinterStatusHistoryStatusCompareWithDB()
	{	
		List listOfSerialNo = new ArrayList();
		if(! hMapData.containsValue("GetPrinterStatusHistory") ){		
			throw new SkipException("Skipping 'TC_PrinterStatusHistoryStatusCompareWithDB' test Method because resource was not available.");
		}
		//if( hMapData.containsValue("GetPrinterStatusHistory") )	{
		test.assignCategory("GetPrinterStatusHistory");	
		test.log(Status.INFO, MarkupHelper.createLabel(" ***Compare Status and sub_Status value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.CYAN));

			//Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.GET_PrinterStatusHistory);
			for(int i=0;i<row.size();i++) {
				List    rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 	      pn = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String   endDate = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(
						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn,startDate,endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrinterStatusHistory",sn, pn,startDate,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationPrinterStatusPage.compareStatusValuesFromPrinterStatusHistoryApiWithDB(response,"status", "status", "sub_Status", sn, pn,startDate,endDate, listOfSerialNo);

				this.readParamsFromExcel(i, sn, pn, startDate, endDate);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

		} else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading Parameters from Parameterized job *** ",ExtentColor.CYAN));

			for(int i = 0 ; i < serialNumber.length ; i++) {

				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_PrinterStatusHistory.toString(),
						serialNumber[i], productNumber[i] ,startDate ,endDate ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrinterStatusHistory",serialNumber[i], productNumber[i] ,startDate ,endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationPrinterStatusPage.compareStatusValuesFromPrinterStatusHistoryApiWithDB(response,"status", "status", "sub_Status", serialNumber[i], productNumber[i] ,startDate,endDate, listOfSerialNo);
			}
			logger.info("Parameterized List of User inputs data::>> " + myMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );

		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel(" List of Serial no's are failed----> " + listOfSerialNo, ExtentColor.RED) );

	}

	/*
	 *   ************************************ Private Methods ******************************************
	 */
	private void  readParameter_fromJenkins()
	{
		String 		      sn = System.getProperty("param1"); 	
		String 		      pn = System.getProperty("param2"); 	
		String         sDate = System.getProperty("param3");	
		String         eDate = System.getProperty("param4"); 
		String       apiname = System.getProperty("param");
		String     eventcode = System.getProperty("param5");
		String detectiondate = System.getProperty("param6");

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
