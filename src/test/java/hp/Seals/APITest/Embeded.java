package hp.Seals.APITest;

import java.util.ArrayList;
import java.util.List;

public class Embeded {
	
	
	List<Contract> contractList = new ArrayList<Contract>();
	List<Warranty> warrantyList = new ArrayList<Warranty>();
	
	// getter and setter method
		
	public List<Contract> getContractList() {
		return contractList;
	}
	public void setContractList(List<Contract> contractList) {
		this.contractList = contractList;
	}
	
	public List<Warranty> getWarrantyList() {
		return warrantyList;
	}
	public void setWarrantyList(List<Warranty> warrantyList) {
		this.warrantyList = warrantyList;
	}
	
	
	
}
