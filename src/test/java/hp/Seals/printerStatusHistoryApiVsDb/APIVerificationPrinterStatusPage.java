package hp.Seals.printerStatusHistoryApiVsDb;

import java.util.*;
import org.apache.log4j.*;
import org.json.*;
import org.testng.Assert;
import org.testng.asserts.*;

import com.amazonaws.samples.PostgreSqlConnection;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.APIPath;
import io.restassured.response.Response;
import utils.*;



public class APIVerificationPrinterStatusPage extends ExtentReportListener
{
	final static Logger logger = LogManager.getLogger(APIVerificationPrinterStatusPage.class);

	static PostgreSqlConnectionDb objSql = new PostgreSqlConnectionDb();

	static SoftAssert softAssert = new SoftAssert();

	// public methods

	/*
	 * count number of objects from Response
	 */
	public static Integer verifyObjectsCountFromResponse(Response response, String listObj, String key) 
	{
		int responseCount = 0;
		int KeyValueCount = 0;
		try {
			List<Map<String, String>> list = response.jsonPath().getList(listObj);
			test.log(Status.PASS, "No of Key value count from API Response ====> " + list.size());
			responseCount = list.size();

			if (list.size() > 0) 
			{
				for (int i = 0; i < list.size(); i++) 
				{
					String actualVal = String.valueOf(list.get(i).get(key));
					if( key != null && actualVal != null ) 
					{
						KeyValueCount = i + 1;
					}
				}
			}			
			Assert.assertEquals(responseCount, KeyValueCount, "API Response object count is not matched");
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		//logger.info("Response object count:: " + responseCount);
		return responseCount;
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public static List<String> verifyObjectsKey_Negative(Response response, String listObj, String key, List serialNoList, String serialNo,int choice) 
	{	
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		//logger.info("object count: " + list.size());
		test.log(Status.PASS, "No of Json Objects ===> " + list.size());

		List alist = new ArrayList();
		int count = 0 ;
		try {
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					String actualVal = String.valueOf(list.get(i).get(key));
					if( key != null && actualVal != null ) {
						alist.add(actualVal);
						try {
							Float floatVal = Float.valueOf(actualVal).floatValue();
							//   System.out.println("Float: " + floatVal);
							if( floatVal  < 0 )	{
								serialNoList.add(serialNo);
								logger.info("Key value is negative...> " + key + "[" + i + "] : " + actualVal + " AND SerialNo=> " + serialNoList);
								test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + actualVal + "AND serialNoList=" + serialNoList );
							}
						} catch(Exception e) {
							e.fillInStackTrace();
						}
						count = i + 1; 
					}
				}
			}
		} catch (Exception e) {

			test.log(Status.FAIL, e.fillInStackTrace());
		}
		return alist; 
	}

	/* 
	 * ********* Getting SN, start_ts and status value from PrinterStatusHistory response ***********
	 * 
	 */
	@SuppressWarnings("unused")
	public static List<PrinterStatusPojo> getSN_Start_TS_StatusValuesFromAPI(Response response, String key, String key1, String key2, String sn, String pn, String startDate, String endDate)
	{
		List<PrinterStatusPojo> listObj = null;
		PrinterStatusPojo printerStatusPojoObj = null;
		if(response.getStatusCode() == 400) {
			logger.info("400 Bad request due to 'No data in requested duration' "  );
			test.log(Status.WARNING, "400 Bad request due to 'No data in requested duration' "  );

			test.log(Status.WARNING,MarkupHelper.createLabel(" *********************************************************************************************** ",ExtentColor.LIME));

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
					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate )); 
					test.log(Status.FAIL, " *********************************************************************************** ");

				} else {
					String srNo = inputJSONObject.getString(key);  // key = serial_Number
					Assert.assertEquals(srNo, sn,"serial_Number value is not matched");										

					JSONArray jsonArray = inputJSONObject.getJSONArray("status");  

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.FAIL,MarkupHelper.createLabel(" 'status' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'status'  object is empty from API Response " );
						countApi = jsonArray.length();

						test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
								APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

						// Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
						test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn,pn,startDate,endDate) );
						test.log(Status.FAIL, "*********************************************************************************** ");	

					}
					else {   // API is calling
						listObj = new ArrayList<PrinterStatusPojo>();
						//printerStatusPojoObj = new PrinterStatusPojo();
						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							printerStatusPojoObj = new PrinterStatusPojo();

							printerStatusPojoObj.setSerial_no(srNo);
							String start_TS = jsonArray.getJSONObject(i).getString(key1);     //key1=start_TS
							printerStatusPojoObj.setStart_TS(start_TS);
							String statusVal = jsonArray.getJSONObject(i).getString(key2);     //key2=status
							printerStatusPojoObj.setStatus(statusVal);	

							//logger.info("Object  values  => " + printerStatusPojoObj);
							listObj.add(printerStatusPojoObj);
							//logger.info("\n****************** \n" + listObj );

						} // End of for loop 
					} // Inner END IF
				} // Outer END IF
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End  ELSE
		logger.info("\n*********From API, List values ********* \n" + listObj );
		return listObj;

	} // End Method	

	// ************** Compare No of field  'serial_Number' ,'start_TS' and 'status' from API with DB *******************
	public static void compareCountOfFieldsFromAPIandDB (Response response, String key, String key1, String key2, String sn, String pn ,String startDate, String endDate, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare no of '" + key2 +"' Value from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("****** Compare no of '" + key2 +"' value from API response and Database *******",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 " , ExtentColor.RED ));
				logger.info("FAILed: API has 400 bad request ");
				serialNoList.add(sn);
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed serial numbers==>>  " + sn );
				test.log(Status.FAIL,"*****************************************************************************************************");

			} else {
				List<PrinterStatusPojo> apiList = getSN_Start_TS_StatusValuesFromAPI(response, key, key1, key2, sn, pn , startDate,  endDate  );
				//test.log(Status.PASS,"From API, Values => " + apiList);

				List<PrinterStatusPojo> dbList = objSql.getSN_Start_TS_StatusFromDB( sn, pn, startDate , endDate );				
				//test.log(Status.PASS,"From  DB, Values => "+ dbList);

				if( dbList.size() == 0 &&  apiList.size() == 0 ) {

					logger.info("'status' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'status' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'status' object is Empty, so Count= " + apiList.size() );
					test.log(Status.WARNING, "From  DB, 'status' object is Empty, so Count= " + dbList.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbList.size() > 0 &&  apiList.size() > 0 && dbList.size() == apiList.size() ) {
					
					test.log(Status.PASS,"From API, '" + key2 + "' count is " + apiList.size() );
					test.log(Status.PASS,"From  DB, '" + key2 + "' count is " + dbList.size() );
					test.log(Status.PASS,MarkupHelper.createLabel("*** '"+ key1 + "' and '" + key2 + "' count is matched  from API and Seals_DB =====>", ExtentColor.CYAN));
					logger.info("***PASS=>  count is matched  from API and Seals_DB =====> \n");

				} else {
					logger.info("'" + key1 + "' and '" + key2 + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From API, '" + key2 + "' count is " + apiList.size() );
					test.log(Status.FAIL,"From  DB, '" + key2 + "' count is " + dbList.size() );
					test.log(Status.FAIL,"'" + key2 + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

					logger.info("API is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("******************************* END of Execution ************************************************************************");
				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	


	// ************** Compare field of 'serial_Number' ,'start_TS' and 'status' from API with DB *******************
	public static void comparePrinterStatusHistoryFieldsFromApiAndDB (Response response, String key, String key1, String key2, String sn, String pn ,String startDate, String endDate, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare 'serial_Number' ,'start_TS' and 'status' Values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("****** Compare 'serial_Number' ,'start_TS' and 'status' values from API response and Database *******",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 " , ExtentColor.RED ));
				logger.info("FAILed: API has 400 bad request ");
				serialNoList.add(sn);
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed serial numbers==>>  " + sn );
				test.log(Status.FAIL,"*****************************************************************************************************");

			} else {
				List<PrinterStatusPojo> apiList = getSN_Start_TS_StatusValuesFromAPI(response, key, key1, key2, sn, pn , startDate,  endDate  );

				List<PrinterStatusPojo> dbList = objSql.getSN_Start_TS_StatusFromDB( sn, pn, startDate , endDate );						

				if( dbList.size() == 0 &&  apiList.size() == 0 ) {

					logger.info("'status' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'status' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'status' object is Empty, hence Count= " + apiList.size() );
					test.log(Status.WARNING, "From  DB, 'status' object is Empty, hence Count= " + dbList.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbList.size() > 0 &&  apiList.size() > 0 && dbList.size() == apiList.size() ) {
					test.log(Status.PASS,"From API, '" + key2 + "' count is " + apiList.size() );
					test.log(Status.PASS,"From  DB, '" + key2 + "' count is " + dbList.size() );
					test.log(Status.PASS,MarkupHelper.createLabel("*** '"+ key1 + "' and '" + key2 + "' count is matched  from API and Seals_DB =====>", ExtentColor.CYAN));
					logger.info("***PASS=>  count is matched  from API and Seals_DB =====> \n");
					
					test.log(Status.INFO, MarkupHelper.createLabel("************** From API, values of 'serial_Number' ,'start_TS' and 'status' are *****************", ExtentColor.GREEN));
					test.log(Status.PASS,"" + apiList);
					test.log(Status.INFO, MarkupHelper.createLabel("************** From  DB, values of 'serial_Number' ,'start_TS' and 'status' are *****************", ExtentColor.BROWN));
					test.log(Status.PASS,""+ dbList);

					for(int i = 1 ; i < apiList.size() ; i++) {
						PrinterStatusPojo  apiObj = dbList.get(i);
						PrinterStatusPojo  dbObj = apiList.get(i);

						comparePrintHeadFields("serial_Number",apiObj.getSerial_no(), dbObj.getSerial_no(), sn, pn,startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("start_TS",apiObj.getStart_TS(), dbObj.getStart_TS() , sn, pn, startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("status",apiObj.getStatus(), dbObj.getStatus() , sn, pn, startDate , endDate, serialNoList, i+1);

					} // End if
				} else {
					logger.info("'" + key2 + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key2 + "' count is " + dbList.size() );
					test.log(Status.FAIL,"From API, '" + key2 + "' count is " + apiList.size() );
					test.log(Status.FAIL,"'" + key2 + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

					logger.info("API is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("******************************* END of Execution ************************************************************************");
				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	

	/* 
	 * ********* Getting end_TS value based on SN, start_ts and status value from PrinterStatusHistory response ***********
	 * 
	 */
	@SuppressWarnings("unused")
	public static List<EndTSpojo> getEndTsFromAPI(Response response, String key, String key1, String key2, String key3, String sn, String pn, String startDate, String endDate)
	{
		List<EndTSpojo> listObj = null;
		EndTSpojo printerStatusPojoObj = null;
		if(response.getStatusCode() == 400) {
			logger.info("400 Bad request due to 'No data in requested duration' "  );
			test.log(Status.WARNING, "400 Bad request due to 'No data in requested duration' "  );

			test.log(Status.WARNING,MarkupHelper.createLabel(" *********************************************************************************************** ",ExtentColor.LIME));

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
					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate )); 
					test.log(Status.FAIL, " *********************************************************************************** ");

				} else {
					String srNo = inputJSONObject.getString(key);  // key = serial_Number
					Assert.assertEquals(srNo, sn,"serial_Number value is not matched");										

					JSONArray jsonArray = inputJSONObject.getJSONArray("status");  

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.FAIL,MarkupHelper.createLabel(" 'status' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'status'  object is empty from API Response " );
						countApi = jsonArray.length();

						test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
								APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

						// Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
						test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn,pn,startDate,endDate) );
						test.log(Status.FAIL, "*********************************************************************************** ");	

					}
					else {   // API is calling
						listObj = new ArrayList<EndTSpojo>();
						//printerStatusPojoObj = new PrinterStatusPojo();
						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							printerStatusPojoObj = new EndTSpojo();

							printerStatusPojoObj.setSerial_no(srNo);
							String start_TS = jsonArray.getJSONObject(i).getString(key1);     //key1=start_TS
							printerStatusPojoObj.setStart_TS(start_TS);
							String statusVal = jsonArray.getJSONObject(i).getString(key2);     //key2=status
							printerStatusPojoObj.setStatus(statusVal);	
							String endTsVal = jsonArray.getJSONObject(i).getString(key3);     //key3=end_TS
							printerStatusPojoObj.setEnd_TS(endTsVal);

							//logger.info("Object  values  => " + printerStatusPojoObj);
							listObj.add(printerStatusPojoObj);
							//logger.info("\n****************** \n" + listObj );

						} // End of for loop 
					} // Inner END IF
				} // Outer END IF
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End  ELSE
		logger.info("\n*********From API, List values ********* \n" + listObj );
		return listObj;

	} // End Method	

	// ************** Compare 'end_TS' value based on 'serial_Number' ,'start_TS' and 'status' from API with DB *******************
	public static void compareEndTsFromPrinterStatusHistoryApiAndDB (Response response, String key, String key1, 
			String key2,String key3, String sn, String pn ,String startDate, String endDate, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare 'end_TS' value based on 'serial_Number' ,'start_TS' and 'status' Values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("****** Compare 'end_TS' value based on 'serial_Number' ,'start_TS' and 'status' values from API response and Database *******",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 " , ExtentColor.RED ));
				logger.info("FAILed: API has 400 bad request ");
				serialNoList.add(sn);
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed serial numbers==>>  " + sn );
				test.log(Status.FAIL,"********************************************************************************************************");

			} else {
				List<EndTSpojo> apiList = getEndTsFromAPI(response, key, key1, key2, key3, sn, pn , startDate,  endDate  );

				List<EndTSpojo> dbList = objSql.getEnd_TsFromDB( sn, pn, startDate , endDate );						

				if( dbList.size() == 0 &&  apiList.size() == 0 ) {

					logger.info("'status' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'status' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'status' object is Empty, hence Count= " + apiList.size() );
					test.log(Status.WARNING, "From  DB, 'status' object is Empty, hence Count= " + dbList.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbList.size() > 0 &&  apiList.size() > 0 && dbList.size() == apiList.size() ) {
					test.log(Status.PASS,"From API, '" + key3 + "' count is " + apiList.size() );
					test.log(Status.PASS,"From  DB, '" + key3 + "' count is " + dbList.size() );
					test.log(Status.PASS,MarkupHelper.createLabel("*** '"+ key1 + "','" + key2 + "' and '" + key3 + "' count is matched  from API and Seals_DB =====>", ExtentColor.CYAN));
					logger.info("***PASS=>  count is matched  from API and Seals_DB =====> \n");
					
					test.log(Status.INFO, MarkupHelper.createLabel("************** From API, values of 'serial_Number' ,'start_TS' , 'status and 'end_TS' are *****************", ExtentColor.GREEN));
					test.log(Status.PASS,"" + apiList);
					test.log(Status.INFO, MarkupHelper.createLabel("************** From  DB, values of 'serial_Number' ,'start_TS' ,'status' and 'end_TS' are *****************", ExtentColor.BROWN));
					test.log(Status.PASS,""+ dbList);

					for(int i = 1 ; i < apiList.size()-1 ; i++) {
						EndTSpojo  apiObj = dbList.get(i);
						EndTSpojo   dbObj = apiList.get(i);

						comparePrintHeadFields("serial_Number",apiObj.getSerial_no(), dbObj.getSerial_no(), sn, pn,startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("start_TS",apiObj.getStart_TS(), dbObj.getStart_TS() , sn, pn, startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("status",apiObj.getStatus(), dbObj.getStatus() , sn, pn, startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("end_TS",apiObj.getEnd_TS(), dbObj.getEnd_TS() , sn, pn, startDate , endDate, serialNoList, i+1);						

					} // End if
				} else {
					logger.info("'" + key3 + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key3 + "' count is " + dbList.size() );
					test.log(Status.FAIL,"From API, '" + key3 + "' count is " + apiList.size() );
					test.log(Status.FAIL,"'" + key3 + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

					logger.info("API is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("******************************* END of Execution ************************************************************************");
				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	

	/* 
	 * ********* Getting 'sub_Status' value based on SN, start_ts and status value from PrinterStatusHistory response ***********
	 * 
	 */
	@SuppressWarnings("unused")
	public static List<SubStatusPojo> getSubStatusFromAPI(Response response, String key, String key1, String key2, String key3, String sn, String pn, String startDate, String endDate)
	{
		List<SubStatusPojo> listObj = null;
		SubStatusPojo printerStatusPojoObj = null;
		if(response.getStatusCode() == 400) {
			logger.info("400 Bad request due to 'No data in requested duration' "  );
			test.log(Status.WARNING, "400 Bad request due to 'No data in requested duration' "  );

			test.log(Status.WARNING,MarkupHelper.createLabel(" *********************************************************************************************** ",ExtentColor.LIME));

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
					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate )); 
					test.log(Status.FAIL, " *********************************************************************************** ");

				} else {
					String srNo = inputJSONObject.getString(key);  // key = serial_Number
					Assert.assertEquals(srNo, sn,"serial_Number value is not matched");										

					JSONArray jsonArray = inputJSONObject.getJSONArray("status");  

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.FAIL,MarkupHelper.createLabel(" 'status' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'status'  object is empty from API Response " );
						countApi = jsonArray.length();

						test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
								APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

						// Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
						test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn,pn,startDate,endDate) );
						test.log(Status.FAIL, "*********************************************************************************** ");	

					}
					else {   // API is calling
						listObj = new ArrayList<SubStatusPojo>();
						//printerStatusPojoObj = new PrinterStatusPojo();
						for(int i = 0 ; i < jsonArray.length(); i++) {
							printerStatusPojoObj = new SubStatusPojo();

							printerStatusPojoObj.setSerial_no(srNo);
							String start_TS = jsonArray.getJSONObject(i).getString(key1);     //key1=start_TS
							printerStatusPojoObj.setStart_TS(start_TS);
							String statusVal = jsonArray.getJSONObject(i).getString(key2);     //key2=status
							printerStatusPojoObj.setStatus(statusVal);	
							String subStatusVal = jsonArray.getJSONObject(i).getString(key3);     //key3=sub_Status
							printerStatusPojoObj.setSub_status(subStatusVal);

							//logger.info("Object  values  => " + printerStatusPojoObj);
							listObj.add(printerStatusPojoObj);
							//logger.info("\n****************** \n" + listObj );

						} // End of for loop 
					} // Inner END IF
				} // Outer END IF
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End  ELSE
		logger.info("\n*********From API, List values ********* \n" + listObj );
		return listObj;

	} // End Method	

	// ************** Compare 'sub_Status' value based on 'serial_Number' ,'start_TS' and 'status' from API with DB *******************
	public static void compareSubStatusFromPrinterStatusHistoryApiAndDB (Response response, String key, String key1, 
			String key2,String key3, String sn, String pn ,String startDate, String endDate, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare 'sub_Status' value based on 'serial_Number' ,'start_TS' and 'status' Values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("****** Compare 'sub_Status' value based on 'serial_Number' ,'start_TS' and 'status' values from API response and Database *******",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 " , ExtentColor.RED ));
				logger.info("FAILed: API has 400 bad request ");
				serialNoList.add(sn);
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed serial numbers==>>  " + sn );
				test.log(Status.FAIL,"*****************************************************************************************************");

			} else {
				List<SubStatusPojo> apiList = getSubStatusFromAPI(response, key, key1, key2, key3, sn, pn , startDate,  endDate  );

				List<SubStatusPojo> dbList = objSql.getSubStatusFromDB( sn, pn, startDate , endDate );						

				if( dbList.size() == 0 &&  apiList.size() == 0 ) {

					logger.info("'status' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'status' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'status' object is Empty, hence Count= " + apiList.size() );
					test.log(Status.WARNING, "From  DB, 'status' object is Empty, hence Count= " + dbList.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbList.size() > 0 &&  apiList.size() > 0 && dbList.size() == apiList.size() ) {
					test.log(Status.PASS,"From API, '" + key3 + "' count is " + apiList.size() );
					test.log(Status.PASS,"From  DB, '" + key3 + "' count is " + dbList.size() );
					test.log(Status.PASS,MarkupHelper.createLabel("*** '"+ key1 + "','" + key2 + "' and '" + key3 + "' count is matched  from API and Seals_DB =====>", ExtentColor.CYAN));
					logger.info("***PASS=>  count is matched  from API and Seals_DB =====> \n");
										
					test.log(Status.INFO, MarkupHelper.createLabel("************** From API, values of 'serial_Number' ,'start_TS' , 'status and 'sub_Status' are *****************", ExtentColor.GREEN));
					test.log(Status.PASS,"" + apiList);
					test.log(Status.INFO, MarkupHelper.createLabel("************** From  DB, values of 'serial_Number' ,'start_TS' ,'status' and 'sub_Status' are *****************", ExtentColor.BROWN));
					test.log(Status.PASS,""+ dbList);

					for(int i = 1 ; i < apiList.size() ; i++) {
						SubStatusPojo  apiObj = dbList.get(i);
						SubStatusPojo   dbObj = apiList.get(i);

						comparePrintHeadFields("serial_Number",apiObj.getSerial_no(), dbObj.getSerial_no(), sn, pn,startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("start_TS",apiObj.getStart_TS(), dbObj.getStart_TS() , sn, pn, startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("status",apiObj.getStatus(), dbObj.getStatus() , sn, pn, startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("sub_Status",apiObj.getSub_status(), dbObj.getSub_status() , sn, pn, startDate , endDate, serialNoList, i+1);						

					} // End if
				} else {
					logger.info("'" + key3 + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key3 + "' count is " + dbList.size() );
					test.log(Status.FAIL,"From API, '" + key3 + "' count is " + apiList.size() );
					test.log(Status.FAIL,"'" + key3 + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

					logger.info("API is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("******************************* END of Execution ************************************************************************");
				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	

	/* 
	 * ********* Getting 'channel' value based on SN, start_ts and status value from PrinterStatusHistory response ***********
	 * 
	 */
	@SuppressWarnings("unused")
	public static List<ChannelPojo> getChannelFromAPI(Response response, String key, String key1, String key2, String key3, String sn, String pn, String startDate, String endDate)
	{
		List<ChannelPojo> listObj = null;
		ChannelPojo printerStatusPojoObj = null;
		if(response.getStatusCode() == 400) {
			logger.info("400 Bad request due to 'No data in requested duration' "  );
			test.log(Status.WARNING, "400 Bad request due to 'No data in requested duration' "  );

			test.log(Status.WARNING,MarkupHelper.createLabel(" *********************************************************************************************** ",ExtentColor.LIME));

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
					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate )); 
					test.log(Status.FAIL, " *********************************************************************************** ");

				} else {
					String srNo = inputJSONObject.getString(key);  // key = serial_Number
					Assert.assertEquals(srNo, sn,"serial_Number value is not matched");										

					JSONArray jsonArray = inputJSONObject.getJSONArray("status");  

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.FAIL,MarkupHelper.createLabel(" 'status' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'status'  object is empty from API Response " );
						countApi = jsonArray.length();

						test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
								APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

						// Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
						test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn,pn,startDate,endDate) );
						test.log(Status.FAIL, "*********************************************************************************** ");	

					}
					else {   // API is calling
						listObj = new ArrayList<ChannelPojo>();
						//printerStatusPojoObj = new PrinterStatusPojo();
						for(int i = 0 ; i < jsonArray.length(); i++) {
							printerStatusPojoObj = new ChannelPojo();

							printerStatusPojoObj.setSerial_no(srNo);
							String start_TS = jsonArray.getJSONObject(i).getString(key1);     //key1=start_TS
							printerStatusPojoObj.setStart_TS(start_TS);
							String statusVal = jsonArray.getJSONObject(i).getString(key2);     //key2=status
							printerStatusPojoObj.setStatus(statusVal);	
							String channelVal = jsonArray.getJSONObject(i).getString(key3);     //key3=channel
							printerStatusPojoObj.setChannel(channelVal);

							//logger.info("Object  values  => " + printerStatusPojoObj);
							listObj.add(printerStatusPojoObj);
							//logger.info("\n****************** \n" + listObj );

						} // End of for loop 
					} // Inner END IF
				} // Outer END IF
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End  ELSE
		logger.info("\n*********From API, List values ********* \n" + listObj );
		return listObj;

	} // End Method	

	// ************** Compare 'channel' value based on 'serial_Number' ,'start_TS' and 'status' from API with DB *******************
	public static void compareChannelFromAPIandDB (Response response, String key, String key1, 
			String key2,String key3, String sn, String pn ,String startDate, String endDate, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare 'channel' value based on 'serial_Number' ,'start_TS' and 'status' Values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("****** Compare 'channel' value based on 'serial_Number' ,'start_TS' and 'status' values from API response and Database *******",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 " , ExtentColor.RED ));
				logger.info("FAILed: API has 400 bad request ");
				serialNoList.add(sn);
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed serial number==>>  " + sn );
				test.log(Status.FAIL,"*****************************************************************************************************");

			} else {
				List<ChannelPojo> apiList = getChannelFromAPI(response, key, key1, key2, key3, sn, pn , startDate,  endDate  );

				List<ChannelPojo> dbList = objSql.getChannelFromDB( sn, pn, startDate , endDate );						

				if( dbList.size() == 0 &&  apiList.size() == 0 ) {

					logger.info("'status' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'status' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'status' object is Empty, hence Count= " + apiList.size() );
					test.log(Status.WARNING, "From  DB, 'status' object is Empty, hence Count= " + dbList.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbList.size() > 0 &&  apiList.size() > 0 && dbList.size() == apiList.size() ) {
					test.log(Status.PASS,"From API, '" + key3 + "' count is " + apiList.size() );
					test.log(Status.PASS,"From  DB, '" + key3 + "' count is " + dbList.size() );
					test.log(Status.PASS,MarkupHelper.createLabel("*** '"+ key1 + "','" + key2 + "' and '" + key3 + "' count is matched  from API and Seals_DB =====>", ExtentColor.CYAN));
					logger.info("***PASS=>  count is matched  from API and Seals_DB =====> \n");
										
					test.log(Status.INFO, MarkupHelper.createLabel("************** From API, values of 'serial_Number' ,'start_TS' , 'status and 'channel' are *****************", ExtentColor.GREEN));
					test.log(Status.PASS,"" + apiList);
					test.log(Status.INFO, MarkupHelper.createLabel("************** From  DB, values of 'serial_Number' ,'start_TS' ,'status' and 'channel' are *****************", ExtentColor.BROWN));
					test.log(Status.PASS,""+ dbList);

					for(int i = 1 ; i < apiList.size() ; i++) {
						ChannelPojo  apiObj = dbList.get(i);
						ChannelPojo   dbObj = apiList.get(i);

						comparePrintHeadFields("serial_Number",apiObj.getSerial_no(), dbObj.getSerial_no(), sn, pn,startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("start_TS",apiObj.getStart_TS(), dbObj.getStart_TS() , sn, pn, startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("status",apiObj.getStatus(), dbObj.getStatus() , sn, pn, startDate , endDate, serialNoList, i+1);
						comparePrintHeadFields("channel",apiObj.getChannel(), dbObj.getChannel() , sn, pn, startDate , endDate, serialNoList, i+1);						

					} // End if
				} else {
					logger.info("'" + key3 + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key3 + "' count is " + dbList.size() );
					test.log(Status.FAIL,"From API, '" + key3 + "' count is " + apiList.size() );
					test.log(Status.FAIL,"'" + key3 + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

					logger.info("API is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, startDate, endDate )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("******************************* END of Execution ************************************************************************");
				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	


	// Choose the field and compare the fields value from Api and DB
	private static void comparePrintHeadFields(String printerStatusHistory_Field, String s1, String s2, String sn,String pn, String start_ts ,String end_ts, HashSet<String> serialNoList , int iObject ) 
	{
		switch (printerStatusHistory_Field) {		
		case "serial_Number" : compareObjects(printerStatusHistory_Field, s1, s2, sn, pn ,start_ts , end_ts, serialNoList, iObject); break;              
		case "start_TS" :  compareObjects(printerStatusHistory_Field, s1, s2, sn, pn, start_ts , end_ts, serialNoList, iObject) ; break;
		case "status" :  compareObjects(printerStatusHistory_Field, s1, s2, sn, pn, start_ts , end_ts, serialNoList, iObject) ; break;
		case "end_TS" :  compareObjects(printerStatusHistory_Field, s1, s2, sn, pn, start_ts , end_ts, serialNoList, iObject) ; break;
		case "sub_Status" :  compareObjects(printerStatusHistory_Field, s1, s2, sn, pn, start_ts , end_ts, serialNoList, iObject) ; break;
		case "channel" :  compareObjects(printerStatusHistory_Field, s1, s2, sn, pn, start_ts , end_ts, serialNoList, iObject) ; break;		

		}
	}

	// Compare two objects where each objects have list of values
	private static void compareObjects(String printerStatusHistory_Field, String s1, String s2, String sn, String pn, String start_ts , String end_ts, HashSet<String> serialNoList , int iObject) 
	{		
		logger.info("From API, '"+ printerStatusHistory_Field + "' field Value = " + s1 + "  and From DB, '" + printerStatusHistory_Field + "'  field Value = " + s2);

		if(s1.equals(s2)) {
			test.log(Status.PASS,"From API->   '" + printerStatusHistory_Field + "'   :   " + s1 );
			test.log(Status.PASS,"From  DB->   '" + printerStatusHistory_Field + "'   :   " + s2 );

			logger.info("PASS==> '" + printerStatusHistory_Field + "' is matched from API and DB ==>\n");
			test.log(Status.PASS,MarkupHelper.createLabel( "PASS=> '" + printerStatusHistory_Field + "' is matched from API and DB ==> ", ExtentColor.GREEN));

		} else {
			test.log(Status.FAIL,"From API->   '" + printerStatusHistory_Field + "'   :   " + s1 );
			test.log(Status.FAIL,"From  DB->   '" + printerStatusHistory_Field + "'   :   " + s2 );

			logger.info("Fail=>" + printerStatusHistory_Field + " is not matched from API and DB ==>\n");
			test.log(Status.FAIL, MarkupHelper.createLabel( "Fail=> " + iObject + "th iteration of field, '" + printerStatusHistory_Field + "'   is not matched from API and DB ==>for SN=" + sn + "  & PN=" + pn, ExtentColor.RED) );

			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

			test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
					APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,start_ts, end_ts )); 

			logger.info("API is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
					APIPath.apiPath.GET_PrinterStatusHistory.toString(), sn, pn, start_ts, end_ts )); 

			// refer query.
			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query result*************** ", ExtentColor.RED) );
			test.log(Status.FAIL,"" + getQueryForPrinterStatusApi(sn, pn, start_ts, end_ts) );

			serialNoList.add(sn);
			test.log(Status.FAIL,"******************************************************************************************");
		}	
	}


	/* 
	 * **********************************************************************************************************
	 * Return List of objects from API Response  
	 * 
	 */
	@SuppressWarnings("unused")
	public static List<PrinterStateResult> getListVauesfromAPi_1(Response response, String listObj, String key1, String key2)
	{
		List<PrinterStateResult> listResult = null;
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		//test.log(Status.PASS, "No of API Json Objects ==> " + list.size());
		try {
			if(400 == response.getStatusCode() &&  (list == null) ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n" );																				

			} else if (list.size() > 0) {			

				PrinterStateResult printerStateResult = null;
				listResult = new ArrayList<PrinterStateResult>();
				int count = 0;

				for (int i = 0; i < list.size(); i++) {
					printerStateResult = new PrinterStateResult();
					String sub_status = list.get(i).get(key1);
					String status = list.get(i).get(key2);
					printerStateResult.setSub_Status(sub_status);
					printerStateResult.setStatus(status);
					//test.log(Status.PASS,"Validated Keys--> " + key1 + " : " + sub_status + "\t\t" + key2 + ": " + status);
					//logger.info("Validated Keys--> " + key1 + " : " + sub_status + "\t\t" + key2 + ": " + status);
					count = i + 1;

					listResult.add(printerStateResult);
				}
				test.log(Status.PASS, MarkupHelper.createLabel("<==From API Response Object key value list is ", ExtentColor.BROWN) );
				test.log(Status.PASS,"From API Response=>  " + listResult );
				logger.info("<--From API Response Object key value list is ----> \n" + listResult + "\n");
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		return listResult;
	}
	// Getting more keys from Response and Compare with Database   *************************
	public static void verifyMoreKeysFromResponseAndDb(Response response, String listObj, String key1, String key2,
			String sn, String pn , String startDate,String endDate ,List serialNoList )
	{		
		List<PrinterStateResult> resultObjApi = getListVauesfromAPi(response, listObj, key1, key2, sn, pn, startDate,endDate);  // Calling Api response List method
		List<PrinterStateResult> resultFromDB = objSql.getStatusAndSubStatus( sn, pn, startDate,endDate ); // calling DB Method

		test.log(Status.INFO, MarkupHelper.createLabel("******Compare 'status' and 'sub_Status' values from API response and Database *******",ExtentColor.AMBER));
		test.log(Status.PASS, MarkupHelper.createLabel("Values from API Response::>  " + resultObjApi ,ExtentColor.BROWN));
		test.log(Status.PASS,MarkupHelper.createLabel("Values from Databse ::>>  " + resultFromDB, ExtentColor.PINK) );

		try {
			if(resultObjApi.equals(resultFromDB))
			{
				softAssert.assertEquals(resultObjApi, resultFromDB, "DB and API Response object count is not matched");
				//test.log(Status.PASS,"Values from API Response::>  " + resultObjApi );
				//test.log(Status.PASS,"Values from Databse ::>>  " + resultFromDB );

				for(int i = 0; i < resultObjApi.size(); i ++)
				{
					if(	(resultObjApi.get(i).getStatus()).equals((resultFromDB.get(i).getStatus())) ) 
					{
						logger.info("<===== 'status' values are PASSED from API Response & DB  ====>" );
						test.log(Status.PASS, "<===='status' values are PASSED from API Response & DB  ====> " );
					} else {
						logger.info(" Failed Object Iteration count => " + resultObjApi.get(i) + "::");
						test.log(Status.FAIL,"FAILED  Object Iteration count=> " + resultObjApi.get(i));
						logger.info("From API 'status': " + resultObjApi.get(i).getStatus() + "AND From DB 'status' :" + resultFromDB.get(i).getStatus() );
						test.log(Status.FAIL,"From API 'status': " + resultObjApi.get(i).getStatus() + "  AND From DB 'status' : " + resultFromDB.get(i).getStatus() );
					}

					if( (resultObjApi.get(i).getSub_Status()).equals ((resultFromDB.get(i).getSub_Status()))  ) 
					{
						logger.info("< ===== 'sub_Status' values are PASSED from API Response & DB  ====>" );
						test.log(Status.PASS, "<==== 'sub_Status' values are PASSED from API Response & DB  ====> " );
					} else {

						logger.info(" FAILED  Object Iteration count=> " + resultObjApi.get(i) + "::");
						test.log(Status.FAIL,"FAILED  Object Iteration count=> " + resultObjApi.get(i));
						logger.info(" From API 'sub_Status': " + resultObjApi.get(i).getSub_Status() + "AND From DB 'sub_Status' :" + resultFromDB.get(i).getSub_Status() );
						test.log(Status.FAIL," From API 'sub_Status': " + resultObjApi.get(i).getSub_Status() + "  AND From DB 'sub_Status' : " + resultFromDB.get(i).getSub_Status() );

					}
				}
			}
			else {
				softAssert.assertEquals(resultObjApi, resultFromDB, "DB and API Response object count is not matched");
				logger.info(" 'Failed': Object count is not matched => " + resultObjApi + " != " + resultFromDB + "\n");
				//System.out.print("From API 'status': " + resultObjApi.get(i).getStatus() + "AND From DB 'status' :" + resultFromDB.get(i).getStatus() );
				test.log(Status.FAIL,"Failed:- Object count is not matched =>  " + resultObjApi +" != " + resultFromDB );	

				serialNoList.add(sn);
			}
		}catch(Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());	
		}
	}

	/* 
	 * *********Compare Status and subStatus value from PrinterStatusHistory reponse and Seals DB **********
	 * 
	 */
	public static List<PrinterStateResult> getListVauesfromAPi(Response response, String listObj, String key1, String key2,
			String sn, String pn, String startDate, String endDate)
	{
		List<PrinterStateResult> listObj1 = new ArrayList<PrinterStateResult>();
		//		if(response.getStatusCode() == 400) {
		//			logger.info("400 Bad request due to 'No data in requested duration' "  );
		//			test.log(Status.WARNING, "400 Bad request due to 'No data in requested duration' "  );
		//
		//			test.log(Status.WARNING,MarkupHelper.createLabel(" *********************************************************************************************** ",ExtentColor.LIME));
		//
		//		} 
		//		else {
		String resultString = response.getBody().asString();       // APi is calling
		JSONObject inputJSONObject = null;

		int countApi = 0;
		try {

			inputJSONObject = new JSONObject(resultString);
			//logger.info("inputJSONObject=> "+ inputJSONObject);

			if(resultString == null) {
				logger.info("FAILed: API has 400 bad request ");
				test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				
				test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
						APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 
				test.log(Status.FAIL, " *********************************************************************************** ");
			} else {

				JSONArray jsonArray = inputJSONObject.getJSONArray(listObj);  // status

				if(jsonArray.isEmpty() || jsonArray.length() == 0) {
					test.log(Status.FAIL,MarkupHelper.createLabel(" 'status' object is empty from API Response ",ExtentColor.LIME) );
					logger.info("Failed ==>'" + listObj + "' object is empty from API Response " );
					countApi = jsonArray.length();

					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

					// Write query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
					test.log(Status.FAIL,"" + getQueryForStatusAndSubStatus(sn,pn,startDate,endDate));
					test.log(Status.FAIL, " *********************************************************************************** ");	
				}
				else {   // API is calling
					for(int i = 0 ; i < jsonArray.length(); i++) 
					{
						PrinterStateResult objPrinterStatus = new PrinterStateResult();
						String statusVal = jsonArray.getJSONObject(i).getString(key1);     //status
						objPrinterStatus.setStatus(statusVal);
						String sub_StatusVal = jsonArray.getJSONObject(i).getString(key2);     //sub_Status
						objPrinterStatus.setSub_Status(sub_StatusVal);	

						//logger.info("Object  values  => " + objPrinterStatus);
						listObj1.add(objPrinterStatus);
						//logger.info("\n********* New List values ********* \n" + listObj1 );

					} // End of for loop 

				} // Inner END IF

			} // Outer END IF
		} catch (JSONException e) {

			test.log(Status.FAIL, e.fillInStackTrace());	
		}
		//	}//End  ELSE
		//logger.info("\n********* New List values ********* \n" + listObj1 );
		return listObj1;

	} // End Method	

	public static void compareStatusValuesFromPrinterStatusHistoryApiWithDB(Response response, String listObj, String key1, String key2,
			String sn,String pn,String startDate, String endDate, List serialNoList) 
	{
		logger.info("\n***Compare 'staus' and 'sub_status' values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel( "****** Compare 'staus' and 'sub_status' values from API response and Database *******", ExtentColor.CYAN ));
		try {
			if(response.getStatusCode() == 400) {
				logger.info("400 Bad request due to 'No data in requested duration' "  );
				test.log(Status.WARNING, "\"error\": [\r\n" + 			" \"No data in requested duration\"\r\n" + 	" ]"  );
				test.log(Status.WARNING,MarkupHelper.createLabel(" ******************************************************************************************************** ",ExtentColor.BLUE));
			} 
			else {
				List<PrinterStateResult> listApiVal = getListVauesfromAPi(response, listObj, key1, key2,sn, pn, startDate,endDate);  // Calling Api response List method
				List<PrinterStateResult> listDbVal = objSql.getStatusAndSubStatus( sn, pn, startDate,endDate ); // calling DB Method

				if( listDbVal == null &&  listApiVal == null ) {
					logger.info("'status' object is Empty From API Response and DB " );
					test.log(Status.PASS, "'status' object is Empty From API Response and DB ");
					test.log(Status.PASS,"Refer This API => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
							APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate ));

					test.log(Status.PASS,MarkupHelper.createLabel("******** Refer below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.PASS,"" + getQueryForStatusAndSubStatus(sn,pn,startDate,endDate) );
				} 
				else if( listDbVal != null &&  listApiVal != null ) {
					logger.info("'status' values From Database =>> " + listDbVal);
					test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'status' values from DB =====> ", ExtentColor.GREY) );
					test.log(Status.PASS,"" + listDbVal);

					logger.info("'status' values From API Response =>> " + listApiVal );
					test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'status' values from API =====> ", ExtentColor.GREY) );
					test.log(Status.PASS,"" + listApiVal);				

					if(listDbVal.containsAll(listDbVal)) {
						test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'status' and 'subStatus' values are matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
						logger.info("*** PASS=>'status' and 'subStatus' values are matched  from API and DB ***\n" );

					} else {

						test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'status' and 'subStatus' values are not matched from API and Seals_DB *** ",ExtentColor.RED) );
						logger.info("*** FAIL =>'status' and 'subStatus' values are not matched  from API and DB *** \n");				

						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

						test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
								APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 

						// refer query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
						test.log(Status.FAIL,"" + getQueryForStatusAndSubStatus(sn,pn,startDate,endDate) );

						serialNoList.add(sn);
						test.log(Status.FAIL, " *********************************************************************************** ");
					}
				}

				//			 else {
				//				logger.info("From  DB, '" + key1 + "'  Objects Count= " + listDbVal.size() );
				//				logger.info("From API, '" + key1 + "' Objects Count= " + listApiVal.size());
				//				test.log(Status.PASS, "From  DB, '" + key1 + "'  Objects Count= " + listDbVal.size() );
				//				test.log(Status.PASS, "From API, '" + key1 + "'  Objects Count= " + listApiVal.size() );	
				//				logger.info("\n 'status' objects count is not same From API Response and DB \n" );
				//				test.log(Status.FAIL,"'status' Objects count is not Same From API Response and DB " );
				//
				//				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );
				//
				//				logger.info(FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
				//						APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate ));
				//
				//				test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(
				//						APIPath.apiPath.GET_PrinterStatusHistory.toString(),sn,pn,startDate,endDate )); 
				//
				//				// refer query.
				//				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
				//				test.log(Status.FAIL,"" + getQueryForStatusAndSubStatus(sn,pn,startDate,endDate) );
				//
				//				serialNoList.add(sn);
				//				test.log(Status.FAIL, " *********************************************************************************** ");
				//			}
			}

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		test.log(Status.PASS, MarkupHelper.createLabel("############################################ END ########################################### ",ExtentColor.ORANGE));
	}	





	/*================================================================================================== */

	/*
	 * ***************** Verify key  value from Response ******from 0th Level**************************************
	 */
	public static String verifyKey_Response(Response response, String key)
	{
		String keyVal = "";
		String result = response.asString();
		// System.out.println("Body====> " + result);
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(result);

			keyVal = UtilityApiMethods.getKey(jsonObj, key);

			// getKey(jsonObj,key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		logger.info("**** Display Key and Value=>>>  " + key + " : " + keyVal + "\n");
		return keyVal;
	}


	/*
	 * Verify key value from Response ***************** N+1 level***********************************************
	 */
	public static Map<Integer, HashMap<String, String>>   verify_Key_Value_Response(Response response, String key)
	{	
		Map<Integer, HashMap<String, String>>    keyValMap = new HashMap<Integer, HashMap<String,String>>();
		String result = response.asString();

		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(result);
			keyValMap = UtilityApiMethods.getKey1(jsonObj, key);

			test.log(Status.PASS, "<=========== Key value from Response========> \n " + keyValMap + "\n");

		} catch (JSONException e) {
			test.log(Status.FAIL, e.fillInStackTrace());	
		}
		//logger.info("\n<===============Key and Value =====================> \n" + keyValMap + "\n");
		//test.log(Status.INFO,"<========================== One Test execution is ENDed ===========================> " );
		return keyValMap;
	}

	/*
	 * Verify key value from Response **********Check N + N Level ***********************************************
	 */

	public static Map<Integer, HashMap<String, String>>   verify_KeyValue(Response response, String key)
	{	
		Map<Integer, HashMap<String, String>>    keyValMap = null;
		//String keyValMap ="";
		String result = response.getBody().asString();
		//System.out.println("result:>>> " + result );
		JSONObject jsonObj = null;
		try {
			UtilityApiMethods.counter = 0;
			//UtilityApiMethods.map=new HashMap<Integer, HashMap<String,String>>();
			jsonObj = new JSONObject(result);

			keyValMap = UtilityApiMethods.get_Key(jsonObj, key);
			//logger.info("key and value: " + keyValMap);
			test.log(Status.PASS, " Key value from Response  ====>  " + keyValMap);

		} catch (JSONException e) {
			test.log(Status.FAIL, e.fillInStackTrace());	
		}

		logger.info(" Object count-------> " + UtilityApiMethods.counter);
		logger.info("\n<--Key and Value ==>" + keyValMap + "\n");

		return keyValMap;
	}



	/*
	 * ************************** Private Methods **********************************************************
	 */
	private static String getQueryForStatusAndSubStatus(String serial_no, String product_no , String start_ts,String end_ts ) 
	{	
		StringBuilder sb = new StringBuilder();
		// Write query.
		//String sql = "SELECT start_ts,end_ts ,sub_status, status FROM  app_bm_graphics_lf_telemetry.printer_state_result where serial_no='SG92K11001'AND product_no='4DC17A' AND  START_TS >='2020-07-01 00:00:00Z' AND END_TS <= '2020-07-05 23:59:59' order by START_TS DESC ";
		//System.out.println("\n<=======Query====> \n" + sql + "\n" );
		sb.append("SELECT start_ts, end_ts ,sub_status, status FROM  app_bm_graphics_lf_telemetry.printer_state_result where serial_no=");
		sb.append("'" + serial_no + "'" + " AND product_no=" + "'" +product_no +"'" );
		sb.append(" AND start_ts >= ' " + start_ts + "' AND end_ts <= '" + end_ts + "'");
		sb.append(" AND (end_ts-start_ts)>'0.1'");

		String sql = sb.toString();
		//System.out.println("sql->"+sb.toString());
		return sql;
	}

	@SuppressWarnings("unused")
	private static String getQueryForPrinterStatusApi(String serial_no, String product_no , String start_ts, String end_ts) 
	{
		StringBuilder sb = new StringBuilder();
		// Write query.
		sb.append("SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status \r\n" + 
				"FROM app_bm_graphics_lf_telemetry.printer_state_result where " );
		sb.append(" serial_no='" + serial_no + "' AND product_no='" + product_no + "' " );
		sb.append("AND ((start_ts >= '" + start_ts + "' AND end_ts <= '" + end_ts + "') " );
		sb.append("OR (start_ts between '" + start_ts + "' AND  '" + end_ts + "') " );
		sb.append("OR (end_ts between '" + start_ts + "' AND '" + end_ts + "') ) " );
		sb.append(" AND (end_ts-start_ts)>'0.1' AND channel = 'oee' ");

		String sql = sb.toString();
		//logger.info("sql-> \n " + sql + "\n");

		return sql;
	}



} // End Class
