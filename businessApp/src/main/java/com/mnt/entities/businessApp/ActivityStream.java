package com.mnt.entities.businessApp;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class ActivityStream {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	
	public String newDisposition1;
	
	public String newDisposition2;
	
	public String newDisposition3;
	
	public String oldDisposition1;
	
	public String oldDisposition2;
	
	public String oldDisposition3;
	
	public String reason;
	
	public Date createdDate;
	
	@ManyToOne
	public Lead lead;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Lead getLead() {
		return lead;
	}

	public void setLead(Lead lead) {
		this.lead = lead;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getNewDisposition3() {
		return newDisposition3;
	}

	public void setNewDisposition3(String newDisposition3) {
		this.newDisposition3 = newDisposition3;
	}

	public String getOldDisposition3() {
		return oldDisposition3;
	}

	public void setOldDisposition3(String oldDisposition3) {
		this.oldDisposition3 = oldDisposition3;
	}
	
	
	
}
