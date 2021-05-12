package hp.Seals.getErrorEventsApiVsDbTest;

import com.google.common.base.Objects;

public class EventTsPojo {
	
	private String cust_cd;	
	private String evt_ocrd_ts;
	
	// public getter and setter Method
	
	public String getCust_cd() {
		return cust_cd;
	}
	public void setCust_cd(String cust_cd) {
		this.cust_cd = cust_cd;
	}
	public String getEvt_ocrd_ts() {
		return evt_ocrd_ts;
	}
	public void setEvt_ocrd_ts(String evt_ocrd_ts) {
		this.evt_ocrd_ts = evt_ocrd_ts;
	}

	@Override
	public boolean equals(Object ob) {
		boolean isEqual = false;
		if( this.cust_cd.equals(cust_cd) && this.evt_ocrd_ts.equals(evt_ocrd_ts) ) {			
			isEqual = true;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode( "cust_cd" + "evt_ocrd_ts" );
	}


	@Override
	public String toString() {
		return  "[" + cust_cd + "," + evt_ocrd_ts +"]"  ;
	}

	
	
}
