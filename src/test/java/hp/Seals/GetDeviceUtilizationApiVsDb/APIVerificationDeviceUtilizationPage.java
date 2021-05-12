package hp.Seals.GetDeviceUtilizationApiVsDb;

import java.util.*;

import org.apache.log4j.*;

import org.json.*;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.amazonaws.samples.PostgreSqlConnection;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.APIPath;
import hp.Seals.getErrorEventsApiVsDbTest.ErrorEventsSeverityPojo;
import io.restassured.response.Response;
import printheadDetails.warrantyStatus.*;
import printheadDetails.warrantyStatus.SortByPhSerialNoWithStatus;
import utils.ExtentReportListener;
import utils.FileandEnv;
import utils.UtilityApiMethods;


public class APIVerificationDeviceUtilizationPage extends ExtentReportListener
{
	final static Logger logger = LogManager.getLogger(APIVerificationDeviceUtilizationPage.class);

	static PostgreSqlConnectionDb objSql = new PostgreSqlConnectionDb();

	static SoftAssert softAssert = new SoftAssert();

	// public methods

	/*
	 * ************ getting 'key'  value from  API ***********************
	 */
	@SuppressWarnings({ "unchecked" })
	public static String getKeyValuesFromAPI(Response response, String key ,String sn,String pn, String startDate, String endDate, List  serialNoList)
	{
		String keyVal = "";
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				

				serialNoList.add(sn);
			} else {	
				JSONObject jsonObj = new JSONObject(response.getBody().asString());

				if (jsonObj.has(key) && jsonObj.get(key) != null) {

					keyVal = String.valueOf((jsonObj.get(key)));
					//logger.info("keyVal  is ==> " + keyVal);
					softAssert.assertEquals( keyVal,sn , "Serial_No is not matched");

					if((keyVal != null || keyVal != "") && keyVal.contentEquals( sn ))	{						
						//	test.log(Status.PASS, "" + key + "\":" + "" + keyVal + "");
						logger.info("Display SerialNo value ==> \"" + key + "\":" + "\"" + keyVal + "\"");

					}else {
						test.log(Status.FAIL, MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), sn, pn, startDate, endDate), ExtentColor.RED ));

						test.log(Status.FAIL, MarkupHelper.createLabel( "Actual '" + key + "' : " + keyVal + " <==> Expected '" + key + "' : " + sn, ExtentColor.RED ));
						logger.info("**FAILed due to Expected 'serial_Number' : " + keyVal + " <==> '" + key + "' : " + sn);

						serialNoList.add(sn);
					}
				} else {
					test.log(Status.FAIL, "No Expected Response due to 'An unexpected error happened' ");
				}
			}
		}catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		//logger.info("keyval= " + keyVal);
		return keyVal;
	}

	/*
	 * *********** Compare 'serial_Number' value from GetDeviceUtilization API and Seals DB *******************
	 */
	@SuppressWarnings("unchecked")
	public static void compareSerialNoFromDeviceUtilizationApiVsDB( Response response, String key ,String sn, String pn, String startDate, String endDate, List<String>  serialNoList) 
	{
		logger.info("******************* Compare the '\" + key + \"' value from API response and DB ***********************");
		test.log(Status.INFO, MarkupHelper.createLabel("******************* Compare the '" + key + "' value from API response and Database ***************",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				

				serialNoList.add(sn);
			} else {		
				String srNoFromApi  = getKeyValuesFromAPI( response,key, sn, pn, startDate, endDate ,serialNoList);	
				logger.info("From API, '" + key + "' : " + srNoFromApi );

				String  srNoFromDB = objSql.getSerialNoValueFromDB(sn,pn,startDate,endDate);
				logger.info("From  DB, '" + key + "' : " + srNoFromDB );
				
				if( (srNoFromApi.isEmpty() || srNoFromApi == "" ) && srNoFromDB == "") {
						test.log(Status.WARNING, "*** WArning ==> '" + key + "' value is not matched from API and Seals_DB *** " );
						test.log(Status.WARNING,"Warning , API has response with serial number as blank or empty" );
						test.log(Status.WARNING,"From API, serial_Number value => '" + key + "' : " + srNoFromApi );
						test.log(Status.WARNING,"Warning , DB Query has No response for the given data=>   SN= " + sn + "  and  PN= " + pn );
						test.log(Status.WARNING,MarkupHelper.createLabel("#############################################################################################", ExtentColor.LIME) );
						
					} else if( (srNoFromApi.equals(sn)) && (srNoFromDB == ""  ||  srNoFromDB.isEmpty()) ) {
						test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> '" + key + "' value is not matched from API and Seals_DB  due to DB_Query has No response*** ",ExtentColor.RED) );
						test.log(Status.FAIL,"Reason-> API has response with serial number=> " + key + " : " + srNoFromApi );
						test.log(Status.FAIL,"Reason-> DB Query has No response for the given data=>   SN= " + sn + "  and  PN= " + pn );
						//test.log(Status.FAIL,"Reason-> From  DB,  ==> " + key + "' : " + srNoFromDB );			
						test.log(Status.FAIL,MarkupHelper.createLabel("########################################## End ###############################################", ExtentColor.RED) );
					
				} else 	if(srNoFromApi != null && srNoFromDB != null) {
					
					test.log(Status.PASS, "Entered 	 '" + key + "' : " + sn );
					test.log(Status.PASS, "From API, '" + key + "' : " + srNoFromApi );
					test.log(Status.PASS, "From  DB, '" + key + "' : " + srNoFromDB);

					if( srNoFromApi.equals(srNoFromDB)  && srNoFromDB.equals(sn) && srNoFromApi.equals(sn)  ) {
						test.log(Status.PASS,MarkupHelper.createLabel("Entered  '" + key + "' value is matched with API and Seals_DB",ExtentColor.GREEN) );
						logger.info("*** PASS=> Entered '" + key + "' value is matched  With API and DB  ***\n" );

					} else {

						test.log(Status.FAIL, MarkupHelper.createLabel( "*** Entered '" + key + "' value is not matched from API and Seals_DB *** ",ExtentColor.RED) );
						logger.info("*** FAIL, Entered=>'\" + key + \"' value is not matched  from API and DB *** \n");				

						test.log(Status.FAIL, MarkupHelper.createLabel(" Below  is '" + key + "'  value from API =====> ", ExtentColor.RED) );
						test.log(Status.FAIL, key + " : " + srNoFromApi );

						test.log(Status.FAIL, MarkupHelper.createLabel("Below  is   '" + key + "' value from Seals_DB =====> ", ExtentColor.RED) );
						test.log(Status.FAIL, key + " : " + srNoFromDB);

						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );
						test.log(Status.FAIL,MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), sn, pn, startDate,endDate), ExtentColor.RED  ));
						//  query.
						test.log(Status.FAIL,MarkupHelper.createLabel("**************** Refer below SQL Query Result ************************ ", ExtentColor.RED) );
						test.log(Status.FAIL,"" + getQueryForDeviceUtilization(sn, pn, startDate, endDate) );

						serialNoList.add(sn);
						test.log(Status.PASS,MarkupHelper.createLabel("#############################################################################################", ExtentColor.RED) );
					}
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.INFO, MarkupHelper.createLabel("############################# Execution Completed for One Product ############################### ", ExtentColor.TEAL) );

	} // End Method

	/*
	 * ************ getting 'productive_Hours' and 'on_Hours' values from GetDeviceUtilization API ***********************
	 */
	public static double getHoursValuesFromDeviceUtilizationAPI(Response response, String key ,String serialNo,String productNo,String startDate, String endDate, List serialNoList)
	{
		double dval = 0.0;
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				

				serialNoList.add(serialNo);
			} else {	
				JSONObject jsonObj = new JSONObject(response.getBody().asString());

				if (jsonObj.has(key) && jsonObj.get(key) != null) {

					String keyVal = String.valueOf((jsonObj.get(key)));
					dval = Double.parseDouble(keyVal);

					if(dval >= 0 )	{						
						//test.log(Status.PASS, " " + key + " : " + dval);
						logger.info("Display  value ==> " + key + " : " + dval);

					}else {
						test.log(Status.FAIL, MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(),
										serialNo,productNo,startDate,endDate),ExtentColor.RED ));

						test.log(Status.FAIL, MarkupHelper.createLabel( "Expected '"+ key + "' < 0  <==> " + key + " : " + dval , ExtentColor.RED ));
						logger.info("**FAILed due to'" + key + "' < 0  <==> " + key + " : " + dval);

						serialNoList.add(serialNo);
					}
				} else {
					test.log(Status.FAIL, "No Expected Response due to 'An unexpected error happened' ");
				}
			}
		}catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		//logger.info("dval= " + dval);
		return dval;
	}

	/*
	 * Verify 'productive_hours' value is  double type and >= 0
	 */
	public static double getProductive_HoursFromDeviceUtilizationAPI(Response response, String key ,String serialNo,String productNo,String startDate, String endDate, List serialNoList)
	{
		double dval = 0.0;
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");
				test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n "
						+ " Hence this Product may be invalid." );																				

				serialNoList.add(serialNo);
			} else {	
				JSONObject jsonObj = new JSONObject(response.getBody().asString());

				if (jsonObj.has(key) && jsonObj.get(key) != null) {

					String keyVal = String.valueOf((jsonObj.get(key)));
					dval = Double.parseDouble(keyVal);

					if(dval >= 0 )	{						
						//test.log(Status.PASS, " " + key + " : " + dval);
						logger.info("Display 'productive_hours' value ==> " + key + " : " + dval);

					}else {
						test.log(Status.FAIL, MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(),
										serialNo,productNo,startDate,endDate),ExtentColor.RED ));

						test.log(Status.FAIL, MarkupHelper.createLabel( "Expected 'productive_hours' > 0 ==> " + key + ": " + dval , ExtentColor.RED ));

						logger.info("**FAILed due to 'productive_hours' < 0  <==> " + key + " : " + dval);

						serialNoList.add(serialNo);
					}
				} else {
					test.log(Status.FAIL, "No Expected Response due to 'An unexpected error happened' ");
				}
			}
		}catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		//logger.info("dval= " + dval);
		return dval;
	}
	/*
	 * Verify on_hours value is greater than 'productive_hours'
	 */
	public static double getOn_HoursGreaterThanProductiveHours(Response response, String key, String key2 ,String serialNo,String productNo,String startDate, String endDate, List serialNoList)
	{
		JSONObject jsonObj = new JSONObject(response.getBody().asString());
		double valOnHours = 0.0;
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");
				//test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n" );																				

				serialNoList.add(serialNo);

			} else if (jsonObj.has(key) && jsonObj.get(key) != null) {

				String keyVal = String.valueOf((jsonObj.get(key)));
				valOnHours = Double.parseDouble(keyVal);				

				double valProductiveHours = getProductive_HoursFromDeviceUtilizationAPI(response,key2 ,serialNo,productNo,startDate, endDate,serialNoList);

				if(valOnHours >= 0 && valProductiveHours > 0 && valOnHours >= valProductiveHours )
				{		
					test.log(Status.PASS, " " + key + " : " + valOnHours);
					logger.info("Display 'on_hours' value ==> " + key + " : " + valOnHours);

				} else if( valOnHours == 0.0 && valProductiveHours == 0.0 ) {

					logger.info("Display 'on_hours' value ==> " + key + " : " + valOnHours);
					test.log(Status.WARNING, "" + key + " : " + valOnHours 
							+ "  &  productive_hours : " + valProductiveHours );

					test.log(Status.WARNING, MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(),
									serialNo,productNo,startDate,endDate), ExtentColor.ORANGE  ));

					test.log(Status.WARNING, MarkupHelper.createLabel("Warning:  due to on_hours: " + valOnHours 
							+ "  ==  " + " productive_hours: " + valProductiveHours, ExtentColor.ORANGE ));

					logger.info(" Warning due to on_hours: " + valOnHours + "  == " + "productive_hours: " + valProductiveHours );

					serialNoList.add(serialNo);


				} else {

					//logger.info("Display 'on_hours' value ==> " + key + ": " + valOnHours);
					test.log(Status.FAIL, "Actual 'on_hours' value ==>  " + key + ": " + valOnHours + "  & 'productive_hours' value==> " + valProductiveHours );

					test.log(Status.FAIL,MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(),
									serialNo,productNo,startDate,endDate), ExtentColor.RED  ));

					test.log(Status.FAIL,MarkupHelper.createLabel("Failed due to on_hours: " + valOnHours + " is less than " + " productive_hours: " + valProductiveHours, ExtentColor.RED ));

					logger.info(" FAILed due to on_hours: " + valOnHours + " is less than " + "productive_hours: " + valProductiveHours );

					serialNoList.add(serialNo);
				}
			} else {
				test.log(Status.FAIL, "No Expected Response due to 'An unexpected error happened' , so Key is not available");
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		return valOnHours;
	}

	/*
	 * *********** Compare 'productive_Hours' value from GetDeviceUtilization API and Seals DB *******************
	 */
	public static void compareProductiveHoursFromAPIandDB( Response response, String key ,String sn, String pn, String startDate, String endDate, List serialNoList) 
	{
		logger.info("*******************Compare the 'productive_Hours' value from API response and DB ***********************");
		test.log(Status.INFO, MarkupHelper.createLabel("******************* Compare the 'productive_Hours' value from API response and Database ***************",ExtentColor.BLUE) );
		try {

			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");
				//test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n" );																				

				serialNoList.add(sn);

			} else {
				//double productive_Hours_valueFromAPI  = getProductive_HoursFromDeviceUtilizationAPI( response,key, sn, pn, startDate, endDate ,serialNoList);
				double productive_Hours_valueFromAPI  = getHoursValuesFromDeviceUtilizationAPI( response,key, sn, pn, startDate, endDate ,serialNoList);	
				logger.info("From API, Original value => " + key + " : " + productive_Hours_valueFromAPI );

				double  productive_Hours_valueFromDB = objSql.getProductiveHoursValueFromDB(sn,pn,startDate,endDate);
				logger.info("From Seals_DB, Original value => " + key + " : " + productive_Hours_valueFromDB );

				if(productive_Hours_valueFromAPI == 0 && productive_Hours_valueFromDB == 0) {
					test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'productive_Hours' value is not matched from API and Seals_DB *** due to Query has No response *** ",ExtentColor.RED) );
					test.log(Status.FAIL, "From DB, Query has No response for the given data=>   SN= " + sn + "  and  PN= " + pn );
					test.log(Status.FAIL,"From API, gives the response with this productive_Hours value => '" + key + "' : " + productive_Hours_valueFromAPI );
					//test.log(Status.FAIL,"From  DB, productive_Hours value => '" + key + "' : " + productive_Hours_valueFromDB );			
					
					test.log(Status.FAIL,MarkupHelper.createLabel("######################################## END ################################################", ExtentColor.RED) );
				
				} else if(productive_Hours_valueFromAPI > 0 && productive_Hours_valueFromDB > 0) {
					
					test.log(Status.PASS, "From API, Original value => " + key + " : " + productive_Hours_valueFromAPI);
					test.log(Status.PASS, "From Seals_DB, Original value => " + key + " : " + productive_Hours_valueFromDB);

					double apiproductiveHoursValue  = getDoubleWithPrecision2(productive_Hours_valueFromAPI);
					logger.info("From API, Taking upto one Precision Value => " + key + " : " + apiproductiveHoursValue );
					test.log(Status.PASS,"From API, Taking upto one Precision Value => " + key + " : " + apiproductiveHoursValue);

					double dBproductiveHoursValue  = getDoubleWithPrecision2(productive_Hours_valueFromDB);
					logger.info("From Seals_DB, Taking upto 1 Precision Value => " + key + " : " + dBproductiveHoursValue );
					test.log(Status.PASS, "From Seals_DB, Taking upto one Precision Value => " + key + " : " + dBproductiveHoursValue);

					if( apiproductiveHoursValue == dBproductiveHoursValue ) {
						test.log(Status.PASS,MarkupHelper.createLabel("'productive_Hours' value is matched from API and Seals_DB",ExtentColor.GREEN) );
						//test.log(Status.PASS,MarkupHelper.createLabel("########################################### PASS ####################################", ExtentColor.GREY) );
						logger.info("*** PASS=>'productive_Hours' value is matched  from API and DB ***\n" );

					} else {

						test.log(Status.FAIL, MarkupHelper.createLabel( "'productive_Hours' value is not matched from API and Seals_DB *** for 'serial_no'=  " + sn , ExtentColor.RED) );
						logger.info("*** FAIL =>'productive_Hours' value is not matched  from API and DB *** \n");				

						test.log(Status.FAIL, MarkupHelper.createLabel(" Below  is  'productive_Hours' value from API =====> ", ExtentColor.RED) );
						test.log(Status.FAIL, key + " : " + apiproductiveHoursValue);

						test.log(Status.FAIL, MarkupHelper.createLabel("Below  is   'productive_Hours' value from Seals_DB =====> ", ExtentColor.RED) );
						test.log(Status.FAIL, key + " : " + dBproductiveHoursValue);

						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );
						test.log(Status.FAIL,MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), sn, pn, startDate,endDate), ExtentColor.RED  ));
						//  query.
						test.log(Status.FAIL,MarkupHelper.createLabel("**************** Refer below SQL Query Result ************************ ", ExtentColor.RED) );
						test.log(Status.FAIL,"" + getQueryForDeviceUtilization(sn, pn, startDate, endDate) );

						serialNoList.add(sn);
					}
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.INFO, MarkupHelper.createLabel("############################# Execution Completed for One Product ############################### ", ExtentColor.TEAL) );
		//test.log(Status.INFO,MarkupHelper.createLabel("*********************** Execution  Completed  for  One Set of DATA  ********************** ",ExtentColor.ORANGE) );
	} // End Method


	/*
	 * *********** Compare 'on_Hours' value from GetDeviceUtilization API and Seals DB *******************
	 */
	@SuppressWarnings("unchecked")
	public static void compareOnHoursFromAPIandDB( Response response, String key ,String sn, String pn, String startDate, String endDate, List serialNoList) 
	{
		logger.info("*******************Compare the 'on_Hours' value from API response and DB ***********************");
		test.log(Status.INFO, MarkupHelper.createLabel("******************* Compare the 'on_Hours' value from API response and Database ***************",ExtentColor.BLUE) );
		try {
			if(400 == response.getStatusCode() ) {
				logger.info("Failed due to Status Code ==> " + response.getStatusCode());
				//test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));
				logger.info("FAILed: API has 400 bad request ");
				//test.log(Status.FAIL,"As API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n" );																				

				serialNoList.add(sn);

			} else {
				double on_Hours_valueFromAPI  = getHoursValuesFromDeviceUtilizationAPI( response,key, sn, pn, startDate, endDate ,serialNoList);
				logger.info("From API, Original value => " + key + " : " + on_Hours_valueFromAPI );

				double  on_Hours_valueFromDB = objSql.getOnHoursValueFromDB(sn,pn,startDate,endDate);
				logger.info("From Seals_DB, Original value => " + key + " : " + on_Hours_valueFromDB );

				if(on_Hours_valueFromAPI == 0 && on_Hours_valueFromDB == 0) {
					test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'on_Hours' value is not matched from API and Seals_DB *** due to Query has No response *** ",ExtentColor.RED) );
					test.log(Status.FAIL, "From DB, Query has No response for the given data=>   SN= " + sn + "  and  PN= " + pn );
					test.log(Status.FAIL,"From API, gives the response with this on_Hours value => '" + key + "' : " + on_Hours_valueFromAPI );
					//test.log(Status.FAIL,"From  DB, productive_Hours value => '" + key + "' : " + on_Hours_valueFromDB );			
					
					test.log(Status.FAIL,MarkupHelper.createLabel("######################################## END ################################################", ExtentColor.RED) );
				
				} else 	if(on_Hours_valueFromAPI > 0 && on_Hours_valueFromDB > 0) {
					test.log(Status.PASS, "From API, Original value => " + key + " : " + on_Hours_valueFromAPI);
					test.log(Status.PASS, "From Seals_DB, Original value => " + key + " : " + on_Hours_valueFromDB);

					double apiOnHoursValue  = getDoubleWithPrecision2(on_Hours_valueFromAPI);
					logger.info("From API, Taking upto one Precision Value => " + key + " : " + apiOnHoursValue );
					test.log(Status.PASS,"From API, Taking upto one Precision Value => " + key + " : " + apiOnHoursValue);

					double dBonHoursValue  = getDoubleWithPrecision2(on_Hours_valueFromDB);
					logger.info("From Seals_DB, Taking upto one Precision Value => " + key + " : " + dBonHoursValue );
					test.log(Status.PASS, "From Seals_DB, Taking upto one Precision Value => " + key + " : " + dBonHoursValue);

					if( apiOnHoursValue == dBonHoursValue ) {
						test.log(Status.PASS,MarkupHelper.createLabel("'on_Hours' value is matched from API and Seals_DB",ExtentColor.GREEN) );
						//test.log(Status.PASS,MarkupHelper.createLabel("########################################### PASS ####################################", ExtentColor.GREY) );
						logger.info("*** PASS=>'on_Hours' value is matched  from API and DB ***\n" );

					} else {

						test.log(Status.FAIL, MarkupHelper.createLabel( " 'on_Hours' value is not matched from API and Seals_DB *** 'serial_no'=  " + sn , ExtentColor.RED) );
						logger.info("*** FAIL =>'on_Hours' value is not matched  from API and DB *** \n");				

						test.log(Status.FAIL, MarkupHelper.createLabel(" Below  is  'on_Hours' value from API =====> ", ExtentColor.RED) );
						test.log(Status.FAIL, key + " : " + apiOnHoursValue);

						test.log(Status.FAIL, MarkupHelper.createLabel("Below  is   'on_Hours' value from Seals_DB =====> ", ExtentColor.RED) );
						test.log(Status.FAIL, key + " : " + dBonHoursValue);

						test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );
						test.log(Status.FAIL,MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetDeviceUtilizationUrl(APIPath.apiPath.Get_DeviceUtilization.toString(), sn, pn, startDate,endDate), ExtentColor.RED  ));
						//  query.
						test.log(Status.FAIL,MarkupHelper.createLabel("**************** Refer below SQL Query Result ************************ ", ExtentColor.RED) );
						test.log(Status.FAIL,"" + getQueryForDeviceUtilization(sn, pn, startDate, endDate) );

						serialNoList.add(sn);
					}
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.INFO, MarkupHelper.createLabel("############################# Execution Completed for One Product ############################### ", ExtentColor.TEAL) );
		logger.info("############################# Execution Completed for One Product ############################### " );
	} // End Method


	/*
	 * ******************************************************************************************************************
	 */
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

	public static List<String> verifyObjectsKey_Negative(Response response, String listObj, String key, List serialNoList, String serialNo,int choice) 
	{	
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		//logger.info("object count: " + list.size());
		test.log(Status.PASS, "No of Json Objects ===> " + list.size());

		List alist = new ArrayList();
		int count = 0 ;
		try {

			if (list.size() > 0) 
			{
				for (int i = 0; i < list.size(); i++) 
				{
					String actualVal = String.valueOf(list.get(i).get(key));
					if( key != null && actualVal != null ) 
					{
						alist.add(actualVal);
						try {
							Float floatVal = Float.valueOf(actualVal).floatValue();
							//   System.out.println("Float: " + floatVal);
							if( floatVal  < 0 )
							{
								serialNoList.add(serialNo);
								logger.info("Key value is negative...> " + key + "[" + i + "] : " + actualVal + " AND SerialNo=> " + serialNoList);
								test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + actualVal + "AND serialNoList=" + serialNoList );
							}
						}catch(Exception e) {
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
	private static double getDoubleWithPrecision2(double d ) {

		double fract = d - (long) d;
		fract = (long) (fract * 1e1 + 0.5) / 1e1; // round to the 2 decimal places.
		//System.out.println("\n fract= "  + fract );
		double integralPart = (int)d ;
		//System.out.println("\n integralPart= "  + integralPart );
		double total = integralPart + fract;
		//System.out.println("\n total= "  + total );

		return total;
	}


	private static String getQueryForDeviceUtilization(String serial_no, String product_no , String start_ts,String end_ts ) 
	{	
		StringBuilder sb = new StringBuilder();
		// Write query.
		sb.append("select product_no, serial_no, status, SUM(T) as T from(\r\n" + 
				"select product_no, serial_no,\r\n" + 
				"CASE WHEN STATUS='IDLE' AND sub_status='MICRO IDLE' THEN 'SETUP' ELSE STATUS END AS STATUS,\r\n" + 
				"---CASE WHEN STATUS='IDLE' AND sub_status='PROCESS IDLE' THEN 'PROCESS' ELSE STATUS END AS STATUS,\r\n" + 
				"---status,\r\n" + 
				"(case\r\n"); 
		sb.append("when (start_ts <'" + start_ts + "' and end_ts >'" + start_ts + "' and end_ts<'" + end_ts + "') then datediff(s, '" + start_ts + "', end_ts)\r\n" ); 
		sb.append("when (end_ts > '" + end_ts + "' and start_ts < '" + end_ts + "' and start_ts > '"+ start_ts +"') then datediff(s, start_ts, '" + end_ts + "')\r\n" ); 
		sb.append("when (start_ts < '" + start_ts + "' and end_ts > '" + end_ts + "') then datediff(s, '"+ start_ts+"', '"+ end_ts+"')\r\n" );
		sb.append("else datediff(s, start_ts, end_ts) END) As T\r\n"); 
		sb.append("from app_bm_graphics_lf_telemetry.printer_state_result\r\n" ); 
		sb.append("where serial_no = '" + serial_no + "' and product_no = '" + product_no + "'\r\n");
		sb.append(" --and channel = 'oee' --(For MV)\r\n" + 
				" and channel in ('oee','warning') --(For PWXL/Juno/Jaguar/Skaar/LV/Marcopolo)\r\n" + 
				" --and channel = 'engine' --(For LV/Marcopolo)---Dimmed \r\n" );
		sb.append("and ((start_ts <= '" + start_ts + "' and end_ts >= '" + start_ts + "')\r\n" ); 
		sb.append("OR (start_ts <= '" + end_ts + "' and end_ts >= '" + end_ts + "')\r\n" ); 
		sb.append("OR (start_ts >= '" + start_ts + "' and end_ts <= '" + end_ts + "')\r\n" ); 
		sb.append("OR (start_ts <= '" + start_ts + "' and end_ts >= '" + end_ts + "'))\r\n" ); 
		sb.append("order by status)\r\n" + 
				"group by product_no, serial_no, status\r\n" + 
				"ORDER BY status");

		String sql = sb.toString();
		//System.out.println("sql->" + sql);
		return sql;
	}


} // End Class
