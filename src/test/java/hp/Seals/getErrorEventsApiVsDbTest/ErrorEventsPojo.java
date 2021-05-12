package hp.Seals.getErrorEventsApiVsDbTest;

import com.google.common.base.Objects;

//import hp.Seals.MaintenanceTasksApiWithDb.ListMaintenances;

public class ErrorEventsPojo {

	private String cust_cd;
	private String short_description;
	private String long_description;
	
	
	// Public getters and setters methods
	
	public String getCust_cd() {
		return cust_cd;
	}
	public void setCust_cd(String cust_cd) {
		this.cust_cd = cust_cd;
	}
	public String getShort_description() {
		return short_description;
	}
	public void setShort_description(String short_description) {
		this.short_description = short_description;
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
		if( this.cust_cd.equals(cust_cd) && this.short_description.equals(short_description) ) {			
			isEqual = true;
		}
	   return isEqual;
	}
  
	@Override
	public int hashCode() {
		return Objects.hashCode( "cust_cd" + "short_description" );
	}

	
	@Override
	public String toString() {
		return  "[" + cust_cd + " , " + short_description + "]"  ;
	}
	
}
