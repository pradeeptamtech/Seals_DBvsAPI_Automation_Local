package apiConfig;

import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import java.util.Date;


public class APITokenGenerate {

	public static byte[] getSHA(String input) throws NoSuchAlgorithmException 
	{  
		// Static getInstance method is called with hashing SHA  
		MessageDigest md = MessageDigest.getInstance("SHA-256");  

		// digest() method called to calculate message digest of an input and return array of byte 
		return md.digest(input.getBytes(StandardCharsets.UTF_8));  
	} 

	public static String toHexString(byte[] hash) 
	{ 
		// Convert byte array into signum representation  
		BigInteger number = new BigInteger(1, hash);  

		// Convert message digest into hex value  
		StringBuilder hexString = new StringBuilder(number.toString(16));  
		//System.out.println("................"   + hexString.length() );

		if( hexString.length() == 64 ) {
			// Pad with leading zeros 
			while (hexString.length() < 32)  
			{  
				hexString.insert(0, '0');  
			} 
		}
		else if( hexString.length() == 63 ){
			hexString.insert(0, '0');
		}
		return hexString.toString();  
	} 

	public static String TokenGenerate() 
	{
		try {
			long interval = 14400;
			String salt = "H5aKXzpBZ3Eej9O56ZV3Uklg1FplOmcUz";
			long seconds = new Date().getTime() / 1000;
			Double x  =  Math.floor(seconds/interval);
			//System.out.println("x....." + x);

			int TOTP_code = (int) Math.floor(x);
			//System.out.println("\n" + "Totp" + " : " +TOTP_code);

			String raw = TOTP_code + salt;
			//System.out.println("\n" + "raw" + " : " + raw); 

			//System.out.println(toHexString(getSHA(raw))); 		    
			return toHexString(getSHA(raw));

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Driver code  
	public static void main(String args[])  
	{  

		//TokenGenerate();
		//APITokenGenerate ob = new APITokenGenerate();
		System.out.println("API Token: "+TokenGenerate());		
	}
}