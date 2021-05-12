package printheadDetails.warrantyStatus;

import com.google.common.base.Objects;

public class PhDetailsApiStartTsResultDb {

	private String ph_serial_no;
	
	private String startTimestamp;
	
	
	// Getter and Setter Method
	public String getPh_serial_no() {
		return ph_serial_no;
	}

	public void setPh_serial_no(String ph_serial_no) {
		this.ph_serial_no = ph_serial_no;
	}

	public String getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(String startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

		
	@Override
	public String toString() {
		//return "[ph_serial_no=" + ph_serial_no + ", startTimestamp=" + startTimestamp + "]";
		return "[" + ph_serial_no + ", " + startTimestamp + "]";
	}

	@Override
    public boolean equals(Object ob) {
		
		boolean isEqual = false;
		if(this.ph_serial_no.equals(ph_serial_no) && this.startTimestamp.equals(startTimestamp)  ) {
			isEqual = true;
		}
	   return isEqual;
	}
  
	
	@Override
	public int hashCode() {		
	//	System.out.println("Hash Code=> " + Objects.hashCode("ph_serial_no","startTimestamp" ));
		
		return Objects.hashCode("ph_serial_no","startTimestamp" );
	}
	
	
	
}
