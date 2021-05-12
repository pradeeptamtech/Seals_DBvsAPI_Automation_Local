package hp.Seals.PrintheadDetailsApiDbTest;

import com.google.common.base.Objects;

public class ColorValuesResultPojo {

	private String color;
	private String ph_serial_no;
	
	// Getter and Setter Method
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
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
		return "[" + ph_serial_no + ", " + color + "]";
	}

	@Override
    public boolean equals(Object ob) {
		
		boolean isEqual = false;
		if( this.ph_serial_no.equals(ph_serial_no) && this.color.equals(color)  ) {
			isEqual = true;
		}
	   return isEqual;
	}
  
	
	@Override
	public int hashCode() {		
	//	System.out.println("Hash Code=> " + Objects.hashCode("ph_serial_no","color" ));
		
		return Objects.hashCode("ph_serial_no", "color" );
	}

}
