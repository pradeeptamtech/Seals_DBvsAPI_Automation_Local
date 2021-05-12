package hp.Seals.MaintenanceTasksApiWithDb;

import java.util.Iterator;

import com.google.common.base.Objects;

public class List_maintenancesPojo {

	private String estimated_date_trigger;
	private String last_maintenance_date;
	private String user_replaceable;
	private String progress_Percentage;
	private String id;
	private String date;
	private String name;
	private String status;
	
	
	// Public Getter and Setter Method
	
	public String getEstimated_date_trigger() {
		return estimated_date_trigger;
	}
	public void setEstimated_date_trigger(String estimated_date_trigger) {
		this.estimated_date_trigger = estimated_date_trigger;
	}
	
	public String getLast_maintenance_date() {
		return last_maintenance_date;
	}
	public void setLast_maintenance_date(String last_maintenance_date) {
		this.last_maintenance_date = last_maintenance_date;
	}
	
	public String getUser_replaceable() {
		return user_replaceable;
	}
	public void setUser_replaceable(String user_replaceable) {
		this.user_replaceable = user_replaceable;
	}
	
	public String getProgress_Percentage() {
		return progress_Percentage;
	}
	public void setProgress_Percentage(String progress_Percentage) {
		this.progress_Percentage = progress_Percentage;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	@Override
    public boolean equals(Object ob) {
		
		List_maintenancesPojo listMaintenancesObj = (List_maintenancesPojo) ob;
		boolean isEqual = false;
		
		if(this.estimated_date_trigger.equals(listMaintenancesObj.estimated_date_trigger) &&
				this.last_maintenance_date.equals(listMaintenancesObj.last_maintenance_date) &&
				this.user_replaceable.equals(listMaintenancesObj.user_replaceable) &&
				this.progress_Percentage.equals(listMaintenancesObj.progress_Percentage) &&
				this.id.equals(listMaintenancesObj.id) &&
				this.date.equals(listMaintenancesObj.date) &&
				this.name.equals(listMaintenancesObj.name) &&
				this.status.equals(listMaintenancesObj.status)
				) {
			
			isEqual = true;
		}
	   return isEqual;
	}
  
	@Override
	public int hashCode() {
		return Objects.hashCode("estimated_date_trigger",
				                "last_maintenance_date;",
				                "user_replaceable",
				                "progress_Percentage",
				                "id",
				                "date",
				                "name",
				                "status" 
				                );
	}

	
	@Override
	public String toString() {
		return  "[ " + estimated_date_trigger + " ,"
					+ last_maintenance_date + " ," 
					+ user_replaceable +" ," 
					+ progress_Percentage + " ," 
					+ id + " ," 
					+ date + " ,"
					+ name + " ," 
					+ status 
				    + " ]";
	}
	
		
}
