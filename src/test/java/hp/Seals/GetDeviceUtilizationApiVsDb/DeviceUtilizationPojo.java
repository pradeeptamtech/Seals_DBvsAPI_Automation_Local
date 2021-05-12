package hp.Seals.GetDeviceUtilizationApiVsDb;

import com.google.common.base.Objects;

public class DeviceUtilizationPojo {

	private String status;
	private Long t;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}	
	
	public long getT() {
		return t;
	}
	public void setT(long t) {
		this.t = t;
	}
	
	
	@Override
	public String toString() {
		return "["  + status + "," + t + "]";
	}
	
	@Override
    public boolean equals(Object ob) {
		
		boolean isEqual = false;
		if(this.status.equals(status) && this.t.equals(t)  ) {
			isEqual = true;
		}
	   return isEqual;
	}
  
	
	@Override
	public int hashCode() {		
	//	System.out.println("Hash Code=> " + Objects.hashCode("status","t" ));
		
		return Objects.hashCode("status","t" );
	}
	
	
}

	

