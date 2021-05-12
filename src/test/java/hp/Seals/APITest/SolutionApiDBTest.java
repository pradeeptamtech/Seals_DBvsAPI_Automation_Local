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


public class SolutionApiDBTest extends BaseTest 
{

	final static Logger   logger = LogManager.getLogger(SolutionApiDBTest.class);

	HeaderConfigs         header = new HeaderConfigs();
	ExcelFileSetFromProp  xlProp = new ExcelFileSetFromProp();

	@SuppressWarnings("rawtypes")
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
	 * *****************Compare 'event_Code' value from API response with Seals DB Api Test ****************
	 * 
	 */
	//@Test
	public void TC_getSolution_eventCode_ValueCompareWithDB()
	{
		//List listOfSerialNo = new ArrayList();
		if(! hMapData.containsValue("GetSolution") )
		{
			throw new SkipException("Skipping 'TC_getSolution_eventCode_ValueCompareWithDB' test Method because resource was not available.");	
		}
		//if( hMapData.containsValue("GetSolution") )	{
			
			test.assignCategory("getSolution_eventCode_ValueCompareWithDB");	
			test.log(Status.INFO ,MarkupHelper.createLabel( " *****Compare 'event_Code' value from API response with Seals DB ******* ",ExtentColor.GREEN));

			if( serialNumber == null || serialNumber.length == 0  )  {
				test.log(Status.PASS,MarkupHelper.createLabel(" ************* Reading Parameters from Excel Sheet ************** ",ExtentColor.PINK));

				for(int i = 0 ; i < row.size() ; i++)	{

					List rowvalue = row.get(i);	
					
					test.log(Status.PASS , "***Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + rowvalue.get(0));
					logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + rowvalue.get(0));

					Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
							get(APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),rowvalue.get(0).toString(),rowvalue.get(1).toString(),rowvalue.get(4).toString(),rowvalue.get(5).toString()));		

					UtilityApiMethods.verifyResponseCode1(response, 200, "GetSolution",rowvalue. get(1).toString(),
							rowvalue.get(0).toString(), rowvalue.get(4).toString(),rowvalue.get(5).toString());

					UtilityApiMethods.Validate_ResponseTime(response);

					//UtilityApiMethods.getResponseKeyFromGSON(response,"solutionJson","severity");
					UtilityApiMethods.verifyEventCode(response, "solutionJson", "event_Code",rowvalue.get(0).toString(),
							rowvalue.get(1).toString(),rowvalue.get(4).toString(),rowvalue.get(5).toString());			

					APIVerification.verifyKey_Response(response,"event_Code"); 

					APIVerification.validateKeyFromResponseWithDB(response,"solutionJson","event_Code");

					this.readParamsFromExcelForGetSolution(i, rowvalue.get(0).toString(),rowvalue.get(1).toString(),rowvalue.get(4).toString(),rowvalue.get(5).toString());
				}
				logger.info("From Excel sheet Reading parameters list ::>> " + eventMap );
				test.log(Status.PASS , MarkupHelper.createLabel("From Excel sheet Reading parameters list::> " + eventMap, ExtentColor.BROWN ));
				//test.log(Status.PASS ,"From Excel sheet Reading parameters list::> " + eventMap );

			} else {
				test.log(Status.PASS,MarkupHelper.createLabel("*** Reading Parameters from Parameterized Job *** ",ExtentColor.PINK ));

				for(int i = 0 ; i < serialNumber.length; i++) {

					test.log(Status.PASS, "Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i] + "  & product_no = " + productNumber[i] );
					logger.info("Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() + "  and based on Serial_no = " + serialNumber[i]  + "  & product_no = " + productNumber[i] );

					Response response = RestAssured.given().headers(header.HeadersWithToken()).when().
							get(APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(), productNumber[i] ,serialNumber[i], eventCode[i],detectionDate));		

					UtilityApiMethods.verifyResponseCode1(response, 200, "GetSolution",productNumber[i] ,serialNumber[i], eventCode[i],detectionDate);
					UtilityApiMethods.Validate_ResponseTime(response);

					//UtilityApiMethods.getResponseKeyFromGSON(response,"solutionJson","severity");
					UtilityApiMethods.verifyEventCode(response, "solutionJson", "event_Code", productNumber[i], serialNumber[i],eventCode[i],detectionDate );			

					APIVerification.verifyKey_Response(response,"event_Code"); 

					APIVerification.validateKeyFromResponseWithDB(response,"solutionJson","event_Code");
				}
				logger.info(".....................Parameter Passing successful ..................." );
				test.log(Status.PASS,MarkupHelper.createLabel(" ************* Parameter Passing successful ******************** ", ExtentColor.PINK));
				
				logger.info("Parameterized List of User inputs data::>> " + eventMap );
				test.log(Status.PASS ,MarkupHelper.createLabel("Parameterized List of User inputs data :> " + eventMap,ExtentColor.INDIGO ));
			}
		//}
	}


	
	/*
	 *   ************************************ Private Methods ******************************************
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
