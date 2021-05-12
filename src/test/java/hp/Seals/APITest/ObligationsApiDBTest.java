package hp.Seals.APITest;

import java.io.*;
import java.util.*;
import org.apache.log4j.*;
import org.testng.*;
import org.testng.annotations.*;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.HeaderConfigs;
import apiConfig.APIPath;
import apiVerification.APIVerification;
import baseTest.BaseTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utils.*;


public class ObligationsApiDBTest extends BaseTest 
{

	final static Logger   logger = LogManager.getLogger(ObligationsApiDBTest.class);

	HeaderConfigs         header = new HeaderConfigs();
	ExcelFileSetFromProp  xlProp = new ExcelFileSetFromProp();

	List<List> row = new ArrayList<List>();

	String[] 	serialNumber = null ;
	String[]   productNumber = null; 
	String         startDate = "" ;
	String           endDate = "" ;
	String              date = "";
	String[]        api_name = null;
	String[]       eventCode = null;
	String     detectionDate = "";

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
		readParameter_fromJenkins();

		String excelPath = "./Data/TestData.xlsx";
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
	 * ****************************** Get the Printer obligation Information ******************************
	 * ******** Verify contract object from *** getobligations API ****************************************
	 */
	@Test(priority=1)
	public void TC_compareContractFromObligationsAPIwithDB()
	{	
		HashSet<String> listOfSerialNo = new HashSet<String> ();
		if(! hMapData.containsValue("GetObligations") )
		{
			throw new SkipException("Skipping 'TC_compareContractFromObligationsAPIwithDB' test Method because resource was not available.");
		}
		//if( hMapData.containsValue("GetObligations") ) {
		test.assignCategory("GetObligations");	
		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare  'contract' objects value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel(" ******* Reading Parameters from Excel Sheet ******** ",ExtentColor.PINK));

			for(int i = 0; i < row.size(); i++)
			{
				List rowvalue = row.get(i);
				test.log(Status.PASS , " *** Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + rowvalue.get(0) + "  AND ProductId = " + rowvalue.get(1));
				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + " and based on Serial_no = " + rowvalue.get(0) + " AND ProductId = " + rowvalue.get(1));

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setObligationUrl(APIPath.apiPath.GET_OBLIGATION.toString(),
								rowvalue.get(0).toString(), rowvalue.get(1).toString()) );

				UtilityApiMethods.validateResponseCode(response,200,"GetObligations",rowvalue.get(0).toString(), rowvalue.get(1).toString() );
				UtilityApiMethods.Validate_ResponseTime(response);	

				//APIVerification.validateContractFromObligationAPI( response );

				APIVerification.compareContractFromApiAndDB(response, rowvalue.get(0).toString(), rowvalue.get(1).toString(), listOfSerialNo);

				this.read2ParamsFromExcel(i,rowvalue.get(0).toString(),rowvalue.get(1).toString() );
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + entitledObligationMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + entitledObligationMap,ExtentColor.INDIGO) );	

		}	else {

			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));

			for(int i = 0 ; i < serialNumber.length ; i++) {

				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setObligationUrl(APIPath.apiPath.GET_OBLIGATION.toString(), serialNumber[i], productNumber[i] ));

				UtilityApiMethods.validateResponseCode(response,200,"GetObligations",serialNumber[i], productNumber[i] );
				UtilityApiMethods.Validate_ResponseTime(response);

				//UtilityApiMethods.VerifyMoreKeyValues(response, "offerCode");

				APIVerification.compareContractFromApiAndDB(response, serialNumber[i], productNumber[i],listOfSerialNo );
			}
			logger.info(".....................Parameter Passing successful ..................." );
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK));

			logger.info("Parameterized List of User inputs data::>> " + entitledObligationMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + entitledObligationMap,ExtentColor.INDIGO) );

		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed ***  " + listOfSerialNo, ExtentColor.RED) );
		//}
	}

	/* 
	 * ********Verify warranty object from *** getobligations API ******************************
	 * 
	 */
	@Test(priority=2)
	public void TC_compareWarrantyFromObligationsAPIwithDB()
	{	
		HashSet<String> listOfSerialNo = new HashSet<String> ();
		if(! hMapData.containsValue("GetObligations") )
		{
			throw new SkipException("Skipping 'TC_compareWarrantyFromObligationsAPIwithDB' test Method because resource was not available.");	
		}
		//if( hMapData.containsValue("GetObligations") )	{
		test.assignCategory("GetObligations");	
		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare  'Warranty' objects value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {
			test.log(Status.PASS,MarkupHelper.createLabel(" ******* Reading Parameters from Excel Sheet ******** ",ExtentColor.PINK));

			for(int i = 0; i < row.size(); i++)
			{
				List rowvalue = row.get(i);
				test.log(Status.PASS	, " *** Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + rowvalue.get(0) + "  AND ProductId = " + rowvalue.get(1));
				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + " and based on Serial_no = " + rowvalue.get(0) + " AND ProductId = " + rowvalue.get(1));

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setObligationUrl(APIPath.apiPath.GET_OBLIGATION.toString(),
								rowvalue.get(0).toString(), rowvalue.get(1).toString()) );

				UtilityApiMethods.responseCodeValidation(response, 200);
				UtilityApiMethods.Validate_ResponseTime(response);	

				//APIVerification.validateWarrantyFromObligationAPI(response);
				APIVerification.compareWarrantyFromApiAndDB(response,rowvalue.get(0).toString(),rowvalue.get(1).toString(), listOfSerialNo);

				this.read2ParamsFromExcel(i,rowvalue.get(0).toString(),rowvalue.get(1).toString() );
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + entitledObligationMap );
			test.log(Status.PASS ,MarkupHelper.createLabel( "From Excel sheet Reading parameters list::> " + entitledObligationMap,ExtentColor.INDIGO) );

		} else {
			test.log(Status.PASS,MarkupHelper.createLabel( "*** Reading Parameters from Parameterized job *** ",ExtentColor.PINK ));

			for(int i = 0 ; i < serialNumber.length ; i++) {

				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setObligationUrl(APIPath.apiPath.GET_OBLIGATION.toString(), serialNumber[i], productNumber[i] ));

				UtilityApiMethods.validateResponseCode(response,200,"GetObligations",serialNumber[i], productNumber[i] );
				UtilityApiMethods.Validate_ResponseTime(response);

				APIVerification.compareWarrantyFromApiAndDB(response,serialNumber[i], productNumber[i], listOfSerialNo);

			}

			logger.info("Parameterized List of User inputs data::>> " + entitledObligationMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + entitledObligationMap,ExtentColor.INDIGO) );
			
			logger.info(".....................Parameter Passing successful ..................." );
			test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK));
		}
		logger.info("Failing Serial_number list ======>  " + listOfSerialNo);
		test.log(Status.PASS,MarkupHelper.createLabel("*** List of Serial no's are failed *** " + listOfSerialNo, ExtentColor.RED) );
	//}
	}

	/* 
	 * *********Compare offerCode value from API Reponse and Seals DB **********
	 * 
	 */
	//@Test
	public void TC_getObligations_offerCode_CompareWithDB()
	{	
		if(! hMapData.containsValue("GetObligations") )
		{
			throw new SkipException("Skipping 'TC_getObligations_offerCode_CompareWithDB' test Method because resource was not available.");	
		}
		test.assignCategory("GetObligations");	
		test.log(Status.INFO, MarkupHelper.createLabel( "***Compare All attributes value from API Response with Seals DB *******",ExtentColor.GREEN));

		if( serialNumber == null || serialNumber.length == 0  )  {

			test.log(Status.PASS,MarkupHelper.createLabel(" **** Reading Parameters from Excel Sheet **** ",ExtentColor.PINK));

			//Response response = RestAssured.given().headers(header.HeadersWithToken()).when().get(APIPath.apiPath.GET_PrinterStatusHistory);
			for(int i = 0; i < row.size(); i++)
			{
				List rowvalue=row.get(i);
				test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + rowvalue.get(0) + " AND ProductId = " + rowvalue.get(1));
				logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + rowvalue.get(0) + " AND ProductId = " + rowvalue.get(1));

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setObligationUrl(APIPath.apiPath.GET_OBLIGATION.toString(),rowvalue.get(0).toString(),rowvalue.get(1).toString()));

				UtilityApiMethods.ValidateResponseCode(response,200,"GetObligations",rowvalue.get(0).toString(),rowvalue.get(1).toString());

				UtilityApiMethods.Validate_ResponseTime(response);

				//UtilityApiMethods.VerifyMoreKeyValues(response, "offerCode");
				APIVerification.compareContractOfferCodeFromApiAndDB(response,"contract","offerCode",rowvalue.get(0).toString(),rowvalue.get(1).toString() );

				this.read2ParamsFromExcel(i,rowvalue.get(0).toString(),rowvalue.get(1).toString() );
			}
			logger.info("From Excel sheet Reading parameters list ::>> " + entitledObligationMap );
			test.log(Status.PASS , "From Excel sheet Reading parameters list::> " + entitledObligationMap );

		}
		else {
			test.log(Status.PASS,MarkupHelper.createLabel(" *** Reading Parameters from Parameterized Job ***", ExtentColor.PINK));

			for(int i = 0 ; i < serialNumber.length ; i++) {

				test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
				logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

				Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
						get(APIPath.apiPath.setObligationUrl(APIPath.apiPath.GET_OBLIGATION.toString(), serialNumber[i], productNumber[i] ));

				UtilityApiMethods.validateResponseCode(response,200,"GetObligations",serialNumber[i], productNumber[i] );
				UtilityApiMethods.Validate_ResponseTime(response);

				//UtilityApiMethods.VerifyMoreKeyValues(response, "offerCode");

				APIVerification.compareContractOfferCodeFromApiAndDB(response,"contract","offerCode", serialNumber[i], productNumber[i] );
			}
			logger.info("Parameterized List of User inputs data::>> " + entitledObligationMap );
			test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + entitledObligationMap,ExtentColor.INDIGO) );
		}
	//}
}

	/*
	 *   ************************************ Private Methods ******************************************
	 */
	private void  readParameter_fromJenkins()
	{
		String 		sn = System.getProperty("param1"); 	
		String 		pn = System.getProperty("param2"); 	
		String   sDate = System.getProperty("param3");	
		String   eDate = System.getProperty("param4"); 
		String apiname = System.getProperty("param");

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

	}

	/*
	 * Reading parameters data from excel
	 */

	@SuppressWarnings("unused")
	private void readParamsFromExcel(int i, String srNo, String prodNo, String startDate, String endDate) {
		excelList = new ArrayList<String>();
		excelList.add( srNo);
		excelList.add( prodNo);
		excelList.add( startDate);				
		excelList.add( endDate);				
		logger.info("Excelsheet Data Lists: " + excelList);
		excelMap.put(i+1,excelList);
	}

	// reading date from Excel for getObligation API
	private void read2ParamsFromExcel(int i, String srNo, String prodNo) {
		excelList = new ArrayList<String>();
		excelList.add( srNo);
		excelList.add( prodNo);
		logger.info("Excelsheet Data Lists: " + excelList);
		entitledObligationMap.put(i+1, excelList);
	}

	//End Private Method

}
