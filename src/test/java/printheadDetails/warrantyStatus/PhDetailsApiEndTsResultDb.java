package printheadDetails.warrantyStatus;

import com.google.common.base.Objects;

public class PhDetailsApiEndTsResultDb {

	private String ph_serial_no;

	private String end_ts;

	// Getter and Setter Method
	public String getPh_serial_no() {
		return ph_serial_no;
	}

	public void setPh_serial_no(String ph_serial_no) {
		this.ph_serial_no = ph_serial_no;
	}

	public String getEnd_ts() {
		return end_ts;
	}

	public void setEnd_ts(String end_ts) {
		this.end_ts = end_ts;
	}
	
	@Override
	public String toString() {
		//return "[ph_serial_no=" + ph_serial_no + ", startTimestamp=" + startTimestamp + "]";
		return "[" + ph_serial_no + ", " + end_ts + "]";
	}

	
	@Override
    public boolean equals(Object ob) {
		
		boolean isEqual = false;
		if(this.ph_serial_no.equals(ph_serial_no) && this.end_ts.equals(end_ts)  ) {
			isEqual = true;
		}
	   return isEqual;
	}
  
	
	@Override
	public int hashCode() {		
	//	System.out.println("Hash Code=> " + Objects.hashCode("ph_serial_no","end_ts" ));
		
		return Objects.hashCode("ph_serial_no","end_ts" );
	}
	
	
}
