package hp.Seals.getErrorEventsApiVsDbTest;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.*;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import hp.Seals.getErrorEventsApiVsDbTest.PostgreSqlConnection;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import apiConfig.APIPath;
import hp.Seals.APITest.*;

import io.restassured.response.Response;
import utils.ExtentReportListener;
import utils.FileandEnv;
import utils.UtilityApiMethods;


public class APIVerificationErrorEventsPage extends ExtentReportListener
{
	final static Logger logger = LogManager.getLogger(APIVerificationErrorEventsPage.class);

	static PostgreSqlConnection objSql = new PostgreSqlConnection();

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
			//			logger.info("From API Response, '" + key + "' count = " + list.size() + "\n");
			//			test.log(Status.PASS,"From API Response '" +  key + "' count  ==> " + list.size());
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
			softAssert.assertEquals(responseCount, KeyValueCount, "API Response object count is not matched");
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		//logger.info("Response object count:: " + responseCount);
		return responseCount;
	}

	public static List<String> verifyObjectsKey_Negative(Response response, String listObj, String key, List serialNoList, String serialNo,int choice) 
	{	
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		logger.info("From API Response '" +  key + "' count  ==> " + list.size());
		test.log(Status.PASS,"From API Response '" +  key + "' count  ==> " + list.size());

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


	/*
	 * Checking one Key Value and return list of fail serialNo List from which Key gets fail from API Response
	 * 
	 */
	public static List<String> validate_Correct_Wrong_KeyValueFromResponse(Response response, String listObj, 
			String key, List serialNoList,String serialNo ,String productNo,String startDate,String endDate,int choice)
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

						switch(choice) 
						{

						case 1: 	//Convert String to int				
							int inum = Integer.parseInt(actualVal); 
							if(inum < 0)
							{
								serialNoList.add(serialNo);
								//logger.info("Key value is negative...> " + key + "[" + i + "] : " + actualVal + " AND SerialNo=> " + serialNoList);
								test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + actualVal + "AND serialNoList=" + serialNoList );
								//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
								//			APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
								//						serialNo,productNo,startDate,endDate));

								test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
										APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
												serialNo,productNo,startDate,endDate));
								break;
							}
							break;

						case 2:	//Convert String to float
							try {
								Float floatVal = Float.valueOf(actualVal).floatValue();
								//   System.out.println("Float: " + floatVal);
								if( floatVal  < 0 )
								{
									serialNoList.add(serialNo);
									//logger.info("Key value is negative...> " + key + "[" + i + "] : " + actualVal + " AND SerialNo=> " + serialNoList);
									test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + actualVal + "AND serialNoList=" + serialNoList );
									//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
									//			APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
									//						serialNo,productNo,startDate,endDate));

									test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
											APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
													serialNo,productNo,startDate,endDate)); 
									break;

								}
							}catch(Exception e) {
								e.fillInStackTrace();
							}							
							break;	

						case 3: //Convert String to double
							try {
								double value = Double.parseDouble(actualVal);
								if(value < 0) {
									serialNoList.add(serialNo);
									//logger.info("Key value is negative...> " + key + "[" + i + "] : " + actualVal + " AND SerialNo=> " + serialNoList);
									test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + actualVal + "AND serialNoList=" + serialNoList );
									//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
									//			APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
									//						serialNo,productNo,startDate,endDate));

									test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
											APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
													serialNo,productNo,startDate,endDate)); 
									break;

								} 
							} catch (NumberFormatException e) {
								e.fillInStackTrace();
								//System.out.println("String "+ actualVal + "is not a number");
							}

							break;

						case 4:  //Convert String to int/float/double
							String numericString = null;
							String temp;
							if(actualVal.startsWith("-")){ //checks for negative values
								temp=actualVal.substring(1);
								if(temp.matches("[+]?\\d*(\\.\\d+)?")){
									numericString=actualVal;
								}
								serialNoList.add(serialNo);
							}
							if(actualVal.matches("[+]?\\d*(\\.\\d+)?")) {
								numericString=actualVal;
							}
							logger.info( numericString);
							serialNoList.add(serialNo);
							test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + actualVal + "AND serialNoList=" + serialNoList );

							//logger.info("KeyValueCount= " + count + " and object_no->"+ (i+1) + " :: " + key +": " + numericString );														
							break;

						case 5:	//Convert String to char					
							char ch = actualVal.charAt(0);
							serialNoList.add(serialNo);
							test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + actualVal + "AND serialNoList=" + serialNoList );
							//logger.info("KeyValueCount= " + count + " and object_no->"+ (i+1) + " ::" + key +" : " + ch );							
							//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							//			APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
							//						serialNo,productNo,startDate,endDate));

							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 

							break;	

						case 6: //Convert String to Date
							String sDate1 = actualVal;  
							Date date1=new SimpleDateFormat("yyyy-mm-dd").parse(sDate1);  
							logger.info(sDate1+"\t"+date1); 
							serialNoList.add(serialNo);
							//logger.info("KeyValueCount= " + count + " and object_no-> "+ (i+1) + " ::" + key +" : " + sDate1+"\t"+date1 );							
							test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + actualVal + "AND serialNoList=" + serialNoList );

							//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							//			APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
							//						serialNo,productNo,startDate,endDate));

							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 

							break;	

						case 7: //Convert String to Boolean
							boolean b1=Boolean.parseBoolean(actualVal);  
							serialNoList.add(serialNo);
							//logger.info("KeyValueCount= " + count + " and object_no->"+ (i+1) + " ::" + actualVal +" : " + b1 );							
							test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + actualVal + "AND serialNoList=" + serialNoList );

							//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							//			APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
							//						serialNo,productNo,startDate,endDate));

							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 

							break;						
						}

						count = i + 1; 	
					}
				}

			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		logger.info("FAIL List SerialNo are =====> " + serialNoList );
		test.log(Status.PASS, "FAIL List SerialNo are =====> " + serialNoList );
		//logger.info("List value are =====> " + alist );
		//test.log(Status.PASS, "Key values Lists are ===> "+ key + "::>> " + alist);

		return alist;
	}



	/*
	 * Invalid Error Code  validation*********************************************** 
	 */
	public static  void verifyInvalidErrorCode(Response response, String listObj, String key ,String serialNo,String productNo,String startDate, String endDate, List serialNoList)
	{
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		test.log(Status.PASS, "No of Json Objects ===> " + list.size());
		List alist = new ArrayList();

		int count = 0 ;
		try {
			if (list.size() > 0) 
			{
				for (int i = 0; i < list.size(); i++) 
				{
					String actualVal = list.get(i).get(key);
					alist.add(actualVal);
					try {																						
						if("0000-0000-0000".equals(actualVal)) {
							serialNoList.add(serialNo);
							test.log(Status.FAIL,"FAIL..Wrong error code...Sr_NO= " + serialNo +" and Object_no: "+ (i+1) +" And "+ key + " : " + actualVal);

							//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							//		APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
							//								serialNo,productNo,startDate,endDate));
							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 
							break;
						}
						else if( "Unknown".equalsIgnoreCase(actualVal) ) {
							serialNoList.add(serialNo);
							test.log(Status.FAIL,"FAIL..Wrong error code...Sr_NO= " + serialNo +" and Object_no: "+ (i+1) +" And "+ key + " : " + actualVal);
							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 
							break;
						}
						else if( actualVal == null || actualVal.isEmpty() ) {
							serialNoList.add(serialNo);
							test.log(Status.FAIL,"FAIL..Wrong error code...Sr_NO= " + serialNo +" and Object_no: "+ (i+1) +" And "+ key + " : " + actualVal);

							//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							//			APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
							//						serialNo,productNo,startDate,endDate));

							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 
							break;
						}

					}catch(Exception e) {						
						test.log(Status.FAIL, e.fillInStackTrace());
					}						
					count = i + 1; 
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		//test.log(Status.PASS	, "########## Get Error Events Test Execution is Passed ############# ");
	}

	// Invalid Key_Value='errorCode check *******************************************
	public static  void validate_Keyvalue(Response response, String listObj, String key , String expVal,
			String serialNo,String productNo,String startDate, String endDate)
	{
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		test.log(Status.PASS, "No of Json Objects ===> " + list.size());
		List alist = new ArrayList();
		int count = 0 ;
		try {
			if (list.size() > 0) 
			{
				for (int i = 0; i < list.size(); i++) 
				{
					String actualVal = list.get(i).get(key);
					alist.add(actualVal);

					try {																						
						if( expVal != null && actualVal != null && expVal.equals(actualVal) ) 
						{	 
							logger.info(".Failed......And....."+ "object_no->"+ (i+1) + " ::" + key +": " + actualVal);
							test.log(Status.FAIL,"FAIL...Wrong error code...and Object_no: "+ (i+1) +"And "+ key + " : " + actualVal);
							//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							//		APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
							//								serialNo,productNo,startDate,endDate));
							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate));   
							break;

						}
						else if( expVal != null && actualVal != null && expVal.equalsIgnoreCase(actualVal) ) 
						{

							logger.info("Failed.....And....."+ "object_no->"+ (i+1) + " ::" + key + ": " + actualVal);
							test.log(Status.FAIL,"FAIL......and Object_no: "+ (i+1) +"And "+ key + " : " + actualVal);
							//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							//		APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
							//								serialNo,productNo,startDate,endDate));
							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 
							break;
						}
						else if( actualVal == expVal ) 
						{	 
							logger.info("Failed......And....."+ "object_no->"+ (i+1) + " ::" + key +": " + actualVal);
							test.log(Status.FAIL,"FAIL......and Object_no: "+ (i+1) +" And "+ key + " : " + actualVal);
							//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							//		APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
							//								serialNo,productNo,startDate,endDate));
							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 
							break;
						}
						else if( actualVal != null && actualVal.isEmpty() ) 
						{
							logger.info("Failed.....And....."+ "object_no->"+ (i+1) + " ::" + key +": " + actualVal);
							test.log(Status.FAIL,"FAIL......and Object_no: "+ (i+1) +" And "+ key + " : " + actualVal);
							//logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							//		APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
							//								serialNo,productNo,startDate,endDate));
							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 
							break;
						}

					}catch(Exception e) {
						test.log(Status.FAIL, e.fillInStackTrace());
					}										
					count = i + 1; 
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		logger.info("List value are =====> " + alist );
		test.log(Status.PASS, "List of Key values are ===> "+ key + "::>> " + alist);

	}


	// Invalid Error code check *******************************************
	public static  void validate_KeyValueCode(Response response, String listObj, String key , int index , String expVal)
	{
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		test.log(Status.PASS, "No of Json Objects ===> " + list.size());
		List alist = new ArrayList();
		int count = 0 ;
		try {
			if (list.size() > 0) 
			{
				for (int i = 0; i < list.size(); i++) 
				{
					String actualVal = list.get(i).get(key);
					alist.add(actualVal);
					switch(index)
					{
					case 1: try {
						if(expVal.equals(actualVal)) {
							logger.info("Failed.....And....."+ "object_no-> "+ (i+1) + " :: " +key +": " + actualVal);
							test.log(Status.INFO,"FAIL...Wrong error code...and Object_no: "+ (i+1) +"And "+ key + " : " + actualVal);
						}
					}catch(Exception e) {
						test.log(Status.FAIL, e.fillInStackTrace());
					}
					break;
					case 2: 
						try {
							if( expVal.equalsIgnoreCase(actualVal) )  {
								logger.info("Failed......And....."+ "object_no-> "+ (i+1) + " ::" + key +": " + actualVal);
								test.log(Status.INFO,"FAIL...Wrong error code...and Object_no: "+ (i+1) +"And "+ key + " : " + actualVal);
							}
						}catch(Exception e) {
							test.log(Status.FAIL, e.fillInStackTrace());
						}
						break;
					case 3:	
						try {
							if( actualVal == expVal || actualVal.isEmpty() ) {
								logger.info("Failed...error code...And....."+ "object_no-> "+ (i+1) + " ::" +key +" : " + actualVal);
								test.log(Status.INFO,"FAIL...Wrong error code...and Object_no: "+ (i+1) +"And "+ key + " : " + actualVal);
							}
						}catch(Exception e) {
							test.log(Status.FAIL, e.fillInStackTrace());
						}					
						break;
					}
					count = i + 1; 
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		logger.info("List value are =====> " + alist );
		test.log(Status.PASS, "List of Key values are ===> "+ key + "::>> " + alist);
		logger.info(" ***********************************************************************\n");
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
	 * *************************** Compare DB and API Response  ******************************************
	 */

	/*
	 * Validation a perticular field 'cust_cd' value from DB and API Response body
	 */
	public static String getValueFromDB() 
	{
		return objSql.getPrintResult().getCust_cd();
	}

	/*
	 * Compare error count from API response and DB  **************************************
	 */
	public static int getErrorCountFromDB(String serialNo, String startDate, String endDate)
	{
		//test.log(Status.PASS, "No of Key value count from Database ====> " + objSql.getNoOfErrorCount(serialNo, startDate, endDate).getSum());
		return objSql.getNoOfErrorCount(serialNo, startDate, endDate).getSum();
	}

	public static void validateErrorCountObjectWithDB(Response response, String listObj, String key, 
			String serialNo,String productNo, String startDate, String endDate, List serialNoList ) 
	{
		logger.info("***Compare error_code count from API response and DB ****\n");
		test.log(Status.INFO,MarkupHelper.createLabel("*****Compare Error_code count from API response and Database ****",ExtentColor.CYAN));

		try {	
			int errorCodeCountFromDB = getErrorCountFromDB(serialNo, startDate, endDate);
			logger.info("From DB , '" + key + "' count = " + errorCodeCountFromDB);
			test.log(Status.PASS, "From DB , '" + key + "' count = " + errorCodeCountFromDB );

			int errorCountFromApi = verifyObjectsCountFromResponse(response, listObj, key);
			logger.info("From API Response, '" + key + "' count = " + errorCountFromApi + "\n");
			test.log(Status.PASS, "From API Response, '" + key + "' count = " + errorCountFromApi );

			if (errorCodeCountFromDB == errorCountFromApi) {
				softAssert.assertEquals(errorCodeCountFromDB, errorCountFromApi, "ErrorCode count is not matched");
				test.log(Status.INFO,MarkupHelper.createLabel("<===PASS====Error count is matched  from API and DB =====> \n",ExtentColor.BROWN));
			} else {
				softAssert.assertEquals(errorCodeCountFromDB, errorCountFromApi, "ErrorCode count is not matched");
				test.log(Status.FAIL, "<============TEST CASE FAILED =================> \n");
				test.log(Status.FAIL, "From DB , '" + key + "' count = " + errorCodeCountFromDB );
				test.log(Status.FAIL, "From API , '" + key + "' count = " + errorCountFromApi );
				test.log(Status.FAIL, MarkupHelper.createLabel("<=====Error count is not same from API and DB ====> \n",ExtentColor.RED));
				logger.info("<==== Error count is not same from API and DB ====>");

				logger.info(FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),serialNo,productNo,startDate,endDate));

				test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),serialNo,productNo,startDate,endDate)); 

				test.log(Status.FAIL, "******************* Refer below Query **************** ");
				test.log(Status.FAIL,"" + getNoOfErrorCount_DB(serialNo , startDate, endDate ));
				serialNoList.add(serialNo);
				test.log(Status.FAIL,"Failed Serial no=> " + serialNoList);
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		// logger.info("Fail Serial no list ======> " + serialNoList);
	}

	/*
	 * ********** GetErrorEvents API **************** Verify 'error code' value from API ************************* 
	 */

	@SuppressWarnings("unused")
	public static  List verifyGetErrorEventsKeyValue(Response response, String listObj, String key ,String serialNo,
			String productNo,String startDate, String endDate )
	{
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		logger.info("No of Json Objects ===> " + list.size());
		//test.log(Status.PASS, "No of Json Objects ===> " + list.size());		
		List alist = new ArrayList();

		try {
			if(list == null) {				
				logger.info("FAILed: API has 400 bad request ");

				test.log(Status.WARNING, FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), serialNo,productNo,startDate,endDate));

				test.log(Status.WARNING,"Error: No data in requested duration ");
				logger.info("Error: No data in requested duration");

			} else if( list != null && list.size() > 0) {

				for (int i = 0; i < list.size(); i++) 
				{
					String actualVal = list.get(i).get(key);
					alist.add(actualVal);
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		logger.info("From API, List of '" + key   + "' are ==> "+ alist);
		//test.log(Status.PASS, "From API, List of '" + key   + "' are ==> " + alist);

		return alist;
	}

	/*
	 * ************************** Compare 'errorcode' value from API response and DB ******************************
	 */	
	public static void validateErrorCodeWithDB(Response response, String listObj, String key, 
			String serialNo,String prod_no,String startDate, String endDate, List serialNoList) 
	{
		logger.info("******* Compare 'error_code' Value from API response and DB ******* ");
		test.log(Status.INFO,MarkupHelper.createLabel("***** Compare 'Error_code' value from API response and Database *****", ExtentColor.AMBER));

		try {			
			List errorCodeFromApi = verifyGetErrorEventsKeyValue(response, listObj, key,serialNo, prod_no,  startDate,  endDate);

			List errorCodeFromDB = objSql.getErrorCode_DB(serialNo,prod_no, startDate, endDate);

			if( errorCodeFromApi == null &&  errorCodeFromDB == null ) {
				logger.info("'error_code' object is Empty From API Response and DB " );
				test.log(Status.PASS, "'error_code' object is Empty From API Response and DB ");
			} 
			else if( errorCodeFromApi != null &&  errorCodeFromDB != null && errorCodeFromApi.size() == errorCodeFromDB.size() ) {

				logger.info("From API, '" + key + "' Objects Count= " + errorCodeFromApi.size());
				logger.info("From  DB, '" + key + "' Objects Count= " + errorCodeFromDB.size() );				
				test.log(Status.PASS, "From API, '" + key + "'  Objects Count= " + errorCodeFromApi.size());
				test.log(Status.PASS, "From  DB, '" + key + "'  Objects Count= " + errorCodeFromDB.size() );				
				logger.info(" 'error_code' objects count are Same From API Response and DB " );
				test.log(Status.PASS,"'error_code' objects count are Same From API Response and DB " );

				logger.info("'error_code' values From Database =>> " + errorCodeFromDB);
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'error_code' values from DB =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + errorCodeFromDB);

				logger.info("'error_code' values From API Response =>> " + errorCodeFromApi );
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'error_code' values from API =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + errorCodeFromApi);							

				if (errorCodeFromDB.equals( errorCodeFromApi)) {
					softAssert.assertEquals(errorCodeFromDB, errorCodeFromApi, "ErrorCode value is not matched");
					test.log(Status.INFO, "<===PASS======= Error code value is matched  from API and DB ===============> \n");
					logger.info("<===== ErrorCode vaue is matched from API and DB ======>\n");

				} else {
					softAssert.assertEquals(errorCodeFromDB, errorCodeFromApi, "ErrorCode count is not matched");

					test.log(Status.FAIL,MarkupHelper.createLabel( "FAIL<===Error Code value is not matched from API and DB.  ",ExtentColor.RED));
					logger.info("FAIL <=======Error Code value is not matched from API and DB ========>\n");

					serialNoList.add(serialNo);

					logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
									serialNo,prod_no,startDate,endDate));

					test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
									serialNo,prod_no,startDate,endDate)); 
					test.log(Status.FAIL,"FAILed Due to mismatch of 'error_Code' " );

				}
			} 
		}catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 *  ***************************** getErrorEvents API ************************************************
	 * Verify getting 'severity' value based on 'error_Code' from getErrorEvents API
	 */
	public static List<ErrorEventsSeverityPojo>  getSeverityFromAPI( Response response,  String sn, String pn , String startDate,String endDate ) 
	{
		List<ErrorEventsSeverityPojo> listObj = new ArrayList<ErrorEventsSeverityPojo>();
		if(response.getStatusCode() == 400) {

			logger.info("400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
					APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

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
					test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("alert");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'alert' object is empty from API Response ",ExtentColor.BROWN) );
						logger.info("Failed ==> 'alert' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" From API, 'severity' is not available due to 'alert' is Empty. ");
						test.log(Status.PASS, " From API, 'severity' is not available due to 'alert' is Empty. " );

						List<ErrorEventsSeverityPojo> db_statusValue = objSql.getSeverityValuesFromDB(sn, pn, startDate,endDate);
						int countFromDB = db_statusValue.size();						
						logger.info("From DB, 'severity' is not available due to 'alert' is Empty." );
						test.log(Status.PASS, "From DB , 'severity' is not available due to 'alert' is Empty." );

						//						if(countApi == countFromDB) {
						//							test.log(Status.PASS,MarkupHelper.createLabel("*** PASS ***=> 'alert' Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are  matched ",ExtentColor.GREEN));
						//							logger.info("***PASS=> Object count is matched  from API and Seals_DB =====> \n");
						//
						//						} else {
						//							logger.info("****FAIL => 'alert' Object count is not matched  from API and DB ********\n");
						//							logger.info("****FAIL => 'alert' Array object is empty from API Response ******\n");
						//							test.log(Status.FAIL,MarkupHelper.createLabel(" 'alert' Array object is empty from API Response ",ExtentColor.ORANGE) );
						//							test.log(Status.FAIL, "Thus, 'alert' is not available in API " );
						//							test.log(Status.FAIL," 'alert' Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are not matched *** ");
						//
						//							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API  *************** ",ExtentColor.BROWN) );
						//							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 
						//							// Write query.
						//							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
						//							//test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));
						//						}

					} else {    // API is calling

						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							ErrorEventsSeverityPojo objErrorEventsPojo = new ErrorEventsSeverityPojo();
							String eventCode = jsonArray.getJSONObject(i).getString("error_Code");   
							objErrorEventsPojo.setError_Code(eventCode);											
							String shortDescription = jsonArray.getJSONObject(i).getString("severity");
							objErrorEventsPojo.setSeverity(shortDescription);
							listObj.add(objErrorEventsPojo);							
						} 						
					} // End Else

				} // Outer END IF
			} catch (JSONException e) {

				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE
		//	logger.info("\n********* New List values ********* \n" + listObj );

		return listObj;

	} // End Method
	/*
	 * ************************** Compare 'severity' values from API response and DB **************************
	 */
	@SuppressWarnings("unchecked")
	public static void compareSeverityValuesFromApiWithDB(Response response, String listObj, String key, 
			String sn,String pn,String startDate, String endDate, List serialNoList) 
	{
		logger.info("\n***Compare 'severity' values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel( "****** Compare 'severity' values from API response and Database *******", ExtentColor.CYAN ));
		try {
			List<ErrorEventsSeverityPojo> listApiVal = getSeverityFromAPI(response, sn, pn,  startDate,  endDate);
			List<ErrorEventsSeverityPojo>  listDbVal = objSql.getSeverityValuesFromDB(sn, pn , startDate, endDate );

			if( listDbVal == null &&  listApiVal == null ) {
				logger.info("'severity' object is Empty From API Response and DB " );
				test.log(Status.PASS, "'severity' object is Empty From API Response and DB ");
			} 
			else if( listDbVal != null &&  listApiVal != null && listDbVal.size() == listApiVal.size() ) {

				logger.info("From  DB, '" + key + "'   Count= " + listDbVal.size() );
				logger.info("From API, '" + key + "'  Count= " + listApiVal.size());
				test.log(Status.PASS, "From  DB, '" + key + "'   Count= " + listDbVal.size() );
				test.log(Status.PASS, "From API, '" + key + "'   Count= " + listApiVal.size() );
				logger.info(" 'severity' objects count are Same From API Response and DB " );
				test.log(Status.PASS,"'severity' objects count are Same From API Response and DB " );

				logger.info("'severity' values From Database =>> " + listDbVal);
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'severity' values from DB =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listDbVal);

				logger.info("'severity' values From API Response =>> " + listApiVal );
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'severity' values from API =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listApiVal);				

				if(listApiVal.equals(listDbVal)) {

					test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'severity' values are matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
					logger.info("*** PASS=>'severity' values are matched  from API and DB ***\n" );

				} else {

					test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'severity' value are not matched from API and Seals_DB *** ",ExtentColor.RED) );
					logger.info("*** FAIL =>'severity' values are not matched  from API and DB *** \n");				

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),sn,pn,startDate,endDate )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForErrorEventsValuesFromDB(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
				}				

			} else {
				logger.info("FAIL:- From  DB, '" + key + "'   Count= " + listDbVal.size() );
				logger.info("FAIL:- From API, '" + key + "'  Count= " + listApiVal.size());
				test.log(Status.FAIL, "From  DB, '" + key + "'   Count= " + listDbVal.size() );
				test.log(Status.FAIL, "From API, '" + key + "'   Count= " + listApiVal.size() );	
				logger.info("\n 'severity'  count is not same From API Response and DB \n" );
				test.log(Status.FAIL,"'severity'  count is not Same From API Response and DB " );

				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

				logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate));

				test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 
				// refer query.
				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
				test.log(Status.FAIL,"" + getQueryForErrorEventsValuesFromDB(sn,pn,startDate,endDate) );

				serialNoList.add(sn);

			}

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.PASS, MarkupHelper.createLabel("############################################ END ########################################### ",ExtentColor.ORANGE));
	}	


	/*
	 *  ***************************** getErrorEvents API ************************************************
	 * Verify 'event_Occurred_TS' value based on 'error_Code' from getErrorEvents API
	 */
	public static List<EventTsPojo>  getEventTsValuesFromAPI( Response response, String sn, String pn , String startDate,String endDate ) 
	{
		List<EventTsPojo> listObj = new ArrayList<EventTsPojo>();
		if(response.getStatusCode() == 400) {

			logger.info("400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
					APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

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
					test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("alert");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'alert' object is empty from API Response ",ExtentColor.BROWN) );
						logger.info("Failed ==> 'alert' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" From API, 'event_TS' is not available due to 'alert' is Empty.");
						test.log(Status.PASS, " From API, 'event_TS' is not available due to 'alert' is Empty.");

						List<ErrorEventsSeverityPojo> db_statusValue = objSql.getSeverityValuesFromDB(sn, pn, startDate,endDate);
						int countFromDB = db_statusValue.size();						
						logger.info(" From DB, 'event_TS' is not available due to 'alert' is Empty. " );
						test.log(Status.PASS, "  From DB, 'event_TS' is not available due to 'alert' is Empty." );

						//						if(countApi == countFromDB) {
						//							test.log(Status.PASS,MarkupHelper.createLabel("*** PASS ***=> 'alert' Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are  matched ",ExtentColor.GREEN));
						//							logger.info("***PASS=> Object count is matched  from API and Seals_DB =====> \n");
						//
						//						} else {
						//							logger.info("****FAIL => 'alert' Object count is not matched  from API and DB ********\n");
						//							logger.info("****FAIL => 'alert' Array object is empty from API Response ******\n");
						//							test.log(Status.FAIL,MarkupHelper.createLabel(" 'alert' Array object is empty from API Response ",ExtentColor.ORANGE) );
						//							test.log(Status.FAIL, "Thus, 'alert' is not available in API " );
						//							test.log(Status.FAIL," 'alert' Object count from API=  " + countApi + " and count From DB= "  + countFromDB + "   are not matched *** ");
						//
						//							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API  *************** ",ExtentColor.BROWN) );
						//							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 
						//							// Write query.
						//							test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query **************** ",ExtentColor.BROWN) );
						//							//test.log(Status.FAIL,"" + PrintheadDetailsDB2(serial_no,product_no,start_ts,end_ts));
						//						}

					} else {    // API is calling

						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							EventTsPojo objEventTsPojo = new EventTsPojo();
							String eventCode = jsonArray.getJSONObject(i).getString("error_Code"); //"error_Code"   
							objEventTsPojo.setCust_cd(eventCode);											
							String shortDescription = jsonArray.getJSONObject(i).getString("event_Occurred_TS"); //"event_Occurred_TS"
							objEventTsPojo.setEvt_ocrd_ts(shortDescription);
							listObj.add(objEventTsPojo);							
						} 						
					} // End Else

				} // Outer END IF
			} catch (JSONException e) {

				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE
		//	logger.info("\n********* New List values ********* \n" + listObj );

		return listObj;

	} // End Method
	/*
	 * ************************** Compare 'event_Occurred_TS' values from API response and DB ******************************
	 */
	@SuppressWarnings("unchecked")
	public static void compareEventOccuredTSvaluesWithDB(Response response, String listObj, String key, 
			String sn,String pn,String startDate, String endDate, List serialNoList) 
	{
		logger.info("\n***Compare 'event_Occurred_TS' values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel( "****** Compare 'event_Occurred_TS' values from API response and Database *******", ExtentColor.CYAN ));
		try {
			List<EventTsPojo> listApiVal = getEventTsValuesFromAPI(response, sn, pn, startDate, endDate);
			List<EventTsPojo>  listDbVal = objSql.geEvent_Occurred_TSValuesFromDB (sn, pn , startDate, endDate );

			if( listDbVal == null &&  listApiVal == null ) {
				logger.info("'event_Occurred_TS' object is Empty From API Response and DB " );
				test.log(Status.PASS, "'event_Occurred_TS' object is Empty From API Response and DB ");
			} 
			else if( listDbVal != null &&  listApiVal != null && listDbVal.size() == listApiVal.size() ) {

				logger.info("From  DB, '" + key + "'   Count= " + listDbVal.size() );
				logger.info("From API, '" + key + "'  Count= " + listApiVal.size());
				test.log(Status.PASS, "From  DB, '" + key + "'   Count= " + listDbVal.size() );
				test.log(Status.PASS, "From API, '" + key + "'   Count= " + listApiVal.size() );
				logger.info("'event_Occurred_TS'  count are Same From API Response and DB " );
				test.log(Status.PASS,"'event_Occurred_TS'  count are Same From API Response and DB " );

				logger.info("'event_Occurred_TS' values From Database =>> " + listDbVal);
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'event_Occurred_TS' values from DB =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listDbVal);

				logger.info("'event_Occurred_TS' values From API Response =>> " + listApiVal );
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'event_Occurred_TS' values from API =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listApiVal);				

				if(listApiVal.equals(listDbVal)) {

					test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'event_Occurred_TS' values are matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
					logger.info("*** PASS=>'event_Occurred_TS' values are matched  from API and DB ***\n" );

				} else {

					test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'event_Occurred_TS' value are not matched from API and Seals_DB *** ",ExtentColor.RED) );
					logger.info("*** FAIL =>'event_Occurred_TS' values are not matched  from API and DB *** \n");				

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),sn,pn,startDate,endDate )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForErrorEventsValuesFromDB(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
				}				

			} else {
				logger.info("From  DB, '" + key + "'  Objects Count= " + listDbVal.size() );
				logger.info("From API, '" + key + "' Objects Count= " + listApiVal.size());
				test.log(Status.FAIL, "From  DB, '" + key + "'  Objects Count= " + listDbVal.size() );
				test.log(Status.FAIL, "From API, '" + key + "'  Objects Count= " + listApiVal.size() );
				logger.info("\n 'event_Occurred_TS' objects count is not same From API Response and DB \n" );
				test.log(Status.FAIL,"'event_Occurred_TS' Objects count is not Same From API Response and DB " );

				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

				logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate));

				test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 
				// refer query.
				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
				test.log(Status.FAIL,"" + getQueryForErrorEventsValuesFromDB(sn,pn,startDate,endDate) );

				serialNoList.add(sn);
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.PASS, MarkupHelper.createLabel("############################################ END ########################################### ",ExtentColor.ORANGE));
	}	

	/*
	 *  ***************************** getErrorEvents API ************************************************
	 * Verify 'short_description' value based on cust_cd from getErrorEvents API
	 */
	public static List<ErrorEventsPojo>  getShortDescriptionFromAPI( Response response,  String sn, String pn , String startDate,String endDate ) 
	{
		List<ErrorEventsPojo> listObj = new ArrayList<ErrorEventsPojo>();
		if(response.getStatusCode() == 400) {

			logger.info("400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
					APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

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
					test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("alert");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'alert' object is empty from API Response ",ExtentColor.BROWN) );
						logger.info("Failed ==> 'alert' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" From API, 'short_Description' is not available due to 'alert' is Empty. ");
						test.log(Status.PASS, "From API, 'short_Description' is not available due to 'alert' is Empty." );

						List db_statusValue = objSql.getShortDescriptionValuesFromDB(sn, pn, startDate,endDate);
						int countFromDB = db_statusValue.size();						
						logger.info(" From DB, 'short_Description' is not available due to 'alert' is Empty. ");
						test.log(Status.PASS, "From DB, 'short_Description' is not available due to 'alert' is Empty. " );

					} else {    // API is calling

						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							ErrorEventsPojo objErrorEventsPojo = new ErrorEventsPojo();
							String eventCode = jsonArray.getJSONObject(i).getString("error_Code");   
							//objErrorEventsPojo.setCust_cd(eventCode);											
							String shortDescription = jsonArray.getJSONObject(i).getString("short_Description");
							objErrorEventsPojo.setShort_description(shortDescription);
							listObj.add(objErrorEventsPojo);							
						} 						
					} // End Else

				} // Outer END IF
			} catch (JSONException e) {

				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE
		//	logger.info("\n********* New List values ********* \n" + listObj );

		return listObj;

	} // End Method


	/*
	 * Compare the 'short_description' value  from API vs DB ***************************
	 */
	public static void compareShortDescriptionFromAPIvsDB(Response response, String sn, String pn , String startDate, String endDate, List serialNoList )
	{
		logger.info("\n***Compare the 'short_description' values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("***Compare the 'short_description' values from API response and Database ***",ExtentColor.BROWN) );
		try {

			List<ErrorEventsPojo> listApiVal = getShortDescriptionFromAPI( response, sn, pn, startDate, endDate );			
			List<ErrorEventsPojo> listDbVal = objSql.getShortDescriptionValuesFromDB(sn, pn, startDate, endDate);

			logger.info("Api value= " + listApiVal + " \n and api size=" + listApiVal.size());
			logger.info("Db  value= " + listDbVal + " \n  and db size=" + listDbVal.size());

			logger.info(" api size=" + listApiVal.isEmpty());
			logger.info("  db size=" + listDbVal.isEmpty());


			if( (listDbVal == null &&  listApiVal == null)  || (listDbVal.isEmpty() && listApiVal.isEmpty()) || listApiVal.size() == listDbVal.size() ) 
			{
				logger.info("'short_description' object is Empty From API Response and DB " );
				test.log(Status.PASS, "'short_description' object is Empty From API Response and DB ");
			} 
			else if( listDbVal != null &&  listApiVal != null ) {

				logger.info("'short_description' values From Database =>> " + listDbVal);
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'short_description' values from DB =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listDbVal);

				logger.info("'short_description' values From API Response =>> " + listApiVal );
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'short_description' values from API =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listApiVal);				

				if( listDbVal.containsAll(listApiVal)) {

					test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'short_description' values are matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
					logger.info("*** PASS=>'short_description' values are matched  from API and DB ***\n" );

				} else {

					test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'short_description' value are not matched from API and Seals_DB *** ",ExtentColor.RED) );
					logger.info("*** FAIL =>'short_description' values are not matched  from API and DB *** \n");				

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

					test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate));
					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForShortLongDescriptionValuesFromDB(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
				}				
			} 

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.PASS, MarkupHelper.createLabel("############################################ END ########################################### ",ExtentColor.ORANGE));
	}	
	//======================================== short_Description ============================================================================
	public static List  getShortDescriptionValueFromAPI( Response response,  String sn, String pn , String startDate,String endDate ) 
	{
		List listObj = new ArrayList<>();
		String shortDescription = "";

		if(response.getStatusCode() == 400) {

			logger.info("400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
					APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

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
					test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("alert");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'alert' object is empty from API Response ",ExtentColor.BROWN) );
						logger.info("Failed ==> 'alert' object is empty from API Response " );
						countApi = jsonArray.length();
						logger.info(" From API, 'short_Description' is not available due to 'alert' is Empty. ");
						test.log(Status.PASS, "From API, 'short_Description' is not available due to 'alert' is Empty." );

						List db_statusValue = objSql.getShortDescriptionValueFromDB(sn, pn, startDate,endDate);
						int countFromDB = db_statusValue.size();						
						logger.info(" From DB, 'short_description' is not available due to 'alert' is Empty. ");
						test.log(Status.PASS, "From DB, 'short_Description' is not available due to 'alert' is Empty. " );

					} else {    // API is calling

						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							//String eventCode = jsonArray.getJSONObject(i).getString("error_Code");   

							shortDescription = jsonArray.getJSONObject(i).getString("short_Description");

							if(shortDescription == null || shortDescription.isEmpty()) {
								listObj.isEmpty();
							} else {
								listObj.add(shortDescription);

							} 						
						} // End for
					} //End Else

				} // Outer END IF
			} catch (JSONException e) {

				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE
		
		
			logger.info("\n********* New List values ********* \n" + listObj );

		return listObj;

	} // End Method
	/*
	 * Compare the 'short_description' value  from API vs DB ***************************
	 */
	@SuppressWarnings("unused")
	public static void compareShortDescriptnFromAPIvsDB(Response response, String sn, String pn , String startDate, String endDate, List serialNoList )
	{
		logger.info("\n***Compare the 'short_description' values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel("***Compare the 'short_description' values from API response and Database ***",ExtentColor.BROWN) );
		try {

			List listApiVal = getShortDescriptionValueFromAPI( response, sn, pn, startDate, endDate );			
			List  listDbVal = objSql.getShortDescriptionValueFromDB(sn, pn, startDate, endDate);

			logger.info("Api value= " + listApiVal + " ##### and api size=" + listApiVal.size());
			logger.info("Db  value= " + listDbVal + "  ##### and db size=" + listDbVal.size());

			logger.info(" API size =" + listApiVal.isEmpty());
			logger.info("  DB size =" + listDbVal.isEmpty());


			if( (listDbVal == null &&  listApiVal == null) ) 
			{
				logger.info("'short_description' object is Empty From API Response and DB " );
				test.log(Status.PASS, "'short_description' object is Empty From API Response and DB ");
			} 
			else if( listDbVal != null &&  listApiVal != null ) {

				logger.info("'short_description' values From Database =>> " + listDbVal);
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'short_description' values from DB =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listDbVal);

				logger.info("'short_description' values From API Response =>> " + listApiVal );
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'short_description' values from API =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listApiVal);				

				if( listDbVal.containsAll(listApiVal)) {

					test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'short_description' values are matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
					logger.info("*** PASS=>'short_description' values are matched  from API and DB ***\n" );

				} else {

					test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'short_description' value are not matched from API and Seals_DB *** ",ExtentColor.RED) );
					logger.info("*** FAIL =>'short_description' values are not matched  from API and DB *** \n");				

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

					test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate));
					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForShortLongDescriptionValuesFromDB(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
				}				
			} 

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.PASS, MarkupHelper.createLabel("############################################ END ########################################### ",ExtentColor.ORANGE));
	}		

	/*
	 *  ***************************** getErrorEvents API ************************************************
	 * Verify 'long_description' value based on cust_cd from getErrorEvents API
	 */
	public static List  getLongDescriptionValueFromAPI( Response response,  String sn, String pn , String startDate,String endDate ) 
	{
		List listObj = new ArrayList<>();
		String longDescription = "";
		if(response.getStatusCode() == 400) {

			logger.info("400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
					APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

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
					test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("alert");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'alert' object is empty from API Response ",ExtentColor.BROWN) );
						logger.info("Failed ==> 'alert' object is empty from API Response " );
						countApi = jsonArray.length();


					} else {    // API is calling

						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							//String eventCode = jsonArray.getJSONObject(i).getString("error_Code");   							
							longDescription = jsonArray.getJSONObject(i).getString("long_Description");
							if(longDescription == null || longDescription.isEmpty()) {
								listObj.isEmpty();
							} else {
								listObj.add(longDescription);
							} 	
							//listObj.add(longDescription);							
						} 						
					} // End Else

				} // Outer END IF
			} catch (JSONException e) {

				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE
		//	logger.info("\n********* New List values ********* \n" + listObj );

		return listObj;

	} // End Method

	/*
	 * Compare the 'long_description' value  from API vs DB ***************************
	 */
	public static void compareLongDescriptionValueFromAPIvsDB(Response response, String sn,String pn,String startDate, String endDate, List serialNoList) 
	{
		logger.info("\n***Compare 'long_description' values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel( "****** Compare 'long_description' values from API response and Database *******", ExtentColor.CYAN ));
		try {
			List listApiVal = getLongDescriptionValueFromAPI(response, sn, pn,  startDate,  endDate);
			List  listDbVal = objSql.getLongDescriptionValueFromDB (sn, pn , startDate, endDate );

			if( listDbVal == null &&  listApiVal == null ) {
				logger.info("'long_description' object is Empty From API Response and DB " );
				test.log(Status.PASS, "'long_description' object is Empty From API Response and DB ");
			} 
			else if( listDbVal != null &&  listApiVal != null && listDbVal.size() == listApiVal.size() ) {

				logger.info("'long_description' values From Database =>> " + listDbVal);
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'long_description' values from DB =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listDbVal);

				logger.info("'long_description' values From API Response =>> " + listApiVal );
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'long_description' values from API =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listApiVal);				

				if(listApiVal.equals(listDbVal)) {

					test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'long_description' values are matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
					logger.info("*** PASS=>'long_description' values are matched  from API and DB ***\n" );

				} else {

					test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'long_description' value are not matched from API and Seals_DB *** ",ExtentColor.RED) );
					logger.info("*** FAIL =>'long_description' values are not matched  from API and DB *** \n");				

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),sn,pn,startDate,endDate )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForShortLongDescriptionValuesFromDB(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
				}
			} 
			//				else {
			//				logger.info("From  DB, '" + key + "'  Objects Count= " + listDbVal.size() );
			//				logger.info("From API, '" + key + "' Objects Count= " + listApiVal.size());
			//				test.log(Status.PASS, "From  DB, '" + key + "'  Objects Count= " + listDbVal.size() );
			//				test.log(Status.PASS, "From API, '" + key + "'  Objects Count= " + listApiVal.size() );
			//				logger.info("\n 'long_description' objects count is not same From API Response and DB \n" );
			//				test.log(Status.FAIL,"'long_description' Objects count is not Same From API Response and DB " );
			//
			//				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );
			//
			//				logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
			//						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate));
			//
			//				test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
			//						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 
			//				// refer query.
			//				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
			//				test.log(Status.FAIL,"" + getQueryForShortLongDescriptionValuesFromDB(sn,pn,startDate,endDate) );
			//
			//				serialNoList.add(sn);
			//			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.PASS, MarkupHelper.createLabel("############################################ END ########################################### ",ExtentColor.ORANGE));
	}	




	/*
	 *  ***************************** getErrorEvents API ************************************************
	 * Verify 'long_description' value based on cust_cd from getErrorEvents API
	 */
	public static List<ErrorEventsLongDesPojo>  getLongDescriptionValuesFromAPI( Response response,  String sn, String pn , String startDate,String endDate ) 
	{
		List<ErrorEventsLongDesPojo> listObj = new ArrayList<ErrorEventsLongDesPojo>();
		if(response.getStatusCode() == 400) {

			logger.info("400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, "400 Bad request due to Wrong date range entry, startDate= " + startDate + "  and endDate= " + endDate );
			test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
					APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

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
					test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
							APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 

				} else {

					JSONArray jsonArray = inputJSONObject.getJSONArray("alert");

					if(jsonArray.isEmpty() || jsonArray.length() == 0) {
						test.log(Status.PASS,MarkupHelper.createLabel(" 'alert' object is empty from API Response ",ExtentColor.BROWN) );
						logger.info("Failed ==> 'alert' object is empty from API Response " );
						countApi = jsonArray.length();


					} else {    // API is calling

						for(int i = 0 ; i < jsonArray.length(); i++) 
						{
							ErrorEventsLongDesPojo objErrorEventsPojo = new ErrorEventsLongDesPojo();
							String eventCode = jsonArray.getJSONObject(i).getString("error_Code");   
							objErrorEventsPojo.setCust_cd(eventCode);											
							String shortDescription = jsonArray.getJSONObject(i).getString("long_Description");
							objErrorEventsPojo.setLong_description(shortDescription);
							listObj.add(objErrorEventsPojo);							
						} 						
					} // End Else

				} // Outer END IF
			} catch (JSONException e) {

				test.log(Status.FAIL, e.fillInStackTrace());	
			}
		}//End 1st ELSE
		//	logger.info("\n********* New List values ********* \n" + listObj );

		return listObj;

	} // End Method

	/*
	 * Compare the 'long_description' value  from API vs DB ***************************
	 */
	public static void compareLongDescriptionValuesFromAPIvsDB(Response response, String sn,String pn,String startDate, String endDate, List serialNoList) 
	{
		logger.info("\n***Compare 'long_description' values from API response and DB ****");
		test.log(Status.INFO, MarkupHelper.createLabel( "****** Compare 'long_description' values from API response and Database *******", ExtentColor.CYAN ));
		try {
			List<ErrorEventsLongDesPojo> listApiVal = getLongDescriptionValuesFromAPI(response, sn, pn,  startDate,  endDate);
			List<ErrorEventsLongDesPojo>  listDbVal = objSql.getLongDescriptionValuesFromDB (sn, pn , startDate, endDate );

			if( listDbVal == null &&  listApiVal == null ) {
				logger.info("'long_description' object is Empty From API Response and DB " );
				test.log(Status.PASS, "'long_description' object is Empty From API Response and DB ");
			} 
			else if( listDbVal != null &&  listApiVal != null && listDbVal.size() == listApiVal.size() ) {

				logger.info("'long_description' values From Database =>> " + listDbVal);
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'long_description' values from DB =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listDbVal);

				logger.info("'long_description' values From API Response =>> " + listApiVal );
				test.log(Status.PASS, MarkupHelper.createLabel(" Below  is  'long_description' values from API =====> ", ExtentColor.GREY) );
				test.log(Status.PASS,"" + listApiVal);				

				if(listApiVal.equals(listDbVal)) {

					test.log(Status.PASS,MarkupHelper.createLabel("*** PASS => 'long_description' values are matched  from API and Seals_DB *** " ,ExtentColor.GREEN));
					logger.info("*** PASS=>'long_description' values are matched  from API and DB ***\n" );

				} else {

					test.log(Status.FAIL, MarkupHelper.createLabel( "*** Failed ==> 'long_description' value are not matched from API and Seals_DB *** ",ExtentColor.RED) );
					logger.info("*** FAIL =>'long_description' values are not matched  from API and DB *** \n");				

					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );

					test.log(Status.FAIL,"API Is => " + FileandEnv.endAndFile().get("ServerUrl") + APIPath.apiPath.setPrintHeadDetailsUrl(
							APIPath.apiPath.GET_PRINT_HEAD_DETAILS.toString(),sn,pn,startDate,endDate )); 

					// refer query.
					test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
					test.log(Status.FAIL,"" + getQueryForShortLongDescriptionValuesFromDB(sn,pn,startDate,endDate) );

					serialNoList.add(sn);
				}
			} 
			//				else {
			//				logger.info("From  DB, '" + key + "'  Objects Count= " + listDbVal.size() );
			//				logger.info("From API, '" + key + "' Objects Count= " + listApiVal.size());
			//				test.log(Status.PASS, "From  DB, '" + key + "'  Objects Count= " + listDbVal.size() );
			//				test.log(Status.PASS, "From API, '" + key + "'  Objects Count= " + listApiVal.size() );
			//				logger.info("\n 'long_description' objects count is not same From API Response and DB \n" );
			//				test.log(Status.FAIL,"'long_description' Objects count is not Same From API Response and DB " );
			//
			//				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below API Response ************ ", ExtentColor.RED) );
			//
			//				logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
			//						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate));
			//
			//				test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
			//						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), sn, pn, startDate, endDate)); 
			//				// refer query.
			//				test.log(Status.FAIL,MarkupHelper.createLabel("******** Refer below SQL Query Result *************** ", ExtentColor.RED) );
			//				test.log(Status.FAIL,"" + getQueryForShortLongDescriptionValuesFromDB(sn,pn,startDate,endDate) );
			//
			//				serialNoList.add(sn);
			//			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		test.log(Status.PASS, MarkupHelper.createLabel("############################################ END ########################################### ",ExtentColor.ORANGE));
	}	








	//#################################### End getErrorEvents API vs DB ############################################	

	/*
	 * Verify *************'Severity' from API only*********************************************** 
	 */
	public static  void verifyGetErrorEvents_severity(Response response, String listObj, String key ,String serialNo,
			String productNo,String startDate, String endDate, List serialNoList)
	{
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		test.log(Status.PASS, "No of Json Objects ===> " + list.size());
		List alist = new ArrayList();

		int count = 0 ;
		try {
			if (list.size() > 0) 
			{
				for (int i = 0; i < list.size(); i++) 
				{
					String actualVal = list.get(i).get(key);
					alist.add(actualVal);
					try {																						
						if("ADVISORY".equals(actualVal)) {
							//serialNoList.add(serialNo);
							test.log(Status.PASS,"PASS..Severity is= " + actualVal);

						}
						else if( "SILENT".equals(actualVal) ) {
							test.log(Status.PASS,"PASS..Severity is= " + actualVal);

						}
						else if( "SEVERE".equals(actualVal) ) {
							test.log(Status.PASS,"PASS..Severity is= " + actualVal);
						}
						else if( "EMERGENCY".equals(actualVal) ) {
							test.log(Status.PASS,"PASS..Severity is= " + actualVal);
						}
						else {
							serialNoList.add(serialNo);
							test.log(Status.FAIL,"FAILed..Severity is= " + actualVal);

							logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate));

							test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
									APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
											serialNo,productNo,startDate,endDate)); 
							//break;
						}

					}catch(Exception e) {						
						test.log(Status.FAIL, e.fillInStackTrace());
					}						
					count = i + 1; 
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}


	/*
	 * ************************* Private Methods *******************************************
	 */

	private static String getQueryForErrorEventsValuesFromDB(String sn, String pn, String startDate, String endDate) 
	{
		StringBuilder sb = new StringBuilder();
		List<String> listObj = new ArrayList<>();
		//SELECT srl_nr, prod_nr,cust_cd,svrty,evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr ='SG92K11001' and prod_nr= '4DC17A'and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59';

		// Write query.
		sb.append("SELECT srl_nr, prod_nr, cust_cd,svrty, evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr =");
		sb.append("'" + sn + "'" + " AND prod_nr =" + "'" + pn + "'");
		sb.append("and evt_ocrd_ts BETWEEN");
		sb.append(" '" + startDate +"'" + " AND " + "'" + endDate + "'");

		String sql = sb.toString();
		//logger.info("\n<=======Query====> \n" + sql + "\n" );
		return sql;
	}


	private static String getQueryForShortLongDescriptionValuesFromDB(String sn, String pn, String startDate, String endDate) 
	{

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT distinct(pld.cust_cd) ,pld.srl_nr, pld.prod_nr,  pld.svrty, pfm.series, es.printer_family , es.short_description , es.long_description\r\n" + 
				"FROM app_bm_graphics_lf_telemetry.prntr_log_dtl pld, app_bm_graphics_lf_telemetry.event_solution es\r\n" + 
				"INNER JOIN app_bm_graphics_lf_telemetry.printer_family_mapping pfm ON pfm.product_no =");
		sb.append("'" + pn + "'" );
		sb.append(" WHERE pld.srl_nr ='" + sn + "' and pld.prod_nr= '" + pn +"' " );
		sb.append(" AND pld.evt_ocrd_ts BETWEEN '" + startDate + "' AND '" + endDate + "' ");
		sb.append("AND es.event_code = pld.cust_cd and (es.short_description like '%' OR es.long_description like '%')");
		sb.append(" AND es.printer_family = (SELECT pf.series FROM app_bm_graphics_lf_telemetry.printer_family_mapping pf WHERE pf.product_no = ");
		sb.append("'" + pn + "' )");

		String sql = sb.toString();
		//System.out.println("\n<=======Query====> \n" + sql + "\n" );

		return sql;
	}

	private static String getNoOfErrorCount_DB(String serialNo,String startDate,String endDate)
	{
		StringBuilder sb = new StringBuilder();

		//SELECT SUM(records) FROM ( SELECT COUNT(cust_cd) AS records FROM app_bm_graphics_lf_telemetry.prntr_log_dtl where srl_nr ='SG92K11001' and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59' GROUP BY cust_cd  HAVING COUNT(*) > 0 ) ";

		sb.append("SELECT SUM(records) FROM ( SELECT COUNT(cust_cd) AS records FROM app_bm_graphics_lf_telemetry.prntr_log_dtl where srl_nr =");
		sb.append("'" + serialNo + "'" );
		sb.append("and evt_ocrd_ts BETWEEN");
		sb.append(" '" + startDate +"'" +" AND " + "'" + endDate + "'");
		sb.append(" GROUP BY cust_cd  HAVING COUNT(*) > 0 ) ");

		String sql = sb.toString();
		//System.out.println("\n<=======Query====> \n" + sql + "\n" );
		return sql;
	}

} // End Class
