package hp.Seals.GetSolutionApiVsDB;

import com.google.common.base.Objects;

import hp.Seals.MaintenanceTasksApiWithDb.List_maintenancesPojo;

public class GetSolutionPojo {

	private String event_code;
	private String insert_ts;
	
	// Getter and Setter Methods

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

	@Override
    public boolean equals(Object ob) {		
		GetSolutionPojo solutionPojoObj = (GetSolutionPojo) ob;
		boolean isEqual = false;
		
		if(this.event_code.equals(event_code) && this.insert_ts.equals(insert_ts))	 {			
			isEqual = true;
		}
	   return isEqual;
	}
  
	@Override
	public int hashCode() {
		return Objects.hashCode("event_code","insert_ts");
	}

	
	@Override
	public String toString() {
		return  "[ \"event_code\" : " + "\"" + event_code + "\" , \"update_TS\" : " + "\"" + insert_ts + "\"" + " ]";
	}
	



}
