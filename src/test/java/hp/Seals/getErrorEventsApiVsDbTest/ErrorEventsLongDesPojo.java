package hp.Seals.getErrorEventsApiVsDbTest;

import com.google.common.base.Objects;

public class ErrorEventsLongDesPojo {
	
	private String cust_cd;
	private String long_description;
	
	
	// Public getters and setters methods
	
	public String getCust_cd() {
		return cust_cd;
	}
	public void setCust_cd(String cust_cd) {
		this.cust_cd = cust_cd;
	}
	
	public String getLong_description() {
		return long_description;
	}
	public void setLong_description(String long_description) {
		this.long_description = long_description;
	}
	
	
	
	@Override
    public boolean equals(Object ob) {
		boolean isEqual = false;
		if( this.cust_cd.equals(cust_cd) && this.long_description.equals(long_description) ) {			
			isEqual = true;
		}
	   return isEqual;
	}
  
	@Override
	public int hashCode() {
		return Objects.hashCode( "cust_cd" + "long_description" );
	}

	
	@Override
	public String toString() {
		return  "[" + cust_cd + " , " + long_description + "]"  ;
	}
	
}
