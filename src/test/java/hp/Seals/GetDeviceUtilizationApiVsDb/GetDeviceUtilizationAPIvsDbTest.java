package hp.Seals.GetDeviceUtilizationApiVsDb;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.*;
import org.testng.*;
import org.testng.annotations.*;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
//import com.relevantcodes.extentreports.LogStatus;
import apiConfig.*;
import apiVerification.APIVerification;
import baseTest.BaseTest;
import hp.Seals.getErrorEventsApiVsDbTest.APIVerificationErrorEventsPage;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.*;


@SuppressWarnings("unused")
public class  GetDeviceUtilizationAPIvsDbTest extends BaseTest {

	final static Logger logger = LogManager.getLogger(GetDeviceUtilizationAPIvsDbTest.class);

	HeaderConfigs header = new HeaderConfigs();
	ExcelFileSetFromProp  xlProp = new ExcelFileSetFromProp();
	@SuppressWarnings("rawtypes")
	List<List> row = new ArrayList<List>(); 

	String[] serialNumber = null ;
	String[] productNumber = null; 
	String startDate = "" ;
	String endDate = "" ;
	String date = "";
	String[] api_name = null;

	Map<String, String> hMapData = null;
	Map<Integer, List<String>> myMap = new HashMap<Integer, List<String>>();
	List<String> list = null;

	List<String> excelList = null;
	Map<Integer, List<String>> excelMap = new HashMap<Integer, List<String>>();


	@BeforeTest
	public void readExcel()
	{
		//this.readParameter_fromJenkins();

		String excelPath = "./Data/DeviceUtilizationTestData.xlsx";
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
		if( map1.get("excelSheetRowCount") != null ) {
			rowCount = Integer.parseInt(map1.get("excelSheetRowCount"));
		}
		// rowCount // for(int i = 0 ; i<excel.getRowCount(); i++)
		for(int i = 1 ; i <= rowCount  ; i++)         
		{   
			List list = new ArrayList();
			for( int j = 0; j < 8 ; j++ ) {
				list.add( excel.getCellData(i, j) );		
			}
			row.add(list);
		}
	}

	@AfterTest
	public void TearDown() {
		extent.flush();
		//test.log(Status.INFO,"<=== All Test Methods are executed ===>");
	}

	/*   
	 * ************************** GetDeviceUtilization API Test Method starts here ********************************
	 */

	/* 
	 * ********* Verify 'productive_Hours' value from GetDeviceUtilization Api with Seals DB  Test ***********
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void TC_CompareProductiveHoursFromApiWithDB()
	{	
		List listOfSerialNo = new ArrayList();
//		if(! hMapData.containsValue("GetDeviceUtilization") )	{
//			throw new SkipException("Skipping 'TC_CompareProductiveHoursFromApiWithDB' test Method because resource was not available.");	
//		}	
//		test.assignCategory("GetDeviceUtilization"); 
//		test.log(Status.INFO , MarkupHelper.createLabel("***Verify  GetDeviceUtilization API Information ****", ExtentColor.CYAN ));
//
//		if( serialNumber == null || serialNumber.length == 0  )       
//		{
			test.log(Status.PASS, MarkupHelper.createLabel( "************* Reading Parameters from Excel Sheet ************** ", ExtentColor.BLUE));

			for(int i = 0 ; i < row.size() ; i++) {

				List rowvalue    = row.get(i);
				String sn        = rowvalue.get(0).toString();
				String pn        = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String endDate   = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  StartDate = " + startDate + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), sn,pn,startDate, endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetDeviceUtilization",sn,pn,startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response);

				//APIVerificationDeviceUtilizationPage.getProductive_HoursFromDeviceUtilizationAPI(response,"productive_Hours",sn,pn,startDate, endDate , listOfSerialNo); 
				APIVerificationDeviceUtilizationPage.compareProductiveHoursFromAPIandDB(response,"productive_Hours", sn, pn, startDate, endDate, listOfSerialNo);

				this.readParamsFromExcel(i, sn,pn,startDate, endDate);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );

//		} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));
//			for(int i = 0 ; i < serialNumber.length; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] + " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]	+ " , StartDate= " + startDate  + " and EndDate= " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), serialNumber[i], productNumber[i], startDate, endDate));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetDeviceUtilization", serialNumber[i], productNumber[i],startDate, endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationDeviceUtilizationPage.compareProductiveHoursFromAPIandDB(response,"productive_Hours", serialNumber[i], productNumber[i], startDate, endDate, listOfSerialNo);
//			}
//			logger.info(".....................Parameter Passing successful ..................." );
//			test.log(Status.PASS,MarkupHelper.createLabel(" ***** Parameter Passing successful ******** ", ExtentColor.PINK));
//
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );
//		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
	}

	/* 
	 * *******Verify 'on_Hours' value from GetDeviceUtilization Api with Seals DB  Test ***********
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void TC_CompareOnHoursFromApiWithDB()
	{	
		List listOfSerialNo = new ArrayList();
//		if(! hMapData.containsValue("GetDeviceUtilization") ) {
//			throw new SkipException("Skipping 'TC_CompareOnHoursFromApiWithDB' test Method because resource was not available.");	
//		}	
//		test.assignCategory("GetDeviceUtilization"); 
//		test.log(Status.INFO , MarkupHelper.createLabel("***Verify  GetDeviceUtilization API Information ****", ExtentColor.CYAN ));
//
//		if( serialNumber == null || serialNumber.length == 0  )       
//		{
			test.log(Status.PASS, MarkupHelper.createLabel( "************* Reading Parameters from Excel Sheet ************** ", ExtentColor.BLUE));

			for(int i = 0 ; i < row.size() ; i++) {
				List rowvalue    = row.get(i);
				String sn        = rowvalue.get(0).toString();
				String pn        = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String endDate   = rowvalue.get(3).toString();

				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "   AND based on SN = " + sn + ", PN = "  + pn + " ,  StartDate = " + startDate + "  and  EndDate = " + endDate );

				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
						+ "  AND based on SN= " + sn + ", PN= " + pn + " , StartDate= " + startDate  + " and EndDate= " + endDate );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), sn,pn,startDate, endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetDeviceUtilization",sn,pn,startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response); 

				//APIVerificationDeviceUtilizationPage.getHoursValuesFromDeviceUtilizationAPI(response,"on_Hours", sn,pn,startDate, endDate, listOfSerialNo);
				APIVerificationDeviceUtilizationPage.compareOnHoursFromAPIandDB(response,"on_Hours", sn, pn, startDate, endDate, listOfSerialNo);

				this.readParamsFromExcel(i, sn,pn,startDate, endDate);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );
		
//	} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));
//			for(int i = 0 ; i < serialNumber.length; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] + " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]	+ " , StartDate= " + startDate  + " and EndDate= " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), serialNumber[i], productNumber[i], startDate, endDate));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetDeviceUtilization", serialNumber[i], productNumber[i],startDate, endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationDeviceUtilizationPage.compareOnHoursFromAPIandDB(response,"on_Hours", serialNumber[i], productNumber[i], startDate, endDate, listOfSerialNo);
//
//			}
//			logger.info(".....................Parameter Passing successful ..................." );
//			test.log(Status.PASS,MarkupHelper.createLabel(" ***** Parameter Passing successful ******** ", ExtentColor.PINK) );
//
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );
//		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
	}
	/* 
	 * *******Verify The entered 'serial_Number' value must be available in response from GetDeviceUtilization Api with Seals DB  Test ***********
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void TC_CompareSerialNumberFromApiWithDB()
	{	
		List listOfSerialNo = new ArrayList<>();
//		if(! hMapData.containsValue("GetDeviceUtilization") ) {
//			throw new SkipException("Skipping 'TC_CompareOnHoursFromApiWithDB' test Method because resource was not available.");	
//		}	
//		test.assignCategory("GetDeviceUtilization"); 
//		test.log(Status.INFO , MarkupHelper.createLabel("***Verify  GetDeviceUtilization API Information ****", ExtentColor.CYAN ));
//
//		if( serialNumber == null || serialNumber.length == 0  )       
//		{
			test.log(Status.PASS, MarkupHelper.createLabel( "************* Reading Parameters from Excel Sheet ************** ", ExtentColor.BLUE));

			for(int i = 0 ; i < row.size() ; i++) {

				List rowvalue    = row.get(i);
				String sn        = rowvalue.get(0).toString();
				String pn        = rowvalue.get(1).toString();
				String startDate = rowvalue.get(2).toString();
				String endDate   = rowvalue.get(3).toString();

				test.log(Status.PASS ,MarkupHelper.createLabel(" ***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on SN = " + sn + " & PN =" + pn, ExtentColor.PINK));
				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on SN = " + sn + " & PN =" + pn);

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), sn,pn,startDate, endDate));

				UtilityApiMethods.responseCodeValidation(response,200,"GetDeviceUtilization",sn,pn,startDate, endDate);
				UtilityApiMethods.Validate_ResponseTime(response); 						
				//APIVerificationDeviceUtilizationPage.getKeyValuesFromAPI(response,"serial_Number", sn,pn,startDate, endDate, listOfSerialNo);

				APIVerificationDeviceUtilizationPage.compareSerialNoFromDeviceUtilizationApiVsDB(response,"serial_Number", sn, pn, startDate, endDate, listOfSerialNo);			
				this.readParamsFromExcel(i, sn,pn,startDate, endDate);
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + excelMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("From Excel sheet Reading parameters list::> " + excelMap,ExtentColor.INDIGO) );
		
//	} else {
//			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));
//			for(int i = 0 ; i < serialNumber.length; i++) {
//				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "   AND based on SN = " + serialNumber[i] + ", PN = "  + productNumber[i] + " ,  StartDate = " + startDate  + "  and  EndDate = " + endDate );
//
//				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() 
//						+ "  AND based on SN= " + serialNumber[i] + ", PN= " + productNumber[i]	+ " , StartDate= " + startDate  + " and EndDate= " + endDate );
//
//				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
//						get(APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), serialNumber[i], productNumber[i], startDate, endDate));
//
//				UtilityApiMethods.responseCodeValidation(response,200,"GetDeviceUtilization", serialNumber[i], productNumber[i],startDate, endDate);
//				UtilityApiMethods.Validate_ResponseTime(response);
//
//				APIVerificationDeviceUtilizationPage.compareSerialNoFromDeviceUtilizationApiVsDB(response,"serial_Number", serialNumber[i], productNumber[i], startDate, endDate, listOfSerialNo);
//
//			}
//			logger.info(".....................Parameter Passing successful ..................." );
//			test.log(Status.PASS,MarkupHelper.createLabel(" ***** Parameter Passing successful ******** ", ExtentColor.PINK) );
//
//			logger.info("Parameterized List of User inputs data::>> " + myMap );
//			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + myMap,ExtentColor.INDIGO) );
//		}
			
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
	}


	/*
	 *   ********************************* Private Methods ******************************************************
	 */

	private void  readParameter_fromJenkins()
	{
		String sn      = System.getProperty("param1"); 	
		String pn      = System.getProperty("param2"); 	
		String sDate   = System.getProperty("param3");	
		String eDate   = System.getProperty("param4"); 
		String apiname = System.getProperty("param");

		logger.info("Serial No := " + sn);
		logger.info("Product No:= " + pn);
		logger.info("Start Date:= " + sDate);
		logger.info("End Date  := " + eDate);
		logger.info("API NAME  := " + apiname);

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
			for(int i = 0; i < serialNumber.length ; i++ ){
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



} // End Class

