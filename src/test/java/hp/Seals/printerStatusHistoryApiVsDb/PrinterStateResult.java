package hp.Seals.printerStatusHistoryApiVsDb;

import com.google.common.base.Objects;

public class PrinterStateResult {

	private String status;
	private String sub_Status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSub_Status() {
		return sub_Status;
	}
	public void setSub_Status(String sub_Status) {
		this.sub_Status = sub_Status;
	}
	
	@Override
	public String toString() {
		return "[" + "status=" + status + ", sub_Status=" + sub_Status + "]";
	}
	
	@Override
    public boolean equals(Object ob) {
		
		boolean isEqual = false;
		if(this.status.equals(status) && this.sub_Status.equals(sub_Status)  ) {
			isEqual = true;
		}
	   return isEqual;
	}
  
	
	@Override
	public int hashCode() {		
	//	System.out.println("Hash Code=> " + Objects.hashCode("ph_serial_no","startTimestamp" ));
		
		return Objects.hashCode("status","sub_Status" );
	}
	
	
}
