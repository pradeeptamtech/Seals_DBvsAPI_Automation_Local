package hp.Seals.PrintheadDetailsApiDbTest;

import java.sql.*;
import java.util.*;
import java.util.List;
import com.amazonaws.entity.*;
import printheadDetails.warrantyStatus.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//import com.amazonaws.services.databasemigrationservice.model.Connection;

public class PostgreSqlConnection_Db {

	static final String dbURL = "jdbc:postgresql://sealsprod.cx4d1xjawowi.us-west-2.redshift.amazonaws.com:5439/dev";
	static final String username = "pradeepta.panigrahi@hp.com";
	static final String userPassword = "5$ONqldNV100P";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;
	//private String query_custCd = "Select top 1 srl_nr ,prod_nr, cust_cd From app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr = 'SG92K11001' AND prod_nr = '4DC17A'";
	//private String querry_errorCount = "SELECT SUM(records) FROM ( SELECT COUNT(cust_cd) AS records FROM app_bm_graphics_lf_telemetry.prntr_log_dtl where srl_nr ='SG92K11001' and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59' GROUP BY cust_cd  HAVING COUNT(*) > 0 ) ";

	public Connection getConnection() {

		try {
			con = DriverManager.getConnection(dbURL, username, userPassword);
			
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

	/*  ***********************print head Details API***************************************************************************** 
	 * 
	 */
	
	public List<PrintheadDetailsResultDb> getStatus_PrintheadDetailsDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		PrintheadDetailsResultDb printheadDeatlsResult =  null; // new PrintheadDetailsResult();
		StringBuilder sb = new StringBuilder();
		List<PrintheadDetailsResultDb>  listPrintheadDetailsResult = new ArrayList<PrintheadDetailsResultDb>();

		// Write query.
		try {
	
			sb.append("SELECT  \"PH_SERIAL_NO\", \"PEN\", \"STATUS\", \"START_TS\",\"END_TS\"\r\n" + 
					"       FROM (SELECT  \"PH_SERIAL_NO\",  \"PEN\",  \"STATUS\", \"START_TS\", \"END_TS\", \r\n" + 
					"                    ROW_NUMBER() over (partition by PRINTER_PRODUCT_NO,PRINTER_SERIAL_NO,ph_serial_no, pen order by start_ts desc) as rank\r\n" + 
					"FROM app_bm_graphics_lf_telemetry.\"PRINTHEAD_WARRANTY_STATUS\"\r\n" + 
					"WHERE PRINTER_PRODUCT_NO=");
			sb.append("'" + product_no + "'" + " AND PRINTER_SERIAL_NO=" + "'" + serial_no + "' " );
			sb.append(" AND PH_SERIAL_NO IS NOT NULL  and PH_SERIAL_NO!=''\r\n" + 
					"  AND( (");
			sb.append(" START_TS between '" + start_ts + "' and '" + end_ts + "')" );
			sb.append(" OR (END_TS between '" + start_ts + "' and '"  + end_ts + "') ");
			sb.append(" OR (START_TS < '" + start_ts + "' and end_ts > '" + end_ts + "') )");
			sb.append(" )  where rank=1 ");
			//sb.append(" ORDER BY  PH_SERIAL_NO  DESC ");
			
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			System.out.println("Sql Query-> \n" + sql + "\n");
			
			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) 
			{	
				printheadDeatlsResult = new PrintheadDetailsResultDb();
				printheadDeatlsResult.setStatus(rs.getString("status"));
				printheadDeatlsResult.setPh_serial_no(rs.getString("ph_serial_no"));

				listPrintheadDetailsResult.add(printheadDeatlsResult);
			}
			System.out.println("\n'status' value from Seals_DB  ===> " + listPrintheadDetailsResult  + "\n");

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
		return listPrintheadDetailsResult;

	} // Method END	

	/*  *****************getting Start_Timestamp from DB******print head Details API***************************************************************************** 
	 * 
	 */

	public List<PhDetailsApiStartTsResultDb> getStartTSfromPrintheadDetailsDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		PhDetailsApiStartTsResultDb startTsPhDetailsResult =  null; // new PhDetailsApiStartTsResultDb();
		StringBuilder sb = new StringBuilder();
		List<PhDetailsApiStartTsResultDb>  listPhStartTsResult = new ArrayList<PhDetailsApiStartTsResultDb>();

		// Write query.
		try {

			sb.append("SELECT  \"PH_SERIAL_NO\", \"PEN\", \"STATUS\", \"START_TS\",\"END_TS\"\r\n" + 
					"       FROM (SELECT  \"PH_SERIAL_NO\",  \"PEN\",  \"STATUS\", \"START_TS\", \"END_TS\", \r\n" + 
					"                    ROW_NUMBER() over (partition by PRINTER_PRODUCT_NO,PRINTER_SERIAL_NO,ph_serial_no, pen order by start_ts desc) as rank\r\n" + 
					"FROM app_bm_graphics_lf_telemetry.\"PRINTHEAD_WARRANTY_STATUS\"\r\n" + 
					"WHERE PRINTER_PRODUCT_NO=");
			sb.append("'" + product_no + "'" + " AND PRINTER_SERIAL_NO=" + "'" + serial_no + "' " );
			sb.append(" AND PH_SERIAL_NO IS NOT NULL  and PH_SERIAL_NO!=''\r\n" + 
					"  AND( (");
			sb.append(" START_TS between '" + start_ts + "' and '" + end_ts + "')" );
			sb.append(" OR (END_TS between '" + start_ts + "' and '"  + end_ts + "') ");
			sb.append(" OR (START_TS < '" + start_ts + "' and end_ts > '" + end_ts + "') )");
			sb.append(" )  where rank=1 ");
			//sb.append(" ORDER BY  PH_SERIAL_NO  DESC ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("Sql Query-> \n" + sql + "\n");

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) 
			{	
				startTsPhDetailsResult = new PhDetailsApiStartTsResultDb();
				startTsPhDetailsResult.setPh_serial_no(rs.getString("ph_serial_no"));
				startTsPhDetailsResult.setStartTimestamp(changeTimeStamp(rs.getString("start_ts")));	  // start_ts is the DB column name

				listPhStartTsResult.add(startTsPhDetailsResult);
			}
			//System.out.println("\n'status' value from Seals_DB  ===> " + listPrintheadDetailsResult  + "\n");

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
		return listPhStartTsResult;

	} // Method END	

	/*  *****************getting Start_Timestamp from DB******print head Details API***************************************************************************** 
	 * 
	 */

	public List<PhDetailsApiEndTsResultDb> getEndTSfromPrintheadDetailsDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		PhDetailsApiEndTsResultDb endTsPhDetailsResult =  null; // new PhDetailsApiEndTsResultDb();
		StringBuilder sb = new StringBuilder();
		List<PhDetailsApiEndTsResultDb>  listPhEndTsResult = new ArrayList<PhDetailsApiEndTsResultDb>();

		// Write query.
		try {

			sb.append("SELECT  \"PH_SERIAL_NO\", \"PEN\", \"STATUS\", \"START_TS\",\"END_TS\"\r\n" + 
					"       FROM (SELECT  \"PH_SERIAL_NO\",  \"PEN\",  \"STATUS\", \"START_TS\", \"END_TS\", \r\n" + 
					"                    ROW_NUMBER() over (partition by PRINTER_PRODUCT_NO,PRINTER_SERIAL_NO,ph_serial_no, pen order by start_ts desc) as rank\r\n" + 
					"FROM app_bm_graphics_lf_telemetry.\"PRINTHEAD_WARRANTY_STATUS\"\r\n" + 
					"WHERE PRINTER_PRODUCT_NO=");
			sb.append("'" + product_no + "'" + " AND PRINTER_SERIAL_NO=" + "'" + serial_no + "' " );
			sb.append(" AND PH_SERIAL_NO IS NOT NULL  and PH_SERIAL_NO!=''\r\n" + 
					"  AND( (");
			sb.append(" START_TS between '" + start_ts + "' and '" + end_ts + "')" );
			sb.append(" OR (END_TS between '" + start_ts + "' and '"  + end_ts + "') ");
			sb.append(" OR (START_TS < '" + start_ts + "' and end_ts > '" + end_ts + "') )");
			sb.append(" )  where rank=1 ");
			//sb.append(" ORDER BY  PH_SERIAL_NO  DESC ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("Sql Query-> \n" + sql + "\n");

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) 
			{	
				endTsPhDetailsResult = new PhDetailsApiEndTsResultDb();
				endTsPhDetailsResult.setPh_serial_no(rs.getString("ph_serial_no"));
				endTsPhDetailsResult.setEnd_ts(changeTimeStamp(rs.getString("end_ts")));
				//listPhEndTsResult.setEnd_ts(changeStartTS(rs.getString("end_ts")));  // end_ts is the DB column name

				listPhEndTsResult.add(endTsPhDetailsResult);
			}
			System.out.println("\n'end_ts' value from Seals_DB  ===> " + listPhEndTsResult  + "\n");

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
		return listPhEndTsResult;

	} // Method END	

	// Count the number of status objects

	//@SuppressWarnings("null")
	public PrinterheadDetailsObjCount countStatus_PrintheadDetailsDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		PrinterheadDetailsObjCount objCount = new PrinterheadDetailsObjCount();
		StringBuilder sb = new StringBuilder();

		// Write query.
		try {		
			sb.append("SELECT SUM(records) FROM (  SELECT COUNT(STATUS) AS records FROM (   ");
			sb.append("SELECT  \"PH_SERIAL_NO\", \"PEN\", \"STATUS\", \"START_TS\",\"END_TS\"\r\n" + 
					"       FROM (SELECT  \"PH_SERIAL_NO\",  \"PEN\",  \"STATUS\", \"START_TS\", \"END_TS\", \r\n" + 
					"                    ROW_NUMBER() over (partition by PRINTER_PRODUCT_NO,PRINTER_SERIAL_NO,ph_serial_no, pen order by start_ts desc) as rank\r\n" + 
					"FROM app_bm_graphics_lf_telemetry.\"PRINTHEAD_WARRANTY_STATUS\"\r\n" + 
					"WHERE PRINTER_PRODUCT_NO=");
			sb.append("'" + product_no + "'" + " AND PRINTER_SERIAL_NO=" + "'" + serial_no + "' " );
			sb.append(" AND PH_SERIAL_NO IS NOT NULL  and PH_SERIAL_NO!=''\r\n" + 
					"  AND( (");
			sb.append(" START_TS between '" + start_ts + "' and '" + end_ts + "')" );
			sb.append(" OR (END_TS between '" + start_ts + "' and '"  + end_ts + "') ");
			sb.append(" OR (START_TS < '" + start_ts + "' and end_ts > '" + end_ts + "') )");
			sb.append(" )  where rank=1 ");
			sb.append(" ) GROUP BY STATUS  HAVING COUNT(*) > 0 ) ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("Sql Query-> \n" + sql + "\n");

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {
				objCount.setSum(rs.getInt("sum"));
				//System.out.println("status Count from DB ===> " + objCount.getSum() );
				objCount.getSum();

			}
			//System.out.println("'status' value  count from Seals_DB  ===> " + objCount.getSum() );

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

		return objCount;

	} // Method END	


	/*  *****************getting ph_serial_no from DB******print head Details API*************************** 
	 * 
	 */
	public List getPhSrNoFromPrintheadDetailsDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		List<String>  listPhSrNoResult = new ArrayList<>();
		
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			sb.append("SELECT distinct(pen), PH_SERIAL_NO,  EVENT_TYPE  FROM (   SELECT  PRINTER_PRODUCT_NO, PRINTER_SERIAL_NO, PH_SERIAL_NO, PEN,\r\n" + 
					"      case when (EVENT_TYPE = 'REPLACE' AND EVENT_END_TS IS NOT NULL) then EVENT_END_TS else EVENT_TS end AS EVENT_TS, EVENT_TYPE, LINE_NO\r\n" + 
					"   FROM  app_bm_graphics_lf_telemetry.PRINTHEAD_EVENT \r\n" + 
					"   WHERE PRINTER_PRODUCT_NO=");
			
			sb.append("'" + product_no + "'" + " AND PRINTER_SERIAL_NO=" + "'" + serial_no + "' " );
			sb.append(" AND  EVENT_TS >= '" + start_ts + "' AND EVENT_TS<= '" + end_ts + "' ");
			sb.append(" UNION ALL\r\n" + 
					"   SELECT  PRINTER_PRODUCT_NO,  PRINTER_SERIAL_NO, PH_SERIAL_NO, PEN, EVENT_END_TS AS EVENT_TS, EVENT_TYPE, LINE_NO \r\n" + 
					"     FROM  app_bm_graphics_lf_telemetry.PRINTHEAD_MISSING_REPLACE_EVENT\r\n" + 
					"        WHERE PRINTER_PRODUCT_NO= " );
			sb.append("'" + product_no + "'" + " AND PRINTER_SERIAL_NO=" + "'" + serial_no + "' " );
			sb.append(" AND  EVENT_TS >= '" + start_ts + "' AND EVENT_TS<= '" + end_ts + "' ");
			sb.append(" AND EVENT_TYPE='REPLACE' ) T  ORDER BY PEN, EVENT_TS ASC  ");
			
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			System.out.println("Sql Query-> \n" + sql + "\n");

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) 
			{	
				if(rs.getString("ph_serial_no") != null ) {
				      listPhSrNoResult.add(rs.getString("ph_serial_no"));
				}
				
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
		//System.out.println("\n'ph_serial_no' values from Seals_DB  ===> " + listPhSrNoResult  + "\n");
		return listPhSrNoResult;

	} // Method END	
	
	/*  
	 * ***************** getting ink_Used from DB ****** print head Details API **************************************  
	 */
	public List<InkUsedValuesResultsPojo> getInk_UsedFromPrintheadDetailsDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		List<InkUsedValuesResultsPojo>  listResult = new ArrayList<InkUsedValuesResultsPojo>();
		InkUsedValuesResultsPojo obj = null;
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			sb.append("select a.PRINTER_SERIAL_NO, a.ph_serial_no,a.pen,(max_ink_used-min_ink_used) AS INK_CONSUMED  ,(max_used_time-min_used_time) AS USAGE_TIME from\r\n" + 
					"                 ( select printer_product_no,printer_serial_no,ph_serial_no,pen,min(event_ts)as min_event_ts,max(event_ts)as max_event_ts\r\n" + 
					"                    from app_bm_graphics_lf_telemetry.PRINTHEAD_METADATA\r\n" + 
					"                    WHERE \r\n" ); 
			
			sb.append("PRINTER_PRODUCT_NO='" + product_no + "' AND PRINTER_SERIAL_NO='" + serial_no + "' ");
			sb.append(" and event_ts between '" +  start_ts + "' and '" + end_ts + "' ");
			sb.append(" and event_ts between '2020-07-01 00:00:00' and '2020-07-05 23:59:59'\r\n" + 
					"                    group by printer_product_no,printer_serial_no,ph_serial_no,pen\r\n" + 
					"                  ) a left join\r\n" + 
					"                    ( select printer_product_no,printer_serial_no,ph_serial_no,pen,event_ts,ink_consumed as min_ink_used,usage_time as min_used_time\r\n" + 
					"                    from app_bm_graphics_lf_telemetry.PRINTHEAD_METADATA WHERE \r\n" + 
					"                     PRINTER_PRODUCT_NO='" + product_no + "' " );
			
			sb.append("AND PRINTER_SERIAL_NO='" + serial_no + "' " );
			sb.append(" and event_ts between '" + start_ts + "' and '" + end_ts + "' " );
			sb.append(" ) b on a.printer_product_no=b.printer_product_no and a.printer_serial_no=b.printer_serial_no\r\n" + 
					"                    and a.ph_serial_no=b.ph_serial_no and a.pen=b.pen and a.min_event_ts=b.event_ts\r\n" + 
					"                  left join\r\n" + 
					"                    ( select printer_product_no,printer_serial_no,ph_serial_no,pen,event_ts,ink_consumed as max_ink_used,usage_time as max_used_time\r\n" + 
					"                    from app_bm_graphics_lf_telemetry.PRINTHEAD_METADATA WHERE \r\n" + 
					"                     PRINTER_PRODUCT_NO='" + product_no + "' " );
			
			sb.append("AND PRINTER_SERIAL_NO='" + serial_no + "'" );
			sb.append(" and event_ts between '" + start_ts + "' and '" + end_ts + "'" );
			sb.append(" ) c on a.printer_product_no=c.printer_product_no and a.printer_serial_no=c.printer_serial_no\r\n" + 
					"                    and a.ph_serial_no=c.ph_serial_no and a.pen=c.pen and a.max_event_ts=c.event_ts" );
					
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			System.out.println("Sql Query-> \n" + sql + "\n");

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) 
			{	
				obj = new InkUsedValuesResultsPojo();
				if(rs.getString("ph_serial_no") != null ) {
					obj.setPh_serial_no(rs.getString("ph_serial_no"));
				}
				obj.setInk_used(Float.parseFloat((rs.getString("ink_used"))));
				
				listResult.add(obj);
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
		System.out.println("\n'ph_serial_no' and 'ink_Used' values from Seals_DB  ===> " + listResult  + "\n");
		
		return listResult;

	} // Method END	
	
	/*  
	 * ***************** getting time_Used from DB ****** print head Details API ************************************  
	 */
	public List<TimeUsedValuesPojo> getTime_UsedFromPrintheadDetailsDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		List<TimeUsedValuesPojo>  listResult = new ArrayList<TimeUsedValuesPojo>();
		TimeUsedValuesPojo obj = null;
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			sb.append("select a.PRINTER_SERIAL_NO, a.ph_serial_no,a.pen,(max_ink_used-min_ink_used) AS INK_CONSUMED  ,(max_used_time-min_used_time) AS USAGE_TIME from\r\n" + 
					"                 ( select printer_product_no,printer_serial_no,ph_serial_no,pen,min(event_ts)as min_event_ts,max(event_ts)as max_event_ts\r\n" + 
					"                    from app_bm_graphics_lf_telemetry.PRINTHEAD_METADATA\r\n" + 
					"                    WHERE \r\n" ); 
			
			sb.append("PRINTER_PRODUCT_NO='" + product_no + "' AND PRINTER_SERIAL_NO='" + serial_no + "' ");
			sb.append(" and event_ts between '" +  start_ts + "' and '" + end_ts + "' ");
			sb.append(" and event_ts between '2020-07-01 00:00:00' and '2020-07-05 23:59:59'\r\n" + 
					"                    group by printer_product_no,printer_serial_no,ph_serial_no,pen\r\n" + 
					"                  ) a left join\r\n" + 
					"                    ( select printer_product_no,printer_serial_no,ph_serial_no,pen,event_ts,ink_consumed as min_ink_used,usage_time as min_used_time\r\n" + 
					"                    from app_bm_graphics_lf_telemetry.PRINTHEAD_METADATA WHERE \r\n" + 
					"                     PRINTER_PRODUCT_NO='" + product_no + "' " );
			
			sb.append("AND PRINTER_SERIAL_NO='" + serial_no + "' " );
			sb.append(" and event_ts between '" + start_ts + "' and '" + end_ts + "' " );
			sb.append(" ) b on a.printer_product_no=b.printer_product_no and a.printer_serial_no=b.printer_serial_no\r\n" + 
					"                    and a.ph_serial_no=b.ph_serial_no and a.pen=b.pen and a.min_event_ts=b.event_ts\r\n" + 
					"                  left join\r\n" + 
					"                    ( select printer_product_no,printer_serial_no,ph_serial_no,pen,event_ts,ink_consumed as max_ink_used,usage_time as max_used_time\r\n" + 
					"                    from app_bm_graphics_lf_telemetry.PRINTHEAD_METADATA WHERE \r\n" + 
					"                     PRINTER_PRODUCT_NO='" + product_no + "' " );
			
			sb.append("AND PRINTER_SERIAL_NO='" + serial_no + "'" );
			sb.append(" and event_ts between '" + start_ts + "' and '" + end_ts + "'" );
			sb.append(" ) c on a.printer_product_no=c.printer_product_no and a.printer_serial_no=c.printer_serial_no\r\n" + 
					"                    and a.ph_serial_no=c.ph_serial_no and a.pen=c.pen and a.max_event_ts=c.event_ts" );
					
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			System.out.println("Sql Query-> \n" + sql + "\n");

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) 
			{	
				obj = new TimeUsedValuesPojo();
				if(rs.getString("ph_serial_no") != null ) {
					obj.setPh_serial_no(rs.getString("ph_serial_no"));
				}
				obj.setTime_used(Integer.parseInt((rs.getString("time_used"))));
				
				listResult.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				rs.close();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("\n'ph_serial_no' and 'time_Used' values from Seals_DB  ===> " + listResult  + "\n");
		
		return listResult;

	} // Method END	
	
	/*  
	 * ***************** getting 'color' value from DB ****** print head Details API ************************************  
	 */
	public List<ColorValuesResultPojo> getColorFromPrintheadDetailsDB(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		List<ColorValuesResultPojo>  listResult = new ArrayList<ColorValuesResultPojo>();
		ColorValuesResultPojo obj = null;
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			sb.append("select a.PRINTER_SERIAL_NO, a.ph_serial_no,a.pen,(max_ink_used-min_ink_used) AS INK_CONSUMED  ,(max_used_time-min_used_time) AS USAGE_TIME from\r\n" + 
					"                 ( select printer_product_no,printer_serial_no,ph_serial_no,pen,min(event_ts)as min_event_ts,max(event_ts)as max_event_ts\r\n" + 
					"                    from app_bm_graphics_lf_telemetry.PRINTHEAD_METADATA\r\n" + 
					"                    WHERE \r\n" ); 
			
			sb.append("PRINTER_PRODUCT_NO='" + product_no + "' AND PRINTER_SERIAL_NO='" + serial_no + "' ");
			sb.append(" and event_ts between '" +  start_ts + "' and '" + end_ts + "' ");
			sb.append(" and event_ts between '2020-07-01 00:00:00' and '2020-07-05 23:59:59'\r\n" + 
					"                    group by printer_product_no,printer_serial_no,ph_serial_no,pen\r\n" + 
					"                  ) a left join\r\n" + 
					"                    ( select printer_product_no,printer_serial_no,ph_serial_no,pen,event_ts,ink_consumed as min_ink_used,usage_time as min_used_time\r\n" + 
					"                    from app_bm_graphics_lf_telemetry.PRINTHEAD_METADATA WHERE \r\n" + 
					"                     PRINTER_PRODUCT_NO='" + product_no + "' " );
			
			sb.append("AND PRINTER_SERIAL_NO='" + serial_no + "' " );
			sb.append(" and event_ts between '" + start_ts + "' and '" + end_ts + "' " );
			sb.append(" ) b on a.printer_product_no=b.printer_product_no and a.printer_serial_no=b.printer_serial_no\r\n" + 
					"                    and a.ph_serial_no=b.ph_serial_no and a.pen=b.pen and a.min_event_ts=b.event_ts\r\n" + 
					"                  left join\r\n" + 
					"                    ( select printer_product_no,printer_serial_no,ph_serial_no,pen,event_ts,ink_consumed as max_ink_used,usage_time as max_used_time\r\n" + 
					"                    from app_bm_graphics_lf_telemetry.PRINTHEAD_METADATA WHERE \r\n" + 
					"                     PRINTER_PRODUCT_NO='" + product_no + "' " );
			
			sb.append("AND PRINTER_SERIAL_NO='" + serial_no + "'" );
			sb.append(" and event_ts between '" + start_ts + "' and '" + end_ts + "'" );
			sb.append(" ) c on a.printer_product_no=c.printer_product_no and a.printer_serial_no=c.printer_serial_no\r\n" + 
					"                    and a.ph_serial_no=c.ph_serial_no and a.pen=c.pen and a.max_event_ts=c.event_ts" );
					
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			System.out.println("Sql Query-> \n" + sql + "\n");

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) 
			{	
				obj = new ColorValuesResultPojo();
				if(rs.getString("ph_serial_no") != null ) {
					obj.setPh_serial_no(rs.getString("ph_serial_no"));
				}
				obj.setColor(rs.getString("time_used"));
				
				listResult.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				rs.close();
				stmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("\n'ph_serial_no' and 'color' values from Seals_DB  ===> " + listResult  + "\n");
		
		return listResult;

	} // Method END	

/*
 *  ***************  Private Methods ************************************************************************	
 */
	// Convert yyyy-MM-dd HH:mm:ss.SSSSSS  to yyyy-MM-dd HH:mm:ss
	private String changeTimeStamp_2(String timeStamp) throws ParseException  
	{
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS"); // Existing Pattern

		Date currentdate = simpleDateFormat.parse(timeStamp); // Returns Date Format,

		SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // New Pattern

		System.out.println(simpleDateFormat1.format(currentdate));

		return simpleDateFormat1.format(currentdate);

	}

	private static String changeTimeStamp(String timeStamp)   // Remove values after decimal point
	{		
		String[] arrOfStr = timeStamp.split("[.]+");   
        for (String a : arrOfStr) {
              //System.out.println(a);
        }
        String s = arrOfStr[0];
       // System.out.println(s);
		return s;
	}



	/*
	 * ******************************** DRIVER MAIN METHOD ******************************************
	 *    
	 */
	public static void main(String[] args)  {

		PostgreSqlConnection_Db ob = new PostgreSqlConnection_Db();

		//ob.getStatus_PrintheadDetailsDB("SG68D1N001", "K4T88A", "2020-06-06T00:00:00Z" , "2020-06-20T23:59:59Z") ;
		//ob.getStatus_PrintheadDetailsDB("SG68D1N001", "K4T88A", "2020-06-06T00:00:00Z" , "2020-06-20T23:59:59Z") ;
		//ob.getStatus_PrintheadDetailsDB("SG87D1R001","1HA07A","2020-06-06T00:00:00Z", "2020-06-10T23:59:59Z" );
		//ob.getStatus_PrintheadDetailsDB("SG92K11001", "4DC17A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z" );

		//ob.getStatus_PrintheadDetailsDB("MY7488Q00N", "2RQ20A", "2020-06-06T00:00:00Z", "2020-06-06T23:59:59Z" );

	//	System.out.println(ob.countStatus_PrintheadDetailsDB("MY79E1401B", "2ET72A", "2020-07-05T00:00:00Z", "2020-07-15T23:59:59Z" ));

	//	ob.getStartTSfromPrintheadDetailsDB("SG92K11001", "4DC17A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z" );
		
		//ob.getEndTSfromPrintheadDetailsDB("SG92K11001", "4DC17A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z" );		
		//ob.getStatus_PrintheadDetailsDB("SG92K11001", "4DC17A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z" );
	
	//	ob.getColorFromPrintheadDetailsDB("SG92K11001", "4DC17A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z" );
		
		ob.getPhSrNoFromPrintheadDetailsDB("SG92K11001", "4DC17A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z" );
		
	} // End Main method
	
} // End Class
