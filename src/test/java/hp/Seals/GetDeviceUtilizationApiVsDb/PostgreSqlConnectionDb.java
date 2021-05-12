package hp.Seals.GetDeviceUtilizationApiVsDb;

import java.sql.*;
import java.util.*;
import org.apache.log4j.*;
import utils.ExtentReportListener;
import com.aventstack.extentreports.Status;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Connection;
//import com.aventstack.extentreports.Status;
//import hp.Seals.APITest.*;


public class PostgreSqlConnectionDb  {

	final static Logger logger = LogManager.getLogger(PostgreSqlConnectionDb.class);
	
	static final String dbURL = "jdbc:postgresql://sealsprod.cx4d1xjawowi.us-west-2.redshift.amazonaws.com:5439/dev";
	static final String username = "pradeepta.panigrahi@hp.com";
	static final String userPassword = "5$ONqldNV100P";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	//private String query_custCd = "Select top 1 srl_nr ,prod_nr, cust_cd From app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr = 'SG92K11001' AND prod_nr = '4DC17A'";
	//private String querry_errorCount = "SELECT SUM(records) FROM ( SELECT COUNT(cust_cd) AS records FROM app_bm_graphics_lf_telemetry.prntr_log_dtl where srl_nr ='SG92K11001' and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59' GROUP BY cust_cd  HAVING COUNT(*) > 0 ) ";

	private Connection getConnection() {

		try {
			con = DriverManager.getConnection(dbURL, username, userPassword);
			// Open a connection and define properties.
			//System.out.println("\n****************************************\n");
			//System.out.println("Connecting to database......and....System Tables.....");
			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", userPassword);
			con = DriverManager.getConnection(dbURL, props);

			// Try a simple query.
			//System.out.println("\nListing system tables..........");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}




	/*  
	 * ***********************************************************************************************************
	 * getting 'productive_Hours' value  from DB
	 * 
	 */

	public double getProductiveHoursValueFromDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{
		DeviceUtilizationPojo deviceUtilizationObj = null;
		List<DeviceUtilizationPojo> listVal = new ArrayList<DeviceUtilizationPojo>();
		double productive_Hours = 0.0;
		double sum = 0;
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
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

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			System.out.println("sql->\n"+ sql + "\n");
			rs = stmt.executeQuery(sql);

			// Get the data from the result set.			
			while (rs.next()) 
			{	
				deviceUtilizationObj = new DeviceUtilizationPojo();
				deviceUtilizationObj.setStatus(rs.getString("status"));
				deviceUtilizationObj.setT(rs.getLong("t"));

				//Productive= (PRINTING+SETUP+SUPPLIES+MAINTENANCE+UIR)/3600;
				if("PRINTING".equals(rs.getString("status")) || "SETUP".equals(rs.getString("status")) ||
						"SUPPLIES".equals(rs.getString("status")) || "MAINTENANCE".equals(rs.getString("status")) 
						|| "UIR".equals(rs.getString("status")) ) 
				{ 	
					//System.out.println(deviceUtilizationObj.getT());
					sum +=  deviceUtilizationObj.getT();
				}
				//System.out.println(" sum= " + sum);
				//listVal.add(deviceUtilizationObj);
			}
			//System.out.println("\n Addition result= " + sum);
			productive_Hours += sum / 3600;
			System.out.println("From DB, value of productive_Hours= " + productive_Hours);

		} catch (SQLException e) {
			e.printStackTrace();

		}finally {
			try {
				rs.close();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//logger.info("From DB,object SIZE= " + productive_Hours.size() );
		if(productive_Hours == 0) {
		       logger.info("From DB, Query has No response for the given data=>   SN= " + serial_no + "  and  PN= " + product_no );
		     //  test.log(Status.WARNING, "From DB, Query has No response for the given data=>   SN= " + serial_no + "  and  PN= " + product_no );     
		       return productive_Hours;
		}
		
		
		return productive_Hours ;
	} // Method END	

	/*  
	 * ***********************************************************************************************************
	 * getting 'on_Hours' value  from DB
	 * 
	 */

	public double getOnHoursValueFromDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{
		DeviceUtilizationPojo deviceUtilizationObj = null;
		List<DeviceUtilizationPojo> listVal = new ArrayList<DeviceUtilizationPojo>();
		double on_Hours = 0.0;
		double sum = 0.0;
		double total = 0.0;
		double off = 0.0;
		double uir = 0.0;
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {

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

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();

			System.out.println("sql->\n"+sb.toString());

			rs = stmt.executeQuery(sql);

			// Get the data from the result set.			
			while (rs.next()) 
			{	
				deviceUtilizationObj = new DeviceUtilizationPojo();
				deviceUtilizationObj.setStatus(rs.getString("status"));
				deviceUtilizationObj.setT(rs.getLong("t"));

				//on_Hours =Total -OFF - UIR= PRINTING+SETUP+SUPPLIES+MAINTENANCE+IDLE  
				total = total + deviceUtilizationObj.getT();

				if("PRINTING".equals(rs.getString("status")) || "SETUP".equals(rs.getString("status")) ||
						"SUPPLIES".equals(rs.getString("status")) || "MAINTENANCE".equals(rs.getString("status")) 
						|| "IDLE".equals(rs.getString("status")) ) 
				{ 					
					sum  +=  deviceUtilizationObj.getT();
				}

//				if("OFF".equals(rs.getString("status"))) {
//					off += deviceUtilizationObj.getT();
//				}
//				if("UIR".equals(rs.getString("status"))) {
//					uir += deviceUtilizationObj.getT();
//				}

				listVal.add(deviceUtilizationObj);
			}
			//System.out.println("\n total= " + total);
			//on_Hours +=  (total-off-uir) / 3600;
			
			on_Hours +=  sum / 3600;
			System.out.println("From DB, Value of on_Hours= " + on_Hours );

		} catch (SQLException e) {
			e.printStackTrace();

		}finally {
			try {
				rs.close();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println("From DB,object SIZE= " + on_Hours.size() );
		if(on_Hours == 0) {
		       logger.info("\n From DB, Query has No response for the given data=>   SN= " + serial_no + "  and  PN= " + product_no + "\n");
		     //  test.log(Status.WARNING, "From DB, Query has No response for the given data=>   SN= " + serial_no + "  and  PN= " + product_no );     
		       return on_Hours;
		}
		
		return on_Hours ;
		
	} // Method END	

	/*  
	 * ***********************************************************************************************************
	 * getting 'serial_no' value  from DB
	 * 
	 */

	public String getSerialNoValueFromDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{
		Set<String> setVal = new HashSet<String>();
		String srNo = "";
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {

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

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();

			System.out.println("sql->\n" + sql + "\n");
			
			rs = stmt.executeQuery(sql);

			// Get the data from the result set.			
			while (rs.next()) 			{	
				setVal.add(rs.getString("serial_no"));				
			}
			//System.out.println("\n Set Value= " + setVal);

			// copy elements from set to string array
			for (String st : setVal) {
				srNo = st;
			}	 
			//logger.info("From DB, The serial_no is "+ srNo);

		} catch (SQLException e) {
			e.printStackTrace();

		}finally {
			try {
				rs.close();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if(srNo == "") {
		       logger.info("\nFrom DB, Query has No response for the given data=>   SN= " + serial_no + "  and  PN= " + product_no + "\n" );
		     //  test.log(Status.WARNING, "From DB, Query has No response for the given data=>   SN= " + serial_no + "  and  PN= " + product_no );     
		       logger.info("\n============From DB ,'SerialNo'= " + srNo + "\n" );
		       return srNo;
		}
		logger.info("\n============From DB ,'SerialNo'= " + srNo );		
		return srNo;

	} // Method END	

	
	
	/*
	 * ******************************** DRIVER MAIN METHOD ******************************************
	 *  
	 */
	public static void main(String[] args) {
		PostgreSqlConnectionDb ob = new PostgreSqlConnectionDb();
		
	//	ob.getProductiveHoursValueFromDB("SG5371P001", "CZ056A", "2020-07-01T00:00:00Z", "2020-07-05T00:00:00Z");
	//	ob.getOnHoursValueFromDB("SG5371P001", "CZ056A", "2020-07-01T00:00:00Z", "2020-07-05T00:00:00Z");
	
//		ob.getProductiveHoursValueFromDB("SG5371P001", "CZ056A", "2019-12-09T11:48:55Z", "2020-02-06T23:41:13Z");
//		ob.getOnHoursValueFromDB("SG5371P001", "CZ056A", "2019-12-09T11:48:55Z", "2020-02-06T23:41:13Z");
//			
//		ob.getProductiveHoursValueFromDB("MY91A1T004", "3XD61A", "2020-06-06T00:00:00Z", "2020-06-20T23:59:59Z");
//		ob.getOnHoursValueFromDB("MY91A1T004", "3XD61A", "2020-06-06T00:00:00Z", "2020-06-20T23:59:59Z");
//
//		ob.getProductiveHoursValueFromDB("SG68D1N001", "K4T88A", "2020-09-20T10:10:19Z", "2020-09-21T10:10:19Z");
//		ob.getOnHoursValueFromDB("SG68D1N001", "K4T88A", "2020-09-20T10:10:19Z", "2020-09-21T10:10:19Z");
//
//		
//		ob.getProductiveHoursValueFromDB("MY97U1T003", "5HB06A", "2020-08-10T00:00:00Z", "2020-08-20T23:59:59Z");
//		ob.getOnHoursValueFromDB("MY97U1T003", "5HB06A", "2020-08-10T00:00:00Z", "2020-08-20T23:59:59Z");		
//
//		ob.getProductiveHoursValueFromDB("SG87D1R001", "1HA07A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z");
//		ob.getOnHoursValueFromDB("SG87D1R001", "1HA07A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z");
//
//		ob.getProductiveHoursValueFromDB("CN8AI0H001", "Y3T75A", "2019-02-01T00:00:00Z", "2019-02-10T00:00:00Z");
//		ob.getOnHoursValueFromDB("CN8AI0H001", "Y3T75A", "2019-02-01T00:00:00Z", "2019-02-10T00:00:00Z");
//				
//		ob.getProductiveHoursValueFromDB("SG6B11Q003", "L2E27A", "2019-07-08T11:59:59Z", "2019-08-08T11:59:59Z");
//		ob.getOnHoursValueFromDB("SG6B11Q003", "L2E27A", "2019-07-08T11:59:59Z", "2019-08-08T11:59:59Z");
	
		//ob.getProductiveHoursValueFromDB("MY83214010", "2ET72A", "2019-07-05T00:00:00Z", "2019-07-15T23:59:59Z");
		//ob.getOnHoursValueFromDB("SG6B11Q003", "L2E27A", "2019-07-08T11:59:59Z", "2019-08-08T11:59:59Z");
	//	ob.getSerialNoValueFromDB("SG6B11Q003", "L2E27A", "2019-07-08T11:59:59Z", "2019-08-08T11:59:59Z");
		
		System.out.println("*********************************************************************************\n");
		//ob.getProductiveHoursValueFromDB("CN8AI0H001","Y3T75A","2020-08-01T20:00:00Z","2020-08-05T13:59:59Z"); // no response
		//ob.getOnHoursValueFromDB("CN8AI0H001","Y3T75A","2020-08-01T20:00:00Z","2020-08-05T13:59:59Z");
		//ob.getSerialNoValueFromDB("CN8AI0H001","Y3T75A","2020-08-01T20:00:00Z","2020-08-05T13:59:59Z");  // no response
		
		ob.getSerialNoValueFromDB("SG87P1N002","4DC17A","2020-08-01T20:00:00Z","2020-08-05T13:59:59Z"); // no response
		ob.getSerialNoValueFromDB("MY79E1401B","2ET72A","2020-08-01T20:00:00Z","2020-08-05T13:59:59Z"); // no response
		
		ob.getOnHoursValueFromDB("SG87P1N002","4DC17A","2020-08-01T20:00:00Z","2020-08-05T13:59:59Z");
	    ob.getProductiveHoursValueFromDB("SG87P1N002","4DC17A","2020-08-01T20:00:00Z","2020-08-05T13:59:59Z");	
	
	}
}
