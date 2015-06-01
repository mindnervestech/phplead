package com.mnt.businessApp.viewmodel;

import java.util.Date;

import com.mnt.entities.businessApp.ActivityStream;

public class LeadHistoryVM {
	public Long number;
	public String status;
	public String reason;
	public Date	date;
	
	public LeadHistoryVM(ActivityStream activityStream) {
		this.number = activityStream.getId();
		this.reason = activityStream.getReason();
		this.date = activityStream.getCreatedDate();
		
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}


}
