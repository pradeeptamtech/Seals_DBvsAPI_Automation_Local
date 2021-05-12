package baseTest;

import java.io.IOException;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.*;
import org.testng.annotations.*;
import org.testng.annotations.Listeners;

//import com.relevantcodes.extentreports.LogStatus;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.RestAssured;
import utils.ExtentReportListener;
import utils.FileandEnv;

@Listeners(ExtentReportListener.class)
public class BaseTest extends ExtentReportListener {
	
	final static Logger logger = LogManager.getLogger(BaseTest.class);
	
	
    @BeforeClass
	public void baseTest() throws IOException {
    	
		logger.info("Before Class method.................");
		//test.log(Status.INFO,"*****Started Test case Execution **********");
				
		//test.log(Status.INFO, "***Base Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() );
				
		logger.info("****Test Methodname=> " + new Throwable().getStackTrace()[0].getMethodName() );

		RestAssured.baseURI = FileandEnv.endAndFile().get("ServerUrl");
		//logger.info("baseURI-> " + RestAssured.baseURI);
		
	//	extent.removeTest(test);
	}
	
	
	@AfterClass
	public void TearDown() {
		
		logger.info("After Class method...Finished test cases execution........");
		//test.log(Status.INFO,"**************Finished test cases execution***********");
	}
	

	
} // End Class
