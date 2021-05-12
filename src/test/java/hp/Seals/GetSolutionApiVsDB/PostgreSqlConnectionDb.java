package hp.Seals.GetSolutionApiVsDB;

import java.sql.*;
import java.util.*;
import org.apache.log4j.*;

import com.aventstack.extentreports.Status;

import hp.Seals.APITest.*;
import hp.Seals.printerStatusHistoryApiVsDb.Key;


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
	 * getting 'event_Code'  and 'insert_ts' values from DB
	 * 
	 */
	public List<GetSolutionPojo>  getEventCodeAndInsertTsFromDB( String product_no, String event_code, String event_type ) 
	{
		GetSolutionPojo solutionPojoObj = null;
		List<GetSolutionPojo> solutionDbObj = new ArrayList<GetSolutionPojo>();
		
		StringBuilder sb = new StringBuilder();	
		try {
			//System.out.println("Listing system tables...");
			con = getConnection();
			stmt = con.createStatement();

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
			System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			

			while (rs.next()) {
				solutionPojoObj = new GetSolutionPojo();

				solutionPojoObj.setEvent_code(rs.getString("event_code"));				
				solutionPojoObj.setInsert_ts(rs.getString("insert_ts"));					

				solutionDbObj.add(solutionPojoObj);
			}
			
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
		logger.info("\nFrom DB: List of objects are => " + solutionDbObj);
		return solutionDbObj;
	}


	//===========================================================================================================
	/*  
	 * ***********************************************************************************************************
	 * getting 'short_description' value based on 'event_code' and 'insert_ts' value  from DB
	 *  or
	 * getting 'severity' value based on 'event_code' and 'insert_ts' value  from DB
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<KeyGetSolution, String> getKeyValueFromSealsDB(String product_no, String event_code, String event_type , String choice ) 
	{		
		Map<KeyGetSolution, String> multiKeyMap = new LinkedHashMap<>();
		KeyGetSolution k12 = null;
				
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
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

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//logger.info("sql-> \n " + sql + "\n");
			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {	
				k12 = new KeyGetSolution(sql, sql);		        
				
				if(rs.getString("event_code").isEmpty() || rs.getString("event_code").equals(null)) {
					k12.setEvent_Code("null");
				}else {
					k12.setEvent_Code(rs.getString("event_code"));
				}	
				
				if(rs.getString("event_code").isEmpty() || rs.getString("event_code").equals(null)) {
					k12.setEvent_Code("null");
				}else {
					 k12.setUpdate_TS(rs.getString("insert_ts"));
				}
		       
		    	//k12 = new Key(rs.getString("event_code"), rs.getString("insert_ts") );
		        
		        switch(choice) {
				case "short_Description":
					if(rs.getString("short_description").isEmpty() || rs.getString("short_description").equals(null)) {
						k12.setShort_Description("null");
						multiKeyMap.put(k12, k12.getShort_Description().toString() );
						//multiKeyMap.put(k12, "\"short_Description\": \"" +  k12.getShort_Description() + "\"");
						
					}else {
						multiKeyMap.put(k12, rs.getString("short_description") );
						//multiKeyMap.put(k12, "\"short_Description\": \"" +  rs.getString("short_description") + "\"");
					}
					logger.info("\n" + rs.getString("short_description") + "\n");
					break;
					
				case "severity": //key3=severity							
					if(rs.getString("severity").isEmpty() || rs.getString("severity").equals(null)) {
						k12.setSeverity("null");
						//multiKeyMap.put(k12, k12.getSeverity().toString() );
						multiKeyMap.put(k12, "\"severity\": \"" +  k12.getSeverity() + "\"");
					}else {
					//multiKeyMap.put(k12, rs.getString("severity") );
						multiKeyMap.put(k12, "\"severity\": \"" +  rs.getString("severity") + "\"");
					}
					break;
				
		        } // end switch			
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
		logger.info("From DB, object count = " + multiKeyMap.size() );
		logger.info("Read '"+ choice + "' value from DB ===> " + multiKeyMap );
		
		return multiKeyMap;
	
	} // Method END	
	
	/*  
	 * ***********************************************************************************************************
	 * getting 'severity' value based on 'event_code' and 'insert_ts' value  from DB
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<KeyGetSolution, String> getSeverityFromSealsDB(String product_no, String event_code, String event_type ) 
	{		
		Map<KeyGetSolution, String> multiKeyMap = new LinkedHashMap<>();
		KeyGetSolution k12 = null;
				
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
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

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//logger.info("sql-> \n " + sql + "\n");
			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {	
				k12 = new KeyGetSolution(sql, sql);
		        k12.setEvent_Code(rs.getString("event_code"));
		        k12.setUpdate_TS(rs.getString("insert_ts"));
		       				
			//	k12 = new Key(rs.getString("event_code"), rs.getString("insert_ts") );				
				multiKeyMap.put(k12, rs.getString("severity") );
				
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
	//	logger.info("From DB, 'severity' count = " + multiKeyMap.size() );
		logger.info("Read 'severity' value from DB ===> " + multiKeyMap );
		
		return multiKeyMap;
	
	} // Method END	
	
	/*  
	 * ***********************************************************************************************************
	 * getting 'short_description' value based on 'event_Code'  and 'insert_ts' values from DB
	 * 
	 */
	public List<SolutionPojo>  getShort_descriptionFromDB( String product_no, String event_code, String event_type ) 
	{
		SolutionPojo solutionPojoObj = null;
		List<SolutionPojo> solutionDbObj = new ArrayList<SolutionPojo>();
		
		StringBuilder sb = new StringBuilder();	
		try {
			//System.out.println("Listing system tables...");
			con = getConnection();
			stmt = con.createStatement();

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
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			

			while (rs.next()) {
				solutionPojoObj = new SolutionPojo();

				solutionPojoObj.setEvent_code(rs.getString("event_code"));				
				solutionPojoObj.setInsert_ts(rs.getString("insert_ts"));					
				solutionPojoObj.setShort_description(rs.getString("short_description"));
				
				solutionDbObj.add(solutionPojoObj);
			}
			
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
		logger.info("\nFrom DB: 'short_description' value is => " + solutionDbObj);
		return solutionDbObj;
	}
	
	
	
	
	
	
	/*
	 * ******************************** DRIVER MAIN METHOD ******************************************
	 *  
	 */
	public static void main(String[] args) 
	{
		PostgreSqlConnectionDb ob = new PostgreSqlConnectionDb();
		
		//ob.getEventCodeAndInsertTsFromDB("K0Q45A", "10000031", "Maintenance");
		
	//	ob.getKeyValueFromSealsDB("CZ056A", "22.02.31:06", "SystemError", "short_Description");
		
//		ob.getKeyValueFromSealsDB("K0Q45A", "10000031", "Maintenance","severity");
//		ob.getKeyValueFromSealsDB("CZ056A", "22.02.31:06", "SystemError","severity");
		
		//ob.getSeverityFromSealsDB("CZ056A", "22.02.31:06", "SystemError");
	
	ob.getShort_descriptionFromDB("CZ056A", "22.02.31:06", "SystemError");
	ob.getEventCodeAndInsertTsFromDB("CZ056A", "22.02.31:06", "SystemError");
	
	}
}
