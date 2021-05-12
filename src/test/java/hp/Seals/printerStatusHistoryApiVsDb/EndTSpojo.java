package hp.Seals.printerStatusHistoryApiVsDb;

import com.google.common.base.Objects;

public class EndTSpojo {

	private String serial_no;
	private String start_TS;
	private String status;
	private String end_TS;
	
	// Getters and setters method
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public String getStart_TS() {
		return start_TS;
	}
	public void setStart_TS(String start_TS) {
		this.start_TS = start_TS;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEnd_TS() {
		return end_TS;
	}
	public void setEnd_TS(String end_TS) {
		this.end_TS = end_TS;
	}
	
	@Override
	public String toString() 
	{
		return " [" + "serial_Number:'" + serial_no + "', start_TS:'" + start_TS + "', status:'" + status + "', " 
					+ "end_TS:'" + end_TS + "'" + "] ";
	}
	
	@Override
    public boolean equals(Object ob) 
	{	
		boolean isEqual = false;
		if(this.serial_no.equals(serial_no) && this.start_TS.equals(start_TS) && this.status.equals(status) && this.end_TS.equals(end_TS) ) {
			isEqual = true;
		}
	   return isEqual;
	}
  
	
	@Override
	public int hashCode() 
	{				
		return Objects.hashCode("serial_no","start_TS","status","end_TS" );
	}
	
}
