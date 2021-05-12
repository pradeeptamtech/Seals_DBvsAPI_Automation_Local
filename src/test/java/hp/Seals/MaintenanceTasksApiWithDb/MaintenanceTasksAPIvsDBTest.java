package hp.Seals.MaintenanceTasksApiWithDb;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;
import org.testng.*;
import org.testng.annotations.*;
import apiVerification.*;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.*;
import baseTest.BaseTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.*;


@SuppressWarnings("unused")
public class MaintenanceTasksAPIvsDBTest extends BaseTest 
{
	final static Logger logger 	 = LogManager.getLogger(MaintenanceTasksAPIvsDBTest.class);

	HeaderConfigs header		 = new HeaderConfigs();
	ExcelFileSetFromProp  xlProp = new ExcelFileSetFromProp();

	@SuppressWarnings("rawtypes")
	List<List> row = new ArrayList<List>();

	String[] serialNumber 	= null ;
	String[] productNumber 	= null; 
	String startDate 		= "" ;
	//String endDate 		= "" ;
	String date 			= "";
	String[] api_name 		= null;

	Map<String, String> hMapData = null;
	Map<Integer, List<String>> myMap = new HashMap<Integer, List<String>>();
	List<String> list = null;

	List<String> lt1 = null;
	Map<Integer, List<String>> mainteananceTasksMap = new HashMap<Integer, List<String>>();

	List<String> excelList = null;
	Map<Integer, List<String>> excelMap = new HashMap<Integer, List<String>>();

	@SuppressWarnings({ "unchecked", "static-access", "rawtypes" })
	@BeforeTest
	public void readExcel()
	{
		readParameter_fromJenkins();

		String excelPath = "./Data/MaintenanceData.xlsx";

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
			for( int j = 0; j < 3 ; j++ ) {
				list.add(excel.getCellData(i, j));		
			}
			row.add(list);
		}
	}
	//==========================================================================================================================
	/* 
	//*************** Compare 'estimated_date_trigger' based on 'id' and 'date' from'list_maintenances' of GetMaintenanceTasks API with Db **********************
	 * 
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	@Test
	public void TC_CompareEstimatedDateTriggerFromMaintenancesApiVsDB() throws Throwable 
	{		
		List<String> listOfSerialNo = new ArrayList<String>();
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetMaintenanceTasks") )	{
			throw new SkipException("Skipping 'TC_CompareEstimatedDateTriggerFromMaintenancesApiVsDB' test Method because resource was not available.");
		}
		//if(hMapData.containsValue("GetMaintenanceTasks") )	{

		test.assignCategory("GetMaintenanceTasks");	
		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'estimated_date_trigger' value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++) {
				List 	rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 		  pn = rowvalue.get(1).toString();
				String 		date = rowvalue.get(2).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + "   &  date = " + date );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " and  date= " + date );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(
						APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date));

				UtilityApiMethods.verifyResponseCode(response,200,"GetMaintenanceTasks" ,sn ,pn ,date );
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationWithDbMaintenancePage.getKey_list_maintenances_FromAPI(response, "estimated_date_trigger");

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"estimated_date_trigger", sn, pn, date, serialNoList);

				this.readParamsFromExcelForMainteneanceTasks(i, sn, pn, date);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + mainteananceTasksMap,ExtentColor.INDIGO) );
		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));

			for(int i = 0 ; i < serialNumber.length ; i++) {
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] + " ,  Date = " + startDate  );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN = " + serialNumber[i] + ", PN = " + productNumber[i]+ " , Date = " + startDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),
						serialNumber[i],productNumber[i],date ));

				UtilityApiMethods.verifyResponseCode(response, 200, "GetMaintenanceTasks",serialNumber[i],productNumber[i],date  ) ;
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"estimated_date_trigger", serialNumber[i], productNumber[i], date, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + mainteananceTasksMap,ExtentColor.INDIGO) );

			logger.info(".....................Parameter Passing successful ..................." ); 
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
		}	
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>  " + serialNoList, ExtentColor.RED) );
	}

	/*
	 * 	//*************** Compare 'last_maintenance_date' based on 'id' and 'date' from 'last_maintenance' of GetMaintenanceTasks API with Db **********************
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	@Test
	public void TC_CompareLastMaintenanceDateFromMaintenancesApiVsDB() throws Throwable 
	{		
		List<String> listOfSerialNo = new ArrayList<String>();
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetMaintenanceTasks") )	{
			throw new SkipException("Skipping 'TC_CompareLastMaintenanceDateFromMaintenancesApiVsDB' test Method because resource was not available.");
		}
		//if(hMapData.containsValue("GetMaintenanceTasks") )	{

		test.assignCategory("GetMaintenanceTasks");	
		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'last_maintenance_date' value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++) {
				List 	rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 		  pn = rowvalue.get(1).toString();
				String 		date = rowvalue.get(2).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + "   &  date = " + date );

				logger.info("\n****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " and  date= " + date );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(
						APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date));

				UtilityApiMethods.verifyResponseCode(response,200,"GetMaintenanceTasks" ,sn ,pn ,date );
				UtilityApiMethods.Validate_ResponseTime(response);
				//	APIVerificationWithDbMaintenancePage.getKey_list_maintenances_FromAPI(response, "last_maintenance_date");

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"last_maintenance_date", sn, pn, date, serialNoList);

				this.readParamsFromExcelForMainteneanceTasks(i, sn, pn, date);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + mainteananceTasksMap,ExtentColor.INDIGO) );
		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));

			for(int i = 0 ; i < serialNumber.length ; i++) {
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] + " ,  Date = " + startDate  );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN = " + serialNumber[i] + ", PN = " + productNumber[i]+ " , Date = " + startDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),
								serialNumber[i],productNumber[i],date ));

				UtilityApiMethods.verifyResponseCode(response, 200, "GetMaintenanceTasks",serialNumber[i],productNumber[i],date  ) ;
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"last_maintenance_date", serialNumber[i], productNumber[i], date, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + mainteananceTasksMap,ExtentColor.INDIGO) );

			logger.info(".....................Parameter Passing successful ..................." ); 
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
		}	
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>  " + serialNoList, ExtentColor.RED) );
	}

	/*
	//****** Compare 'user_replaceable' based on 'id' and 'date' from'list_maintenances' of GetMaintenanceTasks API with Db **********************
	 * 
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	@Test
	public void TC_CompareUserReplaceableFromMaintenancesApiVsDB() throws Throwable 
	{		
		List<String> listOfSerialNo = new ArrayList<String>();
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetMaintenanceTasks") )	{
			throw new SkipException("Skipping 'TC_CompareUserReplaceableFromMaintenancesApiVsDB' test Method because resource was not available.");
		}
		//if(hMapData.containsValue("GetMaintenanceTasks") )	{
		test.assignCategory("GetMaintenanceTasks");	
		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'user_replaceable' value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++) {
				List 	rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 		  pn = rowvalue.get(1).toString();
				String 		date = rowvalue.get(2).toString();
				
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + "   &  date = " + date );

				logger.info("\n****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " and  date= " + date );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(
						APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date));

				UtilityApiMethods.verifyResponseCode(response,200,"GetMaintenanceTasks" ,sn ,pn ,date );
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationWithDbMaintenancePage.getKey_list_maintenances_FromAPI(response, "user_replaceable");

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"user_replaceable", sn, pn, date, serialNoList);

				this.readParamsFromExcelForMainteneanceTasks(i, sn, pn, date);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + mainteananceTasksMap,ExtentColor.INDIGO) );
		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));

			for(int i = 0 ; i < serialNumber.length ; i++) {
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] + " ,  Date = " + startDate  );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN = " + serialNumber[i] + ", PN = " + productNumber[i]+ " , Date = " + startDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),
						serialNumber[i],productNumber[i],date ));

				UtilityApiMethods.verifyResponseCode(response, 200, "GetMaintenanceTasks",serialNumber[i],productNumber[i],date  ) ;
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"user_replaceable", serialNumber[i], productNumber[i], date, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + mainteananceTasksMap,ExtentColor.INDIGO) );

			logger.info(".....................Parameter Passing successful ..................." ); 
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
		}	
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>  " + serialNoList, ExtentColor.RED) );
	}


	/*
	//****** Compare 'progress_Percentage' based on 'id' and 'date' from'list_maintenances' of GetMaintenanceTasks API with Db **********************
	 * 
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	@Test
	public void TC_CompareProgressPercentageFromMaintenancesApiVsDB() throws Throwable 
	{		
		List<String> listOfSerialNo = new ArrayList<String>();
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetMaintenanceTasks") )	{
			throw new SkipException("Skipping 'TC_CompareProgressPercentageFromMaintenancesApiVsDB' test Method because resource was not available.");
		}
		//if(hMapData.containsValue("GetMaintenanceTasks") )	{

		test.assignCategory("GetMaintenanceTasks");	
		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'progress_Percentage' value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++) {
				List 	rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 		  pn = rowvalue.get(1).toString();
				String 		date = rowvalue.get(2).toString();
				
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + "   &  date = " + date );

				logger.info("\n****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " and  date= " + date );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(
						APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date));

				UtilityApiMethods.verifyResponseCode(response,200,"GetMaintenanceTasks" ,sn ,pn ,date );
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationWithDbMaintenancePage.getKey_list_maintenances_FromAPI(response, "progress_Percentage");

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"progress_Percentage", sn, pn, date, serialNoList);

				this.readParamsFromExcelForMainteneanceTasks(i, sn, pn, date);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + mainteananceTasksMap,ExtentColor.INDIGO) );
		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));

			for(int i = 0 ; i < serialNumber.length ; i++) {
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] + " ,  Date = " + startDate  );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN = " + serialNumber[i] + ", PN = " + productNumber[i]+ " , Date = " + startDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),
						serialNumber[i],productNumber[i],date ));

				UtilityApiMethods.verifyResponseCode(response, 200, "GetMaintenanceTasks",serialNumber[i],productNumber[i],date  ) ;
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"progress_Percentage", serialNumber[i], productNumber[i], date, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + mainteananceTasksMap,ExtentColor.INDIGO) );

			logger.info(".....................Parameter Passing successful ..................." ); 
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
		}	
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>  " + serialNoList, ExtentColor.RED) );
	}

	/*
	//****** Compare 'name' based on 'id' and 'date' from'list_maintenances' of GetMaintenanceTasks API with Db **********************
	 * 
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	@Test
	public void TC_CompareNameFromMaintenancesApiVsDB() throws Throwable 
	{		
		List<String> listOfSerialNo = new ArrayList<String>();
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetMaintenanceTasks") )	{
			throw new SkipException("Skipping 'TC_CompareNameFromMaintenancesApiVsDB' test Method because resource was not available.");
		}
		//if(hMapData.containsValue("GetMaintenanceTasks") )	{
		test.assignCategory("GetMaintenanceTasks");	
		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'name' value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++) {
				List 	rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 		  pn = rowvalue.get(1).toString();
				String 		date = rowvalue.get(2).toString();
				
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + "   &  date = " + date );

				logger.info("\n****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " and  date= " + date );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(
						APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date));

				UtilityApiMethods.verifyResponseCode(response,200,"GetMaintenanceTasks" ,sn ,pn ,date );
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationWithDbMaintenancePage.getKey_list_maintenances_FromAPI(response, "name");

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"name", sn, pn, date, serialNoList);
				this.readParamsFromExcelForMainteneanceTasks(i, sn, pn, date);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + mainteananceTasksMap,ExtentColor.INDIGO) );
		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));

			for(int i = 0 ; i < serialNumber.length ; i++) {
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] + " ,  Date = " + startDate  );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN = " + serialNumber[i] + ", PN = " + productNumber[i]+ " , Date = " + startDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),
						serialNumber[i],productNumber[i],date ));

				UtilityApiMethods.verifyResponseCode(response, 200, "GetMaintenanceTasks",serialNumber[i],productNumber[i],date  ) ;
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"name", serialNumber[i], productNumber[i], date, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + mainteananceTasksMap,ExtentColor.INDIGO) );

			logger.info(".....................Parameter Passing successful ..................." ); 
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
		}	
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>  " + serialNoList, ExtentColor.RED) );
	}

	/*
	//****** Compare 'status' based on 'id' and 'date' from'list_maintenances' of GetMaintenanceTasks API with Db **********************
	 * 
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	@Test
	public void TC_CompareStatusFromMaintenancesApiVsDB() throws Throwable 
	{		
		List<String> listOfSerialNo = new ArrayList<String>();
		HashSet<String> serialNoList = new HashSet<String>();
		if(! hMapData.containsValue("GetMaintenanceTasks") )	{
			throw new SkipException("Skipping 'TC_CompareStatusFromMaintenancesApiVsDB' test Method because resource was not available.");
		}
		//if(hMapData.containsValue("GetMaintenanceTasks") )	{

		test.assignCategory("GetMaintenanceTasks");	
		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare 'status' value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Excel Sheet *** ",ExtentColor.BLUE));

			for(int i = 0; i < row.size(); i++) {
				List 	rowvalue = row.get(i);
				String 		  sn = rowvalue.get(0).toString();
				String 		  pn = rowvalue.get(1).toString();
				String 		date = rowvalue.get(2).toString();
				
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + "   &  date = " + date );

				logger.info("\n****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " and  date= " + date );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(
						APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date));

				UtilityApiMethods.verifyResponseCode(response,200,"GetMaintenanceTasks" ,sn ,pn ,date );
				UtilityApiMethods.Validate_ResponseTime(response);
				//APIVerificationWithDbMaintenancePage.getKey_list_maintenances_FromAPI(response, "status");

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"status", sn, pn, date, serialNoList);
				this.readParamsFromExcelForMainteneanceTasks(i, sn, pn, date);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + mainteananceTasksMap,ExtentColor.INDIGO) );
		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel( "**** Reading  Parameters  from Parameterized job *** ",ExtentColor.BLUE));

			for(int i = 0 ; i < serialNumber.length ; i++) {
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] + " ,  Date = " + startDate  );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN = " + serialNumber[i] + ", PN = " + productNumber[i]+ " , Date = " + startDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),
						serialNumber[i],productNumber[i],date ));

				UtilityApiMethods.verifyResponseCode(response, 200, "GetMaintenanceTasks",serialNumber[i],productNumber[i],date  ) ;
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerificationWithDbMaintenancePage.compareKeyOfListMaintenancesFromApiAndDB(response,"status", serialNumber[i], productNumber[i], date, serialNoList);
			}
			logger.info("Parameterized List of User inputs data::>> " + mainteananceTasksMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized  List  of  User inputs data ::>>      " + mainteananceTasksMap,ExtentColor.INDIGO) );

			logger.info(".....................Parameter Passing successful ..................." ); 
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK ));
		}	
		logger.info("Failing Serial_number list ======>  " + serialNoList);
		test.log(Status.PASS,MarkupHelper.createLabel(" ****** List of Serial no's are failed **********>  " + serialNoList, ExtentColor.RED) );
	}


	//=============================================== END ==========================================================
	/*
	 *   ************************************ Private Methods ******************************************
	 */
	@SuppressWarnings("unused")
	private void  readParameter_fromJenkins()
	{
		String 		sn = System.getProperty("param1"); 	
		String 		pn = System.getProperty("param2"); 	
		String 	 sDate = System.getProperty("param3");	
		//String eDate = System.getProperty("param4"); 
		String apiname = System.getProperty("param");

		logger.info("Serial No	:= " + sn);
		logger.info("Product No	:= " + pn);
		logger.info(" Date		:= " + sDate);
		//logger.info("End Date := " + eDate);
		logger.info("API NAME	:= " + apiname);

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
		//endDate = eDate;

		if( apiname != null ) 
		{
			api_name = apiname.split(",");
			hMapData = new HashMap<String, String>();
			for(String str : api_name) {  //iterate through an array
				hMapData.put( "key_" + str , str );    //split the data by :
			}
			logger.info("String Array to HashMap: " + hMapData);	
		}


		// Reading data for Parameterized job 
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

	} //End Private Method

	/*
	 * Reading parameters data from excel
	 */

	// reading date from Excel for getMaintenanceTasks API
	private void readParamsFromExcelForMainteneanceTasks(int i, String srNo, String prodNo, String date) {
		excelList = new ArrayList<String>();
		excelList.add( srNo);
		excelList.add( prodNo);
		excelList.add( date);				
		logger.info("Excelsheet Data Lists: " + excelList);
		mainteananceTasksMap.put(i+1 , excelList);
	}
	//End Private Method

} // End Class
