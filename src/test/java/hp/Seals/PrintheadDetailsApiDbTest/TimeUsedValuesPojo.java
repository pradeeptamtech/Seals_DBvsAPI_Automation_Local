package hp.Seals.PrintheadDetailsApiDbTest;

import com.google.common.base.Objects;

public class TimeUsedValuesPojo 
{
	private String ph_serial_no;
	private Integer  time_used;
	
	// Getter and Setter Method
	
	public String getPh_serial_no() {
		return ph_serial_no;
	}
	public void setPh_serial_no(String ph_serial_no) {
		this.ph_serial_no = ph_serial_no;
	}
		
	public Integer getTime_used() {
		return time_used;
	}
	public void setTime_used(Integer time_used) {
		this.time_used = time_used;
	}
	
	
	@Override
	public String toString() {
		//return "[ph_serial_no=" + ph_serial_no + ", startTimestamp=" + time_used + "]";
		return "[" + ph_serial_no + ", " + time_used + "]";
	}
	
	
	@Override
    public boolean equals(Object ob) {
		
		boolean isEqual = false;
		if( this.ph_serial_no.equals(ph_serial_no) && this.time_used.equals(time_used)  ) {
			isEqual = true;
		}
	   return isEqual;
	}
  
	
	@Override
	public int hashCode() {		
		System.out.println("Hash Code=> " + Objects.hashCode("ph_serial_no","time_used" ));
		
		return Objects.hashCode("ph_serial_no", "time_used" );
	}

}

