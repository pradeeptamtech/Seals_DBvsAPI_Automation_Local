package hp.Seals.printerStatusHistoryApiVsDb;

public class Key<K1,K2,K3> {

	public K1  serial_no;
	public K2  start_TS;
	public K3  status;
	private String channel;

	//Constructor
	public Key(K1 serial_no, K2 start_TS, K3 status) {
		this.serial_no = serial_no;
		this.start_TS = start_TS;
		this.status = status;
	}

	//Getter and Setter methods
	public K1 getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(K1 serial_no) {
		this.serial_no = serial_no;
	}


	public K2 getStart_TS() {
		return start_TS;
	}
	public void setStart_TS(K2 start_TS) {
		this.start_TS = start_TS;
	}


	public K3 getStatus() {
		return status;
	}
	public void setStatus(K3 status) {
		this.status = status;
	}


	public String getChannel() {
		return channel;
	}	public void setChannel(String channel) {
		this.channel = channel;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Key key = (Key) o;

		if (serial_no != null ? !serial_no.equals(key.serial_no) : key.serial_no != null) return false;
		if (start_TS != null ? !start_TS.equals(key.start_TS) : key.start_TS != null) return false;
		if (status != null ? !status.equals(key.status) : key.status != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = serial_no != null ? serial_no.hashCode() : 0;
		result = 31 * result + (start_TS != null ? start_TS.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "[ " + serial_no + " , " + start_TS + " , " + status + " ]";
	}


}
