package com.mnt.businessApp.viewmodel;

import java.util.Date;

import com.mnt.entities.businessApp.Lead;
import com.mnt.entities.businessApp.LeadDetails;

public class LeadDetailsVM {

	public Long id;
	public Long leadNumber;
	public String contactName;
	public String email;
	public Long contactNumber;
	public Long pincode;
	public String product;
	public String disposition1;
	public String disposition2;
	public String esacaletedTo;
	public Date followUpDate;
	
	public LeadDetailsVM() {
	}

	public LeadDetailsVM(Lead lead) {
		this.id = lead.getId();
		this.contactName = lead.getLeadDetails().getName();
		this.leadNumber = lead.getLeadDetails().getSr();
		this.email = lead.getLeadDetails().getEmail();
		this.contactNumber = lead.getLeadDetails().getContactNo();
		this.pincode = lead.getLeadDetails().getPinCode();
		this.product = lead.getLeadDetails().getProduct();
		this.disposition1 = lead.getDisposition1();
		this.disposition2 = lead.getDisposition2();
		this.esacaletedTo = lead.getEscalatedTo().getName();
		this.followUpDate = lead.getFollowUpDate();
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

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(Long contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
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

	public String getEsacaletedTo() {
		return esacaletedTo;
	}

	public void setEsacaletedTo(String esacaletedTo) {
		this.esacaletedTo = esacaletedTo;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}

}
