package hp.Seals.getErrorEventsApiVsDbTest;

import com.google.common.base.Objects;

public class ErrorEventsSeverityPojo {

	private String error_Code;	
	private String severity;

	// Public getters and setters methods

	public String getError_Code() {
		return error_Code;
	}

	public void setError_Code(String error_Code) {
		this.error_Code = error_Code;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	
	@Override
	public boolean equals(Object ob) {
		boolean isEqual = false;
		if( this.error_Code.equals(error_Code) && this.severity.equals(severity) ) {			
			isEqual = true;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode( "error_Code" + "severity" );
	}


	@Override
	public String toString() {
		return  "[" + error_Code + "," + severity +"]"  ;
	}

}

