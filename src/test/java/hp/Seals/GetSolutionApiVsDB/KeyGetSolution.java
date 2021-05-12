package hp.Seals.GetSolutionApiVsDB;

public class KeyGetSolution<K1,K2> {

	public K1  event_Code;
	public K2  update_TS;

	private String short_Description;
	private String severity;
	
	//Constructor
	public KeyGetSolution(K1 event_Code, K2 update_TS) {
		this.event_Code = event_Code;
		this.update_TS = update_TS;		
	}


	// Getter and setters methods
	public K1 getEvent_Code() {
		return event_Code;
	}

	public void setEvent_Code(K1 event_Code) {
		this.event_Code = event_Code;
	}

	public K2 getUpdate_TS() {
		return update_TS;
	}

	public void setUpdate_TS(K2 update_TS) {
		this.update_TS = update_TS;
	}

	public String getShort_Description() {
		return short_Description;
	}
	public void setShort_Description(String short_Description) {
		this.short_Description = short_Description;
	}

	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}


	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o) 
	{
		if (this == o) 
			return true;
		if (o == null || getClass() != o.getClass()) 
			return false;

		KeyGetSolution key = (KeyGetSolution) o;

		if (event_Code != null ? !event_Code.equals(key.event_Code) : key.event_Code != null) 
			return false;
		if (update_TS != null ? !update_TS.equals(key.update_TS) : key.update_TS != null) 
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = event_Code != null ? event_Code.hashCode() : 0;
		result = 31 * result + (update_TS != null ? update_TS.hashCode() : 0);

		return result;
	}

	@Override
	public String toString() {
		return "[ \"event_Code\": \"" + event_Code + "\" , \"update_TS\": \"" + update_TS + "\"" + " ]";
	}


}
