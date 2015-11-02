package com.mnt.entities.businessApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Lead {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	
	public Long leadNumber;
	
	@OneToOne(fetch=FetchType.EAGER)
	public LeadDetails leadDetails;
	
	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	public User user;
	
	public String disposition1;
	
	public String disposition2;
	
	public String disposition3;
	
	public Date	uploadDate;
	
	public Date assignLeadDate;
	
	public Date	lastDispo1ModifiedDate;
	
	public Integer escalatedLevel = 0;
	
	public Date escalatedDate;
	
	public String origin;
	
	public String modalNo;
	
	public String brand;
	
	@ManyToOne(fetch=FetchType.EAGER)
	public User escalatedTo;
	
	
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	public List<ActivityStream> activityStream;
	
	public Date followUpDate;
	
	public String reason;
	
	public String zone;
	
	public String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLeadNumber() {
		return leadNumber;
	}

	public void setLeadNumber(Long leadNumber) {
		this.leadNumber = leadNumber;
	}

	public LeadDetails getLeadDetails() {
		return leadDetails;
	}

	public void setLeadDetails(LeadDetails leadDetails) {
		this.leadDetails = leadDetails;
	}

	public String getDisposition1() {
		return disposition1;
	}

	public void setDisposition1(String disposition1) {
		this.disposition1 = disposition1;
	}

	public String getDisposition2() {
		return disposition2;
	}

	public void setDisposition2(String disposition2) {
		this.disposition2 = disposition2;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public List<ActivityStream> getActivityStream() {
		return activityStream;
	}

	public void setActivityStream(List<ActivityStream> activityStream) {
		this.activityStream = activityStream;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void addActivityStream(ActivityStream activityStream) {
		if(this.activityStream == null){
			this.activityStream = new ArrayList<ActivityStream>();
		}
		this.activityStream.add(activityStream);
	}

	public Integer getEscalatedLevel() {
		return escalatedLevel;
	}

	public void setEscalatedLevel(Integer escalatedLevel) {
		this.escalatedLevel = escalatedLevel;
	}

	public Date getEscalatedDate() {
		return escalatedDate;
	}

	public void setEscalatedDate(Date escalatedDate) {
		this.escalatedDate = escalatedDate;
	}

	public User getEscalatedTo() {
		return escalatedTo;
	}

	public void setEscalatedTo(User escalatedTo) {
		this.escalatedTo = escalatedTo;
	}

	public Date getLastDispo1ModifiedDate() {
		return lastDispo1ModifiedDate;
	}

	public void setLastDispo1ModifiedDate(Date lastDispo1ModifiedDate) {
		this.lastDispo1ModifiedDate = lastDispo1ModifiedDate;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getDisposition3() {
		return disposition3;
	}

	public void setDisposition3(String disposition3) {
		this.disposition3 = disposition3;
	}

	public String getModalNo() {
		return modalNo;
	}

	public void setModalNo(String modalNo) {
		this.modalNo = modalNo;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getAssignLeadDate() {
		return assignLeadDate;
	}

	public void setAssignLeadDate(Date assignLeadDate) {
		this.assignLeadDate = assignLeadDate;
	}
	
	
	
	
}
