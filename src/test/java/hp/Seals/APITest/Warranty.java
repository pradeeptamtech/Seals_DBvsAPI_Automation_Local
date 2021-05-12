package hp.Seals.APITest;

import com.google.common.base.Objects;

public class Warranty {

	private String overallWarrantyStartDate ;
	private String overallWarrantyEndDate ;
	private String startDate;
	private String endDate ;
	private String active;
	private String status ;
	private String warrantyDeterminationDescription ;
	private String factoryWarrantyTermCode ;
	private String factoryWarrantyStartDate ;
	private String factoryWarrantyEndDate;
	private String offerCode;
	private String offerDescription ;
	private String salesOrderNumber ;
	private String covWindow ;
	private String responseCommitment ;
	 
	//private Links  links;

	public String getOverallWarrantyStartDate() {
		return overallWarrantyStartDate;
	}

	public void setOverallWarrantyStartDate(String overallWarrantyStartDate) {
		this.overallWarrantyStartDate = overallWarrantyStartDate;
	}

	public String getOverallWarrantyEndDate() {
		return overallWarrantyEndDate;
	}

	public void setOverallWarrantyEndDate(String overallWarrantyEndDate) {
		this.overallWarrantyEndDate = overallWarrantyEndDate;
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

	public String getWarrantyDeterminationDescription() {
		return warrantyDeterminationDescription;
	}

	public void setWarrantyDeterminationDescription(String warrantyDeterminationDescription) {
		this.warrantyDeterminationDescription = warrantyDeterminationDescription;
	}

	public String getFactoryWarrantyTermCode() {
		return factoryWarrantyTermCode;
	}

	public void setFactoryWarrantyTermCode(String factoryWarrantyTermCode) {
		this.factoryWarrantyTermCode = factoryWarrantyTermCode;
	}

	public String getFactoryWarrantyStartDate() {
		return factoryWarrantyStartDate;
	}

	public void setFactoryWarrantyStartDate(String factoryWarrantyStartDate) {
		this.factoryWarrantyStartDate = factoryWarrantyStartDate;
	}

	public String getFactoryWarrantyEndDate() {
		return factoryWarrantyEndDate;
	}

	public void setFactoryWarrantyEndDate(String factoryWarrantyEndDate) {
		this.factoryWarrantyEndDate = factoryWarrantyEndDate;
	}

	public String getOfferCode() {
		return offerCode;
	}

	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	public String getOfferDescription() {
		return offerDescription;
	}

	public void setOfferDescription(String offerDescription) {
		this.offerDescription = offerDescription;
	}

	public String getSalesOrderNumber() {
		return salesOrderNumber;
	}

	public void setSalesOrderNumber(String salesOrderNumber) {
		this.salesOrderNumber = salesOrderNumber;
	}

	public String getCovWindow() {
		return covWindow;
	}

	public void setCovWindow(String covWindow) {
		this.covWindow = covWindow;
	}

	public String getResponseCommitment() {
		return responseCommitment;
	}

	public void setResponseCommitment(String responseCommitment) {
		this.responseCommitment = responseCommitment;
	}

	@Override
    public boolean equals(Object ob) {
		
		Warranty warranty = (Warranty) ob;
		boolean isEqual = false;
		
		if(this.overallWarrantyStartDate.equals(warranty.overallWarrantyStartDate) &&
				this.overallWarrantyEndDate.equals(warranty.overallWarrantyEndDate) &&
				this.startDate.equals(warranty.startDate) &&
				this.endDate.equals(warranty.endDate) &&
				this.active.equals(warranty.active) &&
				this.status.equals(warranty.status) &&
				this.warrantyDeterminationDescription.equals(warranty.warrantyDeterminationDescription) &&
				this.factoryWarrantyTermCode.equals(warranty.factoryWarrantyTermCode) &&				
				this.factoryWarrantyStartDate.equals(warranty.factoryWarrantyStartDate) &&
				this.factoryWarrantyEndDate.equals(warranty.factoryWarrantyEndDate) &&
				this.offerCode.equals(warranty.offerCode) &&
				this.offerDescription.equals(warranty.offerDescription) &&
				this.salesOrderNumber.equals(warranty.salesOrderNumber) &&
				this.covWindow.equals(warranty.covWindow) &&
				this.responseCommitment.equals(warranty.responseCommitment) 				
				) {
			
			isEqual = true;
		}
	   return isEqual;
	}
  
	@Override
	public int hashCode() {
		return Objects.hashCode("overallWarrantyStartDate","overallWarrantyEndDate","startDate","endDate","active",
				"warrantyDeterminationDescription","factoryWarrantyTermCode","factoryWarrantyStartDate",
				"factoryWarrantyEndDate","offerCode", "offerDescription", "salesOrderNumber","covWindow",
				"responseCommitment" );
	}

	
}
