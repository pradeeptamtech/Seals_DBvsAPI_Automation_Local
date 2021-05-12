package hp.Seals.printerStatusHistoryApiVsDb;

import java.sql.*;
import java.util.*;
import org.apache.log4j.*;

import com.aventstack.extentreports.Status;

import hp.Seals.APITest.*;


@SuppressWarnings("unused")
public class PostgreSqlConnectionDb {
	
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
	 * getting 'status' and 'sub_Status' value  from DB
	 * 
	 */

	public List<PrinterStateResult> getStatusAndSubStatus(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		PrinterStateResult printerStateResult =  null; // new PrinterStateResult();
		List<PrinterStateResult>  listPrinterStateResult = new ArrayList<PrinterStateResult>();

		//SELECT product_no,serial_no ,start_ts,end_ts ,sub_status, status FROM  printer_state_result where serial_no='SG92K11001' AND start_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-05 23:59:59Z'; 

		/*
		 SELECT start_ts,end_ts ,sub_status ,channel ,status 
	FROM app_bm_graphics_lf_telemetry.printer_state_result where serial_no = 'SG87D1R001' AND product_no = '1HA07A' 
	AND start_ts >= '2020-06-06 00:00:00' AND end_ts <= '2020-06-10 23:59:59' AND (end_ts-start_ts)>'0.1'
		 */
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			//String sql = "SELECT start_ts,end_ts ,sub_status, status FROM  app_bm_graphics_lf_telemetry.printer_state_result where serial_no='SG92K11001'AND product_no='4DC17A' AND  START_TS >='2020-07-01 00:00:00Z' AND END_TS <= '2020-07-05 23:59:59' order by START_TS DESC ";
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );
			sb.append("SELECT start_ts, end_ts ,sub_status, status FROM  app_bm_graphics_lf_telemetry.printer_state_result where serial_no=");
			sb.append("'" + serial_no + "'" + " AND product_no=" + "'" +product_no +"'" );
			sb.append(" AND start_ts >= ' " + start_ts + "' AND end_ts <= '" + end_ts + "'");
			sb.append(" AND (end_ts-start_ts)>'0.1'");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//logger.info("sql->"+sb.toString());
			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) 
			{	
				printerStateResult = new PrinterStateResult();
				printerStateResult.setStatus(rs.getString("status"));
				printerStateResult.setSub_Status(rs.getString("sub_Status"));

				listPrinterStateResult.add(printerStateResult);
			}
			//test.log(Status.PASS," Values from Databse ::>>  " + listPrinterStateResult );
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
		logger.info("from DB, SIZE= " + listPrinterStateResult.size() );
		logger.info("Read from DB ===> " + listPrinterStateResult );
		return listPrinterStateResult;


	} // Method END	

	/*  
	 * ***********************************************************************************************************
	 * getting 'serial_no', start_ts and 'status' value  from DB
	 * 
	 */

	public List<PrinterStatusPojo> getSN_Start_TS_StatusFromDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		PrinterStatusPojo printerStatusPojoObj =  null; 
		List<PrinterStatusPojo>  listPrinterStatusPojo = new ArrayList<PrinterStatusPojo>();

		/*
		 SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status 
FROM app_bm_graphics_lf_telemetry.printer_state_result where serial_no = 'SG68D1N001' 
AND product_no = 'K4T88A' 
AND ((start_ts >= '2020-09-20 10:10:19' AND end_ts <= '2020-09-21 10:10:19') 
OR (start_ts between '2020-09-20 10:10:19' AND '2020-09-21 10:10:19')
OR (end_ts between '2020-09-20 10:10:19' AND '2020-09-21 10:10:19') )
AND (end_ts-start_ts)>'0.1' AND channel = 'oee'
		 */
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			sb.append("SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status \r\n" + 
					"FROM app_bm_graphics_lf_telemetry.printer_state_result where " );
			sb.append(" serial_no='" + serial_no + "' AND product_no='" + product_no + "' " );
			sb.append("AND ((start_ts >= '" + start_ts + "' AND end_ts <= '" + end_ts + "') " );
			sb.append("OR (start_ts between '" + start_ts + "' AND  '" + end_ts + "') " );
			sb.append("OR (end_ts between '" + start_ts + "' AND '" + end_ts + "') ) " );
			sb.append(" AND (end_ts-start_ts)>'0.1' AND channel = 'oee' ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//logger.info("sql-> \n " + sql + "\n");
			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {
				
				printerStatusPojoObj = new PrinterStatusPojo();
				printerStatusPojoObj.setSerial_no(rs.getString("serial_no"));
				printerStatusPojoObj.setStart_TS(rs.getString("start_ts"));
				printerStatusPojoObj.setStatus(rs.getString("status"));
				
				listPrinterStatusPojo.add(printerStatusPojoObj);
			}
			//test.log(Status.PASS," Values from Databse ::>>  " + listPrinterStatusPojo );
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
		logger.info("From DB, SIZE= " + listPrinterStatusPojo.size() );
		logger.info("Read from DB ===> " + listPrinterStatusPojo );
		return listPrinterStatusPojo;
	} // Method END	

	/*  
	 * ***********************************************************************************************************
	 * getting 'end_ts' value based on 'serial_no', start_ts and 'status' value  from DB
	 * 
	 */

	public List<EndTSpojo> getEnd_TsFromDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		EndTSpojo endTSpojoObj =  null; 
		List<EndTSpojo>  listPrinterStatusPojo = new ArrayList<EndTSpojo>();

		/*
		 SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status 
FROM app_bm_graphics_lf_telemetry.printer_state_result where serial_no = 'SG68D1N001' 
AND product_no = 'K4T88A' 
AND ((start_ts >= '2020-09-20 10:10:19' AND end_ts <= '2020-09-21 10:10:19') 
OR (start_ts between '2020-09-20 10:10:19' AND '2020-09-21 10:10:19')
OR (end_ts between '2020-09-20 10:10:19' AND '2020-09-21 10:10:19') )
AND (end_ts-start_ts)>'0.1' AND channel = 'oee'
		 */
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			sb.append("SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status \r\n" + 
					"FROM app_bm_graphics_lf_telemetry.printer_state_result where " );
			sb.append(" serial_no='" + serial_no + "' AND product_no='" + product_no + "' " );
			sb.append("AND ((start_ts >= '" + start_ts + "' AND end_ts <= '" + end_ts + "') " );
			sb.append("OR (start_ts between '" + start_ts + "' AND  '" + end_ts + "') " );
			sb.append("OR (end_ts between '" + start_ts + "' AND '" + end_ts + "') ) " );
			sb.append(" AND (end_ts-start_ts)>'0.1' AND channel = 'oee' ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//logger.info("sql-> \n " + sql + "\n");
			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {
				
				endTSpojoObj = new EndTSpojo();
				endTSpojoObj.setSerial_no(rs.getString("serial_no"));
				endTSpojoObj.setStart_TS(rs.getString("start_ts"));
				endTSpojoObj.setStatus(rs.getString("status"));
				endTSpojoObj.setEnd_TS(rs.getString("end_ts"));
				
				listPrinterStatusPojo.add(endTSpojoObj);
			}
			//test.log(Status.PASS," Values from Databse ::>>  " + listPrinterStatusPojo );
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
		logger.info("From DB, SIZE= " + listPrinterStatusPojo.size() );
		logger.info("Read from DB ===> " + listPrinterStatusPojo );
		return listPrinterStatusPojo;
	} // Method END	
	
	/*  
	 * ***********************************************************************************************************
	 * getting 'sub_status' value based on 'serial_no', start_ts and 'status' value  from DB
	 * 
	 */

	public List<SubStatusPojo> getSubStatusFromDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		SubStatusPojo subStatusPojoObj =  null; 
		List<SubStatusPojo>  listPrinterStatusPojo = new ArrayList<SubStatusPojo>();

		/*
		 SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status 
FROM app_bm_graphics_lf_telemetry.printer_state_result where serial_no = 'SG68D1N001' 
AND product_no = 'K4T88A' 
AND ((start_ts >= '2020-09-20 10:10:19' AND end_ts <= '2020-09-21 10:10:19') 
OR (start_ts between '2020-09-20 10:10:19' AND '2020-09-21 10:10:19')
OR (end_ts between '2020-09-20 10:10:19' AND '2020-09-21 10:10:19') )
AND (end_ts-start_ts)>'0.1' AND channel = 'oee'
		 */
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			sb.append("SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status \r\n" + 
					"FROM app_bm_graphics_lf_telemetry.printer_state_result where " );
			sb.append(" serial_no='" + serial_no + "' AND product_no='" + product_no + "' " );
			sb.append("AND ((start_ts >= '" + start_ts + "' AND end_ts <= '" + end_ts + "') " );
			sb.append("OR (start_ts between '" + start_ts + "' AND  '" + end_ts + "') " );
			sb.append("OR (end_ts between '" + start_ts + "' AND '" + end_ts + "') ) " );
			sb.append(" AND (end_ts-start_ts)>'0.1' AND channel = 'oee' ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//logger.info("sql-> \n " + sql + "\n");
			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {
				
				subStatusPojoObj = new SubStatusPojo();
				subStatusPojoObj.setSerial_no(rs.getString("serial_no"));
				subStatusPojoObj.setStart_TS(rs.getString("start_ts"));
				subStatusPojoObj.setStatus(rs.getString("status"));
				subStatusPojoObj.setSub_status(rs.getString("sub_status"));
				
				listPrinterStatusPojo.add(subStatusPojoObj);
			}
			//test.log(Status.PASS," Values from Databse ::>>  " + listPrinterStatusPojo );
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
		logger.info("From DB, SIZE= " + listPrinterStatusPojo.size() );
		logger.info("Read sub_status from DB ===> " + listPrinterStatusPojo );
		return listPrinterStatusPojo;
	} // Method END	

	/*  
	 * ***********************************************************************************************************
	 * getting 'channel' value based on 'serial_no', start_ts and 'status' value  from DB
	 * 
	 */

	public List<ChannelPojo> getChannelFromDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		ChannelPojo channelPojoObj =  null; 
		List<ChannelPojo>  listPrinterStatusPojo = new ArrayList<ChannelPojo>();

		/*
		 SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status 
FROM app_bm_graphics_lf_telemetry.printer_state_result where serial_no = 'SG68D1N001' 
AND product_no = 'K4T88A' 
AND ((start_ts >= '2020-09-20 10:10:19' AND end_ts <= '2020-09-21 10:10:19') 
OR (start_ts between '2020-09-20 10:10:19' AND '2020-09-21 10:10:19')
OR (end_ts between '2020-09-20 10:10:19' AND '2020-09-21 10:10:19') )
AND (end_ts-start_ts)>'0.1' AND channel = 'oee'
		 */
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			sb.append("SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status \r\n" + 
					"FROM app_bm_graphics_lf_telemetry.printer_state_result where " );
			sb.append(" serial_no='" + serial_no + "' AND product_no='" + product_no + "' " );
			sb.append("AND ((start_ts >= '" + start_ts + "' AND end_ts <= '" + end_ts + "') " );
			sb.append("OR (start_ts between '" + start_ts + "' AND  '" + end_ts + "') " );
			sb.append("OR (end_ts between '" + start_ts + "' AND '" + end_ts + "') ) " );
			sb.append(" AND (end_ts-start_ts)>'0.1' AND channel = 'oee' ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//logger.info("sql-> \n " + sql + "\n");
			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {
				
				channelPojoObj = new ChannelPojo();
				channelPojoObj.setSerial_no(rs.getString("serial_no"));
				channelPojoObj.setStart_TS(rs.getString("start_ts"));
				channelPojoObj.setStatus(rs.getString("status"));
				channelPojoObj.setChannel(rs.getString("channel"));
				
				listPrinterStatusPojo.add(channelPojoObj);
			}
			//test.log(Status.PASS," Values from Databse ::>>  " + listPrinterStatusPojo );
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
		logger.info("From DB, SIZE= " + listPrinterStatusPojo.size() );
		logger.info("Read from DB ===> " + listPrinterStatusPojo );
		return listPrinterStatusPojo;
	} // Method END	
	
	//==============================================================================================================
	/*  
	 * ***********************************************************************************************************
	 * getting 'channel' value based on 'serial_no', start_ts and 'status' value  from DB
	 * 
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<Key, String> getChannelsFromSealsDB(String serial_no, String product_no , String start_ts, String end_ts ) 
	{		
		Map<Key, String> multiKeyMap = new LinkedHashMap<>();
		Key k123 = null;
				
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			sb.append("SELECT serial_no,product_no, start_ts, end_ts ,sub_status ,channel ,status \r\n" + 
					"FROM app_bm_graphics_lf_telemetry.printer_state_result where " );
			sb.append(" serial_no='" + serial_no + "' AND product_no='" + product_no + "' " );
			sb.append("AND ((start_ts >= '" + start_ts + "' AND end_ts <= '" + end_ts + "') " );
			sb.append("OR (start_ts between '" + start_ts + "' AND  '" + end_ts + "') " );
			sb.append("OR (end_ts between '" + start_ts + "' AND '" + end_ts + "') ) " );
			sb.append(" AND (end_ts-start_ts)>'0.1' AND channel = 'oee' ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//logger.info("sql-> \n " + sql + "\n");
			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {	
				k123 = new Key(sql, sql, sql );
		        k123.setSerial_no(rs.getString("serial_no"));
		        k123.setStart_TS(rs.getString("start_ts"));
		        k123.setStatus(rs.getString("status"));
				
			//	k123 = new Key(rs.getString("serial_no"), rs.getString("start_ts"), rs.getString("status") );				
				multiKeyMap.put(k123, rs.getString("channel") );
				
			}
			//test.log(Status.PASS," Values from Databse ::>>  " + multiKeyMap );
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
	//	logger.info("From DB, Channel count = " + multiKeyMap.size() );
	//	logger.info("Read Channel value from DB ===> " + multiKeyMap );
		
		return multiKeyMap;
	
	} // Method END	
	
	
	
	
	
	
	
	
	/*
	 * ******************************** DRIVER MAIN METHOD ******************************************
	 *  
	 */
	public static void main(String[] args) 
	{
		PostgreSqlConnectionDb ob = new PostgreSqlConnectionDb();

		//ob.getEnd_TsFromDB("MY91A1T004", "3XD61A", "2020-08-27T00:00:00Z", "2020-08-27T23:59:59Z");
		
		//ob.getEnd_TsFromDB("MY91A1T004","2020-07-01T00:00:00Z","2020-07-10 00:00:00Z");
		//ob.getEnd_TsFromDB("SG5371P001","2020-07-01T00:00:00Z","2020-07-10 00:00:00Z");
		//	ob.getEnd_TsFromDB("SG68D1N001", "K4T88A");

		//ob.getStatusAndSubStatus("MY97U1T003", "5HB06A", "2020-08-10T00:00:00Z", "2020-08-20T23:59:59Z");
		//ob.getStatusAndSubStatus("SG87D1R001", "1HA07A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z");
	//	ob.getSN_Start_TS_StatusFromDB("SG68D1N001", "K4T88A", "2020-09-20T10:10:19Z", "2020-09-21T10:10:19Z");
	//	ob.getEnd_TsFromDB("SG68D1N001", "K4T88A", "2020-09-20T10:10:19Z", "2020-09-21T10:10:19Z");
		ob.getChannelsFromSealsDB("SG68D1N001", "K4T88A", "2020-09-20T10:10:19Z", "2020-09-21T10:10:19Z");
	}
}
