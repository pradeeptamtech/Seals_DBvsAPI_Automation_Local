package hp.Seals.PrintheadDetailsApiDbTest;

import com.google.common.base.Objects;

public class InkUsedValuesResultsPojo {

	private String ph_serial_no;
	private Float ink_used;
	
	
	// Getter and Setter Method
	public String getPh_serial_no() {
		return ph_serial_no;
	}

	public void setPh_serial_no(String ph_serial_no) {
		this.ph_serial_no = ph_serial_no;
	}

	public Float getInk_used() {
		return ink_used;
	}

	public void setInk_used(Float ink_used) {
		this.ink_used = ink_used;
	}

	
	@Override
	public String toString() {
		//return "[ph_serial_no=" + ph_serial_no + ", startTimestamp=" + ink_used + "]";
		return "[" + ph_serial_no + ", " + ink_used + "]";
	}
	
	
	@Override
    public boolean equals(Object ob) 
	{		
		boolean isEqual = false;
		if( this.ph_serial_no.equals(ph_serial_no)  &&  this.ink_used.equals(ink_used) )  {
			isEqual = true;
		}
	   return isEqual;
	} 
	
	@Override
	public int hashCode() 
	{		
		//System.out.println("Hash Code=> " + Objects.hashCode("ph_serial_no","ink_used" ) );
		return Objects.hashCode("ph_serial_no", "ink_used" );
	}
	
	
	
}
