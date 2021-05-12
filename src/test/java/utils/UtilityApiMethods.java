package utils;

import java.util.*;
import com.google.gson.*;
import org.json.*;
import org.apache.log4j.*;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.testng.*;
import org.testng.asserts.*;

//import com.relevantcodes.extentreports.Status;

import apiConfig.APIPath;
import io.restassured.response.Response;

@SuppressWarnings("unused")
public class UtilityApiMethods extends ExtentReportListener
{

	final static Logger logger = LogManager.getLogger(UtilityApiMethods.class);
	static SoftAssert softAssert = new SoftAssert();

	/*
	 * Verify Response body
	 */
	public static void responseBodyValidation(Response response) 
	{
		try {
			// test.log(Status.INFO, "***********Checking Response Body**************");
			String responseBody = response.getBody().asString();
			// logger.info("<===== Response Body =====> \n" + responseBody + "\n\n");
			// test.log(Status.PASS, "Response Body ==> " + responseBody);
			Assert.assertTrue(responseBody != null);
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Verify Response Status code Validation
	 */
	public static void responseCodeValidation(Response response, int statusCode)
	{
		test.log(Status.INFO,MarkupHelper.createLabel( "************************************ Check Response Code **********************************", ExtentColor.BLUE));
		try {
			Assert.assertEquals(statusCode, response.getStatusCode());
			test.log(Status.PASS, "Successfully Validated Status Code is ==> " + response.getStatusCode());
		} catch (AssertionError e) {
			test.log(Status.FAIL, e.fillInStackTrace());
			test.log(Status.FAIL,"Expected Status Code ==> " + statusCode + ",Instead of Getting:: " + response.getStatusCode());
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Verify Response Status code Validation
	 */
	public static void responseCodeValidation(Response response, int statusCode ,String apiName, String serialNo,String productNo,String startDate, String endDate)
	{
		test.log(Status.INFO,MarkupHelper.createLabel( "************************************ Check Response Code **********************************", ExtentColor.BLUE));
		String key = "error" ;
		String ErrorVal = "[\"No data in requested duration\"]"; 
		
		//logger.info("JsonObject value:-" +  jsonObj.toString() );

		try {
			if(statusCode == response.getStatusCode() || 201 == response.getStatusCode() ) 
			{				
				logger.info("Validated Status Code is ==> " + response.getStatusCode());
				test.log(Status.PASS, "Successfully Validated Status Code is ==> " + response.getStatusCode());

			}else if(400 == response.getStatusCode() ) 
			{
				JSONObject jsonObj = new JSONObject(response.getBody().asString());
				if (jsonObj.has(key) && jsonObj.get(key) != null) 
				{
					String keyVal = String.valueOf((jsonObj.get(key)));
					//logger.info("key value:-" + keyVal );	

					if(ErrorVal.equals(keyVal)) 
					{
						logger.info("WARNing due to Status Code ==> " + response.getStatusCode());

						test.log(Status.WARNING, MarkupHelper.createLabel(  FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName + "?SN="+ serialNo + "&PN=" 
								+ productNo + "&startDate=" + startDate + "&endDate=" + endDate ,ExtentColor.ORANGE  ));						

						test.log(Status.WARNING, "Warning:  due  to  Status  Code =  " 
								+ response.getStatusCode() + "  & the response is  " + "\"error\": [\r\n" 
								+ "\"No data in requested duration\"\r\n" + "]" );


						logger.info("Error: No data in requested duration");
					}
					else
					{
						test.log(Status.FAIL,MarkupHelper.createLabel(  FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName + "?SN="+ serialNo + "&PN=" 
								+ productNo + "&startDate=" + startDate + "&endDate=" + endDate, ExtentColor.RED ));

						test.log(Status.FAIL, MarkupHelper.createLabel( "Failed due to Status Code ==> " + response.getStatusCode() 
						+ "  & the response is " + "\"error\":[\"An unexpected error happened\"]"  , ExtentColor.RED ));


						logger.info("Failed due to Status Code ==> " + response.getStatusCode());
						logger.info("\"error\":[\"An unexpected error happened\"]");
					}
				}
			} else {

				logger.info("Wrong status code is returned =  " + response.getStatusCode());

				test.log(Status.FAIL,MarkupHelper.createLabel(  FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName + "?SN="+ serialNo + "&PN=" 
						+ productNo + "&startDate=" + startDate + "&endDate=" + endDate, ExtentColor.RED) );

				test.log(Status.FAIL,MarkupHelper.createLabel( "Wrong status code is returned =   "+ response.getStatusCode(),ExtentColor.RED) );

			}
		} catch (AssertionError e) {
			test.log(Status.FAIL, e.fillInStackTrace());
			test.log(Status.FAIL,"Expected Status Code ==> " + statusCode + ",Instead of Getting:: " + response.getStatusCode());
		} 
		catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	// getMaintenanceTasks
	public static void verifyResponseCode(Response response, int statusCode ,String apiName, String SN,String PN , String date)
	{
		test.log(Status.INFO,MarkupHelper.createLabel( "*********************************** Check Response Code **********************************", ExtentColor.BLUE));	
		String key = "error" ;
		String ErrorVal = "[\"No data in requested duration\"]"; 
		JSONObject jsonObj = new JSONObject(response.getBody().asString());
		//logger.info("JsonObject value:-" +  jsonObj.toString() );

		try {
			if(statusCode == response.getStatusCode() || 201 == response.getStatusCode() ) 
			{				
				logger.info("Validated Status Code is ==> " + response.getStatusCode());
				test.log(Status.PASS, "Successfully Validated Status Code is ==> " + response.getStatusCode());

			}else if(400 == response.getStatusCode() ) 	{

				if (jsonObj.has(key) && jsonObj.get(key) != null) 
				{
					String keyVal = String.valueOf((jsonObj.get(key)));
					//logger.info("key value:-" + keyVal );	

					if(ErrorVal.equals(keyVal)) 
					{

						test.log(Status.WARNING,MarkupHelper.createLabel(FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName 
								+ "?SN="+ SN + "&PN=" + PN + "&date=" + date , ExtentColor.ORANGE ));

						test.log(Status.WARNING, MarkupHelper.createLabel( "Warning:  due  to  Status  Code = " 
								+ response.getStatusCode() + "  & the response is  " + "\"error\": [\r\n" 
								+ "\"No data in requested duration\"\r\n" + "]", ExtentColor.ORANGE) );

						logger.info("WARNING due to Status Code ==> " + response.getStatusCode());
						logger.info("Error: No data in requested duration");

					} else {

						test.log(Status.FAIL, MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName 
								+ "?SN="+ SN + "&PN=" + PN + "&date=" + date , ExtentColor.RED ) );

						test.log(Status.FAIL, MarkupHelper.createLabel( "Failed due to Status Code ==> " + response.getStatusCode() 
						+ "  & the response is " + "\"error\":[\"An unexpected error happened\"]" , ExtentColor.RED) );

						logger.info("Failed due to Status Code ==> " + response.getStatusCode());
						logger.info("\"error\":[\"An unexpected error happened\"]");
					}
				}
			} else {

				logger.info("Wrong status code is returned =  " + response.getStatusCode());

				test.log(Status.FAIL,  MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName 
						+ "?SN="+ SN + "&PN=" + PN + "&date=" + date ,ExtentColor.RED  ));

				test.log(Status.FAIL, MarkupHelper.createLabel( "Wrong status code is returned = " + response.getStatusCode(),ExtentColor.RED) );

			}

		} catch (AssertionError e) {
			test.log(Status.FAIL, e.fillInStackTrace());
			test.log(Status.FAIL,"Expected Status Code ==> " + statusCode + ",Instead of Getting:: " + response.getStatusCode());
		} 
		catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	//get Obligations
	public static void ValidateResponseCode(Response response, int statusCode ,String apiName, String serialNumber,String productID)
	{
		test.log(Status.INFO,MarkupHelper.createLabel( "************************************ Check Response Code **********************************", ExtentColor.BLUE));	
		String key = "error" ;
		String ErrorVal = "[\"No data in requested duration\"]"; 
		JSONObject jsonObj = new JSONObject(response.getBody().asString());
		//logger.info("JsonObject value:-" +  jsonObj.toString() );

		try {
			if(statusCode == response.getStatusCode() || 201 == response.getStatusCode() ) 
			{				
				logger.info("Validated Status Code is ==> " + response.getStatusCode());
				test.log(Status.PASS, "Successfully Validated Status Code is ==> " + response.getStatusCode());

			}else if(400 == response.getStatusCode() ) 	{
				if (jsonObj.has(key) && jsonObj.get(key) != null) 
				{
					String keyVal = String.valueOf((jsonObj.get(key)));
					//logger.info("key value:-" + keyVal );	

					if(ErrorVal.equals(keyVal)) 
					{				
						test.log(Status.WARNING, MarkupHelper.createLabel(  FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName + "?serialNumber=" 
								+ serialNumber + "&productID=" + productID + "&Mode=all" , ExtentColor.ORANGE) );

						test.log(Status.WARNING, MarkupHelper.createLabel( "Warning:  due  to  Status  Code = " 
								+ response.getStatusCode() + "  & the response is  " + "\"error\": [\r\n" 
								+ "\"No data in requested duration\"\r\n" + "]", ExtentColor.ORANGE) );

						logger.info("WARNING due to Status Code ==> " + response.getStatusCode());
						logger.info("Error: No data in requested duration");

					}
					else {

						test.log(Status.FAIL,MarkupHelper.createLabel(  FileandEnv.endAndFile().get("ServerUrl") + "/" 
								+ apiName + "?serialNumber=" + serialNumber + "&productID=" + productID + "&Mode=all" , ExtentColor.RED ));

						test.log(Status.FAIL, MarkupHelper.createLabel( "Failed due to Status Code ==> " + response.getStatusCode() 
						+ "  & the response is " + "\"error\":[\"An unexpected error happened\"]" , ExtentColor.RED ) );


						logger.info("FAILed due to Status Code ==> " + response.getStatusCode());
						logger.info("\"error\":[\"An unexpected error happened\"]");

					}
				}
			}else {

				logger.info("Wrong status code is returned = " + response.getStatusCode());

				test.log(Status.FAIL,MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName 
						+ "?serialNumber="+ serialNumber + "&productID=" + productID + "&Mode=all" , ExtentColor.RED ));

				test.log(Status.FAIL,MarkupHelper.createLabel( "Wrong status code is returned = " 
						+ response.getStatusCode(),ExtentColor.RED) );

			}
		} catch (AssertionError e) {
			test.log(Status.FAIL, e.fillInStackTrace());
			test.log(Status.FAIL,"Expected Status Code ==> " + statusCode + ",Instead of Getting:: " + response.getStatusCode());
		} 
		catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}


	// GetObligations Api 
	public static void Validate_ResponseTime2(Response response,String apiName, String SN, String PN)
	{
		test.log(Status.INFO, MarkupHelper.createLabel("************************************ Check Response Time **********************************",ExtentColor.BLUE));
		try {
			Long responseTime = response.time();
			test.log(Status.PASS, "Actual Response Time is =====> " + responseTime);

			if(responseTime > 600000 ) {
				test.log(Status.FAIL, "FAIL- Response Time= " + responseTime + " > 600000 " + " for SerialNo=" + SN + " & ProductNo=" + PN);

			} else	if (responseTime >= 3000  ) {

				softAssert.assertEquals(responseTime, responseTime >= 3000,	"Expected Response Time is exceeding 3000 ms.....");

				test.log(Status.WARNING, FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName + "?serialNumber="+ SN +
						"&productID=" + PN + "&Mode=all" );

				test.log(Status.WARNING, "WARNING:- due to Response Time= " + responseTime + " > 3000 " + 
						" for SerialNo=" + SN + " & ProductNo=" + PN);

				test.log(Status.WARNING,"Expected Response Time is exceeding 3000 ms.....");

			} else {
				softAssert.assertEquals(responseTime, responseTime < 3000,"Expected Response Time is not exceeding than 3000 ms.....");
				test.log(Status.PASS, "ResponseTime=" + responseTime + " < 3000  " + " for SerialNo=" + SN + " & ProductNo=" + PN);
			}

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	public static void validateResponseCode(Response response, int statusCode ,String apiName, String serialNumber,String productID)
	{
		test.log(Status.INFO,MarkupHelper.createLabel( "************************************ Check Response Code **********************************", ExtentColor.BLUE));	
		String key = "error" ;
		String ErrorVal = "[\"No data in requested duration\"]"; 
		JSONObject jsonObj = new JSONObject(response.getBody().asString());
		//logger.info("JsonObject value:-" +  jsonObj.toString() );

		try {
			if(statusCode == response.getStatusCode() || 201 == response.getStatusCode() ) 
			{				
				logger.info("Validated Status Code is ==> " + response.getStatusCode());
				test.log(Status.PASS, "Successfully Validated Status Code is ==> " + response.getStatusCode());

			}else if(400 == response.getStatusCode() ) 	{
				if (jsonObj.has(key) && jsonObj.get(key) != null) 
				{
					String keyVal = String.valueOf((jsonObj.get(key)));
					//logger.info("key value:-" + keyVal );	

					if(ErrorVal.equals(keyVal)) 
					{				
						test.log(Status.WARNING, MarkupHelper.createLabel(  FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName + "?serialNumber=" 
								+ serialNumber + "&productID=" + productID + "&Mode=all" , ExtentColor.ORANGE) );

						test.log(Status.WARNING, MarkupHelper.createLabel( "Warning:  due  to  Status  Code = " 
								+ response.getStatusCode() + "  & the response is  " + "\"error\": [\r\n" 
								+ "\"No data in requested duration\"\r\n" + "]", ExtentColor.ORANGE) );

						logger.info("WARNING due to Status Code ==> " + response.getStatusCode());
						logger.info("Error: No data in requested duration");

					}
					else {

						test.log(Status.FAIL,MarkupHelper.createLabel(  FileandEnv.endAndFile().get("ServerUrl") + "/" 
								+ apiName + "?serialNumber=" + serialNumber + "&productID=" + productID + "&Mode=all" , ExtentColor.RED ));

						test.log(Status.FAIL, MarkupHelper.createLabel( "Failed due to Status Code ==> " + response.getStatusCode() 
						+ "  & the response is " + "\"error\":[\"An unexpected error happened\"]" , ExtentColor.RED ) );


						logger.info("FAILed due to Status Code ==> " + response.getStatusCode());
						logger.info("\"error\":[\"An unexpected error happened\"]");

					}
				}
			}else {

				logger.info("Wrong status code is returned = " + response.getStatusCode());

				test.log(Status.FAIL,MarkupHelper.createLabel( FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName 
						+ "?serialNumber="+ serialNumber + "&productID=" + productID + "&Mode=all" , ExtentColor.RED ));

				test.log(Status.FAIL,MarkupHelper.createLabel( "Wrong status code is returned = " 
						+ response.getStatusCode(),ExtentColor.RED) );

			}
		} catch (AssertionError e) {
			test.log(Status.FAIL, e.fillInStackTrace());
			test.log(Status.FAIL,"Expected Status Code ==> " + statusCode + ",Instead of Getting:: " + response.getStatusCode());
		} 
		catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	//for getsolution API
	public static void verifyResponseCode1(Response response, int statusCode ,String apiName, String serialNumber, String productID, String eventCode ,String detectionDate)
	{
		test.log(Status.INFO,MarkupHelper.createLabel( "************************************ Check Response Code **********************************", ExtentColor.BLUE));	
		String key = "error" ;
		String ErrorVal = "[\"No data in requested duration\"]"; 
		JSONObject jsonObj = new JSONObject(response.getBody().asString());

		try {
			if(statusCode == response.getStatusCode() || 201 == response.getStatusCode() ) 
			{				
				logger.info("Validated Status Code is ==> " + response.getStatusCode());
				test.log(Status.PASS, "Successfully Validated Status Code is ==> " + response.getStatusCode());

			}else if(400 == response.getStatusCode() ) 	{
				if (jsonObj.has(key) && jsonObj.get(key) != null) 
				{
					String keyVal = String.valueOf((jsonObj.get(key)));
					//logger.info("key value:-" + keyVal );	

					if(ErrorVal.equals(keyVal)) 
					{				
						test.log(Status.WARNING, FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName + "?SN="+ serialNumber 
								+ "&PN=" + productID + "&eventCode" + eventCode +"&detectionDate" + detectionDate);

						test.log(Status.WARNING,  "Warning:  due  to  Status  Code = " 
								+ response.getStatusCode() + "  & the response is  " + "\"error\": [\r\n" 
								+ "\"No data in requested duration\"\r\n" + "]" );

						logger.info("WARNING due to Status Code ==> " + response.getStatusCode());
						logger.info("Error: No data in requested duration");

					}
					else {

						test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName + "?SN="+ serialNumber 
								+ "&PN=" + productID + "&eventCode" + eventCode +"&detectionDate" + detectionDate);

						test.log(Status.FAIL, "Failed due to Status Code ==> " + response.getStatusCode() 
						+ "  & the response is " + "\"error\":[\"An unexpected error happened\"]"  );


						logger.info("FAILed due to Status Code ==> " + response.getStatusCode());
						logger.info("\"error\":[\"An unexpected error happened\"]");

					}
				}
			}else {

				logger.info("Wrong status code is returned = " + response.getStatusCode());

				test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + "/" + apiName + "?SN="+ serialNumber 
						+ "&PN=" + productID + "&eventCode" + eventCode +"&detectionDate" + detectionDate);

				test.log(Status.FAIL, "Wrong status code is returned = " + response.getStatusCode() );

			}

		} catch (AssertionError e) {
			test.log(Status.FAIL, e.fillInStackTrace());
			test.log(Status.FAIL,"Expected Status Code ==> " + statusCode + ",Instead of Getting:: " + response.getStatusCode());
		} 
		catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}


	/*
	 * Checking status line
	 * 
	 */
	public static void statusLineValidation(Response response, String statline)
	{
		try {
			test.log(Status.INFO, "***********Checking status line *****************");
			String statusLine = response.getStatusLine();
			test.log(Status.PASS, "Status Line ==> " + statusLine);
			Assert.assertEquals(statusLine, statline);
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Check content Type
	 */
	public static void contentTypeValidation(Response response, String content_type)
	{
		try {
			test.log(Status.INFO, "***********Check content Type *****************");
			String conentType = response.header("Content-Type");
			test.log(Status.PASS, "Content Type ==> " + conentType);
			Assert.assertEquals(conentType, content_type);
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Check content encoding
	 */
	public static void contentEncodingValidation(Response response, String content_encoding)
	{
		try {
			test.log(Status.INFO, "***********Check content Encoding *****************");
			String conentEncoding = response.header("Content-Encoding");
			test.log(Status.PASS, "Content Encoding ==> " + conentEncoding);
			Assert.assertEquals(conentEncoding, "gzip");

		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

	}

	/*
	 * Check content Length
	 */
	public static void contentLengthValidation(Response response) 
	{
		try {
			test.log(Status.INFO, "***********Check content Length *****************");
			String conentLength = response.header("Content-Length");
			test.log(Status.INFO, "Content Length is ==> " + conentLength);
			if (Integer.parseInt(conentLength) < 100)
				test.log(Status.WARNING, "Content length is less than 100");

			Assert.assertTrue(Integer.parseInt(conentLength) > 100);
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

	}

	/*
	 * Verify Session cookies
	 */
	public static void contentCookiesValidation(Response response, String cookieValue)
	{
		try {
			test.log(Status.INFO, "***********Check Cookies *****************");
			String cookie = response.getCookie("SESSIONID");
			test.log(Status.PASS, "Cookies is ==> " + cookie);
			Assert.assertEquals(cookie, cookieValue);
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Verify Response Time Validation
	 */
	public static void ResponseTimeValidation(Response response)
	{
		test.log(Status.INFO, "***********Checking  Response TIME *****************");
		try {
			Long time = response.time();
			Assert.assertTrue(time < 5000, "Expected Time is not exceeding.....");
			test.log(Status.INFO, "Response Time is ==> " + time);
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Check Response Time Validation
	 */
	public static void Validate_ResponseTime(Response response)
	{
		test.log(Status.INFO,MarkupHelper.createLabel( "************************************ Check Response Time **********************************",ExtentColor.BLUE));
		try {
			Long responseTime = response.time();
			test.log(Status.PASS, "Actual Response Time is =========> " + responseTime);
			if (responseTime >= 3000) {
				softAssert.assertEquals(responseTime, responseTime >= 3000,	"Expected Response Time is exceeding 5000 ms.....");
				test.log(Status.PASS, "Response Time = " + responseTime + " is more than 3000 ms");
				logger.info("Response Time = " + responseTime + " is more than 3000 ms");

			} else {
				softAssert.assertEquals(responseTime, responseTime < 3000,"Expected Response Time is not exceeding than 3000 ms.....");
				test.log(Status.PASS, "Response Time = " + responseTime + " is less than 3000 ms");
				logger.info("Response Time = " + responseTime + " is less than 3000 ms");
			}
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/* 
	 * Verify Response Key Validation from JSON Array 
	 * 
	 */	
	public static void ResponseKeyValidationFromArray(Response response,String key)
	{
		try {
			JSONArray arr = new JSONArray(response.getBody().asString());
			for(int i=0; i<arr.length(); i++)
			{
				JSONObject obj = arr.getJSONObject(i);
				// logger.info(obj.get("title"));
				test.log(Status.PASS, "Successfully Validated value is --> "+ key + "It is " + obj.get(key));
			}
		} catch(Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}	
	}

	/* 
	 * Parse JSON Using org.json  and convert the JSON string into a JSON Object
	 */
	public static String getResponseKeyFromJson(Response response,String listObj,String key)
	{
		String keyValue = "";
		String json = response.asString();
		JSONObject jsonObj = new JSONObject(json);      
		try {			
			if(jsonObj.getJSONObject(listObj)!= null )
			{	
				keyValue = jsonObj.getJSONObject(listObj).getString(key);
				test.log(Status.PASS, "Key value from API====>"+ key + ": " + keyValue );
			}
		} catch(Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		logger.info("Key and Value::> "+ key + ":" + keyValue);		
		return keyValue;
	}

	/*
	 * Parse JSON Using Gson
	 */
	public static String getResponseKeyFromGSON(Response response,String listObj,String key)
	{
		String keyValue = "";
		String json = response.asString();
		JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject(); 
		//logger.info("===Json Object==== " + jsonObject);
		try {			
			if( jsonObject.has(listObj) && jsonObject.get(listObj) != null )
			{
				JsonArray arr = jsonObject.getAsJsonArray(listObj);
				//logger.info("Json Object Count= " + arr.size());
				for (int i = 0; i < arr.size(); i++)
				{
					keyValue = arr.get(i).getAsJsonObject().get(key).getAsString();
					test.log(Status.PASS, "Validated value is --> "+ key + "It is " + keyValue );
					//logger.info("Key and Value: "+ key + ":" + keyValue);
				}
			}
		} catch(Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		logger.info("Key and Value: "+ key + ":" + keyValue);		
		return keyValue;
	}

	public static String verifyEventCode(Response response,String listObj,String key,String serialNo,
			String productNo ,String eventCode, String detectionDate)
	{
		String keyValue = "";
		String json = response.asString();
		JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject(); 
		//logger.info("===Json Object==== " + jsonObject);
		try {			
			if( jsonObject.has(listObj) && jsonObject.get(listObj) != null ) {

				JsonArray arr = jsonObject.getAsJsonArray(listObj);
				//logger.info("Json Object Count= " + arr.size());
				for (int i = 0; i < arr.size(); i++){

					keyValue = arr.get(i).getAsJsonObject().get(key).getAsString();
					test.log(Status.PASS, "Validated value is --> "+ key + "It is " + keyValue );
					logger.info("Key and Value: "+ key + ":" + keyValue);
					if(keyValue.equals(eventCode)) 
					{
						//Assert.assertEquals(keyValue, eventCode, "eventCode value is not matched");
						logger.info("eventCode value is  matched");
					} 
					else
					{
						logger.info("eventCode value is not matched");
						test.log(Status.FAIL, FileandEnv.endAndFile().get("ServerUrl") + 
								APIPath.apiPath.setGetSolutionUrl(APIPath.apiPath.GET_SOLUTION.toString(),
										serialNo,productNo,eventCode,detectionDate));

						test.log(Status.FAIL,"Failed Due to eventCode value is not matched " + "event_code: " +keyValue );

					}
				}
			}

		} catch(Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}

		//logger.info("Key and Value: "+ key + ":" + keyValue);		
		return keyValue;
	}

	/* 
	 * Checking Key Value from API Response
	 * 
	 */
	public static void getKeyValueFromResponse(Response response,String listObj,String key)
	{	
		try {
			List<Map<String, String>> list = response.jsonPath().getList(listObj);
			test.log(Status.PASS, "No of Json Objects ==> " + list.size());
			//logger.info = list.get(0).get(key);
			//logger.info("Size----> " + list.size() );
			int count = 0;
			if(list.size()>0) 
			{	
				for(int i = 0; i<list.size(); i++)
				{
					String actualVal = list.get(i).get(key);
					test.log(Status.PASS, "Validated Key--> " + key +" : " + actualVal);	
					logger.info("Key Value is ----> " +  actualVal);
					count = i+1; ;
				}
				//logger.info("Key value count is ----> " +  count);
			}
		}catch(Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 *  count number of objects from Response     
	 */
	public static Integer getCountNoOfObjectsFromResponse(Response response, String listObj, String key)
	{
		int responseCount = 0;
		int KeyValueCount = 0;
		try {
			List<Map<String, String>> list = response.jsonPath().getList(listObj);
			test.log(Status.PASS, "No of Key value count from Response ====> " + list.size());
			responseCount = list.size();
			//logger.info("No of Key value count from Response ----> "+responseCount + "\n\n");
			if(list.size() > 0) 
			{				
				for(int i = 0; i<list.size() ; i++)
				{
					String actualVal = list.get(i).get(key);
					//test.log(Status.PASS, "Key value is present--> " + key +" : " + actualVal);
					KeyValueCount = i+1;
				}
			}	
			Assert.assertEquals(responseCount, KeyValueCount, "API Response object count is not matched");
		}catch(Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
		return responseCount;
	}

	/*
	 * Verify Response Key Validation from '0'th Level
	 */
	public static void ResponseKeyValidationFromJsonObject(Response response, String key)
	{
		try {
			JSONObject jsonObj = new JSONObject(response.getBody().asString());

			if (jsonObj.has(key) && jsonObj.get(key) != null) {
				test.log(Status.PASS, "Successfully Validated KEY & VALUE for Serial number is ===> " + key + "::> " + jsonObj.get(key));
				logger.info("<====Validate KEY & VALUE for Serial number is====> " + key + ": " + jsonObj.get(key));
			} else
				test.log(Status.FAIL, "Key is not available");
		} catch (Exception e) {
			test.log(Status.FAIL, e.fillInStackTrace());
		}
	}

	/*
	 * Checking one Key Value of Json Object from API Response 
	 * 
	 */
	public static List<String> verifyKeyValueFromResponse(Response response, String listObj, String key)
	{
		List alist = null;

		if(400 == response.getStatusCode()) {
			logger.info("WARNing due to Status Code ==> " + response.getStatusCode());
			test.log(Status.FAIL, MarkupHelper.createLabel( "Status Code is 400 "  ,ExtentColor.RED  ));

		} else {
			List<Map<String, String>> list = response.jsonPath().getList(listObj);
			//logger.info("No of Json Objects ===> " + list.size());
			//test.log(Status.PASS, "No of Json Objects ===> " + list.size());
			// String expectedVal = list.get(0).get(key);

			alist = new ArrayList();
			int count = 0 ;
			try {
				if (list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						String actualVal = String.valueOf(list.get(i).get(key));
						if( key != null && actualVal != null ) {
							alist.add(actualVal);
							count = i + 1; 
						}
					}
				}
			} catch (Exception e) {
				test.log(Status.FAIL, e.fillInStackTrace());
			}
			//	logger.info("Number of Objects count ::> " + count );
			softAssert.assertEquals(list.size(),count ,"Key value count is not matched");
		}
		logger.info("From API, '" + key + "'  values are ==>> "+ alist);
		//test.log(Status.PASS, " From API, '" + key + "'  values Lists are ===> "+ alist);		
		return alist;
	}



	/*
	 * Find the key value from Response ***********************N+1 Level*****************
	 */
	public static  Map getKey1(JSONObject json, String key) 
	{
		//System.out.println(" method getKey called");
		int count = 0;
		String val = "";
		boolean exists = json.has(key);

		Map<String, String> keyValMap = new LinkedHashMap();

		if (!exists) {

			Iterator<?> keys = json.keys();

			while (keys.hasNext())
			{
				String nextKey = (String) keys.next();

				try {
					if (json.get(nextKey) instanceof JSONObject) {

						JSONObject jsonObj = json.getJSONObject(nextKey);

						if (jsonObj.has(key)) {
							val = json.get(key).toString();
							keyValMap.put(key, val);

						}

					} else if (json.get(nextKey) instanceof JSONArray)
					{
						JSONArray jsonArray = json.getJSONArray(nextKey);

						for (int i = 0; i < jsonArray.length(); i++) 
						{

							String jsonArrayString = jsonArray.get(i).toString();

							JSONObject innerJson = new JSONObject(jsonArrayString);

							if (innerJson.has(key))
							{
								val = innerJson.get(key).toString();
								keyValMap.put(key + "[" + i +"]", val);

								try {
									Float floatVal = Float.valueOf(val).floatValue();
									//   System.out.println("Float: " + floatVal);

									if( floatVal  < 0 )
									{
										logger.info("Key value is negative...> " + key + "[" + i + "] : " + val );
										test.log(Status.FAIL, "**** FAILED **** IF Key value is Negative ====>  " + key + "[" + i + "] : " + val);
									}
								}catch(Exception e) {
									e.fillInStackTrace();
								}
							}
							count = count + 1;
						}
					}

				} catch (Exception e) {

					e.printStackTrace();
				}
			}

		} else {

			val = json.get(key).toString();
			keyValMap.put(key, val);
		}

		logger.info("Objects Count :=>  " + count );

		return keyValMap;
	}

	/*
	 * parsing nested  json and get Key Value From Response body
	 */
	// How to parse Dynamic json and nested json
	public static String  parseObject(JSONObject json, String key)
	{
		String actualVal = json.get(key).toString();
		//logger.info("Key : Value ==> " + key + " : " + actualVal);
		//test.log(Status.PASS, "Key : value ==> " + key + " : " + actualVal);
		return  actualVal;
	}

	public static String getKey(JSONObject json,String key)
	{
		boolean exists = json.has(key);
		String keyVal = "";		
		Iterator<?> keys;
		String nextKeys;

		if(!exists) {
			keys = json.keys();
			while(keys.hasNext())
			{
				nextKeys = (String)keys.next();

				try {
					if(json.get(nextKeys) instanceof JSONObject) 
					{
						if (exists == false) 
						{
							getKey(json.getJSONObject(nextKeys),key);
						}
					}
					else if(json.get(nextKeys) instanceof JSONArray)
					{
						JSONArray jsonArray = json.getJSONArray(nextKeys);
						//System.out.println("<===== JsonArray =====> \n" + jsonArray + "\n");
						for(int i=0; i<jsonArray.length(); i++)
						{
							String jsonArrayString = jsonArray.get(i).toString();
							//logger.info("<===== JsonArrayString =====> \n" + jsonArrayString + "\n");
							JSONObject innerJson = new JSONObject(jsonArrayString);
							if(exists == false)
							{
								getKey(innerJson,key);
							}
						}

					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		else {
			keyVal = parseObject(json,key);	

		}
		return keyVal;
	}

	/*
	 * 
	 * Return key value pair from API Response ********************************************
	 */
	public static Integer counter = 0;
	public static Map<Integer, HashMap<String, String>>    map=new HashMap<Integer, HashMap<String,String>>();
	public static  Map<Integer, HashMap<String, String>>   get_Key(JSONObject json, String key)
	{
		String val = "";

		boolean exists = json.has(key);

		Iterator<?> keys;

		String nextKey;

		if (!exists)
		{
			keys = json.keys();

			while (keys.hasNext())
			{
				nextKey = (String) keys.next();

				try {

					if (json.get(nextKey) instanceof JSONObject)
					{
						if (exists == false)
						{
							get_Key(json.getJSONObject(nextKey), key);
						}
					}
					else if (json.get(nextKey) instanceof JSONArray)
					{
						JSONArray jsonArray = json.getJSONArray(nextKey);

						for (int i = 0; i < jsonArray.length(); i++)
						{
							String jsonArrayString = jsonArray.get(i).toString();

							JSONObject innerJson = new JSONObject(jsonArrayString);

							if (exists == false)
							{
								get_Key(innerJson, key);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		try {

			val = json.get(key).toString();
			// System.out.println(" -key method-------> " + val);
			// keyValMap.put(key, val);
			HashMap<String, String> m = new HashMap<String, String>();
			m.put(key, val);
			map.put(counter,m);
			// System.out.println(" -key method-------> " + map);
			counter++;
		}
		catch (Exception e) {
			//
		}	
		//System.out.println(" -key method-------> " + map);
		return map;
	}

	/*
	 * Verify  list of values of Key
	 */
	public static List  VerifyMoreKeyValues(Response response, String key)
	{		
		List listOb = new ArrayList() ;
		String result = response.getBody().asString();
		//System.out.println("result:>>> " + result );
		JSONObject jsonObj = null;

		try {			
			jsonObj = new JSONObject(result);
			//System.out.println("jsonObj-" + jsonObj);
			listOb = getKeyVaues(jsonObj, key);							
			//test.log(Status.PASS, "Key value from Response =>  " + listOb );

		} catch (JSONException e) {
			test.log(Status.FAIL, e.fillInStackTrace());	
		}
		//test.log(Status.PASS, "Key value from API Response =>  " + listOb );
		logger.info("\n Key List Values are ==>  " + listOb );
		return listOb ;
	}

	static List   lst = new ArrayList();
	public static List  getKeyVaues(JSONObject json, String key)
	{		
		String val = "";
		List mList=new ArrayList();
		boolean exists = json.has(key);

		Iterator<?> keys;
		String nextKey;
		if (!exists)
		{
			keys = json.keys();
			while (keys.hasNext())	{

				nextKey = (String) keys.next();
				try {
					if (json.get(nextKey) instanceof JSONObject) {
						if (exists == false) {
							getKeyVaues(json.getJSONObject(nextKey), key);
						}
					} else if (json.get(nextKey) instanceof JSONArray)	{

						JSONArray jsonArray = json.getJSONArray(nextKey);

						for (int i = 0; i < jsonArray.length(); i++) {

							String jsonArrayString = jsonArray.get(i).toString();

							JSONObject innerJson = new JSONObject(jsonArrayString);

							if (exists == false) {
								getKeyVaues(innerJson, key);
							}
						}
					}
				}
				catch (Exception e)	{
					e.printStackTrace();
				}
			}
		}

		try {
			val = json.get(key).toString();			
			lst.add(val);
		}
		catch (Exception e) {

		}
		//System.out.println(" -key method-------> " + lst);
		return lst;
	}


	//*****************************************************************************



} // End Class





