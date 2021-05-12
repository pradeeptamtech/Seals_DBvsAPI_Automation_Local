package apiConfig;

import java.util.HashMap;
import java.util.Map;


public class HeaderConfigs {
	
	APITokenGenerate ob = new APITokenGenerate();
	
	String TOTP = ob.TokenGenerate();

	public Map<String,String> DefaultHeaders()
	{
		Map<String,String> deafultHeaders = new HashMap<String,String>();
	    deafultHeaders.put("Content-Type", "application/json");
	    
	    return 	deafultHeaders;			
	}

	public Map<String,String> HeadersWithToken()
	{
		Map<String,String> deafultHeaders = new HashMap<String,String>();
	    //deafultHeaders.put("Content-Type", "application/json");   
	    //deafultHeaders.put("X-API-KEY", "jmKUZ7mePwqKXpU2hB2MATfPYMSjhu6ryrKUYzfG");
	    deafultHeaders.put("Client-ID", "AutoQA");
	    deafultHeaders.put("Token", TOTP);
	    
	    return 	deafultHeaders;			
	}
	
	/*	
	 * public static void main(String[] args) 
		HeaderConfigs head = new HeaderConfigs();
		System.out.println(head.HeadersWithToken());
		} 
	*/
		
	
}

