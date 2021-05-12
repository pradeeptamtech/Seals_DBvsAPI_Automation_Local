package printheadDetails.warrantyStatus;

import com.google.common.base.Objects;

public class PrintheadDetailsResultDb {

	private String status;
	private String ph_serial_no;
	private int sum;
	
	// Getter and Setter Method
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPh_serial_no() {
		return ph_serial_no;
	}

	public void setPh_serial_no(String ph_serial_no) {
		this.ph_serial_no = ph_serial_no;
	}

	@Override
	public String toString() {
		//return "[ph_serial_no=" + ph_serial_no + ", status=" + status + "]";
		return "[" + ph_serial_no + ", " + status + "]";
	}

	@Override
    public boolean equals(Object ob) {
		
		boolean isEqual = false;
		if(this.ph_serial_no.equals(ph_serial_no) &&this.status.equals(status)  ) {
			isEqual = true;
		}
	   return isEqual;
	}
  
	
	@Override
	public int hashCode() {		
	//	System.out.println("Hash Code=> " + Objects.hashCode("ph_serial_no","status" ));
		
		return Objects.hashCode("ph_serial_no","status" );
	}
	
}
