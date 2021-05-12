package hp.Seals.PrintheadDetailsApiDbTest;

import java.util.*;
import org.apache.log4j.*;
import org.json.*;
import org.testng.asserts.SoftAssert;
import com.amazonaws.samples.PostgreSqlConnection;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.APIPath;
import io.restassured.response.Response;

import printheadDetails.warrantyStatus.*;

import utils.ExtentReportListener;
import utils.FileandEnv;
import utils.UtilityApiMethods;


public class APIVerificationWithDbComparePage extends ExtentReportListener
{
	final static Logger logger = LogManager.getLogger(APIVerificationWithDbComparePage.class);

	static PostgreSqlConnection objSql = new PostgreSqlConnection();
	static PostgreSqlConnection_Db objSqlDb = new PostgreSqlConnection_Db();

	static SoftAssert softAssert = new SoftAssert();

	// public methods

	//=============================PrintHeadDetails API==============================================================================
	/*
	 *  ***************************** PrintHeadDetails API ************************************************
	 * Verify 'status' value based on PH_Serial_no from getPrintHeadDetails API
	 */
	public static List<PrintheadDetailsResultDb>  validateStatusFromPrintHeadDetailsAPI( Response response,  String serial_no, String product_no , String start_ts,String end_ts ) 
	{

		List<PrintheadDetailsResultDb> listObj = new ArrayList<PrintheadDetailsResultDb>();
		if(response.getStatusCode()== 400) {
			logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 
			test.log(Status.FAIL,"##############################################################################");
		} 
		else {
			String resultString = response.getBody().asString();       // APi is calling
			JSONObject inputJSONObject = null;

			int countApi = 0;
			try {

				inputJSONObject = new JSONObject(resultString);
				//logger.info("inputJSONObject=> "+ inputJSONObject);

				if(resultString == null) {
					logger.info("FAILed: API has 400 bad request ");
					test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				
					test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("printhead");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'printhead' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'printhead' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" 'printhead' Count From API==> " + countApi);
						test.log(Status.PASS, " 'printhead'  Object Count From API= " + countApi );

						List db_statusValue = objSqlDb.getStatus_PrintheadDetailsDB(serial_no,product_no,start_ts,end_ts);
						int countFromDB = db_statusValue.size();						
						//int countFromDB = countObjectFrom_PhDetailsDB(serial_no,product_no,start_ts,end_ts);
						logger.info(" 'printhead' Count From DB==> " + countFromDB);
						test.log(Status.PASS, " 'printhead' Object Count From DB= " + countFromDB );

						if(countApi == countFromDB) {
							test.log(Status.PASS,MarkupHelper.createLabel("*** PASS ***=> Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are  matched ",ExtentColor.GREEN));
							logger.info("***PASS=> Object count is matched  from API and Seals_DB =====> \n");

						} else {
							logger.info("****FAIL => Object count is not matched  from API and DB ********\n");
							logger.info("****FAIL => 'printhead' Array object is empty from API Response ******\n");
							test.log(Status.FAIL,MarkupHelper.createLabel(" 'printhead' Array object is empty from API Response ",ExtentColor.ORANGE) );
							test.log(Status.FAIL, "Thus, 'warranty_status' is not available in API " );
							test.log(Status.FAIL," Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are not matched *** ");
							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API  *************** ",ExtentColor.BROWN) );
							test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
									APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

							// Write query.
							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
							test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));
							test.log(Status.FAIL,"##############################################################################");
						}

					} else {
						// API is calling
						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							PrintheadDetailsResultDb objPrintDetails = new PrintheadDetailsResultDb();
							String ph_srNo = jsonArray.getJSONObject(i).getString("printhead_serial_number");     //printhead_serial_number
							//logger.info("ph_srNo_Value======== " + ph_srNo);

							objPrintDetails.setPh_serial_no(ph_srNo);
							//logger.info("object of ph_srNo_Value=> " + objPrintDetails);				

							JSONArray jsonArray2 = jsonArray.getJSONObject(i).getJSONArray("warranty_status");      //"warranty_status"

							JSONObject printheadObj = null;

							for(int j = 0 ; j < jsonArray2.length(); j++)  
							{		        	 
								printheadObj = jsonArray2.getJSONObject(j);

								String statusValue = printheadObj.getString("status");  // status			
								objPrintDetails.setStatus(statusValue);								
								//logger.info("Object  values  => " + objPrintDetails);

							} //End of Inner for loop

							listObj.add(objPrintDetails);

							//list = new ArrayList<String>();

						} // End of Outer for loop 

					} // Inner END IF

				} // Outer END IF
			} catch (JSONException e) {

				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE

		//logger.info("\n********* New List values ********* \n" + listObj );
		return listObj;

	} // End Method

	public static int countObjectFrom_PhDetailsDB(String serial_no,String product_no, String start_ts, String end_ts)
	{
		//test.log(Status.PASS, "No of Key value count from Database ====> " + objSqlDb.countStatus_PrintheadDetailsDB(serial_no,product_no,start_ts,end_ts).getSum());
		return objSqlDb.countStatus_PrintheadDetailsDB(serial_no,product_no,start_ts,end_ts).getSum();
	}

	/*
	 * Compare the 'status' value based on printhead_serial_no from API vs DB ***************************
	 */
	@SuppressWarnings("unchecked")
	public static void CompareStatusFromPrintHeadDetailsAPIvsDB(Response response, String serial_no, String product_no , String start_ts,String end_ts, List serialNoList )
	{

		logger.info("\n***Compare the 'status' value based on printhead_serial_no from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("***Compare the 'status' value based on printhead_serial_no from API response and Database ***",ExtentColor.BROWN) );
		try {
			if(response.getStatusCode() == 400) {

				logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
				test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );

				test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
						APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

			} else {
				List<PrintheadDetailsResultDb> api_statusValue = validateStatusFromPrintHeadDetailsAPI( response,serial_no,product_no,start_ts,end_ts );
				int statusCount_API = api_statusValue.size();
				Collections.sort(api_statusValue, new SortByPhSerialNoWithStatus() );			

				List<PrintheadDetailsResultDb> db_statusValue = objSqlDb.getStatus_PrintheadDetailsDB(serial_no,product_no,start_ts,end_ts);
				int statusCount_DB = db_statusValue.size();
				Collections.sort(db_statusValue, new SortByPhSerialNoWithStatus() );	

				if(statusCount_API > 0) {
					logger.info("'status' count from API Response = " + statusCount_API);
					logger.info("'status' count from Database = " + statusCount_DB );
					test.log(Status.PASS,"'status'  count from API Response = " + statusCount_API);
					test.log(Status.PASS,"'status'  count from Seals_DB = " + statusCount_DB );

					if( statusCount_API == statusCount_DB ) {

						test.log(Status.PASS,MarkupHelper.createLabel("*** PASS=> 'status' count is matched  from API and Seals_DB =====>", ExtentColor.CYAN));
						logger.info("***PASS=> 'status' count is matched  from API and Seals_DB =====> \n");

						logger.info(" 'status' value from API => \n" + api_statusValue );
						test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'status' value from API =====> ", ExtentColor.GREY) );
						test.log(Status.PASS,"" + api_statusValue);

						logger.info(" 'status' value from Seals_DB => \n" + db_statusValue );
						test.log(Status.PASS, MarkupHelper.createLabel("Below  is   'status' value from Seals_DB =====> ", ExtentColor.PINK) );
						test.log(Status.PASS,""+ db_statusValue);

						if(api_statusValue.containsAll(db_statusValue)) {

							test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'status' value is matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
							logger.info("*** PASS=>'status' value is matched  from API and DB ***\n" );

						} else {
							//softAssert.assertEquals(api_statusValue, db_statusValue, "'status' value is matched");
							test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'status' value is not matched from API and Seals_DB *** ",ExtentColor.RED) );
							logger.info("*** FAIL =>'status' value is not matched  from API and DB *** \n");				

							test.log(Status.FAIL, MarkupHelper.createLabel(" Below  is  'status' value from API =====> ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + api_statusValue);

							test.log(Status.FAIL, MarkupHelper.createLabel("Below  is   'status' value from Seals_DB =====> ", ExtentColor.RED) );
							test.log(Status.FAIL,""+ db_statusValue);

							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );
							test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
									APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

							//  query.
							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));

							serialNoList.add(serial_no);
							test.log(Status.FAIL,"##############################################################################");
						}

					} else {
						logger.info(" FAIL => 'status' count is not matched  from API and DB =====> \n");
						test.log(Status.FAIL, MarkupHelper.createLabel("'status'  count from API Response = " + statusCount_API,ExtentColor.RED));
						test.log(Status.FAIL, MarkupHelper.createLabel("'status'  count from Seals_DB = " + statusCount_DB, ExtentColor.RED) );

						test.log(Status.FAIL,MarkupHelper.createLabel("FAIL=> 'status' count from API=  " + statusCount_API + " and From DB= "  + statusCount_DB + " is not matched ", ExtentColor.RED));

						test.log(Status.FAIL,"******** Refer below API Response ********** " );
						test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
								APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

						// Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL QueryResult ***************** ",ExtentColor.RED) );
						test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));

						serialNoList.add(serial_no);
						test.log(Status.FAIL,"##############################################################################");
					} 
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.INFO,MarkupHelper.createLabel("*********************** Execution  Completed  for  One Set of DATA  ********************** ",ExtentColor.ORANGE) );
	} // End Method

	// End Status Comparision from GetPrintHeadDetails API and DB

	/*
	 *  ***************************** PrintHeadDetails API ************************************************
	 * Verify 'start_Timestamp' value based on PH_Serial_no from getPrintHeadDetails API
	 * 
	 * Compare the 'start-ts' value based on printhead_serial_no from API vs DB ***************************
	 */
	public static List<PhDetailsApiStartTsResultDb>  validateStartTSfromPrintHeadDetailsAPI( Response response,  String serial_no, String product_no , String start_ts,String end_ts ) 
	{

		List<PhDetailsApiStartTsResultDb> listObj = new ArrayList<PhDetailsApiStartTsResultDb>();
		if(response.getStatusCode()== 400) {
			logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 
			test.log(Status.FAIL, "################################################################################");
		} 
		else {
			String resultString = response.getBody().asString();       // APi is calling
			JSONObject inputJSONObject = null;

			int countApi = 0;
			try {

				inputJSONObject = new JSONObject(resultString);
				//logger.info("inputJSONObject=> "+ inputJSONObject);

				if(resultString == null) {
					logger.info("FAILed: API has 400 bad request ");
					test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				
					test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 
					test.log(Status.FAIL, "################################################################################");

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("printhead");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'printhead' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'printhead' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" 'printhead' Count From API==> " + countApi);
						test.log(Status.PASS, " 'printhead'  Object Count From API= " + countApi );

						List db_statusValue = objSqlDb.getStatus_PrintheadDetailsDB(serial_no,product_no,start_ts,end_ts);
						int countFromDB = db_statusValue.size();						
						//int countFromDB = countObjectFrom_PhDetailsDB(serial_no,product_no,start_ts,end_ts);
						logger.info(" 'printhead' Count From DB==> " + countFromDB);
						test.log(Status.PASS, " 'printhead' Object Count From DB= " + countFromDB );

						if(countApi == countFromDB) {
							test.log(Status.PASS,MarkupHelper.createLabel("*** PASS ***=> Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are  matched ",ExtentColor.GREEN));
							logger.info("***PASS=> Object count is matched  from API and Seals_DB =====> \n");

						} else {
							logger.info("****FAIL => Object count is not matched  from API and DB ********\n");
							logger.info("****FAIL => 'printhead' Array object is empty from API Response ******\n");
							test.log(Status.FAIL,MarkupHelper.createLabel(" 'printhead' Array object is empty from API Response ",ExtentColor.ORANGE) );
							test.log(Status.FAIL, "Thus, 'warranty_status' is not available in API " );
							test.log(Status.FAIL," Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are not matched *** ");
							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API  *************** ",ExtentColor.BROWN) );
							test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
									APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

							// Write query.
							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
							test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));
							test.log(Status.FAIL, "################################################################################");
						}

					} else {
						// API is calling
						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							PhDetailsApiStartTsResultDb startTsObj = new PhDetailsApiStartTsResultDb();
							String ph_srNo = jsonArray.getJSONObject(i).getString("printhead_serial_number");     //printhead_serial_number
							//logger.info("ph_srNo_Value======== " + ph_srNo);

							startTsObj.setPh_serial_no(ph_srNo);
							//logger.info("object of ph_srNo_Value=> " + objPrintDetails);				

							JSONArray jsonArray2 = jsonArray.getJSONObject(i).getJSONArray("warranty_status");      //"warranty_status"

							JSONObject printheadObj = null;

							for(int j = 0 ; j < jsonArray2.length(); j++)  
							{		        	 
								printheadObj = jsonArray2.getJSONObject(j);

								String startTsValue = printheadObj.getString("start_Timestamp");  // start_Timestamp			
								startTsObj.setStartTimestamp(startTsValue);								
								//logger.info("Object  values  => " + startTsObj);

							} //End of Inner for loop

							listObj.add(startTsObj);

							//	logger.info("\n********* New List values ********* \n" + listObj );
							//list = new ArrayList<String>();

						} // End of Outer for loop 

					} // Inner END IF

				} // Outer END IF
			} catch (JSONException e) {

				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE

		return listObj;

	} // End Method



	/*
	 * Compare the 'start_Timestamp' value based on printhead_serial_no from API vs DB ***************************
	 */
	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	public static void CompareStartTSfromPrintHeadDetailsAPIvsDB(Response response, String serial_no, String product_no , String start_ts,String end_ts, List serialNoList )
	{

		logger.info("\n***Compare the 'start_Timestamp' value based on printhead_serial_no from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("***Compare the 'start_Timestamp' value based on printhead_serial_no from API response and Database ***",ExtentColor.BROWN) );
		try {
			if(response.getStatusCode() == 400) {

				logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
				test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );

				//				test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
				//						APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

			} else {
				List<PhDetailsApiStartTsResultDb> api_startTSvalue = validateStartTSfromPrintHeadDetailsAPI( response,serial_no,product_no,start_ts,end_ts );
				int startTsCount_API = api_startTSvalue.size();
				Collections.sort(api_startTSvalue, new SortByPhSerialNoWithStartTs() );			

				List<PhDetailsApiStartTsResultDb> db_startTSvalue = objSqlDb.getStartTSfromPrintheadDetailsDB(serial_no,product_no,start_ts,end_ts);
				int startTsCount_DB = db_startTSvalue.size();
				Collections.sort(db_startTSvalue, new SortByPhSerialNoWithStartTs() );	

				if(startTsCount_API > 0) {
					logger.info("'start_Timestamp' count from API Response = " + startTsCount_API);
					logger.info("'start_Timestamp' count from Database = " + startTsCount_DB );
					test.log(Status.PASS,"'start_Timestamp'  count from API Response = " + startTsCount_API);
					test.log(Status.PASS,"'start_Timestamp'  count from Seals_DB = " + startTsCount_DB );

					if( startTsCount_API == startTsCount_DB ) {

						test.log(Status.PASS,MarkupHelper.createLabel("*** PASS=> 'start_Timestamp' count is matched  from API and Seals_DB =====>", ExtentColor.CYAN));
						logger.info("***PASS=> 'start_Timestamp' count is matched  from API and Seals_DB =====> \n");

						logger.info(" 'start_Timestamp' value from API => \n" + api_startTSvalue );
						test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'start_Timestamp' value from API =====> ", ExtentColor.GREY) );
						test.log(Status.PASS,"" + api_startTSvalue);

						logger.info(" 'start_Timestamp' value from Seals_DB => \n" + db_startTSvalue );
						test.log(Status.PASS, MarkupHelper.createLabel("Below  is   'start_Timestamp' value from Seals_DB =====> ", ExtentColor.PINK) );
						test.log(Status.PASS,"" + db_startTSvalue);

						if(api_startTSvalue.contains(db_startTSvalue)) {

							test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'start_Timestamp' value is matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
							logger.info("*** PASS=>'start_Timestamp' value is matched  from API and DB ***\n" );

						} else {
							logger.info("*** FAIL =>'start_Timestamp' value is not matched  from API and DB *** \n");

							test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'start_Timestamp' value is not matched from API and Seals_DB *** ",ExtentColor.RED) );
							test.log(Status.FAIL, MarkupHelper.createLabel(" Below  is  'start_Timestamp' value from API =====> ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + api_startTSvalue);

							test.log(Status.FAIL, MarkupHelper.createLabel("Below  is   'start_Timestamp' value from Seals_DB =====> ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + db_startTSvalue);										

							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );
							test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
									APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

							//  query.
							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));

							serialNoList.add(serial_no);
							test.log(Status.FAIL, "################################################################################");
						}

					} else {
						logger.info(" FAIL => 'start_Timestamp' count is not matched  from API and DB =====> \n");

						test.log(Status.FAIL,MarkupHelper.createLabel("FAIL=> 'start_Timestamp' count from API=  " 
								+ startTsCount_API + " and From DB= "  + startTsCount_DB + " is not matched ", ExtentColor.RED));

						test.log(Status.FAIL,"'start_Timestamp'  count from API Response = " + startTsCount_API);
						test.log(Status.FAIL,"'start_Timestamp'  count from Seals_DB = " + startTsCount_DB );					

						test.log(Status.FAIL,"******** Refer below API Response ********** " );
						test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
								APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

						// Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL QueryResult ***************** ",ExtentColor.RED) );
						test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));

						serialNoList.add(serial_no);
						test.log(Status.FAIL, "################################################################################");
					} 
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.INFO,MarkupHelper.createLabel("*********************** Execution  Completed  for  One Set of DATA  ********************** ",ExtentColor.ORANGE) );
	} // End Method


	/*
	 *  ***************************** PrintHeadDetails API ************************************************
	 * Verify 'end_Timestamp' value based on PH_Serial_no from getPrintHeadDetails API
	 *  Compare with 'end-ts' value from DB ***************************
	 */
	@SuppressWarnings("rawtypes")
	public static List<PhDetailsApiEndTsResultDb>  validateEndTSfromPrintHeadDetailsAPI( Response response,  String serial_no, String product_no , String start_ts,String end_ts ) 
	{
		List<PhDetailsApiEndTsResultDb> listObj = new ArrayList<PhDetailsApiEndTsResultDb>();

		if(response.getStatusCode()== 400) {
			logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 

		} 
		else {
			String resultString = response.getBody().asString();       // APi is calling
			JSONObject inputJSONObject = null;

			int countApi = 0;
			try {

				inputJSONObject = new JSONObject(resultString);
				//logger.info("inputJSONObject=> "+ inputJSONObject);

				if(resultString == null) {
					logger.info("FAILed: API has 400 bad request ");
					test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				
					test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 

					test.log(Status.FAIL, "################################################################################");
				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("printhead");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'printhead' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'printhead' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" 'printhead' Count From API==> " + countApi);
						test.log(Status.PASS, " 'printhead'  Object Count From API= " + countApi );

						List db_statusValue = objSqlDb.getStatus_PrintheadDetailsDB(serial_no,product_no,start_ts,end_ts);
						int countFromDB = db_statusValue.size();						
						//int countFromDB = countObjectFrom_PhDetailsDB(serial_no,product_no,start_ts,end_ts);
						logger.info(" 'printhead' Count From DB==> " + countFromDB);
						test.log(Status.PASS, " 'printhead' Object Count From DB= " + countFromDB );

						if(countApi == countFromDB) {
							test.log(Status.PASS,MarkupHelper.createLabel("*** PASS ***=> Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are  matched ",ExtentColor.GREEN));
							logger.info("***PASS=> Object count is matched  from API and Seals_DB =====> \n");

						} else {
							logger.info("****FAIL => Object count is not matched  from API and DB ********\n");
							logger.info("****FAIL => 'printhead' Array object is empty from API Response ******\n");
							test.log(Status.FAIL," Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are not matched *** ");
							test.log(Status.FAIL,MarkupHelper.createLabel(" 'printhead' Array object is empty from API Response ",ExtentColor.ORANGE) );
							test.log(Status.FAIL, "Thus, 'warranty_status' is not available in API " );

							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API  *************** ",ExtentColor.BROWN) );
							test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
									APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

							// Write query.
							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
							test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));
							test.log(Status.FAIL, "################################################################################");
						}

					} else {
						// API is calling
						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							PhDetailsApiEndTsResultDb endTsObj = new PhDetailsApiEndTsResultDb();
							String ph_srNo = jsonArray.getJSONObject(i).getString("printhead_serial_number");     //printhead_serial_number
							//logger.info("ph_srNo_Value======== " + ph_srNo);

							endTsObj.setPh_serial_no(ph_srNo);
							//logger.info("object of ph_srNo_Value=> " + objPrintDetails);				

							JSONArray jsonArray2 = jsonArray.getJSONObject(i).getJSONArray("warranty_status");      //"warranty_status"

							JSONObject printheadObj = null;

							for(int j = 0 ; j < jsonArray2.length(); j++)  
							{		        	 
								printheadObj = jsonArray2.getJSONObject(j);

								String endTsValue = printheadObj.getString("end_Timestamp");  // end_Timestamp			
								endTsObj.setEnd_ts(endTsValue);								
								//logger.info("Object  values  => " + endTsObj);

							} //End of Inner for loop

							listObj.add(endTsObj);

							//	logger.info("\n********* New List values ********* \n" + listObj );
							//list = new ArrayList<String>();

						} // End of Outer for loop 

					} // Inner END IF

				} // Outer END IF
			} catch (JSONException e) {

				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE

		return listObj;

	} // End Method



	/*
	 * Compare the 'end_Timestamp' value based on printhead_serial_no from API vs DB ***************************
	 */
	@SuppressWarnings("unchecked")
	public static void CompareEndTSfromPrintHeadDetailsAPIvsDB(Response response, String serial_no, String product_no , String start_ts,String end_ts, List serialNoList )
	{

		logger.info("\n***Compare the 'end_Timestamp' value based on printhead_serial_no from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("***Compare the 'end_Timestamp' value based on printhead_serial_no from API response and Database ***",ExtentColor.BROWN) );
		try {
			if(response.getStatusCode() == 400) {

				logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
				test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );

				//				test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
				//						APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

			} else {
				List<PhDetailsApiEndTsResultDb> api_endTSvalue = validateEndTSfromPrintHeadDetailsAPI( response,serial_no,product_no,start_ts,end_ts );
				int endTsCount_API = api_endTSvalue.size();
				Collections.sort(api_endTSvalue, new SortByPhSerialNoWithEndTs() );			

				List<PhDetailsApiEndTsResultDb> db_endTSvalue = objSqlDb.getEndTSfromPrintheadDetailsDB(serial_no,product_no,start_ts,end_ts);
				int endTsCount_DB = db_endTSvalue.size();
				Collections.sort(db_endTSvalue, new SortByPhSerialNoWithEndTs() );	

				if(endTsCount_API > 0) {
					logger.info("'end_Timestamp' count from API Response = " + endTsCount_API);
					logger.info("'end_Timestamp' count from Database = " + endTsCount_DB );
					test.log(Status.PASS,"'end_Timestamp'  count from API Response = " + endTsCount_API);
					test.log(Status.PASS,"'end_Timestamp'  count from Seals_DB = " + endTsCount_DB );

					if( endTsCount_API == endTsCount_DB ) {

						test.log(Status.PASS,MarkupHelper.createLabel("*** PASS=> 'end_Timestamp' count is matched  from API and Seals_DB =====>", ExtentColor.CYAN));
						logger.info("***PASS=> 'end_Timestamp' count is matched  from API and Seals_DB =====> \n");

						logger.info(" 'end_Timestamp' value from API => \n" + api_endTSvalue );
						test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'end_Timestamp' value from API =====> ", ExtentColor.GREY) );
						test.log(Status.PASS,"" + api_endTSvalue);

						logger.info(" 'end_Timestamp' value from Seals_DB => \n" + db_endTSvalue );
						test.log(Status.PASS, MarkupHelper.createLabel("Below  is   'end_Timestamp' value from Seals_DB =====> ", ExtentColor.PINK) );
						test.log(Status.PASS,"" + db_endTSvalue);

						if(api_endTSvalue.contains(db_endTSvalue)) {

							test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'end_Timestamp' value is matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
							logger.info("*** PASS=>'end_Timestamp' value is matched  from API and DB ***\n" );

						} else {

							test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'end_Timestamp' value is not matched from API and Seals_DB *** ",ExtentColor.RED) );
							logger.info("*** FAIL =>'end_Timestamp' value is not matched  from API and DB *** \n");				

							test.log(Status.FAIL, MarkupHelper.createLabel(" Below  is  'end_Timestamp' value from API =====> ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + api_endTSvalue);

							test.log(Status.FAIL, MarkupHelper.createLabel("Below  is   'end_Timestamp' value from Seals_DB =====> ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + db_endTSvalue);

							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );
							test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
									APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

							//  query.
							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));

							serialNoList.add(serial_no);
							test.log(Status.FAIL, "################################################################################");
						}

					} else {
						logger.info(" FAIL => 'end_Timestamp' count is not matched  from API and DB =====> \n");

						test.log(Status.FAIL,"'end_Timestamp'  count from API Response = " + endTsCount_API);
						test.log(Status.FAIL,"'end_Timestamp'  count from Seals_DB = " + endTsCount_DB );
						test.log(Status.FAIL,MarkupHelper.createLabel("FAIL=> 'end_Timestamp' count from API=  " + endTsCount_API + " and From DB= "  + endTsCount_DB + " is not matched ", ExtentColor.RED));

						test.log(Status.FAIL,"******** Refer below API Response ********** " );
						test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
								APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts )); 

						// Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL QueryResult ***************** ",ExtentColor.RED) );
						test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));

						serialNoList.add(serial_no);
						test.log(Status.FAIL, "################################################################################");
					} 
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.INFO,MarkupHelper.createLabel("*********************** Execution  Completed  for  One Set of DATA  ********************** ",ExtentColor.ORANGE) );

	} // End Method

	@SuppressWarnings("unchecked")
	public static void comparePhSrNoValuesOfPrintheadEvent(Response response,String listObj, String key, String sn, String pn,String start_ts,String end_ts , List  serialNoList ) //throws Throwable 
	{
		logger.info("\n***Compare 'printhead_serial_number' values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel( "****** Compare 'printhead_serial_number' values from API response and Database *******", ExtentColor.CYAN ));

		try {
			if(response.getStatusCode() == 400) {

				logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
				test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );

			} else {

				List<String> listApiVal =  UtilityApiMethods.verifyKeyValueFromResponse(response, listObj, key);
				Collections.sort(listApiVal);  //, new SortByPrintHeadSrNo() );
				//logger.info("From API, Sorted List values are:\n" + listApiVal);
				//test.log(Status.PASS,"From API 'printhead_serial_number' values =>> " + listApiVal);

				List<String>  listDbVal = objSqlDb.getPhSrNoFromPrintheadDetailsDB(sn, pn , start_ts,end_ts );
				Collections.sort(listDbVal);  //, new SortByPrintHeadSrNo() );
				//logger.info("From DB, Sorted List values are:\n" + listDbVal);
				//test.log(Status.PASS,"From Database 'printhead_serial_number' values =>> " + listDbVal);


				if( listDbVal == null &&  listApiVal == null ) {
					logger.info("'printhead_serial_number' object is Empty From API Response and DB " );
					test.log(Status.PASS, "'printhead_serial_number' object is Empty From API Response and DB ");
				} 
				else if( listDbVal != null &&  listApiVal != null && listDbVal.size() == listApiVal.size() ) {
					logger.info("From  DB," + key + "  Count= " + listDbVal.size() );
					logger.info("From API, " + key + "Count= " + listApiVal.size());
					test.log(Status.PASS, " From DB, " + key + " Count= " + listDbVal.size() );
					test.log(Status.PASS, "From API, " + key + " Count= " + listApiVal.size() );

					logger.info("\n 'printhead_serial_number' objects count are Same From API Response and DB \n" );
					test.log(Status.PASS,"'printhead_serial_number' objects count are Same From API Response and DB " );

					logger.info("'printhead_serial_number' values From Database =>> " + listDbVal);
					test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'printhead_serial_number' values from DB =====> ", ExtentColor.GREY) );
					test.log(Status.PASS,"" + listDbVal);

					logger.info("'printhead_serial_number' value From API Response =>> " + listApiVal );
					test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'printhead_serial_number' values from API =====> ", ExtentColor.GREY) );
					test.log(Status.PASS,"" + listApiVal);				

					if(listApiVal.equals(listDbVal)) {

						test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'printhead_serial_number' values are matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
						logger.info("*** PASS=>'printhead_serial_number' values are matched  from API and DB ***\n" );

					} else {

						test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'printhead_serial_number' value are not matched from API and Seals_DB *** ",ExtentColor.RED) );
						logger.info("*** FAIL =>'printhead_serial_number' values are not matched  from API and DB *** \n");				

						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

						test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
								APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),sn,pn,start_ts,end_ts )); 

						// refer query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
						test.log(Status.FAIL,"" + queryForPhSerialNoForPrintHeadEvent(sn,pn,start_ts,end_ts) );

						serialNoList.add(sn);
						test.log(Status.FAIL, "################################################################################");

					}				

				} else {
					logger.info("From  DB," + key + " Count= " + listDbVal.size() );
					logger.info("From API, " + key + " Count= " + listApiVal.size() );
					logger.info("\n 'printhead_serial_number'  count is not same From API Response and DB \n" );

					test.log(Status.FAIL, "From  DB, " + key + " Count= " + listDbVal.size() );
					test.log(Status.FAIL, "From API, " + key + " Count= " + listApiVal.size() );
					test.log(Status.FAIL,"'printhead_serial_number' Objects count is not Same From API Response and DB " );

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn, pn , start_ts,end_ts )); 

					logger.info("API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn ,pn ,start_ts,end_ts )); 
					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + queryForPhSerialNoForPrintHeadEvent(sn,pn,start_ts,end_ts) );

					serialNoList.add(sn);
					test.log(Status.FAIL, "################################################################################");

				}
			}

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.PASS, MarkupHelper.createLabel("******************************************* END ********************************************** ",ExtentColor.ORANGE));
	}

	/*
	 *  getting values of 'ink_Used' and 'ph_serial_no' value from API *************************** 
	 */
	public static List<InkUsedValuesResultsPojo> getInk_UsedFromPrintheadDetailsAPI( Response response,  String serial_no, String product_no , String start_ts,String end_ts ) 
	{
		List<InkUsedValuesResultsPojo> listObj = null;
		InkUsedValuesResultsPojo objApi = null;
		if(response.getStatusCode()== 400) {
			logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 

		} 
		else {
			String resultString = response.getBody().asString();       // APi is calling
			JSONObject inputJSONObject = null;

			int countApi = 0;
			try {

				inputJSONObject = new JSONObject(resultString);
				//logger.info("inputJSONObject=> "+ inputJSONObject);

				if(resultString == null) {
					logger.info("FAILed: API has 400 bad request ");
					test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				
					test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("printhead");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'printhead' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'printhead' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" 'printhead' Count From API==> " + countApi);
						test.log(Status.PASS, " 'printhead'  Object Count From API= " + countApi );


					} else {
						// API is calling
						listObj = new ArrayList<InkUsedValuesResultsPojo>();
						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							objApi = new InkUsedValuesResultsPojo();
							String ph_srNo = jsonArray.getJSONObject(i).getString("printhead_serial_number");     //printhead_serial_number
							//logger.info("ph_srNo_Value======== " + ph_srNo);

							objApi.setPh_serial_no(ph_srNo);
							//logger.info("object of ph_srNo_Value=> " + objApi);				
							Float inkUsed = jsonArray.getJSONObject(i).getFloat("ink_Used");		
							objApi.setInk_used(Float.valueOf(inkUsed));
							//logger.info("Object  values  => " + objApi);

							listObj.add(objApi);

							//logger.info("\n********* New List values ********* \n" + listObj );				
						} // End of for loop 

					} // Inner END IF

				} // Outer END IF
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE
		logger.info("\n********* New List values ********* \n" + listObj );
		return listObj;

	} // End Method

	// ************** Compare field of  'ink_Used' based on 'printhead_serial_number' from API with DB ********************************************************

	public static void compareInk_UsedOfPrintHeadDetailsFromApiAndDB (Response response, String key, String sn, String pn ,String start_ts, String end_ts, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare '" + key +"' Value based on 'id' and 'date' from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("****** Compare '" + key +"' value from API response and Database *******",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");

				serialNoList.add(sn);

				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed serial numbers==>>  " + serialNoList );
				test.log(Status.FAIL,"*****************************************************************************************************");

			} else {
				List<InkUsedValuesResultsPojo> apiList = getInk_UsedFromPrintheadDetailsAPI(response, sn, pn , start_ts,  end_ts  );

				List<InkUsedValuesResultsPojo> dbList = objSqlDb.getInk_UsedFromPrintheadDetailsDB( sn, pn, start_ts , end_ts);				

				if( dbList.size() == 0 &&  apiList.size() == 0 ) {
					logger.info("'printhead' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'printhead' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'printhead' object is Empty, so Count= " + apiList.size() );
					test.log(Status.WARNING, "From  DB, 'printhead' object is Empty, so Count= " + dbList.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbList.size() > 0 &&  apiList.size() > 0 && dbList.size() == apiList.size() ) {
					for(int i = 0 ; i < apiList.size() ; i++) {
						InkUsedValuesResultsPojo  apiObj = dbList.get(i);
						InkUsedValuesResultsPojo  dbObj = apiList.get(i);

						comparePrintHeadFields("printhead_serial_number",apiObj.getPh_serial_no(), dbObj.getPh_serial_no(), sn, pn,start_ts , end_ts, serialNoList, i+1);
						comparePrintHeadFields("ink_Used",apiObj.getInk_used(), dbObj.getInk_used() , sn, pn, start_ts , end_ts, serialNoList, i+1);

					} // End if
				} else {
					logger.info("'" + key + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key + "' count is " + dbList.size() );
					test.log(Status.FAIL,"From API, '" + key + "' count is " + apiList.size() );
					test.log(Status.FAIL,"'" + key + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn, pn , start_ts,end_ts )); 

					logger.info("API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn ,pn ,start_ts,end_ts )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					//test.log(Status.FAIL,"" + MaintenanceTasks_ListMaintenancesDB(sn,pn,date) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("*******************************END of Execution ************************************************************************");

				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	


	/*
	 *************** getting values of ph_serial_no' and 'time_Used' value from API *********************** 
	 */
	public static List<TimeUsedValuesPojo> getTime_UsedFromPrintheadDetailsAPI( Response response,  String serial_no, String product_no , String start_ts,String end_ts ) 
	{
		List<TimeUsedValuesPojo> listObj = null;
		TimeUsedValuesPojo objApi = null;
		if(response.getStatusCode()== 400) {
			logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 

		} 
		else {
			String resultString = response.getBody().asString();       // APi is calling
			JSONObject inputJSONObject = null;

			int countApi = 0;
			try {
				inputJSONObject = new JSONObject(resultString);
				//logger.info("inputJSONObject=> "+ inputJSONObject);

				if(resultString == null) {
					logger.info("FAILed: API has 400 bad request ");
					test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				
					test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("printhead");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'printhead' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'printhead' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" 'printhead' Count From API==> " + countApi);
						test.log(Status.PASS, " 'printhead'  Object Count From API= " + countApi );

					} else {
						// API is calling
						listObj = new ArrayList<TimeUsedValuesPojo>();
						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							objApi = new TimeUsedValuesPojo();
							String ph_srNo = jsonArray.getJSONObject(i).getString("printhead_serial_number");     //printhead_serial_number

							objApi.setPh_serial_no(ph_srNo);
							//logger.info("object of ph_srNo_Value=> " + objApi);				
							int timeUsed = jsonArray.getJSONObject(i).getInt("time_Used");		
							objApi.setTime_used(Integer.valueOf(timeUsed));
							//logger.info("Object  values  => " + objApi);

							listObj.add(objApi);

							//logger.info("\n********* New List values ********* \n" + listObj );				
						} // End of for loop 

					} // Inner END IF

				} // Outer END IF
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE
		logger.info("\n********* New List values ********* \n" + listObj );
		return listObj;

	} // End Method

	// ************** Compare field of  'time_Used' based on 'printhead_serial_number' from API with DB ********************************************************

	public static void compareTime_UsedOfPrintHeadDetailsFromApiAndDB (Response response, String key, String sn, String pn ,String start_ts, String end_ts, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare '" + key +"' Value based on 'id' and 'date' from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("****** Compare '" + key +"' value from API response and Database *******",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");

				serialNoList.add(sn);

				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed serial numbers==>>  " + serialNoList );
				test.log(Status.FAIL,"*****************************************************************************************************");

			} else {
				List<TimeUsedValuesPojo> apiList = getTime_UsedFromPrintheadDetailsAPI(response, sn, pn , start_ts,  end_ts  );

				List<TimeUsedValuesPojo> dbList = objSqlDb.getTime_UsedFromPrintheadDetailsDB( sn, pn, start_ts , end_ts);				

				if( dbList.size() == 0 &&  apiList.size() == 0 ) {
					logger.info("'printhead' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'printhead' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'printhead' object is Empty, so Count= " + apiList.size() );
					test.log(Status.WARNING, "From  DB, 'printhead' object is Empty, so Count= " + dbList.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbList.size() > 0 &&  apiList.size() > 0 && dbList.size() == apiList.size() ) {
					for(int i = 0 ; i < apiList.size() ; i++) {
						TimeUsedValuesPojo  apiObj = dbList.get(i);
						TimeUsedValuesPojo  dbObj = apiList.get(i);

						comparePrintHeadFields("printhead_serial_number",apiObj.getPh_serial_no(), dbObj.getPh_serial_no(), sn, pn,start_ts , end_ts, serialNoList, i+1);
						comparePrintHeadFields("time_Used",apiObj.getTime_used(), dbObj.getTime_used() , sn, pn, start_ts , end_ts, serialNoList, i+1);

					} // End if
				} else {
					logger.info("'" + key + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key + "' count is " + dbList.size() );
					test.log(Status.FAIL,"From API, '" + key + "' count is " + apiList.size() );
					test.log(Status.FAIL,"'" + key + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn, pn , start_ts,end_ts )); 

					logger.info("API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn ,pn ,start_ts,end_ts )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					//test.log(Status.FAIL,"" + MaintenanceTasks_ListMaintenancesDB(sn,pn,date) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("*******************************END of Execution ************************************************************************");

				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	
				

	
	/*
	 *  getting values of ph_serial_no' and 'color' value from API *************************** 
	 */
	public static List<ColorValuesResultPojo> getColorFromPrintheadDetailsAPI( Response response,  String serial_no, String product_no , String start_ts,String end_ts ) 
	{
		List<ColorValuesResultPojo> listObj = null;
		ColorValuesResultPojo objApi = null;
		if(response.getStatusCode()== 400) {
			logger.info("400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + start_ts + "  and endDate= " + end_ts );
			test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 

		} 
		else {
			String resultString = response.getBody().asString();       // APi is calling
			JSONObject inputJSONObject = null;

			int countApi = 0;
			try {

				inputJSONObject = new JSONObject(resultString);
				//logger.info("inputJSONObject=> "+ inputJSONObject);

				if(resultString == null) {
					logger.info("FAILed: API has 400 bad request ");
					test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				
					test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),serial_no,product_no,start_ts,end_ts ) ); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("printhead");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'printhead' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'printhead' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" 'printhead' Count From API==> " + countApi);
						test.log(Status.PASS, " 'printhead'  Object Count From API= " + countApi );

					} else {
						// API is calling
						listObj = new ArrayList<ColorValuesResultPojo>();
						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							objApi = new ColorValuesResultPojo();
							String ph_srNo = jsonArray.getJSONObject(i).getString("printhead_serial_number");     //printhead_serial_number
							objApi.setPh_serial_no(ph_srNo);
							//logger.info("object of ph_srNo_Value=> " + objApi);				
							String colorVal = jsonArray.getJSONObject(i).getString("color");		
							objApi.setColor(colorVal);
							//logger.info("Object  values  => " + objApi);

							listObj.add(objApi);

							//logger.info("\n********* New List values ********* \n" + listObj );				
						} // End of for loop 

					} // Inner END IF

				} // Outer END IF
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE
		logger.info("\n********* New List values ********* \n" + listObj );
		return listObj;

	} // End Method

	// ************** Compare field of  'color' based on 'printhead_serial_number' from API with DB ********************************************************

	public static void compareColorOfPrintHeadDetailsFromApiAndDB (Response response, String key, String sn, String pn ,String start_ts, String end_ts, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare '" + key +"' Value based on 'id' and 'date' from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("****** Compare '" + key +"' value from API response and Database *******",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");

				serialNoList.add(sn);

				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed serial numbers==>>  " + serialNoList );
				test.log(Status.FAIL,"*****************************************************************************************************");

			} else {
				List<ColorValuesResultPojo> apiList = getColorFromPrintheadDetailsAPI(response, sn, pn , start_ts,  end_ts  );

				List<ColorValuesResultPojo> dbList = objSqlDb.getColorFromPrintheadDetailsDB	( sn, pn, start_ts , end_ts);				

				if( dbList.size() == 0 &&  apiList.size() == 0 ) {
					logger.info("'printhead' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'printhead' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'printhead' object is Empty, so Count= " + apiList.size() );
					test.log(Status.WARNING, "From  DB, 'printhead' object is Empty, so Count= " + dbList.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbList.size() > 0 &&  apiList.size() > 0 && dbList.size() == apiList.size() ) {
					for(int i = 0 ; i < apiList.size() ; i++) {
						ColorValuesResultPojo  apiObj = dbList.get(i);
						ColorValuesResultPojo  dbObj = apiList.get(i);

						comparePrintHeadFields("printhead_serial_number",apiObj.getPh_serial_no(), dbObj.getPh_serial_no(), sn, pn,start_ts , end_ts, serialNoList, i+1);
						comparePrintHeadFields("color",apiObj.getColor(), dbObj.getColor() , sn, pn, start_ts , end_ts, serialNoList, i+1);

					} // End if
				} else {
					logger.info("'" + key + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key + "' count is " + dbList.size() );
					test.log(Status.FAIL,"From API, '" + key + "' count is " + apiList.size() );
					test.log(Status.FAIL,"'" + key + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn, pn , start_ts,end_ts )); 

					logger.info("API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn ,pn ,start_ts,end_ts )); 

					// refer query.
					//test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					//test.log(Status.FAIL,"" + MaintenanceTasks_ListMaintenancesDB(sn,pn,date) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("*******************************END of Execution ************************************************************************");

				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	



	//####################### Private Methods for the Api value ###################################################	 
	
	// Comparing int values
	private static void comparePrintHeadFields(String list_printHead_Field, int s1, int s2,
			String sn, String pn, String start_ts, String end_ts, HashSet<String> serialNoList, int iObject) 
	{		
		switch (list_printHead_Field) 
		{ 
			case "printhead_serial_number" : compareObjects(list_printHead_Field,s1,s2, sn,pn,start_ts , end_ts, serialNoList, iObject); break;
			case "time_Used" :  compareObjects(list_printHead_Field, s1, s2, sn, pn, start_ts , end_ts, serialNoList, iObject) ; break;
		}
	}

	private static void compareObjects(String list_printHead_Field, int s1, int s2, String sn, String pn,
			String start_ts, String end_ts, HashSet<String> serialNoList, int iObject) 
	{
		logger.info("From API, '"+ list_printHead_Field + "' field Value = " + s1 + "  and From DB, '" + list_printHead_Field + "'  field Value = " + s2);
		test.log(Status.PASS,"From API->   '" + list_printHead_Field + "'   :   " + s1 );
		test.log(Status.PASS,"From  DB->   '" + list_printHead_Field + "'   :   " + s2 );

		if(s1 == s2) {
			logger.info("PASS==> '" + list_printHead_Field + "' is matched from API and DB ==>\n");
			test.log(Status.PASS,MarkupHelper.createLabel( "PASS=> '" + list_printHead_Field + "' is matched from API and DB ==> ", ExtentColor.GREEN));
		} else {
			serialNoList.add(sn);
			logger.info("Fail=>" + list_printHead_Field + " is not matched from API and DB ==>\n");
			test.log(Status.FAIL, iObject + "th iteration of object '" + list_printHead_Field + "'  is not matched " );
			test.log(Status.FAIL,"From API, " + list_printHead_Field + "  :  " + s1  );
			test.log(Status.FAIL,"From  DB, " + list_printHead_Field + "  :  " + s2  );
			test.log(Status.FAIL, "Fail=> Field '" + list_printHead_Field + "'  is not matched from API and DB ==>for SN=" + sn + "  & PN=" + pn );

			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

			test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn, pn , start_ts,end_ts )); 

			logger.info("API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn ,pn ,start_ts,end_ts )); 
			// refer query.
			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query result*************** ", ExtentColor.RED) );
			//test.log(Status.FAIL,"" + MaintenanceTasks_ListMaintenancesDB(sn,pn,date) );
			test.log(Status.FAIL,"******************************************************************************************");
		}	
		
	}

	// Comparing float values
	private static void comparePrintHeadFields(String list_printHead_Field, Float s1, Float s2, String sn,
			String pn, String start_ts, String end_ts, HashSet<String> serialNoList, int iObject) 
	{
		switch (list_printHead_Field)
		{ 
			case "printhead_serial_number" : compareObjects(list_printHead_Field,s1,s2, sn,pn,start_ts , end_ts, serialNoList, iObject); break;
			case "ink_Used" :  compareObjects(list_printHead_Field, s1, s2, sn, pn, start_ts , end_ts, serialNoList, iObject) ; break;
		}
	}
	// Comparing objects which contains float values
	private static void compareObjects(String list_printHead_Field, Float s1, Float s2, String sn,
			String pn, String start_ts, String end_ts, HashSet<String> serialNoList, int iObject)
	{	
		logger.info("From API, '"+ list_printHead_Field + "' field Value = " + s1 + "  and From DB, '" + list_printHead_Field + "'  field Value = " + s2);
		test.log(Status.PASS,"From API->   '" + list_printHead_Field + "'   :   " + s1 );
		test.log(Status.PASS,"From  DB->   '" + list_printHead_Field + "'   :   " + s2 );

		if(s1 == s2 ) {
			logger.info("PASS==> '" + list_printHead_Field + "' is matched from API and DB ==>\n");
			test.log(Status.PASS,MarkupHelper.createLabel( "PASS=> '" + list_printHead_Field + "' is matched from API and DB ==> ", ExtentColor.GREEN));

		} else {
			serialNoList.add(sn);
			logger.info("Fail=>" + list_printHead_Field + " is not matched from API and DB ==>\n");
			test.log(Status.FAIL, iObject + "th iteration of object '" + list_printHead_Field + "'  is not matched " );
			test.log(Status.FAIL,"From API, " + list_printHead_Field + "  :  " + s1  );
			test.log(Status.FAIL,"From  DB, " + list_printHead_Field + "  :  " + s2  );
			test.log(Status.FAIL, "Fail=> Field '" + list_printHead_Field + "'  is not matched from API and DB ==>for SN=" + sn + "  & PN=" + pn );

			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

			test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn, pn , start_ts,end_ts )); 

			logger.info("API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn ,pn ,start_ts,end_ts )); 
			// refer query.
			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query result*************** ", ExtentColor.RED) );
			//test.log(Status.FAIL,"" + MaintenanceTasks_ListMaintenancesDB(sn,pn,date) );
			test.log(Status.FAIL,"******************************************************************************************");
		}
	}

	// Choose the field and compare the fields value from Api and DB
	private static void comparePrintHeadFields(String list_printHead_Field, String s1, String s2, String sn,String pn, String start_ts ,String end_ts, HashSet<String> serialNoList , int iObject ) 
	{
		switch (list_printHead_Field) {
			case "printhead_serial_number" : compareObjects(list_printHead_Field,s1,s2, sn,pn,start_ts , end_ts, serialNoList, iObject); break;              
			case "color" :  compareObjects(list_printHead_Field,s1,s2, sn,pn,start_ts , end_ts, serialNoList, iObject) ; break;
		}
	}

	// Compare two objects where each objects have list of values
	private static void compareObjects(String list_printHead_Field, String s1, String s2, String sn, String pn, String start_ts , String end_ts, HashSet<String> serialNoList , int iObject) 
	{		
		logger.info("From API, '"+ list_printHead_Field + "' field Value = " + s1 + "  and From DB, '" + list_printHead_Field + "'  field Value = " + s2);
		test.log(Status.PASS,"From API->   '" + list_printHead_Field + "'   :   " + s1 );
		test.log(Status.PASS,"From  DB->   '" + list_printHead_Field + "'   :   " + s2 );

		if(s1.contains(s2)) {
			logger.info("PASS==> '" + list_printHead_Field + "' is matched from API and DB ==>\n");
			test.log(Status.PASS,MarkupHelper.createLabel( "PASS=> '" + list_printHead_Field + "' is matched from API and DB ==> ", ExtentColor.GREEN));
		} else {
			serialNoList.add(sn);
			logger.info("Fail=>" + list_printHead_Field + " is not matched from API and DB ==>\n");
			test.log(Status.FAIL, iObject + "th iteration of object '" + list_printHead_Field + "'  is not matched " );
			test.log(Status.FAIL,"From API, " + list_printHead_Field + "  :  " + s1  );
			test.log(Status.FAIL,"From  DB, " + list_printHead_Field + "  :  " + s2  );
			test.log(Status.FAIL, "Fail=> Field '" + list_printHead_Field + "'  is not matched from API and DB ==>for SN=" + sn + "  & PN=" + pn );

			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

			test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn, pn , start_ts,end_ts )); 

			logger.info("API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
					APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(), sn ,pn ,start_ts,end_ts )); 
			// refer query.
			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query result*************** ", ExtentColor.RED) );
			//test.log(Status.FAIL,"" + MaintenanceTasks_ListMaintenancesDB(sn,pn,date) );
			test.log(Status.FAIL,"******************************************************************************************");
		}	
	}


	// ****************************** Private method for DB Query ******************************************
	private static String  PrintheadDetailsDB2(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		

		StringBuilder sb = new StringBuilder();
		// Write query.

		sb.append("SELECT  \"PH_SERIAL_NO\", \"PEN\", \"STATUS\", \"START_TS\",\"END_TS\"\r\n" + 
				"       FROM (SELECT  \"PH_SERIAL_NO\",  \"PEN\",  \"STATUS\", \"START_TS\", \"END_TS\", \r\n" + 
				"                    ROW_NUMBER() over (partition by PRINTER_PRODUCT_NO,PRINTER_SERIAL_NO,ph_serial_no, pen order by start_ts desc) as rank\r\n" + 
				"FROM app_bm_graphics_lf_telemetry.\"PRINTHEAD_WARRANTY_STATUS\"\r\n" + 
				"WHERE PRINTER_PRODUCT_NO=");
		sb.append("'" + product_no + "'" + " AND PRINTER_SERIAL_NO=" + "'" + serial_no + "' " );
		sb.append(" AND PH_SERIAL_NO IS NOT NULL  and PH_SERIAL_NO!=''\r\n" + 
				"  AND( (");
		sb.append(" START_TS between '" + start_ts + "' and '" + end_ts + "')" );
		sb.append(" OR (END_TS between '" + start_ts + "' and '"  + end_ts + "') ");
		sb.append(" OR (START_TS < '" + start_ts + "' and end_ts > '" + end_ts + "') )");
		sb.append(" )  where rank=1 ");
		//sb.append(" ORDER BY  PH_SERIAL_NO  DESC ");

		String sql = sb.toString();
		//rs = stmt.executeQuery(sql);
		System.out.println("Sql Query-> \n" + sql + "\n");
		return sql;
	} //End Private Method 

	private static String queryForPhSerialNoForPrintHeadEvent(String serial_no, String product_no , String start_ts, String end_ts )
	{
		StringBuilder sb = new StringBuilder();
		// Write query.

		sb.append("SELECT distinct(pen), PH_SERIAL_NO,  EVENT_TYPE  FROM (   SELECT  PRINTER_PRODUCT_NO, PRINTER_SERIAL_NO, PH_SERIAL_NO, PEN,\r\n" + 
				"      case when (EVENT_TYPE = 'REPLACE' AND EVENT_END_TS IS NOT NULL) then EVENT_END_TS else EVENT_TS end AS EVENT_TS, EVENT_TYPE, LINE_NO\r\n" + 
				"   FROM  app_bm_graphics_lf_telemetry.PRINTHEAD_EVENT \r\n" + 
				"   WHERE PRINTER_PRODUCT_NO=");

		sb.append("'" + product_no + "'" + " AND PRINTER_SERIAL_NO=" + "'" + serial_no + "' " );
		sb.append(" AND  EVENT_TS >= '" + start_ts + "' AND EVENT_TS<= '" + end_ts + "' ");
		sb.append(" UNION ALL\r\n" + 
				"   SELECT  PRINTER_PRODUCT_NO,  PRINTER_SERIAL_NO, PH_SERIAL_NO, PEN, EVENT_END_TS AS EVENT_TS, EVENT_TYPE, LINE_NO \r\n" + 
				"     FROM  app_bm_graphics_lf_telemetry.PRINTHEAD_MISSING_REPLACE_EVENT\r\n" + 
				"        WHERE PRINTER_PRODUCT_NO= " );
		sb.append("'" + product_no + "'" + " AND PRINTER_SERIAL_NO=" + "'" + serial_no + "' " );
		sb.append(" AND  EVENT_TS >= '" + start_ts + "' AND EVENT_TS<= '" + end_ts + "' ");
		sb.append(" AND EVENT_TYPE='REPLACE' ) T  ORDER BY PEN, EVENT_TS ASC  ");

		String sql = sb.toString();
		//logger.info("Sql Query-> \n" + sql + "\n");
		return sql;
	}

} // End Class
