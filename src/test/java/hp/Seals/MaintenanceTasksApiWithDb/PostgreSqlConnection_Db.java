package hp.Seals.MaintenanceTasksApiWithDb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//import com.amazonaws.services.databasemigrationservice.model.Connection;

public class PostgreSqlConnection_Db{

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

	//========================================================================================================
	// ************************ Getting 'estimated_date_trigger' value from DB ***************************************
	public List<List_maintenancesPojo>  getEstimatedDateTrigger_MaintenancesFromDB(String serial_no, String product_no, String date) throws Throwable
	{
		MaintenancePojo maintenanceList = new MaintenancePojo();
		List_maintenancesPojo  maintenancesObj = null;

		//List listObj = new ArrayList();
		String date1 = startDateChange(date);
		String eDate = dateChange(date);
		StringBuilder sb = new StringBuilder();
		try {
			con = getConnection();
			stmt = con.createStatement();

			sb.append("SELECT * from  (\r\n" + 
					"      Select \r\n" + 
					"	distinct serial_no, product_no, estimated_maint_date AS estimated_date_trigger,   last_maint_date AS last_maintenance_date,\r\n" + 
					"	maintenance_category AS user_replaceable , round(current_progress,0) progress_Percentage, maintenance_type AS id,\r\n" + 
					"	trunc(insert_ts)  date,\r\n" + 
					"	trunc(insert_ts) insert_ts  ,  friendly_name AS name ,  severity AS status,\r\n" + 
					"		 row_number() over (partition by maintenance_type, trunc(insert_ts) ORDER BY insert_ts desc ) AS group_idx\r\n" + 
					"             FROM app_bm_graphics_lf_telemetry.maintenance_estimation_result\r\n" + 
					"      WHERE serial_no = ");

			sb.append("'" + serial_no + "'" + " AND product_no=" + "'" + product_no + "'" );
			sb.append( " and insert_ts >= '" + date1 + "' AND insert_ts <= '" + eDate + "'");
			sb.append("  )  WHERE group_idx=1   ORDER BY insert_ts ");

			String sql = sb.toString();
			//System.out.println("<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			

			while (rs.next()) {
				maintenancesObj = new List_maintenancesPojo();

				maintenancesObj.setId(rs.getString("id"));
				maintenancesObj.setDate(rs.getString("date"));						
				maintenancesObj.setEstimated_date_trigger(changeTimeStamp(rs.getString("estimated_date_trigger")));

				maintenanceList.getMaintenancePojoList().add(maintenancesObj);

			}
			//System.out.println("\n MaintenanceTasks values list==> " + maintenanceList.getMaintenancePojoList() );

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
		//	System.out.println("\nFrom DB: List of objects are => " + maintenanceList.getMaintenancePojoList() );

		return maintenanceList.getMaintenancePojoList();
	}

	// ************************ Getting 'last_maintenance_date' value from DB ***************************************
	public List<List_maintenancesPojo>  getLast_maintenance_date_MaintenancesFromDB(String serial_no, String product_no, String date) throws Throwable
	{
		MaintenancePojo maintenanceList = new MaintenancePojo();
		List_maintenancesPojo  maintenancesObj = null;

		//List listObj = new ArrayList();
		String date1 = startDateChange(date);
		String eDate = dateChange(date);
		StringBuilder sb = new StringBuilder();
		try {
			con = getConnection();
			stmt = con.createStatement();

			sb.append("SELECT * from  (\r\n" + 
					"      Select \r\n" + 
					"	distinct serial_no, product_no, estimated_maint_date AS estimated_date_trigger,   last_maint_date AS last_maintenance_date,\r\n" + 
					"	maintenance_category AS user_replaceable , round(current_progress,0) progress_Percentage, maintenance_type AS id,\r\n" + 
					"	trunc(insert_ts)  date,\r\n" + 
					"	trunc(insert_ts) insert_ts  ,  friendly_name AS name ,  severity AS status,\r\n" + 
					"		 row_number() over (partition by maintenance_type, trunc(insert_ts) ORDER BY insert_ts desc ) AS group_idx\r\n" + 
					"             FROM app_bm_graphics_lf_telemetry.maintenance_estimation_result\r\n" + 
					"      WHERE serial_no = ");

			sb.append("'" + serial_no + "'" + " AND product_no=" + "'" + product_no + "'" );
			sb.append( " and insert_ts >= '" + date1 + "' AND insert_ts <= '" + eDate + "'");
			sb.append("  )  WHERE group_idx=1   ORDER BY insert_ts ");

			String sql = sb.toString();
			//System.out.println("<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			

			while (rs.next()) {
				maintenancesObj = new List_maintenancesPojo();

				maintenancesObj.setId(rs.getString("id"));
				maintenancesObj.setDate(rs.getString("date"));						
				maintenancesObj.setLast_maintenance_date(changeTimeStamp(rs.getString("last_maintenance_date")));

				maintenanceList.getMaintenancePojoList().add(maintenancesObj);

			}
			//System.out.println("\n MaintenanceTasks values list==> " + maintenanceList.getMaintenancePojoList() );

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
		//	System.out.println("\nFrom DB: List of objects are => " + maintenanceList.getMaintenancePojoList() );

		return maintenanceList.getMaintenancePojoList();
	}


	//*************************** Getting 'status' values from DB	********************************************************
	public List<List_maintenancesPojo>  getStatus_MaintenancesFromDB(String serial_no, String product_no, String date) throws Throwable
	{
		MaintenancePojo maintenanceList = new MaintenancePojo();
		List_maintenancesPojo  maintenancesObj = null;

		//List listObj = new ArrayList();
		String date1 = startDateChange(date);
		String eDate = dateChange(date);
		StringBuilder sb = new StringBuilder();
		try {
			con = getConnection();
			stmt = con.createStatement();

			sb.append("SELECT * from  (\r\n" + 
					"      Select \r\n" + 
					"	distinct serial_no, product_no, estimated_maint_date AS estimated_date_trigger,   last_maint_date AS last_maintenance_date,\r\n" + 
					"	maintenance_category AS user_replaceable , round(current_progress,0) progress_Percentage, maintenance_type AS id,\r\n" + 
					"	trunc(insert_ts)  date,\r\n" + 
					"	trunc(insert_ts) insert_ts  ,  friendly_name AS name ,  severity AS status,\r\n" + 
					"		 row_number() over (partition by maintenance_type, trunc(insert_ts) ORDER BY insert_ts desc ) AS group_idx\r\n" + 
					"             FROM app_bm_graphics_lf_telemetry.maintenance_estimation_result\r\n" + 
					"      WHERE serial_no = ");

			sb.append("'" + serial_no + "'" + " AND product_no=" + "'" + product_no + "'" );
			sb.append( " and insert_ts >= '" + date1 + "' AND insert_ts <= '" + eDate + "'");
			sb.append("  )  WHERE group_idx=1   ORDER BY insert_ts ");

			String sql = sb.toString();
			//System.out.println("<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			

			while (rs.next()) {
				maintenancesObj = new List_maintenancesPojo();

				maintenancesObj.setId(rs.getString("id"));
				maintenancesObj.setDate(rs.getString("date"));						
				maintenancesObj.setStatus(rs.getString("status"));

				maintenanceList.getMaintenancePojoList().add(maintenancesObj);

			}
			//System.out.println("\n MaintenanceTasks values list==> " + maintenanceList.getMaintenancePojoList() );

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
		System.out.println("\nFrom DB: List of objects are => " + maintenanceList.getMaintenancePojoList() );
		return maintenanceList.getMaintenancePojoList();

	}

	/*
	 * *************************************************************************************************************************
	 */
	// ************************ Getting All fields values of GetMaintenanceTasks API from DB ********************************
	public List<List_maintenancesPojo>  getMaintenancesFromDB(String serial_no, String product_no, String date, String choice) throws Throwable
	{
		MaintenancePojo maintenancePojoList = new MaintenancePojo();
		List_maintenancesPojo  maintenancesObj = null;

		//List listObj = new ArrayList();
		String date1 = startDateChange(date);
		String eDate = dateChange(date);
		StringBuilder sb = new StringBuilder();
		try {
			con = getConnection();
			stmt = con.createStatement();

			sb.append("SELECT * from  (\r\n" + 
					"      Select \r\n" + 
					"	distinct serial_no, product_no, estimated_maint_date AS estimated_date_trigger,   last_maint_date AS last_maintenance_date,\r\n" + 
					"	maintenance_category AS user_replaceable , round(current_progress,0) progress_Percentage, maintenance_type AS id,\r\n" + 
					"	trunc(insert_ts)  date,\r\n" + 
					"	trunc(insert_ts) insert_ts  ,  friendly_name AS name ,  severity AS status,\r\n" + 
					"		 row_number() over (partition by maintenance_type, trunc(insert_ts) ORDER BY insert_ts desc ) AS group_idx\r\n" + 
					"             FROM app_bm_graphics_lf_telemetry.maintenance_estimation_result\r\n" + 
					"      WHERE serial_no = ");

			sb.append("'" + serial_no + "'" + " AND product_no=" + "'" + product_no + "'" );
			sb.append( " and insert_ts >= '" + date1 + "' AND insert_ts <= '" + eDate + "'");
			sb.append("  )  WHERE group_idx=1   ORDER BY insert_ts ");

			String sql = sb.toString();
			System.out.println("<=======Query====> \n" + sql + "\n" );

			rs = stmt.executeQuery(sql);
			// Get the data from the result set.			

			while (rs.next()) {
				maintenancesObj = new List_maintenancesPojo();			
				maintenancesObj.setId(rs.getString("id"));
				maintenancesObj.setDate(rs.getString("date"));

				switch(choice) {

				case "estimated_date_trigger": 
					maintenancesObj.setEstimated_date_trigger(changeTimeStamp(rs.getString("estimated_date_trigger")));
					break;

				case "last_maintenance_date":	
					maintenancesObj.setLast_maintenance_date(changeTimeStamp(rs.getString("last_maintenance_date")));					
					break;

				case "user_replaceable":	
					if(rs.getString("user_replaceable").equals("user") ) {
						maintenancesObj.setUser_replaceable("true");
					} else if(rs.getString("user_replaceable").equals("service") ) {
						maintenancesObj.setUser_replaceable("false");
						//maintenancesObj.setUser_replaceable(rs.getString("user_replaceable"));
					}
					break;

				case "progress_Percentage":	
					maintenancesObj.setProgress_Percentage(String.valueOf(rs.getInt("progress_Percentage")));
					break;

				case "name":	maintenancesObj.setName(rs.getString("name"));  break;
				case "status": 	maintenancesObj.setStatus(rs.getString("status"));  break;
				
			} // End switch	

			//	System.out.println("Output With null  \n"+ maintenancesObj);
				
     		//	System.out.println("Remove null \n"+ maintenancesObj);

				maintenancePojoList.getMaintenancePojoList().add(maintenancesObj);


			} // End while

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
		System.out.println("\nFrom DB: List of objects are => " + maintenancePojoList.getMaintenancePojoList() );	

        return maintenancePojoList.getMaintenancePojoList();
	}	

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	


	/*
	 * **************** Private Methods ************************************************************************************
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
		String s = "";
		if(timeStamp != null) {
			String[] arrOfStr = timeStamp.split("[.]+");   
			for (String a : arrOfStr) {
				//System.out.println(a);
			}
			s = arrOfStr[0];
			// System.out.println(s);
		} else {
			s = "null";
		}
		return s;
	}

	private static String dateChange(String sDate ) throws Throwable {

		String[] dateParts = sDate.split("T");
		//		for (String a : dateParts) 
		//           		System.out.println("DATE: " + a.toString());		
		String s1 = dateParts[0];
		//System.out.println("Change start DATE: " + s1 );

		String endDate = s1 + " 23:59:59"; 
		//System.out.println("\n EndDate ----->  " + endDate);		

		return endDate;
	}

	private static String startDateChange(String date )  {

		String[] dateParts = date.split("T");
		//		for (String a : dateParts) 
		//           		System.out.println("DATE: " + a.toString());		
		String s1 = dateParts[0];
		//System.out.println("Change start DATE: " + s1 );

		String startDate = s1 + " 00:00:00"; 
		//System.out.println("\n startDate ----->  " + startDate);		

		return startDate;
	}


	/*
	 * ******************************** DRIVER MAIN METHOD ******************************************
	 *    
	 */
	public static void main(String[] args) throws Throwable {

		PostgreSqlConnection_Db ob = new PostgreSqlConnection_Db();

		//System.out.println(changeTimeStamp("2020-08-07 00:00:00.000000"));

		//	System.out.println(ob.countStatus_PrintheadDetailsDB("MY79E1401B", "2ET72A", "2020-07-05T00:00:00Z", "2020-07-15T23:59:59Z" ));
		//	ob.getListMaintenancesFromDB("SG5371P001", "CZ056A", "2020-02-23T10:59:59Z");
		//	ob.getListMaintenancesFromDB("MY91A1T004", "3XD61A", "2020-08-27T23:59:59Z");
		//	ob.getListMaintenancesFromDB("SG87D1R001", "1HA07A", "2020-08-02T00:00:00Z");
		//ob.getStatusMaintenanacesFromDB("SG87D1R001", "1HA07A", "2020-08-27T00:00:00Z");
		//ob.getStatusMaintenanacesFromDB("SG87D1R001", "1HA07A", "2020-08-27T23:59:59Z");
		//ob.getStatus_MaintenancesFromDB("SG87D1R001", "1HA07A", "2020-08-27T23:59:59Z");
		
		//ob.getMaintenancesFromDB("SG87D1R001", "1HA07A", "2020-08-27T23:59:59Z", "status");
		
		//ob.getMaintenancesFromDB("SG68D1N001", "K4T88A", "2020-06-06T00:00:00Z" , "2020-06-20T23:59:59Z", "estimated_date_trigger") ;
		//ob.getMaintenancesFromDB("SG87D1R001","1HA07A","2020-06-06T00:00:00Z", "2020-06-10T23:59:59Z" , "last_maintenance_date");
		//ob.getMaintenancesFromDB("SG92K11001", "4DC17A", "2020-07-01T00:00:00Z", "2020-07-05T23:59:59Z","progress_Percentage" );
		//ob.getMaintenancesFromDB("MY7488Q00N", "2RQ20A", "2020-06-06T00:00:00Z", "2020-06-06T23:59:59Z","user_replaceable" );

		ob.getMaintenancesFromDB("SG87D1R001", "1HA07A", "2020-08-27T23:59:59Z", "status");
		ob.getMaintenancesFromDB("SG7B11N002", "K4T88A", "2020-09-01T00:00:00Z", "estimated_date_trigger");

	} // End Main method

} // End Class
