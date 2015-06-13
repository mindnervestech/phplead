package com.mnt.businessApp.viewmodel;

import java.util.Date;

import com.mnt.entities.businessApp.ActivityStream;

public class LeadHistoryVM {
	public Long number;
	public Date	date;
	public String newDisposition1;
	public String newDisposition2;
	public String oldDisposition1;
	public String oldDisposition2;
	public String reason;
	
	public LeadHistoryVM(ActivityStream activityStream) {
		this.number = activityStream.getId();
		this.reason = activityStream.getReason();
		this.date = activityStream.getCreatedDate();
		this.newDisposition1 = activityStream.getNewDisposition1();
		this.newDisposition2 = activityStream.getNewDisposition2();
		this.oldDisposition1 = activityStream.getOldDisposition1();
		this.oldDisposition2 = activityStream.getOldDisposition2();
		
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
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

	public String getNewDisposition1() {
		return newDisposition1;
	}

	public void setNewDisposition1(String newDisposition1) {
		this.newDisposition1 = newDisposition1;
	}

	public String getNewDisposition2() {
		return newDisposition2;
	}

	public void setNewDisposition2(String newDisposition2) {
		this.newDisposition2 = newDisposition2;
	}

	public String getOldDisposition1() {
		return oldDisposition1;
	}

	public void setOldDisposition1(String oldDisposition1) {
		this.oldDisposition1 = oldDisposition1;
	}

	public String getOldDisposition2() {
		return oldDisposition2;
	}

	public void setOldDisposition2(String oldDisposition2) {
		this.oldDisposition2 = oldDisposition2;
	}


}
