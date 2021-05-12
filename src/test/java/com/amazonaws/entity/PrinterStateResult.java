package com.amazonaws.entity;

public class PrinterStateResult {

	private String status;
	private String sub_Status;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSub_Status() {
		return sub_Status;
	}
	public void setSub_Status(String sub_Status) {
		this.sub_Status = sub_Status;
	}
	@Override
	public String toString() {
		return "PrinterStateResult [status=" + status + ", sub_Status=" + sub_Status + "]";
	}
	
	
	
}
