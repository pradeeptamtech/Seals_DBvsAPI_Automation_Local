package utils;

import java.io.File;
import java.io.IOException;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;

import com.aventstack.extentreports.reporter.configuration.Theme;


public class ExtentReportListener implements ITestListener {

	protected static ExtentHtmlReporter htmlReports;
	protected static ExtentReports extent;
	protected static ExtentTest test;
	//protected static ExtentTest parentTest;
	//protected static ExtentTest childTest;
	

	//String fileName = System.getProperty("user.dir") + "\\test-output\\Report\\test\\HtmlTestResults.html";

	private static String resultPath = getResultPath();

	String reportLocation = "test-output/Report/" + resultPath + "/";

	public static String getResultPath() 
	{
		resultPath = "test"; 
		if(!new File(resultPath).isDirectory()) {
			new File(resultPath);
		}
		return resultPath;
	}

	public ExtentReportListener() 
	{
		//htmlReports = new ExtentHtmlReporter(fileName);

		htmlReports = new ExtentHtmlReporter(reportLocation + "ExtentReport.html");
		extent = new ExtentReports();		
		extent.attachReporter(htmlReports);

		htmlReports.config().setReportName("Comparision with DB Seals API Automation Report ");
		htmlReports.config().setTheme(Theme.DARK);           //(Theme.STANDARD);  // DARK
		htmlReports.config().setTestViewChartLocation(ChartLocation.TOP);
		htmlReports.config().setChartVisibilityOnOpen(true);
		htmlReports.config().setDocumentTitle("Compare API With DB Automation Results");
		
		extent.setSystemInfo("User Name","pradeepta.panigrahi@hp.com");
		extent.setSystemInfo("Java Version","1.8.0_252");
		extent.setSystemInfo("OS","Linux");
		
/*		// Remove the skip test "div" from Extent Report		
				htmlReports.config().setJS("var index = 0; \r\n" + 
						"var listdiv_remove = document.getElementsByClassName(\"test displayed  skip\");\r\n" + 
						"while (index < listdiv_remove.length) { \r\n" + 
						"	vars_li = listdiv_remove[index];\r\n" + 
						"	var spans = vars_li.getElementsByTagName(\"span\");\r\n" + 
						"	if(spans[2].innerHTML == \"skip\"){\r\n" + 
						"		     vars_li.style.display = \"none\";	\r\n" + 
						"	}\r\n" + 
						"	index++; \r\n" + 
						"}"
						+ " var ul = document.getElementsByClassName(\"doughnut-legend right\")[0];\r\n" + 
						"var listItems = ul.getElementsByTagName('li');\r\n" + 
						"   //console.log (\"Length= \" + listItems.length);\r\n" + 
						"for (var i = 0; i <= listItems.length - 1; i++) { \r\n" + 
						"     if(listItems[i].innerText == \"Skip\"){\r\n" + 
						"		listItems[i].style.display = \"none\";\r\n" + 
						"	 }\r\n" + 
						"} "
						+ "var myobj = document.getElementById(\"charts-row\");\r\n" + 
						"myobj.remove();");
	*/	

	
	} // End constructor


	public void onTestStart(ITestResult result) 
	{
		test = extent.createTest(result.getMethod().getMethodName());
		
		//test.log(Status.INFO, result.getMethod().getMethodName());
		//System.out.println(result.getTestClass().getTestName());
		//System.out.println("\n<===== Test Method Name =====> " + result.getMethod().getMethodName() + "\n");	  
	}

	public void onTestSuccess(ITestResult result)
	{
		//test.log(Status.PASS, "Test is Passed");
		test.log(Status.PASS, MarkupHelper.createLabel(result.getName() + "  is  PASSED ", ExtentColor.GREEN));
	}
	public void onTestFailure(ITestResult result)
	{
		//test.log(Status.FAIL, "Test is Failed");
		test.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " is FAILED ", ExtentColor.RED));
        test.fail(result.getThrowable());
	}
	public void onTestSkipped(ITestResult result)
	{
		extent.removeTest(test);  //  UnSelected   Test  skip
		
//		test.log(Status.SKIP, "Test is Skipped");
//		test.log(Status.SKIP, MarkupHelper.createLabel(result.getName()+" SKIPPED ", ExtentColor.ORANGE));
//        test.skip(result.getThrowable());
	}
	
	public void onTestFailedButWithinSuccessPercentage(ITestResult result)
	{
		
		test.log(Status.FAIL,"*** Test failed but within percentage % " + result.getMethod().getMethodName());
	}


	public void onStart(ITestContext context)
	{
		//		htmlReports = new ExtentHtmlReporter(reportLocation + "ExtentReport.html");
		//		extent = new ExtentReports();
		//		extent.attachReporter(htmlReports);

	//	test = extent.createTest( context.toString() );		
	}

	public void onFinish(ITestContext context)
	{
		//reports.endTest(test);	
		extent.flush();
	}
	
	
}








