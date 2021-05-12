package hp.Seals.GetSolutionApiVsDB;

import java.util.*;
import org.apache.log4j.*;
import org.json.*;
import org.testng.Assert;
import org.testng.asserts.*;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.APIPath;
import io.restassured.response.Response;
import utils.ExtentReportListener;
import utils.FileandEnv;


public class APIVerificationGetSolutionPage extends ExtentReportListener
{
	final static Logger logger = LogManager.getLogger(APIVerificationGetSolutionPage.class);

	static PostgreSqlConnectionDb objSql = new PostgreSqlConnectionDb();

	static SoftAssert softAssert = new SoftAssert();

	// public methods

	/* 
	 * ********* getting 'event_Code'  and 'update_TS' values from GetSolution Api response DB **********
	 * 
	 */
	public static List<GetSolutionPojo> getEventCode_UpdateTsValuesFromAPI(Response response, String key, String key2, String sn, String pn, String eventCode, String detectionDate, String event_type)
	{
		GetSolutionPojo solutionPojoObj = null;
		List<GetSolutionPojo> solutionObj = null;

		if(response.getStatusCode() == 400) {
			logger.info("400 Bad request due to 'No data in requested duration' "  );
			test.log(Status.WARNING, "400 Bad request due to 'No data in requested duration' "  );

			test.log(Status.WARNING,MarkupHelper.createLabel(" *********************************************************************************************** ",ExtentColor.LIME));
		} 
		else {
			String resultString = response.getBody().asString();       // APi is calling
			JSONObject inputJSONObject = null;

			try {
				inputJSONObject = new JSONObject(resultString);
				//logger.info("inputJSONObject=> "+ inputJSONObject);

				if(resultString == null) {
					logger.info("FAILed: API has 400 bad request ");
					test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				
					test.log(Status.FAIL,"API is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  )); 
					test.log(Status.FAIL, " *********************************************************************************** ");

				} else {
					JSONArray jsonArray = inputJSONObject.getJSONArray("solutionJson");  

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.FAIL,MarkupHelper.createLabel(" 'solutionJson' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'solutionJson'  object is empty from API Response " );
						test.log(Status.FAIL,"API is => " + FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));

						//Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
						test.log(Status.FAIL,"" + getDbQueryResultForGetSolutionApi(pn,eventCode, event_type) );
						test.log(Status.FAIL, "*********************************************************************************** ");	

					} else {   // API is calling
						solutionObj = new ArrayList<GetSolutionPojo>();
						for(int i = 0 ; i < jsonArray.length(); i++) {
							solutionPojoObj = new GetSolutionPojo() ;
							String eventCodeVal = jsonArray.getJSONObject(i).getString(key);     // key=event_Code
							solutionPojoObj.setEvent_code(eventCodeVal);

							String updateTsVal = jsonArray.getJSONObject(i).getString(key2);     //key1=update_TS
							solutionPojoObj.setInsert_ts(updateTsVal);

							solutionObj.add(solutionPojoObj);
						} // End of for loop 
					} // Inner END IF
				} // Outer END IF
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End  ELSE
		logger.info("*********From API, List values ********* " + solutionObj );
		return solutionObj;

	} // End Method	
	/*
	 * ********* Compare 'event_Code'  and 'update_TS' values from GetSolution Api response and Seals DB **********
	 */
	@SuppressWarnings("rawtypes")
	public static void compareEventCodeAndUpdateTsFromAPIandDB (Response response, String key, String key2, 
			String sn, String pn ,String eventCode, String detectionDate, String eventType, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare 'event_Code' and 'update_TS' value from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("******** Compare 'event_Code' and 'update_TS' value from API response and Database ********",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 " , ExtentColor.RED ));
				logger.info("FAILed: API has 400 bad request ");
				serialNoList.add(sn);
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed Serial_no= " + sn + " and Product_no= " + pn );
				test.log(Status.FAIL,"*****************************************************************************************************");

			} else {
				List apiVal = getEventCode_UpdateTsValuesFromAPI(response,key, key2,sn,pn,eventCode,detectionDate,eventType);
				//logger.info("From API, List Values are=> " + apiVal );
				//test.log(Status.PASS,"**** From API, List Values are=> " + apiVal );

				List dbVal = objSql.getEventCodeAndInsertTsFromDB( pn, eventCode , eventType );						
				//logger.info("From  DB, List Values are=> " + dbVal );
				//test.log(Status.PASS,"**** From DB, Map Values are=> " + dbVal );

				if( apiVal.size() == 0 &&  dbVal.size() == 0 ) {

					logger.info("'solutionJson' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'solutionJson' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'solutionJson' object is Empty, hence Count= " + apiVal.size() );
					test.log(Status.WARNING, "From  DB, 'solutionJson' object is Empty, hence Count= " + dbVal.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( apiVal.size() > 0 &&  dbVal.size() > 0 && apiVal.size() == dbVal.size() ) {
					logger.info("'" + key + "' and '" + key2 + "' count is same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.PASS,MarkupHelper.createLabel( "*** From API, '" + key + "' and '" + key2 + "' values are *** ", ExtentColor.BROWN) );
					test.log(Status.PASS,"" + apiVal );
					test.log(Status.PASS,MarkupHelper.createLabel("*** From  DB, '" + key + "' and '" + key2 + "' values are *** " ,ExtentColor.BROWN) );
					test.log(Status.PASS,"" + dbVal);
					test.log(Status.PASS,MarkupHelper.createLabel("'" + key + "' and '" + key2  + "' count is same From API Response and DB " + " for SN= " + sn + " and PN= " + pn, ExtentColor.GREEN) );

					for(int i = 0 ; i < apiVal.size() ; i++) {
						GetSolutionPojo  apiObj = (GetSolutionPojo) apiVal.get(i);
						GetSolutionPojo   dbObj = (GetSolutionPojo)  dbVal.get(i);

						compareSolutionJsonFields("event_Code",apiObj.getEvent_code(), dbObj.getEvent_code(), sn, pn,eventCode, detectionDate, eventType , serialNoList, i+1);
						compareSolutionJsonFields("update_TS",apiObj.getInsert_ts(), dbObj.getInsert_ts() , sn, pn,eventCode, detectionDate, eventType, serialNoList, i+1);

					} // End if
				} else {
					logger.info("'" + key + "' and '" + key2 + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key + "' and '" + key2 + "' count is " + dbVal.size() );
					test.log(Status.FAIL,"From API, '" + key + "' and '" + key2 +"' count is " + apiVal.size() );
					test.log(Status.FAIL,"'" + key + "' and '" + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getDbQueryResultForGetSolutionApi(pn,eventCode,eventType) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("******************************* END of Execution ************************************************************************");
				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.INFO,"########################################## END ###########################################");
	}	

	
	/* *********************************************************************************************************
	 * getting 'short_description' value based on 'event_Code'  and 'insert_ts' values from from GetSolution Api**********
	 * 
	 */
	public static List<SolutionPojo> getShortDescriptionValueFromAPI(Response response, String key1, String key2, String key3 , String sn, String pn, String eventCode, String detectionDate, String event_type)
	{
		SolutionPojo solutionPojoObj = null;
		List<SolutionPojo> solutionObj = null;

		if(response.getStatusCode() == 400) {
			logger.info("400 Bad request due to 'No data in requested duration' "  );
			test.log(Status.WARNING, "400 Bad request due to 'No data in requested duration' "  );

			test.log(Status.WARNING,MarkupHelper.createLabel(" *********************************************************************************************** ",ExtentColor.LIME));
		} 
		else {
			String resultString = response.getBody().asString();       // APi is calling
			JSONObject inputJSONObject = null;

			try {
				inputJSONObject = new JSONObject(resultString);
				//logger.info("inputJSONObject=> "+ inputJSONObject);

				if(resultString == null) {
					logger.info("FAILed: API has 400 bad request ");
					test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				
					test.log(Status.FAIL,"API is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  )); 
					test.log(Status.FAIL, " *********************************************************************************** ");

				} else {
					JSONArray jsonArray = inputJSONObject.getJSONArray("solutionJson");  

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.FAIL,MarkupHelper.createLabel(" 'solutionJson' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> 'solutionJson'  object is empty from API Response " );
						test.log(Status.FAIL,"API is => " + FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));

						//Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
						test.log(Status.FAIL,"" + getDbQueryResultForGetSolutionApi(pn,eventCode, event_type) );
						test.log(Status.FAIL, "*********************************************************************************** ");	

					} else {   // API is calling
						solutionObj = new ArrayList<SolutionPojo>();
						for(int i = 0 ; i < jsonArray.length(); i++) {
							solutionPojoObj = new SolutionPojo() ;
							String eventCodeVal = jsonArray.getJSONObject(i).getString(key1);     // key1=event_Code
							solutionPojoObj.setEvent_code(eventCodeVal);

							String updateTsVal = jsonArray.getJSONObject(i).getString(key2);     //key2=update_TS
							solutionPojoObj.setInsert_ts(updateTsVal);

							String shortDescriptionVal = jsonArray.getJSONObject(i).getString(key3);     //key3=short_Description
							solutionPojoObj.setShort_description(shortDescriptionVal);
							
							solutionObj.add(solutionPojoObj);
						} // End of for loop 
					} // Inner END IF
				} // Outer END IF
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End  ELSE
		logger.info("*********From API, values are ********* " + solutionObj );
		return solutionObj;

	} // End Method	
	/*
	 * ********* Compare 'short_Description' value based on 'event_Code'  and 'update_TS' values from GetSolution Api response and Seals DB **********
	 */
	@SuppressWarnings("rawtypes")
	public static void compareShortDescriptionFromAPIandDB (Response response, String key1, String key2, String key3, 
			String sn, String pn ,String eventCode, String detectionDate, String eventType, HashSet<String> serialNoList)  
	{
		logger.info("\n***Compare 'short_Description' value based on 'event_Code'  and 'update_TS' value from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("******** Compare short_Description' value based on 'event_Code'  and 'update_TS'value from API response and Database ********",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 " , ExtentColor.RED ));
				logger.info("FAILed: API has 400 bad request ");
				serialNoList.add(sn);
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed Serial_no= " + sn + " and Product_no= " + pn );
				test.log(Status.FAIL,"*****************************************************************************************************");

			} else {
				List apiVal = getShortDescriptionValueFromAPI(response,key1,key2,key3,sn,pn,eventCode,detectionDate,eventType);
				//logger.info("From API, List Values are=> " + apiVal );
				//test.log(Status.PASS,"**** From API, List Values are=> " + apiVal );

				List dbVal = objSql.getShort_descriptionFromDB( pn, eventCode , eventType );						
				//logger.info("From  DB, List Values are=> " + dbVal );
				//test.log(Status.PASS,"**** From DB, Map Values are=> " + dbVal );

				if( apiVal.size() == 0 &&  dbVal.size() == 0 ) {

					logger.info("'solutionJson' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'solutionJson' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'solutionJson' object is Empty, hence Count= " + apiVal.size() );
					test.log(Status.WARNING, "From  DB, 'solutionJson' object is Empty, hence Count= " + dbVal.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( apiVal.size() > 0 &&  dbVal.size() > 0 && apiVal.size() == dbVal.size() ) {
					logger.info("'" + key3 + "' count is same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.PASS,MarkupHelper.createLabel("*** From API, '" + key1 + "'" + " , '" + key2 + "'  and '" + key3 + "' values are *** ", ExtentColor.BROWN) );
					test.log(Status.PASS,"" + apiVal );
					test.log(Status.PASS,MarkupHelper.createLabel("*** From  DB, '" + key1 + "'" + " , '" + key2 + "'  and '" + key3  + "' values are *** " ,ExtentColor.BROWN) );
					test.log(Status.PASS,"" + dbVal);
					test.log(Status.PASS,MarkupHelper.createLabel("'" + key3 + "' count is same From API Response and DB " + " for SN= " + sn + " and PN= " + pn, ExtentColor.GREEN) );

					for(int i = 0 ; i < apiVal.size() ; i++) {
						SolutionPojo  apiObj = (SolutionPojo) apiVal.get(i);
						SolutionPojo   dbObj = (SolutionPojo)  dbVal.get(i);

						compareSolutionJsonFields("event_Code",apiObj.getEvent_code(), dbObj.getEvent_code(), sn, pn,eventCode, detectionDate, eventType , serialNoList, i+1);
						compareSolutionJsonFields("update_TS",apiObj.getInsert_ts(), dbObj.getInsert_ts() , sn, pn,eventCode, detectionDate, eventType, serialNoList, i+1);
						compareSolutionJsonFields("short_Description",apiObj.getShort_description(), dbObj.getShort_description() , sn, pn,eventCode, detectionDate, eventType, serialNoList, i+1);

					} // End if
				} else {
					logger.info("'" + key3 + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key1 + "', '" + key2 + "' and '" + key3 + "' count is " + dbVal.size() );
					test.log(Status.FAIL,"From API, '" + key1 + "', '" + key2 + "' and '" + key3 + "' count is " + apiVal.size() );
					test.log(Status.FAIL,"'" + key3 + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getDbQueryResultForGetSolutionApi(pn,eventCode,eventType) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("******************************* END of Execution ************************************************************************");
				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.INFO,"########################################## END ###########################################");
	}	
	
	
	
	
	// Choose the field and compare the fields value from Api and DB
	@SuppressWarnings("unused")
	private static void compareSolutionJsonFields(String solutionJson_Field, String s1, String s2, String sn,String pn, String eventCode, String detectionDate, String eventType,HashSet<String> serialNoList , int iteration ) 
	{
		switch (solutionJson_Field) {		
		case "event_Code" : compareObjects(solutionJson_Field, s1, s2, sn, pn, eventCode, detectionDate, eventType , serialNoList, iteration); break;              
		case "update_TS" :  compareObjects(solutionJson_Field, s1, s2, sn, pn,eventCode, detectionDate, eventType, serialNoList, iteration) ; break;
		case "short_Description" :  compareObjects(solutionJson_Field, s1, s2, sn, pn, eventCode, detectionDate, eventType, serialNoList, iteration) ; break;
		case "severity" :  compareObjects(solutionJson_Field, s1, s2, sn, pn, eventCode, detectionDate, eventType,serialNoList, iteration) ; break;

		}
	}

	// Compare two objects where each objects have list of values
	private static void compareObjects(String solutionJson_Field, String s1, String s2, String sn, String pn,String eventCode, String detectionDate, String eventType, HashSet<String> serialNoList , int iteration) 
	{		
		logger.info("From API, '"+ solutionJson_Field + "' field Value = " + s1 + "  and From DB, '" + solutionJson_Field + "'  field Value = " + s2);

		if(s1.equals(s2)) {
			test.log(Status.PASS,"From API=>   \"" + solutionJson_Field + "\"   :   \"" + s1 + "\"");
			test.log(Status.PASS,"From  DB=>   \"" + solutionJson_Field + "\"   :   \"" + s2 + "\"");

			logger.info("*************PASS==> '" + solutionJson_Field + "' is matched from API and DB ********\n");
			test.log(Status.PASS,MarkupHelper.createLabel("****** PASS=> \"" + solutionJson_Field + "\"  is matched from API and DB *********** ", ExtentColor.GREEN));

		} else {
			test.log(Status.FAIL,"From API=>   \"" + solutionJson_Field + "\"   :   \"" + s1 + "\"");
			test.log(Status.FAIL,"From  DB=>   \"" + solutionJson_Field + "\"   :   \"" + s2 + "\"");

			logger.info("Fail=>'" + solutionJson_Field + "' is not matched from API and DB ==>\n");
			test.log(Status.FAIL, MarkupHelper.createLabel( "Fail=> '" + iteration + "' th iteration of field, '" + solutionJson_Field + "'   is not matched from API and DB ==>for SN=" + sn + "  & PN=" + pn, ExtentColor.RED) );

			logger.info("API is =>   " + FileandEnv.endAndFile().get("ServerUrl") + 
					APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  )); 

			test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );
			test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + 
					APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));

			// refer query.
			test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
			test.log(Status.FAIL,"" + getDbQueryResultForGetSolutionApi(pn,eventCode,eventType) );

			serialNoList.add(sn);
			test.log(Status.FAIL,"******************************************************************************************");
		}	
	}
//==================================================================================================================	
	/* 
	 * ********* getting 'short_description' value based on 'event_code' and 'insert_ts' value  from API response ***********
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<KeyGetSolution, String> validateKeysFromGetSolutionAPI(Response response, String key, String key1, String key2,
			String key3, String sn, String pn, String eventCode, String detectionDate, String eventType)
	{
		Map<KeyGetSolution, String> multiKeyMap = null;
		KeyGetSolution k12 = null;

		if(response.getStatusCode() == 400) {
			logger.info("400 Bad request due to 'No data in requested duration' "  );
			test.log(Status.WARNING, "400 Bad request due to 'No data in requested duration' "  );

			test.log(Status.WARNING,MarkupHelper.createLabel(" *********************************************************************************************** ",ExtentColor.LIME));

		} 
		else {
			String resultString = response.getBody().asString();       // APi is calling
			JSONObject inputJSONObject = null;

			try {
				inputJSONObject = new JSONObject(resultString);
				//logger.info("inputJSONObject=> "+ inputJSONObject);

				if(resultString == null) {
					logger.info("FAILed: API has 400 bad request ");
					test.log(Status.FAIL,"FAIL--> API has 400 bad request  or  response is error\":[\"An unexpected error happened\"]\r\n" );																				

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));
					test.log(Status.FAIL, " ******************************************************************************************* ");

				} else {
					JSONArray jsonArray = inputJSONObject.getJSONArray(key);  // key=solutionJson

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.FAIL,MarkupHelper.createLabel("'" + key + "' object is empty from API Response ",ExtentColor.LIME) );
						logger.info("Failed ==> '" + key + "'  object is empty from API Response " );

						test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );
						test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));

						// Write query.
						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
						test.log(Status.FAIL,"" + getDbQueryResultForGetSolutionApi(pn,eventCode, eventType) );
						test.log(Status.FAIL, "*******************************************************************************************");	

					}
					else {   // API is calling
						multiKeyMap = new LinkedHashMap<>();
						for(int i = 0 ; i < jsonArray.length(); i++) {

							k12 = new KeyGetSolution(jsonArray, jsonArray);

							Object event_CodeVal = jsonArray.getJSONObject(i).get(key1);    // key1=event_Code
							//logger.info("event_Code : " + event_CodeVal );
							if(event_CodeVal.equals(null)) {
								k12.setEvent_Code(event_CodeVal.toString());
							}else {
								k12.setEvent_Code(event_CodeVal.toString());
							}
														
							Object update_TSVal = jsonArray.getJSONObject(i).getString(key2);     //key2=update_TS
							//logger.info("update_TS : " + update_TSVal );
							if(update_TSVal.equals(null)) {
								k12.setUpdate_TS(update_TSVal.toString());
							}else {
								k12.setUpdate_TS(update_TSVal.toString());
							}
							
							switch(key3) {
							case "short_Description":	
								Object short_DescriptionValue = jsonArray.getJSONObject(i).get(key3);  //key3=short_Description							
								//logger.info("short_Description : " + short_DescriptionValue );
								
								if(short_DescriptionValue.equals(null)) {
									multiKeyMap.put(k12, short_DescriptionValue.toString()); 
									//multiKeyMap.put(k12, "\"short_Description\": \"" +  short_DescriptionValue.toString() + "\"");
								
								}else {
									multiKeyMap.put(k12, short_DescriptionValue.toString());
									//multiKeyMap.put(k12, "\"short_Description\": \"" +  short_DescriptionValue.toString() + "\"");						
								}
								logger.info("short_DescriptionValue= \n" + short_DescriptionValue.toString() + "\n");								
								break;
							
							case "severity": 
								Object severityValue = jsonArray.getJSONObject(i).get(key3);  //key3=severity
								//logger.info("severity : " + severityValue );
								if(severityValue.equals(null) || severityValue.equals("")) {
									multiKeyMap.put(k12, "\"severity\": \"" +  severityValue.toString() + "\"");
								}else {
									//multiKeyMap.put(k12, severityValue.toString());
									multiKeyMap.put(k12, "\"severity\": \"" +  severityValue.toString() + "\"");
								}								
								break;
							}
						} // End of for loop 
					} // Inner END Else
				} // Outer END Else
			} catch (JSONException e) {
				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End  ELSE
		logger.info("*********From API, List values ********* " + multiKeyMap  );
		return multiKeyMap; 
	}	
	
	// ************** Compare 'short_Description' or 'severity' value based on 'event_Code' and 'update_TS' from API with DB *******************
	@SuppressWarnings({ "rawtypes"} )
	public static void compareKeyValueFromAPIandDB (Response response, String key, String key1, 
			String key2,String key3, String sn, String pn ,String eventCode, String detectionDate, String eventType, HashSet<String> serialNoList)  
	{
		logger.info("\n**** Compare '" + key3 + "' value based on 'event_Code' and 'update_TS' values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("****Compare '" + key3 + "' value based on 'event_Code' and 'update_TS' values from API response and Database ****",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 " , ExtentColor.RED ));
				logger.info("FAILed: API has 400 bad request ");
				serialNoList.add(sn);
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				
				test.log(Status.FAIL, "Due to 400 error, Failed serial numbers==>>  " + serialNoList );
				test.log(Status.FAIL,"*******************************************************************************************");

			} else {
				Map<KeyGetSolution, String> apiMapVal = validateKeysFromGetSolutionAPI(response, key, key1, key2, key3, sn, pn , eventCode, detectionDate, eventType );
				//logger.info("From API, Map Values are=> " + apiMapVal );
				test.log(Status.INFO,MarkupHelper.createLabel("********* From API,'" + key1 + "' , '" + key2 + "' and '" + key3 + "' values are ************ ",ExtentColor.GREEN) );
				test.log(Status.PASS,"" + apiMapVal );
				
				Map<KeyGetSolution, String> dbMapVal = objSql.getKeyValueFromSealsDB( pn, eventCode , eventType, key3 );						
				//logger.info("From  DB, Map Values are=> " + dbMapVal );
				test.log(Status.INFO,MarkupHelper.createLabel("********* From  DB,'" + key1 + "' , '" + key2 + "' and '" + key3 + "' values are ************ ",ExtentColor.GREEN) );
				test.log(Status.PASS,"" + dbMapVal );

				if( dbMapVal.size() == 0 &&  apiMapVal.size() == 0 ) {

					logger.info("'" + key + "' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'" + key + "' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, '" + key + "' object is Empty, hence Count= " + apiMapVal.size() );
					test.log(Status.WARNING, "From  DB, '" + key + "' object is Empty, hence Count= " + dbMapVal.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbMapVal.size() > 0 &&  apiMapVal.size() > 0 && dbMapVal.size() == apiMapVal.size() ) {
					logger.info("'" + key3 + "'  count is same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					
					test.log(Status.PASS,"From API, '" + key3 + "' count is " + apiMapVal.size() );
					test.log(Status.PASS,"From  DB, '" + key3 + "' count is " + dbMapVal.size() );
					test.log(Status.PASS,MarkupHelper.createLabel("'" + key3 + "' count is same From API Response and DB " + " for SN= " + sn + " and PN= " + pn, ExtentColor.GREEN) );

					Set keySet = apiMapVal.keySet();
					Iterator keySetIterator = keySet.iterator();

					Set keySet2 = dbMapVal.keySet();
					Iterator keySetIterator2 = keySet2.iterator();

					for(int i = 0 ; keySetIterator.hasNext() || keySetIterator2.hasNext() ;i++) {
						logger.info("Iterating Map in Java using KeySet Iterator");
						Object keyMap = keySetIterator.next();
						logger.info("From API, " + keyMap  );					   
						Object keyMap2 = keySetIterator2.next();
						logger.info("From  DB, " + keyMap2 );

						if(keyMap.equals(keyMap2) ) {
							
							if(apiMapVal.get(keyMap).equals(dbMapVal.get(keyMap2))) {
								logger.info("From API, " + keyMap + " : " + apiMapVal.get(keyMap));
								logger.info("From  DB, " + keyMap2 + " : " + dbMapVal.get(keyMap2));
								logger.info("PASS==> '" + key3 + "' value is matched from API and DB ==>\n");
								test.log(Status.PASS,"From API, value of " + apiMapVal.get(keyMap) );
								test.log(Status.PASS,"From  DB, value of "  + dbMapVal.get(keyMap2)  );
								test.log(Status.PASS,MarkupHelper.createLabel( "PASS=> ***** \"" + key3 + "\" value is matched from API and DB ***** ", ExtentColor.GREEN));

							}else {
								logger.info("Fail, From API, " + keyMap + " : " + apiMapVal.get(keyMap));
								logger.info("Fail, From  DB, " + keyMap2 + " : " + dbMapVal.get(keyMap2));
								logger.info("FAIL==> ****'" + key3 + "' value is not matched from API and DB ==> for " + keyMap + " != " + keyMap2 + "*****");
								test.log(Status.FAIL,"From API, " + keyMap + " : " + apiMapVal.get(keyMap));
								test.log(Status.FAIL,"From  DB, " + keyMap2 + " : " + dbMapVal.get(keyMap2));
								test.log(Status.FAIL,MarkupHelper.createLabel( "****** '" + key3 + "' value is not matched from API and DB ****** ", ExtentColor.RED));
								
								test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );
								test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + 
										APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));
								
								// refer query.
								test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
								test.log(Status.FAIL,"" + getDbQueryResultForGetSolutionApi(pn,eventCode, eventType) );
								
								serialNoList.add(sn);
								test.log(Status.FAIL,MarkupHelper.createLabel("=========================================================================================", ExtentColor.RED));
							} 	
						}else {
							logger.info("Fail, From API, keys are : " + keyMap );
							logger.info("Fail, From  DB, keys are : " + keyMap2);
							logger.info("FAIL==> From above keys, keys are not matched from API and DB ==> for " + keyMap + " != " +keyMap2);
							test.log(Status.FAIL,"From API, keys are :  " + keyMap );
							test.log(Status.FAIL,"From  DB, keys are :  " + keyMap2 );
							test.log(Status.FAIL,MarkupHelper.createLabel( "Fail ****** The above key is not matched from API and DB ****** ", ExtentColor.RED));

							test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));
							
							logger.info("API is =>   " + FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));
							
							// refer query.
							test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
							test.log(Status.FAIL,"" + getDbQueryResultForGetSolutionApi(pn,eventCode, eventType) );
							
							serialNoList.add(sn);
							test.log(Status.FAIL,MarkupHelper.createLabel("##############################################################################################", ExtentColor.RED));
						}
						logger.info("=========================================================================");

					}
				} else {
					logger.info("'" + key3 + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key3 + "' count is " + dbMapVal.size() );
					test.log(Status.FAIL,"From API, '" + key3 + "' count is " + apiMapVal.size() );
					test.log(Status.FAIL,"'" + key3 + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );
					
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));
					
					logger.info("API is =>   " + FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),sn, pn, eventCode, detectionDate  ));
					
					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getDbQueryResultForGetSolutionApi(pn,eventCode, eventType) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("******************************* END of Execution ************************************************************************");
				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	

	
	/*============================================================================================================ */
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
	 * ************************** Private Methods **********************************************************
	 */

	@SuppressWarnings("unused")
	private static String getDbQueryResultForGetSolutionApi( String product_no , String event_code, String event_type) 
	{
		StringBuilder sb = new StringBuilder();	
		sb.append("SELECT *  FROM (\r\n" + 
				"        SELECT S.EVENT_TYPE,S.PRINTER_FAMILY, S.EVENT_CODE,S.SOLUTION_ID,\r\n" + 
				"        S.SHORT_DESCRIPTION,S.LONG_DESCRIPTION,S.POSSIBLE_CAUSE,S.CORRECTIVE_ACTION,S.SEVERITY,S.LANGUAGE_CODE,S.PARTS_REQUESTED,\r\n" + 
				"        S.insert_ts,S.responsible,  row_number() over (partition by S.EVENT_CODE,  S.PRINTER_FAMILY order by S.INSERT_TS desc) as RN\r\n" + 
				"        FROM app_bm_graphics_lf_telemetry.EVENT_SOLUTION S\r\n" + 
				"        INNER JOIN app_bm_graphics_lf_telemetry.PRINTER_FAMILY_D PF ON PF.SUB_SERIES = S.PRINTER_FAMILY\r\n" + 
				"        WHERE PF.PRODUCT_NO =");

		sb.append("'" + product_no + "' " );
		sb.append( "  ) RESULT " );
		sb.append(" WHERE RESULT.RN = 1 and event_code= '" + event_code + "' and event_type='" + event_type + "'" );

		String sql = sb.toString();
		//logger.info("\n<=======Query====> \n" + sql + "\n" );
		return sql;
	}



} // End Class
