package hp.Seals.APITest;

import com.google.common.base.Objects;

public class Contract {
	
	private String overallContractStartDate;
	private String overallContractEndDate;
	private String startDate ;
	private String endDate ;
	private String active ;
	private String status;
	private String offerCode;
	private String packageCode;
	private String offerDescription;
 
	// private Links  links;

	public String getOverallContractStartDate() {
		return overallContractStartDate;
	}

	public void setOverallContractStartDate(String overallContractStartDate) {
		this.overallContractStartDate = overallContractStartDate;
	}

	public String getOverallContractEndDate() {
		return overallContractEndDate;
	}

	public void setOverallContractEndDate(String overallContractEndDate) {
		this.overallContractEndDate = overallContractEndDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOfferCode() {
		return offerCode;
	}

	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public String getOfferDescription() {
		return offerDescription;
	}

	public void setOfferDescription(String offerDescription) {
		this.offerDescription = offerDescription;
	}

//	public Links getLinks() {
//		return links;
//	}
//
//	public void setLinks(Links links) {
//		this.links = links;
//	}
      	 
	@Override
    public boolean equals(Object ob) {
		
		Contract contract = (Contract)ob;
		boolean isEqual = false;
		if(this.overallContractStartDate.equals(contract.overallContractStartDate) &&
				this.overallContractEndDate.equals(contract.overallContractEndDate) &&
				this.startDate.equals(contract.startDate) &&
				this.endDate.equals(contract.endDate) &&
				this.active.equals(contract.active) &&
				this.status.equals(contract.status) &&
				this.offerCode.equals(contract.offerCode) &&
				this.packageCode.equals(contract.packageCode) &&				
				this.offerDescription.equals(contract.offerDescription) 
				) {
			
			isEqual = true;
		}
	   return isEqual;
	}
  
	@Override
	public int hashCode() {
		
//		System.out.println("Hash Code=> " + Objects.hashCode("overallContractStartDate","overallContractEndDate","startDate","endDate","active",
//				"status","offerCode","packageCode","offerDescription"));
		
		return Objects.hashCode("overallContractStartDate","overallContractEndDate","startDate","endDate","active",
				"status","offerCode","packageCode","offerDescription");
	}

}
