package com.mnt.businessApp.viewmodel;

import java.util.Date;

import com.mnt.entities.businessApp.Lead;

public class LeadVM {

	public Long id;
	public Long leadNumber;
	public String disposition1;
	public String disposition2;
	public Date	uploadDate;
	public String	title;
	public String	name;
	public String	email;
    public Long		contactNo;
    public String	state;
    public String	city;
    public String	pinCode;
    public String	product;
    public String	type;
    public String	campaignName;
	public String	categorization;
	public String	areaofInterest1;
	public String	areaofInterest2;
	public String	remarks1;
	public Date followUpDate;
	public String reason;
	public Boolean isLost = false;
	public Boolean isCompleated = false;
	public LeadVM() {}
	
	public LeadVM(Lead lead) {
		this.id = lead.getId();
		this.disposition1 = lead.getDisposition1();
		this.disposition2 = lead.getDisposition2();
		//this.uploadDate = lead.getUploadDate();
		this.name = lead.getLeadDetails().getName();
		this.leadNumber = lead.getLeadDetails().getSr();
		this.email = lead.getLeadDetails().getEmail();
		this.contactNo = lead.getLeadDetails().getContactNo();
		this.city = lead.getLeadDetails().getCity();
		this.state = lead.getLeadDetails().getState();
		this.pinCode = lead.getLeadDetails().getPinCode();
		this.product = lead.getLeadDetails().getProduct().getName();
		this.type = lead.getLeadDetails().getType();
		this.campaignName = lead.getLeadDetails().getCampaignName();
		this.categorization = lead.getLeadDetails().getCategorization();
		this.areaofInterest1 = lead.getLeadDetails().getAreaofInterest1();
		this.areaofInterest2 = lead.getLeadDetails().getAreaofInterest2();
		this.remarks1 = lead.getLeadDetails().getRemarks1();
		this.followUpDate = lead.getFollowUpDate();
		if(lead.getDisposition2() != null && (lead.getDisposition2().equals("Won") || lead.getDisposition2().equals("Lost"))){
			this.isCompleated = true;
		}
	}

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getContactNo() {
		return contactNo;
	}

	public void setContactNo(Long contactNo) {
		this.contactNo = contactNo;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getCategorization() {
		return categorization;
	}

	public void setCategorization(String categorization) {
		this.categorization = categorization;
	}

	public String getAreaofInterest1() {
		return areaofInterest1;
	}

	public void setAreaofInterest1(String areaofInterest1) {
		this.areaofInterest1 = areaofInterest1;
	}

	public String getAreaofInterest2() {
		return areaofInterest2;
	}

	public void setAreaofInterest2(String areaofInterest2) {
		this.areaofInterest2 = areaofInterest2;
	}

	public String getRemarks1() {
		return remarks1;
	}

	public void setRemarks1(String remarks1) {
		this.remarks1 = remarks1;
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

	public Boolean getIsLost() {
		return isLost;
	}

	public void setIsLost(Boolean isLost) {
		this.isLost = isLost;
	}

}
