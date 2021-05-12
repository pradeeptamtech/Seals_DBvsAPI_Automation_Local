package apiVerification;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import org.apache.log4j.*;
import org.json.*;
import org.testng.*;
import org.testng.asserts.*;

import com.amazonaws.entity.PrinterStateResult;
import com.amazonaws.samples.PostgreSqlConnection;

import apiConfig.APIPath;
import hp.Seals.APITest.*;
import io.restassured.response.Response;
import utils.*;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;


public class APIVerification extends ExtentReportListener
{
	final static Logger logger = LogManager.getLogger(APIVerification.class);

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

	/* **********************************************************************************************************
	 * Return List of objects from API Response  
	 * 
	 */
	@SuppressWarnings("unused")
	public static List getListVauesfromAPi(Response response, String listObj, String key1, String key2)
	{
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		test.log(Status.PASS, "No of API Json Objects ==> " + list.size());
		PrinterStateResult printerStateResult = null;
		List<PrinterStateResult> listResult = new ArrayList<PrinterStateResult>();
		int count = 0;
		try {
			if(list == null) {
				logger.info("FAILed: API has 400 bad request ");
				test.log(Status.FAIL,"API has 400 bad request  and response is error\":[\"An unexpected error happened\"]\r\n" );																				
			}
			else if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					printerStateResult = new PrinterStateResult();
					String sub_status = list.get(i).get(key1);
					String status = list.get(i).get(key2);
					printerStateResult.setSub_Status(sub_status);
					printerStateResult.setStatus(status);
					//test.log(Status.PASS,"Validated Keys--> " + key1 + " : " + sub_status + "\t\t" + key2 + ": " + status);
					logger.info("Validated Keys--> " + key1 + " : " + sub_status + "\t\t" + key2 + ": " + status);
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
			String serial_no, String product_no , String startDate,String endDate ,List serialNoList )
	{		

		List<PrinterStateResult> resultObjApi = getListVauesfromAPi(response, listObj, key1, key2);  // Calling Api response List method

		List<PrinterStateResult> resultFromDB = objSql.getStatusAndSubStatus( serial_no, product_no, startDate,endDate ); // calling DB Method

		test.log(Status.INFO, MarkupHelper.createLabel("******Compare 'status' and 'sub_Status' values from API response and Database *******",ExtentColor.AMBER));
		test.log(Status.PASS, MarkupHelper.createLabel("Values from API Response::>  " + resultObjApi ,ExtentColor.BROWN));
		test.log(Status.PASS,MarkupHelper.createLabel("Values from Databse ::>>  " + resultFromDB, ExtentColor.PINK) );

		try {
			if(resultObjApi.equals(resultFromDB))
			{
				softAssert.assertEquals(resultObjApi, resultFromDB, "DB and API Response object count is not matched");
				//				test.log(Status.PASS,"Values from API Response::>  " + resultObjApi );
				//				test.log(Status.PASS,"Values from Databse ::>>  " + resultFromDB );

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

				serialNoList.add(serial_no);
			}
		}catch(Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());	
		}
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

	public static void validateKeyFromResponseWithDB(Response response, String listObj , String key) 
	{
		test.log(Status.INFO, "***********Checking  value 'cust_cd' from Response compare with Database *****************");

		try {

			String actualValueFromDB = getValueFromDB();
			logger.info("Key Value From DB------> " + actualValueFromDB);
			String expectedValuefromApi = UtilityApiMethods.getResponseKeyFromGSON(response,listObj, key);
			logger.info("Key Value From Api------> " + expectedValuefromApi);
			Assert.assertEquals(actualValueFromDB, expectedValuefromApi,"key value is not matched from DB and response body");

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Compare error count from API response and DB  **************************************
	 */
	public static int getErrorCountFromDB(String serialNo, String startDate, String endDate)
	{
		test.log(Status.PASS, "No of Key value count from Database ====> " + objSql.getNoOfErrorCount(serialNo, startDate, endDate).getSum());
		return objSql.getNoOfErrorCount(serialNo, startDate, endDate).getSum();
	}

	public static void validateErrorCountObjectWithDB(Response response, String listObj, String key, 
			String serialNo,String productNo, String startDate, String endDate, List serialNoList ) 
	{
		logger.info("***Compare error code count from API response and DB ****\n");
		try {
			test.log(Status.INFO,MarkupHelper.createLabel("*****Compare Error code count from API response and Database ****",ExtentColor.CYAN));
			int errorCodeCountFromDB = getErrorCountFromDB(serialNo, startDate, endDate);
			logger.info("Error count From Database------> " + errorCodeCountFromDB);

			int errorCountFromApi = verifyObjectsCountFromResponse(response, listObj, key);
			logger.info("Error Count From API Response------> " + errorCountFromApi + "\n");

			if (errorCodeCountFromDB == errorCountFromApi) {
				softAssert.assertEquals(errorCodeCountFromDB, errorCountFromApi, "ErrorCode count is not matched");
				test.log(Status.INFO,MarkupHelper.createLabel("<===PASS====Error count is matched  from API and DB =====> \n",ExtentColor.BROWN));
			} else {
				softAssert.assertEquals(errorCodeCountFromDB, errorCountFromApi, "ErrorCode count is not matched");
				test.log(Status.INFO, "<============TEST CASE FAILED =================> \n");
				test.log(Status.INFO, MarkupHelper.createLabel("<=====Error count is not same from API and DB ====> \n",ExtentColor.RED));
				logger.info("<==== Error count is not same from API and DB ====>");
				serialNoList.add(serialNo);
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		// logger.info("Fail Serial no list ======> " + serialNoList);
	}

	/*
	 * Verify 'error code' value from API *********************************************** 
	 */
	@SuppressWarnings("unused")
	public static  List verifyGetErrorEvents_errorCode(Response response, String listObj, String key ,String serialNo,
			String productNo,String startDate, String endDate )
	{
		List<Map<String, String>> list = response.jsonPath().getList(listObj);
		test.log(Status.PASS, "No of Json Objects ===> " + list.size());
		logger.info("No of Json Objects ===> " + list.size());
		List alist = new ArrayList();

		int count = 0 ;
		try {
			if(list == null) 
			{
				logger.info("FAILed: API has 400 bad request ");

				test.log(Status.WARNING, FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(), serialNo,productNo,startDate,endDate));

				test.log(Status.WARNING,"Error: No data in requested duration ");
				logger.info("Error: No data in requested duration");

				logger.info(FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setPrintUrl(APIPath.apiPath.GET_ERROR_EVENTS.toString(),
								serialNo,productNo,startDate,endDate));

			} else if (list != null && list.size() > 0)	{			
				//				logger.info("No of Json Objects ===> " + list.size());
				//				test.log(Status.PASS, "No of Json Objects ===> " + list.size());

				for (int i = 0; i < list.size(); i++) 
				{
					String actualVal = list.get(i).get(key);
					alist.add(actualVal);

					//serialNoList.add(serialNo);
				}
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		logger.info(" List of error Codres are from API=> "+ alist);
		//test.log(Status.PASS, " List of error Codres are from API=> "+ alist);

		return alist;
	}


	/*
	 * Compare errorcode value from API response and DB  **************************************
	 */
	public static List getErrorCodeFromDB(String serialNo,String prod_no, String startDate, String endDate)
	{
		//test.log(LogStatus.PASS, "No of Key value count from Database ====> " + objSql.getErrorCode_DB(serialNo,prod_no, startDate, endDate ));
		return objSql.getErrorCode_DB(serialNo,prod_no, startDate, endDate);
	}
	public static void validateErrorCodeWithDB(Response response, String listObj, String key, 
			String serialNo,String prod_no,String startDate, String endDate, List serialNoList) 
	{
		logger.info("*******Compare error_code Value from API response and DB ************** ");
		test.log(Status.INFO,MarkupHelper.createLabel("***Compare Error_code value from API response and Database ***", ExtentColor.AMBER));
		try {	
			List errorCodeFromApi = verifyGetErrorEvents_errorCode(response, listObj, key,serialNo, prod_no,  startDate,  endDate);
			logger.info("Error_Code values list From API Response => " + errorCodeFromApi );
			test.log(Status.PASS, "Error_Code values list From API Response => "+ errorCodeFromApi);

			List errorCodeFromDB = getErrorCodeFromDB(serialNo,prod_no, startDate, endDate);
			logger.info("Error_Code values list From Database => " + errorCodeFromDB);
			test.log(Status.INFO,"Error_Code values list From Database => " + errorCodeFromDB);

			if (errorCodeFromDB.equals( errorCodeFromApi)) {
				softAssert.assertEquals(errorCodeFromDB, errorCodeFromApi, "ErrorCode value is not matched");
				test.log(Status.PASS, MarkupHelper.createLabel("<===PASS=======Error code value is matched  from API and DB =======>", ExtentColor.GREEN));
				logger.info("<====ErrorCode vaue is matched from API and DB ======>\n");

			} else {
				softAssert.assertEquals(errorCodeFromDB, errorCodeFromApi, "ErrorCode MarkupHelper.createLabel( is not matched");

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
				//break;


			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

	}


	/*
	 * Compare Maintenance Task progress_percentage count from API response and DB  ************************
	 */
	public static int getProgress_percentageCountFromDB(String serialNo,String productNo, String startDate, String endDate)
	{
		test.log(Status.PASS, "No of Key value count from Database ====> " + objSql.getCountProgressPercentage(serialNo,productNo, startDate, endDate).getSum());
		return objSql.getCountProgressPercentage(serialNo,productNo, startDate, endDate).getSum();
	}

	public static void validateProgress_percentageCountApiWithDB(Response response, String listObj, String key, 
			String serialNo,String productNo ,String date, String startDate,  List serialNoList) 
	{
		logger.info("***Compare progress_Percentage count from API response and DB *****\n");
		try {
			test.log(Status.INFO,"***********Compare progress_Percentage count from API response and Database *****");
			int progressPercentageCountFromDB = getProgress_percentageCountFromDB(serialNo,productNo, startDate, date);
			logger.info("progress_Percentage count From Database------> " + progressPercentageCountFromDB);

			int progressPercentageCountFromApi = verifyObjectsCountFromResponse(response, listObj, key);
			logger.info("progress_Percentage Count From API Response------> " + progressPercentageCountFromApi + "\n");

			if (progressPercentageCountFromDB == progressPercentageCountFromApi) {
				Assert.assertEquals(progressPercentageCountFromDB, progressPercentageCountFromApi, "ErrorCode count is not matched");
				test.log(Status.INFO, "<===PASS=======Error count is matched  from API and DB =================> \n");
			} else {
				softAssert.assertEquals(progressPercentageCountFromDB, progressPercentageCountFromApi, "ErrorCode count is not matched");
				test.log(Status.INFO, "<============progress_Percentage count is not same from API and DB =================> \n");

				test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setGetMaintenanceTaskUrl(APIPath.apiPath.GET_MAINTENANCE_TASK.toString(),
								serialNo,productNo,date)); 
				test.log(Status.INFO, "<============TEST CASE FAILED =================> \n");

				serialNoList.add(serialNo);
				logger.info( "<============progress_Percentage count is not same from API and DB =================> \n");
				logger.info("Fail Serial no list ======> " + serialNoList);
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

	}

	/*
	 * Compare printerEntitled 'existsIndicator'value from API response and DB  **************************************
	 */
	public static String getPrinterEntitledFromDB(String serialNo, String productNo)
	{
		test.log(Status.PASS, "Key value from Database ====> " + objSql.getPrinterEntitled(serialNo, productNo).getIs_entitled() );
		return objSql.getPrinterEntitled(serialNo, productNo).getIs_entitled();
	}

	public static void comparePrinterEntitledFromApiAndDB(Response response, String listObj, String key,String serialNo,String productNo, List serialNoList) 
	{
		logger.info("***Compare 'existsIndicator'value from API response and DB ****\n");
		try {
			test.log(Status.INFO, MarkupHelper.createLabel("******Compare 'existsIndicator' value from API response and Database *******",ExtentColor.AMBER));
			String isEntitledFromDB = getPrinterEntitledFromDB(serialNo, productNo);
			logger.info("is_entitled From Database------> " + isEntitledFromDB);

			String existsIndicatorFromApi = UtilityApiMethods.getResponseKeyFromJson(response, listObj, key);
			logger.info("'existsIndicator' value From API Response------> " + existsIndicatorFromApi + "\n"); 

			if ( existsIndicatorFromApi.equalsIgnoreCase(isEntitledFromDB) || (isEntitledFromDB == null) && "false".equalsIgnoreCase(existsIndicatorFromApi) ) {
				softAssert.assertEquals(isEntitledFromDB, existsIndicatorFromApi, "'existsIndicator' value is not matched");
				test.log(Status.PASS,MarkupHelper.createLabel("PASS=>'existsIndicator'value is matched  from API and DB =====> \n",ExtentColor.GREEN));
			} else {
				softAssert.assertEquals(isEntitledFromDB, existsIndicatorFromApi, "'existsIndicator' value is not matched");
				test.log(Status.FAIL, MarkupHelper.createLabel("Failed=> 'existsIndicator'value is not same from API and DB =====> \n",ExtentColor.RED));

				test.log(Status.FAIL,MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setPrinterEntitledUrl(APIPath.apiPath.GET_PrinterEntitled.toString(),serialNo,productNo),ExtentColor.RED)); 

				serialNoList.add(serialNo);

			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Verify *************'Severity'*********************************************** 
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
	 * get Obligations attributes compare with DB
	 */

	public static void compareExistsIndicatorFromApiAndDB(Response response, String listObj, String key,String serialNo,String productNo) 
	{
		logger.info("***Compare 'existsIndicator'value from API response and DB ****\n");
		try {
			test.log(Status.INFO, MarkupHelper.createLabel("******Compare 'existsIndicator' value from API response and Database *******",ExtentColor.AMBER));
			String isEntitledFromDB = getPrinterEntitledFromDB(serialNo, productNo);
			logger.info("is_entitled From Database------> " + isEntitledFromDB);

			String existsIndicatorFromApi = UtilityApiMethods.getResponseKeyFromJson(response, listObj, key);
			logger.info("'existsIndicator' value From API Response------> " + existsIndicatorFromApi + "\n"); 

			if ( existsIndicatorFromApi.equalsIgnoreCase(isEntitledFromDB) || (isEntitledFromDB == null) && "false".equalsIgnoreCase(existsIndicatorFromApi) ) {
				softAssert.assertEquals(isEntitledFromDB, existsIndicatorFromApi, "'existsIndicator' value is not matched");
				test.log(Status.PASS,MarkupHelper.createLabel("PASS=>'existsIndicator'value is matched  from API and DB =====> \n",ExtentColor.GREEN));
			} else {
				softAssert.assertEquals(isEntitledFromDB, existsIndicatorFromApi, "'existsIndicator' value is not matched");
				test.log(Status.FAIL, MarkupHelper.createLabel("Failed=> 'existsIndicator'value is not same from API and DB =====> \n",ExtentColor.RED));

				test.log(Status.FAIL,MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setPrinterEntitledUrl(APIPath.apiPath.GET_PrinterEntitled.toString(),serialNo,productNo),ExtentColor.RED)); 
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Compare 'offerCoder' value from API response and DB  *for getObligation API ***********************
	 */

	public static List getOfferCodeValuesFromDB(String serialNo, String productNo)
	{
		//test.log(Status.PASS, "Key value from Database ====> " + objSql.getOfferCode_DB(serialNo, productNo) );

		return objSql.getOfferCode_forContract_FromDB(serialNo, productNo) ;
	}

	public static void compareOfferCode_C_FromApiAndDB(Response response, String key,String serialNumber,String productID) 
	{
		logger.info("\n***Compare 'offerCode' value from API response and DB ****\n");
		try {
			test.log(Status.INFO, MarkupHelper.createLabel("******Compare 'offerCode' value from API response and Database *******",ExtentColor.AMBER));

			List<String> list1 = new LinkedList<String>(); 
			list1=	getOfferCodeValuesFromDB(serialNumber, productID);
			logger.info("'offerCode' value From Database =>> " + list1);
			test.log(Status.PASS,"'offerCode' value From Database =>> " + list1 );

			List<String> list2 = new LinkedList<String>();			
			list2 = UtilityApiMethods.VerifyMoreKeyValues(response, key);
			logger.info("'offerCode' value From API Response =>> " + list2 + "\n"); 
			test.log(Status.PASS,"'offerCode' value From API Response =>> " + list2 );

			if ( list1.equals(list2) ) {
				//softAssert.assertEquals(list1, list2, "'offerCode' value is not matched");
				test.log(Status.PASS,MarkupHelper.createLabel("PASS=>'offerCode is matched  from API and DB =====> \n",ExtentColor.GREEN));
				logger.info("PASS=>'offerCode is matched  from API and DB =====>\n");
			} else {
				//softAssert.assertEquals(list1, list2, "'offerCode' value is matched");
				test.log(Status.FAIL, MarkupHelper.createLabel("Fail--> 'offerCode'value is not same from API and DB ===> \n",ExtentColor.RED));
				logger.info("FAIL =>'offerCode is Not matched  from API and DB =====>\n");				
				test.log(Status.FAIL,MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setObligationUrl(APIPath.apiPath.GET_OBLIGATION.toString(),serialNumber,productID),ExtentColor.RED)); 
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Compare contract type offerCode  from GetObligations API with Seals Database
	 */
	public static List getOfferCode_C_FromDB(String serialNo, String productNo)
	{
		//test.log(Status.PASS, "Key value from Database ====> " + objSql.getOfferCode_forContract_FromDB(serialNo, productNo) );	
		return objSql.getOfferCode_forContract_FromDB(serialNo, productNo) ;
	}

	/*
	 * verify getObligations attributes from API
	 */	
	public static List  validateMoreKeyValues(Response response,String type, String key) {

		String result = response.getBody().asString();
		//System.out.println("result:>>> " + result );
		List<String> obligationResult = null;
		JSONObject inputJSONObject = null;
		try {
			inputJSONObject = new JSONObject(result);
			obligationResult = new ArrayList<String>();
			Map<Integer, HashMap<String, String>> resultMap = UtilityApiMethods.get_Key(inputJSONObject, type); // type = contract

			for (Entry<Integer, HashMap<String, String>> entrySet : resultMap.entrySet()) {
				HashMap<String, String> entryset1 = entrySet.getValue();
				for (Entry<String, String> s1 : entryset1.entrySet()) {
					String list = s1.getValue();
					JSONArray jsonRules = new JSONArray(list);
					for (int i = 0; i < jsonRules.length(); i++) {
						JSONObject obj = (JSONObject) jsonRules.get(i);
						// System.out.println("====obj====" + obj);

						String offerCode = obj.getString(key);
						obligationResult.add(offerCode);
					}
				}
			}
		} catch (JSONException e) {
			test.log(Status.FAIL, e.fillInStackTrace());	
		}
		logger.info("Obligation Result=" + obligationResult);

		return obligationResult;
	}

	public static void compareContractOfferCodeFromApiAndDB(Response response,String type, String key,String serialNumber,String productID) 
	{
		logger.info("\n\n***Compare 'offerCode' value from API response and DB ****");
		List<String> list1 = null;
		List<String> list2 = null;
		try {
			test.log(Status.INFO, "******Compare contract type'offerCode' value from API response and Database *******" );

			list1 = new ArrayList<String>(); 
			list1 =	getOfferCode_C_FromDB(serialNumber, productID );
			logger.info("'offerCode' value From Database =>> " + list1);
			test.log(Status.PASS,"'offerCode' value From DB Response =>> " + list1);

			list2 = new ArrayList<String>();			
			list2 = validateMoreKeyValues(response,type, key);
			logger.info("'offerCode' value From API Response =>> " + list2 + "\n"); 
			test.log(Status.PASS,"'offerCode' value From API Response =>>" + list2 );

			if ( list1.equals(list2) ) {
				softAssert.assertEquals(list1, list2, "'offerCode' value is not matched");
				test.log(Status.PASS,"PASS=>'offerCode is matched  from API and DB ");
				logger.info("PASS: 'offerCode is matched  from API and DB \n");

			} else {
				//softAssert.assertEquals(list1, list2, "'offerCode' value is matched");
				test.log(Status.FAIL, "Failed: 'offerCode'value is not same from API and DB " );
				logger.info("FAIL: 'offerCode is not matched  from API and DB \n");				
				test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + 
						APIPath.apiPath.setObligationUrl(APIPath.apiPath.GET_OBLIGATION.toString(),serialNumber,productID) ); 
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 *   Verify Contract object from *** getobligations API
	 */
	public static List  validateContractFromObligationAPI(Response response ) {

		String result = response.getBody().asString();

		JSONObject inputJSONObject = null;
		Embeded emb = new Embeded();

		try {
			inputJSONObject = new JSONObject(result);
			//logger.info("inputJSONObject=> "+ inputJSONObject);

			JSONObject embeded = inputJSONObject.getJSONObject("_embedded");
			//logger.info("Embeded-" + embeded + "\n");

			JSONArray jsonArray = embeded.getJSONArray("contract");
			//logger.info("jsonArray::>>> " + jsonArray + "\n");

			JSONObject contractObj = null;
			Contract  contract = null;

			for (int i = 0; i < jsonArray.length(); i++)
			{
				contractObj = jsonArray.getJSONObject(i);
				contract = new Contract();

				contract.setOverallContractStartDate(contractObj.get("overallContractStartDate").toString());
				contract.setOverallContractEndDate(contractObj.get("overallContractEndDate").toString());
				contract.setStartDate(contractObj.get("startDate").toString());
				contract.setEndDate(contractObj.get("endDate").toString());
				contract.setActive(contractObj.get("active").toString());
				contract.setStatus(contractObj.get("status").toString());
				contract.setOfferCode(contractObj.get("offerCode").toString());
				contract.setPackageCode(contractObj.get("packageCode").toString());
				contract.setOfferDescription(contractObj.get("offerDescription").toString());

				emb.getContractList().add(contract);
			}
			//System.out.println("Contract List=> " + emb.getContractList().toString() );		

		} catch (JSONException e) {
			test.log(Status.FAIL, e.fillInStackTrace());	
		}
		logger.info(" Contract List=> " + emb.getContractList().toString() );

		return emb.getContractList();
	}

	// getObligation Contract object Compare with DB ********************************************************************

	public static List getContractFromDB(String serialNo, String productNo)
	{
		//test.log(LogStatus.PASS, "Key value from Database ====> " + objSql.getObligation_forContract_FromDB(serialNo, productNo) );	
		return objSql.getObligation_forContract_FromDB(serialNo, productNo) ;
	}

	public static void compareContractFromApiAndDB(Response response,String serialNumber,String productID , HashSet<String> serialNoList) 
	{
		logger.info("\n\n***Compare 'Contract' Value from API response and DB ****");
		try {
			test.log(Status.INFO, MarkupHelper.createLabel("****** Compare Contract type value from API response and Database *******",ExtentColor.AMBER) );

			List<Contract> list1 = getContractFromDB(serialNumber, productID );
			logger.info("'contract' value From Database =>> " + list1);
			test.log(Status.PASS,"'contract' value From Database =>> " + list1);

			List<Contract> list2 = validateContractFromObligationAPI(response);
			logger.info("'contract' value From API Response =>> " + list2 + "\n"); 
			test.log(Status.PASS,"'contract' value From API Response =>> " + list2);

			if( list1 == null &&  list2 == null ) {
				logger.info("'contract' object is Empty From API Response and DB " );
				test.log(Status.PASS,MarkupHelper.createLabel("'contract' object is Empty From API Response and DB ", ExtentColor.CYAN));

			} else if( list1 != null &&  list2 != null && list1.size() == list2.size() ) {
				for(int i = 0 ; i < list1.size() ; i++) {
					Contract contractApi = list1.get(i);
					Contract contractDb = list2.get(i);

					compareContract("overallContractStartDate",contractApi.getOverallContractStartDate(), contractDb.getOverallContractStartDate(), serialNumber,productID, serialNoList);
					compareContract("overallContractEndDate",contractApi.getOverallContractEndDate(), contractDb.getOverallContractEndDate() , serialNumber,productID, serialNoList);
					compareContract("startDate",contractApi.getStartDate(), contractDb.getStartDate() , serialNumber,productID , serialNoList);
					compareContract("endDate",contractApi.getEndDate() ,contractDb.getEndDate() , serialNumber,productID, serialNoList);
					compareContract("active",contractApi.getActive(), contractDb.getActive() ,serialNumber,productID, serialNoList );
					compareContract("status",contractApi.getStatus(), contractDb.getStatus() ,serialNumber,productID, serialNoList );
					compareContract("offerCode",contractApi.getOfferCode(), contractDb.getOfferCode() ,serialNumber,productID, serialNoList);
					compareContract("packageCode",contractApi.getPackageCode(), contractDb.getPackageCode(),serialNumber,productID , serialNoList);
					compareContract("offerDescription",contractApi.getOfferDescription(), contractDb.getOfferDescription(), serialNumber,productID , serialNoList);				
				}
			} else {

				logger.info("\n'contract' objects count is not same From API Response and DB " +"for serialNumber=" + serialNumber + " and productID= " + productID );
				test.log(Status.PASS,"'contract' objects count is not same From API Response and DB " + " for serialNumber=" + serialNumber + " and productID= " + productID );
			}
		} 
		catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	public static void compareContract(String contractField, String s1, String s2, String serialNumber,String productID, HashSet<String> serialNoList ) 
	{
		switch (contractField) {
		case "overallContractStartDate": compareObjects(contractField,s1,s2, serialNumber,productID, serialNoList); break;              
		case "overallContractEndDate":  compareObjects(contractField,s1,s2, serialNumber,productID, serialNoList) ; break;
		case "startDate":  compareObjects(contractField,s1,s2, serialNumber,productID, serialNoList);  break;
		case "endDate":    compareObjects(contractField,s1,s2, serialNumber,productID, serialNoList);  break;

		case "active":   compareObjects(contractField,s1,s2, serialNumber,productID, serialNoList);  break;
		//			if("status".equals("X") || "status".equals("C") || 	"status".equals("R") || "status".equals("F") )
		//				{
		//					contractField = "FALSE";
		//				} else {
		//						compareObjects(contractField,s1,s2, serialNumber,productID, serialNoList);  
		//						}	
		//			break;

		case "status":     compareObjects(contractField,s1,s2,serialNumber,productID, serialNoList);  break;
		case "offerCode":  compareObjects(contractField,s1,s2,serialNumber,productID, serialNoList);  break;   
		case "packageCode": compareObjects(contractField,s1,s2, serialNumber,productID, serialNoList); break;	
		case "offerDescription": compareObjects(contractField,s1,s2, serialNumber,productID, serialNoList); break; 
		}
	}

	//Verify Warranty object from getObligation

	public static List  validateWarrantyFromObligationAPI( Response response ) 
	{
		String result = response.getBody().asString();

		JSONObject inputJSONObject = null;
		Embeded emb = new Embeded();

		try {
			inputJSONObject = new JSONObject(result);
			//logger.info("inputJSONObject=> "+ inputJSONObject);

			JSONObject embeded = inputJSONObject.getJSONObject("_embedded");
			//logger.info("Embeded-" + embeded + "\n");

			JSONArray jsonArray = embeded.getJSONArray("warranty");
			//logger.info("jsonArray::>>> " + jsonArray + "\n");

			JSONObject warrantyObj = null;
			Warranty  warranty = null;

			for (int i = 0; i < jsonArray.length(); i++)
			{
				warrantyObj = jsonArray.getJSONObject(i);
				warranty = new Warranty();

				warranty.setOverallWarrantyStartDate(warrantyObj.getString("overallWarrantyStartDate"));
				warranty.setOverallWarrantyEndDate(warrantyObj.getString("overallWarrantyEndDate"));
				warranty.setStartDate(warrantyObj.getString("startDate"));
				warranty.setEndDate(warrantyObj.getString("endDate"));
				warranty.setActive(warrantyObj.getString("active"));
				warranty.setStatus(warrantyObj.getString("status"));
				warranty.setWarrantyDeterminationDescription(warrantyObj.getString("warrantyDeterminationDescription"));
				warranty.setFactoryWarrantyTermCode(warrantyObj.getString("factoryWarrantyTermCode"));						
				warranty.setFactoryWarrantyStartDate(warrantyObj.getString("factoryWarrantyStartDate"));
				warranty.setFactoryWarrantyEndDate(warrantyObj.getString("factoryWarrantyEndDate"));
				warranty.setOfferCode(warrantyObj.getString("offerCode"));
				warranty.setOfferDescription(warrantyObj.getString("offerDescription"));
				warranty.setSalesOrderNumber(warrantyObj.getString("salesOrderNumber"));
				warranty.setCovWindow(warrantyObj.getString("covWindow"));
				warranty.setResponseCommitment(warrantyObj.getString("responseCommitment"));

				emb.getWarrantyList().add(warranty);
			}
			//System.out.println("Contract List=> " + emb.getContractList().toString() );		

		} catch (JSONException e) {
			test.log(Status.FAIL, e.fillInStackTrace());	
		}
		logger.info(" Warranty List=> " + emb.getWarrantyList().toString() );

		return emb.getWarrantyList();
	}

	// getObligation Compare with DB ********************************************************************

	public static List getWarrantyFromDB(String serialNo, String productNo)
	{
		//test.log(Status.PASS, "Key value from Database ====> " + objSql.getObligation_forWarranty_FromDB(serialNo, productNo) );	
		return objSql.getObligation_forWarranty_FromDB(serialNo, productNo) ;
	}

	public static void compareWarrantyFromApiAndDB(Response response, String serialNumber, String productID, HashSet<String> serialNoList) 
	{
		logger.info("\n\n***Compare 'Warranty' value from API response and DB ****");
		try {
			test.log(Status.INFO,MarkupHelper.createLabel( "****** Compare Warranty type value from API response and Database *******",ExtentColor.AMBER) );

			List<Warranty> list1 = 	getWarrantyFromDB(serialNumber, productID );
			logger.info("'warranty' value From Database =>> " + list1);
			test.log(Status.PASS,"'warranty' value From Database =>> " + list1);

			List<Warranty> list2 =  validateWarrantyFromObligationAPI(response);
			logger.info("'warranty' value From API Response =>> " + list2 ); 
			test.log(Status.PASS,"'warranty' value From API Response =>> " + list2 ); 

			if( list1 == null &&  list2 == null ) {
				logger.info("'warranty' object is Empty From API Response and DB " );
				test.log(Status.PASS,MarkupHelper.createLabel("'warranty' object is Empty From API Response and DB ", ExtentColor.CYAN));

			} else if( list1 != null &&  list2 != null && list1.size() == list2.size() ) {
				for(int i = 0 ; i < list1.size() ; i++) {
					Warranty warrantyApi = list1.get(i);
					Warranty warrantyDb = list2.get(i);

					compareWarranty("overallWarrantyStartDate",warrantyApi.getOverallWarrantyStartDate(), warrantyDb.getOverallWarrantyStartDate(), serialNumber,productID, serialNoList);
					compareWarranty("overallWarrantyEndDate",warrantyApi.getOverallWarrantyEndDate(), warrantyDb.getOverallWarrantyEndDate() , serialNumber,productID, serialNoList);
					compareWarranty("startDate",warrantyApi.getStartDate(), warrantyDb.getStartDate() , serialNumber,productID , serialNoList);
					compareWarranty("endDate",warrantyApi.getEndDate() ,warrantyDb.getEndDate() , serialNumber,productID, serialNoList);
					compareWarranty("active",warrantyApi.getActive(), warrantyDb.getActive() ,serialNumber,productID, serialNoList );
					compareWarranty("status",warrantyApi.getStatus(), warrantyDb.getStatus() ,serialNumber,productID, serialNoList );
					compareWarranty("warrantyDeterminationDescription",warrantyApi.getWarrantyDeterminationDescription(), warrantyDb.getWarrantyDeterminationDescription() ,serialNumber,productID, serialNoList);
					compareWarranty("factoryWarrantyTermCode",warrantyApi.getFactoryWarrantyTermCode(), warrantyDb.getFactoryWarrantyTermCode(),serialNumber,productID , serialNoList);
					compareWarranty("factoryWarrantyStartDate",warrantyApi.getFactoryWarrantyStartDate(), warrantyDb.getFactoryWarrantyStartDate(), serialNumber,productID , serialNoList);				
					compareWarranty("factoryWarrantyEndDate",warrantyApi.getFactoryWarrantyEndDate(), warrantyDb.getFactoryWarrantyEndDate(), serialNumber,productID, serialNoList);
					compareWarranty("offerCode",warrantyApi.getOfferCode(), warrantyDb.getOfferCode() , serialNumber,productID, serialNoList);
					compareWarranty("offerDescription",warrantyApi.getOfferDescription(), warrantyDb.getOfferDescription() , serialNumber,productID , serialNoList);
					compareWarranty("salesOrderNumber",warrantyApi.getSalesOrderNumber() ,warrantyDb.getSalesOrderNumber() , serialNumber,productID, serialNoList);
					compareWarranty("covWindow",warrantyApi.getCovWindow(), warrantyDb.getCovWindow() , serialNumber,productID , serialNoList);
					compareWarranty("responseCommitment",warrantyApi.getResponseCommitment() ,warrantyDb.getResponseCommitment() , serialNumber,productID, serialNoList);

				}
			} else {
				logger.info("\n'warranty' objects count is not same From API Response and DB " +"for serialNumber=" + serialNumber + " and productID= " + productID );
				test.log(Status.PASS,"'warranty' objects count is not same From API Response and DB " + " for serialNumber=" + serialNumber + " and productID= " + productID );
			}

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	public static void compareWarranty(String warrantyField, String s1, String s2, String serialNumber,String productID, HashSet<String> serialNoList ) 
	{
		switch (warrantyField) {
		case "overallWarrantyStartDate": compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList); break;              
		case "overallWarrantyEndDate":  compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList) ; break;
		case "startDate":  compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList);  break;
		case "endDate":    compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList);  break;		
		case "active":   compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList);  break;
		case "status":     compareObjects(warrantyField,s1,s2,serialNumber,productID, serialNoList);  break;
		case "warrantyDeterminationDescription":  compareObjects(warrantyField,s1,s2,serialNumber,productID, serialNoList);  break;   
		case "factoryWarrantyTermCode": compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList); break;	
		case "factoryWarrantyStartDate": compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList); break; 
		case "factoryWarrantyEndDate": compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList); break;              
		case "offerCode":  compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList) ; break;
		case "offerDescription":  compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList);  break;
		case "salesOrderNumber":    compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList);  break;
		case "covWindow": compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList); break;              
		case "responseCommitment":  compareObjects(warrantyField,s1,s2, serialNumber,productID, serialNoList) ; break;

		}
	}

	public static void compareObjects( String Field, String s1, String s2, String serialNumber,String productID ,HashSet<String> serialNoList) 
	{
		logger.info("API "+ Field + " field Value = " + s1 + " and DB " + Field + "  Field Value = " + s2);
		test.log(Status.PASS,"API "+ Field + " field Value = " + s1 + " and DB " + Field + "  Field Value = " + s2);	

		if(s1.equals(s2)) {
			logger.info("PASS==>" + Field + " is matched from API and DB ==>\n");
			test.log(Status.PASS, "PASS=> '" + Field + "' is matched from API and DB ==> ");
		} else {
			serialNoList.add(serialNumber);
			logger.info("Fail=>" + Field + " is not matched from API and DB ==>\n");
			test.log(Status.FAIL,"From API  "+ Field + " : " + s1 + "   &   from DB " + Field + " : " + s2);
			test.log(Status.FAIL, "Fail=> Field   '" + Field + "'  is not matched from API and DB ==>for serialNumber=" + serialNumber + "  & productID=" + productID );
			test.log(Status.FAIL,FileandEnv.endAndFile().get("ServerUrl") + 
					APIPath.apiPath.setObligationUrl(APIPath.apiPath.GET_OBLIGATION.toString(),serialNumber,productID) );
		}
	}

	/*
	 * *****************************************************************************************************
	 ********* Compare 'total_Events' count from getMaintenanceEvents API response and seals DB  ************
	 */
	public static int getTotal_EventsCountFromDB(String serialNo, String productNo, String startDate, String endDate)
	{
		//test.log(LogStatus.PASS, "No of Key value count from Database ====> " + objSql.getTotal_Events_Count(serialNo,productNo, startDate, endDate).getSum());
		return objSql.getTotal_Events_Count(serialNo,productNo, startDate, endDate).getSum();
	}

	public static void validateTotal_EventsCountObjectWithDB(Response response, String key, 
			String serialNo, String productNo, String startDate, String endDate, List serialNoList) 
	{
		logger.info("\n*******Compare 'total_events' count from API response and DB ************** ");
		test.log(Status.INFO,MarkupHelper.createLabel("*****Compare total_events count from API response and Database *****",ExtentColor.PINK));
		try {			
			int total_events_CountFromDB = getTotal_EventsCountFromDB(serialNo,productNo, startDate, endDate);
			logger.info("'total_events' count From Database------> " + total_events_CountFromDB);
			test.log(Status.PASS,"'total_events' count From Database ==> " + total_events_CountFromDB );

			String events_CountFromApi =  verifyKey_Response(response, key);
			int total_events_CountFromApi = Integer.parseInt(events_CountFromApi);
			logger.info("'total_events' count From API Response------> " + total_events_CountFromApi );
			test.log(Status.PASS, "'total_events' count From API Response ==> " + total_events_CountFromApi );

			if (total_events_CountFromDB == total_events_CountFromApi) {
				Assert.assertEquals(total_events_CountFromDB, total_events_CountFromApi, " 'total_events' count is not matched");
				test.log(Status.INFO, MarkupHelper.createLabel("PASS <==== 'total_events' count is matched  from API and DB =================>",ExtentColor.CYAN));
				logger.info("PASS <====== 'total_events' count is matched  from API and DB =================> \n");

			} else {
				softAssert.assertEquals(total_events_CountFromDB, total_events_CountFromApi, " 'total_events' count is not matched");
				test.log(Status.FAIL,MarkupHelper.createLabel( "<============TEST CASE FAILED ============> ",ExtentColor.RED));
				test.log(Status.FAIL, MarkupHelper.createLabel("<========== 'total_events' count is not same from API and DB =========> ", ExtentColor.RED));
				logger.info("FAIL<======= 'total_events' count is not same from API and DB =================>\n");
				serialNoList.add(serialNo);

				// System.out.println("Fail Serial no list ======> " + lstObj);
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

	}

	

} // End Class
