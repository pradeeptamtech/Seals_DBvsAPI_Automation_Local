package hp.Seals.MaintenanceTasksApiWithDb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.*;
import org.json.*;
import org.testng.asserts.SoftAssert;

//import com.amazonaws.samples.PostgreSqlConnection;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.APIPath;
import io.restassured.response.Response;

import utils.ExtentReportListener;
import utils.FileandEnv;


public class APIVerificationWithDbMaintenancePage extends ExtentReportListener
{
	final static Logger logger = LogManager.getLogger(APIVerificationWithDbMaintenancePage.class);

	//static PostgreSqlConnection objSql = new PostgreSqlConnection();
	static PostgreSqlConnection_Db objSqlDb = new PostgreSqlConnection_Db();

	static SoftAssert softAssert = new SoftAssert();

	// public methods ********************************************************************

	/*
	 *   Verify each fields based on 'id' and 'date' from *** getMaintenanceTasks API **************
	 */
	public static List<List_maintenancesPojo>  getKey_list_maintenances_FromAPI(Response response, String keyChoice ) throws Throwable 
	{
		String result = response.getBody().asString();

		JSONObject inputJSONObject = null;
		MaintenancePojo  maintenancePojoOb = new MaintenancePojo();
		List_maintenancesPojo maintenancesPojoObj = null;
		try {
			inputJSONObject = new JSONObject(result);

			JSONArray jsonArray = inputJSONObject.getJSONArray("list_maintenances");
			//logger.info("jsonArrays::>>> " + jsonArray + "\n");

			JSONObject jsonObject = null;
			logger.info("jsonArray.length=> " + jsonArray.length());

			for (int i = 0; i < jsonArray.length(); i++)
			{			
				jsonObject = jsonArray.getJSONObject(i);
				//logger.info("jsonObject ==> " + jsonObject);	
				maintenancesPojoObj = new List_maintenancesPojo();
				maintenancesPojoObj.setId(jsonObject.getString("id"));
				maintenancesPojoObj.setDate(jsonObject.getString("date"));

				switch(keyChoice) {

				case "estimated_date_trigger": 
					String estimatedDate = String.valueOf(jsonObject.get(keyChoice)); // estimated_date_trigger
					//logger.info("estimated_date_trigger ==> " + estimatedDate );
					if(estimatedDate != "null"){
						maintenancesPojoObj.setEstimated_date_trigger(dateChange(jsonObject.getString(keyChoice))); // estimated_date_trigger
					} else {
						maintenancesPojoObj.setEstimated_date_trigger(dateChange(estimatedDate));
					}
					//maintenancesPojoObj.setEstimated_date_trigger(jsonObject.getString(keyChoice));	 // estimated_date_trigger
					break;

				case "last_maintenance_date": 
					String lastMaintenanceDate = String.valueOf(jsonObject.get(keyChoice)); // last_maintenance_date
					//logger.info("last_maintenance_date ==> " + lastMaintenanceDate );
					if(lastMaintenanceDate != "null" ) {
						maintenancesPojoObj.setLast_maintenance_date(dateChange(jsonObject.getString(keyChoice)));  // last_maintenance_date
					} else {				
						maintenancesPojoObj.setLast_maintenance_date(dateChange(lastMaintenanceDate));
					}
					break;

				case "user_replaceable": 
					maintenancesPojoObj.setUser_replaceable(String.valueOf(jsonObject.get(keyChoice))); // user_replaceable
					break;

				case "progress_Percentage": 
					maintenancesPojoObj.setProgress_Percentage(String.valueOf(jsonObject.get(keyChoice))); // progress_Percentage
					break;
				case "name": 
					maintenancesPojoObj.setName(jsonObject.getString(keyChoice));  // name
					break;
				case "status": 
					maintenancesPojoObj.setStatus(jsonObject.getString(keyChoice)); // status
					break;									
				}// end switch

				maintenancePojoOb.getMaintenancePojoList().add(maintenancesPojoObj);								
			}	

		} catch (JSONException e) {
			test.log(Status.FAIL, e.fillInStackTrace());	
		}

		logger.info("From API id, date and '" + keyChoice  + "' values List=> \n" + maintenancePojoOb.getMaintenancePojoList().toString() );
		return maintenancePojoOb.getMaintenancePojoList();

	}

	// ************** Compare any one field of  'list_maintenances' based on 'id' and 'date' with DB ********************************************************

	public static void compareKeyOfListMaintenancesFromApiAndDB (Response response, String key, String sn, String pn ,String date, HashSet<String> serialNoList) throws Throwable 
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

				List<List_maintenancesPojo> apiList = getKey_list_maintenances_FromAPI(response, key);
				//logger.info("'id', 'date' and '" + key + "' values From API Response =>> " + apiList.toString() + "\n");

				List<List_maintenancesPojo> dbList = objSqlDb.getMaintenancesFromDB(sn,pn,date , key);				
				//logger.info(" 'id', 'date' and '" + key + "' values From Database =>> \n" + dbList.toString());

				//test.log(Status.INFO, MarkupHelper.createLabel("******************************** Below is API  Results ***************************************", ExtentColor.BROWN) );
				Collections.sort(apiList , new MaintenanceTasksSort() );
				logger.info("After Sorting  'id', 'date' and '" + key + "' values From API Response =>> " + apiList.toString() + "\n"); 
				//test.log(Status.PASS," After Sorting 'id', 'date' and '" + key + "' values From API Response =>> " + apiList.toString());
				//test.log(Status.PASS,"" + apiList.toString());

				//test.log(Status.INFO, MarkupHelper.createLabel("******************************* Below is  DB   Results ***********************************", ExtentColor.GREEN) );
				Collections.sort(dbList , new MaintenanceTasksSort() );
				logger.info("After sorting 'id', 'date' and '" + key + "' values From Database =>> \n" + dbList.toString());
				//test.log(Status.PASS,"After Sorting 'id', 'date' and '" + key + "' values From Database =>> " + dbList.toString());
				//test.log(Status.PASS,"" + dbList.toString());

				if( dbList.size() == 0 &&  apiList.size() == 0 ) {
					logger.info("'list_maintenances' object is Empty From API Response and DB " );				
					test.log(Status.WARNING, "'list_maintenances' object is Empty From API Response and DB for this  Serial_No = " + sn);
					test.log(Status.WARNING, "From API, 'list_maintenances' object is Empty, so Count= " + apiList.size() );
					test.log(Status.WARNING, "From  DB, 'list_maintenances' object is Empty, so Count= " + dbList.size() );
					test.log(Status.WARNING,"*******************************************************************************************************");

				} else if( dbList.size() > 0 &&  apiList.size() > 0 && dbList.size() == apiList.size() ) {
					for(int i = 0 ; i < apiList.size() ; i++) {
						List_maintenancesPojo  apiObj = dbList.get(i);
						List_maintenancesPojo  dbObj = apiList.get(i);

						compareList_Maintenances("id",apiObj.getId(), dbObj.getId(), sn,pn,date, serialNoList, i+1);
						compareList_Maintenances("date",apiObj.getDate(), dbObj.getDate() , sn, pn,date, serialNoList, i+1);

						switch(key) {
						case "estimated_date_trigger": 
							compareList_Maintenances("estimated_date_trigger",apiObj.getEstimated_date_trigger(), dbObj.getEstimated_date_trigger() , sn, pn, date , serialNoList, i+1);
							break;
						case "last_maintenance_date":
							compareList_Maintenances("last_maintenance_date",apiObj.getLast_maintenance_date(), dbObj.getLast_maintenance_date() , sn, pn, date , serialNoList, i+1);
							break;
						case "user_replaceable":
							compareList_Maintenances("user_replaceable",apiObj.getUser_replaceable(), dbObj.getUser_replaceable() , sn, pn, date , serialNoList, i+1);
							break;
						case "progress_Percentage":	
							compareList_Maintenances("progress_Percentage",apiObj.getProgress_Percentage(), dbObj.getProgress_Percentage() , sn, pn, date , serialNoList, i+1);
							break;
						case "name":
							compareList_Maintenances("name",apiObj.getName(), dbObj.getName() , sn, pn, date , serialNoList, i+1);
							break;
						case "status":	      
							compareList_Maintenances("status",apiObj.getStatus(), dbObj.getStatus() , sn, pn, date , serialNoList, i+1);
							break;
						} // End switch
					} // End if
				} else {

					logger.info("'" + key + "'  count is not same From API Response and DB " + "for SN= " + sn + " and PN= " + pn );
					test.log(Status.FAIL,"From  DB, '" + key + "' count is " + dbList.size() );
					test.log(Status.FAIL,"From API, '" + key + "' count is " + apiList.size() );
					test.log(Status.FAIL,"'" + key + "' count is not same From API Response and DB " + " for SN= " + sn + " and PN= " + pn );

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the response from below API URL ************ ", ExtentColor.RED) );
					test.log(Status.FAIL,"API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setGetMaintenanceTaskUrl(
							APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date)); 

					logger.info("API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setGetMaintenanceTaskUrl(
							APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date )); 
					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Check the result from below SQL Query *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + MaintenanceTasks_ListMaintenancesDB(sn,pn,date) );

					serialNoList.add(sn);
					test.log(Status.FAIL,MarkupHelper.createLabel("******************************************************************************************",ExtentColor.RED ));
					logger.info("*******************************END of Execution ************************************************************************");

				} // End inner else
			} // End outer else

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}	

	//============================================= Public methods END ======================================================================	
	/*
	 * ############################################  Private methods #######################################################
//####################### Private methods for the Api value ###############################################################	 
	 */
	// Choose the field and compare the fields value from Api and DB
	private static void compareList_Maintenances(String list_maintenances_Field, String s1, String s2, String sn,String pn,String date, HashSet<String> serialNoList , int iObject ) 
	{
		switch (list_maintenances_Field) {
		case "id": compareObjects(list_maintenances_Field,s1,s2, sn,pn,date, serialNoList, iObject); break;              
		case "date":  compareObjects(list_maintenances_Field,s1,s2, sn,pn,date, serialNoList, iObject) ; break;
		case "status":  compareObjects(list_maintenances_Field,s1,s2, sn,pn,date, serialNoList, iObject);  break;
		case "name":   compareObjects(list_maintenances_Field,s1,s2, sn,pn,date, serialNoList, iObject);  break;
		case "progress_Percentage":   compareObjects(list_maintenances_Field,s1,s2, sn,pn,date, serialNoList, iObject);  break;
		case "user_replaceable":  compareObjects(list_maintenances_Field,s1,s2, sn,pn,date, serialNoList, iObject);  break;   
		case "last_maintenance_date": compareObjects(list_maintenances_Field,s1,s2, sn,pn,date, serialNoList, iObject); break;	
		case "estimated_date_trigger": compareObjects(list_maintenances_Field,s1,s2, sn,pn,date, serialNoList, iObject); break; 
		}
	}

	// Compare two objects where each objects have list of values
	private static void compareObjects(String list_maintenances_Field, String s1, String s2, String sn, String pn, String date, HashSet<String> serialNoList , int iObject) 
	{		
		logger.info("From API, '"+ list_maintenances_Field + "' field Value = " + s1 + "  and From DB, '" + list_maintenances_Field + "'  field Value = " + s2);
		test.log(Status.PASS,"From API->   '" + list_maintenances_Field + "'   :   " + s1 );
		test.log(Status.PASS,"From  DB->   '" + list_maintenances_Field + "'   :   " + s2 );

		if(s1.contains(s2)) {
			logger.info("PASS==> '" + list_maintenances_Field + "' is matched from API and DB ==>\n");
			test.log(Status.PASS,MarkupHelper.createLabel( "PASS=> '" + list_maintenances_Field + "' is matched from API and DB ==> ", ExtentColor.GREEN));
		} else {
			serialNoList.add(sn);
			logger.info("Fail=>" + list_maintenances_Field + " is not matched from API and DB ==>\n");
			test.log(Status.FAIL, iObject + "th iteration of object '" + list_maintenances_Field + "'  is not matched " );
			test.log(Status.FAIL,"From API,     " + list_maintenances_Field + "  :  " + s1  );
			test.log(Status.FAIL,"From  DB,     " + list_maintenances_Field + "  :  " + s2  );
			test.log(Status.FAIL, "Fail=> Field   '" + list_maintenances_Field + "'  is not matched from API and DB ==>for SN=" + sn + "  & PN=" + pn );

			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

			test.log(Status.FAIL,"API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setGetMaintenanceTaskUrl(
					APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date)); 

			logger.info("API Is =>   " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setGetMaintenanceTaskUrl(
					APIPath.apiPath.GET_MAINTENANCE_TASK.toString(), sn ,pn ,date )); 
			// refer query.
			test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query result*************** ", ExtentColor.RED) );
			test.log(Status.FAIL,"" + MaintenanceTasks_ListMaintenancesDB(sn,pn,date) );
			test.log(Status.FAIL,"******************************************************************************************");
		}	
	}


	/*
	 *************************************** Private Methods **************************************************
	 */	
	private static String dateChange(String inputDate ) throws Throwable 
	{
		String outputDate = "";
		if(inputDate != "null" ) {		
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");		
			Date date2;
			date2 = dateFormat.parse(inputDate);
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			inputDate=sdf2.format(date2.getTime());
			Date endDate = sdf2.parse(inputDate);
			outputDate = sdf2.format(endDate);

		}else {
			outputDate = "null";			
		}

		//	logger.info("outputDate ----->  " + outputDate);
		return outputDate;
	}


	/**********************************************************************************************************************
 //************************************* Private method for DB Query *******************************************************
	 */
	private static String  MaintenanceTasks_ListMaintenancesDB(String sn, String pn , String date ) 
	{		
		String eDate = dateChange1(date);
		String date1 = startDateChange(date);
		StringBuilder sb = new StringBuilder();
		// Write query.

		sb.append("SELECT * from  (\r\n" + 
				"      Select \r\n" + 
				"	distinct serial_no, product_no, estimated_maint_date AS estimated_date_trigger,   last_maint_date AS last_maintenance_date,\r\n" + 
				"	maintenance_category AS user_replaceable , round(current_progress,0) progress_Percentage, maintenance_type AS id,\r\n" + 
				"	trunc(insert_ts)  date,\r\n" + 
				"	trunc(insert_ts) insert_ts  ,  friendly_name AS name ,  severity AS status,\r\n" + 
				"		 row_number() over (partition by maintenance_type, trunc(insert_ts) ORDER BY insert_ts desc ) AS group_idx\r\n" + 
				"             FROM app_bm_graphics_lf_telemetry.maintenance_estimation_result\r\n" + 
				"      WHERE serial_no = ");

		sb.append("'" + sn + "'" + " AND product_no=" + "'" + pn + "'" );
		sb.append( " and insert_ts >= '" + date1 + "' AND insert_ts <= '" + eDate + "'");
		sb.append("  )  WHERE group_idx=1   ORDER BY insert_ts ");

		String sql = sb.toString();
		logger.info("\n<=======SQL Query====> \n" + sql + "\n" );

		return sql;
	} 

	private static String dateChange1(String sDate )  {

		String[] dateParts = sDate.split("T");
		//		for (String a : dateParts) 
		//           		System.out.println("DATE: " + a.toString());		
		String s1 = dateParts[0];
		//System.out.println("Change start DATE: " + s1 );

		String endDate = s1 + " 23:59:59"; 
		//System.out.println("\n EndDate ----->  " + endDate);		

		return endDate;
	}

	private static String startDateChange(String date )  {

		String[] dateParts = date.split("T");
		//		for (String a : dateParts) 
		//           		System.out.println("DATE: " + a.toString());		
		String s1 = dateParts[0];
		//System.out.println("Change start DATE: " + s1 );

		String startDate = s1 + " 00:00:00"; 
		//System.out.println("\n startDate ----->  " + startDate);		

		return startDate;
	}

	//End Private Method 




} // End Class
