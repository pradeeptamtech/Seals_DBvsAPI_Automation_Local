package apiConfig;

import java.net.URI;
import java.net.URISyntaxException;

public class APIPath {	

	public static final class  apiPath {

		// GET functionality
		//public static final String GET_PRINT_HEAD_INFO = "/GetPrintheadInfo?SN=SG5371P001&PN=CZ056A&startDate=2020-06-20T11:48:55Z&endDate=2020-06-27T23:41:13Z";
		//https://seals-pro.hpcloud.hp.com/SEALSRestAPI

		public static final String GET_PRINT_HEAD_INFO = "/GetPrintheadInfo?"; 

		//public static final String GET_PRINT_HEAD_DETAILS = "/GetPrintheadDetails?SN=SG5371P001&PN=CZ056A&startTimestamp=2020-06-10T10:05:46Z&endTimestamp=2020-06-17T10:05:46Z";

		public static final String GET_PRINT_HEAD_DETAILS = "/GetPrintheadDetails?";

		public static final String GET_OBLIGATION = "/GetObligations?";

		public static final String GET_PrinterStatusHistory = "/GetPrinterStatusHistory?";

		//public static final String GET_MAINTENANCE_TASK = "/GetMaintenanceTasks?SN=MY89Q1T005&PN=Y0U23A&date=2019-11-26T00:00:00Z";
		public static final String GET_MAINTENANCE_TASK = "/GetMaintenanceTasks?";

		public static final String GET_MAINTENANCE_EVENTS = "/GetMaintenanceEvents?";    //SN=SG88B1S001&PN=K0Q46A&startDate=2020-06-15T00:00:00Z&endDate=2020-06-23T23:59:59Z";

		public static final String GET_ERROR_EVENTS = "/GetErrorEvents?";   //SN=SG92K11001&PN=4DC17A&startDate=2020-07-01T00:00:00Z&endDate=2020-08-02T23:59:59Z";

		public static final String GET_TIME_TO_SOLUTION = "/GetTimeToSolution?SN=SG5371P001&PN=CZ056A&startDate=2019-12-09T11:48:55Z&endDate=2019-12-16T23:41:13Z";

		public static final String GET_SOLUTION = "/GetSolution?";   //SN=SG6B71N008&PN=K0Q45A&eventCode=10000031&detectionDate=2020-06-22T12:19:06Z";

		public static final String GET_PrinterEntitled = "/IsPrinterEntitled?";

		public static final String Get_DeviceUtilization = "/GetDeviceUtilization?";



		/*
		 * Public method used for Set Api URL by reading  data from Excel sheet
		 */

		public static URI setPrintUrl(String url, String sn, String pn, String startDate, String endDate) 
		{
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			sb.append(url).append("SN=").append(sn).append("&PN=").append(pn).append("&startDate=").append(startDate).append("&endDate=").append(endDate);
			URI uri = null;
			try {
				uri = new URI(sb.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.print("=====uri=====> "+uri +"\n\n");
			return uri;
		}

		/*
		 * set URI for printer head details when  reading  data from Excel sheet
		 */
		public static URI setPrintHeadDetailsUrl(String url, String sn, String pn, String startDate, String endDate) 
		{
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			sb.append(url).append("SN=").append(sn).append("&PN=").append(pn).append("&startTimestamp=").append(startDate).append("&endTimestamp=").append(endDate);
			URI uri = null;
			try {
				uri = new URI(sb.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.print("=====uri=====> "+uri +"\n\n");
			return uri;
		}

		/*
		 * Public method for Printer get obligation Api when  reading  data from Excel sheet
		 */
		public static URI setObligationUrl(String url, String sn, String pn) 
		{
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			sb.append(url).append("serialNumber=").append(sn).append("&productID=").append(pn).append("&Mode=all");
			System.out.println("SB-> " + sb.toString());
			URI uri = null;
			try {
				uri = new URI(sb.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.print("=====uri=====> "+uri +"\n\n");
			return uri;
		}

		/*
		 * Public method for Printer Entitled get Api when  reading  data from Excel sheet
		 */
		public static URI setPrinterEntitledUrl(String url, String sn, String pn) 
		{
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			sb.append(url).append("serialNumber=").append(sn).append("&productID=").append(pn);
			URI uri = null;
			try {
				uri = new URI(sb.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.print("=====uri=====> "+ uri + "\n\n");
			return uri;
		}

		/*
		 * set URI for printer get Solution API when  reading  data from Excel sheet
		 */
		public static URI setGetSolutionUrl(String url, String sn, String pn, String eventCode,String detectionDate)
		{
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			sb.append(url).append("SN=").append(sn).append("&PN=").append(pn).append("&eventCode=").append(eventCode).append("&detectionDate=").append(detectionDate);
			URI uri = null;
			try {
				uri = new URI(sb.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.print("\n<=====uri=====> " + uri + "\n");
			//System.out.println();
			return uri;
		}

		/*
		 * set URI for printer Get Maintenance task API when  reading  data from Excel sheet
		 */
		public static URI setGetMaintenanceTaskUrl(String url, String sn, String pn, String date)
		{
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			sb.append(url).append("SN=").append(sn).append("&PN=").append(pn).append("&date=").append(date);
			URI uri = null;
			try {
				uri = new URI(sb.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.print("\n<=====uri=====> " + uri + "\n");
			//System.out.println();
			return uri;
		}

		/*
		 * set URI for printer Get DeviceUtilization task API when  reading  data from Excel sheet
		 */
		public static URI setGetDeviceUtilizationUrl(String url, String sn, String pn, String startDate, String endDate) 
		{
			// TODO Auto-generated method stub
			StringBuilder sb = new StringBuilder();
			sb.append(url).append("SN=").append(sn).append("&PN=").append(pn).append("&startDate=").append(startDate).append("&endDate=").append(endDate);
			URI uri = null;
			try {
				uri = new URI(sb.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			 
			return uri;
		}


	}


}
