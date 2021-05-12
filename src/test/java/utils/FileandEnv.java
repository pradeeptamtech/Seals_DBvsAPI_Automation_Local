package utils;


import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class FileandEnv {

	public static Map<String,String> fileandEnv = new HashMap<String,String>();
	public static Properties propMain = new Properties();
	public static Properties propPreSet = new Properties();

	public static Map<String,String> endAndFile()
	{
		String environment = System.getProperty("env");			
		//System.out.println("Environment...> " + environment);
		try {
			
			if(environment != null)
			{
				if("dev".equalsIgnoreCase(environment))
				{
					//FileInputStream fisDev = new FileInputStream(System.getProperty("user.dir")+"/inputs/dev.properties");
					FileInputStream fisDev = new FileInputStream("./inputs/dev.properties");
					propMain.load(fisDev);
					fileandEnv.put("ServerUrl",propMain.getProperty("ServerUrl"));
							
				} 
			}
			else {
				//System.out.println("else execution........");
				FileInputStream fisDev = new FileInputStream("./inputs/dev.properties");
				propMain.load(fisDev);
				fileandEnv.put("ServerUrl",propMain.getProperty("ServerUrl"));
			}

		}catch(Exception e) {
			// handle exception
			e.fillInStackTrace();
		}	
		
		return fileandEnv;
	}

	public static Map<String,String> getConfigReader()
	{
		if(fileandEnv == null) {
			fileandEnv = endAndFile();
		}
		return fileandEnv;
	}
	
//		public static void main(String[] args)
//		{
//			
//			System.out.println("------- " + endAndFile());
//			
//			//System.out.println("properties data----> "+FileandEnv.endAndFile());
//			
//			//System.out.println("------- " + getConfigReader());
//			
//		}
}
