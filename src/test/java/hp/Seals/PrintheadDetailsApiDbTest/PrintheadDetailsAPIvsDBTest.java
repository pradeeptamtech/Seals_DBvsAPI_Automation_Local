package hp.Seals.PrintheadDetailsApiDbTest;

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
import baseTest.BaseTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import utils.*;

public class PrintheadDetailsAPIvsDBTest extends BaseTest 
{
	final static Logger   logger = LogManager.getLogger(PrintheadDetailsAPIvsDBTest.class);
	HeaderConfigs 		  header = new HeaderConfigs();
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

		String excelPath = "./Data/PrintheadDetailsDbData.xlsx";

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
	 * ********************Compare warranty 'status' getPrintheadDetails API with Db test Information ******************************
	 * 
	 */

	@Test
	@SuppressWarnings("rawtypes")
	public void TC_statusValueFromPrintheadDetailsAPIvsDB()
	{	
		List listOfSerialNo = new ArrayList();
//		if(! hMapData.containsValue("GetPrintheadDetails") )	{
//			throw new SkipException("Skipping 'TC_StartTsValueFromPrintheadDetailsAPIvsDB' test Method because resource was not available.");
//		}
//		//if(hMapData.containsValue("GetPrintheadDetails") )	{
//
//		test.assignCategory("GetPrintheadDetails");	
//		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare Status value from API Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0  )  {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++)	{
				List 		 rowvalue = row.get(i);
				String 			   sn = rowvalue.get(0).toString();
				String 			   pn = rowvalue.get(1).toString();
				String startTimestamp = rowvalue.get(2).toString();
				String   endTimestamp = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  startTimestamp = " + startTimestamp  + "  and  endTimestamp = " + endTimestamp );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , startTimestamp= " + startTimestamp  + " and endTimestamp= " + endTimestamp );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
								sn, pn, startTimestamp, endTimestamp ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",sn,pn,startTimestamp, endTimestamp);
				UtilityApiMethods.Validate_ResponseTime(response);

				//APIVerificationApiDbComparePage.validateStatusFromPrintHeadDetailsAPI(response ,serial_no, product_no, startDate, endDate );	// valid
				APIVerificationWithDbComparePage.CompareStatusFromPrintHeadDetailsAPIvsDB(response, sn, pn, startTimestamp, endTimestamp , listOfSerialNo );

				this.readParamsFromExcel(i, sn, pn, startTimestamp, endTimestamp);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );
//		}
//		else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//								+ " ,  startTimestamp = " + startDate  + "  and  endTimestamp = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
//								+ " , startTimestamp= " + startDate  + " and endTimestamp= " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
//								serialNumber[i], productNumber[i] ,startDate ,endDate ));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",serialNumber[i], productNumber[i] ,startDate ,endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				//APIVerificationApiDbComparePage.validateStatusFromPrintHeadDetailsAPI(response ,serial_no, product_no, startDate, endDate );	// valid
//
//				APIVerificationWithDbComparePage.CompareStatusFromPrintHeadDetailsAPIvsDB(response, serialNumber[i], productNumber[i] ,startDate ,endDate , listOfSerialNo );
//			}
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + myMap,ExtentColor.INDIGO) );
//
//			logger.info(".....................Parameter Passing successful ..................." ); 
//			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
//		}	
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>        " + listOfSerialNo, ExtentColor.RED) );
		//test.log(Status.PASS,"" + listOfSerialNo );	
	}

	/* 
	 * ********************Compare warranty 'start_Timestamp' getPrintheadDetails API with Db test Information ******************************
	 * 
	 */

	@SuppressWarnings("rawtypes")
	@Test
	public void TC_StartTsValueFromPrintheadDetailsAPIvsDB()
	{	
		List listOfSerialNo = new ArrayList();
//		if(! hMapData.containsValue("GetPrintheadDetails") )	{
//			throw new SkipException("Skipping 'TC_StartTsValueFromPrintheadDetailsAPIvsDB' test Method because resource was not available.");
//		}
//		//if(hMapData.containsValue("GetPrintheadDetails") )	{		
//		test.assignCategory("GetPrintheadDetails");	
//		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'start_Timestamp' value from API Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0  )  {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++)	{
				List 		 rowvalue = row.get(i);
				String 			   sn = rowvalue.get(0).toString();
				String 			   pn = rowvalue.get(1).toString();
				String startTimestamp = rowvalue.get(2).toString();
				String   endTimestamp = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  startTimestamp = " + startTimestamp  + "  and  endTimestamp = " + endTimestamp );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , startTimestamp= " + startTimestamp  + " and endTimestamp= " + endTimestamp );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
								sn, pn, startTimestamp, endTimestamp ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",sn,pn,startTimestamp, endTimestamp );
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationWithDbComparePage.CompareStartTSfromPrintHeadDetailsAPIvsDB(response, sn,pn,startTimestamp, endTimestamp , listOfSerialNo );

				this.readParamsFromExcel(i, sn,pn,startTimestamp, endTimestamp );
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

//		} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//								+ " ,  startTimestamp = " + startDate  + "  and  endTimestamp = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN = " + serialNumber[i] + ", PN = " + productNumber[i]
//								+ " , startTimestamp = " + startDate  + " and endTimestamp = " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
//								serialNumber[i], productNumber[i] ,startDate ,endDate ));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",serialNumber[i], productNumber[i] ,startDate ,endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationWithDbComparePage.CompareStartTSfromPrintHeadDetailsAPIvsDB(response, serialNumber[i], productNumber[i] ,startDate ,endDate , listOfSerialNo );
//			}
//			logger.info(".....................Parameter Passing successful ..................." ); 
//			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
//
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + myMap,ExtentColor.INDIGO) );
//		}

		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>        " + listOfSerialNo, ExtentColor.RED) );
		//test.log(Status.PASS,"" + listOfSerialNo );

	}

	/* 
	 * ********************Compare warranty 'end_Timestamp' getPrintheadDetails API with Db test Information ******************************
	 * 
	 */

	@Test
	@SuppressWarnings("rawtypes")
	public void TC_EndTsValueFromPrintheadDetailsAPIvsDB()
	{	
		List listOfSerialNo = new ArrayList();
//		if(! hMapData.containsValue("GetPrintheadDetails") )	{
//			throw new SkipException("Skipping 'TC_EndTsValueFromPrintheadDetailsAPIvsDB' test Method because resource was not available.");
//		}
//		//if(hMapData.containsValue("GetPrintheadDetails") ) {	
//
//		test.assignCategory("GetPrintheadDetails");	
//		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'end_Timestamp' value from API Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0  )  {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++)	{
				List 		 rowvalue = row.get(i);
				String 			   sn = rowvalue.get(0).toString();
				String 			   pn = rowvalue.get(1).toString();
				String startTimestamp = rowvalue.get(2).toString();
				String   endTimestamp = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  startTimestamp = " + startTimestamp  + "  and  endTimestamp = " + endTimestamp );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , startTimestamp= " + startTimestamp  + " and endTimestamp= " + endTimestamp );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
								sn, pn, startTimestamp, endTimestamp ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",sn,pn,startTimestamp, endTimestamp );
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationWithDbComparePage.CompareEndTSfromPrintHeadDetailsAPIvsDB(response, sn,pn,startTimestamp, endTimestamp , listOfSerialNo );

				this.readParamsFromExcel(i, sn,pn,startTimestamp, endTimestamp );
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

//		}
//		else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//								+ " ,  startTimestamp = " + startDate  + "  and  endTimestamp = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN = " + serialNumber[i] + ", PN= " + productNumber[i]
//								+ " , startTimestamp = " + startDate  + " and endTimestamp = " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
//								serialNumber[i], productNumber[i] ,startDate ,endDate ));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",serialNumber[i], productNumber[i] ,startDate ,endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationWithDbComparePage.CompareEndTSfromPrintHeadDetailsAPIvsDB(response, serialNumber[i], productNumber[i] ,startDate ,endDate , listOfSerialNo );
//			}
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + myMap,ExtentColor.INDIGO) );
//			logger.info(".....................Parameter Passing successful ..................." ); 
//			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
//		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>>        " + listOfSerialNo, ExtentColor.RED) );
		//test.log(Status.PASS,"" + listOfSerialNo );

	}


	/* 
	 * Compare 'printhead_serial_number' values for 'printhead_event' from getPrintheadDetails API with Db test ********
	 * 
	 */

	@Test
	public void TC_PhSrNoValuesFromPrintheadDetailsAPIvsDB() 
	{	
		List<String> listOfSerialNo = new ArrayList<String>();
//		if(! hMapData.containsValue("GetPrintheadDetails") )	{
//			throw new SkipException("Skipping 'TC_PhSrNoValuesFromPrintheadDetailsAPIvsDB' test Method because resource was not available.");
//		}
//		//if(hMapData.containsValue("GetPrintheadDetails") ) {	
//
//		test.assignCategory("GetPrintheadDetails");	
//		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'printhead_serial_number' values from API Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0  )  {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++)	{
				List 		 rowvalue = row.get(i);
				String 			   sn = rowvalue.get(0).toString();
				String 			   pn = rowvalue.get(1).toString();
				String startTimestamp = rowvalue.get(2).toString();
				String   endTimestamp = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  startTimestamp = " + startTimestamp  + "  and  endTimestamp = " + endTimestamp );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn
						+ " , startTimestamp= " + startTimestamp  + " and endTimestamp= " + endTimestamp );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
								sn, pn, startTimestamp, endTimestamp ));

				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",sn,pn,startTimestamp, endTimestamp);
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationWithDbComparePage.comparePhSrNoValuesOfPrintheadEvent( response,"printhead" ,"printhead_serial_number", sn,pn,startTimestamp, endTimestamp, listOfSerialNo);

				this.readParamsFromExcel(i, sn,pn,startTimestamp, endTimestamp );
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

//		} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//								+ " ,  startTimestamp = " + startDate  + "  and  endTimestamp = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
//								+ " , startTimestamp= " + startDate  + " and endTimestamp= " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
//								serialNumber[i], productNumber[i] ,startDate ,endDate ));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",serialNumber[i], productNumber[i] ,startDate ,endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationWithDbComparePage.comparePhSrNoValuesOfPrintheadEvent( response,"printhead" ,"printhead_serial_number", serialNumber[i], productNumber[i] ,startDate ,endDate, listOfSerialNo);
//			}
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + myMap,ExtentColor.INDIGO) );
//			logger.info(".....................Parameter Passing successful ..................." ); 
//			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
//		}

		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>>        " + listOfSerialNo, ExtentColor.RED) );
	}

	/* 
	 ***** Compare 'ink_Used' value based on 'printhead_serial_number'  from getPrintheadDetails API with Db test *****
	 * 
	 */

	@Test
	@SuppressWarnings("rawtypes")
	public void TC_InkUsedValuesFromPrintheadDetailsAPIvsDB() 
	{	
		HashSet<String> serialNoList = new HashSet<String>();
//		if(! hMapData.containsValue("GetPrintheadDetails") )	{
//			throw new SkipException("Skipping 'TC_InkUsedValuesFromPrintheadDetailsAPIvsDB' test Method because resource was not available.");
//		}
//		test.assignCategory("GetPrintheadDetails");	
//		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'ink_Used' value from API Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0  )  {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++)	{
				List 		 rowvalue = row.get(i);
				String 			   sn = rowvalue.get(0).toString();
				String 			   pn = rowvalue.get(1).toString();
				String startTimestamp = rowvalue.get(2).toString();
				String   endTimestamp = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  startTimestamp = " + startTimestamp  + "  and  endTimestamp = " + endTimestamp );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN = " + sn + ", PN = " + pn
						+ " , startTimestamp= " + startTimestamp  + " and endTimestamp= " + endTimestamp );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),sn,pn,startTimestamp, endTimestamp ));

				UtilityApiMethods.responseCodeValidation( response, 200,"GetPrintheadDetails", sn,pn,startTimestamp, endTimestamp );
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationWithDbComparePage.getInk_UsedFromPrintheadDetailsAPI( response, sn, pn, startTimestamp, endTimestamp );

				APIVerificationWithDbComparePage.compareInk_UsedOfPrintHeadDetailsFromApiAndDB(response, "ink_Used", sn, pn, startTimestamp, endTimestamp, serialNoList);

				this.readParamsFromExcel(i, sn,pn,startTimestamp, endTimestamp );
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

//		} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//								+ " ,  startTimestamp = " + startDate  + "  and  endTimestamp = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
//								+ " , startTimestamp= " + startDate  + " and endTimestamp= " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
//								serialNumber[i], productNumber[i] ,startDate ,endDate ));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",serialNumber[i], productNumber[i] ,startDate ,endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationWithDbComparePage.compareInk_UsedOfPrintHeadDetailsFromApiAndDB( response,"ink_Used", serialNumber[i], productNumber[i] ,startDate ,endDate, serialNoList);
//			}
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + myMap,ExtentColor.INDIGO) );
//			logger.info(".....................Parameter Passing successful ..................." ); 
//			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
//		}	
			
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>> " + serialNoList, ExtentColor.RED) );
	}

	/* 
	 ***** Compare 'time_Used' value based on 'printhead_serial_number' from getPrintheadDetails API with Db test *****
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void TC_TimeUsedValuesFromPrintheadDetailsAPIvsDB() 
	{	
		HashSet<String> serialNoList = new HashSet<String>();
//		if(! hMapData.containsValue("GetPrintheadDetails") )	{
//			throw new SkipException("Skipping 'TC_TimeUsedValuesFromPrintheadDetailsAPIvsDB' test Method because resource was not available.");
//		}
//		test.assignCategory("GetPrintheadDetails");	
//		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'time_Used' value from API Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0  )  {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++)	{
				List 		 rowvalue = row.get(i);
				String 			   sn = rowvalue.get(0).toString();
				String 			   pn = rowvalue.get(1).toString();
				String startTimestamp = rowvalue.get(2).toString();
				String   endTimestamp = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn 
						+ " ,  startTimestamp = " + startTimestamp  + "  and  endTimestamp = " + endTimestamp );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN = " + sn + ", PN = " + pn
						+ " , startTimestamp= " + startTimestamp  + " and endTimestamp= " + endTimestamp );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),sn,pn,startTimestamp, endTimestamp ));

				UtilityApiMethods.responseCodeValidation( response, 200,"GetPrintheadDetails", sn,pn,startTimestamp, endTimestamp );
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationWithDbComparePage.getTime_UsedFromPrintheadDetailsAPI( response, sn, pn, startTimestamp, endTimestamp );

				APIVerificationWithDbComparePage.compareTime_UsedOfPrintHeadDetailsFromApiAndDB(response, "time_Used", sn, pn, startTimestamp, endTimestamp, serialNoList);

				this.readParamsFromExcel(i, sn,pn,startTimestamp, endTimestamp );
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

//		} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//								+ " ,  startTimestamp = " + startDate  + "  and  endTimestamp = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
//								+ " , startTimestamp= " + startDate  + " and endTimestamp= " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
//								serialNumber[i], productNumber[i] ,startDate ,endDate ));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",serialNumber[i], productNumber[i] ,startDate ,endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationWithDbComparePage.compareTime_UsedOfPrintHeadDetailsFromApiAndDB( response,"time_Used", serialNumber[i], productNumber[i] ,startDate ,endDate, serialNoList);
//			}
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + myMap,ExtentColor.INDIGO) );
//			logger.info(".....................Parameter Passing successful ..................." ); 
//			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
//		}	
			
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>> " + serialNoList, ExtentColor.RED) );
	}

	/* 
	 ***** Compare 'color' value based on 'printhead_serial_number' from getPrintheadDetails API with Db test *****
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void TC_ColorValuesFromPrintheadDetailsAPIvsDB() 
	{	
		HashSet<String> serialNoList = new HashSet<String>();
//		if(! hMapData.containsValue("GetPrintheadDetails") )	{
//			throw new SkipException("Skipping 'TC_ColorValuesFromPrintheadDetailsAPIvsDB' test Method because resource was not available.");
//		}
//		test.assignCategory("GetPrintheadDetails");	
//		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'color' value from API Response with Seals DB *******",ExtentColor.GREEN));
//
//		if( serialNumber == null || serialNumber.length == 0  )  {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++)	{
				List 		 rowvalue = row.get(i);
				String 			   sn = rowvalue.get(0).toString();
				String 			   pn = rowvalue.get(1).toString();
				String startTimestamp = rowvalue.get(2).toString();
				String   endTimestamp = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  startTimestamp = " + startTimestamp  + "  and  endTimestamp = " + endTimestamp );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN = " + sn + ", PN = " + pn
						+ " , startTimestamp= " + startTimestamp  + " and endTimestamp= " + endTimestamp );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),sn,pn,startTimestamp, endTimestamp ));

				UtilityApiMethods.responseCodeValidation( response, 200,"GetPrintheadDetails", sn,pn,startTimestamp, endTimestamp );
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationWithDbComparePage.getColorFromPrintheadDetailsAPI( response, sn, pn, startTimestamp, endTimestamp );

				APIVerificationWithDbComparePage.compareColorOfPrintHeadDetailsFromApiAndDB(response, "color", sn, pn, startTimestamp, endTimestamp, serialNoList);
				this.readParamsFromExcel(i, sn,pn,startTimestamp, endTimestamp );
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

//		} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));
//
//			for(int i = 0 ; i < serialNumber.length ; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] 
//								+ " ,  startTimestamp = " + startDate  + "  and  endTimestamp = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]
//								+ " , startTimestamp= " + startDate  + " and endTimestamp= " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setPrintHeadDetailsUrl(APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),
//								serialNumber[i], productNumber[i] ,startDate ,endDate ));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetPrintheadDetails",serialNumber[i], productNumber[i] ,startDate ,endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationWithDbComparePage.compareColorOfPrintHeadDetailsFromApiAndDB( response,"color", serialNumber[i], productNumber[i] ,startDate ,endDate, serialNoList);
//			}
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + myMap,ExtentColor.INDIGO) );
//			logger.info(".....................Parameter Passing successful ..................." ); 
//			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
//		}		
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>> " + serialNoList, ExtentColor.RED) );
	}



	/*
	 *   ************************************ Private Methods *****************************************************
	 */
	private void  readParameter_fromJenkins()
	{
		String            sn = System.getProperty("param1"); 	
		String            pn = System.getProperty("param2"); 	
		String         sDate = System.getProperty("param3");	
		String         eDate = System.getProperty("param4"); 
		String       apiname = System.getProperty("param");
		String 	   eventcode = System.getProperty("param5");
		String detectiondate = System.getProperty("param6");

		logger.info("Serial No      := " + sn);
		logger.info("Product No     := " + pn);
		logger.info("Start Date     := " + sDate);
		logger.info("End Date       := " + eDate);
		logger.info("API NAME       := " + apiname);
		logger.info("EVENT CODE     := " + eventcode);
		logger.info("DETECTION DATE := " + detectiondate);

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

	} //End Private Method

	/*
	 * Reading parameters data from excel
	 */
	private void readParamsFromExcel(int i, String srNo, String prodNo, String startDate, String endDate) 
	{
		excelList = new ArrayList<String>();
		excelList.add( srNo);
		excelList.add( prodNo);
		excelList.add( startDate);				
		excelList.add( endDate);				
		logger.info("Excelsheet Data Lists: " + excelList);
		excelMap.put(i+1,excelList);

	} //End Private Method

} // End Class








