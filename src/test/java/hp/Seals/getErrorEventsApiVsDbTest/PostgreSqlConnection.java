package hp.Seals.getErrorEventsApiVsDbTest;

import java.sql.*;
import java.util.*;
import java.util.Properties;
import com.amazonaws.entity.*;
import hp.Seals.APITest.*;

public class PostgreSqlConnection {

	static final String dbURL = "jdbc:postgresql://sealsprod.cx4d1xjawowi.us-west-2.redshift.amazonaws.com:5439/dev";
	static final String username = "pradeepta.panigrahi@hp.com";
	static final String userPassword = "5$ONqldNV100P";
	Connection con = null;
	Statement stmt = null;
	ResultSet rs = null;

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

	/*  *****************************************************************************************************
	 *  getting cust_cd  from DB 
	 *  
	 *  */
	public PrinterDetails getPrintResult() {
		PrinterDetails printDetail = new PrinterDetails();
		// Try a simple query.
		try {
			//System.out.println("Listing system tables...");
			con = getConnection();
			stmt = con.createStatement();
			String sql = "Select top 1 srl_nr ,prod_nr, cust_cd From app_bm_graphics_lf_telemetry.prntr_log_dtl \r\n" + 
					"WHERE srl_nr = 'SG83N1S001' AND prod_nr = 'K0Q45A';";
			//String sql = "Select top 1 srl_nr ,prod_nr, cust_cd From app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr = 'SG6B71N008' AND prod_nr = 'K0Q45A'";
			rs = stmt.executeQuery(sql);

			// Get the data from the result set.			
			while (rs.next()) {
				printDetail.setCust_cd(rs.getString("cust_cd"));
				//System.out.println("\n event_Code======> " + printDetail.getCust_cd());
				printDetail.getCust_cd();
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
		return printDetail;
	}


	/*  
	 * ***********************************************************************************************************
	 * Count no of objects for error count cust_cd from DB
	 * 
	 */
	public PrinterDetails getNoOfErrorCount(String serialNo,String startDate,String endDate)
	{
		PrinterDetails printDetail = new PrinterDetails();
		StringBuilder sb = new StringBuilder();

		//SELECT SUM(records) FROM ( SELECT COUNT(cust_cd) AS records FROM app_bm_graphics_lf_telemetry.prntr_log_dtl where srl_nr ='SG92K11001' and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59' GROUP BY cust_cd  HAVING COUNT(*) > 0 ) ";

		// Write query.
		try {
			//System.out.println("Listing system tables...");
			sb.append("SELECT SUM(records) FROM ( SELECT COUNT(cust_cd) AS records FROM app_bm_graphics_lf_telemetry.prntr_log_dtl where srl_nr =");
			sb.append("'" + serialNo + "'");
			sb.append("and evt_ocrd_ts BETWEEN");
			sb.append(" '" + startDate +"'" +" AND " + "'" + endDate + "'");
			sb.append(" GROUP BY cust_cd  HAVING COUNT(*) > 0 ) ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);

			// Get the data from the result set.			
			while (rs.next()) {
				printDetail.setSum(rs.getInt("sum"));
				//System.out.println("Error Count from DB ===> " + printDetail.getSum() );
				printDetail.getSum();

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
		return printDetail;

	} // Method END	

	/* ***************************** getErrorEvent API **********************************************
	 * ****************** get error_code value from DB **********************************************
	 */
	public List getErrorCode_DB( String serialNo,String prod_nr ,String startDate,String endDate )
	{
		StringBuilder sb = new StringBuilder();
		List<String> listObj = new ArrayList<>();
		//SELECT cust_cd FROM app_bm_graphics_lf_telemetry.prntr_log_dtl where srl_nr ='SG92K11001' and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59';

		// Write query.
		try {
			//System.out.println("Listing system tables...");
			sb.append("SELECT cust_cd FROM app_bm_graphics_lf_telemetry.prntr_log_dtl where srl_nr =");
			sb.append("'" + serialNo + "'" + " AND prod_nr =" + "'" + prod_nr + "'");
			sb.append("and evt_ocrd_ts BETWEEN");
			sb.append(" '" + startDate +"'" +" AND " + "'" + endDate + "'");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.					
			while (rs.next()) {					

				listObj.add(rs.getString("cust_cd"));
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
		//System.out.println("Read from DB: cust_cd ===> " + listObj );
		return listObj;

	} // Method END	
	/*
	 * ***************************** get severity values from DB ********************************************
	 */
	public List<ErrorEventsSeverityPojo> getSeverityValuesFromDB( String sn,String pn ,String startDate,String endDate )
	{
		ErrorEventsSeverityPojo severityObj = null;		
		List<ErrorEventsSeverityPojo> listObj = new ArrayList<>();
		//SELECT srl_nr, prod_nr,cust_cd,svrty,evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr ='SG92K11001' and prod_nr= '4DC17A'and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59';

		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			//System.out.println("Listing system tables...");
			sb.append("SELECT srl_nr, prod_nr,cust_cd,svrty,evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr =");
			sb.append("'" + sn + "'" + " AND prod_nr =" + "'" + pn + "'");
			sb.append("and evt_ocrd_ts BETWEEN");
			sb.append(" '" + startDate +"'" + " AND " + "'" + endDate + "'");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.					
			while (rs.next()) {
				severityObj = new ErrorEventsSeverityPojo();
				severityObj.setError_Code(rs.getString("cust_cd"));
				severityObj.setSeverity(rs.getString("svrty"));
				listObj.add(severityObj);
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
		//System.out.println("Read from DB: severity ===> " + listObj );
		//System.out.println("'severity' Object count from DB:==> " + listObj.size() );
		return listObj;

	} // Method END	

	/*
	 * ***************************** get 'event_Occurred_TS' values from DB ********************************************
	 */
	public List<EventTsPojo> geEvent_Occurred_TSValuesFromDB( String sn,String pn ,String startDate,String endDate )
	{
		EventTsPojo evtTsObj = null;
		List<EventTsPojo> listObj = new ArrayList<>();
		//SELECT srl_nr, prod_nr,cust_cd,svrty,evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr ='SG92K11001' and prod_nr= '4DC17A'and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59';
		StringBuilder sb = new StringBuilder();
		// Write query.
		try {
			//System.out.println("Listing system tables...");
			sb.append("SELECT srl_nr, prod_nr,cust_cd,svrty,evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr =");
			sb.append("'" + sn + "'" + " AND prod_nr =" + "'" + pn + "'");
			sb.append("and evt_ocrd_ts BETWEEN");
			sb.append(" '" + startDate +"'" + " AND " + "'" + endDate + "'");
			
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.					
			while (rs.next()) {	
				evtTsObj = new EventTsPojo();
				evtTsObj.setCust_cd(rs.getString("cust_cd"));
				evtTsObj.setEvt_ocrd_ts(rs.getString("evt_ocrd_ts"));
				listObj.add(evtTsObj);
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
		//System.out.println("Read from DB: evt_ocrd_ts ===> " + listObj );
		return listObj;

	} // Method END	

	/* ***************************** getErrorEvent API *****************************************************
	 * ****************** get short_description value from DB **********************************************
	 */

	public List<ErrorEventsPojo> getShortDescriptionValuesFromDB( String sn,String pn ,String startDate,String endDate )
	{
		ErrorEventsPojo errorEventsPojoObj = null;
		List<ErrorEventsPojo> listObj = new ArrayList<>();
		//SELECT srl_nr, prod_nr,cust_cd,svrty,evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr ='SG92K11001' and prod_nr= '4DC17A'and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59';

		// Write query.
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("SELECT distinct(pld.cust_cd) ,pld.srl_nr, pld.prod_nr,  pld.svrty, pfm.series, es.printer_family , es.short_description , es.long_description\r\n" + 
					"FROM app_bm_graphics_lf_telemetry.prntr_log_dtl pld, app_bm_graphics_lf_telemetry.event_solution es\r\n" + 
					"INNER JOIN app_bm_graphics_lf_telemetry.printer_family_mapping pfm ON pfm.product_no =");
			sb.append("'" + pn + "'" );
			sb.append(" WHERE pld.srl_nr ='" + sn + "' and pld.prod_nr= '" + pn +"' " );
			sb.append(" AND pld.evt_ocrd_ts BETWEEN '" + startDate + "' AND '" + endDate + "' ");
			sb.append("AND es.event_code = pld.cust_cd and (es.short_description like '%' OR es.long_description like '%')");
			sb.append(" AND es.printer_family = (SELECT pf.series FROM app_bm_graphics_lf_telemetry.printer_family_mapping pf WHERE pf.product_no = ");
			sb.append("'" + pn + "' )");	

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.					
			while (rs.next()) {	
				errorEventsPojoObj = new ErrorEventsPojo();					
				errorEventsPojoObj.setCust_cd(rs.getString("cust_cd"));
				errorEventsPojoObj.setShort_description(rs.getString("short_description"));
				listObj.add(errorEventsPojoObj);
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
		System.out.println("Read from DB: short_description value ===> " + listObj );
		System.out.println("\n short_description Size ===> " + listObj.size() );
		return listObj;

	} // Method END	

	public List getShortDescriptionValueFromDB( String sn,String pn ,String startDate,String endDate )
	{
		List<String> listObj = new ArrayList<>();
		
		//SELECT srl_nr, prod_nr,cust_cd,svrty,evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr ='SG92K11001' and prod_nr= '4DC17A'and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59';

		// Write query.
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("SELECT distinct(pld.cust_cd) ,pld.srl_nr, pld.prod_nr,  pld.svrty, pfm.series, es.printer_family , es.short_description , es.long_description\r\n" + 
					"FROM app_bm_graphics_lf_telemetry.prntr_log_dtl pld, app_bm_graphics_lf_telemetry.event_solution es\r\n" + 
					"INNER JOIN app_bm_graphics_lf_telemetry.printer_family_mapping pfm ON pfm.product_no =");
			sb.append("'" + pn + "'" );
			sb.append(" WHERE pld.srl_nr ='" + sn + "' and pld.prod_nr= '" + pn +"' " );
			sb.append(" AND pld.evt_ocrd_ts BETWEEN '" + startDate + "' AND '" + endDate + "' ");
			sb.append("AND es.event_code = pld.cust_cd and (es.short_description like '%' OR es.long_description like '%')");
			sb.append(" AND es.printer_family = (SELECT pf.series FROM app_bm_graphics_lf_telemetry.printer_family_mapping pf WHERE pf.product_no = ");
			sb.append("'" + pn + "' )");	

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.					
			while (rs.next()) {	
				//listObj = new ArrayList<>();					
				//listObj.add(rs.getString("cust_cd"));
				listObj.add(rs.getString("short_description"));
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
		if(listObj.isEmpty()) {
			listObj.isEmpty();
		}else {
			return listObj;
		}
		
		System.out.println("Read from DB: short_description value ===> " + listObj );
		System.out.println("\n short_description Size ===> " + listObj.size() );
		
		return listObj;

	} // Method END	
	
/*
 * ****************** get long_description value from DB **********************************************
*/

	public List<ErrorEventsLongDesPojo> getLongDescriptionValuesFromDB( String sn,String pn ,String startDate,String endDate )
	{
		ErrorEventsLongDesPojo errorEventsPojoObj = null;
		List<ErrorEventsLongDesPojo> listObj = new ArrayList<>();
		//SELECT srl_nr, prod_nr,cust_cd,svrty,evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr ='SG92K11001' and prod_nr= '4DC17A'and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59';

		StringBuilder sb = new StringBuilder();
		try {
			sb.append("SELECT distinct(pld.cust_cd) ,pld.srl_nr, pld.prod_nr,  pld.svrty, pfm.series, es.printer_family , es.short_description , es.long_description\r\n" + 
					"FROM app_bm_graphics_lf_telemetry.prntr_log_dtl pld, app_bm_graphics_lf_telemetry.event_solution es\r\n" + 
					"INNER JOIN app_bm_graphics_lf_telemetry.printer_family_mapping pfm ON pfm.product_no =");
			sb.append("'" + pn + "'" );
			sb.append(" WHERE pld.srl_nr ='" + sn + "' and pld.prod_nr= '" + pn +"' " );
			sb.append(" AND pld.evt_ocrd_ts BETWEEN '" + startDate + "' AND '" + endDate + "' ");
			sb.append("AND es.event_code = pld.cust_cd and (es.short_description like '%' OR es.long_description like '%')");
			sb.append(" AND es.printer_family = (SELECT pf.series FROM app_bm_graphics_lf_telemetry.printer_family_mapping pf WHERE pf.product_no = ");
			sb.append("'" + pn + "' )");	
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.					
			while (rs.next()) {	
				errorEventsPojoObj = new ErrorEventsLongDesPojo();					
				//errorEventsPojoObj.setCust_cd(rs.getString("cust_cd"));
				errorEventsPojoObj.setLong_description(rs.getString("long_description"));
				listObj.add(errorEventsPojoObj);
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
		System.out.println("Read from DB: long_description value ===> " + listObj );
		System.out.println("\n long_description Size ===> " + listObj.size() );
		return listObj;

	} // Method END	

	public List getLongDescriptionValueFromDB( String sn,String pn ,String startDate,String endDate )
	{
		List<String> listObj = new ArrayList<>();
		//SELECT srl_nr, prod_nr,cust_cd,svrty,evt_ocrd_ts  FROM app_bm_graphics_lf_telemetry.prntr_log_dtl WHERE srl_nr ='SG92K11001' and prod_nr= '4DC17A'and evt_ocrd_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-10 23:59:59';

		StringBuilder sb = new StringBuilder();
		try {
			sb.append("SELECT distinct(pld.cust_cd) ,pld.srl_nr, pld.prod_nr,  pld.svrty, pfm.series, es.printer_family , es.short_description , es.long_description\r\n" + 
					"FROM app_bm_graphics_lf_telemetry.prntr_log_dtl pld, app_bm_graphics_lf_telemetry.event_solution es\r\n" + 
					"INNER JOIN app_bm_graphics_lf_telemetry.printer_family_mapping pfm ON pfm.product_no =");
			sb.append("'" + pn + "'" );
			sb.append(" WHERE pld.srl_nr ='" + sn + "' and pld.prod_nr= '" + pn +"' " );
			sb.append(" AND pld.evt_ocrd_ts BETWEEN '" + startDate + "' AND '" + endDate + "' ");
			sb.append("AND es.event_code = pld.cust_cd and (es.short_description like '%' OR es.long_description like '%')");
			sb.append(" AND es.printer_family = (SELECT pf.series FROM app_bm_graphics_lf_telemetry.printer_family_mapping pf WHERE pf.product_no = ");
			sb.append("'" + pn + "' )");	
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.					
			while (rs.next()) {	
				//listObj = new ArrayList<>();
				//listObj.add(rs.getString("cust_cd"));
				listObj.add(rs.getString("long_description"));
				
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
		
		if(listObj.isEmpty()) {
			listObj.isEmpty();
		}else {
			return listObj;
		}
		
		System.out.println("Read from DB: long_description value ===> " + listObj );
		System.out.println("\n long_description Size ===> " + listObj.size() );
		return listObj;

	} // Method END	

	
	
	/*
	 * ******************************** DRIVER MAIN METHOD ******************************************
	 * Driver Main Method   
	 */
	public static void main(String[] args) {
		PostgreSqlConnection ob = new PostgreSqlConnection();

		//ob.getNoOfErrorCount("MY91A1T004","2020-07-01T00:00:00Z","2020-07-10 00:00:00Z");
		//ob.getNoOfErrorCount("SG5371P001","2020-07-01T00:00:00Z","2020-07-10 00:00:00Z");
		//ob.getSeverityValuesFromDB("SG83N1S001", "K0Q45A");  //("SG84J1S001", "K0Q46A");  
		//		System.out.println( ob.getSeverityValuesFromDB("SG68D1N001", "K4T88A"));
		//		System.out.println( ob.getSeverityValuesFromDB("MY7488Q00N", "2RQ20A"));
		//		System.out.println(ob.getSeverityValuesFromDB("SG8191Q002", "1HA06A"));

		//ob.getSeverityValuesFromDB("SG92K11001","4DC17A","2020-07-01T00:00:00Z","2020-07-12T23:59:59Z");
		//ob.geEvent_Occurred_TSValuesFromDB("SG92K11001","4DC17A","2020-07-01T00:00:00Z","2020-07-12T23:59:59Z");
	
		ob.getShortDescriptionValueFromDB("SG0181N001", "K4T88A", "2020-11-15T23:59:59Z", "2020-11-25T23:59:59Z");
		//ob.getLongDescriptionValueFromDB("SG0181N001", "K4T88A", "2020-11-15T23:59:59Z", "2020-11-25T23:59:59Z");
	
		ob.getShortDescriptionValueFromDB("SG0A112001", "3R745A", "2021-01-01T00:00:00Z", "2021-01-05T00:00:00Z");
		//ob.getLongDescriptionValueFromDB("SG0A112001", "3R745A", "2021-01-01T00:00:00Z", "2021-01-05T00:00:00Z");
	
	}
}
