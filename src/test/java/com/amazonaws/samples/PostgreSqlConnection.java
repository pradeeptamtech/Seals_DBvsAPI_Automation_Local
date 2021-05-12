package com.amazonaws.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.Properties;
//import java.sql.Connection;

//import org.apache.http.annotation.Contract;

import com.amazonaws.entity.MaintenanceTaskDetails;
import com.amazonaws.entity.Obligation;
import com.amazonaws.entity.PrinterDetails;
import com.amazonaws.entity.PrinterStateResult;
import com.amazonaws.entity.prntr_d;
import com.aventstack.extentreports.Status;

import hp.Seals.APITest.Contract;
import hp.Seals.APITest.Embeded;
import hp.Seals.APITest.Warranty;

public class PostgreSqlConnection {

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
			//con = DriverManager.getConnection(dbURL, username, userPassword);
			//System.out.println("Successfully connected to database test");
			
			// Open a connection and define properties.
			System.out.println("Connecting to database......and....System Tables.....");
			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", userPassword);
			con = DriverManager.getConnection(dbURL, props);
			if (con != null) { 
				System.out.println("Successfully connected to database test"); 
			}
			// Try a simple query.
			System.out.println("\nListing system tables..........");
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

	/*
	 * get error_code value from DB
	 */
	public List getErrorCode_DB( String serialNo,String prod_nr ,String startDate,String endDate )
	{
		PrinterDetails printerDetail = new PrinterDetails();
		StringBuilder sb = new StringBuilder();
		List listObj = new ArrayList();
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
				//printerDetail = new PrinterDetails();
				printerDetail.setCust_cd(rs.getString("cust_cd"));
				listObj.add(printerDetail.getCust_cd());
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
	 * ***********************************************************************************************************
	 * total_events Count for getmaintenanceEvents API 'total_Events' from DB
	 * 
	 */
	public PrinterDetails getTotal_Events_Count(String serial_no, String product_no, String startDate,String endDate)
	{
		PrinterDetails printDetail = new PrinterDetails();
		StringBuilder sb = new StringBuilder();

		// Write query.
		try {
			//System.out.println("Listing system tables...");
			
			sb.append("SELECT SUM(records) FROM ( SELECT COUNT(severity) AS records FROM ( ");
			sb.append(" select * from (select distinct serial_no, trunc(insert_ts) insert_ts, maintenance_type,friendly_name, round(current_progress,0) current_progress,\r\n" + 
					"             last_maint_date, estimated_maint_date, severity,part_number,maintenance_category,\r\n" + 
					"             row_number() over (partition by maintenance_type,trunc(insert_ts) order by insert_ts desc ) as group_idx\r\n" + 
					"             from app_bm_graphics_lf_telemetry.maintenance_estimation_result");
			sb.append(" WHERE serial_no= '" + serial_no + "'");
			sb.append(" AND product_no= '" + product_no + "'");
			sb.append(" AND insert_ts >= '" + startDate + "'" + " AND insert_ts <= '" + endDate + "'");
			sb.append(" AND severity in ('Halt','Critical') ) ");
			sb.append(" WHERE group_idx = '1' ");
			sb.append(" ORDER BY insert_ts ) ");
			sb.append(" GROUP BY severity  HAVING COUNT(*) >=1 ) ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);

			// Get the data from the result set.			
			while (rs.next()) {
				printDetail.setSum(rs.getInt("sum"));
				System.out.println("total_events Count from DB ===> " + printDetail.getSum() );
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

	/*
	 *  getting printer entitled and verify existsIndicator
	 */
	public prntr_d getPrinterEntitled(String srl_nr, String prod_nr) 
	{
		prntr_d  printerEntitled = new prntr_d();
		StringBuilder sb = new StringBuilder();
		try {
			//System.out.println("Listing system tables...");
			con = getConnection();
			stmt = con.createStatement();
			//String sql = "SELECT is_entitled from app_bm_graphics_lf_telemetry.prntr_d WHERE srl_nr = 'SG68D1N001' AND  prod_nr = 'K4T88A'";
			sb.append("SELECT is_entitled from app_bm_graphics_lf_telemetry.prntr_d WHERE srl_nr =");
			sb.append("'" + srl_nr + "'" + "AND prod_nr=" + "'" + prod_nr +"'" );

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {
				printerEntitled.setIs_entitled(rs.getString("is_entitled"));
				//System.out.println("\n existsIndicator======> " + printerEntitled.getIs_entitled());
				//printerEntitled.getIs_entitled();
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
		//System.out.println("\n existsIndicator======> " + printerEntitled.getIs_entitled());
		return printerEntitled ;
	}

	/*
	 *  getting Maintenanace Task object count #########################################################
	 * 
	 */
	public  MaintenanceTaskDetails getCountProgressPercentage(String serial_no,String product_no,String startDate,String endDate) 
	{

		MaintenanceTaskDetails maintance = new MaintenanceTaskDetails();
		StringBuilder sb = new StringBuilder();

		//		SELECT SUM(records) FROM	( 
		//				SELECT COUNT(maintenance_percentage) AS records FROM app_bm_graphics_lf_telemetry.maintenance_pwxl_dtl 
		//				where serial_no='MY91A1T004' AND product_no='3XD61A' AND 
		//				insert_ts between '2020-08-27 00:00:00' and '2020-08-27 23:59:59'
		//				GROUP BY maintenance_percentage HAVING COUNT(*) >=1 
		//				);

		// Write query.
		try {
			//System.out.println("Listing system tables...");
			sb.append("SELECT SUM(records) FROM ( SELECT COUNT(maintenance_percentage) AS records FROM app_bm_graphics_lf_telemetry.maintenance_pwxl_dtl where serial_no=");
			sb.append("'" + serial_no + "'" + "and product_no=" + "'" +product_no +"'" );
			sb.append("and insert_ts BETWEEN ");
			sb.append("'" + startDate +"'" +" AND " + "'" + endDate + "'");
			sb.append(" GROUP BY maintenance_percentage  HAVING COUNT(*) > 0 ) ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);

			// Get the data from the result set.			
			while (rs.next()) {
				maintance.setSum(rs.getInt("sum"));
				//System.out.println("progress_percentage Count from DB ===> " + maintance.getSum() );
				maintance.getSum();

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
		return maintance;

	} // Method END	



	/*  
	 * ***********************************************************************************************************
	 * getting 'status' and 'sub_Status' value  from DB
	 * 
	 */

	public List<PrinterStateResult> getStatusAndSubStatus(String serial_no, String product_no , String start_ts,String end_ts ) 
	{		
		PrinterStateResult printerStateResult =  null; // new PrinterStateResult();
		StringBuilder sb = new StringBuilder();
		List<PrinterStateResult>  listPrinterStateResult = new ArrayList<PrinterStateResult>();

		//SELECT product_no,serial_no ,start_ts,end_ts ,sub_status, status FROM  printer_state_result where serial_no='SG92K11001' AND start_ts BETWEEN '2020-07-01 00:00:00Z' AND '2020-07-05 23:59:59Z'; 

		/*
		 SELECT start_ts,end_ts ,sub_status ,channel ,status 
	FROM app_bm_graphics_lf_telemetry.printer_state_result where serial_no = 'SG87D1R001' AND product_no = '1HA07A' 
	AND start_ts >= '2020-06-06 00:00:00' AND end_ts <= '2020-06-10 23:59:59' AND (end_ts-start_ts)>'0.1'
		 */
		// Write query.
		try {

			con = getConnection();
			stmt = con.createStatement();
			//String sql = "SELECT start_ts,end_ts ,sub_status, status FROM  app_bm_graphics_lf_telemetry.printer_state_result where serial_no='SG92K11001'AND product_no='4DC17A' AND  START_TS >='2020-07-01 00:00:00Z' AND END_TS <= '2020-07-05 23:59:59' order by START_TS DESC ";
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );
			sb.append("SELECT start_ts, end_ts ,sub_status, status FROM  app_bm_graphics_lf_telemetry.printer_state_result where serial_no=");
			sb.append("'" + serial_no + "'" + " AND product_no=" + "'" +product_no +"'" );
			sb.append(" AND start_ts >= ' " + start_ts + "' AND end_ts <= '" + end_ts + "'");
			sb.append(" AND (end_ts-start_ts)>'0.1'");

			System.out.println("sql->"+sb.toString());
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			
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
			System.out.println("Read from DB ===> " + listPrinterStateResult );

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
		return listPrinterStateResult;


	} // Method END	

	/*
	 *  getting printer  getOfferCode from _DB
	 */
	public List  getOfferCode_DB(String serial_number, String product_id) 
	{
		Obligation  obligation_offerCodeObj = new Obligation();
		StringBuilder sb = new StringBuilder();
		List listObj = new ArrayList();
		try {
			//System.out.println("Listing system tables...");
			con = getConnection();
			stmt = con.createStatement();
			//String sql = "SELECT offer_code FROM app_bm_graphics_lf_telemetry.product_entitlement WHERE serial_number = 'SG88B1S001' AND product_id = 'K0Q46A'";
			sb.append("SELECT offer_code FROM app_bm_graphics_lf_telemetry.product_entitlement WHERE serial_number=");
			sb.append("'" + serial_number + "'" + " AND product_id=" + "'" + product_id +"'" );

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {
				obligation_offerCodeObj.setOffer_code(rs.getString("offer_code"));
				listObj.add(obligation_offerCodeObj.getOffer_code());
				//System.out.println("\n offer_Code ======> " + obligation_offerCodeObj.getOffer_code());
				//obligation_offerCodeObj.getOffer_code();
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
		//System.out.println("\nFrom DB: List of offer_Code are => " + listObj);
		return listObj;
		//return   obligation_offerCodeObj;
	}
	/*
	 *  getting printer  offerCode for Contract type  from DB
	 */
	public List  getOfferCode_forContract_FromDB(String serial_number, String product_id) 
	{
		Obligation  obligation_Obj = new Obligation();
		StringBuilder sb = new StringBuilder();
		List listObj = new ArrayList();
		try {
			//System.out.println("Listing system tables...");
			con = getConnection();
			stmt = con.createStatement();
			//String sql = "SELECT offer_code FROM app_bm_graphics_lf_telemetry.product_entitlement WHERE serial_number = 'SG68D1N001' AND product_id = 'K4T88A' AND obligation_type = 'C'";
			sb.append("SELECT offer_code FROM app_bm_graphics_lf_telemetry.product_entitlement WHERE serial_number=");
			sb.append("'" + serial_number + "'" + " AND product_id=" + "'" + product_id + "'"  + " AND obligation_type = 'C' ORDER BY status ASC ");

			//System.out.println("sql->" + sb.toString());
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {
				obligation_Obj.setOffer_code(rs.getString("offer_code"));
				listObj.add(obligation_Obj.getOffer_code());
				//System.out.println("\n offer_Code ======> " + obligation_Obj.getOffer_code());
				//obligation_Obj.getOffer_code();
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
		//System.out.println("\nFrom DB: List of offer_Code are => " + listObj);
		return listObj;
		//return   obligation_offerCodeObj;
	}
	// getting warranty type for getObligations API 
	public List  getOfferCode_forWarranty_FromDB(String serial_number, String product_id ) 
	{
		Obligation  obligation_Obj = new Obligation();
		StringBuilder sb = new StringBuilder();
		List listObj = new ArrayList();
		try {
			//System.out.println("Listing system tables...");
			con = getConnection();
			stmt = con.createStatement();
			//String sql = "SELECT distinct(obligation_type), offer_code FROM app_bm_graphics_lf_telemetry.product_entitlement WHERE serial_number = 'SG68D1N001' AND product_id = 'K4T88A' AND obligation_type = 'W'" ;

			sb.append("SELECT offer_code FROM app_bm_graphics_lf_telemetry.product_entitlement WHERE serial_number=");
			sb.append("'" + serial_number + "'" + " AND product_id=" + "'" + product_id + "'"  + " AND obligation_type = 'W' ");

			//System.out.println("sql->"+sb.toString());
			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n <======= Query ====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			while (rs.next()) {
				obligation_Obj.setOffer_code(rs.getString("offer_code") );
				listObj.add(obligation_Obj.getOffer_code() );
				//System.out.println("\n offer_Code ======> " + obligation_Obj.getOffer_code());
				//obligation_Obj.getOffer_code();
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
		//System.out.println("\nFrom DB: List of offer_Code are => " + listObj);
		return listObj;
		//return   obligation_offerCodeObj;
	}


	// getting contract type object from getObligations API
	public List  getObligation_forContract_FromDB(String serial_number, String product_id) 
	{
		StringBuilder sb = new StringBuilder();
		//List listObj = new ArrayList();
		Embeded emb = new Embeded();
		try {
			//System.out.println("Listing system tables...");
			con = getConnection();
			stmt = con.createStatement();
			//String sql = "SELECT distinct overall_contract_start_date , overall_contract_end_date , start_Date, end_Date, active_contract_entitlement , status ,offer_code, package_code, offer_description FROM app_bm_graphics_lf_telemetry.product_entitlement WHERE serial_number = 'SG68D1N001' AND product_id = 'K4T88A' AND obligation_type = 'C' ORDER BY status ASC";

			sb.append("SELECT overall_contract_start_date , overall_contract_end_date , start_Date, end_Date, active_contract_entitlement ,\r\n" + 
					"						 status ,offer_code, package_code, offer_description \r\n" + 
					"						 FROM app_bm_graphics_lf_telemetry.product_entitlement \r\n" + 
					"						 WHERE serial_number =");
			sb.append("'" + serial_number + "'" + " AND product_id=" + "'" + product_id + "'"  + " AND obligation_type = 'C' ORDER BY status ASC "); //ORDER BY status ASC

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			Contract contract = null;

			while (rs.next()) {
				contract = new Contract();

				contract.setOverallContractStartDate(rs.getString("overall_contract_start_date"));
				contract.setOverallContractEndDate(rs.getString("overall_contract_end_date"));
				contract.setStartDate(rs.getString("start_date"));
				contract.setEndDate(rs.getString("end_date"));

				if(rs.getString("status").equals("X") || rs.getString("status").equals("C") || 
						rs.getString("status").equals("R") || rs.getString("status").equals("F"))
				{
					contract.setActive("FALSE");
				} else {
					contract.setActive(rs.getString("active_contract_entitlement"));
				}

				//contract.setActive(rs.getString("active_contract_entitlement"));

				contract.setStatus(rs.getString("status"));
				contract.setOfferCode(rs.getString("offer_code"));
				contract.setPackageCode(rs.getString("package_code"));
				contract.setOfferDescription(rs.getString("offer_description"));

				emb.getContractList().add(contract);

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
		System.out.println("\nFrom DB: List of objects are => " + emb.getContractList().toString() );
		return emb.getContractList();

	}

	// getting warranty type object from getObligations API
	public List  getObligation_forWarranty_FromDB(String serial_number, String product_id) 
	{
		StringBuilder sb = new StringBuilder();
		//List listObj = new ArrayList();
		Embeded emb = new Embeded();
		try {
			//System.out.println("Listing system tables...");
			con = getConnection();
			stmt = con.createStatement();
			//String sql = "SELECT distinct overall_contract_start_date , overall_contract_end_date , start_Date, end_Date, active_contract_entitlement , status ,offer_code, package_code, offer_description FROM app_bm_graphics_lf_telemetry.product_entitlement WHERE serial_number = 'SG68D1N001' AND product_id = 'K4T88A' AND obligation_type = 'C' ORDER BY status ASC";

			sb.append("SELECT distinct overall_warranty_start_date , overall_warranty_end_date ,  start_date,\r\n" + 
					" end_date,active_warranty_entitlement , status , warranty_determination_description , factory_warranty_term_code ,\r\n" + 
					"factory_warranty_start_date , factory_warranty_end_date ,offer_code ,offer_description , sales_order_number , covwindow , response_commitment \r\n" + 
					"FROM app_bm_graphics_lf_telemetry.product_entitlement\r\n" + 
					"WHERE serial_number =");
			sb.append("'" + serial_number + "'" + " AND product_id=" + "'" + product_id + "'"  + " AND obligation_type = 'W' ");

			con = getConnection();
			stmt = con.createStatement();
			String sql = sb.toString();
			//System.out.println("\n<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			
			Warranty warrantyDbObj = null;

			while (rs.next()) {
				warrantyDbObj = new Warranty();

				warrantyDbObj.setOverallWarrantyStartDate(rs.getString("overall_warranty_start_date"));
				warrantyDbObj.setOverallWarrantyEndDate(rs.getString("overall_warranty_end_date"));
				warrantyDbObj.setStartDate(rs.getString("start_date"));
				warrantyDbObj.setEndDate(rs.getString("end_date"));

				if(rs.getString("status").equals("X") || rs.getString("status").equals("C") || 
						rs.getString("status").equals("R") || rs.getString("status").equals("F"))
				{
					warrantyDbObj.setActive("FALSE");
				} else {
					warrantyDbObj.setActive(rs.getString("active_warranty_entitlement"));
				}

				//warrantyDbObj.setActive(rs.getString("active_warranty_entitlement"));

				warrantyDbObj.setStatus(rs.getString("status"));
				warrantyDbObj.setWarrantyDeterminationDescription(rs.getString("warranty_determination_description"));
				warrantyDbObj.setFactoryWarrantyTermCode(rs.getString("factory_warranty_term_code"));						
				warrantyDbObj.setFactoryWarrantyStartDate(rs.getString("factory_warranty_start_date"));
				warrantyDbObj.setFactoryWarrantyEndDate(rs.getString("factory_warranty_end_date"));
				warrantyDbObj.setOfferCode(rs.getString("offer_code"));
				warrantyDbObj.setOfferDescription(rs.getString("offer_description"));
				warrantyDbObj.setSalesOrderNumber(rs.getString("sales_order_number"));
				warrantyDbObj.setCovWindow(rs.getString("covwindow"));
				warrantyDbObj.setResponseCommitment(rs.getString("response_commitment"));

				emb.getWarrantyList().add(warrantyDbObj);

				//System.out.println("\n Contract values list==> " + emb.getContractList() );
				//emb.getContractList();
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
		//System.out.println("\nFrom DB: List of objects are => " + emb.getWarrantyList());
		return emb.getWarrantyList();

	}



	/*
	 * ******************************** DRIVER MAIN METHOD ******************************************
	 * Driver Main Method   
	 */
	public static void main(String[] args) {
		PostgreSqlConnection ob = new PostgreSqlConnection();
		ob.getConnection();
		
		//ob.getCountProgressPercentage("MY91A1T004", "3XD61A", "2020-08-27T00:00:00Z", "2020-08-27T23:59:59Z");
		//ob.getStatusAndSubStatus();
		//ob.getPrintResult();

		//ob.getNoOfErrorCount("MY91A1T004","2020-07-01T00:00:00Z","2020-07-10 00:00:00Z");
		//ob.getNoOfErrorCount("SG5371P001","2020-07-01T00:00:00Z","2020-07-10 00:00:00Z");
		//	ob.getPrinterEntitled("SG68D1N001", "K4T88A");

		//System.out.println( ob.getOfferCode_forWarranty_FromDB("SG68D1N001", "K4T88A"));

		//ob.getOfferCode_forContract_FromDB("SG83N1S001", "K0Q45A");  //("SG84J1S001", "K0Q46A");  

	////	System.out.println( ob.getPrinterEntitled("SG88B1S001", "K0Q46A"));


		//		System.out.println( ob.getObligation_forContract_FromDB("SG68D1N001", "K4T88A"));
		//		System.out.println( ob.getObligation_forContract_FromDB("MY7488Q00N", "2RQ20A"));
		//		System.out.println(ob.getObligation_forContract_FromDB("SG8191Q002", "1HA06A"));

		//System.out.println("offerCode-> "+ ob.getObligation_forContract_FromDB("SG68D1N001", "K4T88A"));
	
	
	}

}
