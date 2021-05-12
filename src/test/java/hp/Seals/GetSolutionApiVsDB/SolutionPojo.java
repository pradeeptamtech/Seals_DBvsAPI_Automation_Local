package hp.Seals.GetSolutionApiVsDB;

import com.google.common.base.Objects;

public class SolutionPojo {

	private String event_code;
	private String insert_ts;
	private String short_description;
	
	// public getter and setter methods
	
	public String getEvent_code() {
		return event_code;
	}
	public void setEvent_code(String event_code) {
		this.event_code = event_code;
	}
	public String getInsert_ts() {
		return insert_ts;
	}
	public void setInsert_ts(String insert_ts) {
		this.insert_ts = insert_ts;
	}
	public String getShort_description() {
		return short_description;
	}
	public void setShort_description(String short_description) {
		this.short_description = short_description;
	}
	
	
	@Override
    public boolean equals(Object ob) {		
		boolean isEqual = false;
		
		if(this.event_code.equals(event_code) && this.insert_ts.equals(insert_ts) && this.short_description.equals(short_description)) {			
			isEqual = true;
		}
	   return isEqual;
	}
  
	@Override
	public int hashCode() {
		return Objects.hashCode("event_code","insert_ts","short_description");
	}

	
	@Override
	public String toString() {
		//return  event_code + " , "+ insert_ts + " , " + short_description ;
		
		return "[ "+ event_code + ","+ insert_ts + " ] = " + short_description ;
		
//		return  "[ \"event_code\" : " + "\"" + event_code + "\" , \"update_TS\" : " + "\"" + insert_ts + "\", \"short_Description\" : \" "
//				+  "\"" + short_description + "\"" + " ]";
	}
	
	
}
