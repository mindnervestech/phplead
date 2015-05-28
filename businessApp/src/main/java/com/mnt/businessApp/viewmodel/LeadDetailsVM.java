package com.mnt.businessApp.viewmodel;

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
	}

}
